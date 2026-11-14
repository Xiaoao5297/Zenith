package gnu.trove.set.hash;

import gnu.trove.TLongCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TLongHashSet extends TLongHash implements TLongSet, Externalizable {

    static final long serialVersionUID = 1L;

    public TLongHashSet() {}

    public TLongHashSet(int i) {
        super(i);
    }

    public TLongHashSet(int i, float f) {
        super(i, f);
    }

    public TLongHashSet(int i, float f, long j) {
        super(i, f, j);
        if (j != 0L) {
            Arrays.fill(this._set, j);
        }

    }

    public TLongHashSet(Collection<? extends Long> collection) {
        this(Math.max(collection.size(), 10));
        this.addAll(collection);
    }

    public TLongHashSet(TLongCollection tlongcollection) {
        this(Math.max(tlongcollection.size(), 10));
        if (tlongcollection instanceof TLongHashSet) {
            TLongHashSet tlonghashset = (TLongHashSet) tlongcollection;

            this._loadFactor = tlonghashset._loadFactor;
            this.no_entry_value = tlonghashset.no_entry_value;
            if (this.no_entry_value != 0L) {
                Arrays.fill(this._set, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.addAll(tlongcollection);
    }

    public TLongHashSet(long[] along) {
        this(Math.max(along.length, 10));
        this.addAll(along);
    }

    @Override
    public TLongIterator iterator() {
        return new TLongHashSet.TLongHashIterator(this);
    }

    @Override
    public long[] toArray() {
        long[] along = new long[this.size()];
        long[] along1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                along[j++] = along1[i];
            }
        }

        return along;
    }

    @Override
    public long[] toArray(long[] along) {
        long[] along1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                along[j++] = along1[i];
            }
        }

        if (along.length > this._size) {
            along[this._size] = this.no_entry_value;
        }

        return along;
    }

    @Override
    public boolean add(long i) {
        int j = this.insertKey(i);

        if (j < 0) {
            return false;
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    @Override
    public boolean remove(long i) {
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

        long i;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Object object = iterator.next();

            if (!(object instanceof Long)) {
                return false;
            }

            i = (Long) object;
        } while (this.contains(i));

        return false;
    }

    @Override
    public boolean containsAll(TLongCollection tlongcollection) {
        TLongIterator tlongiterator = tlongcollection.iterator();

        long i;

        do {
            if (!tlongiterator.hasNext()) {
                return true;
            }

            i = tlongiterator.next();
        } while (this.contains(i));

        return false;
    }

    @Override
    public boolean containsAll(long[] along) {
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(along[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Long olong = (Long) iterator.next();
            long i = olong;

            if (this.add(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TLongCollection tlongcollection) {
        boolean flag = false;
        TLongIterator tlongiterator = tlongcollection.iterator();

        while (tlongiterator.hasNext()) {
            long i = tlongiterator.next();

            if (this.add(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(long[] along) {
        boolean flag = false;
        int i = along.length;

        while (i-- > 0) {
            if (this.add(along[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TLongIterator tlongiterator = this.iterator();

        while (tlongiterator.hasNext()) {
            if (!collection.contains(tlongiterator.next())) {
                tlongiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TLongCollection tlongcollection) {
        if (this == tlongcollection) {
            return false;
        } else {
            boolean flag = false;
            TLongIterator tlongiterator = this.iterator();

            while (tlongiterator.hasNext()) {
                if (!tlongcollection.contains(tlongiterator.next())) {
                    tlongiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean retainAll(long[] along) {
        boolean flag = false;

        Arrays.sort(along);
        long[] along1 = this._set;
        byte[] abyte = this._states;

        this._autoCompactTemporaryDisable = true;
        int i = along1.length;

        while (i-- > 0) {
            if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
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

            if (object instanceof Long) {
                long i = (Long) object;

                if (this.remove(i)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TLongCollection tlongcollection) {
        boolean flag = false;
        TLongIterator tlongiterator = tlongcollection.iterator();

        while (tlongiterator.hasNext()) {
            long i = tlongiterator.next();

            if (this.remove(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(long[] along) {
        boolean flag = false;
        int i = along.length;

        while (i-- > 0) {
            if (this.remove(along[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void clear() {
        super.clear();
        long[] along = this._set;
        byte[] abyte = this._states;

        for (int i = along.length; i-- > 0; abyte[i] = 0) {
            along[i] = this.no_entry_value;
        }

    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        long[] along = this._set;
        byte[] abyte = this._states;

        this._set = new long[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                long l = along[k];

                this.insertKey(l);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TLongSet)) {
            return false;
        } else {
            TLongSet tlongset = (TLongSet) object;

            if (tlongset.size() != this.size()) {
                return false;
            } else {
                int i = this._states.length;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._states[i] != 1 || tlongset.contains(this._set[i]));

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
        objectoutput.writeLong(this.no_entry_value);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeLong(this._set[i]);
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
            this.no_entry_value = objectinput.readLong();
            if (this.no_entry_value != 0L) {
                Arrays.fill(this._set, this.no_entry_value);
            }
        }

        this.setUp(i);

        while (i-- > 0) {
            long j = objectinput.readLong();

            this.add(j);
        }

    }

    class TLongHashIterator extends THashPrimitiveIterator implements TLongIterator {

        private final TLongHash _hash;

        public TLongHashIterator(TLongHash tlonghash) {
            super(tlonghash);
            this._hash = tlonghash;
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
        }
    }
}
