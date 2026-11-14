package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class ByFunctionOrdering<F, T> extends Ordering<F> implements Serializable {

    final Function<F, ? extends T> function;
    final Ordering<T> ordering;
    private static final long serialVersionUID = 0L;

    ByFunctionOrdering(Function<F, ? extends T> function, Ordering<T> ordering) {
        this.function = (Function) Preconditions.checkNotNull(function);
        this.ordering = (Ordering) Preconditions.checkNotNull(ordering);
    }

    @Override
    public int compare(F f0, F f1) {
        return this.ordering.compare(this.function.apply(f0), this.function.apply(f1));
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof ByFunctionOrdering)) {
            return false;
        } else {
            ByFunctionOrdering byfunctionordering = (ByFunctionOrdering) object;

            return this.function.equals(byfunctionordering.function) && this.ordering.equals(byfunctionordering.ordering);
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.function, this.ordering);
    }

    public String toString() {
        return this.ordering + ".onResultOf(" + this.function + ")";
    }
}
