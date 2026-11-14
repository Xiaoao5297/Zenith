package gnu.trove.impl.unmodifiable;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableLongCollection implements TLongCollection, Serializable {

    private static final long serialVersionUID = 1820017752578914078L;
    final TLongCollection c;

    public TUnmodifiableLongCollection(TLongCollection tlongcollection) {
        if (tlongcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tlongcollection;
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
    public boolean contains(long i) {
        return this.c.contains(i);
    }

    @Override
    public long[] toArray() {
        return this.c.toArray();
    }

    @Override
    public long[] toArray(long[] along) {
        return this.c.toArray(along);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public long getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TLongProcedure tlongprocedure) {
        return this.c.forEach(tlongprocedure);
    }

    @Override
    public TLongIterator iterator() {
        return new TLongIterator() {
            TLongIterator i;

            {
                this.i = TUnmodifiableLongCollection.this.c.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public long next() {
                return this.i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.c.containsAll(collection);
    }

    @Override
    public boolean containsAll(TLongCollection tlongcollection) {
        return this.c.containsAll(tlongcollection);
    }

    @Override
    public boolean containsAll(long[] along) {
        return this.c.containsAll(along);
    }

    @Override
    public boolean addAll(TLongCollection tlongcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(long[] along) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TLongCollection tlongcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(long[] along) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TLongCollection tlongcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(long[] along) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
