package gnu.trove.impl.unmodifiable;

import gnu.trove.list.TFloatList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessFloatList extends TUnmodifiableFloatList implements RandomAccess {

    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessFloatList(TFloatList tfloatlist) {
        super(tfloatlist);
    }

    @Override
    public TFloatList subList(int i, int j) {
        return new TUnmodifiableRandomAccessFloatList(this.list.subList(i, j));
    }

    private Object writeReplace() {
        return new TUnmodifiableFloatList(this.list);
    }
}
