package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedCharCollection implements TCharCollection, Serializable {

    private static final long serialVersionUID = 3053995032091335093L;
    final TCharCollection c;
    final Object mutex;

    public TSynchronizedCharCollection(TCharCollection tcharcollection) {
        if (tcharcollection == null) {
            throw new NullPointerException();
        } else {
            this.c = tcharcollection;
            this.mutex = this;
        }
    }

    public TSynchronizedCharCollection(TCharCollection tcharcollection, Object object) {
        this.c = tcharcollection;
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
    public boolean contains(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.contains(c0);
        }
    }

    @Override
    public char[] toArray() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray();
        }
    }

    @Override
    public char[] toArray(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.toArray(achar);
        }
    }

    @Override
    public TCharIterator iterator() {
        return this.c.iterator();
    }

    @Override
    public boolean add(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.add(c0);
        }
    }

    @Override
    public boolean remove(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.remove(c0);
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
    public boolean containsAll(TCharCollection tcharcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(tcharcollection);
        }
    }

    @Override
    public boolean containsAll(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.containsAll(achar);
        }
    }

    @Override
    public boolean addAll(Collection<? extends Character> collection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(collection);
        }
    }

    @Override
    public boolean addAll(TCharCollection tcharcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(tcharcollection);
        }
    }

    @Override
    public boolean addAll(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.addAll(achar);
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
    public boolean removeAll(TCharCollection tcharcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(tcharcollection);
        }
    }

    @Override
    public boolean removeAll(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.removeAll(achar);
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
    public boolean retainAll(TCharCollection tcharcollection) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(tcharcollection);
        }
    }

    @Override
    public boolean retainAll(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.retainAll(achar);
        }
    }

    @Override
    public char getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.forEach(tcharprocedure);
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
