package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ComparisonChain {

    private static final ComparisonChain ACTIVE = new ComparisonChain() {
        @Override
        public ComparisonChain compare(Comparable comparable, Comparable comparable1) {
            return this.classify(comparable.compareTo(comparable1));
        }

        @Override
        public <T> ComparisonChain compare(@Nullable T t0, @Nullable T t1, Comparator<T> comparator) {
            return this.classify(comparator.compare(t0, t1));
        }

        @Override
        public ComparisonChain compare(int i, int j) {
            return this.classify(Ints.compare(i, j));
        }

        @Override
        public ComparisonChain compare(long i, long j) {
            return this.classify(Longs.compare(i, j));
        }

        @Override
        public ComparisonChain compare(float f, float f1) {
            return this.classify(Float.compare(f, f1));
        }

        @Override
        public ComparisonChain compare(double d0, double d1) {
            return this.classify(Double.compare(d0, d1));
        }

        @Override
        public ComparisonChain compareTrueFirst(boolean flag, boolean flag1) {
            return this.classify(Booleans.compare(flag1, flag));
        }

        @Override
        public ComparisonChain compareFalseFirst(boolean flag, boolean flag1) {
            return this.classify(Booleans.compare(flag, flag1));
        }

        ComparisonChain classify(int i) {
            return i < 0 ? ComparisonChain.LESS : (i > 0 ? ComparisonChain.GREATER : ComparisonChain.ACTIVE);
        }

        @Override
        public int result() {
            return 0;
        }
    };
    private static final ComparisonChain LESS = new ComparisonChain.InactiveComparisonChain(-1);
    private static final ComparisonChain GREATER = new ComparisonChain.InactiveComparisonChain(1);

    private ComparisonChain() {}

    public static ComparisonChain start() {
        return ComparisonChain.ACTIVE;
    }

    public abstract ComparisonChain compare(Comparable<?> comparable, Comparable<?> comparable1);

    public abstract <T> ComparisonChain compare(@Nullable T t0, @Nullable T t1, Comparator<T> comparator);

    public abstract ComparisonChain compare(int i, int j);

    public abstract ComparisonChain compare(long i, long j);

    public abstract ComparisonChain compare(float f, float f1);

    public abstract ComparisonChain compare(double d0, double d1);

    public abstract ComparisonChain compareTrueFirst(boolean flag, boolean flag1);

    public abstract ComparisonChain compareFalseFirst(boolean flag, boolean flag1);

    public abstract int result();

    private static final class InactiveComparisonChain extends ComparisonChain {

        final int result;

        InactiveComparisonChain(int i) {
            super(null);
            this.result = i;
        }

        @Override
        public ComparisonChain compare(@Nullable Comparable comparable, @Nullable Comparable comparable1) {
            return this;
        }

        @Override
        public <T> ComparisonChain compare(@Nullable T t0, @Nullable T t1, @Nullable Comparator<T> comparator) {
            return this;
        }

        @Override
        public ComparisonChain compare(int i, int j) {
            return this;
        }

        @Override
        public ComparisonChain compare(long i, long j) {
            return this;
        }

        @Override
        public ComparisonChain compare(float f, float f1) {
            return this;
        }

        @Override
        public ComparisonChain compare(double d0, double d1) {
            return this;
        }

        @Override
        public ComparisonChain compareTrueFirst(boolean flag, boolean flag1) {
            return this;
        }

        @Override
        public ComparisonChain compareFalseFirst(boolean flag, boolean flag1) {
            return this;
        }

        @Override
        public int result() {
            return this.result;
        }
    }
}
