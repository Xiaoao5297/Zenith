package gnu.trove.impl.unmodifiable;

import gnu.trove.set.TLongSet;
import java.io.Serializable;

public class TUnmodifiableLongSet extends TUnmodifiableLongCollection implements TLongSet, Serializable {

    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableLongSet(TLongSet tlongset) {
        super(tlongset);
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
