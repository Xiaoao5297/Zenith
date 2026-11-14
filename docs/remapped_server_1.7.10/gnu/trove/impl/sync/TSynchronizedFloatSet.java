package gnu.trove.impl.sync;

import gnu.trove.set.TFloatSet;

public class TSynchronizedFloatSet extends TSynchronizedFloatCollection implements TFloatSet {

    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedFloatSet(TFloatSet tfloatset) {
        super(tfloatset);
    }

    public TSynchronizedFloatSet(TFloatSet tfloatset, Object object) {
        super(tfloatset, object);
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
