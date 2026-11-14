package gnu.trove.impl.unmodifiable;

import gnu.trove.list.TIntList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessIntList extends TUnmodifiableIntList implements RandomAccess {

    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessIntList(TIntList tintlist) {
        super(tintlist);
    }

    @Override
    public TIntList subList(int i, int j) {
        return new TUnmodifiableRandomAccessIntList(this.list.subList(i, j));
    }

    private Object writeReplace() {
        return new TUnmodifiableIntList(this.list);
    }
}
