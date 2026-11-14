package gnu.trove.decorator;

import gnu.trove.list.TLongList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

public class TLongListDecorator extends AbstractList<Long> implements List<Long>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TLongList list;

    public TLongListDecorator() {}

    public TLongListDecorator(TLongList tlonglist) {
        this.list = tlonglist;
    }

    public TLongList getList() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public Long get(int i) {
        long j = this.list.get(i);

        return j == this.list.getNoEntryValue() ? null : j;
    }

    public Long set(int i, Long olong) {
        long j = this.list.set(i, olong);

        return j == this.list.getNoEntryValue() ? null : j;
    }

    public void add(int i, Long olong) {
        this.list.insert(i, olong);
    }

    public Long remove(int i) {
        long j = this.list.removeAt(i);

        return j == this.list.getNoEntryValue() ? null : j;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.list = (TLongList) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this.list);
    }
}
