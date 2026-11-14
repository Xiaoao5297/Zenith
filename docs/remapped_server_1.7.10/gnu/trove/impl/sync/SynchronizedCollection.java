package gnu.trove.impl.sync;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

class SynchronizedCollection<E> implements Collection<E>, Serializable {

    private static final long serialVersionUID = 3053995032091335093L;
    final Collection<E> c;
    final Object mutex;

    SynchronizedCollection(Collection<E> collection, Object object) {
        this.c = collection;
        this.mutex = object;
    }

    public int size() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.size();
        }
    }

    public boolean isEmpty() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.isEmpty();
        }
    }

    public boolean contains(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.c.contains(object);
        }
    }

    public Object[] toArray() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray();
        }
    }

    public <T> T[] toArray(T[] at) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray(at);
        }
    }

    public Iterator<E> iterator() {
        return this.c.iterator();
    }

    public boolean add(E e0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.add(e0);
        }
    }

    public boolean remove(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.c.remove(object);
        }
    }

    public boolean containsAll(Collection<?> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(collection);
        }
    }

    public boolean addAll(Collection<? extends E> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(collection);
        }
    }

    public boolean removeAll(Collection<?> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(collection);
        }
    }

    public boolean retainAll(Collection<?> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(collection);
        }
    }

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
