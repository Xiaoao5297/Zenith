package io.netty.util.internal.chmv8;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public abstract class ForkJoinTask<V> implements Future<V>, Serializable {

    volatile int status;
    static final int DONE_MASK = -268435456;
    static final int NORMAL = -268435456;
    static final int CANCELLED = -1073741824;
    static final int EXCEPTIONAL = Integer.MIN_VALUE;
    static final int SIGNAL = 65536;
    static final int SMASK = 65535;
    private static final ForkJoinTask.ExceptionNode[] exceptionTable = new ForkJoinTask.ExceptionNode[32];
    private static final ReentrantLock exceptionTableLock = new ReentrantLock();
    private static final ReferenceQueue<Object> exceptionTableRefQueue = new ReferenceQueue();
    private static final int EXCEPTION_MAP_CAPACITY = 32;
    private static final long serialVersionUID = -7721805057305804111L;
    private static final Unsafe U;
    private static final long STATUS;

    public ForkJoinTask() {}

    private int setCompletion(int i) {
        int j;

        do {
            j = this.status;
            if (this.status < 0) {
                return j;
            }
        } while (!ForkJoinTask.U.compareAndSwapInt(this, ForkJoinTask.STATUS, j, j | i));

        if (j >>> 16 != 0) {
            synchronized (this) {
                this.notifyAll();
            }
        }

        return i;
    }

    final int doExec() {
        int i = this.status;

        if (this.status >= 0) {
            boolean flag;

            try {
                flag = this.exec();
            } catch (Throwable throwable) {
                return this.setExceptionalCompletion(throwable);
            }

            if (flag) {
                i = this.setCompletion(-268435456);
            }
        }

        return i;
    }

    final boolean trySetSignal() {
        int i = this.status;

        return i >= 0 && ForkJoinTask.U.compareAndSwapInt(this, ForkJoinTask.STATUS, i, i | 65536);
    }

    private int externalAwaitDone() {
        ForkJoinPool forkjoinpool = ForkJoinPool.common;
        int i = this.status;

        if (this.status >= 0) {
            if (forkjoinpool != null) {
                if (this instanceof CountedCompleter) {
                    i = forkjoinpool.externalHelpComplete((CountedCompleter) this);
                } else if (forkjoinpool.tryExternalUnpush(this)) {
                    i = this.doExec();
                }
            }

            if (i >= 0) {
                i = this.status;
                if (this.status >= 0) {
                    boolean flag = false;

                    do {
                        if (ForkJoinTask.U.compareAndSwapInt(this, ForkJoinTask.STATUS, i, i | 65536)) {
                            synchronized (this) {
                                if (this.status >= 0) {
                                    try {
                                        this.wait();
                                    } catch (InterruptedException interruptedexception) {
                                        flag = true;
                                    }
                                } else {
                                    this.notifyAll();
                                }
                            }
                        }

                        i = this.status;
                    } while (this.status >= 0);

                    if (flag) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        return i;
    }

    private int externalInterruptibleAwaitDone() throws InterruptedException {
        ForkJoinPool forkjoinpool = ForkJoinPool.common;

        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else {
            int i = this.status;

            if (this.status >= 0 && forkjoinpool != null) {
                if (this instanceof CountedCompleter) {
                    forkjoinpool.externalHelpComplete((CountedCompleter) this);
                } else if (forkjoinpool.tryExternalUnpush(this)) {
                    this.doExec();
                }
            }

            while (true) {
                i = this.status;
                if (this.status < 0) {
                    return i;
                }

                if (ForkJoinTask.U.compareAndSwapInt(this, ForkJoinTask.STATUS, i, i | 65536)) {
                    synchronized (this) {
                        if (this.status >= 0) {
                            this.wait();
                        } else {
                            this.notifyAll();
                        }
                    }
                }
            }
        }
    }

    private int doJoin() {
        int i = this.status;
        ForkJoinPool.WorkQueue forkjoinpool_workqueue;
        ForkJoinWorkerThread forkjoinworkerthread;
        Thread thread;

        return this.status < 0 ? i : ((thread = Thread.currentThread()) instanceof ForkJoinWorkerThread ? ((forkjoinpool_workqueue = (forkjoinworkerthread = (ForkJoinWorkerThread) thread).workQueue).tryUnpush(this) && (i = this.doExec()) < 0 ? i : forkjoinworkerthread.pool.awaitJoin(forkjoinpool_workqueue, this)) : this.externalAwaitDone());
    }

    private int doInvoke() {
        int i;
        ForkJoinWorkerThread forkjoinworkerthread;
        Thread thread;

        return (i = this.doExec()) < 0 ? i : ((thread = Thread.currentThread()) instanceof ForkJoinWorkerThread ? (forkjoinworkerthread = (ForkJoinWorkerThread) thread).pool.awaitJoin(forkjoinworkerthread.workQueue, this) : this.externalAwaitDone());
    }

    final int recordExceptionalCompletion(Throwable throwable) {
        int i = this.status;

        if (this.status >= 0) {
            int j = System.identityHashCode(this);
            ReentrantLock reentrantlock = ForkJoinTask.exceptionTableLock;

            reentrantlock.lock();

            try {
                expungeStaleExceptions();
                ForkJoinTask.ExceptionNode[] aforkjointask_exceptionnode = ForkJoinTask.exceptionTable;
                int k = j & aforkjointask_exceptionnode.length - 1;
                ForkJoinTask.ExceptionNode forkjointask_exceptionnode = aforkjointask_exceptionnode[k];

                while (true) {
                    if (forkjointask_exceptionnode == null) {
                        aforkjointask_exceptionnode[k] = new ForkJoinTask.ExceptionNode(this, throwable, aforkjointask_exceptionnode[k]);
                        break;
                    }

                    if (forkjointask_exceptionnode.get() == this) {
                        break;
                    }

                    forkjointask_exceptionnode = forkjointask_exceptionnode.next;
                }
            } finally {
                reentrantlock.unlock();
            }

            i = this.setCompletion(Integer.MIN_VALUE);
        }

        return i;
    }

    private int setExceptionalCompletion(Throwable throwable) {
        int i = this.recordExceptionalCompletion(throwable);

        if ((i & -268435456) == Integer.MIN_VALUE) {
            this.internalPropagateException(throwable);
        }

        return i;
    }

    void internalPropagateException(Throwable throwable) {}

    static final void cancelIgnoringExceptions(ForkJoinTask<?> forkjointask) {
        if (forkjointask != null && forkjointask.status >= 0) {
            try {
                forkjointask.cancel(false);
            } catch (Throwable throwable) {
                ;
            }
        }

    }

    private void clearExceptionalCompletion() {
        int i = System.identityHashCode(this);
        ReentrantLock reentrantlock = ForkJoinTask.exceptionTableLock;

        reentrantlock.lock();

        try {
            ForkJoinTask.ExceptionNode[] aforkjointask_exceptionnode = ForkJoinTask.exceptionTable;
            int j = i & aforkjointask_exceptionnode.length - 1;
            ForkJoinTask.ExceptionNode forkjointask_exceptionnode = aforkjointask_exceptionnode[j];

            ForkJoinTask.ExceptionNode forkjointask_exceptionnode1;

            for (ForkJoinTask.ExceptionNode forkjointask_exceptionnode2 = null; forkjointask_exceptionnode != null; forkjointask_exceptionnode = forkjointask_exceptionnode1) {
                forkjointask_exceptionnode1 = forkjointask_exceptionnode.next;
                if (forkjointask_exceptionnode.get() == this) {
                    if (forkjointask_exceptionnode2 == null) {
                        aforkjointask_exceptionnode[j] = forkjointask_exceptionnode1;
                    } else {
                        forkjointask_exceptionnode2.next = forkjointask_exceptionnode1;
                    }
                    break;
                }

                forkjointask_exceptionnode2 = forkjointask_exceptionnode;
            }

            expungeStaleExceptions();
            this.status = 0;
        } finally {
            reentrantlock.unlock();
        }

    }

    private Throwable getThrowableException() {
        if ((this.status & -268435456) != Integer.MIN_VALUE) {
            return null;
        } else {
            int i = System.identityHashCode(this);
            ReentrantLock reentrantlock = ForkJoinTask.exceptionTableLock;

            reentrantlock.lock();

            ForkJoinTask.ExceptionNode forkjointask_exceptionnode;

            try {
                expungeStaleExceptions();
                ForkJoinTask.ExceptionNode[] aforkjointask_exceptionnode = ForkJoinTask.exceptionTable;

                for (forkjointask_exceptionnode = aforkjointask_exceptionnode[i & aforkjointask_exceptionnode.length - 1]; forkjointask_exceptionnode != null && forkjointask_exceptionnode.get() != this; forkjointask_exceptionnode = forkjointask_exceptionnode.next) {
                    ;
                }
            } finally {
                reentrantlock.unlock();
            }

            if (forkjointask_exceptionnode != null) {
                Throwable throwable = forkjointask_exceptionnode.ex;

                if (forkjointask_exceptionnode.ex != null) {
                    return throwable;
                }
            }

            return null;
        }
    }

    private static void expungeStaleExceptions() {
        label27:
        while (true) {
            Reference reference;

            if ((reference = ForkJoinTask.exceptionTableRefQueue.poll()) != null) {
                if (!(reference instanceof ForkJoinTask.ExceptionNode)) {
                    continue;
                }

                ForkJoinTask forkjointask = (ForkJoinTask) ((ForkJoinTask.ExceptionNode) reference).get();
                ForkJoinTask.ExceptionNode[] aforkjointask_exceptionnode = ForkJoinTask.exceptionTable;
                int i = System.identityHashCode(forkjointask) & aforkjointask_exceptionnode.length - 1;
                ForkJoinTask.ExceptionNode forkjointask_exceptionnode = aforkjointask_exceptionnode[i];
                ForkJoinTask.ExceptionNode forkjointask_exceptionnode1 = null;

                while (true) {
                    if (forkjointask_exceptionnode == null) {
                        continue label27;
                    }

                    ForkJoinTask.ExceptionNode forkjointask_exceptionnode2 = forkjointask_exceptionnode.next;

                    if (forkjointask_exceptionnode == reference) {
                        if (forkjointask_exceptionnode1 == null) {
                            aforkjointask_exceptionnode[i] = forkjointask_exceptionnode2;
                            continue label27;
                        }

                        forkjointask_exceptionnode1.next = forkjointask_exceptionnode2;
                        continue label27;
                    }

                    forkjointask_exceptionnode1 = forkjointask_exceptionnode;
                    forkjointask_exceptionnode = forkjointask_exceptionnode2;
                }
            }

            return;
        }
    }

    static final void helpExpungeStaleExceptions() {
        ReentrantLock reentrantlock = ForkJoinTask.exceptionTableLock;

        if (reentrantlock.tryLock()) {
            try {
                expungeStaleExceptions();
            } finally {
                reentrantlock.unlock();
            }
        }

    }

    static void rethrow(Throwable throwable) {
        if (throwable != null) {
            uncheckedThrow(throwable);
        }

    }

    static <T extends Throwable> void uncheckedThrow(Throwable throwable) throws T {
        throw throwable;
    }

    private void reportException(int i) {
        if (i == -1073741824) {
            throw new CancellationException();
        } else {
            if (i == Integer.MIN_VALUE) {
                rethrow(this.getThrowableException());
            }

        }
    }

    public final ForkJoinTask<V> fork() {
        Thread thread;

        if ((thread = Thread.currentThread()) instanceof ForkJoinWorkerThread) {
            ((ForkJoinWorkerThread) thread).workQueue.push(this);
        } else {
            ForkJoinPool.common.externalPush(this);
        }

        return this;
    }

    public final V join() {
        int i;

        if ((i = this.doJoin() & -268435456) != -268435456) {
            this.reportException(i);
        }

        return this.getRawResult();
    }

    public final V invoke() {
        int i;

        if ((i = this.doInvoke() & -268435456) != -268435456) {
            this.reportException(i);
        }

        return this.getRawResult();
    }

    public static void invokeAll(ForkJoinTask<?> forkjointask, ForkJoinTask<?> forkjointask1) {
        forkjointask1.fork();
        int i;

        if ((i = forkjointask.doInvoke() & -268435456) != -268435456) {
            forkjointask.reportException(i);
        }

        int j;

        if ((j = forkjointask1.doJoin() & -268435456) != -268435456) {
            forkjointask1.reportException(j);
        }

    }

    public static void invokeAll(ForkJoinTask<?>... aforkjointask) {
        Object object = null;
        int i = aforkjointask.length - 1;

        ForkJoinTask forkjointask;
        int j;

        for (j = i; j >= 0; --j) {
            forkjointask = aforkjointask[j];
            if (forkjointask == null) {
                if (object == null) {
                    object = new NullPointerException();
                }
            } else if (j != 0) {
                forkjointask.fork();
            } else if (forkjointask.doInvoke() < -268435456 && object == null) {
                object = forkjointask.getException();
            }
        }

        for (j = 1; j <= i; ++j) {
            forkjointask = aforkjointask[j];
            if (forkjointask != null) {
                if (object != null) {
                    forkjointask.cancel(false);
                } else if (forkjointask.doJoin() < -268435456) {
                    object = forkjointask.getException();
                }
            }
        }

        if (object != null) {
            rethrow((Throwable) object);
        }

    }

    public static <T extends ForkJoinTask<?>> Collection<T> invokeAll(Collection<T> collection) {
        if (collection instanceof RandomAccess && collection instanceof List) {
            List list = (List) collection;
            Object object = null;
            int i = list.size() - 1;

            ForkJoinTask forkjointask;
            int j;

            for (j = i; j >= 0; --j) {
                forkjointask = (ForkJoinTask) list.get(j);
                if (forkjointask == null) {
                    if (object == null) {
                        object = new NullPointerException();
                    }
                } else if (j != 0) {
                    forkjointask.fork();
                } else if (forkjointask.doInvoke() < -268435456 && object == null) {
                    object = forkjointask.getException();
                }
            }

            for (j = 1; j <= i; ++j) {
                forkjointask = (ForkJoinTask) list.get(j);
                if (forkjointask != null) {
                    if (object != null) {
                        forkjointask.cancel(false);
                    } else if (forkjointask.doJoin() < -268435456) {
                        object = forkjointask.getException();
                    }
                }
            }

            if (object != null) {
                rethrow((Throwable) object);
            }

            return collection;
        } else {
            invokeAll((ForkJoinTask[]) collection.toArray(new ForkJoinTask[collection.size()]));
            return collection;
        }
    }

    public boolean cancel(boolean flag) {
        return (this.setCompletion(-1073741824) & -268435456) == -1073741824;
    }

    public final boolean isDone() {
        return this.status < 0;
    }

    public final boolean isCancelled() {
        return (this.status & -268435456) == -1073741824;
    }

    public final boolean isCompletedAbnormally() {
        return this.status < -268435456;
    }

    public final boolean isCompletedNormally() {
        return (this.status & -268435456) == -268435456;
    }

    public final Throwable getException() {
        int i = this.status & -268435456;

        return (Throwable) (i >= -268435456 ? null : (i == -1073741824 ? new CancellationException() : this.getThrowableException()));
    }

    public void completeExceptionally(Throwable throwable) {
        this.setExceptionalCompletion((Throwable) (!(throwable instanceof RuntimeException) && !(throwable instanceof Error) ? new RuntimeException(throwable) : throwable));
    }

    public void complete(V v0) {
        try {
            this.setRawResult(v0);
        } catch (Throwable throwable) {
            this.setExceptionalCompletion(throwable);
            return;
        }

        this.setCompletion(-268435456);
    }

    public final void quietlyComplete() {
        this.setCompletion(-268435456);
    }

    public final V get() throws InterruptedException, ExecutionException {
        int i = Thread.currentThread() instanceof ForkJoinWorkerThread ? this.doJoin() : this.externalInterruptibleAwaitDone();

        if ((i &= -268435456) == -1073741824) {
            throw new CancellationException();
        } else {
            Throwable throwable;

            if (i == Integer.MIN_VALUE && (throwable = this.getThrowableException()) != null) {
                throw new ExecutionException(throwable);
            } else {
                return this.getRawResult();
            }
        }
    }

    public final V get(long i, TimeUnit timeunit) throws InterruptedException, ExecutionException, TimeoutException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else {
            long j = timeunit.toNanos(i);
            int k = this.status;

            if (this.status >= 0 && j > 0L) {
                long l = System.nanoTime() + j;
                ForkJoinPool forkjoinpool = null;
                ForkJoinPool.WorkQueue forkjoinpool_workqueue = null;
                Thread thread = Thread.currentThread();

                if (thread instanceof ForkJoinWorkerThread) {
                    ForkJoinWorkerThread forkjoinworkerthread = (ForkJoinWorkerThread) thread;

                    forkjoinpool = forkjoinworkerthread.pool;
                    forkjoinpool_workqueue = forkjoinworkerthread.workQueue;
                    forkjoinpool.helpJoinOnce(forkjoinpool_workqueue, this);
                } else {
                    ForkJoinPool forkjoinpool1;

                    if ((forkjoinpool1 = ForkJoinPool.common) != null) {
                        if (this instanceof CountedCompleter) {
                            forkjoinpool1.externalHelpComplete((CountedCompleter) this);
                        } else if (forkjoinpool1.tryExternalUnpush(this)) {
                            this.doExec();
                        }
                    }
                }

                boolean flag = false;
                boolean flag1 = false;

                try {
                    while (true) {
                        k = this.status;
                        if (this.status < 0) {
                            break;
                        }

                        if (forkjoinpool_workqueue != null && forkjoinpool_workqueue.qlock < 0) {
                            cancelIgnoringExceptions(this);
                        } else if (!flag) {
                            if (forkjoinpool == null || forkjoinpool.tryCompensate(forkjoinpool.ctl)) {
                                flag = true;
                            }
                        } else {
                            long i1;

                            if ((i1 = TimeUnit.NANOSECONDS.toMillis(j)) > 0L && ForkJoinTask.U.compareAndSwapInt(this, ForkJoinTask.STATUS, k, k | 65536)) {
                                synchronized (this) {
                                    if (this.status >= 0) {
                                        try {
                                            this.wait(i1);
                                        } catch (InterruptedException interruptedexception) {
                                            if (forkjoinpool == null) {
                                                flag1 = true;
                                            }
                                        }
                                    } else {
                                        this.notifyAll();
                                    }
                                }
                            }

                            k = this.status;
                            if (this.status < 0 || flag1 || (j = l - System.nanoTime()) <= 0L) {
                                break;
                            }
                        }
                    }
                } finally {
                    if (forkjoinpool != null && flag) {
                        forkjoinpool.incrementActiveCount();
                    }

                }

                if (flag1) {
                    throw new InterruptedException();
                }
            }

            if ((k &= -268435456) != -268435456) {
                if (k == -1073741824) {
                    throw new CancellationException();
                }

                if (k != Integer.MIN_VALUE) {
                    throw new TimeoutException();
                }

                Throwable throwable;

                if ((throwable = this.getThrowableException()) != null) {
                    throw new ExecutionException(throwable);
                }
            }

            return this.getRawResult();
        }
    }

    public final void quietlyJoin() {
        this.doJoin();
    }

    public final void quietlyInvoke() {
        this.doInvoke();
    }

    public static void helpQuiesce() {
        Thread thread;

        if ((thread = Thread.currentThread()) instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread forkjoinworkerthread = (ForkJoinWorkerThread) thread;

            forkjoinworkerthread.pool.helpQuiescePool(forkjoinworkerthread.workQueue);
        } else {
            ForkJoinPool.quiesceCommonPool();
        }

    }

    public void reinitialize() {
        if ((this.status & -268435456) == Integer.MIN_VALUE) {
            this.clearExceptionalCompletion();
        } else {
            this.status = 0;
        }

    }

    public static ForkJoinPool getPool() {
        Thread thread = Thread.currentThread();

        return thread instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread) thread).pool : null;
    }

    public static boolean inForkJoinPool() {
        return Thread.currentThread() instanceof ForkJoinWorkerThread;
    }

    public boolean tryUnfork() {
        Thread thread;

        return (thread = Thread.currentThread()) instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread) thread).workQueue.tryUnpush(this) : ForkJoinPool.common.tryExternalUnpush(this);
    }

    public static int getQueuedTaskCount() {
        ForkJoinPool.WorkQueue forkjoinpool_workqueue;
        Thread thread;

        if ((thread = Thread.currentThread()) instanceof ForkJoinWorkerThread) {
            forkjoinpool_workqueue = ((ForkJoinWorkerThread) thread).workQueue;
        } else {
            forkjoinpool_workqueue = ForkJoinPool.commonSubmitterQueue();
        }

        return forkjoinpool_workqueue == null ? 0 : forkjoinpool_workqueue.queueSize();
    }

    public static int getSurplusQueuedTaskCount() {
        return ForkJoinPool.getSurplusQueuedTaskCount();
    }

    public abstract V getRawResult();

    protected abstract void setRawResult(V v0);

    protected abstract boolean exec();

    protected static ForkJoinTask<?> peekNextLocalTask() {
        ForkJoinPool.WorkQueue forkjoinpool_workqueue;
        Thread thread;

        if ((thread = Thread.currentThread()) instanceof ForkJoinWorkerThread) {
            forkjoinpool_workqueue = ((ForkJoinWorkerThread) thread).workQueue;
        } else {
            forkjoinpool_workqueue = ForkJoinPool.commonSubmitterQueue();
        }

        return forkjoinpool_workqueue == null ? null : forkjoinpool_workqueue.peek();
    }

    protected static ForkJoinTask<?> pollNextLocalTask() {
        Thread thread;

        return (thread = Thread.currentThread()) instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread) thread).workQueue.nextLocalTask() : null;
    }

    protected static ForkJoinTask<?> pollTask() {
        ForkJoinWorkerThread forkjoinworkerthread;
        Thread thread;

        return (thread = Thread.currentThread()) instanceof ForkJoinWorkerThread ? (forkjoinworkerthread = (ForkJoinWorkerThread) thread).pool.nextTaskFor(forkjoinworkerthread.workQueue) : null;
    }

    public final short getForkJoinTaskTag() {
        return (short) this.status;
    }

    public final short setForkJoinTaskTag(short short0) {
        int i;

        do {
            i = this.status;
        } while (!ForkJoinTask.U.compareAndSwapInt(this, ForkJoinTask.STATUS, this.status, i & -65536 | short0 & '\uffff'));

        return (short) i;
    }

    public final boolean compareAndSetForkJoinTaskTag(short short0, short short1) {
        int i;

        do {
            i = this.status;
            if ((short) this.status != short0) {
                return false;
            }
        } while (!ForkJoinTask.U.compareAndSwapInt(this, ForkJoinTask.STATUS, i, i & -65536 | short1 & '\uffff'));

        return true;
    }

    public static ForkJoinTask<?> adapt(Runnable runnable) {
        return new ForkJoinTask.AdaptedRunnableAction(runnable);
    }

    public static <T> ForkJoinTask<T> adapt(Runnable runnable, T t0) {
        return new ForkJoinTask.AdaptedRunnable<>(runnable, t0);
    }

    public static <T> ForkJoinTask<T> adapt(Callable<? extends T> callable) {
        return new ForkJoinTask.AdaptedCallable<>(callable);
    }

    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeObject(this.getException());
    }

    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        Object object = objectinputstream.readObject();

        if (object != null) {
            this.setExceptionalCompletion((Throwable) object);
        }

    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException securityexception) {
            try {
                return (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                    public Unsafe run() throws Exception {
                        Class oclass = Unsafe.class;
                        Field[] afield = oclass.getDeclaredFields();
                        int i = afield.length;

                        for (int j = 0; j < i; ++j) {
                            Field field = afield[j];

                            field.setAccessible(true);
                            Object object = field.get((Object) null);

                            if (oclass.isInstance(object)) {
                                return (Unsafe) oclass.cast(object);
                            }
                        }

                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            } catch (PrivilegedActionException privilegedactionexception) {
                throw new RuntimeException("Could not initialize intrinsics", privilegedactionexception.getCause());
            }
        }
    }

    static {
        try {
            U = getUnsafe();
            Class oclass = ForkJoinTask.class;

            STATUS = ForkJoinTask.U.objectFieldOffset(oclass.getDeclaredField("status"));
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    static final class AdaptedCallable<T> extends ForkJoinTask<T> implements RunnableFuture<T> {

        final Callable<? extends T> callable;
        T result;
        private static final long serialVersionUID = 2838392045355241008L;

        AdaptedCallable(Callable<? extends T> callable) {
            if (callable == null) {
                throw new NullPointerException();
            } else {
                this.callable = callable;
            }
        }

        @Override
        public final T getRawResult() {
            return this.result;
        }

        @Override
        public final void setRawResult(T t0) {
            this.result = t0;
        }

        @Override
        public final boolean exec() {
            try {
                this.result = this.callable.call();
                return true;
            } catch (Error error) {
                throw error;
            } catch (RuntimeException runtimeexception) {
                throw runtimeexception;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        public final void run() {
            this.invoke();
        }
    }

    static final class RunnableExecuteAction extends ForkJoinTask<Void> {

        final Runnable runnable;
        private static final long serialVersionUID = 5232453952276885070L;

        RunnableExecuteAction(Runnable runnable) {
            if (runnable == null) {
                throw new NullPointerException();
            } else {
                this.runnable = runnable;
            }
        }

        @Override
        public final Void getRawResult() {
            return null;
        }

        public final void setRawResult(Void ovoid) {}

        @Override
        public final boolean exec() {
            this.runnable.run();
            return true;
        }

        @Override
        void internalPropagateException(Throwable throwable) {
            rethrow(throwable);
        }
    }

    static final class AdaptedRunnableAction extends ForkJoinTask<Void> implements RunnableFuture<Void> {

        final Runnable runnable;
        private static final long serialVersionUID = 5232453952276885070L;

        AdaptedRunnableAction(Runnable runnable) {
            if (runnable == null) {
                throw new NullPointerException();
            } else {
                this.runnable = runnable;
            }
        }

        @Override
        public final Void getRawResult() {
            return null;
        }

        public final void setRawResult(Void ovoid) {}

        @Override
        public final boolean exec() {
            this.runnable.run();
            return true;
        }

        public final void run() {
            this.invoke();
        }
    }

    static final class AdaptedRunnable<T> extends ForkJoinTask<T> implements RunnableFuture<T> {

        final Runnable runnable;
        T result;
        private static final long serialVersionUID = 5232453952276885070L;

        AdaptedRunnable(Runnable runnable, T t0) {
            if (runnable == null) {
                throw new NullPointerException();
            } else {
                this.runnable = runnable;
                this.result = t0;
            }
        }

        @Override
        public final T getRawResult() {
            return this.result;
        }

        @Override
        public final void setRawResult(T t0) {
            this.result = t0;
        }

        @Override
        public final boolean exec() {
            this.runnable.run();
            return true;
        }

        public final void run() {
            this.invoke();
        }
    }

    static final class ExceptionNode extends WeakReference<ForkJoinTask<?>> {

        final Throwable ex;
        ForkJoinTask.ExceptionNode next;
        final long thrower;

        ExceptionNode(ForkJoinTask<?> forkjointask, Throwable throwable, ForkJoinTask.ExceptionNode forkjointask_exceptionnode) {
            super(forkjointask, ForkJoinTask.exceptionTableRefQueue);
            this.ex = throwable;
            this.next = forkjointask_exceptionnode;
            this.thrower = Thread.currentThread().getId();
        }
    }
}
