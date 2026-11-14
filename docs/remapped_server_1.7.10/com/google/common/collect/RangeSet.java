package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public interface RangeSet<C extends Comparable> {

    boolean contains(C c0);

    Range<C> rangeContaining(C c0);

    boolean encloses(Range<C> range);

    boolean enclosesAll(RangeSet<C> rangeset);

    boolean isEmpty();

    Range<C> span();

    Set<Range<C>> asRanges();

    RangeSet<C> complement();

    RangeSet<C> subRangeSet(Range<C> range);

    void add(Range<C> range);

    void remove(Range<C> range);

    void clear();

    void addAll(RangeSet<C> rangeset);

    void removeAll(RangeSet<C> rangeset);

    boolean equals(@Nullable Object object);

    int hashCode();

    String toString();
}
