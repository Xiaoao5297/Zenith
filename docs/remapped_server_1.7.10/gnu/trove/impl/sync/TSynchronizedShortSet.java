package gnu.trove.impl.sync;

import gnu.trove.set.TShortSet;

public class TSynchronizedShortSet extends TSynchronizedShortCollection implements TShortSet {

    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedShortSet(TShortSet tshortset) {
        super(tshortset);
    }

    public TSynchronizedShortSet(TShortSet tshortset, Object object) {
        super(tshortset, object);
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
