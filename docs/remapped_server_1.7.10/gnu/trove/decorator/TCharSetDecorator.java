package gnu.trove.decorator;

import gnu.trove.iterator.TCharIterator;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class TCharSetDecorator extends AbstractSet<Character> implements Set<Character>, Externalizable {

    static final long serialVersionUID = 1L;
    protected TCharSet _set;

    public TCharSetDecorator() {}

    public TCharSetDecorator(TCharSet tcharset) {
        this._set = tcharset;
    }

    public TCharSet getSet() {
        return this._set;
    }

    public boolean add(Character character) {
        return character != null && this._set.add(character);
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

                char c0;

                do {
                    if (i-- <= 0) {
                        return true;
                    }

                    Object object1 = iterator.next();

                    if (!(object1 instanceof Character)) {
                        return false;
                    }

                    c0 = (Character) object1;
                } while (this._set.contains(c0));

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
        return object instanceof Character && this._set.remove((Character) object);
    }

    public Iterator<Character> iterator() {
        return new Iterator<Character>() {
            private final TCharIterator it;

            {
                this.it = TCharSetDecorator.this._set.iterator();
            }

            public Character next() {
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
        return !(object instanceof Character) ? false : this._set.contains((Character) object);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._set = (TCharSet) objectinput.readObject();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeObject(this._set);
    }
}
