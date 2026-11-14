package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableByteCollection implements TByteCollection, Serializable {

    private static final long serialVersionUID = 1820017752578914078L;
    final TByteCollection c;

    public TUnmodifiableByteCollection(TByteCollection tbytecollection) {
        if (tbytecollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tbytecollection;
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
    public boolean contains(byte b0) {
        return this.c.contains(b0);
    }

    @Override
    public byte[] toArray() {
        return this.c.toArray();
    }

    @Override
    public byte[] toArray(byte[] abyte) {
        return this.c.toArray(abyte);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public byte getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TByteProcedure tbyteprocedure) {
        return this.c.forEach(tbyteprocedure);
    }

    @Override
    public TByteIterator iterator() {
        return new TByteIterator() {
            TByteIterator i;

            {
                this.i = TUnmodifiableByteCollection.this.c.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public byte next() {
                return this.i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.c.containsAll(collection);
    }

    @Override
    public boolean containsAll(TByteCollection tbytecollection) {
        return this.c.containsAll(tbytecollection);
    }

    @Override
    public boolean containsAll(byte[] abyte) {
        return this.c.containsAll(abyte);
    }

    @Override
    public boolean addAll(TByteCollection tbytecollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Byte> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(byte[] abyte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TByteCollection tbytecollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(byte[] abyte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TByteCollection tbytecollection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(byte[] abyte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
