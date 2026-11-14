package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.MembershipKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class NioDatagramChannel extends AbstractNioMessageChannel implements DatagramChannel {

    private static final ChannelMetadata METADATA = new ChannelMetadata(true);
    private final DatagramChannelConfig config;
    private final Map<InetAddress, List<MembershipKey>> memberships;
    private RecvByteBufAllocator.Handle allocHandle;

    private static java.nio.channels.DatagramChannel newSocket() {
        try {
            return java.nio.channels.DatagramChannel.open();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open a socket.", ioexception);
        }
    }

    private static java.nio.channels.DatagramChannel newSocket(InternetProtocolFamily internetprotocolfamily) {
        if (internetprotocolfamily == null) {
            return newSocket();
        } else if (PlatformDependent.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        } else {
            try {
                return java.nio.channels.DatagramChannel.open(ProtocolFamilyConverter.convert(internetprotocolfamily));
            } catch (IOException ioexception) {
                throw new ChannelException("Failed to open a socket.", ioexception);
            }
        }
    }

    public NioDatagramChannel() {
        this(newSocket());
    }

    public NioDatagramChannel(InternetProtocolFamily internetprotocolfamily) {
        this(newSocket(internetprotocolfamily));
    }

    public NioDatagramChannel(java.nio.channels.DatagramChannel java_nio_channels_datagramchannel) {
        super((Channel) null, java_nio_channels_datagramchannel, 1);
        this.memberships = new HashMap();
        this.config = new NioDatagramChannelConfig(this, java_nio_channels_datagramchannel);
    }

    @Override
    public ChannelMetadata metadata() {
        return NioDatagramChannel.METADATA;
    }

    @Override
    public DatagramChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isActive() {
        java.nio.channels.DatagramChannel java_nio_channels_datagramchannel = this.javaChannel();

        return java_nio_channels_datagramchannel.isOpen() && ((Boolean) this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) && this.isRegistered() || java_nio_channels_datagramchannel.socket().isBound());
    }

    @Override
    public boolean isConnected() {
        return this.javaChannel().isConnected();
    }

    @Override
    protected java.nio.channels.DatagramChannel javaChannel() {
        return (java.nio.channels.DatagramChannel) super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.javaChannel().socket().bind(socketaddress);
    }

    @Override
    protected boolean doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        if (socketaddress1 != null) {
            this.javaChannel().socket().bind(socketaddress1);
        }

        boolean flag = false;

        boolean flag1;

        try {
            this.javaChannel().connect(socketaddress);
            flag = true;
            flag1 = true;
        } finally {
            if (!flag) {
                this.doClose();
            }

        }

        return flag1;
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new Error();
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.javaChannel().disconnect();
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        java.nio.channels.DatagramChannel java_nio_channels_datagramchannel = this.javaChannel();
        DatagramChannelConfig datagramchannelconfig = this.config();
        RecvByteBufAllocator.Handle recvbytebufallocator_handle = this.allocHandle;

        if (recvbytebufallocator_handle == null) {
            this.allocHandle = recvbytebufallocator_handle = datagramchannelconfig.getRecvByteBufAllocator().newHandle();
        }

        ByteBuf bytebuf = recvbytebufallocator_handle.allocate(datagramchannelconfig.getAllocator());
        boolean flag = true;

        byte b0;

        try {
            ByteBuffer bytebuffer = bytebuf.nioBuffer(bytebuf.writerIndex(), bytebuf.writableBytes());
            InetSocketAddress inetsocketaddress = (InetSocketAddress) java_nio_channels_datagramchannel.receive(bytebuffer);
            int i;

            if (inetsocketaddress != null) {
                i = bytebuffer.position();
                bytebuf.writerIndex(bytebuf.writerIndex() + i);
                recvbytebufallocator_handle.record(i);
                list.add(new DatagramPacket(bytebuf, this.localAddress(), inetsocketaddress));
                flag = false;
                byte b1 = 1;

                return b1;
            }

            i = 0;
            return i;
        } catch (Throwable throwable) {
            PlatformDependent.throwException(throwable);
            b0 = -1;
        } finally {
            if (flag) {
                bytebuf.release();
            }

        }

        return b0;
    }

    @Override
    protected boolean doWriteMessage(Object object, ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
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

        if (i == 0) {
            return true;
        } else {
            ByteBufAllocator bytebufallocator = this.alloc();
            boolean flag = bytebuf.nioBufferCount() != 1;

            if (!flag && !bytebuf.isDirect() && bytebufallocator.isDirectBufferPooled()) {
                flag = true;
            }

            ByteBuffer bytebuffer;

            if (!flag) {
                bytebuffer = bytebuf.nioBuffer();
            } else {
                bytebuf = bytebufallocator.directBuffer(i).writeBytes(bytebuf);
                bytebuffer = bytebuf.nioBuffer();
            }

            int j;

            if (socketaddress != null) {
                j = this.javaChannel().send(bytebuffer, socketaddress);
            } else {
                j = this.javaChannel().write(bytebuffer);
            }

            boolean flag1 = j > 0;

            if (flag) {
                if (socketaddress == null) {
                    channeloutboundbuffer.current(bytebuf);
                } else if (!flag1) {
                    channeloutboundbuffer.current(new DefaultAddressedEnvelope<>(bytebuf, socketaddress));
                } else {
                    channeloutboundbuffer.current(bytebuf);
                }
            }

            return flag1;
        }
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
    public ChannelFuture joinGroup(InetAddress inetaddress) {
        return this.joinGroup(inetaddress, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress inetaddress, ChannelPromise channelpromise) {
        try {
            return this.joinGroup(inetaddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), (InetAddress) null, channelpromise);
        } catch (SocketException socketexception) {
            channelpromise.setFailure(socketexception);
            return channelpromise;
        }
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface) {
        return this.joinGroup(inetsocketaddress, networkinterface, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface, ChannelPromise channelpromise) {
        return this.joinGroup(inetsocketaddress.getAddress(), networkinterface, (InetAddress) null, channelpromise);
    }

    @Override
    public ChannelFuture joinGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1) {
        return this.joinGroup(inetaddress, networkinterface, inetaddress1, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise) {
        if (PlatformDependent.javaVersion() >= 7) {
            if (inetaddress == null) {
                throw new NullPointerException("multicastAddress");
            } else if (networkinterface == null) {
                throw new NullPointerException("networkInterface");
            } else {
                try {
                    MembershipKey membershipkey;

                    if (inetaddress1 == null) {
                        membershipkey = this.javaChannel().join(inetaddress, networkinterface);
                    } else {
                        membershipkey = this.javaChannel().join(inetaddress, networkinterface, inetaddress1);
                    }

                    synchronized (this) {
                        Object object = (List) this.memberships.get(inetaddress);

                        if (object == null) {
                            object = new ArrayList();
                            this.memberships.put(inetaddress, object);
                        }

                        ((List) object).add(membershipkey);
                    }

                    channelpromise.setSuccess();
                } catch (Throwable throwable) {
                    channelpromise.setFailure(throwable);
                }

                return channelpromise;
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress inetaddress) {
        return this.leaveGroup(inetaddress, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress inetaddress, ChannelPromise channelpromise) {
        try {
            return this.leaveGroup(inetaddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), (InetAddress) null, channelpromise);
        } catch (SocketException socketexception) {
            channelpromise.setFailure(socketexception);
            return channelpromise;
        }
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface) {
        return this.leaveGroup(inetsocketaddress, networkinterface, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress inetsocketaddress, NetworkInterface networkinterface, ChannelPromise channelpromise) {
        return this.leaveGroup(inetsocketaddress.getAddress(), networkinterface, (InetAddress) null, channelpromise);
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1) {
        return this.leaveGroup(inetaddress, networkinterface, inetaddress1, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise) {
        if (PlatformDependent.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        } else if (inetaddress == null) {
            throw new NullPointerException("multicastAddress");
        } else if (networkinterface == null) {
            throw new NullPointerException("networkInterface");
        } else {
            synchronized (this) {
                if (this.memberships != null) {
                    List list = (List) this.memberships.get(inetaddress);

                    if (list != null) {
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            MembershipKey membershipkey = (MembershipKey) iterator.next();

                            if (networkinterface.equals(membershipkey.networkInterface()) && (inetaddress1 == null && membershipkey.sourceAddress() == null || inetaddress1 != null && inetaddress1.equals(membershipkey.sourceAddress()))) {
                                membershipkey.drop();
                                iterator.remove();
                            }
                        }

                        if (list.isEmpty()) {
                            this.memberships.remove(inetaddress);
                        }
                    }
                }
            }

            channelpromise.setSuccess();
            return channelpromise;
        }
    }

    @Override
    public ChannelFuture block(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1) {
        return this.block(inetaddress, networkinterface, inetaddress1, this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress inetaddress, NetworkInterface networkinterface, InetAddress inetaddress1, ChannelPromise channelpromise) {
        if (PlatformDependent.javaVersion() < 7) {
            throw new UnsupportedOperationException();
        } else if (inetaddress == null) {
            throw new NullPointerException("multicastAddress");
        } else if (inetaddress1 == null) {
            throw new NullPointerException("sourceToBlock");
        } else if (networkinterface == null) {
            throw new NullPointerException("networkInterface");
        } else {
            synchronized (this) {
                if (this.memberships != null) {
                    List list = (List) this.memberships.get(inetaddress);
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        MembershipKey membershipkey = (MembershipKey) iterator.next();

                        if (networkinterface.equals(membershipkey.networkInterface())) {
                            try {
                                membershipkey.block(inetaddress1);
                            } catch (IOException ioexception) {
                                channelpromise.setFailure(ioexception);
                            }
                        }
                    }
                }
            }

            channelpromise.setSuccess();
            return channelpromise;
        }
    }

    @Override
    public ChannelFuture block(InetAddress inetaddress, InetAddress inetaddress1) {
        return this.block(inetaddress, inetaddress1, this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress inetaddress, InetAddress inetaddress1, ChannelPromise channelpromise) {
        try {
            return this.block(inetaddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), inetaddress1, channelpromise);
        } catch (SocketException socketexception) {
            channelpromise.setFailure(socketexception);
            return channelpromise;
        }
    }
}
