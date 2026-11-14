package io.netty.channel;

import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadPerChannelEventLoopGroup extends AbstractEventExecutorGroup implements EventLoopGroup {

    private final Object[] childArgs;
    private final int maxChannels;
    final ThreadFactory threadFactory;
    final Set<ThreadPerChannelEventLoop> activeChildren;
    final Queue<ThreadPerChannelEventLoop> idleChildren;
    private final ChannelException tooManyChannels;
    private volatile boolean shuttingDown;
    private final Promise<?> terminationFuture;
    private final FutureListener<Object> childTerminationListener;

    protected ThreadPerChannelEventLoopGroup() {
        this(0);
    }

    protected ThreadPerChannelEventLoopGroup(int i) {
        this(i, Executors.defaultThreadFactory());
    }

    protected ThreadPerChannelEventLoopGroup(int i, ThreadFactory threadfactory, Object... aobject) {
        this.activeChildren = Collections.newSetFromMap(PlatformDependent.newConcurrentHashMap());
        this.idleChildren = new ConcurrentLinkedQueue();
        this.terminationFuture = new DefaultPromise<>(GlobalEventExecutor.INSTANCE);
        this.childTerminationListener = new FutureListener<Object>() {
            @Override
            public void operationComplete(Future<Object> future) throws Exception {
                if (ThreadPerChannelEventLoopGroup.this.isTerminated()) {
                    ThreadPerChannelEventLoopGroup.this.terminationFuture.trySuccess((Object) null);
                }

            }
        };
        if (i < 0) {
            throw new IllegalArgumentException(String.format("maxChannels: %d (expected: >= 0)", i));
        } else if (threadfactory == null) {
            throw new NullPointerException("threadFactory");
        } else {
            if (aobject == null) {
                this.childArgs = EmptyArrays.EMPTY_OBJECTS;
            } else {
                this.childArgs = (Object[]) aobject.clone();
            }

            this.maxChannels = i;
            this.threadFactory = threadfactory;
            this.tooManyChannels = new ChannelException("too many channels (max: " + i + ')');
            this.tooManyChannels.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        }
    }

    protected ThreadPerChannelEventLoop newChild(Object... aobject) throws Exception {
        return new ThreadPerChannelEventLoop(this);
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return new ReadOnlyIterator<>(this.activeChildren.iterator());
    }

    @Override
    public EventLoop next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> shutdownGracefully(long i, long j, TimeUnit timeunit) {
        this.shuttingDown = true;
        Iterator iterator = this.activeChildren.iterator();

        EventLoop eventloop;

        while (iterator.hasNext()) {
            eventloop = (EventLoop) iterator.next();
            eventloop.shutdownGracefully(i, j, timeunit);
        }

        iterator = this.idleChildren.iterator();

        while (iterator.hasNext()) {
            eventloop = (EventLoop) iterator.next();
            eventloop.shutdownGracefully(i, j, timeunit);
        }

        if (this.isTerminated()) {
            this.terminationFuture.trySuccess((Object) null);
        }

        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void shutdown() {
        this.shuttingDown = true;
        Iterator iterator = this.activeChildren.iterator();

        EventLoop eventloop;

        while (iterator.hasNext()) {
            eventloop = (EventLoop) iterator.next();
            eventloop.shutdown();
        }

        iterator = this.idleChildren.iterator();

        while (iterator.hasNext()) {
            eventloop = (EventLoop) iterator.next();
            eventloop.shutdown();
        }

        if (this.isTerminated()) {
            this.terminationFuture.trySuccess((Object) null);
        }

    }

    @Override
    public boolean isShuttingDown() {
        Iterator iterator = this.activeChildren.iterator();

        EventLoop eventloop;

        do {
            if (!iterator.hasNext()) {
                iterator = this.idleChildren.iterator();

                do {
                    if (!iterator.hasNext()) {
                        return true;
                    }

                    eventloop = (EventLoop) iterator.next();
                } while (eventloop.isShuttingDown());

                return false;
            }

            eventloop = (EventLoop) iterator.next();
        } while (eventloop.isShuttingDown());

        return false;
    }

    public boolean isShutdown() {
        Iterator iterator = this.activeChildren.iterator();

        EventLoop eventloop;

        do {
            if (!iterator.hasNext()) {
                iterator = this.idleChildren.iterator();

                do {
                    if (!iterator.hasNext()) {
                        return true;
                    }

                    eventloop = (EventLoop) iterator.next();
                } while (eventloop.isShutdown());

                return false;
            }

            eventloop = (EventLoop) iterator.next();
        } while (eventloop.isShutdown());

        return false;
    }

    public boolean isTerminated() {
        Iterator iterator = this.activeChildren.iterator();

        EventLoop eventloop;

        do {
            if (!iterator.hasNext()) {
                iterator = this.idleChildren.iterator();

                do {
                    if (!iterator.hasNext()) {
                        return true;
                    }

                    eventloop = (EventLoop) iterator.next();
                } while (eventloop.isTerminated());

                return false;
            }

            eventloop = (EventLoop) iterator.next();
        } while (eventloop.isTerminated());

        return false;
    }

    public boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
        long j = System.nanoTime() + timeunit.toNanos(i);
        Iterator iterator = this.activeChildren.iterator();

        EventLoop eventloop;
        long k;

        while (iterator.hasNext()) {
            eventloop = (EventLoop) iterator.next();

            while (true) {
                k = j - System.nanoTime();
                if (k <= 0L) {
                    return this.isTerminated();
                }

                if (eventloop.awaitTermination(k, TimeUnit.NANOSECONDS)) {
                    break;
                }
            }
        }

        iterator = this.idleChildren.iterator();

        while (iterator.hasNext()) {
            eventloop = (EventLoop) iterator.next();

            while (true) {
                k = j - System.nanoTime();
                if (k <= 0L) {
                    return this.isTerminated();
                }

                if (eventloop.awaitTermination(k, TimeUnit.NANOSECONDS)) {
                    break;
                }
            }
        }

        return this.isTerminated();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else {
            try {
                return this.nextChild().register(channel);
            } catch (Throwable throwable) {
                return channel.newFailedFuture(throwable);
            }
        }
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise channelpromise) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else {
            try {
                return this.nextChild().register(channel, channelpromise);
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
                return channelpromise;
            }
        }
    }

    private EventLoop nextChild() throws Exception {
        if (this.shuttingDown) {
            throw new RejectedExecutionException("shutting down");
        } else {
            ThreadPerChannelEventLoop threadperchanneleventloop = (ThreadPerChannelEventLoop) this.idleChildren.poll();

            if (threadperchanneleventloop == null) {
                if (this.maxChannels > 0 && this.activeChildren.size() >= this.maxChannels) {
                    throw this.tooManyChannels;
                }

                threadperchanneleventloop = this.newChild(this.childArgs);
                threadperchanneleventloop.terminationFuture().addListener(this.childTerminationListener);
            }

            this.activeChildren.add(threadperchanneleventloop);
            return threadperchanneleventloop;
        }
    }
}
