package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TIntProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TIntByteHash extends TPrimitiveHash {

    static final long serialVersionUID = 1L;
    public transient int[] _set;
    protected int no_entry_key;
    protected byte no_entry_value;
    protected boolean consumeFreeSlot;

    public TIntByteHash() {
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TIntByteHash(int i) {
        super(i);
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TIntByteHash(int i, float f) {
        super(i, f);
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TIntByteHash(int i, float f, int j, byte b0) {
        super(i, f);
        this.no_entry_key = j;
        this.no_entry_value = b0;
    }

    public int getNoEntryKey() {
        return this.no_entry_key;
    }

    public byte getNoEntryValue() {
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
        this._set[i] = this.no_entry_key;
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

    protected int XinsertKey(int i) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        int j = abyte.length;
        int k = HashFunctions.hash(i) & Integer.MAX_VALUE;
        int l = k % j;
        byte b0 = abyte[l];

        this.consumeFreeSlot = false;
        if (b0 == 0) {
            this.consumeFreeSlot = true;
            aint[l] = i;
            abyte[l] = 1;
            return l;
        } else if (b0 == 1 && aint[l] == i) {
            return -l - 1;
        } else {
            int i1 = 1 + k % (j - 2);

            if (b0 != 2) {
                do {
                    l -= i1;
                    if (l < 0) {
                        l += j;
                    }

                    b0 = abyte[l];
                } while (b0 == 1 && aint[l] != i);
            }

            if (b0 != 2) {
                if (b0 == 1) {
                    return -l - 1;
                } else {
                    this.consumeFreeSlot = true;
                    aint[l] = i;
                    abyte[l] = 1;
                    return l;
                }
            } else {
                int j1;

                for (j1 = l; b0 != 0 && (b0 == 2 || aint[l] != i); b0 = abyte[l]) {
                    l -= i1;
                    if (l < 0) {
                        l += j;
                    }
                }

                if (b0 == 1) {
                    return -l - 1;
                } else {
                    aint[l] = i;
                    abyte[l] = 1;
                    return j1;
                }
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeInt(this.no_entry_key);
        objectoutput.writeByte(this.no_entry_value);
    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readInt();
        this.no_entry_value = objectinput.readByte();
    }
}
