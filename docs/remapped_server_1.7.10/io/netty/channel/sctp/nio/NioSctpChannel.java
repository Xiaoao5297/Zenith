package io.netty.channel.sctp.nio;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.sctp.DefaultSctpChannelConfig;
import io.netty.channel.sctp.SctpChannel;
import io.netty.channel.sctp.SctpChannelConfig;
import io.netty.channel.sctp.SctpMessage;
import io.netty.channel.sctp.SctpNotificationHandler;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NioSctpChannel extends AbstractNioMessageChannel implements SctpChannel {

    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSctpChannel.class);
    private final SctpChannelConfig config;
    private final NotificationHandler<?> notificationHandler;
    private RecvByteBufAllocator.Handle allocHandle;

    private static com.sun.nio.sctp.SctpChannel newSctpChannel() {
        try {
            return com.sun.nio.sctp.SctpChannel.open();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open a sctp channel.", ioexception);
        }
    }

    public NioSctpChannel() {
        this(newSctpChannel());
    }

    public NioSctpChannel(com.sun.nio.sctp.SctpChannel com_sun_nio_sctp_sctpchannel) {
        this((Channel) null, com_sun_nio_sctp_sctpchannel);
    }

    public NioSctpChannel(Channel channel, com.sun.nio.sctp.SctpChannel com_sun_nio_sctp_sctpchannel) {
        super(channel, com_sun_nio_sctp_sctpchannel, 1);

        try {
            com_sun_nio_sctp_sctpchannel.configureBlocking(false);
            this.config = new DefaultSctpChannelConfig(this, com_sun_nio_sctp_sctpchannel);
            this.notificationHandler = new SctpNotificationHandler(this);
        } catch (IOException ioexception) {
            try {
                com_sun_nio_sctp_sctpchannel.close();
            } catch (IOException ioexception1) {
                if (NioSctpChannel.logger.isWarnEnabled()) {
                    NioSctpChannel.logger.warn("Failed to close a partially initialized sctp channel.", (Throwable) ioexception1);
                }
            }

            throw new ChannelException("Failed to enter non-blocking mode.", ioexception);
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
    public SctpServerChannel parent() {
        return (SctpServerChannel) super.parent();
    }

    @Override
    public ChannelMetadata metadata() {
        return NioSctpChannel.METADATA;
    }

    @Override
    public Association association() {
        try {
            return this.javaChannel().association();
        } catch (IOException ioexception) {
            return null;
        }
    }

    @Override
    public Set<InetSocketAddress> allLocalAddresses() {
        try {
            Set set = this.javaChannel().getAllLocalAddresses();
            LinkedHashSet linkedhashset = new LinkedHashSet(set.size());
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                SocketAddress socketaddress = (SocketAddress) iterator.next();

                linkedhashset.add((InetSocketAddress) socketaddress);
            }

            return linkedhashset;
        } catch (Throwable throwable) {
            return Collections.emptySet();
        }
    }

    @Override
    public SctpChannelConfig config() {
        return this.config;
    }

    @Override
    public Set<InetSocketAddress> allRemoteAddresses() {
        try {
            Set set = this.javaChannel().getRemoteAddresses();
            HashSet hashset = new HashSet(set.size());
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                SocketAddress socketaddress = (SocketAddress) iterator.next();

                hashset.add((InetSocketAddress) socketaddress);
            }

            return hashset;
        } catch (Throwable throwable) {
            return Collections.emptySet();
        }
    }

    @Override
    protected com.sun.nio.sctp.SctpChannel javaChannel() {
        return (com.sun.nio.sctp.SctpChannel) super.javaChannel();
    }

    @Override
    public boolean isActive() {
        com.sun.nio.sctp.SctpChannel com_sun_nio_sctp_sctpchannel = this.javaChannel();

        return com_sun_nio_sctp_sctpchannel.isOpen() && this.association() != null;
    }

    @Override
    protected SocketAddress localAddress0() {
        try {
            Iterator iterator = this.javaChannel().getAllLocalAddresses().iterator();

            if (iterator.hasNext()) {
                return (SocketAddress) iterator.next();
            }
        } catch (IOException ioexception) {
            ;
        }

        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        try {
            Iterator iterator = this.javaChannel().getRemoteAddresses().iterator();

            if (iterator.hasNext()) {
                return (SocketAddress) iterator.next();
            }
        } catch (IOException ioexception) {
            ;
        }

        return null;
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.javaChannel().bind(socketaddress);
    }

    @Override
    protected boolean doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        if (socketaddress1 != null) {
            this.javaChannel().bind(socketaddress1);
        }

        boolean flag = false;

        boolean flag1;

        try {
            boolean flag2 = this.javaChannel().connect(socketaddress);

            if (!flag2) {
                this.selectionKey().interestOps(8);
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
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new Error();
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        com.sun.nio.sctp.SctpChannel com_sun_nio_sctp_sctpchannel = this.javaChannel();
        RecvByteBufAllocator.Handle recvbytebufallocator_handle = this.allocHandle;

        if (recvbytebufallocator_handle == null) {
            this.allocHandle = recvbytebufallocator_handle = this.config().getRecvByteBufAllocator().newHandle();
        }

        ByteBuf bytebuf = recvbytebufallocator_handle.allocate(this.config().getAllocator());
        boolean flag = true;
        boolean flag1 = false;

        int i;
        int j;
        label108:
        {
            label109:
            {
                byte b0;

                try {
                    flag1 = true;
                    ByteBuffer bytebuffer = bytebuf.nioBuffer(bytebuf.writerIndex(), bytebuf.writableBytes());
                    MessageInfo messageinfo = com_sun_nio_sctp_sctpchannel.receive(bytebuffer, (Object) null, this.notificationHandler);

                    if (messageinfo != null) {
                        bytebuffer.flip();
                        list.add(new SctpMessage(messageinfo, bytebuf.writerIndex(bytebuf.writerIndex() + bytebuffer.remaining())));
                        flag = false;
                        i = 1;
                        flag1 = false;
                        break label108;
                    }

                    i = 0;
                    flag1 = false;
                    break label109;
                } catch (Throwable throwable) {
                    PlatformDependent.throwException(throwable);
                    b0 = -1;
                    flag1 = false;
                } finally {
                    if (flag1) {
                        int k = bytebuf.readableBytes();

                        recvbytebufallocator_handle.record(k);
                        if (flag) {
                            bytebuf.release();
                        }

                    }
                }

                i = bytebuf.readableBytes();
                recvbytebufallocator_handle.record(i);
                if (flag) {
                    bytebuf.release();
                }

                return b0;
            }

            j = bytebuf.readableBytes();
            recvbytebufallocator_handle.record(j);
            if (flag) {
                bytebuf.release();
            }

            return i;
        }

        j = bytebuf.readableBytes();
        recvbytebufallocator_handle.record(j);
        if (flag) {
            bytebuf.release();
        }

        return i;
    }

    @Override
    protected boolean doWriteMessage(Object object, ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        SctpMessage sctpmessage = (SctpMessage) object;
        ByteBuf bytebuf = sctpmessage.content();
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

            MessageInfo messageinfo = MessageInfo.createOutgoing(this.association(), (SocketAddress) null, sctpmessage.streamIdentifier());

            messageinfo.payloadProtocolID(sctpmessage.protocolIdentifier());
            messageinfo.streamNumber(sctpmessage.streamIdentifier());
            int j = this.javaChannel().send(bytebuffer, messageinfo);
            boolean flag1 = j > 0;

            if (flag) {
                if (!flag1) {
                    channeloutboundbuffer.current(new SctpMessage(messageinfo, bytebuf));
                } else {
                    channeloutboundbuffer.current(bytebuf);
                }
            }

            return flag1;
        }
    }

    @Override
    public ChannelFuture bindAddress(InetAddress inetaddress) {
        return this.bindAddress(inetaddress, this.newPromise());
    }

    @Override
    public ChannelFuture bindAddress(final InetAddress inetaddress, final ChannelPromise channelpromise) {
        if (this.eventLoop().inEventLoop()) {
            try {
                this.javaChannel().bindAddress(inetaddress);
                channelpromise.setSuccess();
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
            }
        } else {
            this.eventLoop().execute(new Runnable() {
                public void run() {
                    NioSctpChannel.this.bindAddress(inetaddress, channelpromise);
                }
            });
        }

        return channelpromise;
    }

    @Override
    public ChannelFuture unbindAddress(InetAddress inetaddress) {
        return this.unbindAddress(inetaddress, this.newPromise());
    }

    @Override
    public ChannelFuture unbindAddress(final InetAddress inetaddress, final ChannelPromise channelpromise) {
        if (this.eventLoop().inEventLoop()) {
            try {
                this.javaChannel().unbindAddress(inetaddress);
                channelpromise.setSuccess();
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
            }
        } else {
            this.eventLoop().execute(new Runnable() {
                public void run() {
                    NioSctpChannel.this.unbindAddress(inetaddress, channelpromise);
                }
            });
        }

        return channelpromise;
    }
}
