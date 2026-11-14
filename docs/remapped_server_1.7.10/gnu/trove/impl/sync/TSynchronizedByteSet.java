package gnu.trove.impl.sync;

import gnu.trove.set.TByteSet;

public class TSynchronizedByteSet extends TSynchronizedByteCollection implements TByteSet {

    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedByteSet(TByteSet tbyteset) {
        super(tbyteset);
    }

    public TSynchronizedByteSet(TByteSet tbyteset, Object object) {
        super(tbyteset, object);
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
