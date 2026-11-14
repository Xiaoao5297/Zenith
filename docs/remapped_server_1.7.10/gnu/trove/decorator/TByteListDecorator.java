package gnu.trove.decorator;

import gnu.trove.list.TByteList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

public class TByteListDecorator extends AbstractList<Byte> implements List<Byte>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TByteList list;

    public TByteListDecorator() {}

    public TByteListDecorator(TByteList tbytelist) {
        this.list = tbytelist;
    }

    public TByteList getList() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public Byte get(int i) {
        byte b0 = this.list.get(i);

        return b0 == this.list.getNoEntryValue() ? null : b0;
    }

    public Byte set(int i, Byte obyte) {
        byte b0 = this.list.set(i, obyte);

        return b0 == this.list.getNoEntryValue() ? null : b0;
    }

    public void add(int i, Byte obyte) {
        this.list.insert(i, obyte);
    }

    public Byte remove(int i) {
        byte b0 = this.list.removeAt(i);

        return b0 == this.list.getNoEntryValue() ? null : b0;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.list = (TByteList) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this.list);
    }
}
