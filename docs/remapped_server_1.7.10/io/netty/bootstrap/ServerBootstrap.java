package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public final class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, ServerChannel> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);
    private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap();
    private final Map<AttributeKey<?>, Object> childAttrs = new LinkedHashMap();
    private volatile EventLoopGroup childGroup;
    private volatile ChannelHandler childHandler;

    public ServerBootstrap() {}

    private ServerBootstrap(ServerBootstrap serverbootstrap) {
        super(serverbootstrap);
        this.childGroup = serverbootstrap.childGroup;
        this.childHandler = serverbootstrap.childHandler;
        Map map = serverbootstrap.childOptions;

        synchronized (serverbootstrap.childOptions) {
            this.childOptions.putAll(serverbootstrap.childOptions);
        }

        map = serverbootstrap.childAttrs;
        synchronized (serverbootstrap.childAttrs) {
            this.childAttrs.putAll(serverbootstrap.childAttrs);
        }
    }

    @Override
    public ServerBootstrap group(EventLoopGroup eventloopgroup) {
        return this.group(eventloopgroup, eventloopgroup);
    }

    public ServerBootstrap group(EventLoopGroup eventloopgroup, EventLoopGroup eventloopgroup1) {
        super.group(eventloopgroup);
        if (eventloopgroup1 == null) {
            throw new NullPointerException("childGroup");
        } else if (this.childGroup != null) {
            throw new IllegalStateException("childGroup set already");
        } else {
            this.childGroup = eventloopgroup1;
            return this;
        }
    }

    public <T> ServerBootstrap childOption(ChannelOption<T> channeloption, T t0) {
        if (channeloption == null) {
            throw new NullPointerException("childOption");
        } else {
            Map map;

            if (t0 == null) {
                map = this.childOptions;
                synchronized (this.childOptions) {
                    this.childOptions.remove(channeloption);
                }
            } else {
                map = this.childOptions;
                synchronized (this.childOptions) {
                    this.childOptions.put(channeloption, t0);
                }
            }

            return this;
        }
    }

    public <T> ServerBootstrap childAttr(AttributeKey<T> attributekey, T t0) {
        if (attributekey == null) {
            throw new NullPointerException("childKey");
        } else {
            if (t0 == null) {
                this.childAttrs.remove(attributekey);
            } else {
                this.childAttrs.put(attributekey, t0);
            }

            return this;
        }
    }

    public ServerBootstrap childHandler(ChannelHandler channelhandler) {
        if (channelhandler == null) {
            throw new NullPointerException("childHandler");
        } else {
            this.childHandler = channelhandler;
            return this;
        }
    }

    public EventLoopGroup childGroup() {
        return this.childGroup;
    }

    @Override
    void init(Channel channel) throws Exception {
        Map map = this.options();

        synchronized (map) {
            channel.config().setOptions(map);
        }

        Map map1 = this.attrs();

        synchronized (map1) {
            Iterator iterator = map1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                AttributeKey attributekey = (AttributeKey) entry.getKey();

                channel.attr(attributekey).set(entry.getValue());
            }
        }

        ChannelPipeline channelpipeline = channel.pipeline();

        if (this.handler() != null) {
            channelpipeline.addLast(this.handler());
        }

        final EventLoopGroup eventloopgroup = this.childGroup;
        final ChannelHandler channelhandler = this.childHandler;
        Map map2 = this.childOptions;
        final Entry[] aentry;

        synchronized (this.childOptions) {
            aentry = (Entry[]) this.childOptions.entrySet().toArray(newOptionArray(this.childOptions.size()));
        }

        map2 = this.childAttrs;
        final Entry[] aentry1;

        synchronized (this.childAttrs) {
            aentry1 = (Entry[]) this.childAttrs.entrySet().toArray(newAttrArray(this.childAttrs.size()));
        }

        channelpipeline.addLast(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel channel1) throws Exception {
                channel1.pipeline().addLast(new ServerBootstrap.ServerBootstrapAcceptor(eventloopgroup, channelhandler, aentry, aentry1));
            }
        });
    }

    @Override
    public ServerBootstrap validate() {
        super.validate();
        if (this.childHandler == null) {
            throw new IllegalStateException("childHandler not set");
        } else {
            if (this.childGroup == null) {
                ServerBootstrap.logger.warn("childGroup is not set. Using parentGroup instead.");
                this.childGroup = this.group();
            }

            return this;
        }
    }

    private static Entry<ChannelOption<?>, Object>[] newOptionArray(int i) {
        return new Entry[i];
    }

    private static Entry<AttributeKey<?>, Object>[] newAttrArray(int i) {
        return new Entry[i];
    }

    @Override
    public ServerBootstrap clone() {
        return new ServerBootstrap(this);
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder(super.toString());

        stringbuilder.setLength(stringbuilder.length() - 1);
        stringbuilder.append(", ");
        if (this.childGroup != null) {
            stringbuilder.append("childGroup: ");
            stringbuilder.append(this.childGroup.getClass().getSimpleName());
            stringbuilder.append(", ");
        }

        Map map = this.childOptions;

        synchronized (this.childOptions) {
            if (!this.childOptions.isEmpty()) {
                stringbuilder.append("childOptions: ");
                stringbuilder.append(this.childOptions);
                stringbuilder.append(", ");
            }
        }

        map = this.childAttrs;
        synchronized (this.childAttrs) {
            if (!this.childAttrs.isEmpty()) {
                stringbuilder.append("childAttrs: ");
                stringbuilder.append(this.childAttrs);
                stringbuilder.append(", ");
            }
        }

        if (this.childHandler != null) {
            stringbuilder.append("childHandler: ");
            stringbuilder.append(this.childHandler);
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

    private static class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {

        private final EventLoopGroup childGroup;
        private final ChannelHandler childHandler;
        private final Entry<ChannelOption<?>, Object>[] childOptions;
        private final Entry<AttributeKey<?>, Object>[] childAttrs;

        ServerBootstrapAcceptor(EventLoopGroup eventloopgroup, ChannelHandler channelhandler, Entry<ChannelOption<?>, Object>[] aentry, Entry<AttributeKey<?>, Object>[] aentry1) {
            this.childGroup = eventloopgroup;
            this.childHandler = channelhandler;
            this.childOptions = aentry;
            this.childAttrs = aentry1;
        }

        @Override
        public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) {
            Channel channel = (Channel) object;

            channel.pipeline().addLast(this.childHandler);
            Entry[] aentry = this.childOptions;
            int i = aentry.length;

            Entry entry;
            int j;

            for (j = 0; j < i; ++j) {
                entry = aentry[j];

                try {
                    if (!channel.config().setOption((ChannelOption) entry.getKey(), entry.getValue())) {
                        ServerBootstrap.logger.warn("Unknown channel option: " + entry);
                    }
                } catch (Throwable throwable) {
                    ServerBootstrap.logger.warn("Failed to set a channel option: " + channel, throwable);
                }
            }

            aentry = this.childAttrs;
            i = aentry.length;

            for (j = 0; j < i; ++j) {
                entry = aentry[j];
                channel.attr((AttributeKey) entry.getKey()).set(entry.getValue());
            }

            try {
                this.childGroup.register(channel);
            } catch (Throwable throwable1) {
                channel.unsafe().closeForcibly();
                ServerBootstrap.logger.warn("Failed to register an accepted channel: " + channel, throwable1);
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
            final ChannelConfig channelconfig = channelhandlercontext.channel().config();

            if (channelconfig.isAutoRead()) {
                channelconfig.setAutoRead(false);
                channelhandlercontext.channel().eventLoop().schedule(new Runnable() {
                    public void run() {
                        channelconfig.setAutoRead(true);
                    }
                }, 1L, TimeUnit.SECONDS);
            }

            channelhandlercontext.fireExceptionCaught(throwable);
        }
    }
}
