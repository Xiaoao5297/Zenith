package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import java.net.SocketAddress;

public abstract class AbstractServerChannel extends AbstractChannel implements ServerChannel {

    private static final ChannelMetadata METADATA = new ChannelMetadata(false);

    protected AbstractServerChannel() {
        super((Channel) null);
    }

    @Override
    public ChannelMetadata metadata() {
        return AbstractServerChannel.METADATA;
    }

    @Override
    public SocketAddress remoteAddress() {
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new AbstractServerChannel.DefaultServerUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        throw new UnsupportedOperationException();
    }

    private final class DefaultServerUnsafe extends AbstractChannel.AbstractUnsafe {

        private DefaultServerUnsafe() {
            super();
        }

        @Override
        public void write(Object object, ChannelPromise channelpromise) {
            ReferenceCountUtil.release(object);
            this.reject(channelpromise);
        }

        @Override
        public void flush() {}

        @Override
        public void connect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) {
            this.reject(channelpromise);
        }

        private void reject(ChannelPromise channelpromise) {
            channelpromise.setFailure(new UnsupportedOperationException());
        }
    }
}
