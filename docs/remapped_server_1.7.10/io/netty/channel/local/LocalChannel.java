package io.netty.channel.local;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.net.SocketAddress;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayDeque;
import java.util.Queue;

public class LocalChannel extends AbstractChannel {

    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final int MAX_READER_STACK_DEPTH = 8;
    private static final ThreadLocal<Integer> READER_STACK_DEPTH = new ThreadLocal<Integer>() {
        protected Integer initialValue() {
            return 0;
        }
    };
    private final ChannelConfig config = new DefaultChannelConfig(this);
    private final Queue<Object> inboundBuffer = new ArrayDeque();
    private final Runnable readTask = new Runnable() {
        public void run() {
            ChannelPipeline channelpipeline = LocalChannel.this.pipeline();

            while (true) {
                Object object = LocalChannel.this.inboundBuffer.poll();

                if (object == null) {
                    channelpipeline.fireChannelReadComplete();
                    return;
                }

                channelpipeline.fireChannelRead(object);
            }
        }
    };
    private final Runnable shutdownHook = new Runnable() {
        public void run() {
            LocalChannel.this.unsafe().close(LocalChannel.this.unsafe().voidPromise());
        }
    };
    private volatile int state;
    private volatile LocalChannel peer;
    private volatile LocalAddress localAddress;
    private volatile LocalAddress remoteAddress;
    private volatile ChannelPromise connectPromise;
    private volatile boolean readInProgress;

    public LocalChannel() {
        super((Channel) null);
    }

