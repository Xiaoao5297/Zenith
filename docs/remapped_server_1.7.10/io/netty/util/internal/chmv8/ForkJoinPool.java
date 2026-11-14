package io.netty.util.internal.chmv8;

import io.netty.util.internal.ThreadLocalRandom;
import java.lang.Thread.State;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class ForkJoinPool extends AbstractExecutorService {

    static final ThreadLocal<ForkJoinPool.Submitter> submitters;
    public static final ForkJoinPool.ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
    private static final RuntimePermission modifyThreadPermission;
    static final ForkJoinPool common;
    static final int commonParallelism;
    private static int poolNumberSequence;
    private static final long IDLE_TIMEOUT = 2000000000L;
    private static final long FAST_IDLE_TIMEOUT = 200000000L;
    private static final long TIMEOUT_SLOP = 2000000L;
    private static final int MAX_HELP = 64;
    private static final int SEED_INCREMENT = 1640531527;
    private static final int AC_SHIFT = 48;
    private static final int TC_SHIFT = 32;
    private static final int ST_SHIFT = 31;
    private static final int EC_SHIFT = 16;
    private static final int SMASK = 65535;
    private static final int MAX_CAP = 32767;
    private static final int EVENMASK = 65534;
    private static final int SQMASK = 126;
    private static final int SHORT_SIGN = 32768;
    private static final int INT_SIGN = Integer.MIN_VALUE;
    private static final long STOP_BIT = 2147483648L;
    private static final long AC_MASK = -281474976710656L;
    private static final long TC_MASK = 281470681743360L;
    private static final long TC_UNIT = 4294967296L;
    private static final long AC_UNIT = 281474976710656L;
    private static final int UAC_SHIFT = 16;
    private static final int UTC_SHIFT = 0;
    private static final int UAC_MASK = -65536;
    private static final int UTC_MASK = 65535;
    private static final int UAC_UNIT = 65536;
    private static final int UTC_UNIT = 1;
    private static final int E_MASK = Integer.MAX_VALUE;
    private static final int E_SEQ = 65536;
    private static final int SHUTDOWN = Integer.MIN_VALUE;
    private static final int PL_LOCK = 2;
    private static final int PL_SIGNAL = 1;
    private static final int PL_SPINS = 256;
    static final int LIFO_QUEUE = 0;
    static final int FIFO_QUEUE = 1;
    static final int SHARED_QUEUE = -1;
    volatile long pad00;
    volatile long pad01;
    volatile long pad02;
    volatile long pad03;
    volatile long pad04;
    volatile long pad05;
    volatile long pad06;
    volatile long stealCount;
    volatile long ctl;
    volatile int plock;
    volatile int indexSeed;
    final short parallelism;
    final short mode;
    ForkJoinPool.WorkQueue[] workQueues;
    final ForkJoinPool.ForkJoinWorkerThreadFactory factory;
    final UncaughtExceptionHandler ueh;
    final String workerNamePrefix;
    volatile Object pad10;
    volatile Object pad11;
    volatile Object pad12;
    volatile Object pad13;
    volatile Object pad14;
    volatile Object pad15;
    volatile Object pad16;
    volatile Object pad17;
    volatile Object pad18;
    volatile Object pad19;
    volatile Object pad1a;
    volatile Object pad1b;
    private static final Unsafe U;
    private static final long CTL;
    private static final long PARKBLOCKER;
    private static final int ABASE;
    private static final int ASHIFT;
    private static final long STEALCOUNT;
    private static final long PLOCK;
    private static final long INDEXSEED;
    private static final long QBASE;
    private static final long QLOCK;

    private static void checkPermission() {
        SecurityManager securitymanager = System.getSecurityManager();

        if (securitymanager != null) {
            securitymanager.checkPermission(ForkJoinPool.modifyThreadPermission);
        }

    }

    private static final synchronized int nextPoolId() {
        return ++ForkJoinPool.poolNumberSequence;
    }

    private int acquirePlock() {
        int i = 256;

        while (true) {
            int j = this.plock;
            int k;

            if ((this.plock & 2) == 0 && ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, j, k = j + 2)) {
                return k;
            }

            if (i >= 0) {
                if (ThreadLocalRandom.current().nextInt() >= 0) {
                    --i;
                }
            } else if (ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, j, j | 1)) {
                synchronized (this) {
                    if ((this.plock & 1) != 0) {
                        try {
                            this.wait();
                        } catch (InterruptedException interruptedexception) {
                            try {
                                Thread.currentThread().interrupt();
                            } catch (SecurityException securityexception) {
                                ;
                            }
                        }
                    } else {
                        this.notifyAll();
                    }
                }
            }
        }
    }

    private void releasePlock(int i) {
        this.plock = i;
        synchronized (this) {
            this.notifyAll();
        }
    }

    private void tryAddWorker() {
        while (true) {
            long i = this.ctl;
            int j;
            int k;

            if ((k = (int) (this.ctl >>> 32)) < 0 && (k & '\u8000') != 0 && (j = (int) i) >= 0) {
                long l = (long) (k + 1 & '\uffff' | k + 65536 & -65536) << 32 | (long) j;

                if (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, l)) {
                    continue;
                }

                Throwable throwable = null;
                ForkJoinWorkerThread forkjoinworkerthread = null;

                try {
                    ForkJoinPool.ForkJoinWorkerThreadFactory forkjoinpool_forkjoinworkerthreadfactory = this.factory;

                    if (this.factory != null && (forkjoinworkerthread = forkjoinpool_forkjoinworkerthreadfactory.newThread(this)) != null) {
                        forkjoinworkerthread.start();
                        return;
                    }
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                }

                this.deregisterWorker(forkjoinworkerthread, throwable);
            }

            return;
        }
    }

    final ForkJoinPool.WorkQueue registerWorker(ForkJoinWorkerThread forkjoinworkerthread) {
        forkjoinworkerthread.setDaemon(true);
        UncaughtExceptionHandler uncaughtexceptionhandler = this.ueh;

        if (this.ueh != null) {
            forkjoinworkerthread.setUncaughtExceptionHandler(uncaughtexceptionhandler);
        }

        int i;

        do {
            do {
                i = this.indexSeed;
                i += 1640531527;
            } while (!ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.INDEXSEED, this.indexSeed, i));
        } while (i == 0);

        ForkJoinPool.WorkQueue forkjoinpool_workqueue;
        int j;
        label118:
        {
            forkjoinpool_workqueue = new ForkJoinPool.WorkQueue(this, forkjoinworkerthread, this.mode, i);
            j = this.plock;
            if ((this.plock & 2) == 0) {
                int k = j;

                j += 2;
                if (ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, k, j)) {
                    break label118;
                }
            }

            j = this.acquirePlock();
        }

        int l = j & Integer.MIN_VALUE | j + 2 & Integer.MAX_VALUE;

        try {
            ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

            if (this.workQueues != null) {
                int i1 = aforkjoinpool_workqueue.length;
                int j1 = i1 - 1;
                int k1 = i << 1 | 1;

                if (aforkjoinpool_workqueue[k1 &= j1] != null) {
                    int l1 = 0;
                    int i2 = i1 <= 4 ? 2 : (i1 >>> 1 & '\ufffe') + 2;

                    while (aforkjoinpool_workqueue[k1 = k1 + i2 & j1] != null) {
                        ++l1;
                        if (l1 >= i1) {
                            this.workQueues = aforkjoinpool_workqueue = (ForkJoinPool.WorkQueue[]) Arrays.copyOf(aforkjoinpool_workqueue, i1 <<= 1);
                            j1 = i1 - 1;
                            l1 = 0;
                        }
                    }
                }

                forkjoinpool_workqueue.poolIndex = (short) k1;
                forkjoinpool_workqueue.eventCount = k1;
                aforkjoinpool_workqueue[k1] = forkjoinpool_workqueue;
            }
        } finally {
            if (!ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, j, l)) {
                this.releasePlock(l);
            }

        }

        forkjoinworkerthread.setName(this.workerNamePrefix.concat(Integer.toString(forkjoinpool_workqueue.poolIndex >>> 1)));
        return forkjoinpool_workqueue;
    }

    final void deregisterWorker(ForkJoinWorkerThread forkjoinworkerthread, Throwable throwable) {
        ForkJoinPool.WorkQueue forkjoinpool_workqueue = null;

        if (forkjoinworkerthread != null) {
            forkjoinpool_workqueue = forkjoinworkerthread.workQueue;
            if (forkjoinworkerthread.workQueue != null) {
                forkjoinpool_workqueue.qlock = -1;

                long i;

                do {
                    i = this.stealCount;
                } while (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.STEALCOUNT, this.stealCount, i + (long) forkjoinpool_workqueue.nsteals));

                int j;
                label195:
                {
                    j = this.plock;
                    if ((this.plock & 2) == 0) {
                        int k = j;

                        j += 2;
                        if (ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, k, j)) {
                            break label195;
                        }
                    }

                    j = this.acquirePlock();
                }

                int l = j & Integer.MIN_VALUE | j + 2 & Integer.MAX_VALUE;

                try {
                    short short0 = forkjoinpool_workqueue.poolIndex;
                    ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

                    if (aforkjoinpool_workqueue != null && short0 >= 0 && short0 < aforkjoinpool_workqueue.length && aforkjoinpool_workqueue[short0] == forkjoinpool_workqueue) {
                        aforkjoinpool_workqueue[short0] = null;
                    }
                } finally {
                    if (!ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, j, l)) {
                        this.releasePlock(l);
                    }

                }
            }
        }

        long i1;

        do {
            i1 = this.ctl;
        } while (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, this.ctl, i1 - 281474976710656L & -281474976710656L | i1 - 4294967296L & 281470681743360L | i1 & 4294967295L));

        if (!this.tryTerminate(false, false) && forkjoinpool_workqueue != null && forkjoinpool_workqueue.array != null) {
            forkjoinpool_workqueue.cancelAll();

            while (true) {
                i1 = this.ctl;
                int j1;
                int k1;

                if ((k1 = (int) (this.ctl >>> 32)) >= 0 || (j1 = (int) i1) < 0) {
                    break;
                }

                if (j1 > 0) {
                    ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue1 = this.workQueues;
                    ForkJoinPool.WorkQueue forkjoinpool_workqueue1;
                    int l1;

                    if (this.workQueues == null || (l1 = j1 & '\uffff') >= aforkjoinpool_workqueue1.length || (forkjoinpool_workqueue1 = aforkjoinpool_workqueue1[l1]) == null) {
                        break;
                    }

                    long i2 = (long) (forkjoinpool_workqueue1.nextWait & Integer.MAX_VALUE) | (long) (k1 + 65536) << 32;

                    if (forkjoinpool_workqueue1.eventCount != (j1 | Integer.MIN_VALUE)) {
                        break;
                    }

                    if (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i1, i2)) {
                        continue;
                    }

                    forkjoinpool_workqueue1.eventCount = j1 + 65536 & Integer.MAX_VALUE;
                    Thread thread = forkjoinpool_workqueue1.parker;

                    if (forkjoinpool_workqueue1.parker != null) {
                        ForkJoinPool.U.unpark(thread);
                    }
                    break;
                }

                if ((short) k1 < 0) {
                    this.tryAddWorker();
                }
                break;
            }
        }

        if (throwable == null) {
            ForkJoinTask.helpExpungeStaleExceptions();
        } else {
            ForkJoinTask.rethrow(throwable);
        }

    }

    final void externalPush(ForkJoinTask<?> forkjointask) {
        ForkJoinPool.Submitter forkjoinpool_submitter = (ForkJoinPool.Submitter) ForkJoinPool.submitters.get();
        int i = this.plock;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
        int j;

        if (forkjoinpool_submitter != null && i > 0 && aforkjoinpool_workqueue != null && (j = aforkjoinpool_workqueue.length - 1) >= 0) {
            int k = forkjoinpool_submitter.seed;
            ForkJoinPool.WorkQueue forkjoinpool_workqueue;

            if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[j & forkjoinpool_submitter.seed & 126]) != null && k != 0 && ForkJoinPool.U.compareAndSwapInt(forkjoinpool_workqueue, ForkJoinPool.QLOCK, 0, 1)) {
                ForkJoinTask[] aforkjointask = forkjoinpool_workqueue.array;

                if (forkjoinpool_workqueue.array != null) {
                    int l;
                    int i1 = l = aforkjointask.length - 1;
                    int j1 = forkjoinpool_workqueue.top;
                    int k1;

                    if (i1 > (k1 = forkjoinpool_workqueue.top - forkjoinpool_workqueue.base)) {
                        int l1 = ((l & j1) << ForkJoinPool.ASHIFT) + ForkJoinPool.ABASE;

                        ForkJoinPool.U.putOrderedObject(aforkjointask, (long) l1, forkjointask);
                        forkjoinpool_workqueue.top = j1 + 1;
                        forkjoinpool_workqueue.qlock = 0;
                        if (k1 <= 1) {
                            this.signalWork(aforkjoinpool_workqueue, forkjoinpool_workqueue);
                        }

                        return;
                    }
                }

                forkjoinpool_workqueue.qlock = 0;
            }
        }

        this.fullExternalPush(forkjointask);
    }

    private void fullExternalPush(ForkJoinTask<?> forkjointask) {
        int i = 0;
        ForkJoinPool.Submitter forkjoinpool_submitter = (ForkJoinPool.Submitter) ForkJoinPool.submitters.get();

        while (true) {
            if (forkjoinpool_submitter == null) {
                i = this.indexSeed;
                i += 1640531527;
                if (ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.INDEXSEED, this.indexSeed, i) && i != 0) {
                    ForkJoinPool.submitters.set(forkjoinpool_submitter = new ForkJoinPool.Submitter(i));
                }
            } else if (i == 0) {
                i = forkjoinpool_submitter.seed;
                i ^= i << 13;
                i ^= i >>> 17;
                forkjoinpool_submitter.seed = i ^= i << 5;
            }

            int j = this.plock;

            if (this.plock < 0) {
                throw new RejectedExecutionException();
            }

            ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue;
            int k;
            int l;
            int i1;
            int j1;

            if (j != 0) {
                aforkjoinpool_workqueue = this.workQueues;
                int k1;

                if (this.workQueues != null && (k1 = aforkjoinpool_workqueue.length - 1) >= 0) {
                    ForkJoinPool.WorkQueue forkjoinpool_workqueue;
                    int l1;

                    if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[l1 = i & k1 & 126]) != null) {
                        if (forkjoinpool_workqueue.qlock == 0 && ForkJoinPool.U.compareAndSwapInt(forkjoinpool_workqueue, ForkJoinPool.QLOCK, 0, 1)) {
                            ForkJoinTask[] aforkjointask = forkjoinpool_workqueue.array;

                            k = forkjoinpool_workqueue.top;
                            boolean flag = false;

                            try {
                                if (aforkjointask != null && aforkjointask.length > k + 1 - forkjoinpool_workqueue.base || (aforkjointask = forkjoinpool_workqueue.growArray()) != null) {
                                    l = ((aforkjointask.length - 1 & k) << ForkJoinPool.ASHIFT) + ForkJoinPool.ABASE;
                                    ForkJoinPool.U.putOrderedObject(aforkjointask, (long) l, forkjointask);
                                    forkjoinpool_workqueue.top = k + 1;
                                    flag = true;
                                }
                            } finally {
                                forkjoinpool_workqueue.qlock = 0;
                            }

                            if (flag) {
                                this.signalWork(aforkjoinpool_workqueue, forkjoinpool_workqueue);
                                return;
                            }
                        }

                        i = 0;
                    } else {
                        j = this.plock;
                        if ((this.plock & 2) != 0) {
                            i = 0;
                        } else {
                            label214:
                            {
                                forkjoinpool_workqueue = new ForkJoinPool.WorkQueue(this, (ForkJoinWorkerThread) null, -1, i);
                                forkjoinpool_workqueue.poolIndex = (short) l1;
                                j = this.plock;
                                if ((this.plock & 2) == 0) {
                                    i1 = j;
                                    j += 2;
                                    if (ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, i1, j)) {
                                        break label214;
                                    }
                                }

                                j = this.acquirePlock();
                            }

                            aforkjoinpool_workqueue = this.workQueues;
                            if (this.workQueues != null && l1 < aforkjoinpool_workqueue.length && aforkjoinpool_workqueue[l1] == null) {
                                aforkjoinpool_workqueue[l1] = forkjoinpool_workqueue;
                            }

                            j1 = j & Integer.MIN_VALUE | j + 2 & Integer.MAX_VALUE;
                            if (!ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, j, j1)) {
                                this.releasePlock(j1);
                            }
                        }
                    }
                    continue;
                }
            }

            ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue1;
            label230:
            {
                j1 = this.parallelism;
                k = j1 > 1 ? j1 - 1 : 1;
                k |= k >>> 1;
                k |= k >>> 2;
                k |= k >>> 4;
                k |= k >>> 8;
                k |= k >>> 16;
                k = k + 1 << 1;
                aforkjoinpool_workqueue = this.workQueues;
                aforkjoinpool_workqueue1 = this.workQueues != null && aforkjoinpool_workqueue.length != 0 ? null : new ForkJoinPool.WorkQueue[k];
                j = this.plock;
                if ((this.plock & 2) == 0) {
                    i1 = j;
                    j += 2;
                    if (ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, i1, j)) {
                        break label230;
                    }
                }

                j = this.acquirePlock();
            }

            aforkjoinpool_workqueue = this.workQueues;
            if ((this.workQueues == null || aforkjoinpool_workqueue.length == 0) && aforkjoinpool_workqueue1 != null) {
                this.workQueues = aforkjoinpool_workqueue1;
            }

            l = j & Integer.MIN_VALUE | j + 2 & Integer.MAX_VALUE;
            if (!ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, j, l)) {
                this.releasePlock(l);
            }
        }
    }

    final void incrementActiveCount() {
        long i;

        do {
            i = this.ctl;
        } while (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, this.ctl, i & 281474976710655L | (i & -281474976710656L) + 281474976710656L));

    }

    final void signalWork(ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue, ForkJoinPool.WorkQueue forkjoinpool_workqueue) {
        while (true) {
            long i = this.ctl;
            int j;

            if ((j = (int) (this.ctl >>> 32)) < 0) {
                int k;

                if ((k = (int) i) <= 0) {
                    if ((short) j < 0) {
                        this.tryAddWorker();
                    }
                } else {
                    ForkJoinPool.WorkQueue forkjoinpool_workqueue1;
                    int l;

                    if (aforkjoinpool_workqueue != null && aforkjoinpool_workqueue.length > (l = k & '\uffff') && (forkjoinpool_workqueue1 = aforkjoinpool_workqueue[l]) != null) {
                        long i1 = (long) (forkjoinpool_workqueue1.nextWait & Integer.MAX_VALUE) | (long) (j + 65536) << 32;
                        int j1 = k + 65536 & Integer.MAX_VALUE;

                        if (forkjoinpool_workqueue1.eventCount == (k | Integer.MIN_VALUE) && ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, i1)) {
                            forkjoinpool_workqueue1.eventCount = j1;
                            Thread thread = forkjoinpool_workqueue1.parker;

                            if (forkjoinpool_workqueue1.parker != null) {
                                ForkJoinPool.U.unpark(thread);
                            }
                        } else if (forkjoinpool_workqueue == null || forkjoinpool_workqueue.base < forkjoinpool_workqueue.top) {
                            continue;
                        }
                    }
                }
            }

            return;
        }
    }

    final void runWorker(ForkJoinPool.WorkQueue forkjoinpool_workqueue) {
        forkjoinpool_workqueue.growArray();

        for (int i = forkjoinpool_workqueue.hint; this.scan(forkjoinpool_workqueue, i) == 0; i ^= i << 5) {
            i ^= i << 13;
            i ^= i >>> 17;
        }

    }

    private final int scan(ForkJoinPool.WorkQueue forkjoinpool_workqueue, int i) {
        long j = this.ctl;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
        int k;

        if (this.workQueues != null && (k = aforkjoinpool_workqueue.length - 1) >= 0 && forkjoinpool_workqueue != null) {
            int l = k + k + 1;
            int i1 = forkjoinpool_workqueue.eventCount;

            while (true) {
                ForkJoinPool.WorkQueue forkjoinpool_workqueue1;
                long j1;

                if ((forkjoinpool_workqueue1 = aforkjoinpool_workqueue[i - l & k]) != null) {
                    int k1 = forkjoinpool_workqueue1.base;

                    if (forkjoinpool_workqueue1.base - forkjoinpool_workqueue1.top < 0) {
                        ForkJoinTask[] aforkjointask = forkjoinpool_workqueue1.array;

                        if (forkjoinpool_workqueue1.array != null) {
                            j1 = (long) (((aforkjointask.length - 1 & k1) << ForkJoinPool.ASHIFT) + ForkJoinPool.ABASE);
                            ForkJoinTask forkjointask;

                            if ((forkjointask = (ForkJoinTask) ForkJoinPool.U.getObjectVolatile(aforkjointask, j1)) != null) {
                                if (i1 < 0) {
                                    this.helpRelease(j, aforkjoinpool_workqueue, forkjoinpool_workqueue, forkjoinpool_workqueue1, k1);
                                } else if (forkjoinpool_workqueue1.base == k1 && ForkJoinPool.U.compareAndSwapObject(aforkjointask, j1, forkjointask, (Object) null)) {
                                    ForkJoinPool.U.putOrderedInt(forkjoinpool_workqueue1, ForkJoinPool.QBASE, k1 + 1);
                                    if (k1 + 1 - forkjoinpool_workqueue1.top < 0) {
                                        this.signalWork(aforkjoinpool_workqueue, forkjoinpool_workqueue1);
                                    }

                                    forkjoinpool_workqueue.runTask(forkjointask);
                                }
                            }
                            break;
                        }
                    }
                }

                --l;
                if (l < 0) {
                    int l1;

                    if ((i1 | (l1 = (int) j)) < 0) {
                        return this.awaitWork(forkjoinpool_workqueue, j, i1);
                    }

                    if (this.ctl == j) {
                        j1 = (long) i1 | j - 281474976710656L & -4294967296L;
                        forkjoinpool_workqueue.nextWait = l1;
                        forkjoinpool_workqueue.eventCount = i1 | Integer.MIN_VALUE;
                        if (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, j, j1)) {
                            forkjoinpool_workqueue.eventCount = i1;
                        }
                    }
                    break;
                }
            }
        }

        return 0;
    }

    private final int awaitWork(ForkJoinPool.WorkQueue forkjoinpool_workqueue, long i, int j) {
        int k = forkjoinpool_workqueue.qlock;

        if (forkjoinpool_workqueue.qlock >= 0 && forkjoinpool_workqueue.eventCount == j && this.ctl == i && !Thread.interrupted()) {
            int l = (int) i;
            int i1 = (int) (i >>> 32);
            int j1 = (i1 >> 16) + this.parallelism;

            if (l >= 0 && (j1 > 0 || !this.tryTerminate(false, false))) {
                int k1 = forkjoinpool_workqueue.nsteals;
                long l1;

                if (forkjoinpool_workqueue.nsteals != 0) {
                    forkjoinpool_workqueue.nsteals = 0;

                    do {
                        l1 = this.stealCount;
                    } while (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.STEALCOUNT, this.stealCount, l1 + (long) k1));
                } else {
                    l1 = j1 <= 0 && j == (l | Integer.MIN_VALUE) ? (long) (forkjoinpool_workqueue.nextWait & Integer.MAX_VALUE) | (long) (i1 + 65536) << 32 : 0L;
                    long i2;
                    long j2;

                    if (l1 != 0L) {
                        int k2 = -((short) ((int) (i >>> 32)));

                        i2 = k2 < 0 ? 200000000L : (long) (k2 + 1) * 2000000000L;
                        j2 = System.nanoTime() + i2 - 2000000L;
                    } else {
                        j2 = 0L;
                        i2 = 0L;
                    }

                    if (forkjoinpool_workqueue.eventCount == j && this.ctl == i) {
                        Thread thread = Thread.currentThread();

                        ForkJoinPool.U.putObject(thread, ForkJoinPool.PARKBLOCKER, this);
                        forkjoinpool_workqueue.parker = thread;
                        if (forkjoinpool_workqueue.eventCount == j && this.ctl == i) {
                            ForkJoinPool.U.park(false, i2);
                        }

                        forkjoinpool_workqueue.parker = null;
                        ForkJoinPool.U.putObject(thread, ForkJoinPool.PARKBLOCKER, (Object) null);
                        if (i2 != 0L && this.ctl == i && j2 - System.nanoTime() <= 0L && ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, l1)) {
                            k = forkjoinpool_workqueue.qlock = -1;
                        }
                    }
                }
            } else {
                k = forkjoinpool_workqueue.qlock = -1;
            }
        }

        return k;
    }

    private final void helpRelease(long i, ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue, ForkJoinPool.WorkQueue forkjoinpool_workqueue, ForkJoinPool.WorkQueue forkjoinpool_workqueue1, int j) {
        ForkJoinPool.WorkQueue forkjoinpool_workqueue2;
        int k;
        int l;

        if (forkjoinpool_workqueue != null && forkjoinpool_workqueue.eventCount < 0 && (k = (int) i) > 0 && aforkjoinpool_workqueue != null && aforkjoinpool_workqueue.length > (l = k & '\uffff') && (forkjoinpool_workqueue2 = aforkjoinpool_workqueue[l]) != null && this.ctl == i) {
            long i1 = (long) (forkjoinpool_workqueue2.nextWait & Integer.MAX_VALUE) | (long) ((int) (i >>> 32) + 65536) << 32;
            int j1 = k + 65536 & Integer.MAX_VALUE;

            if (forkjoinpool_workqueue1 != null && forkjoinpool_workqueue1.base == j && forkjoinpool_workqueue.eventCount < 0 && forkjoinpool_workqueue2.eventCount == (k | Integer.MIN_VALUE) && ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, i1)) {
                forkjoinpool_workqueue2.eventCount = j1;
                Thread thread = forkjoinpool_workqueue2.parker;

                if (forkjoinpool_workqueue2.parker != null) {
                    ForkJoinPool.U.unpark(thread);
                }
            }
        }

    }

    private int tryHelpStealer(ForkJoinPool.WorkQueue forkjoinpool_workqueue, ForkJoinTask<?> forkjointask) {
        int i = 0;
        int j = 0;

        if (forkjointask != null && forkjoinpool_workqueue != null && forkjoinpool_workqueue.base - forkjoinpool_workqueue.top >= 0) {
            label120:
            while (true) {
                ForkJoinTask forkjointask1 = forkjointask;
                ForkJoinPool.WorkQueue forkjoinpool_workqueue1 = forkjoinpool_workqueue;

                while (true) {
                    int k = forkjointask.status;

                    if (forkjointask.status < 0) {
                        i = k;
                        return i;
                    }

                    ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
                    int l;

                    if (this.workQueues == null || (l = aforkjoinpool_workqueue.length - 1) <= 0) {
                        return i;
                    }

                    int i1;
                    ForkJoinPool.WorkQueue forkjoinpool_workqueue2;

                    if ((forkjoinpool_workqueue2 = aforkjoinpool_workqueue[i1 = (forkjoinpool_workqueue1.hint | 1) & l]) == null || forkjoinpool_workqueue2.currentSteal != forkjointask1) {
                        int j1 = i1;

                        while (true) {
                            if (((i1 = i1 + 2 & l) & 15) == 1 && (forkjointask1.status < 0 || forkjoinpool_workqueue1.currentJoin != forkjointask1)) {
                                continue label120;
                            }

                            if ((forkjoinpool_workqueue2 = aforkjoinpool_workqueue[i1]) != null && forkjoinpool_workqueue2.currentSteal == forkjointask1) {
                                forkjoinpool_workqueue1.hint = i1;
                                break;
                            }

                            if (i1 == j1) {
                                return i;
                            }
                        }
                    }

                    while (true) {
                        if (forkjointask1.status < 0) {
                            continue label120;
                        }

                        int k1 = forkjoinpool_workqueue2.base;

                        if (forkjoinpool_workqueue2.base - forkjoinpool_workqueue2.top >= 0) {
                            break;
                        }

                        ForkJoinTask[] aforkjointask = forkjoinpool_workqueue2.array;

                        if (forkjoinpool_workqueue2.array == null) {
                            break;
                        }

                        int l1 = ((aforkjointask.length - 1 & k1) << ForkJoinPool.ASHIFT) + ForkJoinPool.ABASE;
                        ForkJoinTask forkjointask2 = (ForkJoinTask) ForkJoinPool.U.getObjectVolatile(aforkjointask, (long) l1);

                        if (forkjointask1.status < 0 || forkjoinpool_workqueue1.currentJoin != forkjointask1 || forkjoinpool_workqueue2.currentSteal != forkjointask1) {
                            continue label120;
                        }

                        i = 1;
                        if (forkjoinpool_workqueue2.base == k1) {
                            if (forkjointask2 == null) {
                                return i;
                            }

                            if (ForkJoinPool.U.compareAndSwapObject(aforkjointask, (long) l1, forkjointask2, (Object) null)) {
                                ForkJoinPool.U.putOrderedInt(forkjoinpool_workqueue2, ForkJoinPool.QBASE, k1 + 1);
                                ForkJoinTask forkjointask3 = forkjoinpool_workqueue.currentSteal;
                                int i2 = forkjoinpool_workqueue.top;

                                do {
                                    forkjoinpool_workqueue.currentSteal = forkjointask2;
                                    forkjointask2.doExec();
                                } while (forkjointask.status >= 0 && forkjoinpool_workqueue.top != i2 && (forkjointask2 = forkjoinpool_workqueue.pop()) != null);

                                forkjoinpool_workqueue.currentSteal = forkjointask3;
                                return i;
                            }
                        }
                    }

                    ForkJoinTask forkjointask4 = forkjoinpool_workqueue2.currentJoin;

                    if (forkjointask1.status < 0 || forkjoinpool_workqueue1.currentJoin != forkjointask1 || forkjoinpool_workqueue2.currentSteal != forkjointask1) {
                        break;
                    }

                    if (forkjointask4 == null) {
                        return i;
                    }

                    ++j;
                    if (j == 64) {
                        return i;
                    }

                    forkjointask1 = forkjointask4;
                    forkjoinpool_workqueue1 = forkjoinpool_workqueue2;
                }
            }
        } else {
            return i;
        }
    }

    private int helpComplete(ForkJoinPool.WorkQueue forkjoinpool_workqueue, CountedCompleter<?> countedcompleter) {
        int i = 0;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
        int j;

        if (this.workQueues != null && (j = aforkjoinpool_workqueue.length - 1) >= 0 && forkjoinpool_workqueue != null && countedcompleter != null) {
            int k = forkjoinpool_workqueue.poolIndex;
            int l = j + j + 1;
            long i1 = 0L;
            int j1 = l;

            while (true) {
                i = countedcompleter.status;
                if (countedcompleter.status < 0) {
                    break;
                }

                if (forkjoinpool_workqueue.internalPopAndExecCC(countedcompleter)) {
                    j1 = l;
                } else {
                    i = countedcompleter.status;
                    if (countedcompleter.status < 0) {
                        break;
                    }

                    ForkJoinPool.WorkQueue forkjoinpool_workqueue1;

                    if ((forkjoinpool_workqueue1 = aforkjoinpool_workqueue[k & j]) != null && forkjoinpool_workqueue1.pollAndExecCC(countedcompleter)) {
                        j1 = l;
                    } else {
                        --j1;
                        if (j1 < 0) {
                            long k1 = i1;

                            i1 = this.ctl;
                            if (k1 == this.ctl) {
                                break;
                            }

                            j1 = l;
                        }
                    }
                }

                k += 2;
            }
        }

        return i;
    }

    final boolean tryCompensate(long i) {
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
        short short0 = this.parallelism;
        int j = (int) i;
        int k;

        if (aforkjoinpool_workqueue != null && (k = aforkjoinpool_workqueue.length - 1) >= 0 && j >= 0 && this.ctl == i) {
            ForkJoinPool.WorkQueue forkjoinpool_workqueue = aforkjoinpool_workqueue[j & k];

            if (j != 0 && forkjoinpool_workqueue != null) {
                long l = (long) (forkjoinpool_workqueue.nextWait & Integer.MAX_VALUE) | i & -4294967296L;
                int i1 = j + 65536 & Integer.MAX_VALUE;

                if (forkjoinpool_workqueue.eventCount == (j | Integer.MIN_VALUE) && ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, l)) {
                    forkjoinpool_workqueue.eventCount = i1;
                    Thread thread = forkjoinpool_workqueue.parker;

                    if (forkjoinpool_workqueue.parker != null) {
                        ForkJoinPool.U.unpark(thread);
                    }

                    return true;
                }
            } else {
                long j1;
                short short1;

                if ((short1 = (short) ((int) (i >>> 32))) >= 0 && (int) (i >> 48) + short0 > 1) {
                    j1 = i - 281474976710656L & -281474976710656L | i & 281474976710655L;
                    if (ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, j1)) {
                        return true;
                    }
                } else if (short1 + short0 < 32767) {
                    j1 = i + 4294967296L & 281470681743360L | i & -281470681743361L;
                    if (ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, j1)) {
                        Throwable throwable = null;
                        ForkJoinWorkerThread forkjoinworkerthread = null;

                        try {
                            ForkJoinPool.ForkJoinWorkerThreadFactory forkjoinpool_forkjoinworkerthreadfactory = this.factory;

                            if (this.factory != null && (forkjoinworkerthread = forkjoinpool_forkjoinworkerthreadfactory.newThread(this)) != null) {
                                forkjoinworkerthread.start();
                                return true;
                            }
                        } catch (Throwable throwable1) {
                            throwable = throwable1;
                        }

                        this.deregisterWorker(forkjoinworkerthread, throwable);
                    }
                }
            }
        }

        return false;
    }

    final int awaitJoin(ForkJoinPool.WorkQueue forkjoinpool_workqueue, ForkJoinTask<?> forkjointask) {
        int i = 0;

        if (forkjointask != null) {
            i = forkjointask.status;
            if (forkjointask.status >= 0 && forkjoinpool_workqueue != null) {
                ForkJoinTask forkjointask1 = forkjoinpool_workqueue.currentJoin;

                forkjoinpool_workqueue.currentJoin = forkjointask;

                while (forkjoinpool_workqueue.tryRemoveAndExec(forkjointask)) {
                    i = forkjointask.status;
                    if (forkjointask.status < 0) {
                        break;
                    }
                }

                if (i >= 0 && forkjointask instanceof CountedCompleter) {
                    i = this.helpComplete(forkjoinpool_workqueue, (CountedCompleter) forkjointask);
                }

                long j = 0L;

                while (i >= 0) {
                    i = forkjointask.status;
                    if (forkjointask.status < 0) {
                        break;
                    }

                    if ((i = this.tryHelpStealer(forkjoinpool_workqueue, forkjointask)) == 0) {
                        i = forkjointask.status;
                        if (forkjointask.status >= 0) {
                            if (!this.tryCompensate(j)) {
                                j = this.ctl;
                            } else {
                                if (forkjointask.trySetSignal()) {
                                    i = forkjointask.status;
                                    if (forkjointask.status >= 0) {
                                        synchronized (forkjointask) {
                                            if (forkjointask.status >= 0) {
                                                try {
                                                    forkjointask.wait();
                                                } catch (InterruptedException interruptedexception) {
                                                    ;
                                                }
                                            } else {
                                                forkjointask.notifyAll();
                                            }
                                        }
                                    }
                                }

                                while (true) {
                                    long k = this.ctl;

                                    if (ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, this.ctl, k & 281474976710655L | (k & -281474976710656L) + 281474976710656L)) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                forkjoinpool_workqueue.currentJoin = forkjointask1;
            }
        }

        return i;
    }

    final void helpJoinOnce(ForkJoinPool.WorkQueue forkjoinpool_workqueue, ForkJoinTask<?> forkjointask) {
        if (forkjoinpool_workqueue != null && forkjointask != null) {
            int i = forkjointask.status;

            if (forkjointask.status >= 0) {
                ForkJoinTask forkjointask1 = forkjoinpool_workqueue.currentJoin;

                forkjoinpool_workqueue.currentJoin = forkjointask;

                while (forkjoinpool_workqueue.tryRemoveAndExec(forkjointask)) {
                    i = forkjointask.status;
                    if (forkjointask.status < 0) {
                        break;
                    }
                }

                if (i >= 0) {
                    if (forkjointask instanceof CountedCompleter) {
                        this.helpComplete(forkjoinpool_workqueue, (CountedCompleter) forkjointask);
                    }

                    while (forkjointask.status >= 0 && this.tryHelpStealer(forkjoinpool_workqueue, forkjointask) > 0) {
                        ;
                    }
                }

                forkjoinpool_workqueue.currentJoin = forkjointask1;
            }
        }

    }

    private ForkJoinPool.WorkQueue findNonEmptyStealQueue() {
        int i = ThreadLocalRandom.current().nextInt();

        int j;

        do {
            j = this.plock;
            ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
            int k;

            if (this.workQueues != null && (k = aforkjoinpool_workqueue.length - 1) >= 0) {
                for (int l = k + 1 << 2; l >= 0; --l) {
                    ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                    if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[(i - l << 1 | 1) & k]) != null && forkjoinpool_workqueue.base - forkjoinpool_workqueue.top < 0) {
                        return forkjoinpool_workqueue;
                    }
                }
            }
        } while (this.plock != j);

        return null;
    }

    final void helpQuiescePool(ForkJoinPool.WorkQueue forkjoinpool_workqueue) {
        ForkJoinTask forkjointask = forkjoinpool_workqueue.currentSteal;
        boolean flag = true;

        while (true) {
            while (true) {
                ForkJoinTask forkjointask1;

                while ((forkjointask1 = forkjoinpool_workqueue.nextLocalTask()) != null) {
                    forkjointask1.doExec();
                }

                long i;
                ForkJoinPool.WorkQueue forkjoinpool_workqueue1;

                if ((forkjoinpool_workqueue1 = this.findNonEmptyStealQueue()) == null) {
                    if (flag) {
                        i = this.ctl;
                        long j = this.ctl & 281474976710655L | (i & -281474976710656L) - 281474976710656L;

                        if ((int) (j >> 48) + this.parallelism == 0) {
                            return;
                        }

                        if (ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, j)) {
                            flag = false;
                        }
                    } else {
                        i = this.ctl;
                        if ((int) (this.ctl >> 48) + this.parallelism <= 0 && ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i, i & 281474976710655L | (i & -281474976710656L) + 281474976710656L)) {
                            return;
                        }
                    }
                } else {
                    if (!flag) {
                        flag = true;

                        do {
                            i = this.ctl;
                        } while (!ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, this.ctl, i & 281474976710655L | (i & -281474976710656L) + 281474976710656L));
                    }

                    int k = forkjoinpool_workqueue1.base;

                    if (forkjoinpool_workqueue1.base - forkjoinpool_workqueue1.top < 0 && (forkjointask1 = forkjoinpool_workqueue1.pollAt(k)) != null) {
                        (forkjoinpool_workqueue.currentSteal = forkjointask1).doExec();
                        forkjoinpool_workqueue.currentSteal = forkjointask;
                    }
                }
            }
        }
    }

    final ForkJoinTask<?> nextTaskFor(ForkJoinPool.WorkQueue forkjoinpool_workqueue) {
        ForkJoinTask forkjointask;
        ForkJoinPool.WorkQueue forkjoinpool_workqueue1;
        int i;

        do {
            if ((forkjointask = forkjoinpool_workqueue.nextLocalTask()) != null) {
                return forkjointask;
            }

            if ((forkjoinpool_workqueue1 = this.findNonEmptyStealQueue()) == null) {
                return null;
            }

            i = forkjoinpool_workqueue1.base;
        } while (forkjoinpool_workqueue1.base - forkjoinpool_workqueue1.top >= 0 || (forkjointask = forkjoinpool_workqueue1.pollAt(i)) == null);

        return forkjointask;
    }

    static int getSurplusQueuedTaskCount() {
        Thread thread;

        if ((thread = Thread.currentThread()) instanceof ForkJoinWorkerThread) {
            ForkJoinPool forkjoinpool;
            ForkJoinWorkerThread forkjoinworkerthread;
            int i = (forkjoinpool = (forkjoinworkerthread = (ForkJoinWorkerThread) thread).pool).parallelism;
            ForkJoinPool.WorkQueue forkjoinpool_workqueue = forkjoinworkerthread.workQueue;
            int j = forkjoinworkerthread.workQueue.top - forkjoinpool_workqueue.base;
            int k = (int) (forkjoinpool.ctl >> 48) + i;

            return j - (k > (i >>>= 1) ? 0 : (k > (i >>>= 1) ? 1 : (k > (i >>>= 1) ? 2 : (k > (i >>>= 1) ? 4 : 8))));
        } else {
            return 0;
        }
    }

    private boolean tryTerminate(boolean flag, boolean flag1) {
        if (this == ForkJoinPool.common) {
            return false;
        } else {
            int i = this.plock;

            if (this.plock >= 0) {
                if (!flag1) {
                    return false;
                }

                label130:
                {
                    if ((i & 2) == 0) {
                        int j = i;

                        i += 2;
                        if (ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, j, i)) {
                            break label130;
                        }
                    }

                    i = this.acquirePlock();
                }

                int k = i + 2 & Integer.MAX_VALUE | Integer.MIN_VALUE;

                if (!ForkJoinPool.U.compareAndSwapInt(this, ForkJoinPool.PLOCK, i, k)) {
                    this.releasePlock(k);
                }
            }

            while (true) {
                long l = this.ctl;

                if ((this.ctl & 2147483648L) != 0L) {
                    if ((short) ((int) (l >>> 32)) + this.parallelism <= 0) {
                        synchronized (this) {
                            this.notifyAll();
                        }
                    }

                    return true;
                }

                if (!flag) {
                    if ((int) (l >> 48) + this.parallelism > 0) {
                        return false;
                    }

                    ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

                    if (this.workQueues != null) {
                        for (int i1 = 0; i1 < aforkjoinpool_workqueue.length; ++i1) {
                            ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                            if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[i1]) != null && (!forkjoinpool_workqueue.isEmpty() || (i1 & 1) != 0 && forkjoinpool_workqueue.eventCount >= 0)) {
                                this.signalWork(aforkjoinpool_workqueue, forkjoinpool_workqueue);
                                return false;
                            }
                        }
                    }
                }

                if (ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, l, l | 2147483648L)) {
                    for (int j1 = 0; j1 < 3; ++j1) {
                        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue1 = this.workQueues;

                        if (this.workQueues != null) {
                            int k1 = aforkjoinpool_workqueue1.length;

                            ForkJoinPool.WorkQueue forkjoinpool_workqueue1;
                            int l1;

                            for (l1 = 0; l1 < k1; ++l1) {
                                if ((forkjoinpool_workqueue1 = aforkjoinpool_workqueue1[l1]) != null) {
                                    forkjoinpool_workqueue1.qlock = -1;
                                    if (j1 > 0) {
                                        forkjoinpool_workqueue1.cancelAll();
                                        if (j1 > 1) {
                                            ForkJoinWorkerThread forkjoinworkerthread = forkjoinpool_workqueue1.owner;

                                            if (forkjoinpool_workqueue1.owner != null) {
                                                if (!forkjoinworkerthread.isInterrupted()) {
                                                    try {
                                                        forkjoinworkerthread.interrupt();
                                                    } catch (Throwable throwable) {
                                                        ;
                                                    }
                                                }

                                                ForkJoinPool.U.unpark(forkjoinworkerthread);
                                            }
                                        }
                                    }
                                }
                            }

                            while (true) {
                                long i2 = this.ctl;
                                int j2;

                                if ((j2 = (int) this.ctl & Integer.MAX_VALUE) == 0 || (l1 = j2 & '\uffff') >= k1 || l1 < 0 || (forkjoinpool_workqueue1 = aforkjoinpool_workqueue1[l1]) == null) {
                                    break;
                                }

                                long k2 = (long) (forkjoinpool_workqueue1.nextWait & Integer.MAX_VALUE) | i2 + 281474976710656L & -281474976710656L | i2 & 281472829227008L;

                                if (forkjoinpool_workqueue1.eventCount == (j2 | Integer.MIN_VALUE) && ForkJoinPool.U.compareAndSwapLong(this, ForkJoinPool.CTL, i2, k2)) {
                                    forkjoinpool_workqueue1.eventCount = j2 + 65536 & Integer.MAX_VALUE;
                                    forkjoinpool_workqueue1.qlock = -1;
                                    Thread thread = forkjoinpool_workqueue1.parker;

                                    if (forkjoinpool_workqueue1.parker != null) {
                                        ForkJoinPool.U.unpark(thread);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static ForkJoinPool.WorkQueue commonSubmitterQueue() {
        ForkJoinPool forkjoinpool;
        ForkJoinPool.WorkQueue forkjoinpool_workqueue;
        ForkJoinPool.Submitter forkjoinpool_submitter;

        if ((forkjoinpool_submitter = (ForkJoinPool.Submitter) ForkJoinPool.submitters.get()) != null && (forkjoinpool = ForkJoinPool.common) != null) {
            ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = forkjoinpool.workQueues;
            int i;

            if (forkjoinpool.workQueues != null && (i = aforkjoinpool_workqueue.length - 1) >= 0) {
                forkjoinpool_workqueue = aforkjoinpool_workqueue[i & forkjoinpool_submitter.seed & 126];
                return forkjoinpool_workqueue;
            }
        }

        forkjoinpool_workqueue = null;
        return forkjoinpool_workqueue;
    }

    final boolean tryExternalUnpush(ForkJoinTask<?> forkjointask) {
        ForkJoinPool.Submitter forkjoinpool_submitter = (ForkJoinPool.Submitter) ForkJoinPool.submitters.get();
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
        boolean flag = false;
        ForkJoinPool.WorkQueue forkjoinpool_workqueue;
        int i;

        if (forkjoinpool_submitter != null && aforkjoinpool_workqueue != null && (i = aforkjoinpool_workqueue.length - 1) >= 0 && (forkjoinpool_workqueue = aforkjoinpool_workqueue[forkjoinpool_submitter.seed & i & 126]) != null) {
            int j = forkjoinpool_workqueue.top;

            if (forkjoinpool_workqueue.base != forkjoinpool_workqueue.top) {
                ForkJoinTask[] aforkjointask = forkjoinpool_workqueue.array;

                if (forkjoinpool_workqueue.array != null) {
                    long k = (long) (((aforkjointask.length - 1 & j - 1) << ForkJoinPool.ASHIFT) + ForkJoinPool.ABASE);

                    if (ForkJoinPool.U.getObject(aforkjointask, k) == forkjointask && ForkJoinPool.U.compareAndSwapInt(forkjoinpool_workqueue, ForkJoinPool.QLOCK, 0, 1)) {
                        if (forkjoinpool_workqueue.top == j && forkjoinpool_workqueue.array == aforkjointask && ForkJoinPool.U.compareAndSwapObject(aforkjointask, k, forkjointask, (Object) null)) {
                            forkjoinpool_workqueue.top = j - 1;
                            flag = true;
                        }

                        forkjoinpool_workqueue.qlock = 0;
                    }
                }
            }
        }

        return flag;
    }

    final int externalHelpComplete(CountedCompleter<?> countedcompleter) {
        ForkJoinPool.Submitter forkjoinpool_submitter = (ForkJoinPool.Submitter) ForkJoinPool.submitters.get();
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
        int i = 0;
        int j;

        if (forkjoinpool_submitter != null && aforkjoinpool_workqueue != null && (j = aforkjoinpool_workqueue.length - 1) >= 0) {
            int k = forkjoinpool_submitter.seed;
            ForkJoinPool.WorkQueue forkjoinpool_workqueue;

            if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[forkjoinpool_submitter.seed & j & 126]) != null && countedcompleter != null) {
                int l = j + j + 1;
                long i1 = 0L;

                k |= 1;
                int j1 = l;

                while (true) {
                    i = countedcompleter.status;
                    if (countedcompleter.status < 0) {
                        break;
                    }

                    if (forkjoinpool_workqueue.externalPopAndExecCC(countedcompleter)) {
                        j1 = l;
                    } else {
                        i = countedcompleter.status;
                        if (countedcompleter.status < 0) {
                            break;
                        }

                        ForkJoinPool.WorkQueue forkjoinpool_workqueue1;

                        if ((forkjoinpool_workqueue1 = aforkjoinpool_workqueue[k & j]) != null && forkjoinpool_workqueue1.pollAndExecCC(countedcompleter)) {
                            j1 = l;
                        } else {
                            --j1;
                            if (j1 < 0) {
                                long k1 = i1;

                                i1 = this.ctl;
                                if (k1 == this.ctl) {
                                    break;
                                }

                                j1 = l;
                            }
                        }
                    }

                    k += 2;
                }
            }
        }

        return i;
    }

    public ForkJoinPool() {
        this(Math.min(32767, Runtime.getRuntime().availableProcessors()), ForkJoinPool.defaultForkJoinWorkerThreadFactory, (UncaughtExceptionHandler) null, false);
    }

    public ForkJoinPool(int i) {
        this(i, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (UncaughtExceptionHandler) null, false);
    }

    public ForkJoinPool(int i, ForkJoinPool.ForkJoinWorkerThreadFactory forkjoinpool_forkjoinworkerthreadfactory, UncaughtExceptionHandler uncaughtexceptionhandler, boolean flag) {
        this(checkParallelism(i), checkFactory(forkjoinpool_forkjoinworkerthreadfactory), uncaughtexceptionhandler, flag ? 1 : 0, "ForkJoinPool-" + nextPoolId() + "-worker-");
        checkPermission();
    }

    private static int checkParallelism(int i) {
        if (i > 0 && i <= 32767) {
            return i;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static ForkJoinPool.ForkJoinWorkerThreadFactory checkFactory(ForkJoinPool.ForkJoinWorkerThreadFactory forkjoinpool_forkjoinworkerthreadfactory) {
        if (forkjoinpool_forkjoinworkerthreadfactory == null) {
            throw new NullPointerException();
        } else {
            return forkjoinpool_forkjoinworkerthreadfactory;
        }
    }

    private ForkJoinPool(int i, ForkJoinPool.ForkJoinWorkerThreadFactory forkjoinpool_forkjoinworkerthreadfactory, UncaughtExceptionHandler uncaughtexceptionhandler, int j, String s) {
        this.workerNamePrefix = s;
        this.factory = forkjoinpool_forkjoinworkerthreadfactory;
        this.ueh = uncaughtexceptionhandler;
        this.mode = (short) j;
        this.parallelism = (short) i;
        long k = (long) (-i);

        this.ctl = k << 48 & -281474976710656L | k << 32 & 281470681743360L;
    }

    public static ForkJoinPool commonPool() {
        return ForkJoinPool.common;
    }

    public <T> T invoke(ForkJoinTask<T> forkjointask) {
        if (forkjointask == null) {
            throw new NullPointerException();
        } else {
            this.externalPush(forkjointask);
            return forkjointask.join();
        }
    }

    public void execute(ForkJoinTask<?> forkjointask) {
        if (forkjointask == null) {
            throw new NullPointerException();
        } else {
            this.externalPush(forkjointask);
        }
    }

    public void execute(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException();
        } else {
            Object object;

            if (runnable instanceof ForkJoinTask) {
                object = (ForkJoinTask) runnable;
            } else {
                object = new ForkJoinTask.RunnableExecuteAction(runnable);
            }

            this.externalPush((ForkJoinTask) object);
        }
    }

    public <T> ForkJoinTask<T> submit(ForkJoinTask<T> forkjointask) {
        if (forkjointask == null) {
            throw new NullPointerException();
        } else {
            this.externalPush(forkjointask);
            return forkjointask;
        }
    }

    public <T> ForkJoinTask<T> submit(Callable<T> callable) {
        ForkJoinTask.AdaptedCallable forkjointask_adaptedcallable = new ForkJoinTask.AdaptedCallable<>(callable);

        this.externalPush(forkjointask_adaptedcallable);
        return forkjointask_adaptedcallable;
    }

    public <T> ForkJoinTask<T> submit(Runnable runnable, T t0) {
        ForkJoinTask.AdaptedRunnable forkjointask_adaptedrunnable = new ForkJoinTask.AdaptedRunnable<>(runnable, t0);

        this.externalPush(forkjointask_adaptedrunnable);
        return forkjointask_adaptedrunnable;
    }

    public ForkJoinTask<?> submit(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException();
        } else {
            Object object;

            if (runnable instanceof ForkJoinTask) {
                object = (ForkJoinTask) runnable;
            } else {
                object = new ForkJoinTask.AdaptedRunnableAction(runnable);
            }

            this.externalPush((ForkJoinTask) object);
            return (ForkJoinTask) object;
        }
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) {
        ArrayList arraylist = new ArrayList(collection.size());
        boolean flag = false;
        boolean flag1 = false;

        int i;
        ArrayList arraylist1;

        try {
            flag1 = true;
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                Callable callable = (Callable) iterator.next();
                ForkJoinTask.AdaptedCallable forkjointask_adaptedcallable = new ForkJoinTask.AdaptedCallable<>(callable);

                arraylist.add(forkjointask_adaptedcallable);
                this.externalPush(forkjointask_adaptedcallable);
            }

            int j = 0;

            for (i = arraylist.size(); j < i; ++j) {
                ((ForkJoinTask) arraylist.get(j)).quietlyJoin();
            }

            flag = true;
            arraylist1 = arraylist;
            flag1 = false;
        } finally {
            if (flag1) {
                if (!flag) {
                    int k = 0;

                    for (int l = arraylist.size(); k < l; ++k) {
                        ((Future) arraylist.get(k)).cancel(false);
                    }
                }

            }
        }

        if (!flag) {
            i = 0;

            for (int i1 = arraylist.size(); i < i1; ++i) {
                ((Future) arraylist.get(i)).cancel(false);
            }
        }

        return arraylist1;
    }

    public ForkJoinPool.ForkJoinWorkerThreadFactory getFactory() {
        return this.factory;
    }

    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.ueh;
    }

    public int getParallelism() {
        short short0 = this.parallelism;

        return this.parallelism > 0 ? short0 : 1;
    }

    public static int getCommonPoolParallelism() {
        return ForkJoinPool.commonParallelism;
    }

    public int getPoolSize() {
        return this.parallelism + (short) ((int) (this.ctl >>> 32));
    }

    public boolean getAsyncMode() {
        return this.mode == 1;
    }

    public int getRunningThreadCount() {
        int i = 0;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

        if (this.workQueues != null) {
            for (int j = 1; j < aforkjoinpool_workqueue.length; j += 2) {
                ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[j]) != null && forkjoinpool_workqueue.isApparentlyUnblocked()) {
                    ++i;
                }
            }
        }

        return i;
    }

    public int getActiveThreadCount() {
        int i = this.parallelism + (int) (this.ctl >> 48);

        return i <= 0 ? 0 : i;
    }

    public boolean isQuiescent() {
        return this.parallelism + (int) (this.ctl >> 48) <= 0;
    }

    public long getStealCount() {
        long i = this.stealCount;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

        if (this.workQueues != null) {
            for (int j = 1; j < aforkjoinpool_workqueue.length; j += 2) {
                ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[j]) != null) {
                    i += (long) forkjoinpool_workqueue.nsteals;
                }
            }
        }

        return i;
    }

    public long getQueuedTaskCount() {
        long i = 0L;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

        if (this.workQueues != null) {
            for (int j = 1; j < aforkjoinpool_workqueue.length; j += 2) {
                ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[j]) != null) {
                    i += (long) forkjoinpool_workqueue.queueSize();
                }
            }
        }

        return i;
    }

    public int getQueuedSubmissionCount() {
        int i = 0;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

        if (this.workQueues != null) {
            for (int j = 0; j < aforkjoinpool_workqueue.length; j += 2) {
                ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[j]) != null) {
                    i += forkjoinpool_workqueue.queueSize();
                }
            }
        }

        return i;
    }

    public boolean hasQueuedSubmissions() {
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

        if (this.workQueues != null) {
            for (int i = 0; i < aforkjoinpool_workqueue.length; i += 2) {
                ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[i]) != null && !forkjoinpool_workqueue.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    protected ForkJoinTask<?> pollSubmission() {
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

        if (this.workQueues != null) {
            for (int i = 0; i < aforkjoinpool_workqueue.length; i += 2) {
                ForkJoinTask forkjointask;
                ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[i]) != null && (forkjointask = forkjoinpool_workqueue.poll()) != null) {
                    return forkjointask;
                }
            }
        }

        return null;
    }

    protected int drainTasksTo(Collection<? super ForkJoinTask<?>> collection) {
        int i = 0;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;

        if (this.workQueues != null) {
            for (int j = 0; j < aforkjoinpool_workqueue.length; ++j) {
                ForkJoinTask forkjointask;
                ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[j]) != null) {
                    while ((forkjointask = forkjoinpool_workqueue.poll()) != null) {
                        collection.add(forkjointask);
                        ++i;
                    }
                }
            }
        }

        return i;
    }

    public String toString() {
        long i = 0L;
        long j = 0L;
        int k = 0;
        long l = this.stealCount;
        long i1 = this.ctl;
        ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
        int j1;

        if (this.workQueues != null) {
            for (int k1 = 0; k1 < aforkjoinpool_workqueue.length; ++k1) {
                ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[k1]) != null) {
                    j1 = forkjoinpool_workqueue.queueSize();
                    if ((k1 & 1) == 0) {
                        j += (long) j1;
                    } else {
                        i += (long) j1;
                        l += (long) forkjoinpool_workqueue.nsteals;
                        if (forkjoinpool_workqueue.isApparentlyUnblocked()) {
                            ++k;
                        }
                    }
                }
            }
        }

        short short0 = this.parallelism;

        j1 = short0 + (short) ((int) (i1 >>> 32));
        int l1 = short0 + (int) (i1 >> 48);

        if (l1 < 0) {
            l1 = 0;
        }

        String s;

        if ((i1 & 2147483648L) != 0L) {
            s = j1 == 0 ? "Terminated" : "Terminating";
        } else {
            s = this.plock < 0 ? "Shutting down" : "Running";
        }

        return super.toString() + "[" + s + ", parallelism = " + short0 + ", size = " + j1 + ", active = " + l1 + ", running = " + k + ", steals = " + l + ", tasks = " + i + ", submissions = " + j + "]";
    }

    public void shutdown() {
        checkPermission();
        this.tryTerminate(false, true);
    }

    public List<Runnable> shutdownNow() {
        checkPermission();
        this.tryTerminate(true, true);
        return Collections.emptyList();
    }

    public boolean isTerminated() {
        long i = this.ctl;

        return (i & 2147483648L) != 0L && (short) ((int) (i >>> 32)) + this.parallelism <= 0;
    }

    public boolean isTerminating() {
        long i = this.ctl;

        return (i & 2147483648L) != 0L && (short) ((int) (i >>> 32)) + this.parallelism > 0;
    }

    public boolean isShutdown() {
        return this.plock < 0;
    }

    public boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else if (this == ForkJoinPool.common) {
            this.awaitQuiescence(i, timeunit);
            return false;
        } else {
            long j = timeunit.toNanos(i);

            if (this.isTerminated()) {
                return true;
            } else if (j <= 0L) {
                return false;
            } else {
                long k = System.nanoTime() + j;

                synchronized (this) {
                    while (!this.isTerminated()) {
                        if (j <= 0L) {
                            return false;
                        }

                        long l = TimeUnit.NANOSECONDS.toMillis(j);

                        this.wait(l > 0L ? l : 1L);
                        j = k - System.nanoTime();
                    }

                    return true;
                }
            }
        }
    }

    public boolean awaitQuiescence(long i, TimeUnit timeunit) {
        long j = timeunit.toNanos(i);
        Thread thread = Thread.currentThread();
        ForkJoinWorkerThread forkjoinworkerthread;

        if (thread instanceof ForkJoinWorkerThread && (forkjoinworkerthread = (ForkJoinWorkerThread) thread).pool == this) {
            this.helpQuiescePool(forkjoinworkerthread.workQueue);
            return true;
        } else {
            long k = System.nanoTime();
            int l = 0;
            boolean flag = true;

            while (!this.isQuiescent()) {
                ForkJoinPool.WorkQueue[] aforkjoinpool_workqueue = this.workQueues;
                int i1;

                if (this.workQueues == null || (i1 = aforkjoinpool_workqueue.length - 1) < 0) {
                    break;
                }

                if (!flag) {
                    if (System.nanoTime() - k > j) {
                        return false;
                    }

                    Thread.yield();
                }

                flag = false;

                for (int j1 = i1 + 1 << 2; j1 >= 0; --j1) {
                    ForkJoinPool.WorkQueue forkjoinpool_workqueue;

                    if ((forkjoinpool_workqueue = aforkjoinpool_workqueue[l++ & i1]) != null) {
                        int k1 = forkjoinpool_workqueue.base;

                        if (forkjoinpool_workqueue.base - forkjoinpool_workqueue.top < 0) {
                            flag = true;
                            ForkJoinTask forkjointask;

                            if ((forkjointask = forkjoinpool_workqueue.pollAt(k1)) != null) {
                                forkjointask.doExec();
                            }
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    static void quiesceCommonPool() {
        ForkJoinPool.common.awaitQuiescence(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    public static void managedBlock(ForkJoinPool.ManagedBlocker forkjoinpool_managedblocker) throws InterruptedException {
        Thread thread = Thread.currentThread();

        if (thread instanceof ForkJoinWorkerThread) {
            ForkJoinPool forkjoinpool = ((ForkJoinWorkerThread) thread).pool;

            while (!forkjoinpool_managedblocker.isReleasable()) {
                if (forkjoinpool.tryCompensate(forkjoinpool.ctl)) {
                    try {
                        while (!forkjoinpool_managedblocker.isReleasable() && !forkjoinpool_managedblocker.block()) {
                            ;
                        }

                        return;
                    } finally {
                        forkjoinpool.incrementActiveCount();
                    }
                }
            }
        } else {
            while (!forkjoinpool_managedblocker.isReleasable() && !forkjoinpool_managedblocker.block()) {
                ;
            }
        }

    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T t0) {
        return new ForkJoinTask.AdaptedRunnable<>(runnable, t0);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ForkJoinTask.AdaptedCallable<>(callable);
    }

    private static ForkJoinPool makeCommonPool() {
        int i = -1;
        ForkJoinPool.ForkJoinWorkerThreadFactory forkjoinpool_forkjoinworkerthreadfactory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;
        UncaughtExceptionHandler uncaughtexceptionhandler = null;

        try {
            String s = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
            String s1 = System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
            String s2 = System.getProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler");

            if (s != null) {
                i = Integer.parseInt(s);
            }

            if (s1 != null) {
                forkjoinpool_forkjoinworkerthreadfactory = (ForkJoinPool.ForkJoinWorkerThreadFactory) ClassLoader.getSystemClassLoader().loadClass(s1).newInstance();
            }

            if (s2 != null) {
                uncaughtexceptionhandler = (UncaughtExceptionHandler) ClassLoader.getSystemClassLoader().loadClass(s2).newInstance();
            }
        } catch (Exception exception) {
            ;
        }

        if (i < 0 && (i = Runtime.getRuntime().availableProcessors() - 1) < 0) {
            i = 0;
        }

        if (i > 32767) {
            i = 32767;
        }

        return new ForkJoinPool(i, forkjoinpool_forkjoinworkerthreadfactory, uncaughtexceptionhandler, 0, "ForkJoinPool.commonPool-worker-");
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
            Class oclass = ForkJoinPool.class;

            CTL = ForkJoinPool.U.objectFieldOffset(oclass.getDeclaredField("ctl"));
            STEALCOUNT = ForkJoinPool.U.objectFieldOffset(oclass.getDeclaredField("stealCount"));
            PLOCK = ForkJoinPool.U.objectFieldOffset(oclass.getDeclaredField("plock"));
            INDEXSEED = ForkJoinPool.U.objectFieldOffset(oclass.getDeclaredField("indexSeed"));
            Class oclass1 = Thread.class;

            PARKBLOCKER = ForkJoinPool.U.objectFieldOffset(oclass1.getDeclaredField("parkBlocker"));
            Class oclass2 = ForkJoinPool.WorkQueue.class;

            QBASE = ForkJoinPool.U.objectFieldOffset(oclass2.getDeclaredField("base"));
            QLOCK = ForkJoinPool.U.objectFieldOffset(oclass2.getDeclaredField("qlock"));
            Class oclass3 = ForkJoinTask[].class;

            ABASE = ForkJoinPool.U.arrayBaseOffset(oclass3);
            int i = ForkJoinPool.U.arrayIndexScale(oclass3);

            if ((i & i - 1) != 0) {
                throw new Error("data type scale not a power of two");
            }

            ASHIFT = 31 - Integer.numberOfLeadingZeros(i);
        } catch (Exception exception) {
            throw new Error(exception);
        }

        submitters = new ThreadLocal();
        defaultForkJoinWorkerThreadFactory = new ForkJoinPool.DefaultForkJoinWorkerThreadFactory();
        modifyThreadPermission = new RuntimePermission("modifyThread");
        common = (ForkJoinPool) AccessController.doPrivileged(new PrivilegedAction<ForkJoinPool>() {
            public ForkJoinPool run() {
                return ForkJoinPool.makeCommonPool();
            }
        });
        short short0 = ForkJoinPool.common.parallelism;

        commonParallelism = short0 > 0 ? short0 : 1;
    }

    public interface ManagedBlocker {

        boolean block() throws InterruptedException;

        boolean isReleasable();
    }

    static final class Submitter {

        int seed;

        Submitter(int i) {
            this.seed = i;
        }
    }

    static final class WorkQueue {

        static final int INITIAL_QUEUE_CAPACITY = 8192;
        static final int MAXIMUM_QUEUE_CAPACITY = 67108864;
        volatile long pad00;
        volatile long pad01;
        volatile long pad02;
        volatile long pad03;
        volatile long pad04;
        volatile long pad05;
        volatile long pad06;
        volatile int eventCount;
        int nextWait;
        int nsteals;
        int hint;
        short poolIndex;
        final short mode;
        volatile int qlock;
        volatile int base;
        int top;
        ForkJoinTask<?>[] array;
        final ForkJoinPool pool;
        final ForkJoinWorkerThread owner;
        volatile Thread parker;
        volatile ForkJoinTask<?> currentJoin;
        ForkJoinTask<?> currentSteal;
        volatile Object pad10;
        volatile Object pad11;
        volatile Object pad12;
        volatile Object pad13;
        volatile Object pad14;
        volatile Object pad15;
        volatile Object pad16;
        volatile Object pad17;
        volatile Object pad18;
        volatile Object pad19;
        volatile Object pad1a;
        volatile Object pad1b;
        volatile Object pad1c;
        volatile Object pad1d;
        private static final Unsafe U;
        private static final long QBASE;
        private static final long QLOCK;
        private static final int ABASE;
        private static final int ASHIFT;

        WorkQueue(ForkJoinPool forkjoinpool, ForkJoinWorkerThread forkjoinworkerthread, int i, int j) {
            this.pool = forkjoinpool;
            this.owner = forkjoinworkerthread;
            this.mode = (short) i;
            this.hint = j;
            this.base = this.top = 4096;
        }

        final int queueSize() {
            int i = this.base - this.top;

            return i >= 0 ? 0 : -i;
        }

        final boolean isEmpty() {
            int i = this.top;
            int j = this.base - this.top;
            boolean flag;

            if (j < 0) {
                label29:
                {
                    if (j == -1) {
                        ForkJoinTask[] aforkjointask = this.array;
                        int k;

                        if (this.array == null || (k = aforkjointask.length - 1) < 0 || ForkJoinPool.WorkQueue.U.getObject(aforkjointask, (long) ((k & i - 1) << ForkJoinPool.WorkQueue.ASHIFT) + (long) ForkJoinPool.WorkQueue.ABASE) == null) {
                            break label29;
                        }
                    }

                    flag = false;
                    return flag;
                }
            }

            flag = true;
            return flag;
        }

        final void push(ForkJoinTask<?> forkjointask) {
            int i = this.top;
            ForkJoinTask[] aforkjointask = this.array;

            if (this.array != null) {
                int j = aforkjointask.length - 1;

                ForkJoinPool.WorkQueue.U.putOrderedObject(aforkjointask, (long) (((j & i) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE), forkjointask);
                int k;

                if ((k = (this.top = i + 1) - this.base) <= 2) {
                    ForkJoinPool forkjoinpool = this.pool;

                    this.pool.signalWork(forkjoinpool.workQueues, this);
                } else if (k >= j) {
                    this.growArray();
                }
            }

        }

        final ForkJoinTask<?>[] growArray() {
            ForkJoinTask[] aforkjointask = this.array;
            int i = aforkjointask != null ? aforkjointask.length << 1 : 8192;

            if (i > 67108864) {
                throw new RejectedExecutionException("Queue capacity exceeded");
            } else {
                ForkJoinTask[] aforkjointask1 = this.array = new ForkJoinTask[i];
                int j;

                if (aforkjointask != null && (j = aforkjointask.length - 1) >= 0) {
                    int k = this.top;
                    int l = this.base;

                    if (this.top - this.base > 0) {
                        int i1 = i - 1;

                        do {
                            int j1 = ((l & j) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE;
                            int k1 = ((l & i1) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE;
                            ForkJoinTask forkjointask = (ForkJoinTask) ForkJoinPool.WorkQueue.U.getObjectVolatile(aforkjointask, (long) j1);

                            if (forkjointask != null && ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, (long) j1, forkjointask, (Object) null)) {
                                ForkJoinPool.WorkQueue.U.putObjectVolatile(aforkjointask1, (long) k1, forkjointask);
                            }

                            ++l;
                        } while (l != k);
                    }
                }

                return aforkjointask1;
            }
        }

        final ForkJoinTask<?> pop() {
            ForkJoinTask[] aforkjointask = this.array;
            int i;
            int j;

            if (this.array != null && (i = aforkjointask.length - 1) >= 0) {
                while ((j = this.top - 1) - this.base >= 0) {
                    long k = (long) (((i & j) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE);
                    ForkJoinTask forkjointask;

                    if ((forkjointask = (ForkJoinTask) ForkJoinPool.WorkQueue.U.getObject(aforkjointask, k)) == null) {
                        break;
                    }

                    if (ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, k, forkjointask, (Object) null)) {
                        this.top = j;
                        return forkjointask;
                    }
                }
            }

            return null;
        }

        final ForkJoinTask<?> pollAt(int i) {
            ForkJoinTask[] aforkjointask = this.array;

            if (this.array != null) {
                int j = ((aforkjointask.length - 1 & i) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE;
                ForkJoinTask forkjointask;

                if ((forkjointask = (ForkJoinTask) ForkJoinPool.WorkQueue.U.getObjectVolatile(aforkjointask, (long) j)) != null && this.base == i && ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, (long) j, forkjointask, (Object) null)) {
                    ForkJoinPool.WorkQueue.U.putOrderedInt(this, ForkJoinPool.WorkQueue.QBASE, i + 1);
                    return forkjointask;
                }
            }

            return null;
        }

        final ForkJoinTask<?> poll() {
            while (true) {
                int i = this.base;

                if (this.base - this.top < 0) {
                    ForkJoinTask[] aforkjointask = this.array;

                    if (this.array != null) {
                        int j = ((aforkjointask.length - 1 & i) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE;
                        ForkJoinTask forkjointask = (ForkJoinTask) ForkJoinPool.WorkQueue.U.getObjectVolatile(aforkjointask, (long) j);

                        if (forkjointask != null) {
                            if (!ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, (long) j, forkjointask, (Object) null)) {
                                continue;
                            }

                            ForkJoinPool.WorkQueue.U.putOrderedInt(this, ForkJoinPool.WorkQueue.QBASE, i + 1);
                            return forkjointask;
                        }

                        if (this.base != i) {
                            continue;
                        }

                        if (i + 1 != this.top) {
                            Thread.yield();
                            continue;
                        }
                    }
                }

                return null;
            }
        }

        final ForkJoinTask<?> nextLocalTask() {
            return this.mode == 0 ? this.pop() : this.poll();
        }

        final ForkJoinTask<?> peek() {
            ForkJoinTask[] aforkjointask = this.array;
            int i;

            if (aforkjointask != null && (i = aforkjointask.length - 1) >= 0) {
                int j = this.mode == 0 ? this.top - 1 : this.base;
                int k = ((j & i) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE;

                return (ForkJoinTask) ForkJoinPool.WorkQueue.U.getObjectVolatile(aforkjointask, (long) k);
            } else {
                return null;
            }
        }

        final boolean tryUnpush(ForkJoinTask<?> forkjointask) {
            ForkJoinTask[] aforkjointask = this.array;

            if (this.array != null) {
                int i = this.top;

                if (this.top != this.base) {
                    int j = aforkjointask.length - 1;

                    --i;
                    if (ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, (long) (((j & i) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE), forkjointask, (Object) null)) {
                        this.top = i;
                        return true;
                    }
                }
            }

            return false;
        }

        final void cancelAll() {
            ForkJoinTask.cancelIgnoringExceptions(this.currentJoin);
            ForkJoinTask.cancelIgnoringExceptions(this.currentSteal);

            ForkJoinTask forkjointask;

            while ((forkjointask = this.poll()) != null) {
                ForkJoinTask.cancelIgnoringExceptions(forkjointask);
            }

        }

        final void pollAndExecAll() {
            ForkJoinTask forkjointask;

            while ((forkjointask = this.poll()) != null) {
                forkjointask.doExec();
            }

        }

        final void runTask(ForkJoinTask<?> forkjointask) {
            if ((this.currentSteal = forkjointask) != null) {
                forkjointask.doExec();
                ForkJoinTask[] aforkjointask = this.array;
                short short0 = this.mode;

                ++this.nsteals;
                this.currentSteal = null;
                if (short0 != 0) {
                    this.pollAndExecAll();
                } else if (aforkjointask != null) {
                    int i = aforkjointask.length - 1;

                    int j;

                    while ((j = this.top - 1) - this.base >= 0) {
                        long k = (long) (((i & j) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE);
                        ForkJoinTask forkjointask1 = (ForkJoinTask) ForkJoinPool.WorkQueue.U.getObject(aforkjointask, k);

                        if (forkjointask1 == null) {
                            break;
                        }

                        if (ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, k, forkjointask1, (Object) null)) {
                            this.top = j;
                            forkjointask1.doExec();
                        }
                    }
                }
            }

        }

        final boolean tryRemoveAndExec(ForkJoinTask<?> forkjointask) {
            boolean flag;

            if (forkjointask != null) {
                ForkJoinTask[] aforkjointask = this.array;
                int i;

                if (this.array != null && (i = aforkjointask.length - 1) >= 0) {
                    int j = this.top;
                    int k = this.base;
                    int l;

                    if ((l = this.top - this.base) > 0) {
                        boolean flag1 = false;
                        boolean flag2 = true;

                        flag = true;

                        while (true) {
                            --j;
                            long i1 = (long) (((j & i) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE);
                            ForkJoinTask forkjointask1 = (ForkJoinTask) ForkJoinPool.WorkQueue.U.getObject(aforkjointask, i1);

                            if (forkjointask1 == null) {
                                break;
                            }

                            if (forkjointask1 == forkjointask) {
                                if (j + 1 == this.top) {
                                    if (ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, i1, forkjointask, (Object) null)) {
                                        this.top = j;
                                        flag1 = true;
                                    }
                                } else if (this.base == k) {
                                    flag1 = ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, i1, forkjointask, new ForkJoinPool.EmptyTask());
                                }
                                break;
                            }

                            if (forkjointask1.status >= 0) {
                                flag2 = false;
                            } else if (j + 1 == this.top) {
                                if (ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, i1, forkjointask1, (Object) null)) {
                                    this.top = j;
                                }
                                break;
                            }

                            --l;
                            if (l == 0) {
                                if (!flag2 && this.base == k) {
                                    flag = false;
                                }
                                break;
                            }
                        }

                        if (flag1) {
                            forkjointask.doExec();
                        }

                        return flag;
                    }
                }
            }

            flag = false;
            return flag;
        }

        final boolean pollAndExecCC(CountedCompleter<?> countedcompleter) {
            int i = this.base;

            if (this.base - this.top < 0) {
                ForkJoinTask[] aforkjointask = this.array;

                if (this.array != null) {
                    long j = (long) (((aforkjointask.length - 1 & i) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE);
                    Object object;

                    if ((object = ForkJoinPool.WorkQueue.U.getObjectVolatile(aforkjointask, j)) == null) {
                        return true;
                    }

                    if (object instanceof CountedCompleter) {
                        CountedCompleter countedcompleter1 = (CountedCompleter) object;
                        CountedCompleter countedcompleter2 = countedcompleter1;

                        do {
                            if (countedcompleter2 == countedcompleter) {
                                if (this.base == i && ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, j, countedcompleter1, (Object) null)) {
                                    ForkJoinPool.WorkQueue.U.putOrderedInt(this, ForkJoinPool.WorkQueue.QBASE, i + 1);
                                    countedcompleter1.doExec();
                                }

                                return true;
                            }
                        } while ((countedcompleter2 = countedcompleter2.completer) != null);
                    }
                }
            }

            return false;
        }

        final boolean externalPopAndExecCC(CountedCompleter<?> countedcompleter) {
            int i = this.top;

            if (this.base - this.top < 0) {
                ForkJoinTask[] aforkjointask = this.array;

                if (this.array != null) {
                    long j = (long) (((aforkjointask.length - 1 & i - 1) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE);
                    Object object;

                    if ((object = ForkJoinPool.WorkQueue.U.getObject(aforkjointask, j)) instanceof CountedCompleter) {
                        CountedCompleter countedcompleter1 = (CountedCompleter) object;
                        CountedCompleter countedcompleter2 = countedcompleter1;

                        do {
                            if (countedcompleter2 == countedcompleter) {
                                if (ForkJoinPool.WorkQueue.U.compareAndSwapInt(this, ForkJoinPool.WorkQueue.QLOCK, 0, 1)) {
                                    if (this.top == i && this.array == aforkjointask && ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, j, countedcompleter1, (Object) null)) {
                                        this.top = i - 1;
                                        this.qlock = 0;
                                        countedcompleter1.doExec();
                                    } else {
                                        this.qlock = 0;
                                    }
                                }

                                return true;
                            }
                        } while ((countedcompleter2 = countedcompleter2.completer) != null);
                    }
                }
            }

            return false;
        }

        final boolean internalPopAndExecCC(CountedCompleter<?> countedcompleter) {
            int i = this.top;

            if (this.base - this.top < 0) {
                ForkJoinTask[] aforkjointask = this.array;

                if (this.array != null) {
                    long j = (long) (((aforkjointask.length - 1 & i - 1) << ForkJoinPool.WorkQueue.ASHIFT) + ForkJoinPool.WorkQueue.ABASE);
                    Object object;

                    if ((object = ForkJoinPool.WorkQueue.U.getObject(aforkjointask, j)) instanceof CountedCompleter) {
                        CountedCompleter countedcompleter1 = (CountedCompleter) object;
                        CountedCompleter countedcompleter2 = countedcompleter1;

                        do {
                            if (countedcompleter2 == countedcompleter) {
                                if (ForkJoinPool.WorkQueue.U.compareAndSwapObject(aforkjointask, j, countedcompleter1, (Object) null)) {
                                    this.top = i - 1;
                                    countedcompleter1.doExec();
                                }

                                return true;
                            }
                        } while ((countedcompleter2 = countedcompleter2.completer) != null);
                    }
                }
            }

            return false;
        }

        final boolean isApparentlyUnblocked() {
            boolean flag;

            if (this.eventCount >= 0) {
                ForkJoinWorkerThread forkjoinworkerthread = this.owner;
                State state;

                if (this.owner != null && (state = forkjoinworkerthread.getState()) != State.BLOCKED && state != State.WAITING && state != State.TIMED_WAITING) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }

        static {
            try {
                U = ForkJoinPool.getUnsafe();
                Class oclass = ForkJoinPool.WorkQueue.class;
                Class oclass1 = ForkJoinTask[].class;

                QBASE = ForkJoinPool.WorkQueue.U.objectFieldOffset(oclass.getDeclaredField("base"));
                QLOCK = ForkJoinPool.WorkQueue.U.objectFieldOffset(oclass.getDeclaredField("qlock"));
                ABASE = ForkJoinPool.WorkQueue.U.arrayBaseOffset(oclass1);
                int i = ForkJoinPool.WorkQueue.U.arrayIndexScale(oclass1);

                if ((i & i - 1) != 0) {
                    throw new Error("data type scale not a power of two");
                } else {
                    ASHIFT = 31 - Integer.numberOfLeadingZeros(i);
                }
            } catch (Exception exception) {
                throw new Error(exception);
            }
        }
    }

    static final class EmptyTask extends ForkJoinTask<Void> {

        private static final long serialVersionUID = -7721805057305804111L;

        EmptyTask() {
            this.status = -268435456;
        }

        @Override
        public final Void getRawResult() {
            return null;
        }

        public final void setRawResult(Void ovoid) {}

        @Override
        public final boolean exec() {
            return true;
        }
    }

    static final class DefaultForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        DefaultForkJoinWorkerThreadFactory() {}

        @Override
        public final ForkJoinWorkerThread newThread(ForkJoinPool forkjoinpool) {
            return new ForkJoinWorkerThread(forkjoinpool);
        }
    }

    public interface ForkJoinWorkerThreadFactory {

        ForkJoinWorkerThread newThread(ForkJoinPool forkjoinpool);
    }
}
