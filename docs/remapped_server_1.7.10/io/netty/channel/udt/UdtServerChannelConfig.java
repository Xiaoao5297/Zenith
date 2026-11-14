package io.netty.channel.udt;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface UdtServerChannelConfig extends UdtChannelConfig {

    int getBacklog();

    UdtServerChannelConfig setBacklog(int i);

    @Override
    UdtServerChannelConfig setConnectTimeoutMillis(int i);

    @Override
    UdtServerChannelConfig setMaxMessagesPerRead(int i);

    @Override
    UdtServerChannelConfig setWriteSpinCount(int i);

    @Override
    UdtServerChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    UdtServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    UdtServerChannelConfig setAutoRead(boolean flag);

    @Override
    UdtServerChannelConfig setProtocolReceiveBufferSize(int i);

    @Override
    UdtServerChannelConfig setProtocolSendBufferSize(int i);

    @Override
    UdtServerChannelConfig setReceiveBufferSize(int i);

    @Override
    UdtServerChannelConfig setReuseAddress(boolean flag);

    @Override
    UdtServerChannelConfig setSendBufferSize(int i);

    @Override
    UdtServerChannelConfig setSoLinger(int i);

    @Override
    UdtServerChannelConfig setSystemReceiveBufferSize(int i);

    @Override
    UdtServerChannelConfig setSystemSendBufferSize(int i);

    @Override
    UdtServerChannelConfig setWriteBufferHighWaterMark(int i);

    @Override
    UdtServerChannelConfig setWriteBufferLowWaterMark(int i);

    @Override
    UdtServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
