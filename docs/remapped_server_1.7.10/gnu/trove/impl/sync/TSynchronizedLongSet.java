package gnu.trove.impl.sync;

import gnu.trove.set.TLongSet;

public class TSynchronizedLongSet extends TSynchronizedLongCollection implements TLongSet {

    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedLongSet(TLongSet tlongset) {
        super(tlongset);
    }

    public TSynchronizedLongSet(TLongSet tlongset, Object object) {
        super(tlongset, object);
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
