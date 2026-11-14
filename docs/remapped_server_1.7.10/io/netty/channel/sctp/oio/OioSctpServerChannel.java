package io.netty.channel.sctp.oio;

import com.sun.nio.sctp.SctpChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.sctp.DefaultSctpServerChannelConfig;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.channel.sctp.SctpServerChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OioSctpServerChannel extends AbstractOioMessageChannel implements SctpServerChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSctpServerChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final com.sun.nio.sctp.SctpServerChannel sch;
    private final SctpServerChannelConfig config;
    private final Selector selector;

    private static com.sun.nio.sctp.SctpServerChannel newServerSocket() {
        try {
            return com.sun.nio.sctp.SctpServerChannel.open();
        } catch (IOException ioexception) {
            throw new ChannelException("failed to create a sctp server channel", ioexception);
        }
    }

    public OioSctpServerChannel() {
        this(newServerSocket());
    }

    public OioSctpServerChannel(com.sun.nio.sctp.SctpServerChannel com_sun_nio_sctp_sctpserverchannel) {
        super((Channel) null);
        if (com_sun_nio_sctp_sctpserverchannel == null) {
            throw new NullPointerException("sctp server channel");
        } else {
            this.sch = com_sun_nio_sctp_sctpserverchannel;
            boolean flag = false;

            try {
                com_sun_nio_sctp_sctpserverchannel.configureBlocking(false);
                this.selector = Selector.open();
                com_sun_nio_sctp_sctpserverchannel.register(this.selector, 16);
                this.config = new DefaultSctpServerChannelConfig(this, com_sun_nio_sctp_sctpserverchannel);
                flag = true;
            } catch (Exception exception) {
                throw new ChannelException("failed to initialize a sctp server channel", exception);
            } finally {
                if (!flag) {
                    try {
                        com_sun_nio_sctp_sctpserverchannel.close();
                    } catch (IOException ioexception) {
                        OioSctpServerChannel.logger.warn("Failed to close a sctp server channel.", (Throwable) ioexception);
                    }
                }

            }

        }
    }

    @Override
    public ChannelMetadata metadata() {
        return OioSctpServerChannel.METADATA;
    }

    @Override
    public SctpServerChannelConfig config() {
        return this.config;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public boolean isOpen() {
        return this.sch.isOpen();
    }

    @Override
    protected SocketAddress localAddress0() {
        try {
            Iterator iterator = this.sch.getAllLocalAddresses().iterator();

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
            Set set = this.sch.getAllLocalAddresses();
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
    public boolean isActive() {
        return this.isOpen() && this.localAddress0() != null;
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.sch.bind(socketaddress, this.config.getBacklog());
    }

    @Override
    protected void doClose() throws Exception {
        try {
            this.selector.close();
        } catch (IOException ioexception) {
            OioSctpServerChannel.logger.warn("Failed to close a selector.", (Throwable) ioexception);
        }

        this.sch.close();
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        if (!this.isActive()) {
            return -1;
        } else {
            SctpChannel sctpchannel = null;
            int i = 0;

            try {
                int j = this.selector.select(1000L);

                if (j > 0) {
                    Iterator iterator = this.selector.selectedKeys().iterator();

                    do {
                        SelectionKey selectionkey = (SelectionKey) iterator.next();

                        iterator.remove();
                        if (selectionkey.isAcceptable()) {
                            sctpchannel = this.sch.accept();
                            if (sctpchannel != null) {
                                list.add(new OioSctpChannel(this, sctpchannel));
                                ++i;
                            }
                        }
                    } while (iterator.hasNext());

                    return i;
                }
            } catch (Throwable throwable) {
                OioSctpServerChannel.logger.warn("Failed to create a new channel from an accepted sctp channel.", throwable);
                if (sctpchannel != null) {
                    try {
                        sctpchannel.close();
                    } catch (Throwable throwable1) {
                        OioSctpServerChannel.logger.warn("Failed to close a sctp channel.", throwable1);
                    }
                }
            }

            return i;
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
                this.sch.bindAddress(inetaddress);
                channelpromise.setSuccess();
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
            }
        } else {
            this.eventLoop().execute(new Runnable() {
                public void run() {
                    OioSctpServerChannel.this.bindAddress(inetaddress, channelpromise);
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
                this.sch.unbindAddress(inetaddress);
                channelpromise.setSuccess();
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
            }
        } else {
            this.eventLoop().execute(new Runnable() {
                public void run() {
                    OioSctpServerChannel.this.unbindAddress(inetaddress, channelpromise);
                }
            });
        }

        return channelpromise;
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

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        throw new UnsupportedOperationException();
    }
}
