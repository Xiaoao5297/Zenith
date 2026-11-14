package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MoreExecutors {

    private MoreExecutors() {}

    @Beta
    public static ExecutorService getExitingExecutorService(ThreadPoolExecutor threadpoolexecutor, long i, TimeUnit timeunit) {
        return (new MoreExecutors.Application()).getExitingExecutorService(threadpoolexecutor, i, timeunit);
    }

    @Beta
    public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor scheduledthreadpoolexecutor, long i, TimeUnit timeunit) {
        return (new MoreExecutors.Application()).getExitingScheduledExecutorService(scheduledthreadpoolexecutor, i, timeunit);
    }

    @Beta
    public static void addDelayedShutdownHook(ExecutorService executorservice, long i, TimeUnit timeunit) {
        (new MoreExecutors.Application()).addDelayedShutdownHook(executorservice, i, timeunit);
    }

    @Beta
    public static ExecutorService getExitingExecutorService(ThreadPoolExecutor threadpoolexecutor) {
        return (new MoreExecutors.Application()).getExitingExecutorService(threadpoolexecutor);
    }

    @Beta
    public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor scheduledthreadpoolexecutor) {
        return (new MoreExecutors.Application()).getExitingScheduledExecutorService(scheduledthreadpoolexecutor);
    }

    private static void useDaemonThreadFactory(ThreadPoolExecutor threadpoolexecutor) {
        threadpoolexecutor.setThreadFactory((new ThreadFactoryBuilder()).setDaemon(true).setThreadFactory(threadpoolexecutor.getThreadFactory()).build());
    }

    public static ListeningExecutorService sameThreadExecutor() {
        return new MoreExecutors.SameThreadExecutorService();
    }

    public static ListeningExecutorService listeningDecorator(ExecutorService executorservice) {
        return (ListeningExecutorService) (executorservice instanceof ListeningExecutorService ? (ListeningExecutorService) executorservice : (executorservice instanceof ScheduledExecutorService ? new MoreExecutors.ScheduledListeningDecorator((ScheduledExecutorService) executorservice) : new MoreExecutors.ListeningDecorator(executorservice)));
    }

    public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService scheduledexecutorservice) {
        return (ListeningScheduledExecutorService) (scheduledexecutorservice instanceof ListeningScheduledExecutorService ? (ListeningScheduledExecutorService) scheduledexecutorservice : new MoreExecutors.ScheduledListeningDecorator(scheduledexecutorservice));
    }

    static <T> T invokeAnyImpl(ListeningExecutorService listeningexecutorservice, Collection<? extends Callable<T>> collection, boolean flag, long i) throws InterruptedException, ExecutionException, TimeoutException {
        Preconditions.checkNotNull(listeningexecutorservice);
        int j = collection.size();

        Preconditions.checkArgument(j > 0);
        ArrayList arraylist = Lists.newArrayListWithCapacity(j);
        LinkedBlockingQueue linkedblockingqueue = Queues.newLinkedBlockingQueue();
        boolean flag1 = false;

        Object object;

        try {
            flag1 = true;
            ExecutionException executionexception = null;
            long k = flag ? System.nanoTime() : 0L;
            Iterator iterator = collection.iterator();

            arraylist.add(submitAndAddQueueListener(listeningexecutorservice, (Callable) iterator.next(), linkedblockingqueue));
            --j;
            int l = 1;

            while (true) {
                Future future;

                do {
                    future = (Future) linkedblockingqueue.poll();
                    if (future == null) {
                        if (j > 0) {
                            --j;
                            arraylist.add(submitAndAddQueueListener(listeningexecutorservice, (Callable) iterator.next(), linkedblockingqueue));
                            ++l;
                        } else {
                            if (l == 0) {
                                if (executionexception == null) {
                                    executionexception = new ExecutionException((Throwable) null);
                                }

                                throw executionexception;
                            }

                            if (flag) {
                                future = (Future) linkedblockingqueue.poll(i, TimeUnit.NANOSECONDS);
                                if (future == null) {
                                    throw new TimeoutException();
                                }

                                long i1 = System.nanoTime();

                                i -= i1 - k;
                                k = i1;
                            } else {
                                future = (Future) linkedblockingqueue.take();
                            }
                        }
                    }
                } while (future == null);

                --l;

                try {
                    object = future.get();
                    flag1 = false;
                    break;
                } catch (ExecutionException executionexception1) {
                    executionexception = executionexception1;
                } catch (RuntimeException runtimeexception) {
                    executionexception = new ExecutionException(runtimeexception);
                }
            }
        } finally {
            if (flag1) {
                Iterator iterator1 = arraylist.iterator();

                while (iterator1.hasNext()) {
                    Future future1 = (Future) iterator1.next();

                    future1.cancel(true);
                }

            }
        }

        Iterator iterator2 = arraylist.iterator();

        while (iterator2.hasNext()) {
            Future future2 = (Future) iterator2.next();

            future2.cancel(true);
        }

        return object;
    }

    private static <T> ListenableFuture<T> submitAndAddQueueListener(ListeningExecutorService listeningexecutorservice, Callable<T> callable, final BlockingQueue<Future<T>> blockingqueue) {
        final ListenableFuture listenablefuture = listeningexecutorservice.submit(callable);

        listenablefuture.addListener(new Runnable() {
            public void run() {
                blockingqueue.add(listenablefuture);
            }
        }, sameThreadExecutor());
        return listenablefuture;
    }

    @Beta
    public static ThreadFactory platformThreadFactory() {
        if (!isAppEngine()) {
            return Executors.defaultThreadFactory();
        } else {
            try {
                return (ThreadFactory) Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory").invoke((Object) null);
            } catch (IllegalAccessException illegalaccessexception) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", illegalaccessexception);
            } catch (ClassNotFoundException classnotfoundexception) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", classnotfoundexception);
            } catch (NoSuchMethodException nosuchmethodexception) {
                throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", nosuchmethodexception);
            } catch (InvocationTargetException invocationtargetexception) {
                throw Throwables.propagate(invocationtargetexception.getCause());
            }
        }
    }

    private static boolean isAppEngine() {
        if (System.getProperty("com.google.appengine.runtime.environment") == null) {
            return false;
        } else {
            try {
                return Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment").invoke((Object) null) != null;
            } catch (ClassNotFoundException classnotfoundexception) {
                return false;
            } catch (InvocationTargetException invocationtargetexception) {
                return false;
            } catch (IllegalAccessException illegalaccessexception) {
                return false;
            } catch (NoSuchMethodException nosuchmethodexception) {
                return false;
            }
        }
    }

    static Thread newThread(String s, Runnable runnable) {
        Preconditions.checkNotNull(s);
        Preconditions.checkNotNull(runnable);
        Thread thread = platformThreadFactory().newThread(runnable);

        try {
            thread.setName(s);
        } catch (SecurityException securityexception) {
            ;
        }

        return thread;
    }

    static Executor renamingDecorator(final Executor executor, final Supplier<String> supplier) {
        Preconditions.checkNotNull(executor);
        Preconditions.checkNotNull(supplier);
        return isAppEngine() ? executor : new Executor() {
            public void execute(Runnable runnable) {
                executor.execute(Callables.threadRenaming(runnable, supplier));
            }
        };
    }

    static ExecutorService renamingDecorator(ExecutorService executorservice, final Supplier<String> supplier) {
        Preconditions.checkNotNull(executorservice);
        Preconditions.checkNotNull(supplier);
        return (ExecutorService) (isAppEngine() ? executorservice : new WrappingExecutorService(executorservice) {
            @Override
            protected <T> Callable<T> wrapTask(Callable<T> callable) {
                return Callables.threadRenaming(callable, supplier);
            }

            @Override
            protected Runnable wrapTask(Runnable runnable) {
                return Callables.threadRenaming(runnable, supplier);
            }
        });
    }

    static ScheduledExecutorService renamingDecorator(ScheduledExecutorService scheduledexecutorservice, final Supplier<String> supplier) {
        Preconditions.checkNotNull(scheduledexecutorservice);
        Preconditions.checkNotNull(supplier);
        return (ScheduledExecutorService) (isAppEngine() ? scheduledexecutorservice : new WrappingScheduledExecutorService(scheduledexecutorservice) {
            @Override
            protected <T> Callable<T> wrapTask(Callable<T> callable) {
                return Callables.threadRenaming(callable, supplier);
            }

            @Override
            protected Runnable wrapTask(Runnable runnable) {
                return Callables.threadRenaming(runnable, supplier);
            }
        });
    }

    private static class ScheduledListeningDecorator extends MoreExecutors.ListeningDecorator implements ListeningScheduledExecutorService {

        final ScheduledExecutorService delegate;

        ScheduledListeningDecorator(ScheduledExecutorService scheduledexecutorservice) {
            super(scheduledexecutorservice);
            this.delegate = (ScheduledExecutorService) Preconditions.checkNotNull(scheduledexecutorservice);
        }

        @Override
        public ListenableScheduledFuture<?> schedule(Runnable runnable, long i, TimeUnit timeunit) {
            ListenableFutureTask listenablefuturetask = ListenableFutureTask.create(runnable, (Object) null);
            ScheduledFuture scheduledfuture = this.delegate.schedule(listenablefuturetask, i, timeunit);

            return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask<>(listenablefuturetask, scheduledfuture);
        }

        @Override
        public <V> ListenableScheduledFuture<V> schedule(Callable<V> callable, long i, TimeUnit timeunit) {
            ListenableFutureTask listenablefuturetask = ListenableFutureTask.create(callable);
            ScheduledFuture scheduledfuture = this.delegate.schedule(listenablefuturetask, i, timeunit);

            return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask<>(listenablefuturetask, scheduledfuture);
        }

        @Override
        public ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long i, long j, TimeUnit timeunit) {
            MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask moreexecutors_scheduledlisteningdecorator_neversuccessfullistenablefuturetask = new MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask(runnable);
            ScheduledFuture scheduledfuture = this.delegate.scheduleAtFixedRate(moreexecutors_scheduledlisteningdecorator_neversuccessfullistenablefuturetask, i, j, timeunit);

            return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask<>(moreexecutors_scheduledlisteningdecorator_neversuccessfullistenablefuturetask, scheduledfuture);
        }

        @Override
        public ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long i, long j, TimeUnit timeunit) {
            MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask moreexecutors_scheduledlisteningdecorator_neversuccessfullistenablefuturetask = new MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask(runnable);
            ScheduledFuture scheduledfuture = this.delegate.scheduleWithFixedDelay(moreexecutors_scheduledlisteningdecorator_neversuccessfullistenablefuturetask, i, j, timeunit);

            return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask<>(moreexecutors_scheduledlisteningdecorator_neversuccessfullistenablefuturetask, scheduledfuture);
        }

        private static final class NeverSuccessfulListenableFutureTask extends AbstractFuture<Void> implements Runnable {

            private final Runnable delegate;

            public NeverSuccessfulListenableFutureTask(Runnable runnable) {
                this.delegate = (Runnable) Preconditions.checkNotNull(runnable);
            }

            public void run() {
                try {
                    this.delegate.run();
                } catch (Throwable throwable) {
                    this.setException(throwable);
                    throw Throwables.propagate(throwable);
                }
            }
        }

        private static final class ListenableScheduledTask<V> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> implements ListenableScheduledFuture<V> {

            private final ScheduledFuture<?> scheduledDelegate;

            public ListenableScheduledTask(ListenableFuture<V> listenablefuture, ScheduledFuture<?> scheduledfuture) {
                super(listenablefuture);
                this.scheduledDelegate = scheduledfuture;
            }

            @Override
            public boolean cancel(boolean flag) {
                boolean flag1 = super.cancel(flag);

                if (flag1) {
                    this.scheduledDelegate.cancel(flag);
                }

                return flag1;
            }

            public long getDelay(TimeUnit timeunit) {
                return this.scheduledDelegate.getDelay(timeunit);
            }

            public int compareTo(Delayed delayed) {
                return this.scheduledDelegate.compareTo(delayed);
            }
        }
    }

    private static class ListeningDecorator extends AbstractListeningExecutorService {

        private final ExecutorService delegate;

        ListeningDecorator(ExecutorService executorservice) {
            this.delegate = (ExecutorService) Preconditions.checkNotNull(executorservice);
        }

        public boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
            return this.delegate.awaitTermination(i, timeunit);
        }

        public boolean isShutdown() {
            return this.delegate.isShutdown();
        }

        public boolean isTerminated() {
            return this.delegate.isTerminated();
        }

        public void shutdown() {
            this.delegate.shutdown();
        }

        public List<Runnable> shutdownNow() {
            return this.delegate.shutdownNow();
        }

        public void execute(Runnable runnable) {
            this.delegate.execute(runnable);
        }
    }

    private static class SameThreadExecutorService extends AbstractListeningExecutorService {

        private final Lock lock;
        private final Condition termination;
        private int runningTasks;
        private boolean shutdown;

        private SameThreadExecutorService() {
            this.lock = new ReentrantLock();
            this.termination = this.lock.newCondition();
            this.runningTasks = 0;
            this.shutdown = false;
        }

        public void execute(Runnable runnable) {
            this.startTask();

            try {
                runnable.run();
            } finally {
                this.endTask();
            }

        }

        public boolean isShutdown() {
            this.lock.lock();

            boolean flag;

            try {
                flag = this.shutdown;
            } finally {
                this.lock.unlock();
            }

            return flag;
        }

        public void shutdown() {
            this.lock.lock();

            try {
                this.shutdown = true;
            } finally {
                this.lock.unlock();
            }

        }

        public List<Runnable> shutdownNow() {
            this.shutdown();
            return Collections.emptyList();
        }

        public boolean isTerminated() {
            this.lock.lock();

            boolean flag;

            try {
                flag = this.shutdown && this.runningTasks == 0;
            } finally {
                this.lock.unlock();
            }

            return flag;
        }

        public boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
            long j = timeunit.toNanos(i);

            this.lock.lock();

            try {
                boolean flag;

                while (!this.isTerminated()) {
                    if (j <= 0L) {
                        flag = false;
                        return flag;
                    }

                    j = this.termination.awaitNanos(j);
                }

                flag = true;
                return flag;
            } finally {
                this.lock.unlock();
            }
        }

        private void startTask() {
            this.lock.lock();

            try {
                if (this.isShutdown()) {
                    throw new RejectedExecutionException("Executor already shutdown");
                }

                ++this.runningTasks;
            } finally {
                this.lock.unlock();
            }

        }

        private void endTask() {
            this.lock.lock();

            try {
                --this.runningTasks;
                if (this.isTerminated()) {
                    this.termination.signalAll();
                }
            } finally {
                this.lock.unlock();
            }

        }
    }

    @VisibleForTesting
    static class Application {

        Application() {}

        final ExecutorService getExitingExecutorService(ThreadPoolExecutor threadpoolexecutor, long i, TimeUnit timeunit) {
            MoreExecutors.useDaemonThreadFactory(threadpoolexecutor);
            ExecutorService executorservice = Executors.unconfigurableExecutorService(threadpoolexecutor);

            this.addDelayedShutdownHook(executorservice, i, timeunit);
            return executorservice;
        }

        final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor scheduledthreadpoolexecutor, long i, TimeUnit timeunit) {
            MoreExecutors.useDaemonThreadFactory(scheduledthreadpoolexecutor);
            ScheduledExecutorService scheduledexecutorservice = Executors.unconfigurableScheduledExecutorService(scheduledthreadpoolexecutor);

            this.addDelayedShutdownHook(scheduledexecutorservice, i, timeunit);
            return scheduledexecutorservice;
        }

        final void addDelayedShutdownHook(final ExecutorService executorservice, final long i, final TimeUnit timeunit) {
            Preconditions.checkNotNull(executorservice);
            Preconditions.checkNotNull(timeunit);
            this.addShutdownHook(MoreExecutors.newThread("DelayedShutdownHook-for-" + executorservice, new Runnable() {
                public void run() {
                    try {
                        executorservice.shutdown();
                        executorservice.awaitTermination(i, timeunit);
                    } catch (InterruptedException interruptedexception) {
                        ;
                    }

                }
            }));
        }

        final ExecutorService getExitingExecutorService(ThreadPoolExecutor threadpoolexecutor) {
            return this.getExitingExecutorService(threadpoolexecutor, 120L, TimeUnit.SECONDS);
        }

        final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor scheduledthreadpoolexecutor) {
            return this.getExitingScheduledExecutorService(scheduledthreadpoolexecutor, 120L, TimeUnit.SECONDS);
        }

        @VisibleForTesting
        void addShutdownHook(Thread thread) {
            Runtime.getRuntime().addShutdownHook(thread);
        }
    }
}
