package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;

class NetworkSystem$1 extends ChannelInitializer {

    NetworkSystem$1(NetworkSystem networksystem) {
        this.field_151264_a = networksystem;
    }

    @Override
    protected void initChannel(Channel channel) {
        try {
            channel.config().setOption(ChannelOption.IP_TOS, 24);
        } catch (ChannelException channelexception) {
            ;
        }

        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, false);
        } catch (ChannelException channelexception1) {
            ;
        }

        channel.pipeline().addLast("timeout", (ChannelHandler) (new ReadTimeoutHandler(30))).addLast("legacy_query", (ChannelHandler) (new PingResponseHandler(this.field_151264_a))).addLast("splitter", (ChannelHandler) (new MessageDeserializer2())).addLast("decoder", (ChannelHandler) (new MessageDeserializer(NetworkManager.field_152462_h))).addLast("prepender", (ChannelHandler) (new MessageSerializer2())).addLast("encoder", (ChannelHandler) (new MessageSerializer(NetworkManager.field_152462_h)));
        NetworkManager networkmanager = new NetworkManager(false);

        NetworkSystem.access$000(this.field_151264_a).add(networkmanager);
        channel.pipeline().addLast("packet_handler", (ChannelHandler) networkmanager);
        networkmanager.func_150719_a(new NetHandlerHandshakeTCP(NetworkSystem.access$100(this.field_151264_a), networkmanager));
    }
}
