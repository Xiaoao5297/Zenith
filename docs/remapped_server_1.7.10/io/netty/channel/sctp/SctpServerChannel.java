package io.netty.channel.sctp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ServerChannel;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

public interface SctpServerChannel extends ServerChannel {

    @Override
    SctpServerChannelConfig config();

    @Override
    InetSocketAddress localAddress();

    Set<InetSocketAddress> allLocalAddresses();

    ChannelFuture bindAddress(InetAddress inetaddress);

    ChannelFuture bindAddress(InetAddress inetaddress, ChannelPromise channelpromise);

    ChannelFuture unbindAddress(InetAddress inetaddress);

    ChannelFuture unbindAddress(InetAddress inetaddress, ChannelPromise channelpromise);
}
