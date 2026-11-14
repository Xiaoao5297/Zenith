package gnu.trove.impl.unmodifiable;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableShortCollection implements TShortCollection, Serializable {

    private static final long serialVersionUID = 1820017752578914078L;
    final TShortCollection c;

    public TUnmodifiableShortCollection(TShortCollection tshortcollection) {
        if (tshortcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tshortcollection;
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
    public boolean contains(short short0) {
        return this.c.contains(short0);
    }

    @Override
    public short[] toArray() {
        return this.c.toArray();
    }

    @Override
    public short[] toArray(short[] ashort) {
        return this.c.toArray(ashort);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public short getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TShortProcedure tshortprocedure) {
        return this.c.forEach(tshortprocedure);
    }

    @Override
    public TShortIterator iterator() {
        return new TShortIterator() {
            TShortIterator i;

            {
                this.i = TUnmodifiableShortCollection.this.c.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public short next() {
                return this.i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.c.containsAll(collection);
    }

    @Override
    public boolean containsAll(TShortCollection tshortcollection) {
        return this.c.containsAll(tshortcollection);
    }

    @Override
    public boolean containsAll(short[] ashort) {
        return this.c.containsAll(ashort);
    }

    @Override
    public boolean addAll(TShortCollection tshortcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Short> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(short[] ashort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TShortCollection tshortcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(short[] ashort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TShortCollection tshortcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(short[] ashort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
