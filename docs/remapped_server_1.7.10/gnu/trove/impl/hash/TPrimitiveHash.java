package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;

public abstract class TPrimitiveHash extends THash {

    static final long serialVersionUID = 1L;
    public transient byte[] _states;
    public static final byte FREE = 0;
    public static final byte FULL = 1;
    public static final byte REMOVED = 2;

    public TPrimitiveHash() {}

    public TPrimitiveHash(int i) {
        this(i, 0.5F);
    }

    public TPrimitiveHash(int i, float f) {
        i = Math.max(1, i);
        this._loadFactor = f;
        this.setUp(HashFunctions.fastCeil((float) i / f));
    }

    @Override
    public int capacity() {
        return this._states.length;
    }

    @Override
    protected void removeAt(int i) {
        this._states[i] = 2;
        super.removeAt(i);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._states = new byte[j];
        return j;
    }
}
