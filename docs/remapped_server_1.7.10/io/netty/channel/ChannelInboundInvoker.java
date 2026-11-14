package io.netty.channel;

interface ChannelInboundInvoker {

    ChannelInboundInvoker fireChannelRegistered();

    /** @deprecated */
    @Deprecated
    ChannelInboundInvoker fireChannelUnregistered();

    ChannelInboundInvoker fireChannelActive();

    ChannelInboundInvoker fireChannelInactive();

    ChannelInboundInvoker fireExceptionCaught(Throwable throwable);

    ChannelInboundInvoker fireUserEventTriggered(Object object);

    ChannelInboundInvoker fireChannelRead(Object object);

    ChannelInboundInvoker fireChannelReadComplete();

    ChannelInboundInvoker fireChannelWritabilityChanged();
}
