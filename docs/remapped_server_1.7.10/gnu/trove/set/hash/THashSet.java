package gnu.trove.set.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.array.ToObjectArrayProceedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class THashSet<E> extends TObjectHash<E> implements Set<E>, Iterable<E>, Externalizable {

    static final long serialVersionUID = 1L;

    public THashSet() {}

    public THashSet(int i) {
        super(i);
    }

    public THashSet(int i, float f) {
        super(i, f);
    }

    public THashSet(Collection<? extends E> collection) {
        this(collection.size());
        this.addAll(collection);
    }

    public boolean add(E e0) {
        int i = this.insertKey(e0);

        if (i < 0) {
            return false;
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    public boolean equals(Object object) {
        if (!(object instanceof Set)) {
            return false;
        } else {
            Set set = (Set) object;

            return set.size() != this.size() ? false : this.containsAll(set);
        }
    }

    public int hashCode() {
        THashSet.HashProcedure thashset_hashprocedure = new THashSet.HashProcedure();

        this.forEach(thashset_hashprocedure);
        return thashset_hashprocedure.getHashCode();
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        int k = this.size();
        Object[] aobject = this._set;

        this._set = new Object[i];
        Arrays.fill(this._set, THashSet.FREE);
        int l = 0;
        int i1 = j;

        while (i1-- > 0) {
            Object object = aobject[i1];

            if (object != THashSet.FREE && object != THashSet.REMOVED) {
                int j1 = this.insertKey(object);

                if (j1 < 0) {
                    this.throwObjectContractViolation(this._set[-j1 - 1], object, this.size(), k, aobject);
                }

                ++l;
            }
        }

        reportPotentialConcurrentMod(this.size(), k);
    }

    public Object[] toArray() {
        Object[] aobject = new Object[this.size()];

        this.forEach(new ToObjectArrayProceedure<>(aobject));
        return aobject;
    }

    public <T> T[] toArray(T[] at) {
        int i = this.size();

        if (at.length < i) {
            at = (Object[]) ((Object[]) Array.newInstance(at.getClass().getComponentType(), i));
        }

        this.forEach(new ToObjectArrayProceedure<>(at));
        if (at.length > i) {
            at[i] = null;
        }

        return at;
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, THashSet.FREE);
    }

    public boolean remove(Object object) {
        int i = this.index(object);

        if (i >= 0) {
            this.removeAt(i);
            return true;
        } else {
            return false;
        }
    }

    public TObjectHashIterator<E> iterator() {
        return new TObjectHashIterator<>(this);
    }

    public boolean containsAll(Collection<?> collection) {
        Iterator iterator = collection.iterator();

        do {
            if (!iterator.hasNext()) {
                return true;
            }
        } while (this.contains(iterator.next()));

        return false;
    }

    public boolean addAll(Collection<? extends E> collection) {
        boolean flag = false;
        int i = collection.size();

        this.ensureCapacity(i);
        Iterator iterator = collection.iterator();

        while (i-- > 0) {
            if (this.add(iterator.next())) {
                flag = true;
            }
        }

        return flag;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        int i = collection.size();
        Iterator iterator = collection.iterator();

        while (i-- > 0) {
            if (this.remove(iterator.next())) {
                flag = true;
            }
        }

        return flag;
    }

    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        int i = this.size();
        TObjectHashIterator tobjecthashiterator = this.iterator();

        while (i-- > 0) {
            if (!collection.contains(tobjecthashiterator.next())) {
                tobjecthashiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEach(new TObjectProcedure<E>() {
            private boolean first = true;

            @Override
            public boolean execute(Object object) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(object);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(1);
        super.writeExternal(objectoutput);
        objectoutput.writeInt(this._size);
        this.writeEntries(objectoutput);
    }

    protected void writeEntries(ObjectOutput objectoutput) throws IOException {
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != THashSet.REMOVED && this._set[i] != THashSet.FREE) {
                objectoutput.writeObject(this._set[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        byte b0 = objectinput.readByte();

        if (b0 != 0) {
            super.readExternal(objectinput);
        }

        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();

            this.add(object);
        }

    }

    private final class HashProcedure implements TObjectProcedure<E> {

        private int h;

        private HashProcedure() {
            this.h = 0;
        }

        public int getHashCode() {
            return this.h;
        }

        @Override
        public final boolean execute(E e0) {
            this.h += HashFunctions.hash(e0);
            return true;
        }
    }
}
