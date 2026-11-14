package org.apache.commons.lang3.tuple;

public final class ImmutableTriple<L, M, R> extends Triple<L, M, R> {

    private static final long serialVersionUID = 1L;
    public final L left;
    public final M middle;
    public final R right;

    public static <L, M, R> ImmutableTriple<L, M, R> of(L l0, M m0, R r0) {
        return new ImmutableTriple<>(l0, m0, r0);
    }

    public ImmutableTriple(L l0, M m0, R r0) {
        this.left = l0;
        this.middle = m0;
        this.right = r0;
    }

    @Override
    public L getLeft() {
        return this.left;
    }

    @Override
    public M getMiddle() {
        return this.middle;
    }

    @Override
    public R getRight() {
        return this.right;
    }
}
