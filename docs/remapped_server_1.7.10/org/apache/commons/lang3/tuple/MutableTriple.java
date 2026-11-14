package org.apache.commons.lang3.tuple;

public class MutableTriple<L, M, R> extends Triple<L, M, R> {

    private static final long serialVersionUID = 1L;
    public L left;
    public M middle;
    public R right;

    public static <L, M, R> MutableTriple<L, M, R> of(L l0, M m0, R r0) {
        return new MutableTriple<>(l0, m0, r0);
    }

    public MutableTriple() {}

    public MutableTriple(L l0, M m0, R r0) {
        this.left = l0;
        this.middle = m0;
        this.right = r0;
    }

    @Override
    public L getLeft() {
        return this.left;
    }

    public void setLeft(L l0) {
        this.left = l0;
    }

    @Override
    public M getMiddle() {
        return this.middle;
    }

    public void setMiddle(M m0) {
        this.middle = m0;
    }

    @Override
    public R getRight() {
        return this.right;
    }

    public void setRight(R r0) {
        this.right = r0;
    }
}
