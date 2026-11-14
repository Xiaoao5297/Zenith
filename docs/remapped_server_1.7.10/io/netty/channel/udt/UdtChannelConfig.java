package io.netty.channel.udt;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface UdtChannelConfig extends ChannelConfig {

    int getProtocolReceiveBufferSize();

    int getProtocolSendBufferSize();

    int getReceiveBufferSize();

    int getSendBufferSize();

    int getSoLinger();

    int getSystemReceiveBufferSize();

    int getSystemSendBufferSize();

    boolean isReuseAddress();

    @Override
    UdtChannelConfig setConnectTimeoutMillis(int i);

    @Override
    UdtChannelConfig setMaxMessagesPerRead(int i);

    @Override
    UdtChannelConfig setWriteSpinCount(int i);

    @Override
    UdtChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    UdtChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    UdtChannelConfig setAutoRead(boolean flag);

    @Override
    UdtChannelConfig setWriteBufferHighWaterMark(int i);

    @Override
    UdtChannelConfig setWriteBufferLowWaterMark(int i);

    @Override
    UdtChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);

    UdtChannelConfig setProtocolReceiveBufferSize(int i);

    UdtChannelConfig setProtocolSendBufferSize(int i);

    UdtChannelConfig setReceiveBufferSize(int i);

    UdtChannelConfig setReuseAddress(boolean flag);

    UdtChannelConfig setSendBufferSize(int i);

    UdtChannelConfig setSoLinger(int i);

    UdtChannelConfig setSystemReceiveBufferSize(int i);

    UdtChannelConfig setSystemSendBufferSize(int i);
}
