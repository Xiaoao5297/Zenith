package gnu.trove.stack.array;

import gnu.trove.list.array.TCharArrayList;
import gnu.trove.stack.TCharStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TCharArrayStack implements TCharStack, Externalizable {

    static final long serialVersionUID = 1L;
    protected TCharArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TCharArrayStack() {
        this(10);
    }

    public TCharArrayStack(int i) {
        this._list = new TCharArrayList(i);
    }

    public TCharArrayStack(int i, char c0) {
        this._list = new TCharArrayList(i, c0);
    }

    public TCharArrayStack(TCharStack tcharstack) {
        if (tcharstack instanceof TCharArrayStack) {
            TCharArrayStack tchararraystack = (TCharArrayStack) tcharstack;

            this._list = new TCharArrayList(tchararraystack._list);
        } else {
            throw new UnsupportedOperationException("Only support TCharArrayStack");
        }
    }

    @Override
    public char getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(char c0) {
        this._list.add(c0);
    }

    @Override
    public char pop() {
        return this._list.removeAt(this._list.size() - 1);
    }

    @Override
    public char peek() {
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
    public char[] toArray() {
        char[] achar = this._list.toArray();

        this.reverse(achar, 0, this.size());
        return achar;
    }

    @Override
    public void toArray(char[] achar) {
        int i = this.size();
        int j = i - achar.length;

        if (j < 0) {
            j = 0;
        }

        int k = Math.min(i, achar.length);

        this._list.toArray(achar, j, k);
        this.reverse(achar, 0, k);
        if (achar.length > i) {
            achar[i] = this._list.getNoEntryValue();
        }

    }

    private void reverse(char[] achar, int i, int j) {
        if (i != j) {
            if (i > j) {
                throw new IllegalArgumentException("from cannot be greater than to");
            } else {
                int k = i;

                for (int l = j - 1; k < l; --l) {
                    this.swap(achar, k, l);
                    ++k;
                }

            }
        }
    }

    private void swap(char[] achar, int i, int j) {
        char c0 = achar[i];

        achar[i] = achar[j];
        achar[j] = c0;
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
            TCharArrayStack tchararraystack = (TCharArrayStack) object;

            return this._list.equals(tchararraystack._list);
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
        this._list = (TCharArrayList) objectinput.readObject();
    }
}
