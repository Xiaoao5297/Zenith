package gnu.trove.stack.array;

import gnu.trove.list.array.TByteArrayList;
import gnu.trove.stack.TByteStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TByteArrayStack implements TByteStack, Externalizable {

    static final long serialVersionUID = 1L;
    protected TByteArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TByteArrayStack() {
        this(10);
    }

    public TByteArrayStack(int i) {
        this._list = new TByteArrayList(i);
    }

    public TByteArrayStack(int i, byte b0) {
        this._list = new TByteArrayList(i, b0);
    }

    public TByteArrayStack(TByteStack tbytestack) {
        if (tbytestack instanceof TByteArrayStack) {
            TByteArrayStack tbytearraystack = (TByteArrayStack) tbytestack;

            this._list = new TByteArrayList(tbytearraystack._list);
        } else {
            throw new UnsupportedOperationException("Only support TByteArrayStack");
        }
    }

    @Override
    public byte getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(byte b0) {
        this._list.add(b0);
    }

    @Override
    public byte pop() {
        return this._list.removeAt(this._list.size() - 1);
    }

    @Override
    public byte peek() {
        return this._list.get(this._list.size() - 1);
    }

    @Override
    public int size() {
        return this._list.size();
    }

    @Override
    public void clear() {
        this._list.clear();
    }

    @Override
    public byte[] toArray() {
        byte[] abyte = this._list.toArray();

        this.reverse(abyte, 0, this.size());
        return abyte;
    }

    @Override
    public void toArray(byte[] abyte) {
        int i = this.size();
        int j = i - abyte.length;

        if (j < 0) {
            j = 0;
        }

        int k = Math.min(i, abyte.length);

        this._list.toArray(abyte, j, k);
        this.reverse(abyte, 0, k);
        if (abyte.length > i) {
            abyte[i] = this._list.getNoEntryValue();
        }

    }

    private void reverse(byte[] abyte, int i, int j) {
        if (i != j) {
            if (i > j) {
                throw new IllegalArgumentException("from cannot be greater than to");
            } else {
                int k = i;

                for (int l = j - 1; k < l; --l) {
                    this.swap(abyte, k, l);
                    ++k;
                }

            }
        }
    }

    private void swap(byte[] abyte, int i, int j) {
        byte b0 = abyte[i];

        abyte[i] = abyte[j];
        abyte[j] = b0;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");

        for (int i = this._list.size() - 1; i > 0; --i) {
            stringbuilder.append(this._list.get(i));
            stringbuilder.append(", ");
        }

        if (this.size() > 0) {
            stringbuilder.append(this._list.get(0));
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            TByteArrayStack tbytearraystack = (TByteArrayStack) object;

            return this._list.equals(tbytearraystack._list);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this._list.hashCode();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._list);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._list = (TByteArrayList) objectinput.readObject();
    }
}
