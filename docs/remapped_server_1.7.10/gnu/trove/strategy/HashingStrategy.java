package gnu.trove.strategy;

import java.io.Serializable;

public interface HashingStrategy<T> extends Serializable {

    long serialVersionUID = 5674097166776615540L;

    int computeHashCode(T t0);

    boolean equals(T t0, T t1);
}
