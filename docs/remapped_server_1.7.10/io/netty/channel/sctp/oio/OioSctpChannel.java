package io.netty.channel.sctp.oio;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioMessageChannel;
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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OioSctpChannel extends AbstractOioMessageChannel implements SctpChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSctpChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final com.sun.nio.sctp.SctpChannel ch;
    private final SctpChannelConfig config;
    private final Selector readSelector;
    private final Selector writeSelector;
    private final Selector connectSelector;
    private final NotificationHandler<?> notificationHandler;
    private RecvByteBufAllocator.Handle allocHandle;

    private static com.sun.nio.sctp.SctpChannel openChannel() {
        try {
            return com.sun.nio.sctp.SctpChannel.open();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open a sctp channel.", ioexception);
        }
    }

    public OioSctpChannel() {
        this(openChannel());
    }

    public OioSctpChannel(com.sun.nio.sctp.SctpChannel com_sun_nio_sctp_sctpchannel) {
        this((Channel) null, com_sun_nio_sctp_sctpchannel);
    }

    public OioSctpChannel(Channel channel, com.sun.nio.sctp.SctpChannel com_sun_nio_sctp_sctpchannel) {
        super(channel);
        this.ch = com_sun_nio_sctp_sctpchannel;
        boolean flag = false;

        try {
            com_sun_nio_sctp_sctpchannel.configureBlocking(false);
            this.readSelector = Selector.open();
            this.writeSelector = Selector.open();
            this.connectSelector = Selector.open();
            com_sun_nio_sctp_sctpchannel.register(this.readSelector, 1);
            com_sun_nio_sctp_sctpchannel.register(this.writeSelector, 4);
            com_sun_nio_sctp_sctpchannel.register(this.connectSelector, 8);
            this.config = new DefaultSctpChannelConfig(this, com_sun_nio_sctp_sctpchannel);
            this.notificationHandler = new SctpNotificationHandler(this);
            flag = true;
        } catch (Exception exception) {
            throw new ChannelException("failed to initialize a sctp channel", exception);
        } finally {
            if (!flag) {
                try {
                    com_sun_nio_sctp_sctpchannel.close();
                } catch (IOException ioexception) {
                    OioSctpChannel.logger.warn("Failed to close a sctp channel.", (Throwable) ioexception);
                }
            }

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
        return OioSctpChannel.METADATA;
    }

    @Override
    public SctpChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return this.ch.isOpen();
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        if (!this.readSelector.isOpen()) {
            return 0;
        } else {
            int i = 0;
            int j = this.readSelector.select(1000L);
            boolean flag = j > 0;

            if (!flag) {
                return i;
            } else {
                Set set = this.readSelector.selectedKeys();

                int k;

                try {
                    Iterator iterator = set.iterator();

                    RecvByteBufAllocator.Handle recvbytebufallocator_handle;
                    ByteBuf bytebuf;
                    boolean flag1;

                    while (true) {
                        if (!iterator.hasNext()) {
                            return i;
                        }

                        SelectionKey selectionkey = (SelectionKey) iterator.next();

                        recvbytebufallocator_handle = this.allocHandle;
                        if (recvbytebufallocator_handle == null) {
                            this.allocHandle = recvbytebufallocator_handle = this.config().getRecvByteBufAllocator().newHandle();
                        }

                        bytebuf = recvbytebufallocator_handle.allocate(this.config().getAllocator());
                        flag1 = true;
                        boolean flag2 = false;

                        int l;
                        label231:
                        {
                            try {
                                flag2 = true;
                                ByteBuffer bytebuffer = bytebuf.nioBuffer(bytebuf.writerIndex(), bytebuf.writableBytes());
                                MessageInfo messageinfo = this.ch.receive(bytebuffer, (Object) null, this.notificationHandler);

                                if (messageinfo == null) {
                                    k = i;
                                    flag2 = false;
                                    break;
                                }

                                bytebuffer.flip();
                                list.add(new SctpMessage(messageinfo, bytebuf.writerIndex(bytebuf.writerIndex() + bytebuffer.remaining())));
                                flag1 = false;
                                ++i;
                                flag2 = false;
                                break label231;
                            } catch (Throwable throwable) {
                                PlatformDependent.throwException(throwable);
                                flag2 = false;
                            } finally {
                                if (flag2) {
                                    int i1 = bytebuf.readableBytes();

                                    recvbytebufallocator_handle.record(i1);
                                    if (flag1) {
                                        bytebuf.release();
                                    }

                                }
                            }

                            l = bytebuf.readableBytes();
                            recvbytebufallocator_handle.record(l);
                            if (flag1) {
                                bytebuf.release();
                            }
                            continue;
                        }

                        l = bytebuf.readableBytes();
                        recvbytebufallocator_handle.record(l);
                        if (flag1) {
                            bytebuf.release();
                        }
                    }

                    int j1 = bytebuf.readableBytes();

                    recvbytebufallocator_handle.record(j1);
                    if (flag1) {
                        bytebuf.release();
                    }
                } finally {
                    set.clear();
                }

                return k;
            }
        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        if (this.writeSelector.isOpen()) {
            int i = channeloutboundbuffer.size();
            int j = this.writeSelector.select(1000L);

            if (j > 0) {
                Set set = this.writeSelector.selectedKeys();

                if (!set.isEmpty()) {
                    Iterator iterator = set.iterator();
                    int k = 0;

                    while (k != i) {
                        iterator.next();
                        iterator.remove();
                        SctpMessage sctpmessage = (SctpMessage) channeloutboundbuffer.current();

                        if (sctpmessage == null) {
                            return;
                        }

                        ByteBuf bytebuf = sctpmessage.content();
                        int l = bytebuf.readableBytes();
                        ByteBuffer bytebuffer;

                        if (bytebuf.nioBufferCount() != -1) {
                            bytebuffer = bytebuf.nioBuffer();
                        } else {
                            bytebuffer = ByteBuffer.allocate(l);
                            bytebuf.getBytes(bytebuf.readerIndex(), bytebuffer);
                            bytebuffer.flip();
                        }

                        MessageInfo messageinfo = MessageInfo.createOutgoing(this.association(), (SocketAddress) null, sctpmessage.streamIdentifier());

                        messageinfo.payloadProtocolID(sctpmessage.protocolIdentifier());
                        messageinfo.streamNumber(sctpmessage.streamIdentifier());
                        this.ch.send(bytebuffer, messageinfo);
                        ++k;
                        channeloutboundbuffer.remove();
                        if (!iterator.hasNext()) {
                            return;
                        }
                    }

                }
            }
        }
    }

    @Override
    public Association association() {
        try {
            return this.ch.association();
        } catch (IOException ioexception) {
            return null;
        }
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && this.association() != null;
    }

    @Override
    protected SocketAddress localAddress0() {
        try {
            Iterator iterator = this.ch.getAllLocalAddresses().iterator();

            if (iterator.hasNext()) {
                return (SocketAddress) iterator.next();
            }
        } catch (IOException ioexception) {
            ;
        }

        return null;
    }

    @Override
    public Set<InetSocketAddress> allLocalAddresses() {
        try {
            Set set = this.ch.getAllLocalAddresses();
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
    protected SocketAddress remoteAddress0() {
        try {
            Iterator iterator = this.ch.getRemoteAddresses().iterator();

            if (iterator.hasNext()) {
                return (SocketAddress) iterator.next();
            }
        } catch (IOException ioexception) {
            ;
        }

        return null;
    }

    @Override
    public Set<InetSocketAddress> allRemoteAddresses() {
        try {
            Set set = this.ch.getRemoteAddresses();
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
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.ch.bind(socketaddress);
    }

    @Override
    protected void doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        if (socketaddress1 != null) {
            this.ch.bind(socketaddress1);
        }

        boolean flag = false;

        try {
            this.ch.connect(socketaddress);
            boolean flag1 = false;

            while (!flag1) {
                if (this.connectSelector.select(1000L) >= 0) {
                    Set set = this.connectSelector.selectedKeys();
                    Iterator iterator = set.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey selectionkey = (SelectionKey) iterator.next();

                        if (selectionkey.isConnectable()) {
                            set.clear();
                            flag1 = true;
                            break;
                        }
                    }

                    set.clear();
                }
            }

            flag = this.ch.finishConnect();
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
        closeSelector("read", this.readSelector);
        closeSelector("write", this.writeSelector);
        closeSelector("connect", this.connectSelector);
        this.ch.close();
    }

    private static void closeSelector(String s, Selector selector) {
        try {
            selector.close();
        } catch (IOException ioexception) {
            OioSctpChannel.logger.warn("Failed to close a " + s + " selector.", (Throwable) ioexception);
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
                this.ch.bindAddress(inetaddress);
                channelpromise.setSuccess();
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
            }
        } else {
            this.eventLoop().execute(new Runnable() {
                public void run() {
                    OioSctpChannel.this.bindAddress(inetaddress, channelpromise);
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
                this.ch.unbindAddress(inetaddress);
                channelpromise.setSuccess();
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
            }
        } else {
            this.eventLoop().execute(new Runnable() {
                public void run() {
                    OioSctpChannel.this.unbindAddress(inetaddress, channelpromise);
                }
            });
        }

        return channelpromise;
    }
}
