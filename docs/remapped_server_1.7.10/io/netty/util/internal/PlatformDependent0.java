package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import sun.misc.Cleaner;
import sun.misc.Unsafe;

final class PlatformDependent0 {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
    private static final Unsafe UNSAFE;
    private static final boolean BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    private static final long CLEANER_FIELD_OFFSET;
    private static final long ADDRESS_FIELD_OFFSET;
    private static final Field CLEANER_FIELD;
    private static final boolean UNALIGNED;

    static boolean hasUnsafe() {
        return PlatformDependent0.UNSAFE != null;
    }

    static void throwException(Throwable throwable) {
        PlatformDependent0.UNSAFE.throwException(throwable);
    }

    static void freeDirectBufferUnsafe(ByteBuffer bytebuffer) {
        try {
            Cleaner cleaner = (Cleaner) getObject(bytebuffer, PlatformDependent0.CLEANER_FIELD_OFFSET);

            if (cleaner == null) {
                throw new IllegalArgumentException("attempted to deallocate the buffer which was allocated via JNIEnv->NewDirectByteBuffer()");
            }

            cleaner.clean();
        } catch (Throwable throwable) {
            ;
        }

    }

    static void freeDirectBuffer(ByteBuffer bytebuffer) {
        if (PlatformDependent0.CLEANER_FIELD != null) {
            try {
                Cleaner cleaner = (Cleaner) PlatformDependent0.CLEANER_FIELD.get(bytebuffer);

                if (cleaner == null) {
                    throw new IllegalArgumentException("attempted to deallocate the buffer which was allocated via JNIEnv->NewDirectByteBuffer()");
                }

                cleaner.clean();
            } catch (Throwable throwable) {
                ;
            }

        }
    }

    static long directBufferAddress(ByteBuffer bytebuffer) {
        return getLong(bytebuffer, PlatformDependent0.ADDRESS_FIELD_OFFSET);
    }

    static long arrayBaseOffset() {
        return (long) PlatformDependent0.UNSAFE.arrayBaseOffset(byte[].class);
    }

    static Object getObject(Object object, long i) {
        return PlatformDependent0.UNSAFE.getObject(object, i);
    }

    static int getInt(Object object, long i) {
        return PlatformDependent0.UNSAFE.getInt(object, i);
    }

    private static long getLong(Object object, long i) {
        return PlatformDependent0.UNSAFE.getLong(object, i);
    }

    static long objectFieldOffset(Field field) {
        return PlatformDependent0.UNSAFE.objectFieldOffset(field);
    }

    static byte getByte(long i) {
        return PlatformDependent0.UNSAFE.getByte(i);
    }

    static short getShort(long i) {
        return PlatformDependent0.UNALIGNED ? PlatformDependent0.UNSAFE.getShort(i) : (PlatformDependent0.BIG_ENDIAN ? (short) (getByte(i) << 8 | getByte(i + 1L) & 255) : (short) (getByte(i + 1L) << 8 | getByte(i) & 255));
    }

    static int getInt(long i) {
        return PlatformDependent0.UNALIGNED ? PlatformDependent0.UNSAFE.getInt(i) : (PlatformDependent0.BIG_ENDIAN ? getByte(i) << 24 | (getByte(i + 1L) & 255) << 16 | (getByte(i + 2L) & 255) << 8 | getByte(i + 3L) & 255 : getByte(i + 3L) << 24 | (getByte(i + 2L) & 255) << 16 | (getByte(i + 1L) & 255) << 8 | getByte(i) & 255);
    }

    static long getLong(long i) {
        return PlatformDependent0.UNALIGNED ? PlatformDependent0.UNSAFE.getLong(i) : (PlatformDependent0.BIG_ENDIAN ? (long) getByte(i) << 56 | ((long) getByte(i + 1L) & 255L) << 48 | ((long) getByte(i + 2L) & 255L) << 40 | ((long) getByte(i + 3L) & 255L) << 32 | ((long) getByte(i + 4L) & 255L) << 24 | ((long) getByte(i + 5L) & 255L) << 16 | ((long) getByte(i + 6L) & 255L) << 8 | (long) getByte(i + 7L) & 255L : (long) getByte(i + 7L) << 56 | ((long) getByte(i + 6L) & 255L) << 48 | ((long) getByte(i + 5L) & 255L) << 40 | ((long) getByte(i + 4L) & 255L) << 32 | ((long) getByte(i + 3L) & 255L) << 24 | ((long) getByte(i + 2L) & 255L) << 16 | ((long) getByte(i + 1L) & 255L) << 8 | (long) getByte(i) & 255L);
    }

    static void putByte(long i, byte b0) {
        PlatformDependent0.UNSAFE.putByte(i, b0);
    }

    static void putShort(long i, short short0) {
        if (PlatformDependent0.UNALIGNED) {
            PlatformDependent0.UNSAFE.putShort(i, short0);
        } else if (PlatformDependent0.BIG_ENDIAN) {
            putByte(i, (byte) (short0 >>> 8));
            putByte(i + 1L, (byte) short0);
        } else {
            putByte(i + 1L, (byte) (short0 >>> 8));
            putByte(i, (byte) short0);
        }

    }

