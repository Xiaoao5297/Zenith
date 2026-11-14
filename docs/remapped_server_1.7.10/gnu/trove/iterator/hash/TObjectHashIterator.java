package gnu.trove.iterator.hash;

import gnu.trove.impl.hash.THashIterator;
import gnu.trove.impl.hash.TObjectHash;

public class TObjectHashIterator<E> extends THashIterator<E> {

    protected final TObjectHash _objectHash;

    public TObjectHashIterator(TObjectHash<E> tobjecthash) {
        super(tobjecthash);
        this._objectHash = tobjecthash;
    }

    @Override
    protected E objectAtIndex(int i) {
        Object object = this._objectHash._set[i];

        return object != TObjectHash.FREE && object != TObjectHash.REMOVED ? object : null;
    }
}
