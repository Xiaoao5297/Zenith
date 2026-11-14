package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ServerSocketChannelConfig;

public interface OioServerSocketChannelConfig extends ServerSocketChannelConfig {

    OioServerSocketChannelConfig setSoTimeout(int i);

    int getSoTimeout();

    @Override
    OioServerSocketChannelConfig setBacklog(int i);

    @Override
    OioServerSocketChannelConfig setReuseAddress(boolean flag);

    @Override
    OioServerSocketChannelConfig setReceiveBufferSize(int i);

    @Override
    OioServerSocketChannelConfig setPerformancePreferences(int i, int j, int k);

    @Override
    OioServerSocketChannelConfig setConnectTimeoutMillis(int i);

    @Override
    OioServerSocketChannelConfig setMaxMessagesPerRead(int i);

    @Override
    OioServerSocketChannelConfig setWriteSpinCount(int i);

    @Override
    OioServerSocketChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    OioServerSocketChannelConfig setAutoRead(boolean flag);

    @Override
    OioServerSocketChannelConfig setWriteBufferHighWaterMark(int i);

    @Override
    OioServerSocketChannelConfig setWriteBufferLowWaterMark(int i);

    @Override
    OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
