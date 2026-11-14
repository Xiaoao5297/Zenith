package gnu.trove.impl.sync;

import gnu.trove.list.TIntList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessIntList extends TSynchronizedIntList implements RandomAccess {

    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessIntList(TIntList tintlist) {
        super(tintlist);
    }

    public TSynchronizedRandomAccessIntList(TIntList tintlist, Object object) {
        super(tintlist, object);
    }

    @Override
    public TIntList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedRandomAccessIntList(this.list.subList(i, j), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedIntList(this.list);
    }
}
