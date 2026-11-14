package gnu.trove.decorator;

import gnu.trove.iterator.TByteIterator;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class TByteSetDecorator extends AbstractSet<Byte> implements Set<Byte>, Externalizable {

    static final long serialVersionUID = 1L;
    protected TByteSet _set;

    public TByteSetDecorator() {}

    public TByteSetDecorator(TByteSet tbyteset) {
        this._set = tbyteset;
    }

    public TByteSet getSet() {
        return this._set;
    }

    public boolean add(Byte obyte) {
        return obyte != null && this._set.add(obyte);
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

                byte b0;

                do {
                    if (i-- <= 0) {
                        return true;
                    }

                    Object object1 = iterator.next();

                    if (!(object1 instanceof Byte)) {
                        return false;
                    }

                    b0 = (Byte) object1;
                } while (this._set.contains(b0));

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
        return object instanceof Byte && this._set.remove((Byte) object);
    }

    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private final TByteIterator it;

            {
                this.it = TByteSetDecorator.this._set.iterator();
            }

            public Byte next() {
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
        return !(object instanceof Byte) ? false : this._set.contains((Byte) object);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._set = (TByteSet) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._set);
    }
}
