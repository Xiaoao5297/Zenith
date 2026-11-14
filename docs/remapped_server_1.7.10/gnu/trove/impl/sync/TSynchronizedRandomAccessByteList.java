package gnu.trove.impl.sync;

import gnu.trove.list.TByteList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessByteList extends TSynchronizedByteList implements RandomAccess {

    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessByteList(TByteList tbytelist) {
        super(tbytelist);
    }

    public TSynchronizedRandomAccessByteList(TByteList tbytelist, Object object) {
        super(tbytelist, object);
    }

    @Override
    public TByteList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedRandomAccessByteList(this.list.subList(i, j), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedByteList(this.list);
    }
}
