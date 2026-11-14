package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableCharCollection implements TCharCollection, Serializable {

    private static final long serialVersionUID = 1820017752578914078L;
    final TCharCollection c;

    public TUnmodifiableCharCollection(TCharCollection tcharcollection) {
        if (tcharcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tcharcollection;
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
    public boolean contains(char c0) {
        return this.c.contains(c0);
    }

    @Override
    public char[] toArray() {
        return this.c.toArray();
    }

    @Override
    public char[] toArray(char[] achar) {
        return this.c.toArray(achar);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public char getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TCharProcedure tcharprocedure) {
        return this.c.forEach(tcharprocedure);
    }

    @Override
    public TCharIterator iterator() {
        return new TCharIterator() {
            TCharIterator i;

            {
                this.i = TUnmodifiableCharCollection.this.c.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public char next() {
                return this.i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.c.containsAll(collection);
    }

    @Override
    public boolean containsAll(TCharCollection tcharcollection) {
        return this.c.containsAll(tcharcollection);
    }

    @Override
    public boolean containsAll(char[] achar) {
        return this.c.containsAll(achar);
    }

    @Override
    public boolean addAll(TCharCollection tcharcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Character> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(char[] achar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TCharCollection tcharcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(char[] achar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TCharCollection tcharcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(char[] achar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
