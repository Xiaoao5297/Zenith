package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Triple<L, M, R> implements Comparable<Triple<L, M, R>>, Serializable {

    private static final long serialVersionUID = 1L;

    public Triple() {}

    public static <L, M, R> Triple<L, M, R> of(L l0, M m0, R r0) {
        return new ImmutableTriple<>(l0, m0, r0);
    }

    public abstract L getLeft();

    public abstract M getMiddle();

    public abstract R getRight();

    public int compareTo(Triple<L, M, R> triple) {
        return (new CompareToBuilder()).append(this.getLeft(), triple.getLeft()).append(this.getMiddle(), triple.getMiddle()).append(this.getRight(), triple.getRight()).toComparison();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof Triple)) {
            return false;
        } else {
            Triple triple = (Triple) object;

            return ObjectUtils.equals(this.getLeft(), triple.getLeft()) && ObjectUtils.equals(this.getMiddle(), triple.getMiddle()) && ObjectUtils.equals(this.getRight(), triple.getRight());
        }
    }

    public int hashCode() {
        return (this.getLeft() == null ? 0 : this.getLeft().hashCode()) ^ (this.getMiddle() == null ? 0 : this.getMiddle().hashCode()) ^ (this.getRight() == null ? 0 : this.getRight().hashCode());
    }

    public String toString() {
        return "" + '(' + this.getLeft() + ',' + this.getMiddle() + ',' + this.getRight() + ')';
    }

    public String toString(String s) {
        return String.format(s, this.getLeft(), this.getMiddle(), this.getRight());
    }
}
