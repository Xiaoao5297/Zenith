package org.apache.commons.lang3.tuple;

public final class ImmutablePair<L, R> extends Pair<L, R> {

    private static final long serialVersionUID = 4954918890077093841L;
    public final L left;
    public final R right;

    public static <L, R> ImmutablePair<L, R> of(L l0, R r0) {
        return new ImmutablePair<>(l0, r0);
    }

    public ImmutablePair(L l0, R r0) {
        this.left = l0;
        this.right = r0;
    }

    @Override
    public L getLeft() {
        return this.left;
    }

    @Override
    public R getRight() {
        return this.right;
    }

    public R setValue(R r0) {
        throw new UnsupportedOperationException();
    }
}
