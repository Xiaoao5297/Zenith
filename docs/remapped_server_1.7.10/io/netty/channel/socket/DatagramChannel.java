package io.netty.channel.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

public interface DatagramChannel extends Channel {

    @Override
    DatagramChannelConfig config();

    @Override
    InetSocketAddress localAddress();

    @Override
    InetSocketAddress remoteAddress();

    boolean isConnected();

    ChannelFuture joinGroup(InetAddress inetaddress);

    ChannelFuture joinGroup(InetAddress inetaddress, ChannelPromise channelpromise);

    ChannelFuture joinGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface);

    ChannelFuture joinGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface, ChannelPromise channelpromise);

    ChannelFuture joinGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1);

    ChannelFuture joinGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise);

    ChannelFuture leaveGroup(InetAddress inetaddress);

    ChannelFuture leaveGroup(InetAddress inetaddress, ChannelPromise channelpromise);

    ChannelFuture leaveGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface);

    ChannelFuture leaveGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface, ChannelPromise channelpromise);

    ChannelFuture leaveGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1);

    ChannelFuture leaveGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise);

    ChannelFuture block(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1);

    ChannelFuture block(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise);

    ChannelFuture block(InetAddress inetaddress, InetAddress inetaddress1);

    ChannelFuture block(InetAddress inetaddress, InetAddress inetaddress1, ChannelPromise channelpromise);
}
