package gnu.trove.decorator;

import gnu.trove.list.TDoubleList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

public class TDoubleListDecorator extends AbstractList<Double> implements List<Double>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TDoubleList list;

    public TDoubleListDecorator() {}

    public TDoubleListDecorator(TDoubleList tdoublelist) {
        this.list = tdoublelist;
    }

    public TDoubleList getList() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public Double get(int i) {
        double d0 = this.list.get(i);

        return d0 == this.list.getNoEntryValue() ? null : d0;
    }

    public Double set(int i, Double odouble) {
        double d0 = this.list.set(i, odouble);

        return d0 == this.list.getNoEntryValue() ? null : d0;
    }

    public void add(int i, Double odouble) {
        this.list.insert(i, odouble);
    }

    public Double remove(int i) {
        double d0 = this.list.removeAt(i);

        return d0 == this.list.getNoEntryValue() ? null : d0;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.list = (TDoubleList) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this.list);
    }
}
