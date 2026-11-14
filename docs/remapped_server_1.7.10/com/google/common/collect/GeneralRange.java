package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class GeneralRange<T> implements Serializable {

    private final Comparator<? super T> comparator;
    private final boolean hasLowerBound;
    @Nullable
    private final T lowerEndpoint;
    private final BoundType lowerBoundType;
    private final boolean hasUpperBound;
    @Nullable
    private final T upperEndpoint;
    private final BoundType upperBoundType;
    private transient GeneralRange<T> reverse;

    static <T extends Comparable> GeneralRange<T> from(Range<T> range) {
        Comparable comparable = range.hasLowerBound() ? range.lowerEndpoint() : null;
        BoundType boundtype = range.hasLowerBound() ? range.lowerBoundType() : BoundType.OPEN;
        Comparable comparable1 = range.hasUpperBound() ? range.upperEndpoint() : null;
        BoundType boundtype1 = range.hasUpperBound() ? range.upperBoundType() : BoundType.OPEN;

        return new GeneralRange<>(Ordering.natural(), range.hasLowerBound(), comparable, boundtype, range.hasUpperBound(), comparable1, boundtype1);
    }

    static <T> GeneralRange<T> all(Comparator<? super T> comparator) {
        return new GeneralRange<>(comparator, false, (Object) null, BoundType.OPEN, false, (Object) null, BoundType.OPEN);
    }

    static <T> GeneralRange<T> downTo(Comparator<? super T> comparator, @Nullable T t0, BoundType boundtype) {
        return new GeneralRange<>(comparator, true, t0, boundtype, false, (Object) null, BoundType.OPEN);
    }

    static <T> GeneralRange<T> upTo(Comparator<? super T> comparator, @Nullable T t0, BoundType boundtype) {
        return new GeneralRange<>(comparator, false, (Object) null, BoundType.OPEN, true, t0, boundtype);
    }

    static <T> GeneralRange<T> range(Comparator<? super T> comparator, @Nullable T t0, BoundType boundtype, @Nullable T t1, BoundType boundtype1) {
        return new GeneralRange<>(comparator, true, t0, boundtype, true, t1, boundtype1);
    }

    private GeneralRange(Comparator<? super T> comparator, boolean flag, @Nullable T t0, BoundType boundtype, boolean flag1, @Nullable T t1, BoundType boundtype1) {
        this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
        this.hasLowerBound = flag;
        this.hasUpperBound = flag1;
        this.lowerEndpoint = t0;
        this.lowerBoundType = (BoundType) Preconditions.checkNotNull(boundtype);
        this.upperEndpoint = t1;
        this.upperBoundType = (BoundType) Preconditions.checkNotNull(boundtype1);
        if (flag) {
            comparator.compare(t0, t0);
        }

        if (flag1) {
            comparator.compare(t1, t1);
        }

        if (flag && flag1) {
            int i = comparator.compare(t0, t1);

            Preconditions.checkArgument(i <= 0, "lowerEndpoint (%s) > upperEndpoint (%s)", t0, t1);
            if (i == 0) {
                Preconditions.checkArgument(boundtype != BoundType.OPEN | boundtype1 != BoundType.OPEN);
            }
        }

    }

    Comparator<? super T> comparator() {
        return this.comparator;
    }

    boolean hasLowerBound() {
        return this.hasLowerBound;
    }

    boolean hasUpperBound() {
        return this.hasUpperBound;
    }

    boolean isEmpty() {
        return this.hasUpperBound() && this.tooLow(this.getUpperEndpoint()) || this.hasLowerBound() && this.tooHigh(this.getLowerEndpoint());
    }

    boolean tooLow(@Nullable T t0) {
        if (!this.hasLowerBound()) {
            return false;
        } else {
            Object object = this.getLowerEndpoint();
            int i = this.comparator.compare(t0, object);

            return i < 0 | i == 0 & this.getLowerBoundType() == BoundType.OPEN;
        }
    }

    boolean tooHigh(@Nullable T t0) {
        if (!this.hasUpperBound()) {
            return false;
        } else {
            Object object = this.getUpperEndpoint();
            int i = this.comparator.compare(t0, object);

            return i > 0 | i == 0 & this.getUpperBoundType() == BoundType.OPEN;
        }
    }

    boolean contains(@Nullable T t0) {
        return !this.tooLow(t0) && !this.tooHigh(t0);
    }

    GeneralRange<T> intersect(GeneralRange<T> generalrange) {
        Preconditions.checkNotNull(generalrange);
        Preconditions.checkArgument(this.comparator.equals(generalrange.comparator));
        boolean flag = this.hasLowerBound;
        Object object = this.getLowerEndpoint();
        BoundType boundtype = this.getLowerBoundType();

        if (!this.hasLowerBound()) {
            flag = generalrange.hasLowerBound;
            object = generalrange.getLowerEndpoint();
            boundtype = generalrange.getLowerBoundType();
        } else if (generalrange.hasLowerBound()) {
            int i = this.comparator.compare(this.getLowerEndpoint(), generalrange.getLowerEndpoint());

            if (i < 0 || i == 0 && generalrange.getLowerBoundType() == BoundType.OPEN) {
                object = generalrange.getLowerEndpoint();
                boundtype = generalrange.getLowerBoundType();
            }
        }

        boolean flag1 = this.hasUpperBound;
        Object object1 = this.getUpperEndpoint();
        BoundType boundtype1 = this.getUpperBoundType();
        int j;

        if (!this.hasUpperBound()) {
            flag1 = generalrange.hasUpperBound;
            object1 = generalrange.getUpperEndpoint();
            boundtype1 = generalrange.getUpperBoundType();
        } else if (generalrange.hasUpperBound()) {
            j = this.comparator.compare(this.getUpperEndpoint(), generalrange.getUpperEndpoint());
            if (j > 0 || j == 0 && generalrange.getUpperBoundType() == BoundType.OPEN) {
                object1 = generalrange.getUpperEndpoint();
                boundtype1 = generalrange.getUpperBoundType();
            }
        }

        if (flag && flag1) {
            j = this.comparator.compare(object, object1);
            if (j > 0 || j == 0 && boundtype == BoundType.OPEN && boundtype1 == BoundType.OPEN) {
                object = object1;
                boundtype = BoundType.OPEN;
                boundtype1 = BoundType.CLOSED;
            }
        }

        return new GeneralRange<>(this.comparator, flag, object, boundtype, flag1, object1, boundtype1);
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof GeneralRange)) {
            return false;
        } else {
            GeneralRange generalrange = (GeneralRange) object;

            return this.comparator.equals(generalrange.comparator) && this.hasLowerBound == generalrange.hasLowerBound && this.hasUpperBound == generalrange.hasUpperBound && this.getLowerBoundType().equals(generalrange.getLowerBoundType()) && this.getUpperBoundType().equals(generalrange.getUpperBoundType()) && Objects.equal(this.getLowerEndpoint(), generalrange.getLowerEndpoint()) && Objects.equal(this.getUpperEndpoint(), generalrange.getUpperEndpoint());
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.comparator, this.getLowerEndpoint(), this.getLowerBoundType(), this.getUpperEndpoint(), this.getUpperBoundType());
    }

    GeneralRange<T> reverse() {
        GeneralRange generalrange = this.reverse;

        if (generalrange == null) {
            generalrange = new GeneralRange<>(Ordering.from(this.comparator).reverse(), this.hasUpperBound, this.getUpperEndpoint(), this.getUpperBoundType(), this.hasLowerBound, this.getLowerEndpoint(), this.getLowerBoundType());
            generalrange.reverse = this;
            return this.reverse = generalrange;
        } else {
            return generalrange;
        }
    }

    public String toString() {
        return this.comparator + ":" + (this.lowerBoundType == BoundType.CLOSED ? '[' : '(') + (this.hasLowerBound ? this.lowerEndpoint : "-\u221e") + ',' + (this.hasUpperBound ? this.upperEndpoint : "\u221e") + (this.upperBoundType == BoundType.CLOSED ? ']' : ')');
    }

    T getLowerEndpoint() {
        return this.lowerEndpoint;
    }

    BoundType getLowerBoundType() {
        return this.lowerBoundType;
    }

    T getUpperEndpoint() {
        return this.upperEndpoint;
    }

    BoundType getUpperBoundType() {
        return this.upperBoundType;
    }
}
