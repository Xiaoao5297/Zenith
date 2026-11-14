package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;

interface ChannelPropertyAccess {

    ChannelPipeline pipeline();

    ByteBufAllocator alloc();

    ChannelPromise newPromise();

    ChannelProgressivePromise newProgressivePromise();

    ChannelFuture newSucceededFuture();

    ChannelFuture newFailedFuture(Throwable throwable);

    ChannelPromise voidPromise();
}
