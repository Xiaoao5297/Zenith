package gnu.trove.impl.unmodifiable;

import gnu.trove.set.TShortSet;
import java.io.Serializable;

public class TUnmodifiableShortSet extends TUnmodifiableShortCollection implements TShortSet, Serializable {

    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableShortSet(TShortSet tshortset) {
        super(tshortset);
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
