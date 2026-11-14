package io.netty.channel.sctp;

import com.sun.nio.sctp.Association;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

public interface SctpChannel extends Channel {

    @Override
    SctpServerChannel parent();

    Association association();

    @Override
    InetSocketAddress localAddress();

    Set<InetSocketAddress> allLocalAddresses();

    @Override
    SctpChannelConfig config();

    @Override
    InetSocketAddress remoteAddress();

    Set<InetSocketAddress> allRemoteAddresses();

    ChannelFuture bindAddress(InetAddress inetaddress);

    ChannelFuture bindAddress(InetAddress inetaddress, ChannelPromise channelpromise);

    ChannelFuture unbindAddress(InetAddress inetaddress);

    ChannelFuture unbindAddress(InetAddress inetaddress, ChannelPromise channelpromise);
}
