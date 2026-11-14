package gnu.trove.impl.sync;

import gnu.trove.list.TLongList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessLongList extends TSynchronizedLongList implements RandomAccess {

    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessLongList(TLongList tlonglist) {
        super(tlonglist);
    }

    public TSynchronizedRandomAccessLongList(TLongList tlonglist, Object object) {
        super(tlonglist, object);
    }

    @Override
    public TLongList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedRandomAccessLongList(this.list.subList(i, j), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedLongList(this.list);
    }
}
