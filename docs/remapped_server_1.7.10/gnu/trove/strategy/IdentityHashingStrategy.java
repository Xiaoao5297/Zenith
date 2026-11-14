package gnu.trove.strategy;

public class IdentityHashingStrategy<K> implements HashingStrategy<K> {

    static final long serialVersionUID = -5188534454583764904L;
    public static final IdentityHashingStrategy<Object> INSTANCE = new IdentityHashingStrategy<>();

    public IdentityHashingStrategy() {}

    @Override
    public int computeHashCode(K k0) {
        return System.identityHashCode(k0);
    }

    @Override
    public boolean equals(K k0, K k1) {
        return k0 == k1;
    }
}
