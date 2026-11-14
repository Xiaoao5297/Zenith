package gnu.trove.decorator;

import gnu.trove.list.TShortList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

public class TShortListDecorator extends AbstractList<Short> implements List<Short>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TShortList list;

    public TShortListDecorator() {}

    public TShortListDecorator(TShortList tshortlist) {
        this.list = tshortlist;
    }

    public TShortList getList() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public Short get(int i) {
        short short0 = this.list.get(i);

        return short0 == this.list.getNoEntryValue() ? null : short0;
    }

    public Short set(int i, Short oshort) {
        short short0 = this.list.set(i, oshort);

        return short0 == this.list.getNoEntryValue() ? null : short0;
    }

    public void add(int i, Short oshort) {
        this.list.insert(i, oshort);
    }

    public Short remove(int i) {
        short short0 = this.list.removeAt(i);

        return short0 == this.list.getNoEntryValue() ? null : short0;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.list = (TShortList) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this.list);
    }
}
