package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.Recycler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

final class DefaultChannelHandlerContext extends DefaultAttributeMap implements ChannelHandlerContext {

    volatile DefaultChannelHandlerContext next;
    volatile DefaultChannelHandlerContext prev;
    private final boolean inbound;
    private final boolean outbound;
    private final AbstractChannel channel;
    private final DefaultChannelPipeline pipeline;
    private final String name;
    private final ChannelHandler handler;
    private boolean removed;
    final EventExecutor executor;
    private ChannelFuture succeededFuture;
    private Runnable invokeChannelReadCompleteTask;
    private Runnable invokeReadTask;
    private Runnable invokeFlushTask;
    private Runnable invokeChannelWritableStateChangedTask;

    DefaultChannelHandlerContext(DefaultChannelPipeline defaultchannelpipeline, EventExecutorGroup eventexecutorgroup, String s, ChannelHandler channelhandler) {
        if (s == null) {
            throw new NullPointerException("name");
        } else if (channelhandler == null) {
            throw new NullPointerException("handler");
        } else {
            this.channel = defaultchannelpipeline.channel;
            this.pipeline = defaultchannelpipeline;
            this.name = s;
            this.handler = channelhandler;
            if (eventexecutorgroup != null) {
                EventExecutor eventexecutor = (EventExecutor) defaultchannelpipeline.childExecutors.get(eventexecutorgroup);

                if (eventexecutor == null) {
                    eventexecutor = eventexecutorgroup.next();
                    defaultchannelpipeline.childExecutors.put(eventexecutorgroup, eventexecutor);
                }

                this.executor = eventexecutor;
            } else {
                this.executor = null;
            }

            this.inbound = channelhandler instanceof ChannelInboundHandler;
            this.outbound = channelhandler instanceof ChannelOutboundHandler;
        }
    }

