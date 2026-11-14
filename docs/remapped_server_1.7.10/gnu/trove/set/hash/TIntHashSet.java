package gnu.trove.set.hash;

import gnu.trove.TIntCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TIntHashSet extends TIntHash implements TIntSet, Externalizable {

    static final long serialVersionUID = 1L;

    public TIntHashSet() {}

    public TIntHashSet(int i) {
        super(i);
    }

    public TIntHashSet(int i, float f) {
        super(i, f);
    }

    public TIntHashSet(int i, float f, int j) {
        super(i, f, j);
        if (j != 0) {
            Arrays.fill(this._set, j);
        }

    }

    public TIntHashSet(Collection<? extends Integer> collection) {
        this(Math.max(collection.size(), 10));
        this.addAll(collection);
    }

    public TIntHashSet(TIntCollection tintcollection) {
        this(Math.max(tintcollection.size(), 10));
        if (tintcollection instanceof TIntHashSet) {
            TIntHashSet tinthashset = (TIntHashSet) tintcollection;

            this._loadFactor = tinthashset._loadFactor;
            this.no_entry_value = tinthashset.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._set, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.addAll(tintcollection);
    }

    public TIntHashSet(int[] aint) {
        this(Math.max(aint.length, 10));
        this.addAll(aint);
    }

    @Override
    public TIntIterator iterator() {
        return new TIntHashSet.TIntHashIterator(this);
    }

    @Override
    public int[] toArray() {
        int[] aint = new int[this.size()];
        int[] aint1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                aint[j++] = aint1[i];
            }
        }

        return aint;
    }

    @Override
    public int[] toArray(int[] aint) {
        int[] aint1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                aint[j++] = aint1[i];
            }
        }

        if (aint.length > this._size) {
            aint[this._size] = this.no_entry_value;
        }

        return aint;
    }

    @Override
    public boolean add(int i) {
        int j = this.insertKey(i);

        if (j < 0) {
            return false;
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    @Override
    public boolean remove(int i) {
        int j = this.index(i);

        if (j >= 0) {
            this.removeAt(j);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        Iterator iterator = collection.iterator();

        int i;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Object object = iterator.next();

            if (!(object instanceof Integer)) {
                return false;
            }

            i = (Integer) object;
        } while (this.contains(i));

        return false;
    }

    @Override
    public boolean containsAll(TIntCollection tintcollection) {
        TIntIterator tintiterator = tintcollection.iterator();

        int i;

        do {
            if (!tintiterator.hasNext()) {
                return true;
            }

            i = tintiterator.next();
        } while (this.contains(i));

        return false;
    }

    @Override
    public boolean containsAll(int[] aint) {
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(aint[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            int i = integer;

            if (this.add(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TIntCollection tintcollection) {
        boolean flag = false;
        TIntIterator tintiterator = tintcollection.iterator();

        while (tintiterator.hasNext()) {
            int i = tintiterator.next();

            if (this.add(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(int[] aint) {
        boolean flag = false;
        int i = aint.length;

        while (i-- > 0) {
            if (this.add(aint[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TIntIterator tintiterator = this.iterator();

        while (tintiterator.hasNext()) {
            if (!collection.contains(tintiterator.next())) {
                tintiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TIntCollection tintcollection) {
        if (this == tintcollection) {
            return false;
        } else {
            boolean flag = false;
            TIntIterator tintiterator = this.iterator();

            while (tintiterator.hasNext()) {
                if (!tintcollection.contains(tintiterator.next())) {
                    tintiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean retainAll(int[] aint) {
        boolean flag = false;

        Arrays.sort(aint);
        int[] aint1 = this._set;
        byte[] abyte = this._states;

        this._autoCompactTemporaryDisable = true;
        int i = aint1.length;

        while (i-- > 0) {
            if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
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

            if (object instanceof Integer) {
                int i = (Integer) object;

                if (this.remove(i)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TIntCollection tintcollection) {
        boolean flag = false;
        TIntIterator tintiterator = tintcollection.iterator();

        while (tintiterator.hasNext()) {
            int i = tintiterator.next();

            if (this.remove(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(int[] aint) {
        boolean flag = false;
        int i = aint.length;

        while (i-- > 0) {
            if (this.remove(aint[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void clear() {
        super.clear();
        int[] aint = this._set;
        byte[] abyte = this._states;

        for (int i = aint.length; i-- > 0; abyte[i] = 0) {
            aint[i] = this.no_entry_value;
        }

    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        int[] aint = this._set;
        byte[] abyte = this._states;

        this._set = new int[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                int l = aint[k];

                this.insertKey(l);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TIntSet)) {
            return false;
        } else {
            TIntSet tintset = (TIntSet) object;

            if (tintset.size() != this.size()) {
                return false;
            } else {
                int i = this._states.length;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._states[i] != 1 || tintset.contains(this._set[i]));

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
        objectoutput.writeInt(this.no_entry_value);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeInt(this._set[i]);
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
            this.no_entry_value = objectinput.readInt();
            if (this.no_entry_value != 0) {
                Arrays.fill(this._set, this.no_entry_value);
            }
        }

        this.setUp(i);

        while (i-- > 0) {
            int j = objectinput.readInt();

            this.add(j);
        }

    }

    class TIntHashIterator extends THashPrimitiveIterator implements TIntIterator {

        private final TIntHash _hash;

        public TIntHashIterator(TIntHash tinthash) {
            super(tinthash);
            this._hash = tinthash;
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
        }
    }
}
