package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedLongCollection implements TLongCollection, Serializable {

    private static final long serialVersionUID = 3053995032091335093L;
    final TLongCollection c;
    final Object mutex;

    public TSynchronizedLongCollection(TLongCollection tlongcollection) {
        if (tlongcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tlongcollection;
            this.mutex = this;
        }
    }

    public TSynchronizedLongCollection(TLongCollection tlongcollection, Object object) {
        this.c = tlongcollection;
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
    public boolean contains(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.contains(i);
        }
    }

    @Override
    public long[] toArray() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray();
        }
    }

    @Override
    public long[] toArray(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray(along);
        }
    }

    @Override
    public TLongIterator iterator() {
        return this.c.iterator();
    }

    @Override
    public boolean add(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.add(i);
        }
    }

    @Override
    public boolean remove(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.remove(i);
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
    public boolean containsAll(TLongCollection tlongcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(tlongcollection);
        }
    }

    @Override
    public boolean containsAll(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(along);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(collection);
        }
    }

    @Override
    public boolean addAll(TLongCollection tlongcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(tlongcollection);
        }
    }

    @Override
    public boolean addAll(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(along);
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
    public boolean removeAll(TLongCollection tlongcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(tlongcollection);
        }
    }

    @Override
    public boolean removeAll(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(along);
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
    public boolean retainAll(TLongCollection tlongcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(tlongcollection);
        }
    }

    @Override
    public boolean retainAll(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(along);
        }
    }

    @Override
    public long getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.forEach(tlongprocedure);
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
