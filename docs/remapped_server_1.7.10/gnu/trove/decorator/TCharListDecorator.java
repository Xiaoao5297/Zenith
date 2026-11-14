package gnu.trove.decorator;

import gnu.trove.list.TCharList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

public class TCharListDecorator extends AbstractList<Character> implements List<Character>, Externalizable, Cloneable {

    static final long serialVersionUID = 1L;
    protected TCharList list;

    public TCharListDecorator() {}

    public TCharListDecorator(TCharList tcharlist) {
        this.list = tcharlist;
    }

    public TCharList getList() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    public Character get(int i) {
        char c0 = this.list.get(i);

        return c0 == this.list.getNoEntryValue() ? null : c0;
    }

    public Character set(int i, Character character) {
        char c0 = this.list.set(i, character);

        return c0 == this.list.getNoEntryValue() ? null : c0;
    }

    public void add(int i, Character character) {
        this.list.insert(i, character);
    }

    public Character remove(int i) {
        char c0 = this.list.removeAt(i);

        return c0 == this.list.getNoEntryValue() ? null : c0;
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.list = (TCharList) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this.list);
    }
}
