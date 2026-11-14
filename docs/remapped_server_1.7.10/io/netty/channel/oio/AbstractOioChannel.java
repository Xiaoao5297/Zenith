package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ThreadPerChannelEventLoop;
import java.net.ConnectException;
import java.net.SocketAddress;

public abstract class AbstractOioChannel extends AbstractChannel {

    protected static final int SO_TIMEOUT = 1000;
    private boolean readInProgress;
    private final Runnable readTask = new Runnable() {
        public void run() {
            AbstractOioChannel.this.readInProgress = false;
            AbstractOioChannel.this.doRead();
        }
    };

    protected AbstractOioChannel(Channel channel) {
        super(channel);
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new AbstractOioChannel.DefaultOioUnsafe();
    }

    @Override
    protected boolean isCompatible(EventLoop eventloop) {
        return eventloop instanceof ThreadPerChannelEventLoop;
    }

    protected abstract void doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception;

    @Override
    protected void doBeginRead() throws Exception {
        if (!this.readInProgress) {
            this.readInProgress = true;
            this.eventLoop().execute(this.readTask);
        }
    }

    protected abstract void doRead();

    private final class DefaultOioUnsafe extends AbstractChannel.AbstractUnsafe {

        private DefaultOioUnsafe() {
            super();
        }

        @Override
        public void connect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) {
            if (this.ensureOpen(channelpromise)) {
                if (!channelpromise.setUncancellable()) {
                    this.close(this.voidPromise());
                } else {
                    try {
                        boolean flag = AbstractOioChannel.this.isActive();

                        AbstractOioChannel.this.doConnect(socketaddress, socketaddress1);
                        channelpromise.setSuccess();
                        if (!flag && AbstractOioChannel.this.isActive()) {
                            AbstractOioChannel.this.pipeline().fireChannelActive();
                        }
                    } catch (Throwable throwable) {
                        Object object = throwable;

                        if (throwable instanceof ConnectException) {
                            ConnectException connectexception = new ConnectException(throwable.getMessage() + ": " + socketaddress);

                            connectexception.setStackTrace(throwable.getStackTrace());
                            object = connectexception;
                        }

                        this.closeIfClosed();
                        channelpromise.setFailure((Throwable) object);
                    }

                }
            }
        }
    }
}
