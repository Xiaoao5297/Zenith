package gnu.trove.impl.sync;

import gnu.trove.set.TDoubleSet;

public class TSynchronizedDoubleSet extends TSynchronizedDoubleCollection implements TDoubleSet {

    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedDoubleSet(TDoubleSet tdoubleset) {
        super(tdoubleset);
    }

    public TSynchronizedDoubleSet(TDoubleSet tdoubleset, Object object) {
        super(tdoubleset, object);
    }

    @Override
    public boolean equals(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.c.equals(object);
        }
    }

    @Override
    public int hashCode() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.c.hashCode();
        }
    }
}
