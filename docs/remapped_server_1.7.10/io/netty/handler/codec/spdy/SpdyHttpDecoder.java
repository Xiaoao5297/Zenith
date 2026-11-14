package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SpdyHttpDecoder extends MessageToMessageDecoder<SpdyFrame> {

    private final int spdyVersion;
    private final int maxContentLength;
    private final Map<Integer, FullHttpMessage> messageMap;

    public SpdyHttpDecoder(int i, int j) {
        this(i, j, new HashMap());
    }

    protected SpdyHttpDecoder(int i, int j, Map<Integer, FullHttpMessage> map) {
        if (i >= 2 && i <= 3) {
            if (j <= 0) {
                throw new IllegalArgumentException("maxContentLength must be a positive integer: " + j);
            } else {
                this.spdyVersion = i;
                this.maxContentLength = j;
                this.messageMap = map;
            }
        } else {
            throw new IllegalArgumentException("unsupported version: " + i);
        }
    }

    protected FullHttpMessage putMessage(int i, FullHttpMessage fullhttpmessage) {
        return (FullHttpMessage) this.messageMap.put(i, fullhttpmessage);
    }

    protected FullHttpMessage getMessage(int i) {
        return (FullHttpMessage) this.messageMap.get(i);
    }

    protected FullHttpMessage removeMessage(int i) {
        return (FullHttpMessage) this.messageMap.remove(i);
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, SpdyFrame spdyframe, List<Object> list) throws Exception {
        int i;
        DefaultSpdyRstStreamFrame defaultspdyrststreamframe;

        if (spdyframe instanceof SpdySynStreamFrame) {
            SpdySynStreamFrame spdysynstreamframe = (SpdySynStreamFrame) spdyframe;

            i = spdysynstreamframe.getStreamId();
            if (SpdyCodecUtil.isServerId(i)) {
                int j = spdysynstreamframe.getAssociatedToStreamId();

                if (j == 0) {
                    defaultspdyrststreamframe = new DefaultSpdyRstStreamFrame(i, SpdyStreamStatus.INVALID_STREAM);
                    channelhandlercontext.writeAndFlush(defaultspdyrststreamframe);
                    return;
                }

                String s = SpdyHeaders.getUrl(this.spdyVersion, spdysynstreamframe);
                DefaultSpdyRstStreamFrame defaultspdyrststreamframe1;

                if (s == null) {
                    defaultspdyrststreamframe1 = new DefaultSpdyRstStreamFrame(i, SpdyStreamStatus.PROTOCOL_ERROR);
                    channelhandlercontext.writeAndFlush(defaultspdyrststreamframe1);
                    return;
                }

                if (spdysynstreamframe.isTruncated()) {
                    defaultspdyrststreamframe1 = new DefaultSpdyRstStreamFrame(i, SpdyStreamStatus.INTERNAL_ERROR);
                    channelhandlercontext.writeAndFlush(defaultspdyrststreamframe1);
                    return;
                }

                try {
                    FullHttpResponse fullhttpresponse = createHttpResponse(this.spdyVersion, spdysynstreamframe);

                    SpdyHttpHeaders.setStreamId(fullhttpresponse, i);
                    SpdyHttpHeaders.setAssociatedToStreamId(fullhttpresponse, j);
                    SpdyHttpHeaders.setPriority(fullhttpresponse, spdysynstreamframe.getPriority());
                    SpdyHttpHeaders.setUrl(fullhttpresponse, s);
                    if (spdysynstreamframe.isLast()) {
                        HttpHeaders.setContentLength(fullhttpresponse, 0L);
                        list.add(fullhttpresponse);
                    } else {
                        this.putMessage(i, fullhttpresponse);
                    }
                } catch (Exception exception) {
                    DefaultSpdyRstStreamFrame defaultspdyrststreamframe2 = new DefaultSpdyRstStreamFrame(i, SpdyStreamStatus.PROTOCOL_ERROR);

                    channelhandlercontext.writeAndFlush(defaultspdyrststreamframe2);
                }
            } else {
                if (spdysynstreamframe.isTruncated()) {
                    DefaultSpdySynReplyFrame defaultspdysynreplyframe = new DefaultSpdySynReplyFrame(i);

                    defaultspdysynreplyframe.setLast(true);
                    SpdyHeaders.setStatus(this.spdyVersion, defaultspdysynreplyframe, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
                    SpdyHeaders.setVersion(this.spdyVersion, defaultspdysynreplyframe, HttpVersion.HTTP_1_0);
                    channelhandlercontext.writeAndFlush(defaultspdysynreplyframe);
                    return;
                }

                try {
                    FullHttpRequest fullhttprequest = createHttpRequest(this.spdyVersion, spdysynstreamframe);

                    SpdyHttpHeaders.setStreamId(fullhttprequest, i);
                    if (spdysynstreamframe.isLast()) {
                        list.add(fullhttprequest);
                    } else {
                        this.putMessage(i, fullhttprequest);
                    }
                } catch (Exception exception1) {
                    DefaultSpdySynReplyFrame defaultspdysynreplyframe1 = new DefaultSpdySynReplyFrame(i);

                    defaultspdysynreplyframe1.setLast(true);
                    SpdyHeaders.setStatus(this.spdyVersion, defaultspdysynreplyframe1, HttpResponseStatus.BAD_REQUEST);
                    SpdyHeaders.setVersion(this.spdyVersion, defaultspdysynreplyframe1, HttpVersion.HTTP_1_0);
                    channelhandlercontext.writeAndFlush(defaultspdysynreplyframe1);
                }
            }
        } else if (spdyframe instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame spdysynreplyframe = (SpdySynReplyFrame) spdyframe;

            i = spdysynreplyframe.getStreamId();
            if (spdysynreplyframe.isTruncated()) {
                DefaultSpdyRstStreamFrame defaultspdyrststreamframe3 = new DefaultSpdyRstStreamFrame(i, SpdyStreamStatus.INTERNAL_ERROR);

                channelhandlercontext.writeAndFlush(defaultspdyrststreamframe3);
                return;
            }

            try {
                FullHttpResponse fullhttpresponse1 = createHttpResponse(this.spdyVersion, spdysynreplyframe);

                SpdyHttpHeaders.setStreamId(fullhttpresponse1, i);
                if (spdysynreplyframe.isLast()) {
                    HttpHeaders.setContentLength(fullhttpresponse1, 0L);
                    list.add(fullhttpresponse1);
                } else {
                    this.putMessage(i, fullhttpresponse1);
                }
            } catch (Exception exception2) {
                defaultspdyrststreamframe = new DefaultSpdyRstStreamFrame(i, SpdyStreamStatus.PROTOCOL_ERROR);
                channelhandlercontext.writeAndFlush(defaultspdyrststreamframe);
            }
        } else {
            FullHttpMessage fullhttpmessage;

            if (spdyframe instanceof SpdyHeadersFrame) {
                SpdyHeadersFrame spdyheadersframe = (SpdyHeadersFrame) spdyframe;

                i = spdyheadersframe.getStreamId();
                fullhttpmessage = this.getMessage(i);
                if (fullhttpmessage == null) {
                    return;
                }

                if (!spdyheadersframe.isTruncated()) {
                    Iterator iterator = spdyheadersframe.headers().entries().iterator();

                    while (iterator.hasNext()) {
                        Entry entry = (Entry) iterator.next();

                        fullhttpmessage.headers().add((String) entry.getKey(), entry.getValue());
                    }
                }

                if (spdyheadersframe.isLast()) {
                    HttpHeaders.setContentLength(fullhttpmessage, (long) fullhttpmessage.content().readableBytes());
                    this.removeMessage(i);
                    list.add(fullhttpmessage);
                }
            } else if (spdyframe instanceof SpdyDataFrame) {
                SpdyDataFrame spdydataframe = (SpdyDataFrame) spdyframe;

                i = spdydataframe.getStreamId();
                fullhttpmessage = this.getMessage(i);
                if (fullhttpmessage == null) {
                    return;
                }

                ByteBuf bytebuf = fullhttpmessage.content();

                if (bytebuf.readableBytes() > this.maxContentLength - spdydataframe.content().readableBytes()) {
                    this.removeMessage(i);
                    throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
                }

                ByteBuf bytebuf1 = spdydataframe.content();
                int k = bytebuf1.readableBytes();

                bytebuf.writeBytes(bytebuf1, bytebuf1.readerIndex(), k);
                if (spdydataframe.isLast()) {
                    HttpHeaders.setContentLength(fullhttpmessage, (long) bytebuf.readableBytes());
                    this.removeMessage(i);
                    list.add(fullhttpmessage);
                }
            } else if (spdyframe instanceof SpdyRstStreamFrame) {
                SpdyRstStreamFrame spdyrststreamframe = (SpdyRstStreamFrame) spdyframe;

                i = spdyrststreamframe.getStreamId();
                this.removeMessage(i);
            }
        }

    }

    private static FullHttpRequest createHttpRequest(int i, SpdyHeadersFrame spdyheadersframe) throws Exception {
        HttpMethod httpmethod = SpdyHeaders.getMethod(i, spdyheadersframe);
        String s = SpdyHeaders.getUrl(i, spdyheadersframe);
        HttpVersion httpversion = SpdyHeaders.getVersion(i, spdyheadersframe);

        SpdyHeaders.removeMethod(i, spdyheadersframe);
        SpdyHeaders.removeUrl(i, spdyheadersframe);
        SpdyHeaders.removeVersion(i, spdyheadersframe);
        DefaultFullHttpRequest defaultfullhttprequest = new DefaultFullHttpRequest(httpversion, httpmethod, s);

        SpdyHeaders.removeScheme(i, spdyheadersframe);
        if (i >= 3) {
            String s1 = SpdyHeaders.getHost(spdyheadersframe);

            SpdyHeaders.removeHost(spdyheadersframe);
            HttpHeaders.setHost(defaultfullhttprequest, s1);
        }

        Iterator iterator = spdyheadersframe.headers().entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            defaultfullhttprequest.headers().add((String) entry.getKey(), entry.getValue());
        }

        HttpHeaders.setKeepAlive(defaultfullhttprequest, true);
        defaultfullhttprequest.headers().remove("Transfer-Encoding");
        return defaultfullhttprequest;
    }

    private static FullHttpResponse createHttpResponse(int i, SpdyHeadersFrame spdyheadersframe) throws Exception {
        HttpResponseStatus httpresponsestatus = SpdyHeaders.getStatus(i, spdyheadersframe);
        HttpVersion httpversion = SpdyHeaders.getVersion(i, spdyheadersframe);

        SpdyHeaders.removeStatus(i, spdyheadersframe);
        SpdyHeaders.removeVersion(i, spdyheadersframe);
        DefaultFullHttpResponse defaultfullhttpresponse = new DefaultFullHttpResponse(httpversion, httpresponsestatus);
        Iterator iterator = spdyheadersframe.headers().entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            defaultfullhttpresponse.headers().add((String) entry.getKey(), entry.getValue());
        }

        HttpHeaders.setKeepAlive(defaultfullhttpresponse, true);
        defaultfullhttpresponse.headers().remove("Transfer-Encoding");
        defaultfullhttpresponse.headers().remove("Trailer");
        return defaultfullhttpresponse;
    }
}
