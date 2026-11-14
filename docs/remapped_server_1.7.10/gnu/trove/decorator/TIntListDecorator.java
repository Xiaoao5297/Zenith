package gnu.trove.decorator;

import gnu.trove.list.TIntList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

public class TIntListDecorator extends AbstractList<Integer> implements List<Integer>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TIntList list;

    public TIntListDecorator() {}

    public TIntListDecorator(TIntList tintlist) {
        this.list = tintlist;
    }

    public TIntList getList() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public Integer get(int i) {
        int j = this.list.get(i);

        return j == this.list.getNoEntryValue() ? null : j;
    }

    public Integer set(int i, Integer integer) {
        int j = this.list.set(i, integer);

        return j == this.list.getNoEntryValue() ? null : j;
    }

    public void add(int i, Integer integer) {
        this.list.insert(i, integer);
    }

    public Integer remove(int i) {
        int j = this.list.removeAt(i);

        return j == this.list.getNoEntryValue() ? null : j;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.list = (TIntList) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this.list);
    }
}
