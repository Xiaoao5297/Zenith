package gnu.trove.impl.unmodifiable;

import gnu.trove.set.TByteSet;
import java.io.Serializable;

public class TUnmodifiableByteSet extends TUnmodifiableByteCollection implements TByteSet, Serializable {

    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableByteSet(TByteSet tbyteset) {
        super(tbyteset);
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
