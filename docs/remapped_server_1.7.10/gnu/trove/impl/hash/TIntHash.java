package gnu.trove.impl.hash;

import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TIntProcedure;
import java.util.Arrays;

public abstract class TIntHash extends TPrimitiveHash {

    static final long serialVersionUID = 1L;
    public transient int[] _set;
    protected int no_entry_value;
    protected boolean consumeFreeSlot;

    public TIntHash() {
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TIntHash(int i) {
        super(i);
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TIntHash(int i, float f) {
        super(i, f);
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TIntHash(int i, float f, int j) {
        super(i, f);
        this.no_entry_value = j;
        if (j != 0) {
            Arrays.fill(this._set, j);
        }

    }

    public int getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._set = new int[j];
        return j;
    }

    public boolean contains(int i) {
        return this.index(i) >= 0;
    }

    public boolean forEach(TIntProcedure tintprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintprocedure.execute(aint[i]));

        return false;
    }

    @Override
    protected void removeAt(int i) {
        this._set[i] = this.no_entry_value;
        super.removeAt(i);
    }

    protected int index(int i) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        int j = abyte.length;
        int k = HashFunctions.hash(i) & Integer.MAX_VALUE;
        int l = k % j;
        byte b0 = abyte[l];

        return b0 == 0 ? -1 : (b0 == 1 && aint[l] == i ? l : this.indexRehashed(i, l, k, b0));
    }

    int indexRehashed(int i, int j, int k, byte b0) {
        int l = this._set.length;
        int i1 = 1 + k % (l - 2);
        int j1 = j;

        do {
            j -= i1;
            if (j < 0) {
                j += l;
            }

            b0 = this._states[j];
            if (b0 == 0) {
                return -1;
            }

            if (i == this._set[j] && b0 != 2) {
                return j;
            }
        } while (j != j1);

        return -1;
    }

    protected int insertKey(int i) {
        int j = HashFunctions.hash(i) & Integer.MAX_VALUE;
        int k = j % this._states.length;
        byte b0 = this._states[k];

        this.consumeFreeSlot = false;
        if (b0 == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt(k, i);
            return k;
        } else {
            return b0 == 1 && this._set[k] == i ? -k - 1 : this.insertKeyRehash(i, k, j, b0);
        }
    }

    int insertKeyRehash(int i, int j, int k, byte b0) {
        int l = this._set.length;
        int i1 = 1 + k % (l - 2);
        int j1 = j;
        int k1 = -1;

        do {
            if (b0 == 2 && k1 == -1) {
                k1 = j;
            }

            j -= i1;
            if (j < 0) {
                j += l;
            }

            b0 = this._states[j];
            if (b0 == 0) {
                if (k1 != -1) {
                    this.insertKeyAt(k1, i);
                    return k1;
                }

                this.consumeFreeSlot = true;
                this.insertKeyAt(j, i);
                return j;
            }

            if (b0 == 1 && this._set[j] == i) {
                return -j - 1;
            }
        } while (j != j1);

        if (k1 != -1) {
            this.insertKeyAt(k1, i);
            return k1;
        } else {
            throw new IllegalStateException("No free or removed slots available. Key set full?!!");
        }
    }

    void insertKeyAt(int i, int j) {
        this._set[i] = j;
        this._states[i] = 1;
    }
}
