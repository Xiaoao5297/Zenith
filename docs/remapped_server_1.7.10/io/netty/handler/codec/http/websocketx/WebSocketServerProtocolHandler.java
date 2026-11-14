package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import java.util.List;

public class WebSocketServerProtocolHandler extends WebSocketProtocolHandler {

    private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = new AttributeKey<>(WebSocketServerHandshaker.class.getName());
    private final String websocketPath;
    private final String subprotocols;
    private final boolean allowExtensions;

    public WebSocketServerProtocolHandler(String s) {
        this(s, (String) null, false);
    }

    public WebSocketServerProtocolHandler(String s, String s1) {
        this(s, s1, false);
    }

    public WebSocketServerProtocolHandler(String s, String s1, boolean flag) {
        this.websocketPath = s;
        this.subprotocols = s1;
        this.allowExtensions = flag;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) {
        ChannelPipeline channelpipeline = channelhandlercontext.pipeline();

        if (channelpipeline.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
            channelhandlercontext.pipeline().addBefore(channelhandlercontext.name(), WebSocketServerProtocolHandshakeHandler.class.getName(), new WebSocketServerProtocolHandshakeHandler(this.websocketPath, this.subprotocols, this.allowExtensions));
        }

    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, WebSocketFrame websocketframe, List<Object> list) throws Exception {
        if (websocketframe instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker websocketserverhandshaker = getHandshaker(channelhandlercontext);

            websocketframe.retain();
            websocketserverhandshaker.close(channelhandlercontext.channel(), (CloseWebSocketFrame) websocketframe);
        } else {
            super.decode(channelhandlercontext, websocketframe, list);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        if (throwable instanceof WebSocketHandshakeException) {
            DefaultFullHttpResponse defaultfullhttpresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(throwable.getMessage().getBytes()));

            channelhandlercontext.channel().writeAndFlush(defaultfullhttpresponse).addListener(ChannelFutureListener.CLOSE);
        } else {
            channelhandlercontext.close();
        }

    }

    static WebSocketServerHandshaker getHandshaker(ChannelHandlerContext channelhandlercontext) {
        return (WebSocketServerHandshaker) channelhandlercontext.attr(WebSocketServerProtocolHandler.HANDSHAKER_ATTR_KEY).get();
    }

    static void setHandshaker(ChannelHandlerContext channelhandlercontext, WebSocketServerHandshaker websocketserverhandshaker) {
        channelhandlercontext.attr(WebSocketServerProtocolHandler.HANDSHAKER_ATTR_KEY).set(websocketserverhandshaker);
    }

    static ChannelHandler forbiddenHttpRequestResponder() {
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
                if (object instanceof FullHttpRequest) {
                    DefaultFullHttpResponse defaultfullhttpresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);

                    channelhandlercontext.channel().writeAndFlush(defaultfullhttpresponse);
                } else {
                    channelhandlercontext.fireChannelRead(object);
                }

            }
        };
    }

    public static enum ServerHandshakeStateEvent {

        HANDSHAKE_COMPLETE;

        private ServerHandshakeStateEvent() {}
    }
}
