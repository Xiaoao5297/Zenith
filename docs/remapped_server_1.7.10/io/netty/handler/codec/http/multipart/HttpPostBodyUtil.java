package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;

final class HttpPostBodyUtil {

    public static final int chunkSize = 8096;
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String NAME = "name";
    public static final String FILENAME = "filename";
    public static final String FORM_DATA = "form-data";
    public static final String ATTACHMENT = "attachment";
    public static final String FILE = "file";
    public static final String MULTIPART_MIXED = "multipart/mixed";
    public static final Charset ISO_8859_1 = CharsetUtil.ISO_8859_1;
    public static final Charset US_ASCII = CharsetUtil.US_ASCII;
    public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";

    private HttpPostBodyUtil() {}

    static int findNonWhitespace(String s, int i) {
        int j;

        for (j = i; j < s.length() && Character.isWhitespace(s.charAt(j)); ++j) {
            ;
        }

        return j;
    }

    static int findWhitespace(String s, int i) {
        int j;

        for (j = i; j < s.length() && !Character.isWhitespace(s.charAt(j)); ++j) {
            ;
        }

        return j;
    }

    static int findEndOfString(String s) {
        int i;

        for (i = s.length(); i > 0 && Character.isWhitespace(s.charAt(i - 1)); --i) {
            ;
        }

        return i;
    }

    static class SeekAheadOptimize {

        byte[] bytes;
        int readerIndex;
        int pos;
        int origPos;
        int limit;
        ByteBuf buffer;

        SeekAheadOptimize(ByteBuf bytebuf) throws HttpPostBodyUtil.SeekAheadNoBackArrayException {
            if (!bytebuf.hasArray()) {
                throw new HttpPostBodyUtil.SeekAheadNoBackArrayException();
            } else {
                this.buffer = bytebuf;
                this.bytes = bytebuf.array();
                this.readerIndex = bytebuf.readerIndex();
                this.origPos = this.pos = bytebuf.arrayOffset() + this.readerIndex;
                this.limit = bytebuf.arrayOffset() + bytebuf.writerIndex();
            }
        }

        void setReadPosition(int i) {
            this.pos -= i;
            this.readerIndex = this.getReadPosition(this.pos);
            this.buffer.readerIndex(this.readerIndex);
        }

        int getReadPosition(int i) {
            return i - this.origPos + this.readerIndex;
        }

        void clear() {
            this.buffer = null;
            this.bytes = null;
            this.limit = 0;
            this.pos = 0;
            this.readerIndex = 0;
        }
    }

    static class SeekAheadNoBackArrayException extends Exception {

        private static final long serialVersionUID = -630418804938699495L;

        SeekAheadNoBackArrayException() {}
    }

    public static enum TransferEncodingMechanism {

        BIT7("7bit"), BIT8("8bit"), BINARY("binary");

        private final String value;

        private TransferEncodingMechanism(String s) {
            this.value = s;
        }

        private TransferEncodingMechanism() {
            this.value = this.name();
        }

        public String value() {
            return this.value;
        }

        public String toString() {
            return this.value;
        }
    }
}
