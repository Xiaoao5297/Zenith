package io.netty.channel.local;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.util.internal.PlatformDependent;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;

final class LocalChannelRegistry {

    private static final ConcurrentMap<LocalAddress, Channel> boundChannels = PlatformDependent.newConcurrentHashMap();

    static LocalAddress register(Channel channel, LocalAddress localaddress, SocketAddress socketaddress) {
        if (localaddress != null) {
            throw new ChannelException("already bound");
        } else if (!(socketaddress instanceof LocalAddress)) {
            throw new ChannelException("unsupported address type: " + socketaddress.getClass().getSimpleName());
        } else {
            LocalAddress localaddress1 = (LocalAddress) socketaddress;

            if (LocalAddress.ANY.equals(localaddress1)) {
                localaddress1 = new LocalAddress(channel);
            }

            Channel channel1 = (Channel) LocalChannelRegistry.boundChannels.putIfAbsent(localaddress1, channel);

            if (channel1 != null) {
                throw new ChannelException("address already in use by: " + channel1);
            } else {
                return localaddress1;
            }
        }
    }

    static Channel get(SocketAddress socketaddress) {
        return (Channel) LocalChannelRegistry.boundChannels.get(socketaddress);
    }

    static void unregister(LocalAddress localaddress) {
        LocalChannelRegistry.boundChannels.remove(localaddress);
    }

    private LocalChannelRegistry() {}
}
