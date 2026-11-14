package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.ServerSocketChannelUDT;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.udt.DefaultUdtServerChannelConfig;
import io.netty.channel.udt.UdtServerChannel;
import io.netty.channel.udt.UdtServerChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class NioUdtAcceptorChannel extends AbstractNioMessageChannel implements UdtServerChannel {

    protected static final InternalLogger logger = InternalLoggerFactory.getInstance(NioUdtAcceptorChannel.class);
    private final UdtServerChannelConfig config;

    protected NioUdtAcceptorChannel(ServerSocketChannelUDT serversocketchanneludt) {
        super((Channel) null, serversocketchanneludt, 16);

        try {
            serversocketchanneludt.configureBlocking(false);
            this.config = new DefaultUdtServerChannelConfig(this, serversocketchanneludt, true);
        } catch (Exception exception) {
            try {
                serversocketchanneludt.close();
            } catch (Exception exception1) {
                if (NioUdtAcceptorChannel.logger.isWarnEnabled()) {
                    NioUdtAcceptorChannel.logger.warn("Failed to close channel.", (Throwable) exception1);
                }
            }

            throw new ChannelException("Failed to configure channel.", exception);
        }
    }

    protected NioUdtAcceptorChannel(TypeUDT typeudt) {
        this(NioUdtProvider.newAcceptorChannelUDT(typeudt));
    }

    @Override
    public UdtServerChannelConfig config() {
        return this.config;
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
    protected boolean doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean doWriteMessage(Object object, ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isActive() {
        return this.javaChannel().socket().isBound();
    }

    @Override
    protected ServerSocketChannelUDT javaChannel() {
        return (ServerSocketChannelUDT) super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }
}
