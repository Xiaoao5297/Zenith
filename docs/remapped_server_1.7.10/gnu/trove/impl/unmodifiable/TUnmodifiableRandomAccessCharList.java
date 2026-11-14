package gnu.trove.impl.unmodifiable;

import gnu.trove.list.TCharList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessCharList extends TUnmodifiableCharList implements RandomAccess {

    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessCharList(TCharList tcharlist) {
        super(tcharlist);
    }

    @Override
    public TCharList subList(int i, int j) {
        return new TUnmodifiableRandomAccessCharList(this.list.subList(i, j));
    }

    private Object writeReplace() {
        return new TUnmodifiableCharList(this.list);
    }
}
