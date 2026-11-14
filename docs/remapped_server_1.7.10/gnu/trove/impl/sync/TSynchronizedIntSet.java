package gnu.trove.impl.sync;

import gnu.trove.set.TIntSet;

public class TSynchronizedIntSet extends TSynchronizedIntCollection implements TIntSet {

    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedIntSet(TIntSet tintset) {
        super(tintset);
    }

    public TSynchronizedIntSet(TIntSet tintset, Object object) {
        super(tintset, object);
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
