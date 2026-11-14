package io.netty.channel.socket.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DefaultServerSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

public class NioServerSocketChannel extends AbstractNioMessageChannel implements ServerSocketChannel {

    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioServerSocketChannel.class);
    private final ServerSocketChannelConfig config = new DefaultServerSocketChannelConfig(this, this.javaChannel().socket());

    private static java.nio.channels.ServerSocketChannel newSocket() {
        try {
            return java.nio.channels.ServerSocketChannel.open();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open a server socket.", ioexception);
        }
    }

    public NioServerSocketChannel() {
        super((Channel) null, newSocket(), 16);
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return NioServerSocketChannel.METADATA;
    }

    @Override
    public ServerSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isActive() {
        return this.javaChannel().socket().isBound();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    protected java.nio.channels.ServerSocketChannel javaChannel() {
        return (java.nio.channels.ServerSocketChannel) super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.javaChannel().socket().bind(socketaddress, this.config.getBacklog());
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        SocketChannel socketchannel = this.javaChannel().accept();

        try {
            if (socketchannel != null) {
                list.add(new NioSocketChannel(this, socketchannel));
                return 1;
            }
        } catch (Throwable throwable) {
            NioServerSocketChannel.logger.warn("Failed to create a new channel from an accepted socket.", throwable);

            try {
                socketchannel.close();
            } catch (Throwable throwable1) {
                NioServerSocketChannel.logger.warn("Failed to close a socket.", throwable1);
            }
        }

        return 0;
    }

    @Override
    protected boolean doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean doWriteMessage(Object object, ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        throw new UnsupportedOperationException();
    }
}
