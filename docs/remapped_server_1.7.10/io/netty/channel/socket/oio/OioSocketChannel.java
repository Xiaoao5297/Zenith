package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.oio.OioByteStreamChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class OioSocketChannel extends OioByteStreamChannel implements SocketChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSocketChannel.class);
    private final Socket socket;
    private final OioSocketChannelConfig config;

    public OioSocketChannel() {
        this(new Socket());
    }

    public OioSocketChannel(Socket socket) {
        this((Channel) null, socket);
    }

    public OioSocketChannel(Channel channel, Socket socket) {
        super(channel);
        this.socket = socket;
        this.config = new DefaultOioSocketChannelConfig(this, socket);
        boolean flag = false;

        try {
            if (socket.isConnected()) {
                this.activate(socket.getInputStream(), socket.getOutputStream());
            }

            socket.setSoTimeout(1000);
            flag = true;
        } catch (Exception exception) {
            throw new ChannelException("failed to initialize a socket", exception);
        } finally {
            if (!flag) {
                try {
                    socket.close();
                } catch (IOException ioexception) {
                    OioSocketChannel.logger.warn("Failed to close a socket.", (Throwable) ioexception);
                }
            }

        }

    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel) super.parent();
    }

    @Override
    public OioSocketChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return !this.socket.isClosed();
    }

    @Override
    public boolean isActive() {
        return !this.socket.isClosed() && this.socket.isConnected();
    }

    @Override
    public boolean isInputShutdown() {
        return super.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown() {
        return this.socket.isOutputShutdown() || !this.isActive();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }

    @Override
    protected int doReadBytes(ByteBuf bytebuf) throws Exception {
        if (this.socket.isClosed()) {
            return -1;
        } else {
            try {
                return super.doReadBytes(bytebuf);
            } catch (SocketTimeoutException sockettimeoutexception) {
                return 0;
            }
        }
    }

    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise channelpromise) {
        EventLoop eventloop = this.eventLoop();

        if (eventloop.inEventLoop()) {
            try {
                this.socket.shutdownOutput();
                channelpromise.setSuccess();
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
            }
        } else {
            eventloop.execute(new Runnable() {
                public void run() {
                    OioSocketChannel.this.shutdownOutput(channelpromise);
                }
            });
        }

        return channelpromise;
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) super.remoteAddress();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.socket.getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.socket.getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.socket.bind(socketaddress);
    }

    @Override
    protected void doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        if (socketaddress1 != null) {
            this.socket.bind(socketaddress1);
        }

        boolean flag = false;

        try {
            this.socket.connect(socketaddress, this.config().getConnectTimeoutMillis());
            this.activate(this.socket.getInputStream(), this.socket.getOutputStream());
            flag = true;
        } catch (SocketTimeoutException sockettimeoutexception) {
            ConnectTimeoutException connecttimeoutexception = new ConnectTimeoutException("connection timed out: " + socketaddress);

            connecttimeoutexception.setStackTrace(sockettimeoutexception.getStackTrace());
            throw connecttimeoutexception;
        } finally {
            if (!flag) {
                this.doClose();
            }

        }

    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.socket.close();
    }

    @Override
    protected boolean checkInputShutdown() {
        if (this.isInputShutdown()) {
            try {
                Thread.sleep((long) this.config().getSoTimeout());
            } catch (Throwable throwable) {
                ;
            }

            return true;
        } else {
            return false;
        }
    }
}
