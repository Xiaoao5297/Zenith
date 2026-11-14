package gnu.trove.impl.sync;

import java.util.Set;

class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {

    private static final long serialVersionUID = 487447009682186044L;

    SynchronizedSet(Set<E> set, Object object) {
        super(set, object);
    }

    public boolean equals(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.c.equals(object);
        }
    }

    public int hashCode() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.hashCode();
        }
    }
}
