package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Beta
public abstract class Striped<L> {

    private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() {
        @Override
        public ReadWriteLock get() {
            return new ReentrantReadWriteLock();
        }
    };
    private static final int ALL_SET = -1;

    private Striped() {}

    public abstract L get(Object object);

    public abstract L getAt(int i);

    abstract int indexFor(Object object);

    public abstract int size();

    public Iterable<L> bulkGet(Iterable<?> iterable) {
        Object[] aobject = Iterables.toArray(iterable, Object.class);
        int[] aint = new int[aobject.length];

        int i;

        for (i = 0; i < aobject.length; ++i) {
            aint[i] = this.indexFor(aobject[i]);
        }

        Arrays.sort(aint);

        for (i = 0; i < aobject.length; ++i) {
            aobject[i] = this.getAt(aint[i]);
        }

        List list = Arrays.asList(aobject);

        return Collections.unmodifiableList(list);
    }

    public static Striped<Lock> lock(int i) {
        return new Striped.CompactStriped<>(i, new Supplier<Lock>() {
            @Override
            public Lock get() {
                return new Striped.PaddedLock();
            }
        });
    }

    public static Striped<Lock> lazyWeakLock(int i) {
        return new Striped.LazyStriped<>(i, new Supplier<Lock>() {
            @Override
            public Lock get() {
                return new ReentrantLock(false);
            }
        });
    }

    public static Striped<Semaphore> semaphore(int i, final int j) {
        return new Striped.CompactStriped<>(i, new Supplier<Semaphore>() {
            @Override
            public Semaphore get() {
                return new Striped.PaddedSemaphore(j);
            }
        });
    }

    public static Striped<Semaphore> lazyWeakSemaphore(int i, final int j) {
        return new Striped.LazyStriped<>(i, new Supplier<Semaphore>() {
            @Override
            public Semaphore get() {
                return new Semaphore(j, false);
            }
        });
    }

    public static Striped<ReadWriteLock> readWriteLock(int i) {
        return new Striped.CompactStriped<>(i, Striped.READ_WRITE_LOCK_SUPPLIER);
    }

    public static Striped<ReadWriteLock> lazyWeakReadWriteLock(int i) {
        return new Striped.LazyStriped<>(i, Striped.READ_WRITE_LOCK_SUPPLIER);
    }

    private static int ceilToPowerOfTwo(int i) {
        return 1 << IntMath.log2(i, RoundingMode.CEILING);
    }

    private static int smear(int i) {
        i ^= i >>> 20 ^ i >>> 12;
        return i ^ i >>> 7 ^ i >>> 4;
    }

    private static class PaddedSemaphore extends Semaphore {

        long q1;
        long q2;
        long q3;

        PaddedSemaphore(int i) {
            super(i, false);
        }
    }

    private static class PaddedLock extends ReentrantLock {

        long q1;
        long q2;
        long q3;

        PaddedLock() {
            super(false);
        }
    }

    private static class LazyStriped<L> extends Striped.PowerOfTwoStriped<L> {

        final ConcurrentMap<Integer, L> locks;
        final Supplier<L> supplier;
        final int size;

        LazyStriped(int i, Supplier<L> supplier) {
            super(i);
            this.size = this.mask == -1 ? Integer.MAX_VALUE : this.mask + 1;
            this.supplier = supplier;
            this.locks = (new MapMaker()).weakValues().makeMap();
        }

        @Override
        public L getAt(int i) {
            if (this.size != Integer.MAX_VALUE) {
                Preconditions.checkElementIndex(i, this.size());
            }

            Object object = this.locks.get(i);

            if (object != null) {
                return object;
            } else {
                Object object1 = this.supplier.get();

                object = this.locks.putIfAbsent(i, object1);
                return Objects.firstNonNull(object, object1);
            }
        }

        @Override
        public int size() {
            return this.size;
        }
    }

    private static class CompactStriped<L> extends Striped.PowerOfTwoStriped<L> {

        private final Object[] array;

        private CompactStriped(int i, Supplier<L> supplier) {
            super(i);
            Preconditions.checkArgument(i <= 1073741824, "Stripes must be <= 2^30)");
            this.array = new Object[this.mask + 1];

            for (int j = 0; j < this.array.length; ++j) {
                this.array[j] = supplier.get();
            }

        }

        @Override
        public L getAt(int i) {
            return this.array[i];
        }

        @Override
        public int size() {
            return this.array.length;
        }
    }

    private abstract static class PowerOfTwoStriped<L> extends Striped<L> {

        final int mask;

        PowerOfTwoStriped(int i) {
            super(null);
            Preconditions.checkArgument(i > 0, "Stripes must be positive");
            this.mask = i > 1073741824 ? -1 : Striped.ceilToPowerOfTwo(i) - 1;
        }

        @Override
        final int indexFor(Object object) {
            int i = Striped.smear(object.hashCode());

            return i & this.mask;
        }

        @Override
        public final L get(Object object) {
            return this.getAt(this.indexFor(object));
        }
    }
}
