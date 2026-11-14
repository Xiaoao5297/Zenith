package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class SpdyHttpEncoder extends MessageToMessageEncoder<HttpObject> {

    private final int spdyVersion;
    private int currentStreamId;

    public SpdyHttpEncoder(int i) {
        if (i >= 2 && i <= 3) {
            this.spdyVersion = i;
        } else {
            throw new IllegalArgumentException("unsupported version: " + i);
        }
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, HttpObject httpobject, List<Object> list) throws Exception {
        boolean flag = false;
        boolean flag1 = false;
        SpdySynStreamFrame spdysynstreamframe;

        if (httpobject instanceof HttpRequest) {
            HttpRequest httprequest = (HttpRequest) httpobject;

            spdysynstreamframe = this.createSynStreamFrame(httprequest);
            list.add(spdysynstreamframe);
            flag1 = spdysynstreamframe.isLast();
            flag = true;
        }

        if (httpobject instanceof HttpResponse) {
            HttpResponse httpresponse = (HttpResponse) httpobject;

            if (httpresponse.headers().contains("X-SPDY-Associated-To-Stream-ID")) {
                spdysynstreamframe = this.createSynStreamFrame(httpresponse);
                flag1 = spdysynstreamframe.isLast();
                list.add(spdysynstreamframe);
            } else {
                SpdySynReplyFrame spdysynreplyframe = this.createSynReplyFrame(httpresponse);

                flag1 = spdysynreplyframe.isLast();
                list.add(spdysynreplyframe);
            }

            flag = true;
        }

        if (httpobject instanceof HttpContent && !flag1) {
            HttpContent httpcontent = (HttpContent) httpobject;

            httpcontent.content().retain();
            DefaultSpdyDataFrame defaultspdydataframe = new DefaultSpdyDataFrame(this.currentStreamId, httpcontent.content());

            defaultspdydataframe.setLast(httpcontent instanceof LastHttpContent);
            if (httpcontent instanceof LastHttpContent) {
                LastHttpContent lasthttpcontent = (LastHttpContent) httpcontent;
                List list1 = lasthttpcontent.trailingHeaders().entries();

                if (list1.isEmpty()) {
                    list.add(defaultspdydataframe);
                } else {
                    DefaultSpdyHeadersFrame defaultspdyheadersframe = new DefaultSpdyHeadersFrame(this.currentStreamId);
                    Iterator iterator = list1.iterator();

                    while (iterator.hasNext()) {
                        Entry entry = (Entry) iterator.next();

                        defaultspdyheadersframe.headers().add((String) entry.getKey(), entry.getValue());
                    }

                    list.add(defaultspdyheadersframe);
                    list.add(defaultspdydataframe);
                }
            } else {
                list.add(defaultspdydataframe);
            }

            flag = true;
        }

        if (!flag) {
            throw new UnsupportedMessageTypeException(httpobject, new Class[0]);
        }
    }

    private SpdySynStreamFrame createSynStreamFrame(HttpMessage httpmessage) throws Exception {
        int i = SpdyHttpHeaders.getStreamId(httpmessage);
        int j = SpdyHttpHeaders.getAssociatedToStreamId(httpmessage);
        byte b0 = SpdyHttpHeaders.getPriority(httpmessage);
        String s = SpdyHttpHeaders.getUrl(httpmessage);
        String s1 = SpdyHttpHeaders.getScheme(httpmessage);

        SpdyHttpHeaders.removeStreamId(httpmessage);
        SpdyHttpHeaders.removeAssociatedToStreamId(httpmessage);
        SpdyHttpHeaders.removePriority(httpmessage);
        SpdyHttpHeaders.removeUrl(httpmessage);
        SpdyHttpHeaders.removeScheme(httpmessage);
        httpmessage.headers().remove("Connection");
        httpmessage.headers().remove("Keep-Alive");
        httpmessage.headers().remove("Proxy-Connection");
        httpmessage.headers().remove("Transfer-Encoding");
        DefaultSpdySynStreamFrame defaultspdysynstreamframe = new DefaultSpdySynStreamFrame(i, j, b0);

        if (httpmessage instanceof FullHttpRequest) {
            HttpRequest httprequest = (HttpRequest) httpmessage;

            SpdyHeaders.setMethod(this.spdyVersion, defaultspdysynstreamframe, httprequest.getMethod());
            SpdyHeaders.setUrl(this.spdyVersion, defaultspdysynstreamframe, httprequest.getUri());
            SpdyHeaders.setVersion(this.spdyVersion, defaultspdysynstreamframe, httpmessage.getProtocolVersion());
        }

        if (httpmessage instanceof HttpResponse) {
            HttpResponse httpresponse = (HttpResponse) httpmessage;

            SpdyHeaders.setStatus(this.spdyVersion, defaultspdysynstreamframe, httpresponse.getStatus());
            SpdyHeaders.setUrl(this.spdyVersion, defaultspdysynstreamframe, s);
            SpdyHeaders.setVersion(this.spdyVersion, defaultspdysynstreamframe, httpmessage.getProtocolVersion());
            defaultspdysynstreamframe.setUnidirectional(true);
        }

        if (this.spdyVersion >= 3) {
            String s2 = HttpHeaders.getHost(httpmessage);

            httpmessage.headers().remove("Host");
            SpdyHeaders.setHost(defaultspdysynstreamframe, s2);
        }

        if (s1 == null) {
            s1 = "https";
        }

        SpdyHeaders.setScheme(this.spdyVersion, defaultspdysynstreamframe, s1);
        Iterator iterator = httpmessage.headers().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            defaultspdysynstreamframe.headers().add((String) entry.getKey(), entry.getValue());
        }

        this.currentStreamId = defaultspdysynstreamframe.getStreamId();
        defaultspdysynstreamframe.setLast(isLast(httpmessage));
        return defaultspdysynstreamframe;
    }

    private SpdySynReplyFrame createSynReplyFrame(HttpResponse httpresponse) throws Exception {
        int i = SpdyHttpHeaders.getStreamId(httpresponse);

        SpdyHttpHeaders.removeStreamId(httpresponse);
        httpresponse.headers().remove("Connection");
        httpresponse.headers().remove("Keep-Alive");
        httpresponse.headers().remove("Proxy-Connection");
        httpresponse.headers().remove("Transfer-Encoding");
        DefaultSpdySynReplyFrame defaultspdysynreplyframe = new DefaultSpdySynReplyFrame(i);

        SpdyHeaders.setStatus(this.spdyVersion, defaultspdysynreplyframe, httpresponse.getStatus());
        SpdyHeaders.setVersion(this.spdyVersion, defaultspdysynreplyframe, httpresponse.getProtocolVersion());
        Iterator iterator = httpresponse.headers().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            defaultspdysynreplyframe.headers().add((String) entry.getKey(), entry.getValue());
        }

        this.currentStreamId = i;
        defaultspdysynreplyframe.setLast(isLast(httpresponse));
        return defaultspdysynreplyframe;
    }

    private static boolean isLast(HttpMessage httpmessage) {
        if (httpmessage instanceof FullHttpMessage) {
            FullHttpMessage fullhttpmessage = (FullHttpMessage) httpmessage;

            if (fullhttpmessage.trailingHeaders().isEmpty() && !fullhttpmessage.content().isReadable()) {
                return true;
            }
        }

        return false;
    }
}
