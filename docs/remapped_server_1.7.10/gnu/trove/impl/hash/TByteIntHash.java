package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.procedure.TByteProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TByteIntHash extends TPrimitiveHash {

    static final long serialVersionUID = 1L;
    public transient byte[] _set;
    protected byte no_entry_key;
    protected int no_entry_value;
    protected boolean consumeFreeSlot;

    public TByteIntHash() {
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TByteIntHash(int i) {
        super(i);
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TByteIntHash(int i, float f) {
        super(i, f);
        this.no_entry_key = 0;
        this.no_entry_value = 0;
    }

    public TByteIntHash(int i, float f, byte b0, int j) {
        super(i, f);
        this.no_entry_key = b0;
        this.no_entry_value = j;
    }

    public byte getNoEntryKey() {
        return this.no_entry_key;
    }

    public int getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._set = new byte[j];
        return j;
    }

    public boolean contains(byte b0) {
        return this.index(b0) >= 0;
    }

    public boolean forEach(TByteProcedure tbyteprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbyteprocedure.execute(abyte1[i]));

        return false;
    }

    @Override
    protected void removeAt(int i) {
        this._set[i] = this.no_entry_key;
        super.removeAt(i);
    }

    protected int index(byte b0) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        int i = abyte.length;
        int j = HashFunctions.hash(b0) & Integer.MAX_VALUE;
        int k = j % i;
        byte b1 = abyte[k];

        return b1 == 0 ? -1 : (b1 == 1 && abyte1[k] == b0 ? k : this.indexRehashed(b0, k, j, b1));
    }

    int indexRehashed(byte b0, int i, int j, byte b1) {
        int k = this._set.length;
        int l = 1 + j % (k - 2);
        int i1 = i;

        do {
            i -= l;
            if (i < 0) {
                i += k;
            }

            b1 = this._states[i];
            if (b1 == 0) {
                return -1;
            }

            if (b0 == this._set[i] && b1 != 2) {
                return i;
            }
        } while (i != i1);

        return -1;
    }

    protected int insertKey(byte b0) {
        int i = HashFunctions.hash(b0) & Integer.MAX_VALUE;
        int j = i % this._states.length;
        byte b1 = this._states[j];

        this.consumeFreeSlot = false;
        if (b1 == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt(j, b0);
            return j;
        } else {
            return b1 == 1 && this._set[j] == b0 ? -j - 1 : this.insertKeyRehash(b0, j, i, b1);
        }
    }

    int insertKeyRehash(byte b0, int i, int j, byte b1) {
        int k = this._set.length;
        int l = 1 + j % (k - 2);
        int i1 = i;
        int j1 = -1;

        do {
            if (b1 == 2 && j1 == -1) {
                j1 = i;
            }

            i -= l;
            if (i < 0) {
                i += k;
            }

            b1 = this._states[i];
            if (b1 == 0) {
                if (j1 != -1) {
                    this.insertKeyAt(j1, b0);
                    return j1;
                }

                this.consumeFreeSlot = true;
                this.insertKeyAt(i, b0);
                return i;
            }

            if (b1 == 1 && this._set[i] == b0) {
                return -i - 1;
            }
        } while (i != i1);

        if (j1 != -1) {
            this.insertKeyAt(j1, b0);
            return j1;
        } else {
            throw new IllegalStateException("No free or removed slots available. Key set full?!!");
        }
    }

    void insertKeyAt(int i, byte b0) {
        this._set[i] = b0;
        this._states[i] = 1;
    }

    protected int XinsertKey(byte b0) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        int i = abyte.length;
        int j = HashFunctions.hash(b0) & Integer.MAX_VALUE;
        int k = j % i;
        byte b1 = abyte[k];

        this.consumeFreeSlot = false;
        if (b1 == 0) {
            this.consumeFreeSlot = true;
            abyte1[k] = b0;
            abyte[k] = 1;
            return k;
        } else if (b1 == 1 && abyte1[k] == b0) {
            return -k - 1;
        } else {
            int l = 1 + j % (i - 2);

            if (b1 != 2) {
                do {
                    k -= l;
                    if (k < 0) {
                        k += i;
                    }

                    b1 = abyte[k];
                } while (b1 == 1 && abyte1[k] != b0);
            }

            if (b1 != 2) {
                if (b1 == 1) {
                    return -k - 1;
                } else {
                    this.consumeFreeSlot = true;
                    abyte1[k] = b0;
                    abyte[k] = 1;
                    return k;
                }
            } else {
                int i1;

                for (i1 = k; b1 != 0 && (b1 == 2 || abyte1[k] != b0); b1 = abyte[k]) {
                    k -= l;
                    if (k < 0) {
                        k += i;
                    }
                }

                if (b1 == 1) {
                    return -k - 1;
                } else {
                    abyte1[k] = b0;
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
        objectoutput.writeByte(this.no_entry_key);
        objectoutput.writeInt(this.no_entry_value);
    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readByte();
        this.no_entry_value = objectinput.readInt();
    }
}
