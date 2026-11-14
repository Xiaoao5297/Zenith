package gnu.trove.stack.array;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.stack.TDoubleStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TDoubleArrayStack implements TDoubleStack, Externalizable {

    static final long serialVersionUID = 1L;
    protected TDoubleArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TDoubleArrayStack() {
        this(10);
    }

    public TDoubleArrayStack(int i) {
        this._list = new TDoubleArrayList(i);
    }

    public TDoubleArrayStack(int i, double d0) {
        this._list = new TDoubleArrayList(i, d0);
    }

    public TDoubleArrayStack(TDoubleStack tdoublestack) {
        if (tdoublestack instanceof TDoubleArrayStack) {
            TDoubleArrayStack tdoublearraystack = (TDoubleArrayStack) tdoublestack;

            this._list = new TDoubleArrayList(tdoublearraystack._list);
        } else {
            throw new UnsupportedOperationException("Only support TDoubleArrayStack");
        }
    }

    @Override
    public double getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    @Override
    public void push(double d0) {
        this._list.add(d0);
    }

    @Override
    public double pop() {
        return this._list.removeAt(this._list.size() - 1);
    }

    @Override
    public double peek() {
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
    public double[] toArray() {
        double[] adouble = this._list.toArray();

        this.reverse(adouble, 0, this.size());
        return adouble;
    }

    @Override
    public void toArray(double[] adouble) {
        int i = this.size();
        int j = i - adouble.length;

        if (j < 0) {
            j = 0;
        }

        int k = Math.min(i, adouble.length);

        this._list.toArray(adouble, j, k);
        this.reverse(adouble, 0, k);
        if (adouble.length > i) {
            adouble[i] = this._list.getNoEntryValue();
        }

    }

    private void reverse(double[] adouble, int i, int j) {
        if (i != j) {
            if (i > j) {
                throw new IllegalArgumentException("from cannot be greater than to");
            } else {
                int k = i;

                for (int l = j - 1; k < l; --l) {
                    this.swap(adouble, k, l);
                    ++k;
                }

            }
        }
    }

    private void swap(double[] adouble, int i, int j) {
        double d0 = adouble[i];

        adouble[i] = adouble[j];
        adouble[j] = d0;
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
            TDoubleArrayStack tdoublearraystack = (TDoubleArrayStack) object;

            return this._list.equals(tdoublearraystack._list);
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
        this._list = (TDoubleArrayList) objectinput.readObject();
    }
}
