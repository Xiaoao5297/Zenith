package io.netty.channel.sctp.nio;

import com.sun.nio.sctp.SctpChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.sctp.DefaultSctpServerChannelConfig;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.channel.sctp.SctpServerChannelConfig;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NioSctpServerChannel extends AbstractNioMessageChannel implements SctpServerChannel {

    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final SctpServerChannelConfig config = new DefaultSctpServerChannelConfig(this, this.javaChannel());

    private static com.sun.nio.sctp.SctpServerChannel newSocket() {
        try {
            return com.sun.nio.sctp.SctpServerChannel.open();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open a server socket.", ioexception);
        }
    }

    public NioSctpServerChannel() {
        super((Channel) null, newSocket(), 16);
    }

    @Override
    public ChannelMetadata metadata() {
        return NioSctpServerChannel.METADATA;
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
    public SctpServerChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && !this.allLocalAddresses().isEmpty();
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
    protected com.sun.nio.sctp.SctpServerChannel javaChannel() {
        return (com.sun.nio.sctp.SctpServerChannel) super.javaChannel();
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
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.javaChannel().bind(socketaddress, this.config.getBacklog());
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    @Override
    protected int doReadMessages(List<Object> list) throws Exception {
        SctpChannel sctpchannel = this.javaChannel().accept();

        if (sctpchannel == null) {
            return 0;
        } else {
            list.add(new NioSctpChannel(this, sctpchannel));
            return 1;
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
                    NioSctpServerChannel.this.bindAddress(inetaddress, channelpromise);
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
                    NioSctpServerChannel.this.unbindAddress(inetaddress, channelpromise);
                }
            });
        }

        return channelpromise;
    }

    @Override
    protected boolean doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doFinishConnect() throws Exception {
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
    protected boolean doWriteMessage(Object object, ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        throw new UnsupportedOperationException();
    }
}
