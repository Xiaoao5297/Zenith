package gnu.trove.set.hash;

import gnu.trove.TByteCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TByteHashSet extends TByteHash implements TByteSet, Externalizable {

    static final long serialVersionUID = 1L;

    public TByteHashSet() {}

    public TByteHashSet(int i) {
        super(i);
    }

    public TByteHashSet(int i, float f) {
        super(i, f);
    }

    public TByteHashSet(int i, float f, byte b0) {
        super(i, f, b0);
        if (b0 != 0) {
            Arrays.fill(this._set, b0);
        }

    }

    public TByteHashSet(Collection<? extends Byte> collection) {
        this(Math.max(collection.size(), 10));
        this.addAll(collection);
    }

    public TByteHashSet(TByteCollection tbytecollection) {
        this(Math.max(tbytecollection.size(), 10));
        if (tbytecollection instanceof TByteHashSet) {
            TByteHashSet tbytehashset = (TByteHashSet) tbytecollection;

            this._loadFactor = tbytehashset._loadFactor;
            this.no_entry_value = tbytehashset.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._set, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.addAll(tbytecollection);
    }

    public TByteHashSet(byte[] abyte) {
        this(Math.max(abyte.length, 10));
        this.addAll(abyte);
    }

    @Override
    public TByteIterator iterator() {
        return new TByteHashSet.TByteHashIterator(this);
    }

    @Override
    public byte[] toArray() {
        byte[] abyte = new byte[this.size()];
        byte[] abyte1 = this._set;
        byte[] abyte2 = this._states;
        int i = abyte2.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte2[i] == 1) {
                abyte[j++] = abyte1[i];
            }
        }

        return abyte;
    }

    @Override
    public byte[] toArray(byte[] abyte) {
        byte[] abyte1 = this._set;
        byte[] abyte2 = this._states;
        int i = abyte2.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte2[i] == 1) {
                abyte[j++] = abyte1[i];
            }
        }

        if (abyte.length > this._size) {
            abyte[this._size] = this.no_entry_value;
        }

        return abyte;
    }

    @Override
    public boolean add(byte b0) {
        int i = this.insertKey(b0);

        if (i < 0) {
            return false;
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    @Override
    public boolean remove(byte b0) {
        int i = this.index(b0);

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

        byte b0;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Object object = iterator.next();

            if (!(object instanceof Byte)) {
                return false;
            }

            b0 = (Byte) object;
        } while (this.contains(b0));

        return false;
    }

    @Override
    public boolean containsAll(TByteCollection tbytecollection) {
        TByteIterator tbyteiterator = tbytecollection.iterator();

        byte b0;

        do {
            if (!tbyteiterator.hasNext()) {
                return true;
            }

            b0 = tbyteiterator.next();
        } while (this.contains(b0));

        return false;
    }

    @Override
    public boolean containsAll(byte[] abyte) {
        int i = abyte.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(abyte[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Byte obyte = (Byte) iterator.next();
            byte b0 = obyte;

            if (this.add(b0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TByteCollection tbytecollection) {
        boolean flag = false;
        TByteIterator tbyteiterator = tbytecollection.iterator();

        while (tbyteiterator.hasNext()) {
            byte b0 = tbyteiterator.next();

            if (this.add(b0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(byte[] abyte) {
        boolean flag = false;
        int i = abyte.length;

        while (i-- > 0) {
            if (this.add(abyte[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TByteIterator tbyteiterator = this.iterator();

        while (tbyteiterator.hasNext()) {
            if (!collection.contains(tbyteiterator.next())) {
                tbyteiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TByteCollection tbytecollection) {
        if (this == tbytecollection) {
            return false;
        } else {
            boolean flag = false;
            TByteIterator tbyteiterator = this.iterator();

            while (tbyteiterator.hasNext()) {
                if (!tbytecollection.contains(tbyteiterator.next())) {
                    tbyteiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean retainAll(byte[] abyte) {
        boolean flag = false;

        Arrays.sort(abyte);
        byte[] abyte1 = this._set;
        byte[] abyte2 = this._states;

        this._autoCompactTemporaryDisable = true;
        int i = abyte1.length;

        while (i-- > 0) {
            if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
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

            if (object instanceof Byte) {
                byte b0 = (Byte) object;

                if (this.remove(b0)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TByteCollection tbytecollection) {
        boolean flag = false;
        TByteIterator tbyteiterator = tbytecollection.iterator();

        while (tbyteiterator.hasNext()) {
            byte b0 = tbyteiterator.next();

            if (this.remove(b0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(byte[] abyte) {
        boolean flag = false;
        int i = abyte.length;

        while (i-- > 0) {
            if (this.remove(abyte[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void clear() {
        super.clear();
        byte[] abyte = this._set;
        byte[] abyte1 = this._states;

        for (int i = abyte.length; i-- > 0; abyte1[i] = 0) {
            abyte[i] = this.no_entry_value;
        }

    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        byte[] abyte = this._set;
        byte[] abyte1 = this._states;

        this._set = new byte[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                byte b0 = abyte[k];

                this.insertKey(b0);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TByteSet)) {
            return false;
        } else {
            TByteSet tbyteset = (TByteSet) object;

            if (tbyteset.size() != this.size()) {
                return false;
            } else {
                int i = this._states.length;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._states[i] != 1 || tbyteset.contains(this._set[i]));

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
        objectoutput.writeByte(this.no_entry_value);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeByte(this._set[i]);
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
            this.no_entry_value = objectinput.readByte();
            if (this.no_entry_value != 0) {
                Arrays.fill(this._set, this.no_entry_value);
            }
        }

        this.setUp(i);

        while (i-- > 0) {
            byte b1 = objectinput.readByte();

            this.add(b1);
        }

    }

    class TByteHashIterator extends THashPrimitiveIterator implements TByteIterator {

        private final TByteHash _hash;

        public TByteHashIterator(TByteHash tbytehash) {
            super(tbytehash);
            this._hash = tbytehash;
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
        }
    }
}
