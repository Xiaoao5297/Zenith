package gnu.trove.stack.array;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.stack.TShortStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TShortArrayStack implements TShortStack, Externalizable {

    static final long serialVersionUID = 1L;
    protected TShortArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TShortArrayStack() {
        this(10);
    }

    public TShortArrayStack(int i) {
        this._list = new TShortArrayList(i);
    }

    public TShortArrayStack(int i, short short0) {
        this._list = new TShortArrayList(i, short0);
    }

    public TShortArrayStack(TShortStack tshortstack) {
        if (tshortstack instanceof TShortArrayStack) {
            TShortArrayStack tshortarraystack = (TShortArrayStack) tshortstack;

            this._list = new TShortArrayList(tshortarraystack._list);
        } else {
            throw new UnsupportedOperationException("Only support TShortArrayStack");
        }
    }

    @Override
    public short getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(short short0) {
        this._list.add(short0);
    }

    @Override
    public short pop() {
        return this._list.removeAt(this._list.size() - 1);
    }

    @Override
    public short peek() {
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
    public short[] toArray() {
        short[] ashort = this._list.toArray();

        this.reverse(ashort, 0, this.size());
        return ashort;
    }

    @Override
    public void toArray(short[] ashort) {
        int i = this.size();
        int j = i - ashort.length;

        if (j < 0) {
            j = 0;
        }

        int k = Math.min(i, ashort.length);

        this._list.toArray(ashort, j, k);
        this.reverse(ashort, 0, k);
        if (ashort.length > i) {
            ashort[i] = this._list.getNoEntryValue();
        }

    }

    private void reverse(short[] ashort, int i, int j) {
        if (i != j) {
            if (i > j) {
                throw new IllegalArgumentException("from cannot be greater than to");
            } else {
                int k = i;

                for (int l = j - 1; k < l; --l) {
                    this.swap(ashort, k, l);
                    ++k;
                }

            }
        }
    }

    private void swap(short[] ashort, int i, int j) {
        short short0 = ashort[i];

        ashort[i] = ashort[j];
        ashort[j] = short0;
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
            TShortArrayStack tshortarraystack = (TShortArrayStack) object;

            return this._list.equals(tshortarraystack._list);
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
        this._list = (TShortArrayList) objectinput.readObject();
    }
}
