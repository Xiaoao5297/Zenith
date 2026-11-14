package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface ServerSocketChannelConfig extends ChannelConfig {

    int getBacklog();

    ServerSocketChannelConfig setBacklog(int i);

    boolean isReuseAddress();

    ServerSocketChannelConfig setReuseAddress(boolean flag);

    int getReceiveBufferSize();

    ServerSocketChannelConfig setReceiveBufferSize(int i);

    ServerSocketChannelConfig setPerformancePreferences(int i, int j, int k);

    @Override
    ServerSocketChannelConfig setConnectTimeoutMillis(int i);

    @Override
    ServerSocketChannelConfig setMaxMessagesPerRead(int i);

    @Override
    ServerSocketChannelConfig setWriteSpinCount(int i);

    @Override
    ServerSocketChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    ServerSocketChannelConfig setAutoRead(boolean flag);

    @Override
    ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
