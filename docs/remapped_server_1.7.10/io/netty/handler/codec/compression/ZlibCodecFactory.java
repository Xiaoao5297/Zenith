package io.netty.handler.codec.compression;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ZlibCodecFactory {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
    private static final boolean noJdkZlibDecoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", true);

    public static ZlibEncoder newZlibEncoder(int i) {
        return (ZlibEncoder) (PlatformDependent.javaVersion() < 7 ? new JZlibEncoder(i) : new JdkZlibEncoder(i));
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper zlibwrapper) {
        return (ZlibEncoder) (PlatformDependent.javaVersion() < 7 ? new JZlibEncoder(zlibwrapper) : new JdkZlibEncoder(zlibwrapper));
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper zlibwrapper, int i) {
        return (ZlibEncoder) (PlatformDependent.javaVersion() < 7 ? new JZlibEncoder(zlibwrapper, i) : new JdkZlibEncoder(zlibwrapper, i));
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper zlibwrapper, int i, int j, int k) {
        return (ZlibEncoder) (PlatformDependent.javaVersion() < 7 ? new JZlibEncoder(zlibwrapper, i, j, k) : new JdkZlibEncoder(zlibwrapper, i));
    }

    public static ZlibEncoder newZlibEncoder(byte[] abyte) {
        return (ZlibEncoder) (PlatformDependent.javaVersion() < 7 ? new JZlibEncoder(abyte) : new JdkZlibEncoder(abyte));
    }

    public static ZlibEncoder newZlibEncoder(int i, byte[] abyte) {
        return (ZlibEncoder) (PlatformDependent.javaVersion() < 7 ? new JZlibEncoder(i, abyte) : new JdkZlibEncoder(i, abyte));
    }

    public static ZlibEncoder newZlibEncoder(int i, int j, int k, byte[] abyte) {
        return (ZlibEncoder) (PlatformDependent.javaVersion() < 7 ? new JZlibEncoder(i, j, k, abyte) : new JdkZlibEncoder(i, abyte));
    }

    public static ZlibDecoder newZlibDecoder() {
        return (ZlibDecoder) (PlatformDependent.javaVersion() >= 7 && !ZlibCodecFactory.noJdkZlibDecoder ? new JdkZlibDecoder() : new JZlibDecoder());
    }

    public static ZlibDecoder newZlibDecoder(ZlibWrapper zlibwrapper) {
        switch (zlibwrapper) {
            case ZLIB_OR_NONE:
                return new JZlibDecoder(zlibwrapper);
            default:
                return (ZlibDecoder) (PlatformDependent.javaVersion() >= 7 && !ZlibCodecFactory.noJdkZlibDecoder ? new JdkZlibDecoder(zlibwrapper) : new JZlibDecoder(zlibwrapper));
        }
    }

    public static ZlibDecoder newZlibDecoder(byte[] abyte) {
        return (ZlibDecoder) (PlatformDependent.javaVersion() >= 7 && !ZlibCodecFactory.noJdkZlibDecoder ? new JdkZlibDecoder(abyte) : new JZlibDecoder(abyte));
    }

    private ZlibCodecFactory() {}

    static {
        ZlibCodecFactory.logger.debug("-Dio.netty.noJdkZlibDecoder: {}", (Object) ZlibCodecFactory.noJdkZlibDecoder);
    }
}
