package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlatformDependent {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent.class);
    private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
    private static final boolean IS_ANDROID = isAndroid0();
    private static final boolean IS_WINDOWS = isWindows0();
    private static final boolean IS_ROOT = isRoot0();
    private static final int JAVA_VERSION = javaVersion0();
    private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !isAndroid();
    private static final boolean HAS_UNSAFE = hasUnsafe0();
    private static final boolean CAN_USE_CHM_V8 = PlatformDependent.HAS_UNSAFE && PlatformDependent.JAVA_VERSION < 8;
    private static final boolean DIRECT_BUFFER_PREFERRED = PlatformDependent.HAS_UNSAFE && !SystemPropertyUtil.getBoolean("io.netty.noPreferDirect", false);
    private static final long MAX_DIRECT_MEMORY = maxDirectMemory0();
    private static final long ARRAY_BASE_OFFSET = arrayBaseOffset0();
    private static final boolean HAS_JAVASSIST = hasJavassist0();

    public static boolean isAndroid() {
        return PlatformDependent.IS_ANDROID;
    }

    public static boolean isWindows() {
        return PlatformDependent.IS_WINDOWS;
    }

    public static boolean isRoot() {
        return PlatformDependent.IS_ROOT;
    }

    public static int javaVersion() {
        return PlatformDependent.JAVA_VERSION;
    }

    public static boolean canEnableTcpNoDelayByDefault() {
        return PlatformDependent.CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    }

    public static boolean hasUnsafe() {
        return PlatformDependent.HAS_UNSAFE;
    }

    public static boolean directBufferPreferred() {
        return PlatformDependent.DIRECT_BUFFER_PREFERRED;
    }

    public static long maxDirectMemory() {
        return PlatformDependent.MAX_DIRECT_MEMORY;
    }

    public static boolean hasJavassist() {
        return PlatformDependent.HAS_JAVASSIST;
    }

    public static void throwException(Throwable throwable) {
        if (hasUnsafe()) {
            PlatformDependent0.throwException(throwable);
        } else {
            throwException0(throwable);
        }

    }

    private static <E extends Throwable> void throwException0(Throwable throwable) throws E {
        throw throwable;
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
        return (ConcurrentMap) (PlatformDependent.CAN_USE_CHM_V8 ? new ConcurrentHashMapV8<>() : new ConcurrentHashMap());
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int i) {
        return (ConcurrentMap) (PlatformDependent.CAN_USE_CHM_V8 ? new ConcurrentHashMapV8<>(i) : new ConcurrentHashMap(i));
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int i, float f) {
        return (ConcurrentMap) (PlatformDependent.CAN_USE_CHM_V8 ? new ConcurrentHashMapV8<>(i, f) : new ConcurrentHashMap(i, f));
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int i, float f, int j) {
        return (ConcurrentMap) (PlatformDependent.CAN_USE_CHM_V8 ? new ConcurrentHashMapV8<>(i, f, j) : new ConcurrentHashMap(i, f, j));
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(Map<? extends K, ? extends V> map) {
        return (ConcurrentMap) (PlatformDependent.CAN_USE_CHM_V8 ? new ConcurrentHashMapV8<>(map) : new ConcurrentHashMap(map));
    }

    public static void freeDirectBuffer(ByteBuffer bytebuffer) {
        if (bytebuffer.isDirect()) {
            if (hasUnsafe()) {
                PlatformDependent0.freeDirectBufferUnsafe(bytebuffer);
            } else {
                PlatformDependent0.freeDirectBuffer(bytebuffer);
            }
        }

    }

    public static long directBufferAddress(ByteBuffer bytebuffer) {
        return PlatformDependent0.directBufferAddress(bytebuffer);
    }

    public static Object getObject(Object object, long i) {
        return PlatformDependent0.getObject(object, i);
    }

    public static int getInt(Object object, long i) {
        return PlatformDependent0.getInt(object, i);
    }

    public static long objectFieldOffset(Field field) {
        return PlatformDependent0.objectFieldOffset(field);
    }

    public static byte getByte(long i) {
        return PlatformDependent0.getByte(i);
    }

    public static short getShort(long i) {
        return PlatformDependent0.getShort(i);
    }

    public static int getInt(long i) {
        return PlatformDependent0.getInt(i);
    }

    public static long getLong(long i) {
        return PlatformDependent0.getLong(i);
    }

    public static void putByte(long i, byte b0) {
        PlatformDependent0.putByte(i, b0);
    }

    public static void putShort(long i, short short0) {
        PlatformDependent0.putShort(i, short0);
    }

    public static void putInt(long i, int j) {
        PlatformDependent0.putInt(i, j);
    }

    public static void putLong(long i, long j) {
        PlatformDependent0.putLong(i, j);
    }

    public static void copyMemory(long i, long j, long k) {
        PlatformDependent0.copyMemory(i, j, k);
    }

    public static void copyMemory(byte[] abyte, int i, long j, long k) {
        PlatformDependent0.copyMemory(abyte, PlatformDependent.ARRAY_BASE_OFFSET + (long) i, (Object) null, j, k);
    }

    public static void copyMemory(long i, byte[] abyte, int j, long k) {
        PlatformDependent0.copyMemory((Object) null, i, abyte, PlatformDependent.ARRAY_BASE_OFFSET + (long) j, k);
    }

    private static boolean isAndroid0() {
        boolean flag;

        try {
            Class.forName("android.app.Application", false, ClassLoader.getSystemClassLoader());
            flag = true;
        } catch (Exception exception) {
            flag = false;
        }

        if (flag) {
            PlatformDependent.logger.debug("Platform: Android");
        }

        return flag;
    }

    private static boolean isWindows0() {
        boolean flag = SystemPropertyUtil.get("os.name", "").toLowerCase(Locale.US).contains("win");

        if (flag) {
            PlatformDependent.logger.debug("Platform: Windows");
        }

        return flag;
    }

    private static boolean isRoot0() {
        if (isWindows()) {
            return false;
        } else {
            String[] astring = new String[]{"/usr/bin/id", "/bin/id", "id"};
            Pattern pattern = Pattern.compile("^(?:0|[1-9][0-9]*)$");
            String[] astring1 = astring;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String s = astring1[j];
                Process process = null;
                BufferedReader bufferedreader = null;
                String s1 = null;

                try {
                    process = Runtime.getRuntime().exec(new String[]{s, "-u"});
                    bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream(), CharsetUtil.US_ASCII));
                    s1 = bufferedreader.readLine();
                    bufferedreader.close();

                    while (true) {
                        try {
                            int k = process.waitFor();

                            if (k != 0) {
                                s1 = null;
                            }
                            break;
                        } catch (InterruptedException interruptedexception) {
                            ;
                        }
                    }
                } catch (Exception exception) {
                    s1 = null;
                } finally {
                    if (bufferedreader != null) {
                        try {
                            bufferedreader.close();
                        } catch (IOException ioexception) {
                            ;
                        }
                    }

                    if (process != null) {
                        try {
                            process.destroy();
                        } catch (Exception exception1) {
                            ;
                        }
                    }

                }

                if (s1 != null && pattern.matcher(s1).matches()) {
                    PlatformDependent.logger.debug("UID: {}", (Object) s1);
                    return "0".equals(s1);
                }
            }

            PlatformDependent.logger.debug("Could not determine the current UID using /usr/bin/id; attempting to bind at privileged ports.");
            Pattern pattern1 = Pattern.compile(".*(?:denied|not.*permitted).*");

            i = 1023;

            while (true) {
                if (i > 0) {
                    ServerSocket serversocket = null;

                    try {
                        serversocket = new ServerSocket();
                        serversocket.setReuseAddress(true);
                        serversocket.bind(new InetSocketAddress(i));
                        if (PlatformDependent.logger.isDebugEnabled()) {
                            PlatformDependent.logger.debug("UID: 0 (succeded to bind at port {})", (Object) i);
                        }

                        boolean flag = true;

                        return flag;
                    } catch (Exception exception2) {
                        String s2 = exception2.getMessage();

                        if (s2 == null) {
                            s2 = "";
                        }

                        s2 = s2.toLowerCase();
                        if (pattern1.matcher(s2).matches()) {
                            break label90;
                        }
                    } finally {
                        if (serversocket != null) {
                            try {
                                serversocket.close();
                            } catch (Exception exception3) {
                                ;
                            }
                        }

                    }

                    --i;
                    continue;
                }

                PlatformDependent.logger.debug("UID: non-root (failed to bind at any privileged ports)");
                return false;
            }
        }
    }

    private static int javaVersion0() {
        byte b0;

        if (isAndroid()) {
            b0 = 6;
        } else {
            try {
                Class.forName("java.time.Clock", false, Object.class.getClassLoader());
                b0 = 8;
            } catch (Exception exception) {
                try {
                    Class.forName("java.util.concurrent.LinkedTransferQueue", false, BlockingQueue.class.getClassLoader());
                    b0 = 7;
                } catch (Exception exception1) {
                    b0 = 6;
                }
            }
        }

        if (PlatformDependent.logger.isDebugEnabled()) {
            PlatformDependent.logger.debug("Java version: {}", (Object) Integer.valueOf(b0));
        }

        return b0;
    }

    private static boolean hasUnsafe0() {
        boolean flag = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);

        PlatformDependent.logger.debug("-Dio.netty.noUnsafe: {}", (Object) flag);
        if (isAndroid()) {
            PlatformDependent.logger.debug("sun.misc.Unsafe: unavailable (Android)");
            return false;
        } else if (flag) {
            PlatformDependent.logger.debug("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
            return false;
        } else {
            boolean flag1;

            if (SystemPropertyUtil.contains("io.netty.tryUnsafe")) {
                flag1 = SystemPropertyUtil.getBoolean("io.netty.tryUnsafe", true);
            } else {
                flag1 = SystemPropertyUtil.getBoolean("org.jboss.netty.tryUnsafe", true);
            }

            if (!flag1) {
                PlatformDependent.logger.debug("sun.misc.Unsafe: unavailable (io.netty.tryUnsafe/org.jboss.netty.tryUnsafe)");
                return false;
            } else {
                try {
                    boolean flag2 = PlatformDependent0.hasUnsafe();

                    PlatformDependent.logger.debug("sun.misc.Unsafe: {}", (Object) (flag2 ? "available" : "unavailable"));
                    return flag2;
                } catch (Throwable throwable) {
                    return false;
                }
            }
        }
    }

    private static long arrayBaseOffset0() {
        return !hasUnsafe() ? -1L : PlatformDependent0.arrayBaseOffset();
    }

    private static long maxDirectMemory0() {
        long i = 0L;

        Class oclass;

        try {
            oclass = Class.forName("sun.misc.VM", true, ClassLoader.getSystemClassLoader());
            Method method = oclass.getDeclaredMethod("maxDirectMemory");

            i = ((Number) method.invoke((Object) null)).longValue();
        } catch (Throwable throwable) {
            ;
        }

        if (i > 0L) {
            return i;
        } else {
            try {
                oclass = Class.forName("java.lang.management.ManagementFactory", true, ClassLoader.getSystemClassLoader());
                Class oclass1 = Class.forName("java.lang.management.RuntimeMXBean", true, ClassLoader.getSystemClassLoader());
                Object object = oclass.getDeclaredMethod("getRuntimeMXBean").invoke((Object) null);
                List list = (List) oclass1.getDeclaredMethod("getInputArguments").invoke(object);

                label41:
                for (int j = list.size() - 1; j >= 0; --j) {
                    Matcher matcher = PlatformDependent.MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher((CharSequence) list.get(j));

                    if (matcher.matches()) {
                        i = Long.parseLong(matcher.group(1));
                        switch (matcher.group(2).charAt(0)) {
                            case 'G':
                            case 'g':
                                i *= 1073741824L;
                                break label41;
                            case 'K':
                            case 'k':
                                i *= 1024L;
                                break label41;
                            case 'M':
                            case 'm':
                                i *= 1048576L;
                            default:
                                break label41;
                        }
                    }
                }
            } catch (Throwable throwable1) {
                ;
            }

            if (i <= 0L) {
                i = Runtime.getRuntime().maxMemory();
                PlatformDependent.logger.debug("maxDirectMemory: {} bytes (maybe)", (Object) i);
            } else {
                PlatformDependent.logger.debug("maxDirectMemory: {} bytes", (Object) i);
            }

            return i;
        }
    }

    private static boolean hasJavassist0() {
        boolean flag = SystemPropertyUtil.getBoolean("io.netty.noJavassist", false);

        PlatformDependent.logger.debug("-Dio.netty.noJavassist: {}", (Object) flag);
        if (flag) {
            PlatformDependent.logger.debug("Javassist: unavailable (io.netty.noJavassist)");
            return false;
        } else {
            try {
                JavassistTypeParameterMatcherGenerator.generate(Object.class, PlatformDependent.class.getClassLoader());
                PlatformDependent.logger.debug("Javassist: available");
                return true;
            } catch (Throwable throwable) {
                PlatformDependent.logger.debug("Javassist: unavailable");
                PlatformDependent.logger.debug("You don't have Javassist in your class path or you don't have enough permission to load dynamically generated classes.  Please check the configuration for better performance.");
                return false;
            }
        }
    }

    private PlatformDependent() {}

    static {
        if (PlatformDependent.logger.isDebugEnabled()) {
            PlatformDependent.logger.debug("-Dio.netty.noPreferDirect: {}", (Object) (!PlatformDependent.DIRECT_BUFFER_PREFERRED));
        }

        if (!hasUnsafe()) {
            PlatformDependent.logger.info("Your platform does not provide complete low-level API for accessing direct buffers reliably. Unless explicitly requested, heap buffer will always be preferred to avoid potential system unstability.");
        }

    }
}
