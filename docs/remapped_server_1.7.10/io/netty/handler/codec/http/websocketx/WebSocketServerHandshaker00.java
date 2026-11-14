package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.regex.Pattern;

public class WebSocketServerHandshaker00 extends WebSocketServerHandshaker {

    private static final Pattern BEGINNING_DIGIT = Pattern.compile("[^0-9]");
    private static final Pattern BEGINNING_SPACE = Pattern.compile("[^ ]");

    public WebSocketServerHandshaker00(String s, String s1, int i) {
        super(WebSocketVersion.V00, s, s1, i);
    }

    @Override
    protected FullHttpResponse newHandshakeResponse(FullHttpRequest fullhttprequest, HttpHeaders httpheaders) {
        if ("Upgrade".equalsIgnoreCase(fullhttprequest.headers().get("Connection")) && "WebSocket".equalsIgnoreCase(fullhttprequest.headers().get("Upgrade"))) {
            boolean flag = fullhttprequest.headers().contains("Sec-WebSocket-Key1") && fullhttprequest.headers().contains("Sec-WebSocket-Key2");
            DefaultFullHttpResponse defaultfullhttpresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, flag ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"));

            if (httpheaders != null) {
                defaultfullhttpresponse.headers().add(httpheaders);
            }

            defaultfullhttpresponse.headers().add("Upgrade", (Object) "WebSocket");
            defaultfullhttpresponse.headers().add("Connection", (Object) "Upgrade");
            String s;

            if (flag) {
                defaultfullhttpresponse.headers().add("Sec-WebSocket-Origin", (Object) fullhttprequest.headers().get("Origin"));
                defaultfullhttpresponse.headers().add("Sec-WebSocket-Location", (Object) this.uri());
                s = fullhttprequest.headers().get("Sec-WebSocket-Protocol");
                String s1;

                if (s != null) {
                    s1 = this.selectSubprotocol(s);
                    if (s1 == null) {
                        throw new WebSocketHandshakeException("Requested subprotocol(s) not supported: " + s);
                    }

                    defaultfullhttpresponse.headers().add("Sec-WebSocket-Protocol", (Object) s1);
                }

                s1 = fullhttprequest.headers().get("Sec-WebSocket-Key1");
                String s2 = fullhttprequest.headers().get("Sec-WebSocket-Key2");
                int i = (int) (Long.parseLong(WebSocketServerHandshaker00.BEGINNING_DIGIT.matcher(s1).replaceAll("")) / (long) WebSocketServerHandshaker00.BEGINNING_SPACE.matcher(s1).replaceAll("").length());
                int j = (int) (Long.parseLong(WebSocketServerHandshaker00.BEGINNING_DIGIT.matcher(s2).replaceAll("")) / (long) WebSocketServerHandshaker00.BEGINNING_SPACE.matcher(s2).replaceAll("").length());
                long k = fullhttprequest.content().readLong();
                ByteBuf bytebuf = Unpooled.buffer(16);

                bytebuf.writeInt(i);
                bytebuf.writeInt(j);
                bytebuf.writeLong(k);
                defaultfullhttpresponse.content().writeBytes(WebSocketUtil.md5(bytebuf.array()));
            } else {
                defaultfullhttpresponse.headers().add("WebSocket-Origin", (Object) fullhttprequest.headers().get("Origin"));
                defaultfullhttpresponse.headers().add("WebSocket-Location", (Object) this.uri());
                s = fullhttprequest.headers().get("WebSocket-Protocol");
                if (s != null) {
                    defaultfullhttpresponse.headers().add("WebSocket-Protocol", (Object) this.selectSubprotocol(s));
                }
            }

            return defaultfullhttpresponse;
        } else {
            throw new WebSocketHandshakeException("not a WebSocket handshake request: missing upgrade");
        }
    }

    @Override
    public ChannelFuture close(Channel channel, CloseWebSocketFrame closewebsocketframe, ChannelPromise channelpromise) {
        return channel.writeAndFlush(closewebsocketframe, channelpromise);
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket00FrameDecoder(this.maxFramePayloadLength());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket00FrameEncoder();
    }
}
