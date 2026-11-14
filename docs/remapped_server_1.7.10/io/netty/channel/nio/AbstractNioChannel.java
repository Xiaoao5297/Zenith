package io.netty.channel.nio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioChannel extends AbstractChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);
    private final SelectableChannel ch;
    protected final int readInterestOp;
    private volatile SelectionKey selectionKey;
    private volatile boolean inputShutdown;
    private ChannelPromise connectPromise;
    private ScheduledFuture<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;

    protected AbstractNioChannel(Channel channel, SelectableChannel selectablechannel, int i) {
        super(channel);
        this.ch = selectablechannel;
        this.readInterestOp = i;

        try {
            selectablechannel.configureBlocking(false);
        } catch (IOException ioexception) {
            try {
                selectablechannel.close();
            } catch (IOException ioexception1) {
                if (AbstractNioChannel.logger.isWarnEnabled()) {
                    AbstractNioChannel.logger.warn("Failed to close a partially initialized socket.", (Throwable) ioexception1);
                }
            }

            throw new ChannelException("Failed to enter non-blocking mode.", ioexception);
        }
    }

    @Override
    public boolean isOpen() {
        return this.ch.isOpen();
    }

    @Override
    public AbstractNioChannel.NioUnsafe unsafe() {
        return (AbstractNioChannel.NioUnsafe) super.unsafe();
    }

    protected SelectableChannel javaChannel() {
        return this.ch;
    }

    @Override
    public NioEventLoop eventLoop() {
        return (NioEventLoop) super.eventLoop();
    }

    protected SelectionKey selectionKey() {
        assert this.selectionKey != null;

        return this.selectionKey;
    }

    protected boolean isInputShutdown() {
        return this.inputShutdown;
    }

    void setInputShutdown() {
        this.inputShutdown = true;
    }

    @Override
    protected boolean isCompatible(EventLoop eventloop) {
        return eventloop instanceof NioEventLoop;
    }

    @Override
    protected void doRegister() throws Exception {
        boolean flag = false;

        while (true) {
            try {
                this.selectionKey = this.javaChannel().register(this.eventLoop().selector, 0, this);
                return;
            } catch (CancelledKeyException cancelledkeyexception) {
                if (flag) {
                    throw cancelledkeyexception;
                }

                this.eventLoop().selectNow();
                flag = true;
            }
        }
    }

    @Override
    protected void doDeregister() throws Exception {
        this.eventLoop().cancel(this.selectionKey());
    }

    @Override
    protected void doBeginRead() throws Exception {
        if (!this.inputShutdown) {
            SelectionKey selectionkey = this.selectionKey;

            if (selectionkey.isValid()) {
                int i = selectionkey.interestOps();

                if ((i & this.readInterestOp) == 0) {
                    selectionkey.interestOps(i | this.readInterestOp);
                }

            }
        }
    }

    protected abstract boolean doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception;

    protected abstract void doFinishConnect() throws Exception;

    protected abstract class AbstractNioUnsafe extends AbstractChannel.AbstractUnsafe implements AbstractNioChannel.NioUnsafe {

        protected AbstractNioUnsafe() {
            super();
        }

        @Override
        public SelectableChannel ch() {
            return AbstractNioChannel.this.javaChannel();
        }

        @Override
        public void connect(final SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) {
            if (this.ensureOpen(channelpromise)) {
                try {
                    if (AbstractNioChannel.this.connectPromise != null) {
                        throw new IllegalStateException("connection attempt already made");
                    }

                    boolean flag = AbstractNioChannel.this.isActive();

                    if (AbstractNioChannel.this.doConnect(socketaddress, socketaddress1)) {
                        channelpromise.setSuccess();
                        if (!flag && AbstractNioChannel.this.isActive()) {
                            AbstractNioChannel.this.pipeline().fireChannelActive();
                        }
                    } else {
                        AbstractNioChannel.this.connectPromise = channelpromise;
                        AbstractNioChannel.this.requestedRemoteAddress = socketaddress;
                        int i = AbstractNioChannel.this.config().getConnectTimeoutMillis();

                        if (i > 0) {
                            AbstractNioChannel.this.connectTimeoutFuture = AbstractNioChannel.this.eventLoop().schedule(new Runnable() {
                                public void run() {
                                    ChannelPromise channelpromise1 = AbstractNioChannel.this.connectPromise;
                                    ConnectTimeoutException connecttimeoutexception = new ConnectTimeoutException("connection timed out: " + socketaddress);

                                    if (channelpromise1 != null && channelpromise1.tryFailure(connecttimeoutexception)) {
                                        AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                                    }

                                }
                            }, (long) i, TimeUnit.MILLISECONDS);
                        }

                        channelpromise.addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture channelfuture) throws Exception {
                                if (channelfuture.isCancelled()) {
                                    if (AbstractNioChannel.this.connectTimeoutFuture != null) {
                                        AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
                                    }

                                    AbstractNioChannel.this.connectPromise = null;
                                    AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                                }

                            }
                        });
                    }
                } catch (Throwable throwable) {
                    Object object = throwable;

                    if (throwable instanceof ConnectException) {
                        ConnectException connectexception = new ConnectException(throwable.getMessage() + ": " + socketaddress);

                        connectexception.setStackTrace(throwable.getStackTrace());
                        object = connectexception;
                    }

                    this.closeIfClosed();
                    channelpromise.tryFailure((Throwable) object);
                }

            }
        }

        @Override
        public void finishConnect() {
            assert AbstractNioChannel.this.eventLoop().inEventLoop();

            assert AbstractNioChannel.this.connectPromise != null;

            try {
                boolean flag = AbstractNioChannel.this.isActive();

                AbstractNioChannel.this.doFinishConnect();
                AbstractNioChannel.this.connectPromise.setSuccess();
                if (!flag && AbstractNioChannel.this.isActive()) {
                    AbstractNioChannel.this.pipeline().fireChannelActive();
                }
            } catch (Throwable throwable) {
                Object object = throwable;

                if (throwable instanceof ConnectException) {
                    ConnectException connectexception = new ConnectException(throwable.getMessage() + ": " + AbstractNioChannel.this.requestedRemoteAddress);

                    connectexception.setStackTrace(throwable.getStackTrace());
                    object = connectexception;
                }

                AbstractNioChannel.this.connectPromise.setFailure((Throwable) object);
                this.closeIfClosed();
            } finally {
                if (AbstractNioChannel.this.connectTimeoutFuture != null) {
                    AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
                }

                AbstractNioChannel.this.connectPromise = null;
            }

        }

        @Override
        protected void flush0() {
            if (!this.isFlushPending()) {
                super.flush0();
            }
        }

        @Override
        public void forceFlush() {
            super.flush0();
        }

        private boolean isFlushPending() {
            SelectionKey selectionkey = AbstractNioChannel.this.selectionKey();

            return selectionkey.isValid() && (selectionkey.interestOps() & 4) != 0;
        }
    }

    public interface NioUnsafe extends Channel.Unsafe {

        SelectableChannel ch();

        void finishConnect();

        void read();

        void forceFlush();
    }
}
