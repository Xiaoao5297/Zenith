package gnu.trove.impl.hash;

import gnu.trove.strategy.HashingStrategy;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TCustomObjectHash<T> extends TObjectHash<T> {

    static final long serialVersionUID = 8766048185963756400L;
    protected HashingStrategy<? super T> strategy;

    public TCustomObjectHash() {}

    public TCustomObjectHash(HashingStrategy<? super T> hashingstrategy) {
        this.strategy = hashingstrategy;
    }

    public TCustomObjectHash(HashingStrategy<? super T> hashingstrategy, int i) {
        super(i);
        this.strategy = hashingstrategy;
    }

    public TCustomObjectHash(HashingStrategy<? super T> hashingstrategy, int i, float f) {
        super(i, f);
        this.strategy = hashingstrategy;
    }

    @Override
    protected int hash(Object object) {
        return this.strategy.computeHashCode(object);
    }

    @Override
    protected boolean equals(Object object, Object object1) {
        return object1 != TCustomObjectHash.REMOVED && this.strategy.equals(object, object1);
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeObject(this.strategy);
    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.strategy = (HashingStrategy) objectinput.readObject();
    }
}
