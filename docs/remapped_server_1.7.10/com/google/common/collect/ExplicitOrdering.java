package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class ExplicitOrdering<T> extends Ordering<T> implements Serializable {

    final ImmutableMap<T, Integer> rankMap;
    private static final long serialVersionUID = 0L;

    ExplicitOrdering(List<T> list) {
        this(buildRankMap(list));
    }

    ExplicitOrdering(ImmutableMap<T, Integer> immutablemap) {
        this.rankMap = immutablemap;
    }

    @Override
    public int compare(T t0, T t1) {
        return this.rank(t0) - this.rank(t1);
    }

    private int rank(T t0) {
        Integer integer = (Integer) this.rankMap.get(t0);

        if (integer == null) {
            throw new Ordering.IncomparableValueException(t0);
        } else {
            return integer;
        }
    }

    private static <T> ImmutableMap<T, Integer> buildRankMap(List<T> list) {
        ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
        int i = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            immutablemap_builder.put(object, i++);
        }

        return immutablemap_builder.build();
    }

    public boolean equals(@Nullable Object object) {
        if (object instanceof ExplicitOrdering) {
            ExplicitOrdering explicitordering = (ExplicitOrdering) object;

            return this.rankMap.equals(explicitordering.rankMap);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.rankMap.hashCode();
    }

    public String toString() {
        return "Ordering.explicit(" + this.rankMap.keySet() + ")";
    }
}
