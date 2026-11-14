package gnu.trove.impl.unmodifiable;

import gnu.trove.list.TByteList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessByteList extends TUnmodifiableByteList implements RandomAccess {

    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessByteList(TByteList tbytelist) {
        super(tbytelist);
    }

    @Override
    public TByteList subList(int i, int j) {
        return new TUnmodifiableRandomAccessByteList(this.list.subList(i, j));
    }

    private Object writeReplace() {
        return new TUnmodifiableByteList(this.list);
    }
}
