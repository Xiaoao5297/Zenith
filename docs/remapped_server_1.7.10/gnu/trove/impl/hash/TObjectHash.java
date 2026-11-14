package gnu.trove.impl.hash;

import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class TObjectHash<T> extends THash {

    static final long serialVersionUID = -3461112548087185871L;
    public transient Object[] _set;
    public static final Object REMOVED = new Object();
    public static final Object FREE = new Object();
    protected boolean consumeFreeSlot;

    public TObjectHash() {}

    public TObjectHash(int i) {
        super(i);
    }

    public TObjectHash(int i, float f) {
        super(i, f);
    }

    @Override
    public int capacity() {
        return this._set.length;
    }

    @Override
    protected void removeAt(int i) {
        this._set[i] = TObjectHash.REMOVED;
        super.removeAt(i);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._set = new Object[j];
        Arrays.fill(this._set, TObjectHash.FREE);
        return j;
    }

    public boolean forEach(TObjectProcedure<? super T> tobjectprocedure) {
        Object[] aobject = this._set;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectHash.FREE || aobject[i] == TObjectHash.REMOVED || tobjectprocedure.execute(aobject[i]));

        return false;
    }

    public boolean contains(Object object) {
        return this.index(object) >= 0;
    }

    protected int index(Object object) {
        if (object == null) {
            return this.indexForNull();
        } else {
            int i = this.hash(object) & Integer.MAX_VALUE;
            int j = i % this._set.length;
            Object object1 = this._set[j];

            return object1 == TObjectHash.FREE ? -1 : (object1 != object && !this.equals(object, object1) ? this.indexRehashed(object, j, i, object1) : j);
        }
    }

    private int indexRehashed(Object object, int i, int j, Object object1) {
        Object[] aobject = this._set;
        int k = aobject.length;
        int l = 1 + j % (k - 2);
        int i1 = i;

        do {
            i -= l;
            if (i < 0) {
                i += k;
            }

            object1 = aobject[i];
            if (object1 == TObjectHash.FREE) {
                return -1;
            }

            if (object1 == object || this.equals(object, object1)) {
                return i;
            }
        } while (i != i1);

        return -1;
    }

    private int indexForNull() {
        int i = 0;
        Object[] aobject = this._set;
        int j = aobject.length;

        for (int k = 0; k < j; ++k) {
            Object object = aobject[k];

            if (object == null) {
                return i;
            }

            if (object == TObjectHash.FREE) {
                return -1;
            }

            ++i;
        }

        return -1;
    }

    /** @deprecated */
    @Deprecated
    protected int insertionIndex(T t0) {
        return this.insertKey(t0);
    }

    protected int insertKey(T t0) {
        this.consumeFreeSlot = false;
        if (t0 == null) {
            return this.insertKeyForNull();
        } else {
            int i = this.hash(t0) & Integer.MAX_VALUE;
            int j = i % this._set.length;
            Object object = this._set[j];

            if (object == TObjectHash.FREE) {
                this.consumeFreeSlot = true;
                this._set[j] = t0;
                return j;
            } else {
                return object != t0 && !this.equals(t0, object) ? this.insertKeyRehash(t0, j, i, object) : -j - 1;
            }
        }
    }

    private int insertKeyRehash(T t0, int i, int j, Object object) {
        Object[] aobject = this._set;
        int k = aobject.length;
        int l = 1 + j % (k - 2);
        int i1 = i;
        int j1 = -1;

        do {
            if (object == TObjectHash.REMOVED && j1 == -1) {
                j1 = i;
            }

            i -= l;
            if (i < 0) {
                i += k;
            }

            object = aobject[i];
            if (object == TObjectHash.FREE) {
                if (j1 != -1) {
                    this._set[j1] = t0;
                    return j1;
                }

                this.consumeFreeSlot = true;
                this._set[i] = t0;
                return i;
            }

            if (object == t0 || this.equals(t0, object)) {
                return -i - 1;
            }
        } while (i != i1);

        if (j1 != -1) {
            this._set[j1] = t0;
            return j1;
        } else {
            throw new IllegalStateException("No free or removed slots available. Key set full?!!");
        }
    }

    private int insertKeyForNull() {
        int i = 0;
        int j = -1;
        Object[] aobject = this._set;
        int k = aobject.length;

        for (int l = 0; l < k; ++l) {
            Object object = aobject[l];

            if (object == TObjectHash.REMOVED && j == -1) {
                j = i;
            }

            if (object == TObjectHash.FREE) {
                if (j != -1) {
                    this._set[j] = null;
                    return j;
                }

                this.consumeFreeSlot = true;
                this._set[i] = null;
                return i;
            }

            if (object == null) {
                return -i - 1;
            }

            ++i;
        }

        if (j != -1) {
            this._set[j] = null;
            return j;
        } else {
            throw new IllegalStateException("Could not find insertion index for null key. Key set full!?!!");
        }
    }

    protected final void throwObjectContractViolation(Object object, Object object1) throws IllegalArgumentException {
        throw this.buildObjectContractViolation(object, object1, "");
    }

    protected final void throwObjectContractViolation(Object object, Object object1, int i, int j, Object[] aobject) throws IllegalArgumentException {
        String s = this.dumpExtraInfo(object, object1, this.size(), j, aobject);

        throw this.buildObjectContractViolation(object, object1, s);
    }

    protected final IllegalArgumentException buildObjectContractViolation(Object object, Object object1, String s) {
        return new IllegalArgumentException("Equal objects must have equal hashcodes. During rehashing, Trove discovered that the following two objects claim to be equal (as in java.lang.Object.equals()) but their hashCodes (or those calculated by your TObjectHashingStrategy) are not equal.This violates the general contract of java.lang.Object.hashCode().  See bullet point two in that method's documentation. object #1 =" + objectInfo(object) + "; object #2 =" + objectInfo(object1) + "\n" + s);
    }

    protected boolean equals(Object object, Object object1) {
        return object1 != null && object1 != TObjectHash.REMOVED ? object.equals(object1) : false;
    }

    protected int hash(Object object) {
        return object.hashCode();
    }

    protected static String reportPotentialConcurrentMod(int i, int j) {
        return i != j ? "[Warning] apparent concurrent modification of the key set. Size before and after rehash() do not match " + j + " vs " + i : "";
    }

    protected String dumpExtraInfo(Object object, Object object1, int i, int j, Object[] aobject) {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.dumpKeyTypes(object, object1));
        stringbuilder.append(reportPotentialConcurrentMod(i, j));
        stringbuilder.append(detectKeyLoss(aobject, j));
        if (object == object1) {
            stringbuilder.append("Inserting same object twice, rehashing bug. Object= ").append(object1);
        }

        return stringbuilder.toString();
    }

    private static String detectKeyLoss(Object[] aobject, int i) {
        StringBuilder stringbuilder = new StringBuilder();
        Set set = makeKeySet(aobject);

        if (set.size() != i) {
            stringbuilder.append("\nhashCode() and/or equals() have inconsistent implementation");
            stringbuilder.append("\nKey set lost entries, now got ").append(set.size()).append(" instead of ").append(i);
            stringbuilder.append(". This can manifest itself as an apparent duplicate key.");
        }

        return stringbuilder.toString();
    }

    private static Set<Object> makeKeySet(Object[] aobject) {
        HashSet hashset = new HashSet();
        Object[] aobject1 = aobject;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object object = aobject1[j];

            if (object != TObjectHash.FREE && object != TObjectHash.REMOVED) {
                hashset.add(object);
            }
        }

        return hashset;
    }

    private static String equalsSymmetryInfo(Object object, Object object1) {
        StringBuilder stringbuilder = new StringBuilder();

        if (object == object1) {
            return "a == b";
        } else {
            if (object.getClass() != object1.getClass()) {
                stringbuilder.append("Class of objects differ a=").append(object.getClass()).append(" vs b=").append(object1.getClass());
                boolean flag = object.equals(object1);
                boolean flag1 = object1.equals(object);

                if (flag != flag1) {
                    stringbuilder.append("\nequals() of a or b object are asymmetric");
                    stringbuilder.append("\na.equals(b) =").append(flag);
                    stringbuilder.append("\nb.equals(a) =").append(flag1);
                }
            }

            return stringbuilder.toString();
        }
    }

    protected static String objectInfo(Object object) {
        return (object == null ? "class null" : object.getClass()) + " id= " + System.identityHashCode(object) + " hashCode= " + (object == null ? 0 : object.hashCode()) + " toString= " + object;
    }

    private String dumpKeyTypes(Object object, Object object1) {
        StringBuilder stringbuilder = new StringBuilder();
        HashSet hashset = new HashSet();
        Object[] aobject = this._set;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object object2 = aobject[j];

            if (object2 != TObjectHash.FREE && object2 != TObjectHash.REMOVED) {
                if (object2 != null) {
                    hashset.add(object2.getClass());
                } else {
                    hashset.add((Object) null);
                }
            }
        }

        if (hashset.size() > 1) {
            stringbuilder.append("\nMore than one type used for keys. Watch out for asymmetric equals(). Read about the 'Liskov substitution principle' and the implications for equals() in java.");
            stringbuilder.append("\nKey types: ").append(hashset);
            stringbuilder.append(equalsSymmetryInfo(object, object1));
        }

        return stringbuilder.toString();
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
    }
}
