package com.google.common.collect;

import java.util.Iterator;
import javax.annotation.Nullable;

abstract class AbstractRangeSet<C extends Comparable> implements RangeSet<C> {

    AbstractRangeSet() {}

    @Override
    public boolean contains(C c0) {
        return this.rangeContaining(c0) != null;
    }

    @Override
    public abstract Range<C> rangeContaining(C c0);

    @Override
    public boolean isEmpty() {
        return this.asRanges().isEmpty();
    }

    @Override
    public void add(Range<C> range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Range<C> range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.remove(Range.all());
    }

    @Override
    public boolean enclosesAll(RangeSet<C> rangeset) {
        Iterator iterator = rangeset.asRanges().iterator();

        Range range;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            range = (Range) iterator.next();
        } while (this.encloses(range));

        return false;
    }

    @Override
    public void addAll(RangeSet<C> rangeset) {
        Iterator iterator = rangeset.asRanges().iterator();

        while (iterator.hasNext()) {
            Range range = (Range) iterator.next();

            this.add(range);
        }

    }

    @Override
    public void removeAll(RangeSet<C> rangeset) {
        Iterator iterator = rangeset.asRanges().iterator();

        while (iterator.hasNext()) {
            Range range = (Range) iterator.next();

            this.remove(range);
        }

    }

    @Override
    public abstract boolean encloses(Range<C> range);

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof RangeSet) {
            RangeSet rangeset = (RangeSet) object;

            return this.asRanges().equals(rangeset.asRanges());
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return this.asRanges().hashCode();
    }

    @Override
    public final String toString() {
        return this.asRanges().toString();
    }
}
