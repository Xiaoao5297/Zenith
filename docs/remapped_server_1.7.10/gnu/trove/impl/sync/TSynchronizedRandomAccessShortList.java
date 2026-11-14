package gnu.trove.impl.sync;

import gnu.trove.list.TShortList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessShortList extends TSynchronizedShortList implements RandomAccess {

    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessShortList(TShortList tshortlist) {
        super(tshortlist);
    }

    public TSynchronizedRandomAccessShortList(TShortList tshortlist, Object object) {
        super(tshortlist, object);
    }

    @Override
    public TShortList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedRandomAccessShortList(this.list.subList(i, j), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedShortList(this.list);
    }
}
