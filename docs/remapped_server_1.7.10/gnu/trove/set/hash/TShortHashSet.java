package gnu.trove.set.hash;

import gnu.trove.TShortCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TShortHash;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TShortHashSet extends TShortHash implements TShortSet, Externalizable {

    static final long serialVersionUID = 1L;

    public TShortHashSet() {}

    public TShortHashSet(int i) {
        super(i);
    }

    public TShortHashSet(int i, float f) {
        super(i, f);
    }

    public TShortHashSet(int i, float f, short short0) {
        super(i, f, short0);
        if (short0 != 0) {
            Arrays.fill(this._set, short0);
        }

    }

    public TShortHashSet(Collection<? extends Short> collection) {
        this(Math.max(collection.size(), 10));
        this.addAll(collection);
    }

    public TShortHashSet(TShortCollection tshortcollection) {
        this(Math.max(tshortcollection.size(), 10));
        if (tshortcollection instanceof TShortHashSet) {
            TShortHashSet tshorthashset = (TShortHashSet) tshortcollection;

            this._loadFactor = tshorthashset._loadFactor;
            this.no_entry_value = tshorthashset.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._set, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.addAll(tshortcollection);
    }

    public TShortHashSet(short[] ashort) {
        this(Math.max(ashort.length, 10));
        this.addAll(ashort);
    }

    @Override
    public TShortIterator iterator() {
        return new TShortHashSet.TShortHashIterator(this);
    }

    @Override
    public short[] toArray() {
        short[] ashort = new short[this.size()];
        short[] ashort1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                ashort[j++] = ashort1[i];
            }
        }

        return ashort;
    }

    @Override
    public short[] toArray(short[] ashort) {
        short[] ashort1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                ashort[j++] = ashort1[i];
            }
        }

        if (ashort.length > this._size) {
            ashort[this._size] = this.no_entry_value;
        }

        return ashort;
    }

    @Override
    public boolean add(short short0) {
        int i = this.insertKey(short0);

        if (i < 0) {
            return false;
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    @Override
    public boolean remove(short short0) {
        int i = this.index(short0);

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

        short short0;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Object object = iterator.next();

            if (!(object instanceof Short)) {
                return false;
            }

            short0 = (Short) object;
        } while (this.contains(short0));

        return false;
    }

    @Override
    public boolean containsAll(TShortCollection tshortcollection) {
        TShortIterator tshortiterator = tshortcollection.iterator();

        short short0;

        do {
            if (!tshortiterator.hasNext()) {
                return true;
            }

            short0 = tshortiterator.next();
        } while (this.contains(short0));

        return false;
    }

    @Override
    public boolean containsAll(short[] ashort) {
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(ashort[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Short> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Short oshort = (Short) iterator.next();
            short short0 = oshort;

            if (this.add(short0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TShortCollection tshortcollection) {
        boolean flag = false;
        TShortIterator tshortiterator = tshortcollection.iterator();

        while (tshortiterator.hasNext()) {
            short short0 = tshortiterator.next();

            if (this.add(short0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(short[] ashort) {
        boolean flag = false;
        int i = ashort.length;

        while (i-- > 0) {
            if (this.add(ashort[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TShortIterator tshortiterator = this.iterator();

        while (tshortiterator.hasNext()) {
            if (!collection.contains(tshortiterator.next())) {
                tshortiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TShortCollection tshortcollection) {
        if (this == tshortcollection) {
            return false;
        } else {
            boolean flag = false;
            TShortIterator tshortiterator = this.iterator();

            while (tshortiterator.hasNext()) {
                if (!tshortcollection.contains(tshortiterator.next())) {
                    tshortiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean retainAll(short[] ashort) {
        boolean flag = false;

        Arrays.sort(ashort);
        short[] ashort1 = this._set;
        byte[] abyte = this._states;

        this._autoCompactTemporaryDisable = true;
        int i = ashort1.length;

        while (i-- > 0) {
            if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
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

            if (object instanceof Short) {
                short short0 = (Short) object;

                if (this.remove(short0)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TShortCollection tshortcollection) {
        boolean flag = false;
        TShortIterator tshortiterator = tshortcollection.iterator();

        while (tshortiterator.hasNext()) {
            short short0 = tshortiterator.next();

            if (this.remove(short0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(short[] ashort) {
        boolean flag = false;
        int i = ashort.length;

        while (i-- > 0) {
            if (this.remove(ashort[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void clear() {
        super.clear();
        short[] ashort = this._set;
        byte[] abyte = this._states;

        for (int i = ashort.length; i-- > 0; abyte[i] = 0) {
            ashort[i] = this.no_entry_value;
        }

    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        short[] ashort = this._set;
        byte[] abyte = this._states;

        this._set = new short[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                short short0 = ashort[k];

                this.insertKey(short0);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TShortSet)) {
            return false;
        } else {
            TShortSet tshortset = (TShortSet) object;

            if (tshortset.size() != this.size()) {
                return false;
            } else {
                int i = this._states.length;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._states[i] != 1 || tshortset.contains(this._set[i]));

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
        objectoutput.writeShort(this.no_entry_value);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeShort(this._set[i]);
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
            this.no_entry_value = objectinput.readShort();
            if (this.no_entry_value != 0) {
                Arrays.fill(this._set, this.no_entry_value);
            }
        }

        this.setUp(i);

        while (i-- > 0) {
            short short0 = objectinput.readShort();

            this.add(short0);
        }

    }

    class TShortHashIterator extends THashPrimitiveIterator implements TShortIterator {

        private final TShortHash _hash;

        public TShortHashIterator(TShortHash tshorthash) {
            super(tshorthash);
            this._hash = tshorthash;
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
        }
    }
}
