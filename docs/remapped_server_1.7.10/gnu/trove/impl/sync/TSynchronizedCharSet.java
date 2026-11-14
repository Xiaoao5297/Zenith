package gnu.trove.impl.sync;

import gnu.trove.set.TCharSet;

public class TSynchronizedCharSet extends TSynchronizedCharCollection implements TCharSet {

    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedCharSet(TCharSet tcharset) {
        super(tcharset);
    }

    public TSynchronizedCharSet(TCharSet tcharset, Object object) {
        super(tcharset, object);
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