    LocalChannel(LocalServerChannel localserverchannel, LocalChannel localchannel) {
        super(localserverchannel);
        this.peer = localchannel;
        this.localAddress = localserverchannel.localAddress();
        this.remoteAddress = localchannel.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return LocalChannel.METADATA;
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public LocalServerChannel parent() {
        return (LocalServerChannel) super.parent();
    }

    @Override
    public LocalAddress localAddress() {
        return (LocalAddress) super.localAddress();
    }

    @Override
    public LocalAddress remoteAddress() {
        return (LocalAddress) super.remoteAddress();
    }

    @Override
    public boolean isOpen() {
        return this.state < 3;
    }

    @Override
    public boolean isActive() {
        return this.state == 2;
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new LocalChannel.LocalUnsafe();
    }

    @Override
    protected boolean isCompatible(EventLoop eventloop) {
        return eventloop instanceof SingleThreadEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.localAddress;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.remoteAddress;
    }

    @Override
    protected void doRegister() throws Exception {
        if (this.peer != null) {
            this.state = 2;
            this.peer.remoteAddress = this.parent().localAddress();
            this.peer.state = 2;
            this.peer.eventLoop().execute(new Runnable() {
                public void run() {
                    LocalChannel.this.peer.pipeline().fireChannelActive();
                    LocalChannel.this.peer.connectPromise.setSuccess();
                }
            });
        }

        ((SingleThreadEventExecutor) this.eventLoop()).addShutdownHook(this.shutdownHook);
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.localAddress = LocalChannelRegistry.register(this, this.localAddress, socketaddress);
        this.state = 1;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        if (this.state <= 2) {
            if (this.localAddress != null) {
                if (this.parent() == null) {
                    LocalChannelRegistry.unregister(this.localAddress);
                }

                this.localAddress = null;
            }

            this.state = 3;
        }

        final LocalChannel localchannel = this.peer;

        if (localchannel != null && localchannel.isActive()) {
            EventLoop eventloop = localchannel.eventLoop();

            if (eventloop.inEventLoop()) {
                localchannel.unsafe().close(this.unsafe().voidPromise());
            } else {
                localchannel.eventLoop().execute(new Runnable() {
                    public void run() {
                        localchannel.unsafe().close(LocalChannel.this.unsafe().voidPromise());
                    }
                });
            }

            this.peer = null;
        }

    }

    @Override
    protected void doDeregister() throws Exception {
        if (this.isOpen()) {
            this.unsafe().close(this.unsafe().voidPromise());
        }

        ((SingleThreadEventExecutor) this.eventLoop()).removeShutdownHook(this.shutdownHook);
    }

    @Override
    protected void doBeginRead() throws Exception {
        if (!this.readInProgress) {
            ChannelPipeline channelpipeline = this.pipeline();
            Queue queue = this.inboundBuffer;

            if (queue.isEmpty()) {
                this.readInProgress = true;
            } else {
                Integer integer = (Integer) LocalChannel.READER_STACK_DEPTH.get();

                if (integer < 8) {
                    LocalChannel.READER_STACK_DEPTH.set(integer + 1);

                    try {
                        while (true) {
                            Object object = queue.poll();

                            if (object == null) {
                                channelpipeline.fireChannelReadComplete();
                                break;
                            }

                            channelpipeline.fireChannelRead(object);
                        }
                    } finally {
                        LocalChannel.READER_STACK_DEPTH.set(integer);
                    }
                } else {
                    this.eventLoop().execute(this.readTask);
                }

            }
        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        if (this.state < 2) {
            throw new NotYetConnectedException();
        } else if (this.state > 2) {
            throw new ClosedChannelException();
        } else {
            final LocalChannel localchannel = this.peer;
            final ChannelPipeline channelpipeline = localchannel.pipeline();
            EventLoop eventloop = localchannel.eventLoop();

            if (eventloop == this.eventLoop()) {
                while (true) {
                    Object object = channeloutboundbuffer.current();

                    if (object == null) {
                        finishPeerRead(localchannel, channelpipeline);
                        break;
                    }

                    localchannel.inboundBuffer.add(object);
                    ReferenceCountUtil.retain(object);
                    channeloutboundbuffer.remove();
                }
            } else {
                final Object[] aobject = new Object[channeloutboundbuffer.size()];

                for (int i = 0; i < aobject.length; ++i) {
                    aobject[i] = ReferenceCountUtil.retain(channeloutboundbuffer.current());
                    channeloutboundbuffer.remove();
                }

                eventloop.execute(new Runnable() {
                    public void run() {
                        for (int j = 0; j < aobject.length; ++j) {
                            localchannel.inboundBuffer.add(aobject[j]);
                        }

                        LocalChannel.finishPeerRead(localchannel, channelpipeline);
                    }
                });
            }

        }
    }

    private static void finishPeerRead(LocalChannel localchannel, ChannelPipeline channelpipeline) {
        if (localchannel.readInProgress) {
            localchannel.readInProgress = false;

            while (true) {
                Object object = localchannel.inboundBuffer.poll();

                if (object == null) {
                    channelpipeline.fireChannelReadComplete();
                    break;
                }

                channelpipeline.fireChannelRead(object);
            }
        }

    }

    private class LocalUnsafe extends AbstractChannel.AbstractUnsafe {

        private LocalUnsafe() {
            super();
        }

        @Override
        public void connect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) {
            if (this.ensureOpen(channelpromise)) {
                if (LocalChannel.this.state == 2) {
                    AlreadyConnectedException alreadyconnectedexception = new AlreadyConnectedException();

                    channelpromise.setFailure(alreadyconnectedexception);
                    LocalChannel.this.pipeline().fireExceptionCaught(alreadyconnectedexception);
                } else if (LocalChannel.this.connectPromise != null) {
                    throw new ConnectionPendingException();
                } else {
                    LocalChannel.this.connectPromise = channelpromise;
                    if (LocalChannel.this.state != 1 && socketaddress1 == null) {
                        socketaddress1 = new LocalAddress(LocalChannel.this);
                    }

                    if (socketaddress1 != null) {
                        try {
                            LocalChannel.this.doBind((SocketAddress) socketaddress1);
                        } catch (Throwable throwable) {
                            channelpromise.setFailure(throwable);
                            LocalChannel.this.pipeline().fireExceptionCaught(throwable);
                            this.close(this.voidPromise());
                            return;
                        }
                    }

                    Channel channel = LocalChannelRegistry.get(socketaddress);

                    if (!(channel instanceof LocalServerChannel)) {
                        ChannelException channelexception = new ChannelException("connection refused");

                        channelpromise.setFailure(channelexception);
                        this.close(this.voidPromise());
                    } else {
                        LocalServerChannel localserverchannel = (LocalServerChannel) channel;

                        LocalChannel.this.peer = localserverchannel.serve(LocalChannel.this);
                    }
                }
            }
        }
    }
}