    static void putInt(long i, int j) {
        if (PlatformDependent0.UNALIGNED) {
            PlatformDependent0.UNSAFE.putInt(i, j);
        } else if (PlatformDependent0.BIG_ENDIAN) {
            putByte(i, (byte) (j >>> 24));
            putByte(i + 1L, (byte) (j >>> 16));
            putByte(i + 2L, (byte) (j >>> 8));
            putByte(i + 3L, (byte) j);
        } else {
            putByte(i + 3L, (byte) (j >>> 24));
            putByte(i + 2L, (byte) (j >>> 16));
            putByte(i + 1L, (byte) (j >>> 8));
            putByte(i, (byte) j);
        }

    }

    static void putLong(long i, long j) {
        if (PlatformDependent0.UNALIGNED) {
            PlatformDependent0.UNSAFE.putLong(i, j);
        } else if (PlatformDependent0.BIG_ENDIAN) {
            putByte(i, (byte) ((int) (j >>> 56)));
            putByte(i + 1L, (byte) ((int) (j >>> 48)));
            putByte(i + 2L, (byte) ((int) (j >>> 40)));
            putByte(i + 3L, (byte) ((int) (j >>> 32)));
            putByte(i + 4L, (byte) ((int) (j >>> 24)));
            putByte(i + 5L, (byte) ((int) (j >>> 16)));
            putByte(i + 6L, (byte) ((int) (j >>> 8)));
            putByte(i + 7L, (byte) ((int) j));
        } else {
            putByte(i + 7L, (byte) ((int) (j >>> 56)));
            putByte(i + 6L, (byte) ((int) (j >>> 48)));
            putByte(i + 5L, (byte) ((int) (j >>> 40)));
            putByte(i + 4L, (byte) ((int) (j >>> 32)));
            putByte(i + 3L, (byte) ((int) (j >>> 24)));
            putByte(i + 2L, (byte) ((int) (j >>> 16)));
            putByte(i + 1L, (byte) ((int) (j >>> 8)));
            putByte(i, (byte) ((int) j));
        }

    }

    static void copyMemory(long i, long j, long k) {
        PlatformDependent0.UNSAFE.copyMemory(i, j, k);
    }

    static void copyMemory(Object object, long i, Object object1, long j, long k) {
        PlatformDependent0.UNSAFE.copyMemory(object, i, object1, j, k);
    }

    private PlatformDependent0() {}

    static {
        ByteBuffer bytebuffer = ByteBuffer.allocateDirect(1);

        Field field;

        try {
            field = bytebuffer.getClass().getDeclaredField("cleaner");
            field.setAccessible(true);
            Cleaner cleaner = (Cleaner) field.get(bytebuffer);

            cleaner.clean();
        } catch (Throwable throwable) {
            field = null;
        }

        CLEANER_FIELD = field;
        PlatformDependent0.logger.debug("java.nio.ByteBuffer.cleaner: {}", (Object) (field != null ? "available" : "unavailable"));

        Field field1;

        try {
            field1 = Buffer.class.getDeclaredField("address");
            field1.setAccessible(true);
            if (field1.getLong(ByteBuffer.allocate(1)) != 0L) {
                field1 = null;
            } else {
                bytebuffer = ByteBuffer.allocateDirect(1);
                if (field1.getLong(bytebuffer) == 0L) {
                    field1 = null;
                }

                Cleaner cleaner1 = (Cleaner) field.get(bytebuffer);

                cleaner1.clean();
            }
        } catch (Throwable throwable1) {
            field1 = null;
        }

        PlatformDependent0.logger.debug("java.nio.Buffer.address: {}", (Object) (field1 != null ? "available" : "unavailable"));
        Unsafe unsafe;

        if (field1 != null && field != null) {
            try {
                Field field2 = Unsafe.class.getDeclaredField("theUnsafe");

                field2.setAccessible(true);
                unsafe = (Unsafe) field2.get((Object) null);
                PlatformDependent0.logger.debug("sun.misc.Unsafe.theUnsafe: {}", (Object) (unsafe != null ? "available" : "unavailable"));

                try {
                    unsafe.getClass().getDeclaredMethod("copyMemory", Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE);
                    PlatformDependent0.logger.debug("sun.misc.Unsafe.copyMemory: available");
                } catch (NoSuchMethodError nosuchmethoderror) {
                    PlatformDependent0.logger.debug("sun.misc.Unsafe.copyMemory: unavailable");
                    throw nosuchmethoderror;
                } catch (NoSuchMethodException nosuchmethodexception) {
                    PlatformDependent0.logger.debug("sun.misc.Unsafe.copyMemory: unavailable");
                    throw nosuchmethodexception;
                }
            } catch (Throwable throwable2) {
                unsafe = null;
            }
        } else {
            unsafe = null;
        }

        UNSAFE = unsafe;
        if (unsafe == null) {
            CLEANER_FIELD_OFFSET = -1L;
            ADDRESS_FIELD_OFFSET = -1L;
            UNALIGNED = false;
        } else {
            ADDRESS_FIELD_OFFSET = objectFieldOffset(field1);
            CLEANER_FIELD_OFFSET = objectFieldOffset(field);

            boolean flag;

            try {
                Class oclass = Class.forName("java.nio.Bits", false, ClassLoader.getSystemClassLoader());
                Method method = oclass.getDeclaredMethod("unaligned");

                method.setAccessible(true);
                flag = Boolean.TRUE.equals(method.invoke((Object) null));
            } catch (Throwable throwable3) {
                String s = SystemPropertyUtil.get("os.arch", "");

                flag = s.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
            }

            UNALIGNED = flag;
            PlatformDependent0.logger.debug("java.nio.Bits.unaligned: {}", (Object) PlatformDependent0.UNALIGNED);
        }

    }
}
