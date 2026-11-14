package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;

public abstract class AbstractChannel extends DefaultAttributeMap implements Channel {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannel.class);
    static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = new ClosedChannelException();
    static final NotYetConnectedException NOT_YET_CONNECTED_EXCEPTION = new NotYetConnectedException();
    private MessageSizeEstimator.Handle estimatorHandle;
    private final Channel parent;
    private final long hashCode = ThreadLocalRandom.current().nextLong();
    private final Channel.Unsafe unsafe;
    private final DefaultChannelPipeline pipeline;
    private final ChannelFuture succeededFuture = new SucceededChannelFuture(this, (EventExecutor) null);
    private final VoidChannelPromise voidPromise = new VoidChannelPromise(this, true);
    private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise(this, false);
    private final AbstractChannel.CloseFuture closeFuture = new AbstractChannel.CloseFuture(this);
    private volatile SocketAddress localAddress;
    private volatile SocketAddress remoteAddress;
    private volatile EventLoop eventLoop;
    private volatile boolean registered;
    private boolean strValActive;
    private String strVal;

    protected AbstractChannel(Channel channel) {
        this.parent = channel;
        this.unsafe = this.newUnsafe();
        this.pipeline = new DefaultChannelPipeline(this);
    }

    @Override
    public boolean isWritable() {
        ChannelOutboundBuffer channeloutboundbuffer = this.unsafe.outboundBuffer();

        return channeloutboundbuffer != null && channeloutboundbuffer.getWritable();
    }

    @Override
    public Channel parent() {
        return this.parent;
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.config().getAllocator();
    }

    @Override
    public EventLoop eventLoop() {
        EventLoop eventloop = this.eventLoop;

        if (eventloop == null) {
            throw new IllegalStateException("channel not registered to an event loop");
        } else {
            return eventloop;
        }
    }

    @Override
    public SocketAddress localAddress() {
        SocketAddress socketaddress = this.localAddress;

        if (socketaddress == null) {
            try {
                this.localAddress = socketaddress = this.unsafe().localAddress();
            } catch (Throwable throwable) {
                return null;
            }
        }

        return socketaddress;
    }

    protected void invalidateLocalAddress() {
        this.localAddress = null;
    }

    @Override
    public SocketAddress remoteAddress() {
        SocketAddress socketaddress = this.remoteAddress;

        if (socketaddress == null) {
            try {
                this.remoteAddress = socketaddress = this.unsafe().remoteAddress();
            } catch (Throwable throwable) {
                return null;
            }
        }

        return socketaddress;
    }

    protected void invalidateRemoteAddress() {
        this.remoteAddress = null;
    }

    @Override
    public boolean isRegistered() {
        return this.registered;
    }

    @Override
    public ChannelFuture bind(SocketAddress socketaddress) {
        return this.pipeline.bind(socketaddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress) {
        return this.pipeline.connect(socketaddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress, SocketAddress socketaddress1) {
        return this.pipeline.connect(socketaddress, socketaddress1);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.pipeline.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.pipeline.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.pipeline.deregister();
    }

    @Override
    public Channel flush() {
        this.pipeline.flush();
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress socketaddress, ChannelPromise channelpromise) {
        return this.pipeline.bind(socketaddress, channelpromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress, ChannelPromise channelpromise) {
        return this.pipeline.connect(socketaddress, channelpromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) {
        return this.pipeline.connect(socketaddress, socketaddress1, channelpromise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise channelpromise) {
        return this.pipeline.disconnect(channelpromise);
    }

    @Override
    public ChannelFuture close(ChannelPromise channelpromise) {
        return this.pipeline.close(channelpromise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise channelpromise) {
        return this.pipeline.deregister(channelpromise);
    }

    @Override
    public Channel read() {
        this.pipeline.read();
        return this;
    }

    @Override
    public ChannelFuture write(Object object) {
        return this.pipeline.write(object);
    }

    @Override
    public ChannelFuture write(Object object, ChannelPromise channelpromise) {
        return this.pipeline.write(object, channelpromise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object object) {
        return this.pipeline.writeAndFlush(object);
    }

    @Override
    public ChannelFuture writeAndFlush(Object object, ChannelPromise channelpromise) {
        return this.pipeline.writeAndFlush(object, channelpromise);
    }

    @Override
    public ChannelPromise newPromise() {
        return new DefaultChannelPromise(this);
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise(this);
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return this.succeededFuture;
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable throwable) {
        return new FailedChannelFuture(this, (EventExecutor) null, throwable);
    }

    @Override
    public ChannelFuture closeFuture() {
        return this.closeFuture;
    }

    @Override
    public Channel.Unsafe unsafe() {
        return this.unsafe;
    }

    protected abstract AbstractChannel.AbstractUnsafe newUnsafe();

    public final int hashCode() {
        return (int) this.hashCode;
    }

    public final boolean equals(Object object) {
        return this == object;
    }

    public final int compareTo(Channel channel) {
        if (this == channel) {
            return 0;
        } else {
            long i = this.hashCode - (long) channel.hashCode();

            if (i > 0L) {
                return 1;
            } else if (i < 0L) {
                return -1;
            } else {
                i = (long) (System.identityHashCode(this) - System.identityHashCode(channel));
                if (i != 0L) {
                    return (int) i;
                } else {
                    throw new Error();
                }
            }
        }
    }

    public String toString() {
        boolean flag = this.isActive();

        if (this.strValActive == flag && this.strVal != null) {
            return this.strVal;
        } else {
            SocketAddress socketaddress = this.remoteAddress();
            SocketAddress socketaddress1 = this.localAddress();

            if (socketaddress != null) {
                SocketAddress socketaddress2;
                SocketAddress socketaddress3;

                if (this.parent == null) {
                    socketaddress2 = socketaddress1;
                    socketaddress3 = socketaddress;
                } else {
                    socketaddress2 = socketaddress;
                    socketaddress3 = socketaddress1;
                }

                this.strVal = String.format("[id: 0x%08x, %s %s %s]", (int) this.hashCode, socketaddress2, flag ? "=>" : ":>", socketaddress3);
            } else if (socketaddress1 != null) {
                this.strVal = String.format("[id: 0x%08x, %s]", (int) this.hashCode, socketaddress1);
            } else {
                this.strVal = String.format("[id: 0x%08x]", (int) this.hashCode);
            }

            this.strValActive = flag;
            return this.strVal;
        }
    }

    @Override
    public final ChannelPromise voidPromise() {
        return this.voidPromise;
    }

    final MessageSizeEstimator.Handle estimatorHandle() {
        if (this.estimatorHandle == null) {
            this.estimatorHandle = this.config().getMessageSizeEstimator().newHandle();
        }

        return this.estimatorHandle;
    }

    protected abstract boolean isCompatible(EventLoop eventloop);

    protected abstract SocketAddress localAddress0();

    protected abstract SocketAddress remoteAddress0();

    protected void doRegister() throws Exception {}

    protected abstract void doBind(SocketAddress socketaddress) throws Exception;

    protected abstract void doDisconnect() throws Exception;

    protected abstract void doClose() throws Exception;

    protected void doDeregister() throws Exception {}

    protected abstract void doBeginRead() throws Exception;

    protected abstract void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception;

    protected static void checkEOF(FileRegion fileregion) throws IOException {
        if (fileregion.transfered() < fileregion.count()) {
            throw new EOFException("Expected to be able to write " + fileregion.count() + " bytes, but only wrote " + fileregion.transfered());
        }
    }

    static {
        AbstractChannel.CLOSED_CHANNEL_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        AbstractChannel.NOT_YET_CONNECTED_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    }

    static final class CloseFuture extends DefaultChannelPromise {

        CloseFuture(AbstractChannel abstractchannel) {
            super((Channel) abstractchannel);
        }

        @Override
        public ChannelPromise setSuccess() {
            throw new IllegalStateException();
        }

        @Override
        public ChannelPromise setFailure(Throwable throwable) {
            throw new IllegalStateException();
        }

        @Override
        public boolean trySuccess() {
            throw new IllegalStateException();
        }

        @Override
        public boolean tryFailure(Throwable throwable) {
            throw new IllegalStateException();
        }

        boolean setClosed() {
            return super.trySuccess();
        }
    }

    protected abstract class AbstractUnsafe implements Channel.Unsafe {

        private ChannelOutboundBuffer outboundBuffer = ChannelOutboundBuffer.newInstance(AbstractChannel.this);
        private boolean inFlush0;

        protected AbstractUnsafe() {}

        @Override
        public final ChannelOutboundBuffer outboundBuffer() {
            return this.outboundBuffer;
        }

        @Override
        public final SocketAddress localAddress() {
            return AbstractChannel.this.localAddress0();
        }

        @Override
        public final SocketAddress remoteAddress() {
            return AbstractChannel.this.remoteAddress0();
        }

        @Override
        public final void register(EventLoop eventloop, final ChannelPromise channelpromise) {
            if (eventloop == null) {
                throw new NullPointerException("eventLoop");
            } else if (AbstractChannel.this.isRegistered()) {
                channelpromise.setFailure(new IllegalStateException("registered to an event loop already"));
            } else if (!AbstractChannel.this.isCompatible(eventloop)) {
                channelpromise.setFailure(new IllegalStateException("incompatible event loop type: " + eventloop.getClass().getName()));
            } else {
                AbstractChannel.this.eventLoop = eventloop;
                if (eventloop.inEventLoop()) {
                    this.register0(channelpromise);
                } else {
                    try {
                        eventloop.execute(new Runnable() {
                            public void run() {
                                AbstractUnsafe.this.register0(channelpromise);
                            }
                        });
                    } catch (Throwable throwable) {
                        AbstractChannel.logger.warn("Force-closing a channel whose registration task was not accepted by an event loop: {}", AbstractChannel.this, throwable);
                        this.closeForcibly();
                        AbstractChannel.this.closeFuture.setClosed();
                        channelpromise.setFailure(throwable);
                    }
                }

            }
        }

        private void register0(ChannelPromise channelpromise) {
            try {
                if (!this.ensureOpen(channelpromise)) {
                    return;
                }

                AbstractChannel.this.doRegister();
                AbstractChannel.this.registered = true;
                channelpromise.setSuccess();
                AbstractChannel.this.pipeline.fireChannelRegistered();
                if (AbstractChannel.this.isActive()) {
                    AbstractChannel.this.pipeline.fireChannelActive();
                }
            } catch (Throwable throwable) {
                this.closeForcibly();
                AbstractChannel.this.closeFuture.setClosed();
                if (!channelpromise.tryFailure(throwable)) {
                    AbstractChannel.logger.warn("Tried to fail the registration promise, but it is complete already. Swallowing the cause of the registration failure:", throwable);
                }
            }

        }

        @Override
        public final void bind(SocketAddress socketaddress, ChannelPromise channelpromise) {
            if (this.ensureOpen(channelpromise)) {
                if (!PlatformDependent.isWindows() && !PlatformDependent.isRoot() && Boolean.TRUE.equals(AbstractChannel.this.config().getOption(ChannelOption.SO_BROADCAST)) && socketaddress instanceof InetSocketAddress && !((InetSocketAddress) socketaddress).getAddress().isAnyLocalAddress()) {
                    AbstractChannel.logger.warn("A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; binding to a non-wildcard address (" + socketaddress + ") anyway as requested.");
                }

                boolean flag = AbstractChannel.this.isActive();

                try {
                    AbstractChannel.this.doBind(socketaddress);
                } catch (Throwable throwable) {
                    this.closeIfClosed();
                    channelpromise.setFailure(throwable);
                    return;
                }

                if (!flag && AbstractChannel.this.isActive()) {
                    this.invokeLater(new Runnable() {
                        public void run() {
                            AbstractChannel.this.pipeline.fireChannelActive();
                        }
                    });
                }

                channelpromise.setSuccess();
            }
        }

        @Override
        public final void disconnect(ChannelPromise channelpromise) {
            boolean flag = AbstractChannel.this.isActive();

            try {
                AbstractChannel.this.doDisconnect();
            } catch (Throwable throwable) {
                this.closeIfClosed();
                channelpromise.setFailure(throwable);
                return;
            }

            if (flag && !AbstractChannel.this.isActive()) {
                this.invokeLater(new Runnable() {
                    public void run() {
                        AbstractChannel.this.pipeline.fireChannelInactive();
                    }
                });
            }

            this.closeIfClosed();
            channelpromise.setSuccess();
        }

        @Override
        public final void close(final ChannelPromise channelpromise) {
            if (this.inFlush0) {
                this.invokeLater(new Runnable() {
                    public void run() {
                        AbstractUnsafe.this.close(channelpromise);
                    }
                });
            } else if (AbstractChannel.this.closeFuture.isDone()) {
                channelpromise.setSuccess();
            } else {
                boolean flag = AbstractChannel.this.isActive();
                ChannelOutboundBuffer channeloutboundbuffer = this.outboundBuffer;

                this.outboundBuffer = null;

                try {
                    AbstractChannel.this.doClose();
                    AbstractChannel.this.closeFuture.setClosed();
                    channelpromise.setSuccess();
                } catch (Throwable throwable) {
                    AbstractChannel.this.closeFuture.setClosed();
                    channelpromise.setFailure(throwable);
                }

                try {
                    channeloutboundbuffer.failFlushed(AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
                    channeloutboundbuffer.close(AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
                } finally {
                    if (flag && !AbstractChannel.this.isActive()) {
                        this.invokeLater(new Runnable() {
                            public void run() {
                                AbstractChannel.this.pipeline.fireChannelInactive();
                            }
                        });
                    }

                    this.deregister(this.voidPromise());
                }

            }
        }

        @Override
        public final void closeForcibly() {
            try {
                AbstractChannel.this.doClose();
            } catch (Exception exception) {
                AbstractChannel.logger.warn("Failed to close a channel.", (Throwable) exception);
            }

        }

        @Override
        public final void deregister(ChannelPromise channelpromise) {
            if (!AbstractChannel.this.registered) {
                channelpromise.setSuccess();
            } else {
                try {
                    AbstractChannel.this.doDeregister();
                } catch (Throwable throwable) {
                    AbstractChannel.logger.warn("Unexpected exception occurred while deregistering a channel.", throwable);
                } finally {
                    if (AbstractChannel.this.registered) {
                        AbstractChannel.this.registered = false;
                        channelpromise.setSuccess();
                        this.invokeLater(new Runnable() {
                            public void run() {
                                AbstractChannel.this.pipeline.fireChannelUnregistered();
                            }
                        });
                    } else {
                        channelpromise.setSuccess();
                    }

                }

            }
        }

        @Override
        public void beginRead() {
            if (AbstractChannel.this.isActive()) {
                try {
                    AbstractChannel.this.doBeginRead();
                } catch (final Exception exception) {
                    this.invokeLater(new Runnable() {
                        public void run() {
                            AbstractChannel.this.pipeline.fireExceptionCaught(exception);
                        }
                    });
                    this.close(this.voidPromise());
                }

            }
        }

        @Override
        public void write(Object object, ChannelPromise channelpromise) {
            if (!AbstractChannel.this.isActive()) {
                if (AbstractChannel.this.isOpen()) {
                    channelpromise.tryFailure(AbstractChannel.NOT_YET_CONNECTED_EXCEPTION);
                } else {
                    channelpromise.tryFailure(AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
                }

                ReferenceCountUtil.release(object);
            } else {
                this.outboundBuffer.addMessage(object, channelpromise);
            }

        }

        @Override
        public void flush() {
            ChannelOutboundBuffer channeloutboundbuffer = this.outboundBuffer;

            if (channeloutboundbuffer != null) {
                channeloutboundbuffer.addFlush();
                this.flush0();
            }
        }

        protected void flush0() {
            if (!this.inFlush0) {
                ChannelOutboundBuffer channeloutboundbuffer = this.outboundBuffer;

                if (channeloutboundbuffer != null && !channeloutboundbuffer.isEmpty()) {
                    this.inFlush0 = true;
                    if (!AbstractChannel.this.isActive()) {
                        try {
                            if (AbstractChannel.this.isOpen()) {
                                channeloutboundbuffer.failFlushed(AbstractChannel.NOT_YET_CONNECTED_EXCEPTION);
                            } else {
                                channeloutboundbuffer.failFlushed(AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
                            }
                        } finally {
                            this.inFlush0 = false;
                        }

                    } else {
                        try {
                            AbstractChannel.this.doWrite(channeloutboundbuffer);
                        } catch (Throwable throwable) {
                            channeloutboundbuffer.failFlushed(throwable);
                            if (throwable instanceof IOException) {
                                this.close(this.voidPromise());
                            }
                        } finally {
                            this.inFlush0 = false;
                        }

                    }
                }
            }
        }

        @Override
        public ChannelPromise voidPromise() {
            return AbstractChannel.this.unsafeVoidPromise;
        }

        protected final boolean ensureOpen(ChannelPromise channelpromise) {
            if (AbstractChannel.this.isOpen()) {
                return true;
            } else {
                channelpromise.setFailure(AbstractChannel.CLOSED_CHANNEL_EXCEPTION);
                return false;
            }
        }

        protected final void closeIfClosed() {
            if (!AbstractChannel.this.isOpen()) {
                this.close(this.voidPromise());
            }
        }

        private void invokeLater(Runnable runnable) {
            AbstractChannel.this.eventLoop().execute(runnable);
        }
    }
}
