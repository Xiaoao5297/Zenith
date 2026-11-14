package gnu.trove.procedure.array;

import gnu.trove.procedure.TObjectProcedure;

public final class ToObjectArrayProceedure<T> implements TObjectProcedure<T> {

    private final T[] target;
    private int pos = 0;

    public ToObjectArrayProceedure(T[] at) {
        this.target = at;
    }

    @Override
    public final boolean execute(T t0) {
        this.target[this.pos++] = t0;
        return true;
    }
}
