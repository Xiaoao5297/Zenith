package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TDoubleCharHash extends TPrimitiveHash {

    static final long serialVersionUID = 1L;
    public transient double[] _set;
    protected double no_entry_key;
    protected char no_entry_value;
    protected boolean consumeFreeSlot;

    public TDoubleCharHash() {
        this.no_entry_key = 0.0D;
        this.no_entry_value = 0;
    }

    public TDoubleCharHash(int i) {
        super(i);
        this.no_entry_key = 0.0D;
        this.no_entry_value = 0;
    }

    public TDoubleCharHash(int i, float f) {
        super(i, f);
        this.no_entry_key = 0.0D;
        this.no_entry_value = 0;
    }

    public TDoubleCharHash(int i, float f, double d0, char c0) {
        super(i, f);
        this.no_entry_key = d0;
        this.no_entry_value = c0;
    }

    public double getNoEntryKey() {
        return this.no_entry_key;
    }

    public char getNoEntryValue() {
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
        this._set[i] = this.no_entry_key;
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

    protected int XinsertKey(double d0) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        int i = abyte.length;
        int j = HashFunctions.hash(d0) & Integer.MAX_VALUE;
        int k = j % i;
        byte b0 = abyte[k];

        this.consumeFreeSlot = false;
        if (b0 == 0) {
            this.consumeFreeSlot = true;
            adouble[k] = d0;
            abyte[k] = 1;
            return k;
        } else if (b0 == 1 && adouble[k] == d0) {
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
                } while (b0 == 1 && adouble[k] != d0);
            }

            if (b0 != 2) {
                if (b0 == 1) {
                    return -k - 1;
                } else {
                    this.consumeFreeSlot = true;
                    adouble[k] = d0;
                    abyte[k] = 1;
                    return k;
                }
            } else {
                int i1;

                for (i1 = k; b0 != 0 && (b0 == 2 || adouble[k] != d0); b0 = abyte[k]) {
                    k -= l;
                    if (k < 0) {
                        k += i;
                    }
                }

                if (b0 == 1) {
                    return -k - 1;
                } else {
                    adouble[k] = d0;
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
        objectoutput.writeDouble(this.no_entry_key);
        objectoutput.writeChar(this.no_entry_value);
    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readDouble();
        this.no_entry_value = objectinput.readChar();
    }
}
