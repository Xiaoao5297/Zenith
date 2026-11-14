package gnu.trove.impl.unmodifiable;

import gnu.trove.list.TShortList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessShortList extends TUnmodifiableShortList implements RandomAccess {

    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessShortList(TShortList tshortlist) {
        super(tshortlist);
    }

    @Override
    public TShortList subList(int i, int j) {
        return new TUnmodifiableRandomAccessShortList(this.list.subList(i, j));
    }

    private Object writeReplace() {
        return new TUnmodifiableShortList(this.list);
    }
}
