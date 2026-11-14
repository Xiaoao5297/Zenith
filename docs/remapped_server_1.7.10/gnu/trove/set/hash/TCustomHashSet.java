package gnu.trove.set.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.array.ToObjectArrayProceedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TCustomHashSet<E> extends TCustomObjectHash<E> implements Set<E>, Iterable<E>, Externalizable {

    static final long serialVersionUID = 1L;

    public TCustomHashSet() {}

    public TCustomHashSet(HashingStrategy<? super E> hashingstrategy) {
        super(hashingstrategy);
    }

    public TCustomHashSet(HashingStrategy<? super E> hashingstrategy, int i) {
        super(hashingstrategy, i);
    }

    public TCustomHashSet(HashingStrategy<? super E> hashingstrategy, int i, float f) {
        super(hashingstrategy, i, f);
    }

    public TCustomHashSet(HashingStrategy<? super E> hashingstrategy, Collection<? extends E> collection) {
        this(hashingstrategy, collection.size());
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
        TCustomHashSet.HashProcedure tcustomhashset_hashprocedure = new TCustomHashSet.HashProcedure();

        this.forEach(tcustomhashset_hashprocedure);
        return tcustomhashset_hashprocedure.getHashCode();
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        int k = this.size();
        Object[] aobject = this._set;

        this._set = new Object[i];
        Arrays.fill(this._set, TCustomHashSet.FREE);
        int l = j;

        while (l-- > 0) {
            Object object = aobject[l];

            if (object != TCustomHashSet.FREE && object != TCustomHashSet.REMOVED) {
                int i1 = this.insertKey(object);

                if (i1 < 0) {
                    this.throwObjectContractViolation(this._set[-i1 - 1], object, this.size(), k, aobject);
                }
            }
        }

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
        Arrays.fill(this._set, 0, this._set.length, TCustomHashSet.FREE);
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
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TCustomHashSet.REMOVED && this._set[i] != TCustomHashSet.FREE) {
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
