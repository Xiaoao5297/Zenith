package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.SocketChannelConfig;

public interface OioSocketChannelConfig extends SocketChannelConfig {

    OioSocketChannelConfig setSoTimeout(int i);

    int getSoTimeout();

    @Override
    OioSocketChannelConfig setTcpNoDelay(boolean flag);

    @Override
    OioSocketChannelConfig setSoLinger(int i);

    @Override
    OioSocketChannelConfig setSendBufferSize(int i);

    @Override
    OioSocketChannelConfig setReceiveBufferSize(int i);

    @Override
    OioSocketChannelConfig setKeepAlive(boolean flag);

    @Override
    OioSocketChannelConfig setTrafficClass(int i);

    @Override
    OioSocketChannelConfig setReuseAddress(boolean flag);

    @Override
    OioSocketChannelConfig setPerformancePreferences(int i, int j, int k);

    @Override
    OioSocketChannelConfig setAllowHalfClosure(boolean flag);

    @Override
    OioSocketChannelConfig setConnectTimeoutMillis(int i);

    @Override
    OioSocketChannelConfig setMaxMessagesPerRead(int i);

    @Override
    OioSocketChannelConfig setWriteSpinCount(int i);

    @Override
    OioSocketChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    OioSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    OioSocketChannelConfig setAutoRead(boolean flag);

    @Override
    OioSocketChannelConfig setWriteBufferHighWaterMark(int i);

    @Override
    OioSocketChannelConfig setWriteBufferLowWaterMark(int i);

    @Override
    OioSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
