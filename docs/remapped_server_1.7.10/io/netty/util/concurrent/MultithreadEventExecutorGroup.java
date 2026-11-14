package io.netty.util.concurrent;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {

    private final EventExecutor[] children;
    private final AtomicInteger childIndex = new AtomicInteger();
    private final AtomicInteger terminatedChildren = new AtomicInteger();
    private final Promise<?> terminationFuture;

    protected MultithreadEventExecutorGroup(int i, ThreadFactory threadfactory, Object... aobject) {
        this.terminationFuture = new DefaultPromise<>(GlobalEventExecutor.INSTANCE);
        if (i <= 0) {
            throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", i));
        } else {
            if (threadfactory == null) {
                threadfactory = this.newDefaultThreadFactory();
            }

            this.children = new SingleThreadEventExecutor[i];

            int j;

            for (int k = 0; k < i; ++k) {
                boolean flag = false;
                boolean flag1 = false;

                try {
                    flag1 = true;
                    this.children[k] = this.newChild(threadfactory, aobject);
                    flag = true;
                    flag1 = false;
                } catch (Exception exception) {
                    throw new IllegalStateException("failed to create a child event loop", exception);
                } finally {
                    if (flag1) {
                        if (!flag) {
                            int l;

                            for (l = 0; l < k; ++l) {
                                this.children[l].shutdownGracefully();
                            }

                            for (l = 0; l < k; ++l) {
                                EventExecutor eventexecutor = this.children[l];

                                try {
                                    while (!eventexecutor.isTerminated()) {
                                        eventexecutor.awaitTermination(2147483647L, TimeUnit.SECONDS);
                                    }
                                } catch (InterruptedException interruptedexception) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        }

                    }
                }

                if (!flag) {
                    for (j = 0; j < k; ++j) {
                        this.children[j].shutdownGracefully();
                    }

                    for (j = 0; j < k; ++j) {
                        EventExecutor eventexecutor1 = this.children[j];

                        try {
                            while (!eventexecutor1.isTerminated()) {
                                eventexecutor1.awaitTermination(2147483647L, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException interruptedexception1) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }

            FutureListener futurelistener = new FutureListener<Object>() {
                @Override
                public void operationComplete(Future<Object> future) throws Exception {
                    if (MultithreadEventExecutorGroup.this.terminatedChildren.incrementAndGet() == MultithreadEventExecutorGroup.this.children.length) {
                        MultithreadEventExecutorGroup.this.terminationFuture.setSuccess((Object) null);
                    }

                }
            };
            EventExecutor[] aeventexecutor = this.children;

            j = aeventexecutor.length;

            for (int i1 = 0; i1 < j; ++i1) {
                EventExecutor eventexecutor2 = aeventexecutor[i1];

                eventexecutor2.terminationFuture().addListener(futurelistener);
            }

        }
    }

    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(this.getClass());
    }

    @Override
    public EventExecutor next() {
        return this.children[Math.abs(this.childIndex.getAndIncrement() % this.children.length)];
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return this.children().iterator();
    }

    public final int executorCount() {
        return this.children.length;
    }

    protected Set<EventExecutor> children() {
        Set set = Collections.newSetFromMap(new LinkedHashMap());

        Collections.addAll(set, this.children);
        return set;
    }

    protected abstract EventExecutor newChild(ThreadFactory threadfactory, Object... aobject) throws Exception;

    @Override
    public Future<?> shutdownGracefully(long i, long j, TimeUnit timeunit) {
        EventExecutor[] aeventexecutor = this.children;
        int k = aeventexecutor.length;

        for (int l = 0; l < k; ++l) {
            EventExecutor eventexecutor = aeventexecutor[l];

            eventexecutor.shutdownGracefully(i, j, timeunit);
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
        EventExecutor[] aeventexecutor = this.children;
        int i = aeventexecutor.length;

        for (int j = 0; j < i; ++j) {
            EventExecutor eventexecutor = aeventexecutor[j];

            eventexecutor.shutdown();
        }

    }

    @Override
    public boolean isShuttingDown() {
        EventExecutor[] aeventexecutor = this.children;
        int i = aeventexecutor.length;

        for (int j = 0; j < i; ++j) {
            EventExecutor eventexecutor = aeventexecutor[j];

            if (!eventexecutor.isShuttingDown()) {
                return false;
            }
        }

        return true;
    }

    public boolean isShutdown() {
        EventExecutor[] aeventexecutor = this.children;
        int i = aeventexecutor.length;

        for (int j = 0; j < i; ++j) {
            EventExecutor eventexecutor = aeventexecutor[j];

            if (!eventexecutor.isShutdown()) {
                return false;
            }
        }

        return true;
    }

    public boolean isTerminated() {
        EventExecutor[] aeventexecutor = this.children;
        int i = aeventexecutor.length;

        for (int j = 0; j < i; ++j) {
            EventExecutor eventexecutor = aeventexecutor[j];

            if (!eventexecutor.isTerminated()) {
                return false;
            }
        }

        return true;
    }

    public boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
        long j = System.nanoTime() + timeunit.toNanos(i);
        EventExecutor[] aeventexecutor = this.children;
        int k = aeventexecutor.length;

        for (int l = 0; l < k; ++l) {
            EventExecutor eventexecutor = aeventexecutor[l];

            long i1;

            do {
                i1 = j - System.nanoTime();
                if (i1 <= 0L) {
                    return this.isTerminated();
                }
            } while (!eventexecutor.awaitTermination(i1, TimeUnit.NANOSECONDS));
        }

        return this.isTerminated();
    }
}
