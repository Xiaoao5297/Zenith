package gnu.trove.impl.unmodifiable;

import gnu.trove.set.TIntSet;
import java.io.Serializable;

public class TUnmodifiableIntSet extends TUnmodifiableIntCollection implements TIntSet, Serializable {

    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableIntSet(TIntSet tintset) {
        super(tintset);
    }

    @Override
    public boolean equals(Object object) {
        return object == this || this.c.equals(object);
    }

    @Override
    public int hashCode() {
        return this.c.hashCode();
    }
}
