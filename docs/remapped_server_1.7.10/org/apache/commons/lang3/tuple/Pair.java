package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import java.util.Map.Entry;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Pair<L, R> implements Entry<L, R>, Comparable<Pair<L, R>>, Serializable {

    private static final long serialVersionUID = 4954918890077093841L;

    public Pair() {}

    public static <L, R> Pair<L, R> of(L l0, R r0) {
        return new ImmutablePair<>(l0, r0);
    }

    public abstract L getLeft();

    public abstract R getRight();

    public final L getKey() {
        return this.getLeft();
    }

    public R getValue() {
        return this.getRight();
    }

    public int compareTo(Pair<L, R> pair) {
        return (new CompareToBuilder()).append(this.getLeft(), pair.getLeft()).append(this.getRight(), pair.getRight()).toComparison();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof Entry)) {
            return false;
        } else {
            Entry entry = (Entry) object;

            return ObjectUtils.equals(this.getKey(), entry.getKey()) && ObjectUtils.equals(this.getValue(), entry.getValue());
        }
    }

    public int hashCode() {
        return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
    }

    public String toString() {
        return "" + '(' + this.getLeft() + ',' + this.getRight() + ')';
    }

    public String toString(String s) {
        return String.format(s, this.getLeft(), this.getRight());
    }
}
