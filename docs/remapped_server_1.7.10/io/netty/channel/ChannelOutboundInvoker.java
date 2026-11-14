package io.netty.channel;

import java.net.SocketAddress;

interface ChannelOutboundInvoker {

    ChannelFuture bind(SocketAddress socketaddress);

    ChannelFuture connect(SocketAddress socketaddress);

    ChannelFuture connect(SocketAddress socketaddress, SocketAddress socketaddress1);

    ChannelFuture disconnect();

    ChannelFuture close();

    /** @deprecated */
    @Deprecated
    ChannelFuture deregister();

    ChannelFuture bind(SocketAddress socketaddress, ChannelPromise channelpromise);

    ChannelFuture connect(SocketAddress socketaddress, ChannelPromise channelpromise);

    ChannelFuture connect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise);

    ChannelFuture disconnect(ChannelPromise channelpromise);

    ChannelFuture close(ChannelPromise channelpromise);

    /** @deprecated */
    @Deprecated
    ChannelFuture deregister(ChannelPromise channelpromise);

    ChannelOutboundInvoker read();

    ChannelFuture write(Object object);

    ChannelFuture write(Object object, ChannelPromise channelpromise);

    ChannelOutboundInvoker flush();

    ChannelFuture writeAndFlush(Object object, ChannelPromise channelpromise);

    ChannelFuture writeAndFlush(Object object);
}
