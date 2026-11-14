package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface SocketChannelConfig extends ChannelConfig {

    boolean isTcpNoDelay();

    SocketChannelConfig setTcpNoDelay(boolean flag);

    int getSoLinger();

    SocketChannelConfig setSoLinger(int i);

    int getSendBufferSize();

    SocketChannelConfig setSendBufferSize(int i);

    int getReceiveBufferSize();

    SocketChannelConfig setReceiveBufferSize(int i);

    boolean isKeepAlive();

    SocketChannelConfig setKeepAlive(boolean flag);

    int getTrafficClass();

    SocketChannelConfig setTrafficClass(int i);

    boolean isReuseAddress();

    SocketChannelConfig setReuseAddress(boolean flag);

    SocketChannelConfig setPerformancePreferences(int i, int j, int k);

    boolean isAllowHalfClosure();

    SocketChannelConfig setAllowHalfClosure(boolean flag);

    @Override
    SocketChannelConfig setConnectTimeoutMillis(int i);

    @Override
    SocketChannelConfig setMaxMessagesPerRead(int i);

    @Override
    SocketChannelConfig setWriteSpinCount(int i);

    @Override
    SocketChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    SocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    SocketChannelConfig setAutoRead(boolean flag);

    @Override
    SocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
