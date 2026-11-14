package gnu.trove.decorator;

import gnu.trove.list.TFloatList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

public class TFloatListDecorator extends AbstractList<Float> implements List<Float>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TFloatList list;

    public TFloatListDecorator() {}

    public TFloatListDecorator(TFloatList tfloatlist) {
        this.list = tfloatlist;
    }

    public TFloatList getList() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public Float get(int i) {
        float f = this.list.get(i);

        return f == this.list.getNoEntryValue() ? null : f;
    }

    public Float set(int i, Float ofloat) {
        float f = this.list.set(i, ofloat);

        return f == this.list.getNoEntryValue() ? null : f;
    }

    public void add(int i, Float ofloat) {
        this.list.insert(i, ofloat);
    }

    public Float remove(int i) {
        float f = this.list.removeAt(i);

        return f == this.list.getNoEntryValue() ? null : f;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.list = (TFloatList) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this.list);
    }
}
