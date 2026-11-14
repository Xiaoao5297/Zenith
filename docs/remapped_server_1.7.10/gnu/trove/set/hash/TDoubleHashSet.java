package gnu.trove.set.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TDoubleHashSet extends TDoubleHash implements TDoubleSet, Externalizable {

    static final long serialVersionUID = 1L;

    public TDoubleHashSet() {}

    public TDoubleHashSet(int i) {
        super(i);
    }

    public TDoubleHashSet(int i, float f) {
        super(i, f);
    }

    public TDoubleHashSet(int i, float f, double d0) {
        super(i, f, d0);
        if (d0 != 0.0D) {
            Arrays.fill(this._set, d0);
        }

    }

    public TDoubleHashSet(Collection<? extends Double> collection) {
        this(Math.max(collection.size(), 10));
        this.addAll(collection);
    }

    public TDoubleHashSet(TDoubleCollection tdoublecollection) {
        this(Math.max(tdoublecollection.size(), 10));
        if (tdoublecollection instanceof TDoubleHashSet) {
            TDoubleHashSet tdoublehashset = (TDoubleHashSet) tdoublecollection;

            this._loadFactor = tdoublehashset._loadFactor;
            this.no_entry_value = tdoublehashset.no_entry_value;
            if (this.no_entry_value != 0.0D) {
                Arrays.fill(this._set, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.addAll(tdoublecollection);
    }

    public TDoubleHashSet(double[] adouble) {
        this(Math.max(adouble.length, 10));
        this.addAll(adouble);
    }

    @Override
    public TDoubleIterator iterator() {
        return new TDoubleHashSet.TDoubleHashIterator(this);
    }

    @Override
    public double[] toArray() {
        double[] adouble = new double[this.size()];
        double[] adouble1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                adouble[j++] = adouble1[i];
            }
        }

        return adouble;
    }

    @Override
    public double[] toArray(double[] adouble) {
        double[] adouble1 = this._set;
        byte[] abyte = this._states;
        int i = abyte.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                adouble[j++] = adouble1[i];
            }
        }

        if (adouble.length > this._size) {
            adouble[this._size] = this.no_entry_value;
        }

        return adouble;
    }

    @Override
    public boolean add(double d0) {
        int i = this.insertKey(d0);

        if (i < 0) {
            return false;
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    @Override
    public boolean remove(double d0) {
        int i = this.index(d0);

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

        double d0;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Object object = iterator.next();

            if (!(object instanceof Double)) {
                return false;
            }

            d0 = (Double) object;
        } while (this.contains(d0));

        return false;
    }

    @Override
    public boolean containsAll(TDoubleCollection tdoublecollection) {
        TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

        double d0;

        do {
            if (!tdoubleiterator.hasNext()) {
                return true;
            }

            d0 = tdoubleiterator.next();
        } while (this.contains(d0));

        return false;
    }

    @Override
    public boolean containsAll(double[] adouble) {
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(adouble[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Double> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Double odouble = (Double) iterator.next();
            double d0 = odouble;

            if (this.add(d0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TDoubleCollection tdoublecollection) {
        boolean flag = false;
        TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

        while (tdoubleiterator.hasNext()) {
            double d0 = tdoubleiterator.next();

            if (this.add(d0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(double[] adouble) {
        boolean flag = false;
        int i = adouble.length;

        while (i-- > 0) {
            if (this.add(adouble[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TDoubleIterator tdoubleiterator = this.iterator();

        while (tdoubleiterator.hasNext()) {
            if (!collection.contains(tdoubleiterator.next())) {
                tdoubleiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TDoubleCollection tdoublecollection) {
        if (this == tdoublecollection) {
            return false;
        } else {
            boolean flag = false;
            TDoubleIterator tdoubleiterator = this.iterator();

            while (tdoubleiterator.hasNext()) {
                if (!tdoublecollection.contains(tdoubleiterator.next())) {
                    tdoubleiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean retainAll(double[] adouble) {
        boolean flag = false;

        Arrays.sort(adouble);
        double[] adouble1 = this._set;
        byte[] abyte = this._states;

        this._autoCompactTemporaryDisable = true;
        int i = adouble1.length;

        while (i-- > 0) {
            if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
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

            if (object instanceof Double) {
                double d0 = (Double) object;

                if (this.remove(d0)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TDoubleCollection tdoublecollection) {
        boolean flag = false;
        TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

        while (tdoubleiterator.hasNext()) {
            double d0 = tdoubleiterator.next();

            if (this.remove(d0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(double[] adouble) {
        boolean flag = false;
        int i = adouble.length;

        while (i-- > 0) {
            if (this.remove(adouble[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void clear() {
        super.clear();
        double[] adouble = this._set;
        byte[] abyte = this._states;

        for (int i = adouble.length; i-- > 0; abyte[i] = 0) {
            adouble[i] = this.no_entry_value;
        }

    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        double[] adouble = this._set;
        byte[] abyte = this._states;

        this._set = new double[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                double d0 = adouble[k];

                this.insertKey(d0);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TDoubleSet)) {
            return false;
        } else {
            TDoubleSet tdoubleset = (TDoubleSet) object;

            if (tdoubleset.size() != this.size()) {
                return false;
            } else {
                int i = this._states.length;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._states[i] != 1 || tdoubleset.contains(this._set[i]));

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
        objectoutput.writeDouble(this.no_entry_value);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeDouble(this._set[i]);
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
            this.no_entry_value = objectinput.readDouble();
            if (this.no_entry_value != 0.0D) {
                Arrays.fill(this._set, this.no_entry_value);
            }
        }

        this.setUp(i);

        while (i-- > 0) {
            double d0 = objectinput.readDouble();

            this.add(d0);
        }

    }

    class TDoubleHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        private final TDoubleHash _hash;

        public TDoubleHashIterator(TDoubleHash tdoublehash) {
            super(tdoublehash);
            this._hash = tdoublehash;
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
        }
    }
}
