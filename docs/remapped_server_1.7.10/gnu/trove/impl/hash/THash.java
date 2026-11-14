package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.PrimeFinder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class THash implements Externalizable {

    static final long serialVersionUID = -1792948471915530295L;
    protected static final float DEFAULT_LOAD_FACTOR = 0.5F;
    protected static final int DEFAULT_CAPACITY = 10;
    protected transient int _size;
    protected transient int _free;
    protected float _loadFactor;
    protected int _maxSize;
    protected int _autoCompactRemovesRemaining;
    protected float _autoCompactionFactor;
    protected transient boolean _autoCompactTemporaryDisable;

    public THash() {
        this(10, 0.5F);
    }

    public THash(int i) {
        this(i, 0.5F);
    }

    public THash(int i, float f) {
        this._autoCompactTemporaryDisable = false;
        this._loadFactor = f;
        this._autoCompactionFactor = f;
        this.setUp(HashFunctions.fastCeil((float) i / f));
    }

    public boolean isEmpty() {
        return 0 == this._size;
    }

    public int size() {
        return this._size;
    }

    public abstract int capacity();

    public void ensureCapacity(int i) {
        if (i > this._maxSize - this.size()) {
            this.rehash(PrimeFinder.nextPrime(Math.max(this.size() + 1, HashFunctions.fastCeil((float) (i + this.size()) / this._loadFactor) + 1)));
            this.computeMaxSize(this.capacity());
        }

    }

    public void compact() {
        this.rehash(PrimeFinder.nextPrime(Math.max(this._size + 1, HashFunctions.fastCeil((float) this.size() / this._loadFactor) + 1)));
        this.computeMaxSize(this.capacity());
        if (this._autoCompactionFactor != 0.0F) {
            this.computeNextAutoCompactionAmount(this.size());
        }

    }

    public void setAutoCompactionFactor(float f) {
        if (f < 0.0F) {
            throw new IllegalArgumentException("Factor must be >= 0: " + f);
        } else {
            this._autoCompactionFactor = f;
        }
    }

    public float getAutoCompactionFactor() {
        return this._autoCompactionFactor;
    }

    public final void trimToSize() {
        this.compact();
    }

    protected void removeAt(int i) {
        --this._size;
        if (this._autoCompactionFactor != 0.0F) {
            --this._autoCompactRemovesRemaining;
            if (!this._autoCompactTemporaryDisable && this._autoCompactRemovesRemaining <= 0) {
                this.compact();
            }
        }

    }

    public void clear() {
        this._size = 0;
        this._free = this.capacity();
    }

    protected int setUp(int i) {
        int j = PrimeFinder.nextPrime(i);

        this.computeMaxSize(j);
        this.computeNextAutoCompactionAmount(i);
        return j;
    }

    protected abstract void rehash(int i);

    public void tempDisableAutoCompaction() {
        this._autoCompactTemporaryDisable = true;
    }

    public void reenableAutoCompaction(boolean flag) {
        this._autoCompactTemporaryDisable = false;
        if (flag && this._autoCompactRemovesRemaining <= 0 && this._autoCompactionFactor != 0.0F) {
            this.compact();
        }

    }

    protected void computeMaxSize(int i) {
        this._maxSize = Math.min(i - 1, (int) ((float) i * this._loadFactor));
        this._free = i - this._size;
    }

    protected void computeNextAutoCompactionAmount(int i) {
        if (this._autoCompactionFactor != 0.0F) {
            this._autoCompactRemovesRemaining = (int) ((float) i * this._autoCompactionFactor + 0.5F);
        }

    }

    protected final void postInsertHook(boolean flag) {
        if (flag) {
            --this._free;
        }

        if (++this._size > this._maxSize || this._free == 0) {
            int i = this._size > this._maxSize ? PrimeFinder.nextPrime(this.capacity() << 1) : this.capacity();

            this.rehash(i);
            this.computeMaxSize(this.capacity());
        }

    }

    protected int calculateGrownCapacity() {
        return this.capacity() << 1;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeFloat(this._loadFactor);
        objectoutput.writeFloat(this._autoCompactionFactor);
    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        float f = this._loadFactor;

        this._loadFactor = objectinput.readFloat();
        this._autoCompactionFactor = objectinput.readFloat();
        if (f != this._loadFactor) {
            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

    }
}
