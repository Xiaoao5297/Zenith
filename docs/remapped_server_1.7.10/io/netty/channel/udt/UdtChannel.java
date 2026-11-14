package io.netty.channel.udt;

import io.netty.channel.Channel;
import java.net.InetSocketAddress;

public interface UdtChannel extends Channel {

    @Override
    UdtChannelConfig config();

    @Override
    InetSocketAddress localAddress();

    @Override
    InetSocketAddress remoteAddress();
}
