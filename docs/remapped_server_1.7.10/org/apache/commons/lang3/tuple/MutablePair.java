package org.apache.commons.lang3.tuple;

public class MutablePair<L, R> extends Pair<L, R> {

    private static final long serialVersionUID = 4954918890077093841L;
    public L left;
    public R right;

    public static <L, R> MutablePair<L, R> of(L l0, R r0) {
        return new MutablePair<>(l0, r0);
    }

    public MutablePair() {}

    public MutablePair(L l0, R r0) {
        this.left = l0;
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
    public R getRight() {
        return this.right;
    }

    public void setRight(R r0) {
        this.right = r0;
    }

    public R setValue(R r0) {
        Object object = this.getRight();

        this.setRight(r0);
        return object;
    }
}
