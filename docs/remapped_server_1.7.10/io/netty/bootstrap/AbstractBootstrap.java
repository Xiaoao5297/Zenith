package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel> implements Cloneable {

    private volatile EventLoopGroup group;
    private volatile ChannelFactory<? extends C> channelFactory;
    private volatile SocketAddress localAddress;
    private final Map<ChannelOption<?>, Object> options = new LinkedHashMap();
    private final Map<AttributeKey<?>, Object> attrs = new LinkedHashMap();
    private volatile ChannelHandler handler;

    AbstractBootstrap() {}

    AbstractBootstrap(AbstractBootstrap<B, C> abstractbootstrap) {
        this.group = abstractbootstrap.group;
        this.channelFactory = abstractbootstrap.channelFactory;
        this.handler = abstractbootstrap.handler;
        this.localAddress = abstractbootstrap.localAddress;
        Map map = abstractbootstrap.options;

        synchronized (abstractbootstrap.options) {
            this.options.putAll(abstractbootstrap.options);
        }

        map = abstractbootstrap.attrs;
        synchronized (abstractbootstrap.attrs) {
            this.attrs.putAll(abstractbootstrap.attrs);
        }
    }

    public B group(EventLoopGroup eventloopgroup) {
        if (eventloopgroup == null) {
            throw new NullPointerException("group");
        } else if (this.group != null) {
            throw new IllegalStateException("group set already");
        } else {
            this.group = eventloopgroup;
            return this;
        }
    }

    public B channel(Class<? extends C> oclass) {
        if (oclass == null) {
            throw new NullPointerException("channelClass");
        } else {
            return this.channelFactory(new AbstractBootstrap.BootstrapChannelFactory<>(oclass));
        }
    }

    public B channelFactory(ChannelFactory<? extends C> channelfactory) {
        if (channelfactory == null) {
            throw new NullPointerException("channelFactory");
        } else if (this.channelFactory != null) {
            throw new IllegalStateException("channelFactory set already");
        } else {
            this.channelFactory = channelfactory;
            return this;
        }
    }

    public B localAddress(SocketAddress socketaddress) {
        this.localAddress = socketaddress;
        return this;
    }

    public B localAddress(int i) {
        return this.localAddress(new InetSocketAddress(i));
    }

    public B localAddress(String s, int i) {
        return this.localAddress(new InetSocketAddress(s, i));
    }

    public B localAddress(InetAddress inetaddress, int i) {
        return this.localAddress(new InetSocketAddress(inetaddress, i));
    }

    public <T> B option(ChannelOption<T> channeloption, T t0) {
        if (channeloption == null) {
            throw new NullPointerException("option");
        } else {
            Map map;

            if (t0 == null) {
                map = this.options;
                synchronized (this.options) {
                    this.options.remove(channeloption);
                }
            } else {
                map = this.options;
                synchronized (this.options) {
                    this.options.put(channeloption, t0);
                }
            }

            return this;
        }
    }

    public <T> B attr(AttributeKey<T> attributekey, T t0) {
        if (attributekey == null) {
            throw new NullPointerException("key");
        } else {
            Map map;

            if (t0 == null) {
                map = this.attrs;
                synchronized (this.attrs) {
                    this.attrs.remove(attributekey);
                }
            } else {
                map = this.attrs;
                synchronized (this.attrs) {
                    this.attrs.put(attributekey, t0);
                }
            }

            return this;
        }
    }

    public B validate() {
        if (this.group == null) {
            throw new IllegalStateException("group not set");
        } else if (this.channelFactory == null) {
            throw new IllegalStateException("factory not set");
        } else {
            return this;
        }
    }

    public abstract B clone();

    public ChannelFuture register() {
        this.validate();
        return this.initAndRegister();
    }

    public ChannelFuture bind() {
        this.validate();
        SocketAddress socketaddress = this.localAddress;

        if (socketaddress == null) {
            throw new IllegalStateException("localAddress not set");
        } else {
            return this.doBind(socketaddress);
        }
    }

    public ChannelFuture bind(int i) {
        return this.bind(new InetSocketAddress(i));
    }

    public ChannelFuture bind(String s, int i) {
        return this.bind(new InetSocketAddress(s, i));
    }

    public ChannelFuture bind(InetAddress inetaddress, int i) {
        return this.bind(new InetSocketAddress(inetaddress, i));
    }

    public ChannelFuture bind(SocketAddress socketaddress) {
        this.validate();
        if (socketaddress == null) {
            throw new NullPointerException("localAddress");
        } else {
            return this.doBind(socketaddress);
        }
    }

    private ChannelFuture doBind(final SocketAddress socketaddress) {
        ChannelFuture channelfuture = this.initAndRegister();
        final Channel channel = channelfuture.channel();
        final ChannelPromise channelpromise = channel.newPromise();

        if (channelfuture.isDone()) {
            doBind0(channelfuture, channel, socketaddress, channelpromise);
        } else {
            channelfuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelfuture1) throws Exception {
                    AbstractBootstrap.doBind0(channelfuture1, channel, socketaddress, channelpromise);
                }
            });
        }

        return channelpromise;
    }

    final ChannelFuture initAndRegister() {
        Channel channel = this.channelFactory().newChannel();

        try {
            this.init(channel);
        } catch (Throwable throwable) {
            channel.unsafe().closeForcibly();
            return channel.newFailedFuture(throwable);
        }

        ChannelPromise channelpromise = channel.newPromise();

        this.group().register(channel, channelpromise);
        if (channelpromise.cause() != null) {
            if (channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }

        return channelpromise;
    }

    abstract void init(Channel channel) throws Exception;

    private static void doBind0(final ChannelFuture channelfuture, final Channel channel, final SocketAddress socketaddress, final ChannelPromise channelpromise) {
        channel.eventLoop().execute(new Runnable() {
            public void run() {
                if (channelfuture.isSuccess()) {
                    channel.bind(socketaddress, channelpromise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    channelpromise.setFailure(channelfuture.cause());
                }

            }
        });
    }

    public B handler(ChannelHandler channelhandler) {
        if (channelhandler == null) {
            throw new NullPointerException("handler");
        } else {
            this.handler = channelhandler;
            return this;
        }
    }

    final SocketAddress localAddress() {
        return this.localAddress;
    }

    final ChannelFactory<? extends C> channelFactory() {
        return this.channelFactory;
    }

    final ChannelHandler handler() {
        return this.handler;
    }

    public final EventLoopGroup group() {
        return this.group;
    }

    final Map<ChannelOption<?>, Object> options() {
        return this.options;
    }

    final Map<AttributeKey<?>, Object> attrs() {
        return this.attrs;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append('(');
        if (this.group != null) {
            stringbuilder.append("group: ");
            stringbuilder.append(this.group.getClass().getSimpleName());
            stringbuilder.append(", ");
        }

        if (this.channelFactory != null) {
            stringbuilder.append("channelFactory: ");
            stringbuilder.append(this.channelFactory);
            stringbuilder.append(", ");
        }

        if (this.localAddress != null) {
            stringbuilder.append("localAddress: ");
            stringbuilder.append(this.localAddress);
            stringbuilder.append(", ");
        }

        Map map = this.options;

        synchronized (this.options) {
            if (!this.options.isEmpty()) {
                stringbuilder.append("options: ");
                stringbuilder.append(this.options);
                stringbuilder.append(", ");
            }
        }

        map = this.attrs;
        synchronized (this.attrs) {
            if (!this.attrs.isEmpty()) {
                stringbuilder.append("attrs: ");
                stringbuilder.append(this.attrs);
                stringbuilder.append(", ");
            }
        }

        if (this.handler != null) {
            stringbuilder.append("handler: ");
            stringbuilder.append(this.handler);
            stringbuilder.append(", ");
        }

        if (stringbuilder.charAt(stringbuilder.length() - 1) == '(') {
            stringbuilder.append(')');
        } else {
            stringbuilder.setCharAt(stringbuilder.length() - 2, ')');
            stringbuilder.setLength(stringbuilder.length() - 1);
        }

        return stringbuilder.toString();
    }

    private static final class BootstrapChannelFactory<T extends Channel> implements ChannelFactory<T> {

        private final Class<? extends T> clazz;

        BootstrapChannelFactory(Class<? extends T> oclass) {
            this.clazz = oclass;
        }

        @Override
        public T newChannel() {
            try {
                return (Channel) this.clazz.newInstance();
            } catch (Throwable throwable) {
                throw new ChannelException("Unable to create Channel from class " + this.clazz, throwable);
            }
        }

        public String toString() {
            return this.clazz.getSimpleName() + ".class";
        }
    }
}
