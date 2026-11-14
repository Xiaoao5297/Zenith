package gnu.trove.impl.sync;

import gnu.trove.list.TFloatList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessFloatList extends TSynchronizedFloatList implements RandomAccess {

    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessFloatList(TFloatList tfloatlist) {
        super(tfloatlist);
    }

    public TSynchronizedRandomAccessFloatList(TFloatList tfloatlist, Object object) {
        super(tfloatlist, object);
    }

    @Override
    public TFloatList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedRandomAccessFloatList(this.list.subList(i, j), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedFloatList(this.list);
    }
}
