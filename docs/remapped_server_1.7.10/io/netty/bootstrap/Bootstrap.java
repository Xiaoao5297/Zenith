package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class Bootstrap extends AbstractBootstrap<Bootstrap, Channel> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Bootstrap.class);
    private volatile SocketAddress remoteAddress;

    public Bootstrap() {}

    private Bootstrap(Bootstrap bootstrap) {
        super(bootstrap);
        this.remoteAddress = bootstrap.remoteAddress;
    }

    public Bootstrap remoteAddress(SocketAddress socketaddress) {
        this.remoteAddress = socketaddress;
        return this;
    }

    public Bootstrap remoteAddress(String s, int i) {
        this.remoteAddress = new InetSocketAddress(s, i);
        return this;
    }

    public Bootstrap remoteAddress(InetAddress inetaddress, int i) {
        this.remoteAddress = new InetSocketAddress(inetaddress, i);
        return this;
    }

    public ChannelFuture connect() {
        this.validate();
        SocketAddress socketaddress = this.remoteAddress;

        if (socketaddress == null) {
            throw new IllegalStateException("remoteAddress not set");
        } else {
            return this.doConnect(socketaddress, this.localAddress());
        }
    }

    public ChannelFuture connect(String s, int i) {
        return this.connect(new InetSocketAddress(s, i));
    }

    public ChannelFuture connect(InetAddress inetaddress, int i) {
        return this.connect(new InetSocketAddress(inetaddress, i));
    }

    public ChannelFuture connect(SocketAddress socketaddress) {
        if (socketaddress == null) {
            throw new NullPointerException("remoteAddress");
        } else {
            this.validate();
            return this.doConnect(socketaddress, this.localAddress());
        }
    }

    public ChannelFuture connect(SocketAddress socketaddress, SocketAddress socketaddress1) {
        if (socketaddress == null) {
            throw new NullPointerException("remoteAddress");
        } else {
            this.validate();
            return this.doConnect(socketaddress, socketaddress1);
        }
    }

    private ChannelFuture doConnect(final SocketAddress socketaddress, final SocketAddress socketaddress1) {
        final ChannelFuture channelfuture = this.initAndRegister();
        final Channel channel = channelfuture.channel();

        if (channelfuture.cause() != null) {
            return channelfuture;
        } else {
            final ChannelPromise channelpromise = channel.newPromise();

            if (channelfuture.isDone()) {
                doConnect0(channelfuture, channel, socketaddress, socketaddress1, channelpromise);
            } else {
                channelfuture.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture channelfuture1) throws Exception {
                        Bootstrap.doConnect0(channelfuture, channel, socketaddress, socketaddress1, channelpromise);
                    }
                });
            }

            return channelpromise;
        }
    }

    private static void doConnect0(final ChannelFuture channelfuture, final Channel channel, final SocketAddress socketaddress, final SocketAddress socketaddress1, final ChannelPromise channelpromise) {
        channel.eventLoop().execute(new Runnable() {
            public void run() {
                if (channelfuture.isSuccess()) {
                    if (socketaddress1 == null) {
                        channel.connect(socketaddress, channelpromise);
                    } else {
                        channel.connect(socketaddress, socketaddress1, channelpromise);
                    }

                    channelpromise.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    channelpromise.setFailure(channelfuture.cause());
                }

            }
        });
    }

    @Override
    void init(Channel channel) throws Exception {
        ChannelPipeline channelpipeline = channel.pipeline();

        channelpipeline.addLast(this.handler());
        Map map = this.options();

        synchronized (map) {
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                try {
                    if (!channel.config().setOption((ChannelOption) entry.getKey(), entry.getValue())) {
                        Bootstrap.logger.warn("Unknown channel option: " + entry);
                    }
                } catch (Throwable throwable) {
                    Bootstrap.logger.warn("Failed to set a channel option: " + channel, throwable);
                }
            }
        }

        Map map1 = this.attrs();

        synchronized (map1) {
            Iterator iterator1 = map1.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();

                channel.attr((AttributeKey) entry1.getKey()).set(entry1.getValue());
            }

        }
    }

    @Override
    public Bootstrap validate() {
        super.validate();
        if (this.handler() == null) {
            throw new IllegalStateException("handler not set");
        } else {
            return this;
        }
    }

    @Override
    public Bootstrap clone() {
        return new Bootstrap(this);
    }

    @Override
    public String toString() {
        if (this.remoteAddress == null) {
            return super.toString();
        } else {
            StringBuilder stringbuilder = new StringBuilder(super.toString());

            stringbuilder.setLength(stringbuilder.length() - 1);
            stringbuilder.append(", remoteAddress: ");
            stringbuilder.append(this.remoteAddress);
            stringbuilder.append(')');
            return stringbuilder.toString();
        }
    }
}
