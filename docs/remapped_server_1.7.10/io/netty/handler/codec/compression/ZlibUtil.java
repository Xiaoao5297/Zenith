package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.JZlib.WrapperType;

final class ZlibUtil {

    static void fail(Inflater inflater, String s, int i) {
        throw inflaterException(inflater, s, i);
    }

    static void fail(Deflater deflater, String s, int i) {
        throw deflaterException(deflater, s, i);
    }

    static CompressionException inflaterException(Inflater inflater, String s, int i) {
        return new CompressionException(s + " (" + i + ')' + (inflater.msg != null ? ": " + inflater.msg : ""));
    }

    static CompressionException deflaterException(Deflater deflater, String s, int i) {
        return new CompressionException(s + " (" + i + ')' + (deflater.msg != null ? ": " + deflater.msg : ""));
    }

    static WrapperType convertWrapperType(ZlibWrapper zlibwrapper) {
        WrapperType wrappertype;

        switch (zlibwrapper) {
            case NONE:
                wrappertype = JZlib.W_NONE;
                break;
            case ZLIB:
                wrappertype = JZlib.W_ZLIB;
                break;
            case GZIP:
                wrappertype = JZlib.W_GZIP;
                break;
            case ZLIB_OR_NONE:
                wrappertype = JZlib.W_ANY;
                break;
            default:
                throw new Error();
        }

        return wrappertype;
    }

    private ZlibUtil() {}
}