    void teardown() {
        EventExecutor eventexecutor = this.executor();

        if (eventexecutor.inEventLoop()) {
            this.teardown0();
        } else {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    DefaultChannelHandlerContext.this.teardown0();
                }
            });
        }

    }

    private void teardown0() {
        DefaultChannelHandlerContext defaultchannelhandlercontext = this.prev;

        if (defaultchannelhandlercontext != null) {
            DefaultChannelPipeline defaultchannelpipeline = this.pipeline;

            synchronized (this.pipeline) {
                this.pipeline.remove0(this);
            }

            defaultchannelhandlercontext.teardown();
        }

    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.channel().config().getAllocator();
    }

    @Override
    public EventExecutor executor() {
        return (EventExecutor) (this.executor == null ? this.channel().eventLoop() : this.executor);
    }

    @Override
    public ChannelHandler handler() {
        return this.handler;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextInbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeChannelRegistered();
        } else {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    defaultchannelhandlercontext.invokeChannelRegistered();
                }
            });
        }

        return this;
    }

    private void invokeChannelRegistered() {
        try {
            ((ChannelInboundHandler) this.handler).channelRegistered(this);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelHandlerContext fireChannelUnregistered() {
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextInbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeChannelUnregistered();
        } else {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    defaultchannelhandlercontext.invokeChannelUnregistered();
                }
            });
        }

        return this;
    }

    private void invokeChannelUnregistered() {
        try {
            ((ChannelInboundHandler) this.handler).channelUnregistered(this);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelHandlerContext fireChannelActive() {
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextInbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeChannelActive();
        } else {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    defaultchannelhandlercontext.invokeChannelActive();
                }
            });
        }

        return this;
    }

    private void invokeChannelActive() {
        try {
            ((ChannelInboundHandler) this.handler).channelActive(this);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelHandlerContext fireChannelInactive() {
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextInbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeChannelInactive();
        } else {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    defaultchannelhandlercontext.invokeChannelInactive();
                }
            });
        }

        return this;
    }

    private void invokeChannelInactive() {
        try {
            ((ChannelInboundHandler) this.handler).channelInactive(this);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelHandlerContext fireExceptionCaught(final Throwable throwable) {
        if (throwable == null) {
            throw new NullPointerException("cause");
        } else {
            final DefaultChannelHandlerContext defaultchannelhandlercontext = this.next;
            EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

            if (eventexecutor.inEventLoop()) {
                defaultchannelhandlercontext.invokeExceptionCaught(throwable);
            } else {
                try {
                    eventexecutor.execute(new Runnable() {
                        public void run() {
                            defaultchannelhandlercontext.invokeExceptionCaught(throwable);
                        }
                    });
                } catch (Throwable throwable1) {
                    if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                        DefaultChannelPipeline.logger.warn("Failed to submit an exceptionCaught() event.", throwable1);
                        DefaultChannelPipeline.logger.warn("The exceptionCaught() event that was failed to submit was:", throwable);
                    }
                }
            }

            return this;
        }
    }

    private void invokeExceptionCaught(Throwable throwable) {
        try {
            this.handler.exceptionCaught(this, throwable);
        } catch (Throwable throwable1) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("An exception was thrown by a user handler's exceptionCaught() method while handling the following exception:", throwable);
            }
        }

    }

    @Override
    public ChannelHandlerContext fireUserEventTriggered(final Object object) {
        if (object == null) {
            throw new NullPointerException("event");
        } else {
            final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextInbound();
            EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

            if (eventexecutor.inEventLoop()) {
                defaultchannelhandlercontext.invokeUserEventTriggered(object);
            } else {
                eventexecutor.execute(new Runnable() {
                    public void run() {
                        defaultchannelhandlercontext.invokeUserEventTriggered(object);
                    }
                });
            }

            return this;
        }
    }

    private void invokeUserEventTriggered(Object object) {
        try {
            ((ChannelInboundHandler) this.handler).userEventTriggered(this, object);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelHandlerContext fireChannelRead(final Object object) {
        if (object == null) {
            throw new NullPointerException("msg");
        } else {
            final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextInbound();
            EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

            if (eventexecutor.inEventLoop()) {
                defaultchannelhandlercontext.invokeChannelRead(object);
            } else {
                eventexecutor.execute(new Runnable() {
                    public void run() {
                        defaultchannelhandlercontext.invokeChannelRead(object);
                    }
                });
            }

            return this;
        }
    }

    private void invokeChannelRead(Object object) {
        try {
            ((ChannelInboundHandler) this.handler).channelRead(this, object);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelHandlerContext fireChannelReadComplete() {
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextInbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeChannelReadComplete();
        } else {
            Runnable runnable = defaultchannelhandlercontext.invokeChannelReadCompleteTask;

            if (runnable == null) {
                defaultchannelhandlercontext.invokeChannelReadCompleteTask = runnable = new Runnable() {
                    public void run() {
                        defaultchannelhandlercontext.invokeChannelReadComplete();
                    }
                };
            }

            eventexecutor.execute(runnable);
        }

        return this;
    }

    private void invokeChannelReadComplete() {
        try {
            ((ChannelInboundHandler) this.handler).channelReadComplete(this);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged() {
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextInbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeChannelWritabilityChanged();
        } else {
            Runnable runnable = defaultchannelhandlercontext.invokeChannelWritableStateChangedTask;

            if (runnable == null) {
                defaultchannelhandlercontext.invokeChannelWritableStateChangedTask = runnable = new Runnable() {
                    public void run() {
                        defaultchannelhandlercontext.invokeChannelWritabilityChanged();
                    }
                };
            }

            eventexecutor.execute(runnable);
        }

        return this;
    }

    private void invokeChannelWritabilityChanged() {
        try {
            ((ChannelInboundHandler) this.handler).channelWritabilityChanged(this);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelFuture bind(SocketAddress socketaddress) {
        return this.bind(socketaddress, this.newPromise());
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress) {
        return this.connect(socketaddress, this.newPromise());
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress, SocketAddress socketaddress1) {
        return this.connect(socketaddress, socketaddress1, this.newPromise());
    }

    @Override
    public ChannelFuture disconnect() {
        return this.disconnect(this.newPromise());
    }

    @Override
    public ChannelFuture close() {
        return this.close(this.newPromise());
    }

    @Override
    public ChannelFuture deregister() {
        return this.deregister(this.newPromise());
    }

    @Override
    public ChannelFuture bind(final SocketAddress socketaddress, final ChannelPromise channelpromise) {
        if (socketaddress == null) {
            throw new NullPointerException("localAddress");
        } else {
            this.validatePromise(channelpromise, false);
            final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextOutbound();
            EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

            if (eventexecutor.inEventLoop()) {
                defaultchannelhandlercontext.invokeBind(socketaddress, channelpromise);
            } else {
                eventexecutor.execute(new Runnable() {
                    public void run() {
                        defaultchannelhandlercontext.invokeBind(socketaddress, channelpromise);
                    }
                });
            }

            return channelpromise;
        }
    }

    private void invokeBind(SocketAddress socketaddress, ChannelPromise channelpromise) {
        try {
            ((ChannelOutboundHandler) this.handler).bind(this, socketaddress, channelpromise);
        } catch (Throwable throwable) {
            notifyOutboundHandlerException(throwable, channelpromise);
        }

    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress, ChannelPromise channelpromise) {
        return this.connect(socketaddress, (SocketAddress) null, channelpromise);
    }

    @Override
    public ChannelFuture connect(final SocketAddress socketaddress, final SocketAddress socketaddress1, final ChannelPromise channelpromise) {
        if (socketaddress == null) {
            throw new NullPointerException("remoteAddress");
        } else {
            this.validatePromise(channelpromise, false);
            final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextOutbound();
            EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

            if (eventexecutor.inEventLoop()) {
                defaultchannelhandlercontext.invokeConnect(socketaddress, socketaddress1, channelpromise);
            } else {
                eventexecutor.execute(new Runnable() {
                    public void run() {
                        defaultchannelhandlercontext.invokeConnect(socketaddress, socketaddress1, channelpromise);
                    }
                });
            }

            return channelpromise;
        }
    }

    private void invokeConnect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) {
        try {
            ((ChannelOutboundHandler) this.handler).connect(this, socketaddress, socketaddress1, channelpromise);
        } catch (Throwable throwable) {
            notifyOutboundHandlerException(throwable, channelpromise);
        }

    }

    @Override
    public ChannelFuture disconnect(final ChannelPromise channelpromise) {
        this.validatePromise(channelpromise, false);
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextOutbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            if (!this.channel().metadata().hasDisconnect()) {
                defaultchannelhandlercontext.invokeClose(channelpromise);
            } else {
                defaultchannelhandlercontext.invokeDisconnect(channelpromise);
            }
        } else {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    if (!DefaultChannelHandlerContext.this.channel().metadata().hasDisconnect()) {
                        defaultchannelhandlercontext.invokeClose(channelpromise);
                    } else {
                        defaultchannelhandlercontext.invokeDisconnect(channelpromise);
                    }

                }
            });
        }

        return channelpromise;
    }

    private void invokeDisconnect(ChannelPromise channelpromise) {
        try {
            ((ChannelOutboundHandler) this.handler).disconnect(this, channelpromise);
        } catch (Throwable throwable) {
            notifyOutboundHandlerException(throwable, channelpromise);
        }

    }

    @Override
    public ChannelFuture close(final ChannelPromise channelpromise) {
        this.validatePromise(channelpromise, false);
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextOutbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeClose(channelpromise);
        } else {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    defaultchannelhandlercontext.invokeClose(channelpromise);
                }
            });
        }

        return channelpromise;
    }

    private void invokeClose(ChannelPromise channelpromise) {
        try {
            ((ChannelOutboundHandler) this.handler).close(this, channelpromise);
        } catch (Throwable throwable) {
            notifyOutboundHandlerException(throwable, channelpromise);
        }

    }

    @Override
    public ChannelFuture deregister(final ChannelPromise channelpromise) {
        this.validatePromise(channelpromise, false);
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextOutbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeDeregister(channelpromise);
        } else {
            eventexecutor.execute(new Runnable() {
                public void run() {
                    defaultchannelhandlercontext.invokeDeregister(channelpromise);
                }
            });
        }

        return channelpromise;
    }

    private void invokeDeregister(ChannelPromise channelpromise) {
        try {
            ((ChannelOutboundHandler) this.handler).deregister(this, channelpromise);
        } catch (Throwable throwable) {
            notifyOutboundHandlerException(throwable, channelpromise);
        }

    }

    @Override
    public ChannelHandlerContext read() {
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextOutbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeRead();
        } else {
            Runnable runnable = defaultchannelhandlercontext.invokeReadTask;

            if (runnable == null) {
                defaultchannelhandlercontext.invokeReadTask = runnable = new Runnable() {
                    public void run() {
                        defaultchannelhandlercontext.invokeRead();
                    }
                };
            }

            eventexecutor.execute(runnable);
        }

        return this;
    }

    private void invokeRead() {
        try {
            ((ChannelOutboundHandler) this.handler).read(this);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelFuture write(Object object) {
        return this.write(object, this.newPromise());
    }

    @Override
    public ChannelFuture write(Object object, ChannelPromise channelpromise) {
        if (object == null) {
            throw new NullPointerException("msg");
        } else {
            this.validatePromise(channelpromise, true);
            this.write(object, false, channelpromise);
            return channelpromise;
        }
    }

    private void invokeWrite(Object object, ChannelPromise channelpromise) {
        try {
            ((ChannelOutboundHandler) this.handler).write(this, object, channelpromise);
        } catch (Throwable throwable) {
            notifyOutboundHandlerException(throwable, channelpromise);
        }

    }

    @Override
    public ChannelHandlerContext flush() {
        final DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextOutbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeFlush();
        } else {
            Runnable runnable = defaultchannelhandlercontext.invokeFlushTask;

            if (runnable == null) {
                defaultchannelhandlercontext.invokeFlushTask = runnable = new Runnable() {
                    public void run() {
                        defaultchannelhandlercontext.invokeFlush();
                    }
                };
            }

            eventexecutor.execute(runnable);
        }

        return this;
    }

    private void invokeFlush() {
        try {
            ((ChannelOutboundHandler) this.handler).flush(this);
        } catch (Throwable throwable) {
            this.notifyHandlerException(throwable);
        }

    }

    @Override
    public ChannelFuture writeAndFlush(Object object, ChannelPromise channelpromise) {
        if (object == null) {
            throw new NullPointerException("msg");
        } else {
            this.validatePromise(channelpromise, true);
            this.write(object, true, channelpromise);
            return channelpromise;
        }
    }

    private void write(Object object, boolean flag, ChannelPromise channelpromise) {
        DefaultChannelHandlerContext defaultchannelhandlercontext = this.findContextOutbound();
        EventExecutor eventexecutor = defaultchannelhandlercontext.executor();

        if (eventexecutor.inEventLoop()) {
            defaultchannelhandlercontext.invokeWrite(object, channelpromise);
            if (flag) {
                defaultchannelhandlercontext.invokeFlush();
            }
        } else {
            int i = this.channel.estimatorHandle().size(object);

            if (i > 0) {
                ChannelOutboundBuffer channeloutboundbuffer = this.channel.unsafe().outboundBuffer();

                if (channeloutboundbuffer != null) {
                    channeloutboundbuffer.incrementPendingOutboundBytes(i);
                }
            }

            eventexecutor.execute(DefaultChannelHandlerContext.WriteTask.newInstance(defaultchannelhandlercontext, object, i, flag, channelpromise));
        }

    }

    @Override
    public ChannelFuture writeAndFlush(Object object) {
        return this.writeAndFlush(object, this.newPromise());
    }

    private static void notifyOutboundHandlerException(Throwable throwable, ChannelPromise channelpromise) {
        if (!(channelpromise instanceof VoidChannelPromise)) {
            if (!channelpromise.tryFailure(throwable) && DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("Failed to fail the promise because it's done already: {}", channelpromise, throwable);
            }

        }
    }

    private void notifyHandlerException(Throwable throwable) {
        if (inExceptionCaught(throwable)) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("An exception was thrown by a user handler while handling an exceptionCaught event", throwable);
            }

        } else {
            this.invokeExceptionCaught(throwable);
        }
    }

    private static boolean inExceptionCaught(Throwable throwable) {
        do {
            StackTraceElement[] astacktraceelement = throwable.getStackTrace();

            if (astacktraceelement != null) {
                StackTraceElement[] astacktraceelement1 = astacktraceelement;
                int i = astacktraceelement.length;

                for (int j = 0; j < i; ++j) {
                    StackTraceElement stacktraceelement = astacktraceelement1[j];

                    if (stacktraceelement == null) {
                        break;
                    }

                    if ("exceptionCaught".equals(stacktraceelement.getMethodName())) {
                        return true;
                    }
                }
            }

            throwable = throwable.getCause();
        } while (throwable != null);

        return false;
    }

    @Override
    public ChannelPromise newPromise() {
        return new DefaultChannelPromise(this.channel(), this.executor());
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise(this.channel(), this.executor());
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        Object object = this.succeededFuture;

        if (object == null) {
            this.succeededFuture = (ChannelFuture) (object = new SucceededChannelFuture(this.channel(), this.executor()));
        }

        return (ChannelFuture) object;
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable throwable) {
        return new FailedChannelFuture(this.channel(), this.executor(), throwable);
    }

    private void validatePromise(ChannelPromise channelpromise, boolean flag) {
        if (channelpromise == null) {
            throw new NullPointerException("promise");
        } else if (channelpromise.isDone()) {
            throw new IllegalArgumentException("promise already done: " + channelpromise);
        } else if (channelpromise.channel() != this.channel()) {
            throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", channelpromise.channel(), this.channel()));
        } else if (channelpromise.getClass() != DefaultChannelPromise.class) {
            if (!flag && channelpromise instanceof VoidChannelPromise) {
                throw new IllegalArgumentException(StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
            } else if (channelpromise instanceof AbstractChannel.CloseFuture) {
                throw new IllegalArgumentException(StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
            }
        }
    }

    private DefaultChannelHandlerContext findContextInbound() {
        DefaultChannelHandlerContext defaultchannelhandlercontext = this;

        do {
            defaultchannelhandlercontext = defaultchannelhandlercontext.next;
        } while (!defaultchannelhandlercontext.inbound);

        return defaultchannelhandlercontext;
    }

    private DefaultChannelHandlerContext findContextOutbound() {
        DefaultChannelHandlerContext defaultchannelhandlercontext = this;

        do {
            defaultchannelhandlercontext = defaultchannelhandlercontext.prev;
        } while (!defaultchannelhandlercontext.outbound);

        return defaultchannelhandlercontext;
    }

    @Override
    public ChannelPromise voidPromise() {
        return this.channel.voidPromise();
    }

    void setRemoved() {
        this.removed = true;
    }

    @Override
    public boolean isRemoved() {
        return this.removed;
    }

    static final class WriteTask implements Runnable {

        private DefaultChannelHandlerContext ctx;
        private Object msg;
        private ChannelPromise promise;
        private int size;
        private boolean flush;
        private static final Recycler<DefaultChannelHandlerContext.WriteTask> RECYCLER = new Recycler<DefaultChannelHandlerContext.WriteTask>() {
            @Override
            protected DefaultChannelHandlerContext.WriteTask newObject(Recycler.Handle recycler_handle) {
                return new DefaultChannelHandlerContext.WriteTask(recycler_handle);
            }
        };
        private final Recycler.Handle handle;

        private static DefaultChannelHandlerContext.WriteTask newInstance(DefaultChannelHandlerContext defaultchannelhandlercontext, Object object, int i, boolean flag, ChannelPromise channelpromise) {
            DefaultChannelHandlerContext.WriteTask defaultchannelhandlercontext_writetask = (DefaultChannelHandlerContext.WriteTask) DefaultChannelHandlerContext.WriteTask.RECYCLER.get();

            defaultchannelhandlercontext_writetask.ctx = defaultchannelhandlercontext;
            defaultchannelhandlercontext_writetask.msg = object;
            defaultchannelhandlercontext_writetask.promise = channelpromise;
            defaultchannelhandlercontext_writetask.size = i;
            defaultchannelhandlercontext_writetask.flush = flag;
            return defaultchannelhandlercontext_writetask;
        }

        private WriteTask(Recycler.Handle recycler_handle) {
            this.handle = recycler_handle;
        }

        public void run() {
            try {
                if (this.size > 0) {
                    ChannelOutboundBuffer channeloutboundbuffer = this.ctx.channel.unsafe().outboundBuffer();

                    if (channeloutboundbuffer != null) {
                        channeloutboundbuffer.decrementPendingOutboundBytes(this.size);
                    }
                }

                this.ctx.invokeWrite(this.msg, this.promise);
                if (this.flush) {
                    this.ctx.invokeFlush();
                }
            } finally {
                this.ctx = null;
                this.msg = null;
                this.promise = null;
                DefaultChannelHandlerContext.WriteTask.RECYCLER.recycle(this, this.handle);
            }

        }
    }
}
