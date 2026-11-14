package gnu.trove.decorator;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class TLongSetDecorator extends AbstractSet<Long> implements Set<Long>, Externalizable {

    static final long serialVersionUID = 1L;
    protected TLongSet _set;

    public TLongSetDecorator() {}

    public TLongSetDecorator(TLongSet tlongset) {
        this._set = tlongset;
    }

    public TLongSet getSet() {
        return this._set;
    }

    public boolean add(Long olong) {
        return olong != null && this._set.add(olong);
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

                long j;

                do {
                    if (i-- <= 0) {
                        return true;
                    }

                    Object object1 = iterator.next();

                    if (!(object1 instanceof Long)) {
                        return false;
                    }

                    j = (Long) object1;
                } while (this._set.contains(j));

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
        return object instanceof Long && this._set.remove((Long) object);
    }

    public Iterator<Long> iterator() {
        return new Iterator<Long>() {
            private final TLongIterator it;

            {
                this.it = TLongSetDecorator.this._set.iterator();
            }

            public Long next() {
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
        return !(object instanceof Long) ? false : this._set.contains((Long) object);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._set = (TLongSet) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._set);
    }
}
