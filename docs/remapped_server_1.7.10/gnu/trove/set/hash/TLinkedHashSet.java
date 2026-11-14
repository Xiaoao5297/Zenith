package gnu.trove.set.hash;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

public class TLinkedHashSet<E> extends THashSet<E> {

    TIntList order;

    public TLinkedHashSet() {}

    public TLinkedHashSet(int i) {
        super(i);
    }

    public TLinkedHashSet(int i, float f) {
        super(i, f);
    }

    public TLinkedHashSet(Collection<? extends E> collection) {
        super(collection);
    }

    @Override
    public int setUp(int i) {
        this.order = new TIntArrayList(i) {
            @Override
            public void ensureCapacity(int j) {
                if (j > this._data.length) {
                    int k = Math.max(TLinkedHashSet.this._set.length, j);
                    int[] aint = new int[k];

                    System.arraycopy(this._data, 0, aint, 0, this._data.length);
                    this._data = aint;
                }

            }
        };
        return super.setUp(i);
    }

    @Override
    public void clear() {
        super.clear();
        this.order.clear();
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        boolean flag = true;

        for (TObjectHashIterator tobjecthashiterator = this.iterator(); tobjecthashiterator.hasNext(); stringbuilder.append(tobjecthashiterator.next())) {
            if (flag) {
                flag = false;
            } else {
                stringbuilder.append(", ");
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    @Override
    public boolean add(E e0) {
        int i = this.insertKey(e0);

        if (i < 0) {
            return false;
        } else if (!this.order.add(i)) {
            throw new IllegalStateException("Order not changed after insert");
        } else {
            this.postInsertHook(this.consumeFreeSlot);
            return true;
        }
    }

    @Override
    protected void removeAt(int i) {
        this.order.remove(i);
        super.removeAt(i);
    }

    @Override
    protected void rehash(int i) {
        TIntLinkedList tintlinkedlist = new TIntLinkedList(this.order);
        int j = this.size();
        Object[] aobject = this._set;

        this.order.clear();
        this._set = new Object[i];
        Arrays.fill(this._set, TLinkedHashSet.FREE);
        TIntIterator tintiterator = tintlinkedlist.iterator();

        while (tintiterator.hasNext()) {
            int k = tintiterator.next();
            Object object = aobject[k];

            if (object == TLinkedHashSet.FREE || object == TLinkedHashSet.REMOVED) {
                throw new IllegalStateException("Iterating over empty location while rehashing");
            }

            if (object != TLinkedHashSet.FREE && object != TLinkedHashSet.REMOVED) {
                int l = this.insertKey(object);

                if (l < 0) {
                    this.throwObjectContractViolation(this._set[-l - 1], object, this.size(), j, aobject);
                }

                if (!this.order.add(l)) {
                    throw new IllegalStateException("Order not changed after insert");
                }
            }
        }

    }

    @Override
    protected void writeEntries(ObjectOutput objectoutput) throws IOException {
        TLinkedHashSet.WriteProcedure tlinkedhashset_writeprocedure = new TLinkedHashSet.WriteProcedure(objectoutput);

        if (!this.order.forEach(tlinkedhashset_writeprocedure)) {
            throw tlinkedhashset_writeprocedure.getIoException();
        }
    }

    @Override
    public TObjectHashIterator<E> iterator() {
        return new TObjectHashIterator<E>(this) {
            TIntIterator localIterator;
            int lastIndex;

            {
                this.localIterator = TLinkedHashSet.this.order.iterator();
            }

            @Override
            public E next() {
                this.lastIndex = this.localIterator.next();
                return this.objectAtIndex(this.lastIndex);
            }

            @Override
            public boolean hasNext() {
                return this.localIterator.hasNext();
            }

            @Override
            public void remove() {
                this.localIterator.remove();

                try {
                    this._hash.tempDisableAutoCompaction();
                    TLinkedHashSet.this.removeAt(this.lastIndex);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

            }
        };
    }

    @Override
    public boolean forEach(TObjectProcedure<? super E> tobjectprocedure) {
        TLinkedHashSet.ForEachProcedure tlinkedhashset_foreachprocedure = new TLinkedHashSet.ForEachProcedure(this._set, tobjectprocedure);

        return this.order.forEach(tlinkedhashset_foreachprocedure);
    }

    class ForEachProcedure implements TIntProcedure {

        boolean changed = false;
        final Object[] set;
        final TObjectProcedure<? super E> procedure;

        public ForEachProcedure(Object[] aobject, TObjectProcedure<? super E> tobjectprocedure) {
            this.set = aobject;
            this.procedure = tobjectprocedure;
        }

        @Override
        public boolean execute(int i) {
            return this.procedure.execute(this.set[i]);
        }
    }

    class WriteProcedure implements TIntProcedure {

        final ObjectOutput output;
        IOException ioException;

        WriteProcedure(ObjectOutput objectoutput) {
            this.output = objectoutput;
        }

        public IOException getIoException() {
            return this.ioException;
        }

        @Override
        public boolean execute(int i) {
            try {
                this.output.writeObject(TLinkedHashSet.this._set[i]);
                return true;
            } catch (IOException ioexception) {
                this.ioException = ioexception;
                return false;
            }
        }
    }
}
