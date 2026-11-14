package gnu.trove.impl.unmodifiable;

import gnu.trove.set.TDoubleSet;
import java.io.Serializable;

public class TUnmodifiableDoubleSet extends TUnmodifiableDoubleCollection implements TDoubleSet, Serializable {

    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableDoubleSet(TDoubleSet tdoubleset) {
        super(tdoubleset);
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
