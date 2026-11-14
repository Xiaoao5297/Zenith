package gnu.trove.decorator;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class TIntSetDecorator extends AbstractSet<Integer> implements Set<Integer>, Externalizable {

    static final long serialVersionUID = 1L;
    protected TIntSet _set;

    public TIntSetDecorator() {}

    public TIntSetDecorator(TIntSet tintset) {
        this._set = tintset;
    }

    public TIntSet getSet() {
        return this._set;
    }

    public boolean add(Integer integer) {
        return integer != null && this._set.add(integer);
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

                int j;

                do {
                    if (i-- <= 0) {
                        return true;
                    }

                    Object object1 = iterator.next();

                    if (!(object1 instanceof Integer)) {
                        return false;
                    }

                    j = (Integer) object1;
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
        return object instanceof Integer && this._set.remove((Integer) object);
    }

    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private final TIntIterator it;

            {
                this.it = TIntSetDecorator.this._set.iterator();
            }

            public Integer next() {
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
        return !(object instanceof Integer) ? false : this._set.contains((Integer) object);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._set = (TIntSet) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._set);
    }
}
