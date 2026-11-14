package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface SctpChannelConfig extends ChannelConfig {

    boolean isSctpNoDelay();

    SctpChannelConfig setSctpNoDelay(boolean flag);

    int getSendBufferSize();

    SctpChannelConfig setSendBufferSize(int i);

    int getReceiveBufferSize();

    SctpChannelConfig setReceiveBufferSize(int i);

    InitMaxStreams getInitMaxStreams();

    SctpChannelConfig setInitMaxStreams(InitMaxStreams initmaxstreams);

    @Override
    SctpChannelConfig setConnectTimeoutMillis(int i);

    @Override
    SctpChannelConfig setMaxMessagesPerRead(int i);

    @Override
    SctpChannelConfig setWriteSpinCount(int i);

    @Override
    SctpChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    SctpChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    SctpChannelConfig setAutoRead(boolean flag);

    @Override
    SctpChannelConfig setWriteBufferHighWaterMark(int i);

    @Override
    SctpChannelConfig setWriteBufferLowWaterMark(int i);

    @Override
    SctpChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
