package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public abstract class HttpObjectDecoder extends ReplayingDecoder<HttpObjectDecoder.State> {

    private static final ThreadLocal<StringBuilder> BUILDERS = new ThreadLocal<StringBuilder>() {
        protected StringBuilder initialValue() {
            return new StringBuilder(512);
        }

        public StringBuilder get() {
            StringBuilder stringbuilder = (StringBuilder) super.get();

            stringbuilder.setLength(0);
            return stringbuilder;
        }
    };
    private final int maxInitialLineLength;
    private final int maxHeaderSize;
    private final int maxChunkSize;
    private final boolean chunkedSupported;
    private ByteBuf content;
    private HttpMessage message;
    private long chunkSize;
    private int headerSize;
    private int contentRead;

    protected HttpObjectDecoder() {
        this(4096, 8192, 8192, true);
    }

    protected HttpObjectDecoder(int i, int j, int k, boolean flag) {
        super(HttpObjectDecoder.State.SKIP_CONTROL_CHARS);
        if (i <= 0) {
            throw new IllegalArgumentException("maxInitialLineLength must be a positive integer: " + i);
        } else if (j <= 0) {
            throw new IllegalArgumentException("maxHeaderSize must be a positive integer: " + j);
        } else if (k < 0) {
            throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + k);
        } else {
            this.maxInitialLineLength = i;
            this.maxHeaderSize = j;
            this.maxChunkSize = k;
            this.chunkedSupported = flag;
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        int i;
        int j;
        int k;

        switch ((HttpObjectDecoder.State) this.state()) {
            case READ_FIXED_LENGTH_CONTENT:
                this.readFixedLengthContent(channelhandlercontext, bytebuf, list);
                return;
            case READ_VARIABLE_LENGTH_CONTENT:
                i = this.actualReadableBytes();
                if (i > this.maxChunkSize) {
                    i = this.maxChunkSize;
                }

                list.add(this.message);
                list.add(new DefaultHttpContent(ByteBufUtil.readBytes(channelhandlercontext.alloc(), bytebuf, i)));
                return;
            case SKIP_CONTROL_CHARS:
                try {
                    skipControlCharacters(bytebuf);
                    this.checkpoint(HttpObjectDecoder.State.READ_INITIAL);
                } finally {
                    this.checkpoint();
                }
            case READ_INITIAL:
                try {
                    String[] astring = splitInitialLine(readLine(bytebuf, this.maxInitialLineLength));

                    if (astring.length < 3) {
                        this.checkpoint(HttpObjectDecoder.State.SKIP_CONTROL_CHARS);
                        return;
                    }

                    this.message = this.createMessage(astring);
                    this.checkpoint(HttpObjectDecoder.State.READ_HEADER);
                } catch (Exception exception) {
                    list.add(this.invalidMessage(exception));
                    return;
                }
            case READ_HEADER:
                break;
            case READ_VARIABLE_LENGTH_CONTENT_AS_CHUNKS:
                i = this.actualReadableBytes();
                if (i > this.maxChunkSize) {
                    i = this.maxChunkSize;
                }

                ByteBuf bytebuf1 = ByteBufUtil.readBytes(channelhandlercontext.alloc(), bytebuf, i);

                if (!bytebuf.isReadable()) {
                    this.reset();
                    list.add(new DefaultLastHttpContent(bytebuf1));
                    return;
                }

                list.add(new DefaultHttpContent(bytebuf1));
                return;
            case READ_FIXED_LENGTH_CONTENT_AS_CHUNKS:
                long l = this.chunkSize;

                j = this.actualReadableBytes();
                if (j == 0) {
                    return;
                }

                int i1 = j;

                if (j > this.maxChunkSize) {
                    i1 = this.maxChunkSize;
                }

                if ((long) i1 > l) {
                    i1 = (int) l;
                }

                ByteBuf bytebuf2 = ByteBufUtil.readBytes(channelhandlercontext.alloc(), bytebuf, i1);

                if (l > (long) i1) {
                    l -= (long) i1;
                } else {
                    l = 0L;
                }

                this.chunkSize = l;
                if (l == 0L) {
                    this.reset();
                    list.add(new DefaultLastHttpContent(bytebuf2));
                    return;
                }

                list.add(new DefaultHttpContent(bytebuf2));
                return;
            case READ_CHUNK_SIZE:
                try {
                    StringBuilder stringbuilder = readLine(bytebuf, this.maxInitialLineLength);

                    k = getChunkSize(stringbuilder.toString());
                    this.chunkSize = (long) k;
                    if (k == 0) {
                        this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_FOOTER);
                        return;
                    }

                    if (k > this.maxChunkSize) {
                        this.checkpoint(HttpObjectDecoder.State.READ_CHUNKED_CONTENT_AS_CHUNKS);
                    } else {
                        this.checkpoint(HttpObjectDecoder.State.READ_CHUNKED_CONTENT);
                    }
                } catch (Exception exception1) {
                    list.add(this.invalidChunk(exception1));
                    return;
                }
            case READ_CHUNKED_CONTENT:
                assert this.chunkSize <= 2147483647L;

                DefaultHttpContent defaulthttpcontent = new DefaultHttpContent(ByteBufUtil.readBytes(channelhandlercontext.alloc(), bytebuf, (int) this.chunkSize));

                this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_DELIMITER);
                list.add(defaulthttpcontent);
                return;
            case READ_CHUNKED_CONTENT_AS_CHUNKS:
                assert this.chunkSize <= 2147483647L;

                i = (int) this.chunkSize;
                k = this.actualReadableBytes();
                if (k == 0) {
                    return;
                }

                j = i;
                if (i > this.maxChunkSize) {
                    j = this.maxChunkSize;
                }

                if (j > k) {
                    j = k;
                }

                DefaultHttpContent defaulthttpcontent1 = new DefaultHttpContent(ByteBufUtil.readBytes(channelhandlercontext.alloc(), bytebuf, j));

                if (i > j) {
                    i -= j;
                } else {
                    i = 0;
                }

                this.chunkSize = (long) i;
                if (i == 0) {
                    this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_DELIMITER);
                }

                list.add(defaulthttpcontent1);
                return;
            case READ_CHUNK_DELIMITER:
                do {
                    while (true) {
                        i = bytebuf.readByte();
                        if (i == 13) {
                            break;
                        }

                        if (i == 10) {
                            this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_SIZE);
                            return;
                        }

                        this.checkpoint();
                    }
                } while (bytebuf.readByte() != 10);

                this.checkpoint(HttpObjectDecoder.State.READ_CHUNK_SIZE);
                return;
            case READ_CHUNK_FOOTER:
                try {
                    LastHttpContent lasthttpcontent = this.readTrailingHeaders(bytebuf);

                    if (this.maxChunkSize == 0) {
                        this.reset(list);
                        return;
                    }

                    this.reset();
                    list.add(lasthttpcontent);
                    return;
                } catch (Exception exception2) {
                    list.add(this.invalidChunk(exception2));
                    return;
                }
            case BAD_MESSAGE:
                bytebuf.skipBytes(this.actualReadableBytes());
                return;
            default:
                throw new Error("Shouldn't reach here.");
        }

        try {
            HttpObjectDecoder.State httpobjectdecoder_state = this.readHeaders(bytebuf);

            this.checkpoint(httpobjectdecoder_state);
            if (httpobjectdecoder_state == HttpObjectDecoder.State.READ_CHUNK_SIZE) {
                if (!this.chunkedSupported) {
                    throw new IllegalArgumentException("Chunked messages not supported");
                } else {
                    list.add(this.message);
                }
            } else if (httpobjectdecoder_state == HttpObjectDecoder.State.SKIP_CONTROL_CHARS) {
                this.reset(list);
            } else {
                long j1 = HttpHeaders.getContentLength(this.message, -1L);

                if (j1 != 0L && (j1 != -1L || !this.isDecodingRequest())) {
                    switch (httpobjectdecoder_state) {
                        case READ_FIXED_LENGTH_CONTENT:
                            if (j1 > (long) this.maxChunkSize || HttpHeaders.is100ContinueExpected(this.message)) {
                                this.checkpoint(HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT_AS_CHUNKS);
                                this.chunkSize = HttpHeaders.getContentLength(this.message, -1L);
                                list.add(this.message);
                                return;
                            }
                            break;
                        case READ_VARIABLE_LENGTH_CONTENT:
                            if (bytebuf.readableBytes() > this.maxChunkSize || HttpHeaders.is100ContinueExpected(this.message)) {
                                this.checkpoint(HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT_AS_CHUNKS);
                                list.add(this.message);
                                return;
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unexpected state: " + httpobjectdecoder_state);
                    }

                } else {
                    this.content = Unpooled.EMPTY_BUFFER;
                    this.reset(list);
                }
            }
        } catch (Exception exception3) {
            list.add(this.invalidMessage(exception3));
        }
    }

    @Override
    protected void decodeLast(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        this.decode(channelhandlercontext, bytebuf, list);
        if (this.message != null) {
            HttpMessage httpmessage = this.message;
            int i;

            if (this.content != null) {
                i = this.content.readableBytes();
            } else {
                i = 0;
            }

            boolean flag;

            if (this.isDecodingRequest()) {
                flag = true;
            } else {
                long j = HttpHeaders.getContentLength(httpmessage, -1L);

                flag = j >= 0L && (long) i != j;
            }

            if (!flag) {
                if (i == 0) {
                    list.add(LastHttpContent.EMPTY_LAST_CONTENT);
                } else {
                    list.add(new DefaultLastHttpContent(this.content));
                }
            }
        }

    }

    protected boolean isContentAlwaysEmpty(HttpMessage httpmessage) {
        if (httpmessage instanceof HttpResponse) {
            HttpResponse httpresponse = (HttpResponse) httpmessage;
            int i = httpresponse.getStatus().code();

            if (i >= 100 && i < 200) {
                if (i == 101 && !httpresponse.headers().contains("Sec-WebSocket-Accept")) {
                    return false;
                }

                return true;
            }

            switch (i) {
                case 204:
                case 205:
                case 304:
                    return true;
            }
        }

        return false;
    }

    private void reset() {
        this.reset((List) null);
    }

    private void reset(List<Object> list) {
        if (list != null) {
            HttpMessage httpmessage = this.message;
            ByteBuf bytebuf = this.content;
            Object object;

            if (bytebuf != null && bytebuf.isReadable()) {
                object = new DefaultLastHttpContent(bytebuf);
            } else {
                object = LastHttpContent.EMPTY_LAST_CONTENT;
            }

            list.add(httpmessage);
            list.add(object);
        }

        this.content = null;
        this.message = null;
        this.checkpoint(HttpObjectDecoder.State.SKIP_CONTROL_CHARS);
    }

    private HttpMessage invalidMessage(Exception exception) {
        this.checkpoint(HttpObjectDecoder.State.BAD_MESSAGE);
        if (this.message != null) {
            this.message.setDecoderResult(DecoderResult.failure(exception));
        } else {
            this.message = this.createInvalidMessage();
            this.message.setDecoderResult(DecoderResult.failure(exception));
        }

        return this.message;
    }

    private HttpContent invalidChunk(Exception exception) {
        this.checkpoint(HttpObjectDecoder.State.BAD_MESSAGE);
        DefaultHttpContent defaulthttpcontent = new DefaultHttpContent(Unpooled.EMPTY_BUFFER);

        defaulthttpcontent.setDecoderResult(DecoderResult.failure(exception));
        return defaulthttpcontent;
    }

    private static void skipControlCharacters(ByteBuf bytebuf) {
        char c0;

        do {
            c0 = (char) bytebuf.readUnsignedByte();
        } while (Character.isISOControl(c0) || Character.isWhitespace(c0));

        bytebuf.readerIndex(bytebuf.readerIndex() - 1);
    }

    private void readFixedLengthContent(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) {
        long i = HttpHeaders.getContentLength(this.message, -1L);

        assert i <= 2147483647L;

        int j = (int) i - this.contentRead;

        if (j > this.actualReadableBytes()) {
            j = this.actualReadableBytes();
        }

        this.contentRead += j;
        if (i < (long) this.contentRead) {
            list.add(this.message);
            list.add(new DefaultHttpContent(ByteBufUtil.readBytes(channelhandlercontext.alloc(), bytebuf, j)));
        } else {
            if (this.content == null) {
                this.content = ByteBufUtil.readBytes(channelhandlercontext.alloc(), bytebuf, (int) i);
            } else {
                this.content.writeBytes(bytebuf, (int) i);
            }

            this.reset(list);
        }
    }

    private HttpObjectDecoder.State readHeaders(ByteBuf bytebuf) {
        this.headerSize = 0;
        HttpMessage httpmessage = this.message;
        HttpHeaders httpheaders = httpmessage.headers();
        StringBuilder stringbuilder = this.readHeader(bytebuf);
        String s = null;
        String s1 = null;

        if (stringbuilder.length() > 0) {
            httpheaders.clear();

            do {
                char c0 = stringbuilder.charAt(0);

                if (s != null && (c0 == ' ' || c0 == '\t')) {
                    s1 = s1 + ' ' + stringbuilder.toString().trim();
                } else {
                    if (s != null) {
                        httpheaders.add(s, (Object) s1);
                    }

                    String[] astring = splitHeader(stringbuilder);

                    s = astring[0];
                    s1 = astring[1];
                }

                stringbuilder = this.readHeader(bytebuf);
            } while (stringbuilder.length() > 0);

            if (s != null) {
                httpheaders.add(s, (Object) s1);
            }
        }

        HttpObjectDecoder.State httpobjectdecoder_state;

        if (this.isContentAlwaysEmpty(httpmessage)) {
            HttpHeaders.removeTransferEncodingChunked(httpmessage);
            httpobjectdecoder_state = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
        } else if (HttpHeaders.isTransferEncodingChunked(httpmessage)) {
            httpobjectdecoder_state = HttpObjectDecoder.State.READ_CHUNK_SIZE;
        } else if (HttpHeaders.getContentLength(httpmessage, -1L) >= 0L) {
            httpobjectdecoder_state = HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT;
        } else {
            httpobjectdecoder_state = HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;
        }

        return httpobjectdecoder_state;
    }

    private LastHttpContent readTrailingHeaders(ByteBuf bytebuf) {
        this.headerSize = 0;
        StringBuilder stringbuilder = this.readHeader(bytebuf);
        String s = null;

        if (stringbuilder.length() <= 0) {
            return LastHttpContent.EMPTY_LAST_CONTENT;
        } else {
            DefaultLastHttpContent defaultlasthttpcontent = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);

            do {
                char c0 = stringbuilder.charAt(0);

                if (s != null && (c0 == ' ' || c0 == '\t')) {
                    List list = defaultlasthttpcontent.trailingHeaders().getAll(s);

                    if (!list.isEmpty()) {
                        int i = list.size() - 1;
                        String s1 = (String) list.get(i) + stringbuilder.toString().trim();

                        list.set(i, s1);
                    }
                } else {
                    String[] astring = splitHeader(stringbuilder);
                    String s2 = astring[0];

                    if (!s2.equalsIgnoreCase("Content-Length") && !s2.equalsIgnoreCase("Transfer-Encoding") && !s2.equalsIgnoreCase("Trailer")) {
                        defaultlasthttpcontent.trailingHeaders().add(s2, (Object) astring[1]);
                    }

                    s = s2;
                }

                stringbuilder = this.readHeader(bytebuf);
            } while (stringbuilder.length() > 0);

            return defaultlasthttpcontent;
        }
    }

    private StringBuilder readHeader(ByteBuf bytebuf) {
        StringBuilder stringbuilder = (StringBuilder) HttpObjectDecoder.BUILDERS.get();
        int i = this.headerSize;

        label20:
        while (true) {
            char c0 = (char) bytebuf.readByte();

            ++i;
            switch (c0) {
                case '\n':
                    break label20;
                case '\r':
                    c0 = (char) bytebuf.readByte();
                    ++i;
                    if (c0 == '\n') {
                        break label20;
                    }
                default:
                    if (i >= this.maxHeaderSize) {
                        throw new TooLongFrameException("HTTP header is larger than " + this.maxHeaderSize + " bytes.");
                    }

                    stringbuilder.append(c0);
            }
        }

        this.headerSize = i;
        return stringbuilder;
    }

    protected abstract boolean isDecodingRequest();

    protected abstract HttpMessage createMessage(String[] astring) throws Exception;

    protected abstract HttpMessage createInvalidMessage();

    private static int getChunkSize(String s) {
        s = s.trim();

        for (int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);

            if (c0 == ';' || Character.isWhitespace(c0) || Character.isISOControl(c0)) {
                s = s.substring(0, i);
                break;
            }
        }

        return Integer.parseInt(s, 16);
    }

    private static StringBuilder readLine(ByteBuf bytebuf, int i) {
        StringBuilder stringbuilder = (StringBuilder) HttpObjectDecoder.BUILDERS.get();
        int j = 0;

        byte b0;

        do {
            while (true) {
                b0 = bytebuf.readByte();
                if (b0 == 13) {
                    b0 = bytebuf.readByte();
                    break;
                }

                if (b0 == 10) {
                    return stringbuilder;
                }

                if (j >= i) {
                    throw new TooLongFrameException("An HTTP line is larger than " + i + " bytes.");
                }

                ++j;
                stringbuilder.append((char) b0);
            }
        } while (b0 != 10);

        return stringbuilder;
    }

    private static String[] splitInitialLine(StringBuilder stringbuilder) {
        int i = findNonWhitespace(stringbuilder, 0);
        int j = findWhitespace(stringbuilder, i);
        int k = findNonWhitespace(stringbuilder, j);
        int l = findWhitespace(stringbuilder, k);
        int i1 = findNonWhitespace(stringbuilder, l);
        int j1 = findEndOfString(stringbuilder);

        return new String[]{stringbuilder.substring(i, j), stringbuilder.substring(k, l), i1 < j1 ? stringbuilder.substring(i1, j1) : ""};
    }

    private static String[] splitHeader(StringBuilder stringbuilder) {
        int i = stringbuilder.length();
        int j = findNonWhitespace(stringbuilder, 0);

        int k;

        for (k = j; k < i; ++k) {
            char c0 = stringbuilder.charAt(k);

            if (c0 == ':' || Character.isWhitespace(c0)) {
                break;
            }
        }

        int l;

        for (l = k; l < i; ++l) {
            if (stringbuilder.charAt(l) == ':') {
                ++l;
                break;
            }
        }

        int i1 = findNonWhitespace(stringbuilder, l);

        if (i1 == i) {
            return new String[]{stringbuilder.substring(j, k), ""};
        } else {
            int j1 = findEndOfString(stringbuilder);

            return new String[]{stringbuilder.substring(j, k), stringbuilder.substring(i1, j1)};
        }
    }

    private static int findNonWhitespace(CharSequence charsequence, int i) {
        int j;

        for (j = i; j < charsequence.length() && Character.isWhitespace(charsequence.charAt(j)); ++j) {
            ;
        }

        return j;
    }

    private static int findWhitespace(CharSequence charsequence, int i) {
        int j;

        for (j = i; j < charsequence.length() && !Character.isWhitespace(charsequence.charAt(j)); ++j) {
            ;
        }

        return j;
    }

    private static int findEndOfString(CharSequence charsequence) {
        int i;

        for (i = charsequence.length(); i > 0 && Character.isWhitespace(charsequence.charAt(i - 1)); --i) {
            ;
        }

        return i;
    }

    static enum State {

        SKIP_CONTROL_CHARS, READ_INITIAL, READ_HEADER, READ_VARIABLE_LENGTH_CONTENT, READ_VARIABLE_LENGTH_CONTENT_AS_CHUNKS, READ_FIXED_LENGTH_CONTENT, READ_FIXED_LENGTH_CONTENT_AS_CHUNKS, READ_CHUNK_SIZE, READ_CHUNKED_CONTENT, READ_CHUNKED_CONTENT_AS_CHUNKS, READ_CHUNK_DELIMITER, READ_CHUNK_FOOTER, BAD_MESSAGE;

        private State() {}
    }
}
