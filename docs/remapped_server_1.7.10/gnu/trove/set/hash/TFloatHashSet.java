package gnu.trove.set.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TFloatHashSet extends TFloatHash implements TFloatSet, Externalizable {

    static final long serialVersionUID = 1L;

    public TFloatHashSet() {}

    public TFloatHashSet(int i) {
        super(i);
    }

    public TFloatHashSet(int i, float f) {
        super(i, f);
    }

    public TFloatHashSet(int i, float f, float f1) {
        super(i, f, f1);
        if (f1 != 0.0F) {
            Arrays.fill(this._set, f1);
        }

    }

    public TFloatHashSet(Collection<? extends Float> collection) {
        this(Math.max(collection.size(), 10));
        this.addAll(collection);
    }

    public TFloatHashSet(TFloatCollection tfloatcollection) {
        this(Math.max(tfloatcollection.size(), 10));
        if (tfloatcollection instanceof TFloatHashSet) {
            TFloatHashSet tfloathashset = (TFloatHashSet) tfloatcollection;

            this._loadFactor = tfloathashset._loadFactor;
            this.no_entry_value = tfloathashset.no_entry_value;
            if (this.no_entry_value != 0.0F) {
                Arrays.fill(this._set, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.addAll(tfloatcollection);
    }

    public TFloatHashSet(float[] afloat) {
        this(Math.max(afloat.length, 10));
        this.addAll(afloat);
    }

    @Override
    public TFloatIterator iterator() {
        return new TFloatHashSet.TFloatHashIterator(this);
    }

    @Override
    public float[] toArray() {
        float[] afloat = new float[this.size()];
        float[] afloat1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                afloat[j++] = afloat1[i];
            }
        }

        return afloat;
    }

    @Override
    public float[] toArray(float[] afloat) {
        float[] afloat1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                afloat[j++] = afloat1[i];
            }
        }

        if (afloat.length > this._size) {
            afloat[this._size] = this.no_entry_value;
        }

        return afloat;
    }

    @Override
    public boolean add(float f) {
        int i = this.insertKey(f);

        if (i < 0) {
            return false;
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    @Override
    public boolean remove(float f) {
        int i = this.index(f);

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

        float f;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Object object = iterator.next();

            if (!(object instanceof Float)) {
                return false;
            }

            f = (Float) object;
        } while (this.contains(f));

        return false;
    }

    @Override
    public boolean containsAll(TFloatCollection tfloatcollection) {
        TFloatIterator tfloatiterator = tfloatcollection.iterator();

        float f;

        do {
            if (!tfloatiterator.hasNext()) {
                return true;
            }

            f = tfloatiterator.next();
        } while (this.contains(f));

        return false;
    }

    @Override
    public boolean containsAll(float[] afloat) {
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(afloat[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Float> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Float ofloat = (Float) iterator.next();
            float f = ofloat;

            if (this.add(f)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TFloatCollection tfloatcollection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = tfloatcollection.iterator();

        while (tfloatiterator.hasNext()) {
            float f = tfloatiterator.next();

            if (this.add(f)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(float[] afloat) {
        boolean flag = false;
        int i = afloat.length;

        while (i-- > 0) {
            if (this.add(afloat[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            if (!collection.contains(tfloatiterator.next())) {
                tfloatiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TFloatCollection tfloatcollection) {
        if (this == tfloatcollection) {
            return false;
        } else {
            boolean flag = false;
            TFloatIterator tfloatiterator = this.iterator();

            while (tfloatiterator.hasNext()) {
                if (!tfloatcollection.contains(tfloatiterator.next())) {
                    tfloatiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean retainAll(float[] afloat) {
        boolean flag = false;

        Arrays.sort(afloat);
        float[] afloat1 = this._set;
        byte[] abyte = this._states;

        this._autoCompactTemporaryDisable = true;
        int i = afloat1.length;

        while (i-- > 0) {
            if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
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

            if (object instanceof Float) {
                float f = (Float) object;

                if (this.remove(f)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TFloatCollection tfloatcollection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = tfloatcollection.iterator();

        while (tfloatiterator.hasNext()) {
            float f = tfloatiterator.next();

            if (this.remove(f)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(float[] afloat) {
        boolean flag = false;
        int i = afloat.length;

        while (i-- > 0) {
            if (this.remove(afloat[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void clear() {
        super.clear();
        float[] afloat = this._set;
        byte[] abyte = this._states;

        for (int i = afloat.length; i-- > 0; abyte[i] = 0) {
            afloat[i] = this.no_entry_value;
        }

    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        float[] afloat = this._set;
        byte[] abyte = this._states;

        this._set = new float[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                float f = afloat[k];

                this.insertKey(f);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TFloatSet)) {
            return false;
        } else {
            TFloatSet tfloatset = (TFloatSet) object;

            if (tfloatset.size() != this.size()) {
                return false;
            } else {
                int i = this._states.length;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._states[i] != 1 || tfloatset.contains(this._set[i]));

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
        objectoutput.writeFloat(this.no_entry_value);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeFloat(this._set[i]);
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
            this.no_entry_value = objectinput.readFloat();
            if (this.no_entry_value != 0.0F) {
                Arrays.fill(this._set, this.no_entry_value);
            }
        }

        this.setUp(i);

        while (i-- > 0) {
            float f = objectinput.readFloat();

            this.add(f);
        }

    }

    class TFloatHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        private final TFloatHash _hash;

        public TFloatHashIterator(TFloatHash tfloathash) {
            super(tfloathash);
            this._hash = tfloathash;
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
        }
    }
}
