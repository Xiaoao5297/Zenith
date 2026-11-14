package io.netty.channel.udt.nio;

import com.barchart.udt.SocketUDT;
import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.ChannelUDT;
import com.barchart.udt.nio.KindUDT;
import com.barchart.udt.nio.RendezvousChannelUDT;
import com.barchart.udt.nio.SelectorProviderUDT;
import com.barchart.udt.nio.ServerSocketChannelUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtServerChannel;
import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;

public final class NioUdtProvider<T extends UdtChannel> implements ChannelFactory<T> {

    public static final ChannelFactory<UdtServerChannel> BYTE_ACCEPTOR = new NioUdtProvider<>(TypeUDT.STREAM, KindUDT.ACCEPTOR);
    public static final ChannelFactory<UdtChannel> BYTE_CONNECTOR = new NioUdtProvider<>(TypeUDT.STREAM, KindUDT.CONNECTOR);
    public static final SelectorProvider BYTE_PROVIDER = SelectorProviderUDT.STREAM;
    public static final ChannelFactory<UdtChannel> BYTE_RENDEZVOUS = new NioUdtProvider<>(TypeUDT.STREAM, KindUDT.RENDEZVOUS);
    public static final ChannelFactory<UdtServerChannel> MESSAGE_ACCEPTOR = new NioUdtProvider<>(TypeUDT.DATAGRAM, KindUDT.ACCEPTOR);
    public static final ChannelFactory<UdtChannel> MESSAGE_CONNECTOR = new NioUdtProvider<>(TypeUDT.DATAGRAM, KindUDT.CONNECTOR);
    public static final SelectorProvider MESSAGE_PROVIDER = SelectorProviderUDT.DATAGRAM;
    public static final ChannelFactory<UdtChannel> MESSAGE_RENDEZVOUS = new NioUdtProvider<>(TypeUDT.DATAGRAM, KindUDT.RENDEZVOUS);
    private final KindUDT kind;
    private final TypeUDT type;

    public static ChannelUDT channelUDT(Channel channel) {
        return (ChannelUDT) (channel instanceof NioUdtByteAcceptorChannel ? ((NioUdtByteAcceptorChannel) channel).javaChannel() : (channel instanceof NioUdtByteConnectorChannel ? ((NioUdtByteConnectorChannel) channel).javaChannel() : (channel instanceof NioUdtByteRendezvousChannel ? ((NioUdtByteRendezvousChannel) channel).javaChannel() : (channel instanceof NioUdtMessageAcceptorChannel ? ((NioUdtMessageAcceptorChannel) channel).javaChannel() : (channel instanceof NioUdtMessageConnectorChannel ? ((NioUdtMessageConnectorChannel) channel).javaChannel() : (channel instanceof NioUdtMessageRendezvousChannel ? ((NioUdtMessageRendezvousChannel) channel).javaChannel() : null))))));
    }

    protected static ServerSocketChannelUDT newAcceptorChannelUDT(TypeUDT typeudt) {
        try {
            return SelectorProviderUDT.from(typeudt).openServerSocketChannel();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open channel");
        }
    }

    protected static SocketChannelUDT newConnectorChannelUDT(TypeUDT typeudt) {
        try {
            return SelectorProviderUDT.from(typeudt).openSocketChannel();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open channel");
        }
    }

    protected static RendezvousChannelUDT newRendezvousChannelUDT(TypeUDT typeudt) {
        try {
            return SelectorProviderUDT.from(typeudt).openRendezvousChannel();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open channel");
        }
    }

    public static SocketUDT socketUDT(Channel channel) {
        ChannelUDT channeludt = channelUDT(channel);

        return channeludt == null ? null : channeludt.socketUDT();
    }

    private NioUdtProvider(TypeUDT typeudt, KindUDT kindudt) {
        this.type = typeudt;
        this.kind = kindudt;
    }

    public KindUDT kind() {
        return this.kind;
    }

    @Override
    public T newChannel() {
        switch (this.kind) {
            case ACCEPTOR:
                switch (this.type) {
                    case DATAGRAM:
                        return new NioUdtMessageAcceptorChannel();
                    case STREAM:
                        return new NioUdtByteAcceptorChannel();
                    default:
                        throw new IllegalStateException("wrong type=" + this.type);
                }
            case CONNECTOR:
                switch (this.type) {
                    case DATAGRAM:
                        return new NioUdtMessageConnectorChannel();
                    case STREAM:
                        return new NioUdtByteConnectorChannel();
                    default:
                        throw new IllegalStateException("wrong type=" + this.type);
                }
            case RENDEZVOUS:
                switch (this.type) {
                    case DATAGRAM:
                        return new NioUdtMessageRendezvousChannel();
                    case STREAM:
                        return new NioUdtByteRendezvousChannel();
                    default:
                        throw new IllegalStateException("wrong type=" + this.type);
                }
            default:
                throw new IllegalStateException("wrong kind=" + this.kind);
        }
    }

    public TypeUDT type() {
        return this.type;
    }
}
