package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedShortCollection implements TShortCollection, Serializable {

    private static final long serialVersionUID = 3053995032091335093L;
    final TShortCollection c;
    final Object mutex;

    public TSynchronizedShortCollection(TShortCollection tshortcollection) {
        if (tshortcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tshortcollection;
            this.mutex = this;
        }
    }

    public TSynchronizedShortCollection(TShortCollection tshortcollection, Object object) {
        this.c = tshortcollection;
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
    public boolean contains(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.contains(short0);
        }
    }

    @Override
    public short[] toArray() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray();
        }
    }

    @Override
    public short[] toArray(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray(ashort);
        }
    }

    @Override
    public TShortIterator iterator() {
        return this.c.iterator();
    }

    @Override
    public boolean add(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.add(short0);
        }
    }

    @Override
    public boolean remove(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.remove(short0);
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
    public boolean containsAll(TShortCollection tshortcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(tshortcollection);
        }
    }

    @Override
    public boolean containsAll(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(ashort);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Short> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(collection);
        }
    }

    @Override
    public boolean addAll(TShortCollection tshortcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(tshortcollection);
        }
    }

    @Override
    public boolean addAll(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(ashort);
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
    public boolean removeAll(TShortCollection tshortcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(tshortcollection);
        }
    }

    @Override
    public boolean removeAll(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(ashort);
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
    public boolean retainAll(TShortCollection tshortcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(tshortcollection);
        }
    }

    @Override
    public boolean retainAll(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(ashort);
        }
    }

    @Override
    public short getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.forEach(tshortprocedure);
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
