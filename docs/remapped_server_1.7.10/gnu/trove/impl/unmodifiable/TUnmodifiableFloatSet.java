package gnu.trove.impl.unmodifiable;

import gnu.trove.set.TFloatSet;
import java.io.Serializable;

public class TUnmodifiableFloatSet extends TUnmodifiableFloatCollection implements TFloatSet, Serializable {

    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableFloatSet(TFloatSet tfloatset) {
        super(tfloatset);
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
