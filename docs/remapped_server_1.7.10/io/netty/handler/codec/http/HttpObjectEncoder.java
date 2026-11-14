package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public abstract class HttpObjectEncoder<H extends HttpMessage> extends MessageToMessageEncoder<Object> {

    private static final byte[] CRLF = new byte[]{13, 10};
    private static final byte[] ZERO_CRLF = new byte[]{48, 13, 10};
    private static final byte[] ZERO_CRLF_CRLF = new byte[]{48, 13, 10, 13, 10};
    private static final byte[] HEADER_SEPARATOR = new byte[]{58, 32};
    private static final ByteBuf CRLF_BUF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(HttpObjectEncoder.CRLF.length).writeBytes(HttpObjectEncoder.CRLF));
    private static final ByteBuf ZERO_CRLF_CRLF_BUF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(HttpObjectEncoder.ZERO_CRLF_CRLF.length).writeBytes(HttpObjectEncoder.ZERO_CRLF_CRLF));
    private static final int ST_INIT = 0;
    private static final int ST_CONTENT_NON_CHUNK = 1;
    private static final int ST_CONTENT_CHUNK = 2;
    private int state = 0;

    public HttpObjectEncoder() {}

    @Override
    protected void encode(ChannelHandlerContext channelhandlercontext, Object object, List<Object> list) throws Exception {
        if (object instanceof HttpMessage) {
            if (this.state != 0) {
                throw new IllegalStateException("unexpected message type: " + object.getClass().getSimpleName());
            }

            HttpMessage httpmessage = (HttpMessage) object;
            ByteBuf bytebuf = channelhandlercontext.alloc().buffer();

            this.encodeInitialLine(bytebuf, httpmessage);
            encodeHeaders(bytebuf, httpmessage.headers());
            bytebuf.writeBytes(HttpObjectEncoder.CRLF);
            list.add(bytebuf);
            this.state = HttpHeaders.isTransferEncodingChunked(httpmessage) ? 2 : 1;
        }

        if (object instanceof HttpContent || object instanceof ByteBuf || object instanceof FileRegion) {
            if (this.state == 0) {
                throw new IllegalStateException("unexpected message type: " + object.getClass().getSimpleName());
            }

            int i = contentLength(object);

            if (this.state == 1) {
                if (i > 0) {
                    list.add(encodeAndRetain(object));
                } else {
                    list.add(Unpooled.EMPTY_BUFFER);
                }

                if (object instanceof LastHttpContent) {
                    this.state = 0;
                }
            } else {
                if (this.state != 2) {
                    throw new Error();
                }

                this.encodeChunkedContent(channelhandlercontext, object, i, list);
            }
        }

    }

    private void encodeChunkedContent(ChannelHandlerContext channelhandlercontext, Object object, int i, List<Object> list) {
        ByteBuf bytebuf;

        if (i > 0) {
            byte[] abyte = Integer.toHexString(i).getBytes(CharsetUtil.US_ASCII);

            bytebuf = channelhandlercontext.alloc().buffer(abyte.length + 2);
            bytebuf.writeBytes(abyte);
            bytebuf.writeBytes(HttpObjectEncoder.CRLF);
            list.add(bytebuf);
            list.add(encodeAndRetain(object));
            list.add(HttpObjectEncoder.CRLF_BUF.duplicate());
        }

        if (object instanceof LastHttpContent) {
            HttpHeaders httpheaders = ((LastHttpContent) object).trailingHeaders();

            if (httpheaders.isEmpty()) {
                list.add(HttpObjectEncoder.ZERO_CRLF_CRLF_BUF.duplicate());
            } else {
                bytebuf = channelhandlercontext.alloc().buffer();
                bytebuf.writeBytes(HttpObjectEncoder.ZERO_CRLF);
                encodeHeaders(bytebuf, httpheaders);
                bytebuf.writeBytes(HttpObjectEncoder.CRLF);
                list.add(bytebuf);
            }

            this.state = 0;
        } else if (i == 0) {
            list.add(Unpooled.EMPTY_BUFFER);
        }

    }

    @Override
    public boolean acceptOutboundMessage(Object object) throws Exception {
        return object instanceof HttpObject || object instanceof ByteBuf || object instanceof FileRegion;
    }

    private static Object encodeAndRetain(Object object) {
        if (object instanceof ByteBuf) {
            return ((ByteBuf) object).retain();
        } else if (object instanceof HttpContent) {
            return ((HttpContent) object).content().retain();
        } else if (object instanceof FileRegion) {
            return ((FileRegion) object).retain();
        } else {
            throw new IllegalStateException("unexpected message type: " + object.getClass().getSimpleName());
        }
    }

    private static int contentLength(Object object) {
        if (object instanceof HttpContent) {
            return ((HttpContent) object).content().readableBytes();
        } else if (object instanceof ByteBuf) {
            return ((ByteBuf) object).readableBytes();
        } else if (object instanceof FileRegion) {
            return (int) ((FileRegion) object).count();
        } else {
            throw new IllegalStateException("unexpected message type: " + object.getClass().getSimpleName());
        }
    }

    private static void encodeHeaders(ByteBuf bytebuf, HttpHeaders httpheaders) {
        Iterator iterator = httpheaders.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            encodeHeader(bytebuf, (String) entry.getKey(), (String) entry.getValue());
        }

    }

    private static void encodeHeader(ByteBuf bytebuf, String s, String s1) {
        encodeAscii(s, bytebuf);
        bytebuf.writeBytes(HttpObjectEncoder.HEADER_SEPARATOR);
        encodeAscii(s1, bytebuf);
        bytebuf.writeBytes(HttpObjectEncoder.CRLF);
    }

    protected static void encodeAscii(String s, ByteBuf bytebuf) {
        for (int i = 0; i < s.length(); ++i) {
            bytebuf.writeByte(s.charAt(i));
        }

    }

    protected abstract void encodeInitialLine(ByteBuf bytebuf, H h0) throws Exception;
}
