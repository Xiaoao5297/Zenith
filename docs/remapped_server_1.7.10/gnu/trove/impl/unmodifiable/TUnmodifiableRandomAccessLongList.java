package gnu.trove.impl.unmodifiable;

import gnu.trove.list.TLongList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessLongList extends TUnmodifiableLongList implements RandomAccess {

    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessLongList(TLongList tlonglist) {
        super(tlonglist);
    }

    @Override
    public TLongList subList(int i, int j) {
        return new TUnmodifiableRandomAccessLongList(this.list.subList(i, j));
    }

    private Object writeReplace() {
        return new TUnmodifiableLongList(this.list);
    }
}
