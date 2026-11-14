package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadLocalRandom extends Random {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadLocalRandom.class);
    private static final AtomicLong seedUniquifier = new AtomicLong();
    private static volatile long initialSeedUniquifier;
    private static final long multiplier = 25214903917L;
    private static final long addend = 11L;
    private static final long mask = 281474976710655L;
    private long rnd;
    boolean initialized = true;
    private long pad0;
    private long pad1;
    private long pad2;
    private long pad3;
    private long pad4;
    private long pad5;
    private long pad6;
    private long pad7;
    private static final ThreadLocal<ThreadLocalRandom> localRandom = new ThreadLocal<ThreadLocalRandom>() {
        protected ThreadLocalRandom initialValue() {
            return new ThreadLocalRandom();
        }
    };
    private static final long serialVersionUID = -5851777807851030925L;

    public static void setInitialSeedUniquifier(long i) {
        ThreadLocalRandom.initialSeedUniquifier = i;
    }

    public static synchronized long getInitialSeedUniquifier() {
        long i = ThreadLocalRandom.initialSeedUniquifier;

        if (i == 0L) {
            ThreadLocalRandom.initialSeedUniquifier = i = SystemPropertyUtil.getLong("io.netty.initialSeedUniquifier", 0L);
        }

        if (i == 0L) {
            final LinkedBlockingQueue linkedblockingqueue = new LinkedBlockingQueue();
            Thread thread = new Thread("initialSeedUniquifierGenerator") {
                public void run() {
                    SecureRandom securerandom = new SecureRandom();

                    linkedblockingqueue.add(securerandom.nextLong());
                }
            };

            thread.start();
            long j = 3L;
            long k = System.nanoTime() + TimeUnit.SECONDS.toNanos(3L);

            while (true) {
                long l = k - System.nanoTime();

                if (l <= 0L) {
                    ThreadLocalRandom.logger.warn("Failed to get the secure random number from SecureRandom within {} seconds. Not enough entrophy?", (Object) 3L);
                    break;
                }

                try {
                    Long olong = (Long) linkedblockingqueue.poll(l, TimeUnit.NANOSECONDS);

                    if (olong != null) {
                        i = olong;
                        break;
                    }
                } catch (InterruptedException interruptedexception) {
                    ;
                }
            }

            i ^= 3627065505421648153L;
            i ^= Long.reverse(System.nanoTime());
            ThreadLocalRandom.initialSeedUniquifier = i;
        }

        return i;
    }

    private static long newSeed() {
        long i;
        long j;
        long k;

        do {
            i = ThreadLocalRandom.seedUniquifier.get();
            j = i != 0L ? i : getInitialSeedUniquifier();
            k = j * 181783497276652981L;
        } while (!ThreadLocalRandom.seedUniquifier.compareAndSet(i, k));

        if (i == 0L && ThreadLocalRandom.logger.isDebugEnabled()) {
            ThreadLocalRandom.logger.debug(String.format("-Dio.netty.initialSeedUniquifier: 0x%016x", j));
        }

        return k ^ System.nanoTime();
    }

    ThreadLocalRandom() {
        super(newSeed());
    }

    public static ThreadLocalRandom current() {
        return (ThreadLocalRandom) ThreadLocalRandom.localRandom.get();
    }

    public void setSeed(long i) {
        if (this.initialized) {
            throw new UnsupportedOperationException();
        } else {
            this.rnd = (i ^ 25214903917L) & 281474976710655L;
        }
    }

    protected int next(int i) {
        this.rnd = this.rnd * 25214903917L + 11L & 281474976710655L;
        return (int) (this.rnd >>> 48 - i);
    }

    public int nextInt(int i, int j) {
        if (i >= j) {
            throw new IllegalArgumentException();
        } else {
            return this.nextInt(j - i) + i;
        }
    }

    public long nextLong(long i) {
        if (i <= 0L) {
            throw new IllegalArgumentException("n must be positive");
        } else {
            long j;
            long k;

            for (k = 0L; i >= 2147483647L; i = j) {
                int l = this.next(2);
                long i1 = i >>> 1;

                j = (l & 2) == 0 ? i1 : i - i1;
                if ((l & 1) == 0) {
                    k += i - j;
                }
            }

            return k + (long) this.nextInt((int) i);
        }
    }

    public long nextLong(long i, long j) {
        if (i >= j) {
            throw new IllegalArgumentException();
        } else {
            return this.nextLong(j - i) + i;
        }
    }

    public double nextDouble(double d0) {
        if (d0 <= 0.0D) {
            throw new IllegalArgumentException("n must be positive");
        } else {
            return this.nextDouble() * d0;
        }
    }

    public double nextDouble(double d0, double d1) {
        if (d0 >= d1) {
            throw new IllegalArgumentException();
        } else {
            return this.nextDouble() * (d1 - d0) + d0;
        }
    }
}
