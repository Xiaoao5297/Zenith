package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedIntCollection implements TIntCollection, Serializable {

    private static final long serialVersionUID = 3053995032091335093L;
    final TIntCollection c;
    final Object mutex;

    public TSynchronizedIntCollection(TIntCollection tintcollection) {
        if (tintcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tintcollection;
            this.mutex = this;
        }
    }

    public TSynchronizedIntCollection(TIntCollection tintcollection, Object object) {
        this.c = tintcollection;
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
    public boolean contains(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.contains(i);
        }
    }

    @Override
    public int[] toArray() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray();
        }
    }

    @Override
    public int[] toArray(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray(aint);
        }
    }

    @Override
    public TIntIterator iterator() {
        return this.c.iterator();
    }

    @Override
    public boolean add(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.add(i);
        }
    }

    @Override
    public boolean remove(int i) {
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
    public boolean containsAll(TIntCollection tintcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(tintcollection);
        }
    }

    @Override
    public boolean containsAll(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(aint);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(collection);
        }
    }

    @Override
    public boolean addAll(TIntCollection tintcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(tintcollection);
        }
    }

    @Override
    public boolean addAll(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(aint);
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
    public boolean removeAll(TIntCollection tintcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(tintcollection);
        }
    }

    @Override
    public boolean removeAll(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(aint);
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
    public boolean retainAll(TIntCollection tintcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(tintcollection);
        }
    }

    @Override
    public boolean retainAll(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(aint);
        }
    }

    @Override
    public int getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.forEach(tintprocedure);
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
