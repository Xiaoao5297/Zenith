package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import java.util.concurrent.atomic.AtomicLong;

@GwtCompatible(emulated = true)
final class LongAddables {

    private static final Supplier<LongAddable> SUPPLIER;

    LongAddables() {}

    public static LongAddable create() {
        return (LongAddable) LongAddables.SUPPLIER.get();
    }

    static {
        Supplier supplier;

        try {
            new LongAdder();
            supplier = new Supplier<LongAddable>() {
                @Override
                public LongAddable get() {
                    return new LongAdder();
                }
            };
        } catch (Throwable throwable) {
            supplier = new Supplier<LongAddable>() {
                @Override
                public LongAddable get() {
                    return new LongAddables.PureJavaLongAddable();
                }
            };
        }

        SUPPLIER = supplier;
    }

    private static final class PureJavaLongAddable extends AtomicLong implements LongAddable {

        private PureJavaLongAddable() {}

        @Override
        public void increment() {
            this.getAndIncrement();
        }

        @Override
        public void add(long i) {
            this.getAndAdd(i);
        }

        @Override
        public long sum() {
            return this.get();
        }
    }
}
