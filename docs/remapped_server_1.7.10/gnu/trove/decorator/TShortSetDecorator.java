package gnu.trove.decorator;

import gnu.trove.iterator.TShortIterator;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class TShortSetDecorator extends AbstractSet<Short> implements Set<Short>, Externalizable {

    static final long serialVersionUID = 1L;
    protected TShortSet _set;

    public TShortSetDecorator() {}

    public TShortSetDecorator(TShortSet tshortset) {
        this._set = tshortset;
    }

    public TShortSet getSet() {
        return this._set;
    }

    public boolean add(Short oshort) {
        return oshort != null && this._set.add(oshort);
    }

    public boolean equals(Object object) {
        if (this._set.equals(object)) {
            return true;
        } else if (object instanceof Set) {
            Set set = (Set) object;

            if (set.size() != this._set.size()) {
                return false;
            } else {
                Iterator iterator = set.iterator();
                int i = set.size();

                short short0;

                do {
                    if (i-- <= 0) {
                        return true;
                    }

                    Object object1 = iterator.next();

                    if (!(object1 instanceof Short)) {
                        return false;
                    }

                    short0 = (Short) object1;
                } while (this._set.contains(short0));

                return false;
            }
        } else {
            return false;
        }
    }

    public void clear() {
        this._set.clear();
    }

    public boolean remove(Object object) {
        return object instanceof Short && this._set.remove((Short) object);
    }

    public Iterator<Short> iterator() {
        return new Iterator<Short>() {
            private final TShortIterator it;

            {
                this.it = TShortSetDecorator.this._set.iterator();
            }

            public Short next() {
                return this.it.next();
            }

            public boolean hasNext() {
                return this.it.hasNext();
            }

            public void remove() {
                this.it.remove();
            }
        };
    }

    public int size() {
        return this._set.size();
    }

    public boolean isEmpty() {
        return this._set.size() == 0;
    }

    public boolean contains(Object object) {
        return !(object instanceof Short) ? false : this._set.contains((Short) object);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._set = (TShortSet) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._set);
    }
}
