package gnu.trove.impl.hash;

import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Arrays;

public abstract class TDoubleHash extends TPrimitiveHash {

    static final long serialVersionUID = 1L;
    public transient double[] _set;
    protected double no_entry_value;
    protected boolean consumeFreeSlot;

    public TDoubleHash() {
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0.0D) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TDoubleHash(int i) {
        super(i);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0.0D) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TDoubleHash(int i, float f) {
        super(i, f);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0.0D) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TDoubleHash(int i, float f, double d0) {
        super(i, f);
        this.no_entry_value = d0;
        if (d0 != 0.0D) {
            Arrays.fill(this._set, d0);
        }

    }

    public double getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._set = new double[j];
        return j;
    }

    public boolean contains(double d0) {
        return this.index(d0) >= 0;
    }

    public boolean forEach(TDoubleProcedure tdoubleprocedure) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tdoubleprocedure.execute(adouble[i]));

        return false;
    }

    @Override
    protected void removeAt(int i) {
        this._set[i] = this.no_entry_value;
        super.removeAt(i);
    }

    protected int index(double d0) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        int i = abyte.length;
        int j = HashFunctions.hash(d0) & Integer.MAX_VALUE;
        int k = j % i;
        byte b0 = abyte[k];

        return b0 == 0 ? -1 : (b0 == 1 && adouble[k] == d0 ? k : this.indexRehashed(d0, k, j, b0));
    }

    int indexRehashed(double d0, int i, int j, byte b0) {
        int k = this._set.length;
        int l = 1 + j % (k - 2);
        int i1 = i;

        do {
            i -= l;
            if (i < 0) {
                i += k;
            }

            b0 = this._states[i];
            if (b0 == 0) {
                return -1;
            }

            if (d0 == this._set[i] && b0 != 2) {
                return i;
            }
        } while (i != i1);

        return -1;
    }

    protected int insertKey(double d0) {
        int i = HashFunctions.hash(d0) & Integer.MAX_VALUE;
        int j = i % this._states.length;
        byte b0 = this._states[j];

        this.consumeFreeSlot = false;
        if (b0 == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt(j, d0);
            return j;
        } else {
            return b0 == 1 && this._set[j] == d0 ? -j - 1 : this.insertKeyRehash(d0, j, i, b0);
        }
    }

    int insertKeyRehash(double d0, int i, int j, byte b0) {
        int k = this._set.length;
        int l = 1 + j % (k - 2);
        int i1 = i;
        int j1 = -1;

        do {
            if (b0 == 2 && j1 == -1) {
                j1 = i;
            }

            i -= l;
            if (i < 0) {
                i += k;
            }

            b0 = this._states[i];
            if (b0 == 0) {
                if (j1 != -1) {
                    this.insertKeyAt(j1, d0);
                    return j1;
                }

                this.consumeFreeSlot = true;
                this.insertKeyAt(i, d0);
                return i;
            }

            if (b0 == 1 && this._set[i] == d0) {
                return -i - 1;
            }
        } while (i != i1);

        if (j1 != -1) {
            this.insertKeyAt(j1, d0);
            return j1;
        } else {
            throw new IllegalStateException("No free or removed slots available. Key set full?!!");
        }
    }

    void insertKeyAt(int i, double d0) {
        this._set[i] = d0;
        this._states[i] = 1;
    }
}
