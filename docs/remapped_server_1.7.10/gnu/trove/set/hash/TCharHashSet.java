package gnu.trove.set.hash;

import gnu.trove.TCharCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TCharHashSet extends TCharHash implements TCharSet, Externalizable {

    static final long serialVersionUID = 1L;

    public TCharHashSet() {}

    public TCharHashSet(int i) {
        super(i);
    }

    public TCharHashSet(int i, float f) {
        super(i, f);
    }

    public TCharHashSet(int i, float f, char c0) {
        super(i, f, c0);
        if (c0 != 0) {
            Arrays.fill(this._set, c0);
        }

    }

    public TCharHashSet(Collection<? extends Character> collection) {
        this(Math.max(collection.size(), 10));
        this.addAll(collection);
    }

    public TCharHashSet(TCharCollection tcharcollection) {
        this(Math.max(tcharcollection.size(), 10));
        if (tcharcollection instanceof TCharHashSet) {
            TCharHashSet tcharhashset = (TCharHashSet) tcharcollection;

            this._loadFactor = tcharhashset._loadFactor;
            this.no_entry_value = tcharhashset.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._set, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.addAll(tcharcollection);
    }

    public TCharHashSet(char[] achar) {
        this(Math.max(achar.length, 10));
        this.addAll(achar);
    }

    @Override
    public TCharIterator iterator() {
        return new TCharHashSet.TCharHashIterator(this);
    }

    @Override
    public char[] toArray() {
        char[] achar = new char[this.size()];
        char[] achar1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                achar[j++] = achar1[i];
            }
        }

        return achar;
    }

    @Override
    public char[] toArray(char[] achar) {
        char[] achar1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                achar[j++] = achar1[i];
            }
        }

        if (achar.length > this._size) {
            achar[this._size] = this.no_entry_value;
        }

        return achar;
    }

    @Override
    public boolean add(char c0) {
        int i = this.insertKey(c0);

        if (i < 0) {
            return false;
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    @Override
    public boolean remove(char c0) {
        int i = this.index(c0);

        if (i >= 0) {
            this.removeAt(i);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        Iterator iterator = collection.iterator();

        char c0;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Object object = iterator.next();

            if (!(object instanceof Character)) {
                return false;
            }

            c0 = (Character) object;
        } while (this.contains(c0));

        return false;
    }

    @Override
    public boolean containsAll(TCharCollection tcharcollection) {
        TCharIterator tchariterator = tcharcollection.iterator();

        char c0;

        do {
            if (!tchariterator.hasNext()) {
                return true;
            }

            c0 = tchariterator.next();
        } while (this.contains(c0));

        return false;
    }

    @Override
    public boolean containsAll(char[] achar) {
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(achar[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Character> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Character character = (Character) iterator.next();
            char c0 = character;

            if (this.add(c0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TCharCollection tcharcollection) {
        boolean flag = false;
        TCharIterator tchariterator = tcharcollection.iterator();

        while (tchariterator.hasNext()) {
            char c0 = tchariterator.next();

            if (this.add(c0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(char[] achar) {
        boolean flag = false;
        int i = achar.length;

        while (i-- > 0) {
            if (this.add(achar[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TCharIterator tchariterator = this.iterator();

        while (tchariterator.hasNext()) {
            if (!collection.contains(tchariterator.next())) {
                tchariterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TCharCollection tcharcollection) {
        if (this == tcharcollection) {
            return false;
        } else {
            boolean flag = false;
            TCharIterator tchariterator = this.iterator();

            while (tchariterator.hasNext()) {
                if (!tcharcollection.contains(tchariterator.next())) {
                    tchariterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean retainAll(char[] achar) {
        boolean flag = false;

        Arrays.sort(achar);
        char[] achar1 = this._set;
        byte[] abyte = this._states;

        this._autoCompactTemporaryDisable = true;
        int i = achar1.length;

        while (i-- > 0) {
            if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                this.removeAt(i);
                flag = true;
            }
        }

        this._autoCompactTemporaryDisable = false;
        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            if (object instanceof Character) {
                char c0 = (Character) object;

                if (this.remove(c0)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TCharCollection tcharcollection) {
        boolean flag = false;
        TCharIterator tchariterator = tcharcollection.iterator();

        while (tchariterator.hasNext()) {
            char c0 = tchariterator.next();

            if (this.remove(c0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(char[] achar) {
        boolean flag = false;
        int i = achar.length;

        while (i-- > 0) {
            if (this.remove(achar[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void clear() {
        super.clear();
        char[] achar = this._set;
        byte[] abyte = this._states;

        for (int i = achar.length; i-- > 0; abyte[i] = 0) {
            achar[i] = this.no_entry_value;
        }

    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        char[] achar = this._set;
        byte[] abyte = this._states;

        this._set = new char[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                char c0 = achar[k];

                this.insertKey(c0);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TCharSet)) {
            return false;
        } else {
            TCharSet tcharset = (TCharSet) object;

            if (tcharset.size() != this.size()) {
                return false;
            } else {
                int i = this._states.length;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._states[i] != 1 || tcharset.contains(this._set[i]));

                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        int i = 0;
        int j = this._states.length;

        while (j-- > 0) {
            if (this._states[j] == 1) {
                i += HashFunctions.hash(this._set[j]);
            }
        }

        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder(this._size * 2 + 2);

        stringbuilder.append("{");
        int i = this._states.length;
        int j = 1;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                stringbuilder.append(this._set[i]);
                if (j++ < this._size) {
                    stringbuilder.append(",");
                }
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(1);
        super.writeExternal(objectoutput);
        objectoutput.writeInt(this._size);
        objectoutput.writeFloat(this._loadFactor);
        objectoutput.writeChar(this.no_entry_value);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeChar(this._set[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        byte b0 = objectinput.readByte();

        super.readExternal(objectinput);
        int i = objectinput.readInt();

        if (b0 >= 1) {
            this._loadFactor = objectinput.readFloat();
            this.no_entry_value = objectinput.readChar();
            if (this.no_entry_value != 0) {
                Arrays.fill(this._set, this.no_entry_value);
            }
        }

        this.setUp(i);

        while (i-- > 0) {
            char c0 = objectinput.readChar();

            this.add(c0);
        }

    }

    class TCharHashIterator extends THashPrimitiveIterator implements TCharIterator {

        private final TCharHash _hash;

        public TCharHashIterator(TCharHash tcharhash) {
            super(tcharhash);
            this._hash = tcharhash;
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
        }
    }
}
