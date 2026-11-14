package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Iterator;

public interface ChannelGroupFuture extends Future<Void>, Iterable<ChannelFuture> {

    ChannelGroup group();

    ChannelFuture find(Channel channel);

    @Override
    boolean isSuccess();

    @Override
    ChannelGroupException cause();

    boolean isPartialSuccess();

    boolean isPartialFailure();

    @Override
    ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelGroupFuture await() throws InterruptedException;

    @Override
    ChannelGroupFuture awaitUninterruptibly();

    @Override
    ChannelGroupFuture syncUninterruptibly();

    @Override
    ChannelGroupFuture sync() throws InterruptedException;

    Iterator<ChannelFuture> iterator();
}
