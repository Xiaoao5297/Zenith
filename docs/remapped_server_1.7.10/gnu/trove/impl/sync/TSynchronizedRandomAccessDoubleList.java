package gnu.trove.impl.sync;

import gnu.trove.list.TDoubleList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessDoubleList extends TSynchronizedDoubleList implements RandomAccess {

    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessDoubleList(TDoubleList tdoublelist) {
        super(tdoublelist);
    }

    public TSynchronizedRandomAccessDoubleList(TDoubleList tdoublelist, Object object) {
        super(tdoublelist, object);
    }

    @Override
    public TDoubleList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedRandomAccessDoubleList(this.list.subList(i, j), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedDoubleList(this.list);
    }
}
