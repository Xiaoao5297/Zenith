package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import java.util.Map;

public interface ChannelConfig {

    Map<ChannelOption<?>, Object> getOptions();

    boolean setOptions(Map<ChannelOption<?>, ?> map);

    <T> T getOption(ChannelOption<T> channeloption);

    <T> boolean setOption(ChannelOption<T> channeloption, T t0);

    int getConnectTimeoutMillis();

    ChannelConfig setConnectTimeoutMillis(int i);

    int getMaxMessagesPerRead();

    ChannelConfig setMaxMessagesPerRead(int i);

    int getWriteSpinCount();

    ChannelConfig setWriteSpinCount(int i);

    ByteBufAllocator getAllocator();

    ChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    RecvByteBufAllocator getRecvByteBufAllocator();

    ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    boolean isAutoRead();

    ChannelConfig setAutoRead(boolean flag);

    int getWriteBufferHighWaterMark();

    ChannelConfig setWriteBufferHighWaterMark(int i);

    int getWriteBufferLowWaterMark();

    ChannelConfig setWriteBufferLowWaterMark(int i);

    MessageSizeEstimator getMessageSizeEstimator();

    ChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
