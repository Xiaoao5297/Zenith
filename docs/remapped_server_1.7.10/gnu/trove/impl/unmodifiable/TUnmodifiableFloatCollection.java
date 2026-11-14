package gnu.trove.impl.unmodifiable;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableFloatCollection implements TFloatCollection, Serializable {

    private static final long serialVersionUID = 1820017752578914078L;
    final TFloatCollection c;

    public TUnmodifiableFloatCollection(TFloatCollection tfloatcollection) {
        if (tfloatcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tfloatcollection;
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
    public boolean contains(float f) {
        return this.c.contains(f);
    }

    @Override
    public float[] toArray() {
        return this.c.toArray();
    }

    @Override
    public float[] toArray(float[] afloat) {
        return this.c.toArray(afloat);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public float getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TFloatProcedure tfloatprocedure) {
        return this.c.forEach(tfloatprocedure);
    }

    @Override
    public TFloatIterator iterator() {
        return new TFloatIterator() {
            TFloatIterator i;

            {
                this.i = TUnmodifiableFloatCollection.this.c.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public float next() {
                return this.i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.c.containsAll(collection);
    }

    @Override
    public boolean containsAll(TFloatCollection tfloatcollection) {
        return this.c.containsAll(tfloatcollection);
    }

    @Override
    public boolean containsAll(float[] afloat) {
        return this.c.containsAll(afloat);
    }

    @Override
    public boolean addAll(TFloatCollection tfloatcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Float> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(float[] afloat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TFloatCollection tfloatcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(float[] afloat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TFloatCollection tfloatcollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(float[] afloat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
