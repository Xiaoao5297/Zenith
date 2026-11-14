package gnu.trove.stack.array;

import gnu.trove.list.array.TLongArrayList;
import gnu.trove.stack.TLongStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TLongArrayStack implements TLongStack, Externalizable {

    static final long serialVersionUID = 1L;
    protected TLongArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TLongArrayStack() {
        this(10);
    }

    public TLongArrayStack(int i) {
        this._list = new TLongArrayList(i);
    }

    public TLongArrayStack(int i, long j) {
        this._list = new TLongArrayList(i, j);
    }

    public TLongArrayStack(TLongStack tlongstack) {
        if (tlongstack instanceof TLongArrayStack) {
            TLongArrayStack tlongarraystack = (TLongArrayStack) tlongstack;

            this._list = new TLongArrayList(tlongarraystack._list);
        } else {
            throw new UnsupportedOperationException("Only support TLongArrayStack");
        }
    }

    @Override
    public long getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(long i) {
        this._list.add(i);
    }

    @Override
    public long pop() {
        return this._list.removeAt(this._list.size() - 1);
    }

    @Override
    public long peek() {
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
    public long[] toArray() {
        long[] along = this._list.toArray();

        this.reverse(along, 0, this.size());
        return along;
    }

    @Override
    public void toArray(long[] along) {
        int i = this.size();
        int j = i - along.length;

        if (j < 0) {
            j = 0;
        }

        int k = Math.min(i, along.length);

        this._list.toArray(along, j, k);
        this.reverse(along, 0, k);
        if (along.length > i) {
            along[i] = this._list.getNoEntryValue();
        }

    }

    private void reverse(long[] along, int i, int j) {
        if (i != j) {
            if (i > j) {
                throw new IllegalArgumentException("from cannot be greater than to");
            } else {
                int k = i;

                for (int l = j - 1; k < l; --l) {
                    this.swap(along, k, l);
                    ++k;
                }

            }
        }
    }

    private void swap(long[] along, int i, int j) {
        long k = along[i];

        along[i] = along[j];
        along[j] = k;
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
            TLongArrayStack tlongarraystack = (TLongArrayStack) object;

            return this._list.equals(tlongarraystack._list);
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
        this._list = (TLongArrayList) objectinput.readObject();
    }
}
