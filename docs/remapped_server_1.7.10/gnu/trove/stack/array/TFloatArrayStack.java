package gnu.trove.stack.array;

import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.stack.TFloatStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TFloatArrayStack implements TFloatStack, Externalizable {

    static final long serialVersionUID = 1L;
    protected TFloatArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TFloatArrayStack() {
        this(10);
    }

    public TFloatArrayStack(int i) {
        this._list = new TFloatArrayList(i);
    }

    public TFloatArrayStack(int i, float f) {
        this._list = new TFloatArrayList(i, f);
    }

    public TFloatArrayStack(TFloatStack tfloatstack) {
        if (tfloatstack instanceof TFloatArrayStack) {
            TFloatArrayStack tfloatarraystack = (TFloatArrayStack) tfloatstack;

            this._list = new TFloatArrayList(tfloatarraystack._list);
        } else {
            throw new UnsupportedOperationException("Only support TFloatArrayStack");
        }
    }

    @Override
    public float getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(float f) {
        this._list.add(f);
    }

    @Override
    public float pop() {
        return this._list.removeAt(this._list.size() - 1);
    }

    @Override
    public float peek() {
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
    public float[] toArray() {
        float[] afloat = this._list.toArray();

        this.reverse(afloat, 0, this.size());
        return afloat;
    }

    @Override
    public void toArray(float[] afloat) {
        int i = this.size();
        int j = i - afloat.length;

        if (j < 0) {
            j = 0;
        }

        int k = Math.min(i, afloat.length);

        this._list.toArray(afloat, j, k);
        this.reverse(afloat, 0, k);
        if (afloat.length > i) {
            afloat[i] = this._list.getNoEntryValue();
        }

    }

    private void reverse(float[] afloat, int i, int j) {
        if (i != j) {
            if (i > j) {
                throw new IllegalArgumentException("from cannot be greater than to");
            } else {
                int k = i;

                for (int l = j - 1; k < l; --l) {
                    this.swap(afloat, k, l);
                    ++k;
                }

            }
        }
    }

    private void swap(float[] afloat, int i, int j) {
        float f = afloat[i];

        afloat[i] = afloat[j];
        afloat[j] = f;
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
            TFloatArrayStack tfloatarraystack = (TFloatArrayStack) object;

            return this._list.equals(tfloatarraystack._list);
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
        this._list = (TFloatArrayList) objectinput.readObject();
    }
}
