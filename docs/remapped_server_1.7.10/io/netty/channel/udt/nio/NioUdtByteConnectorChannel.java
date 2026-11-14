package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.udt.DefaultUdtChannelConfig;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class NioUdtByteConnectorChannel extends AbstractNioByteChannel implements UdtChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioUdtByteConnectorChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final UdtChannelConfig config;

    public NioUdtByteConnectorChannel() {
        this(TypeUDT.STREAM);
    }

    public NioUdtByteConnectorChannel(Channel channel, SocketChannelUDT socketchanneludt) {
        super(channel, socketchanneludt);

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
                if (NioUdtByteConnectorChannel.logger.isWarnEnabled()) {
                    NioUdtByteConnectorChannel.logger.warn("Failed to close channel.", (Throwable) exception1);
                }
            }

            throw new ChannelException("Failed to configure channel.", exception);
        }
    }

    public NioUdtByteConnectorChannel(SocketChannelUDT socketchanneludt) {
        this((Channel) null, socketchanneludt);
    }

    public NioUdtByteConnectorChannel(TypeUDT typeudt) {
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
    protected int doReadBytes(ByteBuf bytebuf) throws Exception {
        return bytebuf.writeBytes((ScatteringByteChannel) this.javaChannel(), bytebuf.writableBytes());
    }

    @Override
    protected int doWriteBytes(ByteBuf bytebuf) throws Exception {
        int i = bytebuf.readableBytes();
        int j = bytebuf.readBytes((GatheringByteChannel) this.javaChannel(), i);

        return j;
    }

    @Override
    protected long doWriteFileRegion(FileRegion fileregion) throws Exception {
        throw new UnsupportedOperationException();
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
        return NioUdtByteConnectorChannel.METADATA;
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
