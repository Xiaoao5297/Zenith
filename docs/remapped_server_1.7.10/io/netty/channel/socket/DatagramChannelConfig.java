package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import java.net.InetAddress;
import java.net.NetworkInterface;

public interface DatagramChannelConfig extends ChannelConfig {

    int getSendBufferSize();

    DatagramChannelConfig setSendBufferSize(int i);

    int getReceiveBufferSize();

    DatagramChannelConfig setReceiveBufferSize(int i);

    int getTrafficClass();

    DatagramChannelConfig setTrafficClass(int i);

    boolean isReuseAddress();

    DatagramChannelConfig setReuseAddress(boolean flag);

    boolean isBroadcast();

    DatagramChannelConfig setBroadcast(boolean flag);

    boolean isLoopbackModeDisabled();

    DatagramChannelConfig setLoopbackModeDisabled(boolean flag);

    int getTimeToLive();

    DatagramChannelConfig setTimeToLive(int i);

    InetAddress getInterface();

    DatagramChannelConfig setInterface(InetAddress inetaddress);

    NetworkInterface getNetworkInterface();

    DatagramChannelConfig setNetworkInterface(NetworkInterface networkinterface);

    @Override
    DatagramChannelConfig setMaxMessagesPerRead(int i);

    @Override
    DatagramChannelConfig setWriteSpinCount(int i);

    @Override
    DatagramChannelConfig setConnectTimeoutMillis(int i);

    @Override
    DatagramChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    DatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    DatagramChannelConfig setAutoRead(boolean flag);

    @Override
    DatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);
}
