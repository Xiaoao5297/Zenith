package io.netty.channel.socket.oio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OioServerSocketChannel extends AbstractOioMessageChannel implements ServerSocketChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioServerSocketChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    final ServerSocket socket;
    final Lock shutdownLock;
    private final OioServerSocketChannelConfig config;

    private static ServerSocket newServerSocket() {
        try {
            return new ServerSocket();
        } catch (IOException ioexception) {
            throw new ChannelException("failed to create a server socket", ioexception);
        }
    }

    public OioServerSocketChannel() {
        this(newServerSocket());
    }

    public OioServerSocketChannel(ServerSocket serversocket) {
        super((Channel) null);
        this.shutdownLock = new ReentrantLock();
        if (serversocket == null) {
            throw new NullPointerException("socket");
        } else {
            boolean flag = false;

            try {
                serversocket.setSoTimeout(1000);
                flag = true;
            } catch (IOException ioexception) {
                throw new ChannelException("Failed to set the server socket timeout.", ioexception);
            } finally {
                if (!flag) {
                    try {
                        serversocket.close();
                    } catch (IOException ioexception1) {
                        if (OioServerSocketChannel.logger.isWarnEnabled()) {
                            OioServerSocketChannel.logger.warn("Failed to close a partially initialized socket.", (Throwable) ioexception1);
                        }
                    }
                }

            }

            this.socket = serversocket;
            this.config = new DefaultOioServerSocketChannelConfig(this, serversocket);
        }
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return OioServerSocketChannel.METADATA;
    }

    @Override
    public OioServerSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return !this.socket.isClosed();
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && this.socket.isBound();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.socket.getLocalSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.socket.bind(socketaddress, this.config.getBacklog());
    }

    @Override
    protected void doClose() throws Exception {
        this.socket.close();
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        if (this.socket.isClosed()) {
            return -1;
        } else {
            try {
                Socket socket = this.socket.accept();

                try {
                    if (socket != null) {
                        list.add(new OioSocketChannel(this, socket));
                        return 1;
                    }
                } catch (Throwable throwable) {
                    OioServerSocketChannel.logger.warn("Failed to create a new channel from an accepted socket.", throwable);
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Throwable throwable1) {
                            OioServerSocketChannel.logger.warn("Failed to close a socket.", throwable1);
                        }
                    }
                }
            } catch (SocketTimeoutException sockettimeoutexception) {
                ;
            }

            return 0;
        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
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
}
