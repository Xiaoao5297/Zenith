package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TFloatProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TFloatCharHash extends TPrimitiveHash {

    static final long serialVersionUID = 1L;
    public transient float[] _set;
    protected float no_entry_key;
    protected char no_entry_value;
    protected boolean consumeFreeSlot;

    public TFloatCharHash() {
        this.no_entry_key = 0.0F;
        this.no_entry_value = 0;
    }

    public TFloatCharHash(int i) {
        super(i);
        this.no_entry_key = 0.0F;
        this.no_entry_value = 0;
    }

    public TFloatCharHash(int i, float f) {
        super(i, f);
        this.no_entry_key = 0.0F;
        this.no_entry_value = 0;
    }

    public TFloatCharHash(int i, float f, float f1, char c0) {
        super(i, f);
        this.no_entry_key = f1;
        this.no_entry_value = c0;
    }

    public float getNoEntryKey() {
        return this.no_entry_key;
    }

    public char getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._set = new float[j];
        return j;
    }

    public boolean contains(float f) {
        return this.index(f) >= 0;
    }

    public boolean forEach(TFloatProcedure tfloatprocedure) {
        byte[] abyte = this._states;
        float[] afloat = this._set;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tfloatprocedure.execute(afloat[i]));

        return false;
    }

    @Override
    protected void removeAt(int i) {
        this._set[i] = this.no_entry_key;
        super.removeAt(i);
    }

    protected int index(float f) {
        byte[] abyte = this._states;
        float[] afloat = this._set;
        int i = abyte.length;
        int j = HashFunctions.hash(f) & Integer.MAX_VALUE;
        int k = j % i;
        byte b0 = abyte[k];

        return b0 == 0 ? -1 : (b0 == 1 && afloat[k] == f ? k : this.indexRehashed(f, k, j, b0));
    }

    int indexRehashed(float f, int i, int j, byte b0) {
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

            if (f == this._set[i] && b0 != 2) {
                return i;
            }
        } while (i != i1);

        return -1;
    }

    protected int insertKey(float f) {
        int i = HashFunctions.hash(f) & Integer.MAX_VALUE;
        int j = i % this._states.length;
        byte b0 = this._states[j];

        this.consumeFreeSlot = false;
        if (b0 == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt(j, f);
            return j;
        } else {
            return b0 == 1 && this._set[j] == f ? -j - 1 : this.insertKeyRehash(f, j, i, b0);
        }
    }

    int insertKeyRehash(float f, int i, int j, byte b0) {
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
                    this.insertKeyAt(j1, f);
                    return j1;
                }

                this.consumeFreeSlot = true;
                this.insertKeyAt(i, f);
                return i;
            }

            if (b0 == 1 && this._set[i] == f) {
                return -i - 1;
            }
        } while (i != i1);

        if (j1 != -1) {
            this.insertKeyAt(j1, f);
            return j1;
        } else {
            throw new IllegalStateException("No free or removed slots available. Key set full?!!");
        }
    }

    void insertKeyAt(int i, float f) {
        this._set[i] = f;
        this._states[i] = 1;
    }

    protected int XinsertKey(float f) {
        byte[] abyte = this._states;
        float[] afloat = this._set;
        int i = abyte.length;
        int j = HashFunctions.hash(f) & Integer.MAX_VALUE;
        int k = j % i;
        byte b0 = abyte[k];

        this.consumeFreeSlot = false;
        if (b0 == 0) {
            this.consumeFreeSlot = true;
            afloat[k] = f;
            abyte[k] = 1;
            return k;
        } else if (b0 == 1 && afloat[k] == f) {
            return -k - 1;
        } else {
            int l = 1 + j % (i - 2);

            if (b0 != 2) {
                do {
                    k -= l;
                    if (k < 0) {
                        k += i;
                    }

                    b0 = abyte[k];
                } while (b0 == 1 && afloat[k] != f);
            }

            if (b0 != 2) {
                if (b0 == 1) {
                    return -k - 1;
                } else {
                    this.consumeFreeSlot = true;
                    afloat[k] = f;
                    abyte[k] = 1;
                    return k;
                }
            } else {
                int i1;

                for (i1 = k; b0 != 0 && (b0 == 2 || afloat[k] != f); b0 = abyte[k]) {
                    k -= l;
                    if (k < 0) {
                        k += i;
                    }
                }

                if (b0 == 1) {
                    return -k - 1;
                } else {
                    afloat[k] = f;
                    abyte[k] = 1;
                    return i1;
                }
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeFloat(this.no_entry_key);
        objectoutput.writeChar(this.no_entry_value);
    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readFloat();
        this.no_entry_value = objectinput.readChar();
    }
}
