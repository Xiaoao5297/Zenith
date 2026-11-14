package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.internal.StringUtil;

public class HttpContentCompressor extends HttpContentEncoder {

    private final int compressionLevel;
    private final int windowBits;
    private final int memLevel;

    public HttpContentCompressor() {
        this(6);
    }

    public HttpContentCompressor(int i) {
        this(i, 15, 8);
    }

    public HttpContentCompressor(int i, int j, int k) {
        if (i >= 0 && i <= 9) {
            if (j >= 9 && j <= 15) {
                if (k >= 1 && k <= 9) {
                    this.compressionLevel = i;
                    this.windowBits = j;
                    this.memLevel = k;
                } else {
                    throw new IllegalArgumentException("memLevel: " + k + " (expected: 1-9)");
                }
            } else {
                throw new IllegalArgumentException("windowBits: " + j + " (expected: 9-15)");
            }
        } else {
            throw new IllegalArgumentException("compressionLevel: " + i + " (expected: 0-9)");
        }
    }

    @Override
    protected HttpContentEncoder.Result beginEncode(HttpResponse httpresponse, String s) throws Exception {
        String s1 = httpresponse.headers().get("Content-Encoding");

        if (s1 != null && !"identity".equalsIgnoreCase(s1)) {
            return null;
        } else {
            ZlibWrapper zlibwrapper = this.determineWrapper(s);

            if (zlibwrapper == null) {
                return null;
            } else {
                String s2;

                switch (zlibwrapper) {
                    case GZIP:
                        s2 = "gzip";
                        break;
                    case ZLIB:
                        s2 = "deflate";
                        break;
                    default:
                        throw new Error();
                }

                return new HttpContentEncoder.Result(s2, new EmbeddedChannel(new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder(zlibwrapper, this.compressionLevel, this.windowBits, this.memLevel)}));
            }
        }
    }

    protected ZlibWrapper determineWrapper(String s) {
        float f = -1.0F;
        float f1 = -1.0F;
        float f2 = -1.0F;
        String[] astring = StringUtil.split(s, ',');
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String s1 = astring[j];
            float f3 = 1.0F;
            int k = s1.indexOf(61);

            if (k != -1) {
                try {
                    f3 = Float.valueOf(s1.substring(k + 1));
                } catch (NumberFormatException numberformatexception) {
                    f3 = 0.0F;
                }
            }

            if (s1.contains("*")) {
                f = f3;
            } else if (s1.contains("gzip") && f3 > f1) {
                f1 = f3;
            } else if (s1.contains("deflate") && f3 > f2) {
                f2 = f3;
            }
        }

        if (f1 <= 0.0F && f2 <= 0.0F) {
            if (f > 0.0F) {
                if (f1 == -1.0F) {
                    return ZlibWrapper.GZIP;
                }

                if (f2 == -1.0F) {
                    return ZlibWrapper.ZLIB;
                }
            }

            return null;
        } else if (f1 >= f2) {
            return ZlibWrapper.GZIP;
        } else {
            return ZlibWrapper.ZLIB;
        }
    }
}
