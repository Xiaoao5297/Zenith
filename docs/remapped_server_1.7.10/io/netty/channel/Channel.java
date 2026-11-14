package io.netty.channel;

import io.netty.util.AttributeMap;
import java.net.SocketAddress;

public interface Channel extends AttributeMap, ChannelOutboundInvoker, ChannelPropertyAccess, Comparable<Channel> {

    EventLoop eventLoop();

    Channel parent();

    ChannelConfig config();

    boolean isOpen();

    boolean isRegistered();

    boolean isActive();

    ChannelMetadata metadata();

    SocketAddress localAddress();

    SocketAddress remoteAddress();

    ChannelFuture closeFuture();

    boolean isWritable();

    @Override
    Channel flush();

    @Override
    Channel read();

    Channel.Unsafe unsafe();

    public interface Unsafe {

        SocketAddress localAddress();

        SocketAddress remoteAddress();

        void register(EventLoop eventloop, ChannelPromise channelpromise);

        void bind(SocketAddress socketaddress, ChannelPromise channelpromise);

        void connect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise);

        void disconnect(ChannelPromise channelpromise);

        void close(ChannelPromise channelpromise);

        void closeForcibly();

        /** @deprecated */
        @Deprecated
        void deregister(ChannelPromise channelpromise);

        void beginRead();

        void write(Object object, ChannelPromise channelpromise);

        void flush();

        ChannelPromise voidPromise();

        ChannelOutboundBuffer outboundBuffer();
    }
}
