package io.netty.channel;

import io.netty.util.concurrent.EventExecutorGroup;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface ChannelPipeline extends ChannelInboundInvoker, ChannelOutboundInvoker, Iterable<Entry<String, ChannelHandler>> {

    ChannelPipeline addFirst(String s, ChannelHandler channelhandler);

    ChannelPipeline addFirst(EventExecutorGroup eventexecutorgroup, String s, ChannelHandler channelhandler);

    ChannelPipeline addLast(String s, ChannelHandler channelhandler);

    ChannelPipeline addLast(EventExecutorGroup eventexecutorgroup, String s, ChannelHandler channelhandler);

    ChannelPipeline addBefore(String s, String s1, ChannelHandler channelhandler);

    ChannelPipeline addBefore(EventExecutorGroup eventexecutorgroup, String s, String s1, ChannelHandler channelhandler);

    ChannelPipeline addAfter(String s, String s1, ChannelHandler channelhandler);

    ChannelPipeline addAfter(EventExecutorGroup eventexecutorgroup, String s, String s1, ChannelHandler channelhandler);

    ChannelPipeline addFirst(ChannelHandler... achannelhandler);

    ChannelPipeline addFirst(EventExecutorGroup eventexecutorgroup, ChannelHandler... achannelhandler);

    ChannelPipeline addLast(ChannelHandler... achannelhandler);

    ChannelPipeline addLast(EventExecutorGroup eventexecutorgroup, ChannelHandler... achannelhandler);

    ChannelPipeline remove(ChannelHandler channelhandler);

    ChannelHandler remove(String s);

    <T extends ChannelHandler> T remove(Class<T> oclass);

    ChannelHandler removeFirst();

    ChannelHandler removeLast();

    ChannelPipeline replace(ChannelHandler channelhandler, String s, ChannelHandler channelhandler1);

    ChannelHandler replace(String s, String s1, ChannelHandler channelhandler);

    <T extends ChannelHandler> T replace(Class<T> oclass, String s, ChannelHandler channelhandler);

    ChannelHandler first();

    ChannelHandlerContext firstContext();

    ChannelHandler last();

    ChannelHandlerContext lastContext();

    ChannelHandler get(String s);

    <T extends ChannelHandler> T get(Class<T> oclass);

    ChannelHandlerContext context(ChannelHandler channelhandler);

    ChannelHandlerContext context(String s);

    ChannelHandlerContext context(Class<? extends ChannelHandler> oclass);

    Channel channel();

    List<String> names();

    Map<String, ChannelHandler> toMap();

    @Override
    ChannelPipeline fireChannelRegistered();

    /** @deprecated */
    @Deprecated
    @Override
    ChannelPipeline fireChannelUnregistered();

    @Override
    ChannelPipeline fireChannelActive();

    @Override
    ChannelPipeline fireChannelInactive();

    @Override
    ChannelPipeline fireExceptionCaught(Throwable throwable);

    @Override
    ChannelPipeline fireUserEventTriggered(Object object);

    @Override
    ChannelPipeline fireChannelRead(Object object);

    @Override
    ChannelPipeline fireChannelReadComplete();

    @Override
    ChannelPipeline fireChannelWritabilityChanged();

    @Override
    ChannelPipeline flush();

    @Override
    ChannelPipeline read();
}
