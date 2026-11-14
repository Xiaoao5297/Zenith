package gnu.trove.impl.sync;

import gnu.trove.list.TCharList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessCharList extends TSynchronizedCharList implements RandomAccess {

    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessCharList(TCharList tcharlist) {
        super(tcharlist);
    }

    public TSynchronizedRandomAccessCharList(TCharList tcharlist, Object object) {
        super(tcharlist, object);
    }

    @Override
    public TCharList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedRandomAccessCharList(this.list.subList(i, j), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedCharList(this.list);
    }
}
