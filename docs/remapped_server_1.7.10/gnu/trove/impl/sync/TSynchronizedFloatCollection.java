package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedFloatCollection implements TFloatCollection, Serializable {

    private static final long serialVersionUID = 3053995032091335093L;
    final TFloatCollection c;
    final Object mutex;

    public TSynchronizedFloatCollection(TFloatCollection tfloatcollection) {
        if (tfloatcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tfloatcollection;
            this.mutex = this;
        }
    }

    public TSynchronizedFloatCollection(TFloatCollection tfloatcollection, Object object) {
        this.c = tfloatcollection;
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
    public boolean contains(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.contains(f);
        }
    }

    @Override
    public float[] toArray() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray();
        }
    }

    @Override
    public float[] toArray(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray(afloat);
        }
    }

    @Override
    public TFloatIterator iterator() {
        return this.c.iterator();
    }

    @Override
    public boolean add(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.add(f);
        }
    }

    @Override
    public boolean remove(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.remove(f);
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
    public boolean containsAll(TFloatCollection tfloatcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(tfloatcollection);
        }
    }

    @Override
    public boolean containsAll(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(afloat);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Float> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(collection);
        }
    }

    @Override
    public boolean addAll(TFloatCollection tfloatcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(tfloatcollection);
        }
    }

    @Override
    public boolean addAll(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(afloat);
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
    public boolean removeAll(TFloatCollection tfloatcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(tfloatcollection);
        }
    }

    @Override
    public boolean removeAll(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(afloat);
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
    public boolean retainAll(TFloatCollection tfloatcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(tfloatcollection);
        }
    }

    @Override
    public boolean retainAll(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(afloat);
        }
    }

    @Override
    public float getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TFloatProcedure tfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.forEach(tfloatprocedure);
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
