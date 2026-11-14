package gnu.trove.impl.unmodifiable;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableIntCollection implements TIntCollection, Serializable {

    private static final long serialVersionUID = 1820017752578914078L;
    final TIntCollection c;

    public TUnmodifiableIntCollection(TIntCollection tintcollection) {
        if (tintcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tintcollection;
        }
    }

    @Override
    public int size() {
        return this.c.size();
    }

    @Override
    public boolean isEmpty() {
        return this.c.isEmpty();
    }

    @Override
    public boolean contains(int i) {
        return this.c.contains(i);
    }

    @Override
    public int[] toArray() {
        return this.c.toArray();
    }

    @Override
    public int[] toArray(int[] aint) {
        return this.c.toArray(aint);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public int getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TIntProcedure tintprocedure) {
        return this.c.forEach(tintprocedure);
    }

    @Override
    public TIntIterator iterator() {
        return new TIntIterator() {
            TIntIterator i;

            {
                this.i = TUnmodifiableIntCollection.this.c.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public int next() {
                return this.i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.c.containsAll(collection);
    }

    @Override
    public boolean containsAll(TIntCollection tintcollection) {
        return this.c.containsAll(tintcollection);
    }

    @Override
    public boolean containsAll(int[] aint) {
        return this.c.containsAll(aint);
    }

    @Override
    public boolean addAll(TIntCollection tintcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int[] aint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TIntCollection tintcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(int[] aint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TIntCollection tintcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(int[] aint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
