package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedDoubleCollection implements TDoubleCollection, Serializable {

    private static final long serialVersionUID = 3053995032091335093L;
    final TDoubleCollection c;
    final Object mutex;

    public TSynchronizedDoubleCollection(TDoubleCollection tdoublecollection) {
        if (tdoublecollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tdoublecollection;
            this.mutex = this;
        }
    }

    public TSynchronizedDoubleCollection(TDoubleCollection tdoublecollection, Object object) {
        this.c = tdoublecollection;
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
    public boolean contains(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.contains(d0);
        }
    }

    @Override
    public double[] toArray() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray();
        }
    }

    @Override
    public double[] toArray(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray(adouble);
        }
    }

    @Override
    public TDoubleIterator iterator() {
        return this.c.iterator();
    }

    @Override
    public boolean add(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.add(d0);
        }
    }

    @Override
    public boolean remove(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.remove(d0);
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
    public boolean containsAll(TDoubleCollection tdoublecollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(tdoublecollection);
        }
    }

    @Override
    public boolean containsAll(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(adouble);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Double> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(collection);
        }
    }

    @Override
    public boolean addAll(TDoubleCollection tdoublecollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(tdoublecollection);
        }
    }

    @Override
    public boolean addAll(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(adouble);
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
    public boolean removeAll(TDoubleCollection tdoublecollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(tdoublecollection);
        }
    }

    @Override
    public boolean removeAll(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(adouble);
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
    public boolean retainAll(TDoubleCollection tdoublecollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(tdoublecollection);
        }
    }

    @Override
    public boolean retainAll(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(adouble);
        }
    }

    @Override
    public double getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.forEach(tdoubleprocedure);
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
