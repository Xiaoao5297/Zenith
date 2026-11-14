package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface SctpServerChannelConfig extends ChannelConfig {

    int getBacklog();

    SctpServerChannelConfig setBacklog(int i);

    int getSendBufferSize();

    SctpServerChannelConfig setSendBufferSize(int i);

    int getReceiveBufferSize();

    SctpServerChannelConfig setReceiveBufferSize(int i);

    InitMaxStreams getInitMaxStreams();

    SctpServerChannelConfig setInitMaxStreams(InitMaxStreams initmaxstreams);

    @Override
    SctpServerChannelConfig setMaxMessagesPerRead(int i);

    @Override
    SctpServerChannelConfig setWriteSpinCount(int i);

    @Override
    SctpServerChannelConfig setConnectTimeoutMillis(int i);

    @Override
    SctpServerChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    SctpServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    SctpServerChannelConfig setAutoRead(boolean flag);

    @Override
    SctpServerChannelConfig setWriteBufferHighWaterMark(int i);

    @Override
    SctpServerChannelConfig setWriteBufferLowWaterMark(int i);

    @Override
    SctpServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
