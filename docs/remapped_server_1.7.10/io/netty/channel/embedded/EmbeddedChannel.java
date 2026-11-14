package io.netty.channel.embedded;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class EmbeddedChannel extends AbstractChannel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EmbeddedChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final EmbeddedEventLoop loop = new EmbeddedEventLoop();
    private final ChannelConfig config = new DefaultChannelConfig(this);
    private final SocketAddress localAddress = new EmbeddedSocketAddress();
    private final SocketAddress remoteAddress = new EmbeddedSocketAddress();
    private final Queue<Object> inboundMessages = new ArrayDeque();
    private final Queue<Object> outboundMessages = new ArrayDeque();
    private Throwable lastException;
    private int state;

    public EmbeddedChannel(ChannelHandler... achannelhandler) {
        super((Channel) null);
        if (achannelhandler == null) {
            throw new NullPointerException("handlers");
        } else {
            int i = 0;
            ChannelPipeline channelpipeline = this.pipeline();
            ChannelHandler[] achannelhandler1 = achannelhandler;
            int j = achannelhandler.length;

            for (int k = 0; k < j; ++k) {
                ChannelHandler channelhandler = achannelhandler1[k];

                if (channelhandler == null) {
                    break;
                }

                ++i;
                channelpipeline.addLast(channelhandler);
            }

            if (i == 0) {
                throw new IllegalArgumentException("handlers is empty.");
            } else {
                channelpipeline.addLast(new EmbeddedChannel.LastInboundHandler());
                this.loop.register(this);
            }
        }
    }

    @Override
    public ChannelMetadata metadata() {
        return EmbeddedChannel.METADATA;
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return this.state < 2;
    }

    @Override
    public boolean isActive() {
        return this.state == 1;
    }

    public Queue<Object> inboundMessages() {
        return this.inboundMessages;
    }

    /** @deprecated */
    @Deprecated
    public Queue<Object> lastInboundBuffer() {
        return this.inboundMessages();
    }

    public Queue<Object> outboundMessages() {
        return this.outboundMessages;
    }

    /** @deprecated */
    @Deprecated
    public Queue<Object> lastOutboundBuffer() {
        return this.outboundMessages();
    }

    public Object readInbound() {
        return this.inboundMessages.poll();
    }

    public Object readOutbound() {
        return this.outboundMessages.poll();
    }

    public boolean writeInbound(Object... aobject) {
        this.ensureOpen();
        if (aobject.length == 0) {
            return !this.inboundMessages.isEmpty();
        } else {
            ChannelPipeline channelpipeline = this.pipeline();
            Object[] aobject1 = aobject;
            int i = aobject.length;

            for (int j = 0; j < i; ++j) {
                Object object = aobject1[j];

                channelpipeline.fireChannelRead(object);
            }

            channelpipeline.fireChannelReadComplete();
            this.runPendingTasks();
            this.checkException();
            return !this.inboundMessages.isEmpty();
        }
    }

    public boolean writeOutbound(Object... aobject) {
        this.ensureOpen();
        if (aobject.length == 0) {
            return !this.outboundMessages.isEmpty();
        } else {
            RecyclableArrayList recyclablearraylist = RecyclableArrayList.newInstance(aobject.length);

            boolean flag;

            try {
                Object[] aobject1 = aobject;
                int i = aobject.length;

                for (int j = 0; j < i; ++j) {
                    Object object = aobject1[j];

                    if (object == null) {
                        break;
                    }

                    recyclablearraylist.add(this.write(object));
                }

                this.flush();
                int k = recyclablearraylist.size();

                for (i = 0; i < k; ++i) {
                    ChannelFuture channelfuture = (ChannelFuture) recyclablearraylist.get(i);

                    assert channelfuture.isDone();

                    if (channelfuture.cause() != null) {
                        this.recordException(channelfuture.cause());
                    }
                }

                this.runPendingTasks();
                this.checkException();
                flag = !this.outboundMessages.isEmpty();
            } finally {
                recyclablearraylist.recycle();
            }

            return flag;
        }
    }

    public boolean finish() {
        this.close();
        this.runPendingTasks();
        this.checkException();
        return !this.inboundMessages.isEmpty() || !this.outboundMessages.isEmpty();
    }

    public void runPendingTasks() {
        try {
            this.loop.runTasks();
        } catch (Exception exception) {
            this.recordException(exception);
        }

    }

    private void recordException(Throwable throwable) {
        if (this.lastException == null) {
            this.lastException = throwable;
        } else {
            EmbeddedChannel.logger.warn("More than one exception was raised. Will report only the first one and log others.", throwable);
        }

    }

    public void checkException() {
        Throwable throwable = this.lastException;

        if (throwable != null) {
            this.lastException = null;
            PlatformDependent.throwException(throwable);
        }
    }

    protected final void ensureOpen() {
        if (!this.isOpen()) {
            this.recordException(new ClosedChannelException());
            this.checkException();
        }

    }

    @Override
    protected boolean isCompatible(EventLoop eventloop) {
        return eventloop instanceof EmbeddedEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.isActive() ? this.localAddress : null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.isActive() ? this.remoteAddress : null;
    }

    @Override
    protected void doRegister() throws Exception {
        this.state = 1;
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {}

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.state = 2;
    }

    @Override
    protected void doBeginRead() throws Exception {}

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new EmbeddedChannel.DefaultUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        while (true) {
            Object object = channeloutboundbuffer.current();

            if (object == null) {
                return;
            }

            ReferenceCountUtil.retain(object);
            this.outboundMessages.add(object);
            channeloutboundbuffer.remove();
        }
    }

    private final class LastInboundHandler extends ChannelInboundHandlerAdapter {

        private LastInboundHandler() {}

        @Override
        public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
            EmbeddedChannel.this.inboundMessages.add(object);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
            EmbeddedChannel.this.recordException(throwable);
        }
    }

    private class DefaultUnsafe extends AbstractChannel.AbstractUnsafe {

        private DefaultUnsafe() {
            super();
        }

        @Override
        public void connect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) {
            channelpromise.setSuccess();
        }
    }
}
