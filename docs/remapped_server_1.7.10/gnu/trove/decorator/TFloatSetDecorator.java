package gnu.trove.decorator;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class TFloatSetDecorator extends AbstractSet<Float> implements Set<Float>, Externalizable {

    static final long serialVersionUID = 1L;
    protected TFloatSet _set;

    public TFloatSetDecorator() {}

    public TFloatSetDecorator(TFloatSet tfloatset) {
        this._set = tfloatset;
    }

    public TFloatSet getSet() {
        return this._set;
    }

    public boolean add(Float ofloat) {
        return ofloat != null && this._set.add(ofloat);
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

                float f;

                do {
                    if (i-- <= 0) {
                        return true;
                    }

                    Object object1 = iterator.next();

                    if (!(object1 instanceof Float)) {
                        return false;
                    }

                    f = (Float) object1;
                } while (this._set.contains(f));

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
        return object instanceof Float && this._set.remove((Float) object);
    }

    public Iterator<Float> iterator() {
        return new Iterator<Float>() {
            private final TFloatIterator it;

            {
                this.it = TFloatSetDecorator.this._set.iterator();
            }

            public Float next() {
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
        return !(object instanceof Float) ? false : this._set.contains((Float) object);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._set = (TFloatSet) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._set);
    }
}
