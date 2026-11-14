package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class TDoubleSetDecorator extends AbstractSet<Double> implements Set<Double>, Externalizable {

    static final long serialVersionUID = 1L;
    protected TDoubleSet _set;

    public TDoubleSetDecorator() {}

    public TDoubleSetDecorator(TDoubleSet tdoubleset) {
        this._set = tdoubleset;
    }

    public TDoubleSet getSet() {
        return this._set;
    }

    public boolean add(Double odouble) {
        return odouble != null && this._set.add(odouble);
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

                double d0;

                do {
                    if (i-- <= 0) {
                        return true;
                    }

                    Object object1 = iterator.next();

                    if (!(object1 instanceof Double)) {
                        return false;
                    }

                    d0 = (Double) object1;
                } while (this._set.contains(d0));

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
        return object instanceof Double && this._set.remove((Double) object);
    }

    public Iterator<Double> iterator() {
        return new Iterator<Double>() {
            private final TDoubleIterator it;

            {
                this.it = TDoubleSetDecorator.this._set.iterator();
            }

            public Double next() {
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
        return !(object instanceof Double) ? false : this._set.contains((Double) object);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._set = (TDoubleSet) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._set);
    }
}
