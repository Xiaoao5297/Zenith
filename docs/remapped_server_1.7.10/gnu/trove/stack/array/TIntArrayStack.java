package gnu.trove.stack.array;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.stack.TIntStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TIntArrayStack implements TIntStack, Externalizable {

    static final long serialVersionUID = 1L;
    protected TIntArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TIntArrayStack() {
        this(10);
    }

    public TIntArrayStack(int i) {
        this._list = new TIntArrayList(i);
    }

    public TIntArrayStack(int i, int j) {
        this._list = new TIntArrayList(i, j);
    }

    public TIntArrayStack(TIntStack tintstack) {
        if (tintstack instanceof TIntArrayStack) {
            TIntArrayStack tintarraystack = (TIntArrayStack) tintstack;

            this._list = new TIntArrayList(tintarraystack._list);
        } else {
            throw new UnsupportedOperationException("Only support TIntArrayStack");
        }
    }

    @Override
    public int getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(int i) {
        this._list.add(i);
    }

    @Override
    public int pop() {
        return this._list.removeAt(this._list.size() - 1);
    }

    @Override
    public int peek() {
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
    public int[] toArray() {
        int[] aint = this._list.toArray();

        this.reverse(aint, 0, this.size());
        return aint;
    }

    @Override
    public void toArray(int[] aint) {
        int i = this.size();
        int j = i - aint.length;

        if (j < 0) {
            j = 0;
        }

        int k = Math.min(i, aint.length);

        this._list.toArray(aint, j, k);
        this.reverse(aint, 0, k);
        if (aint.length > i) {
            aint[i] = this._list.getNoEntryValue();
        }

    }

    private void reverse(int[] aint, int i, int j) {
        if (i != j) {
            if (i > j) {
                throw new IllegalArgumentException("from cannot be greater than to");
            } else {
                int k = i;

                for (int l = j - 1; k < l; --l) {
                    this.swap(aint, k, l);
                    ++k;
                }

            }
        }
    }

    private void swap(int[] aint, int i, int j) {
        int k = aint[i];

        aint[i] = aint[j];
        aint[j] = k;
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
            TIntArrayStack tintarraystack = (TIntArrayStack) object;

            return this._list.equals(tintarraystack._list);
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
        this._list = (TIntArrayList) objectinput.readObject();
    }
}
