package io.netty.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.IdentityHashMap;
import java.util.Map;

public final class CharsetUtil {

    public static final Charset UTF_16 = Charset.forName("UTF-16");
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset US_ASCII = Charset.forName("US-ASCII");
    private static final ThreadLocal<Map<Charset, CharsetEncoder>> encoders = new ThreadLocal<Map<Charset, CharsetEncoder>>() {
        protected Map<Charset, CharsetEncoder> initialValue() {
            return new IdentityHashMap();
        }
    };
    private static final ThreadLocal<Map<Charset, CharsetDecoder>> decoders = new ThreadLocal<Map<Charset, CharsetDecoder>>() {
        protected Map<Charset, CharsetDecoder> initialValue() {
            return new IdentityHashMap();
        }
    };

    public static CharsetEncoder getEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            Map map = (Map) CharsetUtil.encoders.get();
            CharsetEncoder charsetencoder = (CharsetEncoder) map.get(charset);

            if (charsetencoder != null) {
                charsetencoder.reset();
                charsetencoder.onMalformedInput(CodingErrorAction.REPLACE);
                charsetencoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
                return charsetencoder;
            } else {
                charsetencoder = charset.newEncoder();
                charsetencoder.onMalformedInput(CodingErrorAction.REPLACE);
                charsetencoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
                map.put(charset, charsetencoder);
                return charsetencoder;
            }
        }
    }

    public static CharsetDecoder getDecoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            Map map = (Map) CharsetUtil.decoders.get();
            CharsetDecoder charsetdecoder = (CharsetDecoder) map.get(charset);

            if (charsetdecoder != null) {
                charsetdecoder.reset();
                charsetdecoder.onMalformedInput(CodingErrorAction.REPLACE);
                charsetdecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
                return charsetdecoder;
            } else {
                charsetdecoder = charset.newDecoder();
                charsetdecoder.onMalformedInput(CodingErrorAction.REPLACE);
                charsetdecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
                map.put(charset, charsetdecoder);
                return charsetdecoder;
            }
        }
    }

    private CharsetUtil() {}
}
