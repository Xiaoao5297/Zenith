package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.udt.DefaultUdtChannelConfig;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtChannelConfig;
import io.netty.channel.udt.UdtMessage;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ScatteringByteChannel;
import java.util.List;

public class NioUdtMessageConnectorChannel extends AbstractNioMessageChannel implements UdtChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioUdtMessageConnectorChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final UdtChannelConfig config;

    public NioUdtMessageConnectorChannel() {
        this(TypeUDT.DATAGRAM);
    }

    public NioUdtMessageConnectorChannel(Channel channel, SocketChannelUDT socketchanneludt) {
        super(channel, socketchanneludt, 1);

        try {
            socketchanneludt.configureBlocking(false);
            switch (socketchanneludt.socketUDT().status()) {
                case INIT:
                case OPENED:
                    this.config = new DefaultUdtChannelConfig(this, socketchanneludt, true);
                    break;
                default:
                    this.config = new DefaultUdtChannelConfig(this, socketchanneludt, false);
            }

        } catch (Exception exception) {
            try {
                socketchanneludt.close();
            } catch (Exception exception1) {
                if (NioUdtMessageConnectorChannel.logger.isWarnEnabled()) {
                    NioUdtMessageConnectorChannel.logger.warn("Failed to close channel.", (Throwable) exception1);
                }
            }

            throw new ChannelException("Failed to configure channel.", exception);
        }
    }

    public NioUdtMessageConnectorChannel(SocketChannelUDT socketchanneludt) {
        this((Channel) null, socketchanneludt);
    }

    public NioUdtMessageConnectorChannel(TypeUDT typeudt) {
        this(NioUdtProvider.newConnectorChannelUDT(typeudt));
    }

    @Override
    public UdtChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.javaChannel().bind(socketaddress);
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    @Override
    protected boolean doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        this.doBind((SocketAddress) (socketaddress1 != null ? socketaddress1 : new InetSocketAddress(0)));
        boolean flag = false;

        boolean flag1;

        try {
            boolean flag2 = this.javaChannel().connect(socketaddress);

            if (!flag2) {
                this.selectionKey().interestOps(this.selectionKey().interestOps() | 8);
            }

            flag = true;
            flag1 = flag2;
        } finally {
            if (!flag) {
                this.doClose();
            }

        }

        return flag1;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doFinishConnect() throws Exception {
        if (this.javaChannel().finishConnect()) {
            this.selectionKey().interestOps(this.selectionKey().interestOps() & -9);
        } else {
            throw new Error("Provider error: failed to finish connect. Provider library should be upgraded.");
        }
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        int i = this.config.getReceiveBufferSize();
        ByteBuf bytebuf = this.config.getAllocator().directBuffer(i);
        int j = bytebuf.writeBytes((ScatteringByteChannel) this.javaChannel(), i);

        if (j <= 0) {
            bytebuf.release();
            return 0;
        } else if (j >= i) {
            this.javaChannel().close();
            throw new ChannelException("Invalid config : increase receive buffer size to avoid message truncation");
        } else {
            list.add(new UdtMessage(bytebuf));
            return 1;
        }
    }

    @Override
    protected boolean doWriteMessage(Object object, ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        UdtMessage udtmessage = (UdtMessage) object;
        ByteBuf bytebuf = udtmessage.content();
        int i = bytebuf.readableBytes();
        long j;

        if (bytebuf.nioBufferCount() == 1) {
            j = (long) this.javaChannel().write(bytebuf.nioBuffer());
        } else {
            j = this.javaChannel().write(bytebuf.nioBuffers());
        }

        if (j <= 0L && i > 0) {
            return false;
        } else if (j != (long) i) {
            throw new Error("Provider error: failed to write message. Provider library should be upgraded.");
        } else {
            return true;
        }
    }

    @Override
    public boolean isActive() {
        SocketChannelUDT socketchanneludt = this.javaChannel();

        return socketchanneludt.isOpen() && socketchanneludt.isConnectFinished();
    }

    @Override
    protected SocketChannelUDT javaChannel() {
        return (SocketChannelUDT) super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return NioUdtMessageConnectorChannel.METADATA;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) super.remoteAddress();
    }
}
