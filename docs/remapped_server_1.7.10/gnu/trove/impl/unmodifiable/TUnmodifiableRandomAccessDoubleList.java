package gnu.trove.impl.unmodifiable;

import gnu.trove.list.TDoubleList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessDoubleList extends TUnmodifiableDoubleList implements RandomAccess {

    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessDoubleList(TDoubleList tdoublelist) {
        super(tdoublelist);
    }

    @Override
    public TDoubleList subList(int i, int j) {
        return new TUnmodifiableRandomAccessDoubleList(this.list.subList(i, j));
    }

    private Object writeReplace() {
        return new TUnmodifiableDoubleList(this.list);
    }
}
