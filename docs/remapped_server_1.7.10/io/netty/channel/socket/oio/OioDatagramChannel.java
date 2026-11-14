package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

public class OioDatagramChannel extends AbstractOioMessageChannel implements DatagramChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioDatagramChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(true);
    private final MulticastSocket socket;
    private final DatagramChannelConfig config;
    private final DatagramPacket tmpPacket;
    private RecvByteBufAllocator.Handle allocHandle;

    private static MulticastSocket newSocket() {
        try {
            return new MulticastSocket((SocketAddress) null);
        } catch (Exception exception) {
            throw new ChannelException("failed to create a new socket", exception);
        }
    }

    public OioDatagramChannel() {
        this(newSocket());
    }

    public OioDatagramChannel(MulticastSocket multicastsocket) {
        super((Channel) null);
        this.tmpPacket = new DatagramPacket(EmptyArrays.EMPTY_BYTES, 0);
        boolean flag = false;

        try {
            multicastsocket.setSoTimeout(1000);
            multicastsocket.setBroadcast(false);
            flag = true;
        } catch (SocketException socketexception) {
            throw new ChannelException("Failed to configure the datagram socket timeout.", socketexception);
        } finally {
            if (!flag) {
                multicastsocket.close();
            }

        }

        this.socket = multicastsocket;
        this.config = new DefaultDatagramChannelConfig(this, multicastsocket);
    }

    @Override
    public ChannelMetadata metadata() {
        return OioDatagramChannel.METADATA;
    }

    @Override
    public DatagramChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return !this.socket.isClosed();
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && (Boolean) this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) && this.isRegistered() || this.socket.isBound();
    }

    @Override
    public boolean isConnected() {
        return this.socket.isConnected();
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
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) super.remoteAddress();
    }

    @Override
    protected void doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        if (socketaddress1 != null) {
            this.socket.bind(socketaddress1);
        }

        boolean flag = false;

        try {
            this.socket.connect(socketaddress);
            flag = true;
        } finally {
            if (!flag) {
                try {
                    this.socket.close();
                } catch (Throwable throwable) {
                    OioDatagramChannel.logger.warn("Failed to close a socket.", throwable);
                }
            }

        }

    }

    @Override
    protected void doDisconnect() throws Exception {
        this.socket.disconnect();
    }

    @Override
    protected void doClose() throws Exception {
        this.socket.close();
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        DatagramChannelConfig datagramchannelconfig = this.config();
        RecvByteBufAllocator.Handle recvbytebufallocator_handle = this.allocHandle;

        if (recvbytebufallocator_handle == null) {
            this.allocHandle = recvbytebufallocator_handle = datagramchannelconfig.getRecvByteBufAllocator().newHandle();
        }

        ByteBuf bytebuf = datagramchannelconfig.getAllocator().heapBuffer(recvbytebufallocator_handle.guess());
        boolean flag = true;

        int i;

        try {
            this.tmpPacket.setData(bytebuf.array(), bytebuf.arrayOffset(), bytebuf.capacity());
            this.socket.receive(this.tmpPacket);
            InetSocketAddress inetsocketaddress = (InetSocketAddress) this.tmpPacket.getSocketAddress();

            if (inetsocketaddress == null) {
                inetsocketaddress = this.remoteAddress();
            }

            i = this.tmpPacket.getLength();
            recvbytebufallocator_handle.record(i);
            list.add(new io.netty.channel.socket.DatagramPacket(bytebuf.writerIndex(i), this.localAddress(), inetsocketaddress));
            flag = false;
            byte b0 = 1;

            return b0;
        } catch (SocketTimeoutException sockettimeoutexception) {
            i = 0;
            return i;
        } catch (SocketException socketexception) {
            if (!socketexception.getMessage().toLowerCase(Locale.US).contains("socket closed")) {
                throw socketexception;
            }

            i = -1;
            return i;
        } catch (Throwable throwable) {
            PlatformDependent.throwException(throwable);
            i = -1;
        } finally {
            if (flag) {
                bytebuf.release();
            }

        }

        return i;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        while (true) {
            Object object = channeloutboundbuffer.current();

            if (object == null) {
                return;
            }

            SocketAddress socketaddress;
            Object object1;

            if (object instanceof AddressedEnvelope) {
                AddressedEnvelope addressedenvelope = (AddressedEnvelope) object;

                socketaddress = addressedenvelope.recipient();
                object1 = addressedenvelope.content();
            } else {
                object1 = object;
                socketaddress = null;
            }

            ByteBuf bytebuf;

            if (object1 instanceof ByteBufHolder) {
                bytebuf = ((ByteBufHolder) object1).content();
            } else {
                if (!(object1 instanceof ByteBuf)) {
                    throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(object));
                }

                bytebuf = (ByteBuf) object1;
            }

            int i = bytebuf.readableBytes();

            if (socketaddress != null) {
                this.tmpPacket.setSocketAddress(socketaddress);
            }

            if (bytebuf.hasArray()) {
                this.tmpPacket.setData(bytebuf.array(), bytebuf.arrayOffset() + bytebuf.readerIndex(), i);
            } else {
                byte[] abyte = new byte[i];

                bytebuf.getBytes(bytebuf.readerIndex(), abyte);
                this.tmpPacket.setData(abyte);
            }

            this.socket.send(this.tmpPacket);
            channeloutboundbuffer.remove();
        }
    }

    @Override
    public ChannelFuture joinGroup(InetAddress inetaddress) {
        return this.joinGroup(inetaddress, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress inetaddress, ChannelPromise channelpromise) {
        this.ensureBound();

        try {
            this.socket.joinGroup(inetaddress);
            channelpromise.setSuccess();
        } catch (IOException ioexception) {
            channelpromise.setFailure(ioexception);
        }

        return channelpromise;
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface) {
        return this.joinGroup(inetsocketaddress, networkinterface, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface, ChannelPromise channelpromise) {
        this.ensureBound();

        try {
            this.socket.joinGroup(inetsocketaddress, networkinterface);
            channelpromise.setSuccess();
        } catch (IOException ioexception) {
            channelpromise.setFailure(ioexception);
        }

        return channelpromise;
    }

    @Override
    public ChannelFuture joinGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1) {
        return this.newFailedFuture(new UnsupportedOperationException());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise) {
        channelpromise.setFailure(new UnsupportedOperationException());
        return channelpromise;
    }

    private void ensureBound() {
        if (!this.isActive()) {
            throw new IllegalStateException(DatagramChannel.class.getName() + " must be bound to join a group.");
        }
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress inetaddress) {
        return this.leaveGroup(inetaddress, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress inetaddress, ChannelPromise channelpromise) {
        try {
            this.socket.leaveGroup(inetaddress);
            channelpromise.setSuccess();
        } catch (IOException ioexception) {
            channelpromise.setFailure(ioexception);
        }

        return channelpromise;
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface) {
        return this.leaveGroup(inetsocketaddress, networkinterface, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface, ChannelPromise channelpromise) {
        try {
            this.socket.leaveGroup(inetsocketaddress, networkinterface);
            channelpromise.setSuccess();
        } catch (IOException ioexception) {
            channelpromise.setFailure(ioexception);
        }

        return channelpromise;
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1) {
        return this.newFailedFuture(new UnsupportedOperationException());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise) {
        channelpromise.setFailure(new UnsupportedOperationException());
        return channelpromise;
    }

    @Override
    public ChannelFuture block(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1) {
        return this.newFailedFuture(new UnsupportedOperationException());
    }

    @Override
    public ChannelFuture block(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise) {
        channelpromise.setFailure(new UnsupportedOperationException());
        return channelpromise;
    }

    @Override
    public ChannelFuture block(InetAddress inetaddress, InetAddress inetaddress1) {
        return this.newFailedFuture(new UnsupportedOperationException());
    }

    @Override
    public ChannelFuture block(InetAddress inetaddress, InetAddress inetaddress1, ChannelPromise channelpromise) {
        channelpromise.setFailure(new UnsupportedOperationException());
        return channelpromise;
    }
}
