package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
final class FunctionalEquivalence<F, T> extends Equivalence<F> implements Serializable {

    private static final long serialVersionUID = 0L;
    private final Function<F, ? extends T> function;
    private final Equivalence<T> resultEquivalence;

    FunctionalEquivalence(Function<F, ? extends T> function, Equivalence<T> equivalence) {
        this.function = (Function) Preconditions.checkNotNull(function);
        this.resultEquivalence = (Equivalence) Preconditions.checkNotNull(equivalence);
    }

    @Override
    protected boolean doEquivalent(F f0, F f1) {
        return this.resultEquivalence.equivalent(this.function.apply(f0), this.function.apply(f1));
    }

    @Override
    protected int doHash(F f0) {
        return this.resultEquivalence.hash(this.function.apply(f0));
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof FunctionalEquivalence)) {
            return false;
        } else {
            FunctionalEquivalence functionalequivalence = (FunctionalEquivalence) object;

            return this.function.equals(functionalequivalence.function) && this.resultEquivalence.equals(functionalequivalence.resultEquivalence);
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.function, this.resultEquivalence);
    }

    public String toString() {
        return this.resultEquivalence + ".onResultOf(" + this.function + ")";
    }
}
