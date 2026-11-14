package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible
public final class Bytes {

    private Bytes() {}

    public static int hashCode(byte b0) {
        return b0;
    }

    public static boolean contains(byte[] abyte, byte b0) {
        byte[] abyte1 = abyte;
        int i = abyte.length;

        for (int j = 0; j < i; ++j) {
            byte b1 = abyte1[j];

            if (b1 == b0) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(byte[] abyte, byte b0) {
        return indexOf(abyte, b0, 0, abyte.length);
    }

    private static int indexOf(byte[] abyte, byte b0, int i, int j) {
        for (int k = i; k < j; ++k) {
            if (abyte[k] == b0) {
                return k;
            }
        }

        return -1;
    }

    public static int indexOf(byte[] abyte, byte[] abyte1) {
        Preconditions.checkNotNull(abyte, "array");
        Preconditions.checkNotNull(abyte1, "target");
        if (abyte1.length == 0) {
            return 0;
        } else {
            int i = 0;

            label27:
            while (i < abyte.length - abyte1.length + 1) {
                for (int j = 0; j < abyte1.length; ++j) {
                    if (abyte[i + j] != abyte1[j]) {
                        ++i;
                        continue label27;
                    }
                }

                return i;
            }

            return -1;
        }
    }

    public static int lastIndexOf(byte[] abyte, byte b0) {
        return lastIndexOf(abyte, b0, 0, abyte.length);
    }

    private static int lastIndexOf(byte[] abyte, byte b0, int i, int j) {
        for (int k = j - 1; k >= i; --k) {
            if (abyte[k] == b0) {
                return k;
            }
        }

        return -1;
    }

    public static byte[] concat(byte[]... abyte) {
        int i = 0;
        byte[][] abyte1 = abyte;
        int j = abyte.length;

        for (int k = 0; k < j; ++k) {
            byte[] abyte2 = abyte1[k];

            i += abyte2.length;
        }

        byte[] abyte3 = new byte[i];

        j = 0;
        byte[][] abyte4 = abyte;
        int l = abyte.length;

        for (int i1 = 0; i1 < l; ++i1) {
            byte[] abyte5 = abyte4[i1];

            System.arraycopy(abyte5, 0, abyte3, j, abyte5.length);
            j += abyte5.length;
        }

        return abyte3;
    }

    public static byte[] ensureCapacity(byte[] abyte, int i, int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        return abyte.length < i ? copyOf(abyte, i + j) : abyte;
    }

    private static byte[] copyOf(byte[] abyte, int i) {
        byte[] abyte1 = new byte[i];

        System.arraycopy(abyte, 0, abyte1, 0, Math.min(abyte.length, i));
        return abyte1;
    }

    public static byte[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof Bytes.ByteArrayAsList) {
            return ((Bytes.ByteArrayAsList) collection).toByteArray();
        } else {
            Object[] aobject = collection.toArray();
            int i = aobject.length;
            byte[] abyte = new byte[i];

            for (int j = 0; j < i; ++j) {
                abyte[j] = ((Number) Preconditions.checkNotNull(aobject[j])).byteValue();
            }

            return abyte;
        }
    }

    public static List<Byte> asList(byte... abyte) {
        return (List) (abyte.length == 0 ? Collections.emptyList() : new Bytes.ByteArrayAsList(abyte));
    }

    @GwtCompatible
    private static class ByteArrayAsList extends AbstractList<Byte> implements RandomAccess, Serializable {

        final byte[] array;
        final int start;
        final int end;
        private static final long serialVersionUID = 0L;

        ByteArrayAsList(byte[] abyte) {
            this(abyte, 0, abyte.length);
        }

        ByteArrayAsList(byte[] abyte, int i, int j) {
            this.array = abyte;
            this.start = i;
            this.end = j;
        }

        public int size() {
            return this.end - this.start;
        }

        public boolean isEmpty() {
            return false;
        }

        public Byte get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.array[this.start + i];
        }

        public boolean contains(Object object) {
            return object instanceof Byte && Bytes.indexOf(this.array, (Byte) object, this.start, this.end) != -1;
        }

        public int indexOf(Object object) {
            if (object instanceof Byte) {
                int i = Bytes.indexOf(this.array, (Byte) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public int lastIndexOf(Object object) {
            if (object instanceof Byte) {
                int i = Bytes.lastIndexOf(this.array, (Byte) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public Byte set(int i, Byte obyte) {
            Preconditions.checkElementIndex(i, this.size());
            byte b0 = this.array[this.start + i];

            this.array[this.start + i] = (Byte) Preconditions.checkNotNull(obyte);
            return b0;
        }

        public List<Byte> subList(int i, int j) {
            int k = this.size();

            Preconditions.checkPositionIndexes(i, j, k);
            return (List) (i == j ? Collections.emptyList() : new Bytes.ByteArrayAsList(this.array, this.start + i, this.start + j));
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (object instanceof Bytes.ByteArrayAsList) {
                Bytes.ByteArrayAsList bytes_bytearrayaslist = (Bytes.ByteArrayAsList) object;
                int i = this.size();

                if (bytes_bytearrayaslist.size() != i) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (this.array[this.start + j] != bytes_bytearrayaslist.array[bytes_bytearrayaslist.start + j]) {
                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return super.equals(object);
            }
        }

        public int hashCode() {
            int i = 1;

            for (int j = this.start; j < this.end; ++j) {
                i = 31 * i + Bytes.hashCode(this.array[j]);
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder(this.size() * 5);

            stringbuilder.append('[').append(this.array[this.start]);

            for (int i = this.start + 1; i < this.end; ++i) {
                stringbuilder.append(", ").append(this.array[i]);
            }

            return stringbuilder.append(']').toString();
        }

        byte[] toByteArray() {
            int i = this.size();
            byte[] abyte = new byte[i];

            System.arraycopy(this.array, this.start, abyte, 0, i);
            return abyte;
        }
    }
}
