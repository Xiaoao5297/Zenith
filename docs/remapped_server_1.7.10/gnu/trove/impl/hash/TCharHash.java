package gnu.trove.impl.hash;

import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TCharProcedure;
import java.util.Arrays;

public abstract class TCharHash extends TPrimitiveHash {

    static final long serialVersionUID = 1L;
    public transient char[] _set;
    protected char no_entry_value;
    protected boolean consumeFreeSlot;

    public TCharHash() {
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TCharHash(int i) {
        super(i);
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TCharHash(int i, float f) {
        super(i, f);
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._set, this.no_entry_value);
        }

    }

    public TCharHash(int i, float f, char c0) {
        super(i, f);
        this.no_entry_value = c0;
        if (c0 != 0) {
            Arrays.fill(this._set, c0);
        }

    }

    public char getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._set = new char[j];
        return j;
    }

    public boolean contains(char c0) {
        return this.index(c0) >= 0;
    }

    public boolean forEach(TCharProcedure tcharprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharprocedure.execute(achar[i]));

        return false;
    }

    @Override
    protected void removeAt(int i) {
        this._set[i] = this.no_entry_value;
        super.removeAt(i);
    }

    protected int index(char c0) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        int i = abyte.length;
        int j = HashFunctions.hash(c0) & Integer.MAX_VALUE;
        int k = j % i;
        byte b0 = abyte[k];

        return b0 == 0 ? -1 : (b0 == 1 && achar[k] == c0 ? k : this.indexRehashed(c0, k, j, b0));
    }

    int indexRehashed(char c0, int i, int j, byte b0) {
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

            if (c0 == this._set[i] && b0 != 2) {
                return i;
            }
        } while (i != i1);

        return -1;
    }

    protected int insertKey(char c0) {
        int i = HashFunctions.hash(c0) & Integer.MAX_VALUE;
        int j = i % this._states.length;
        byte b0 = this._states[j];

        this.consumeFreeSlot = false;
        if (b0 == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt(j, c0);
            return j;
        } else {
            return b0 == 1 && this._set[j] == c0 ? -j - 1 : this.insertKeyRehash(c0, j, i, b0);
        }
    }

    int insertKeyRehash(char c0, int i, int j, byte b0) {
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
                    this.insertKeyAt(j1, c0);
                    return j1;
                }

                this.consumeFreeSlot = true;
                this.insertKeyAt(i, c0);
                return i;
            }

            if (b0 == 1 && this._set[i] == c0) {
                return -i - 1;
            }
        } while (i != i1);

        if (j1 != -1) {
            this.insertKeyAt(j1, c0);
            return j1;
        } else {
            throw new IllegalStateException("No free or removed slots available. Key set full?!!");
        }
    }

    void insertKeyAt(int i, char c0) {
        this._set[i] = c0;
        this._states[i] = 1;
    }
}
