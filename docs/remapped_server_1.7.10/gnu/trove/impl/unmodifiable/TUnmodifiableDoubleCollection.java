package gnu.trove.impl.unmodifiable;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableDoubleCollection implements TDoubleCollection, Serializable {

    private static final long serialVersionUID = 1820017752578914078L;
    final TDoubleCollection c;

    public TUnmodifiableDoubleCollection(TDoubleCollection tdoublecollection) {
        if (tdoublecollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tdoublecollection;
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
    public boolean contains(double d0) {
        return this.c.contains(d0);
    }

    @Override
    public double[] toArray() {
        return this.c.toArray();
    }

    @Override
    public double[] toArray(double[] adouble) {
        return this.c.toArray(adouble);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public double getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TDoubleProcedure tdoubleprocedure) {
        return this.c.forEach(tdoubleprocedure);
    }

    @Override
    public TDoubleIterator iterator() {
        return new TDoubleIterator() {
            TDoubleIterator i;

            {
                this.i = TUnmodifiableDoubleCollection.this.c.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public double next() {
                return this.i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.c.containsAll(collection);
    }

    @Override
    public boolean containsAll(TDoubleCollection tdoublecollection) {
        return this.c.containsAll(tdoublecollection);
    }

    @Override
    public boolean containsAll(double[] adouble) {
        return this.c.containsAll(adouble);
    }

    @Override
    public boolean addAll(TDoubleCollection tdoublecollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Double> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(double[] adouble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TDoubleCollection tdoublecollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(double[] adouble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TDoubleCollection tdoublecollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(double[] adouble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
