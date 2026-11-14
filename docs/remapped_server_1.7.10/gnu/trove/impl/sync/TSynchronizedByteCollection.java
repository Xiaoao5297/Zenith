package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedByteCollection implements TByteCollection, Serializable {

    private static final long serialVersionUID = 3053995032091335093L;
    final TByteCollection c;
    final Object mutex;

    public TSynchronizedByteCollection(TByteCollection tbytecollection) {
        if (tbytecollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tbytecollection;
            this.mutex = this;
        }
    }

    public TSynchronizedByteCollection(TByteCollection tbytecollection, Object object) {
        this.c = tbytecollection;
        this.mutex = object;
    }

    @Override
    public int size() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.size();
        }
    }

    @Override
    public boolean isEmpty() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.isEmpty();
        }
    }

    @Override
    public boolean contains(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.contains(b0);
        }
    }

    @Override
    public byte[] toArray() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray();
        }
    }

    @Override
    public byte[] toArray(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray(abyte);
        }
    }

    @Override
    public TByteIterator iterator() {
        return this.c.iterator();
    }

    @Override
    public boolean add(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.add(b0);
        }
    }

    @Override
    public boolean remove(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.remove(b0);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(collection);
        }
    }

    @Override
    public boolean containsAll(TByteCollection tbytecollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(tbytecollection);
        }
    }

    @Override
    public boolean containsAll(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(abyte);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Byte> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(collection);
        }
    }

    @Override
    public boolean addAll(TByteCollection tbytecollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(tbytecollection);
        }
    }

    @Override
    public boolean addAll(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(abyte);
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(collection);
        }
    }

    @Override
    public boolean removeAll(TByteCollection tbytecollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(tbytecollection);
        }
    }

    @Override
    public boolean removeAll(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(abyte);
        }
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(collection);
        }
    }

    @Override
    public boolean retainAll(TByteCollection tbytecollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(tbytecollection);
        }
    }

    @Override
    public boolean retainAll(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(abyte);
        }
    }

    @Override
    public byte getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.forEach(tbyteprocedure);
        }
    }

    @Override
    public void clear() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.c.clear();
        }
    }

    public String toString() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toString();
        }
    }

    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        Object object = this.mutex;

        synchronized (this.mutex) {
            objectoutputstream.defaultWriteObject();
        }
    }
}
