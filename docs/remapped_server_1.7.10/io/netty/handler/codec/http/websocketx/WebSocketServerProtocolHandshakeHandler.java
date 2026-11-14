package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;

class WebSocketServerProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {

    private final String websocketPath;
    private final String subprotocols;
    private final boolean allowExtensions;

    public WebSocketServerProtocolHandshakeHandler(String s, String s1, boolean flag) {
        this.websocketPath = s;
        this.subprotocols = s1;
        this.allowExtensions = flag;
    }

    @Override
    public void channelRead(final ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        FullHttpRequest fullhttprequest = (FullHttpRequest) object;

        if (fullhttprequest.getMethod() != HttpMethod.GET) {
            sendHttpResponse(channelhandlercontext, fullhttprequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
        } else {
            WebSocketServerHandshakerFactory websocketserverhandshakerfactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(channelhandlercontext.pipeline(), fullhttprequest, this.websocketPath), this.subprotocols, this.allowExtensions);
            WebSocketServerHandshaker websocketserverhandshaker = websocketserverhandshakerfactory.newHandshaker(fullhttprequest);

            if (websocketserverhandshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(channelhandlercontext.channel());
            } else {
                ChannelFuture channelfuture = websocketserverhandshaker.handshake(channelhandlercontext.channel(), fullhttprequest);

                channelfuture.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture channelfuture1) throws Exception {
                        if (!channelfuture1.isSuccess()) {
                            channelhandlercontext.fireExceptionCaught(channelfuture1.cause());
                        } else {
                            channelhandlercontext.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                        }

                    }
                });
                WebSocketServerProtocolHandler.setHandshaker(channelhandlercontext, websocketserverhandshaker);
                channelhandlercontext.pipeline().replace((ChannelHandler) this, "WS403Responder", WebSocketServerProtocolHandler.forbiddenHttpRequestResponder());
            }

        }
    }

    private static void sendHttpResponse(ChannelHandlerContext channelhandlercontext, HttpRequest httprequest, HttpResponse httpresponse) {
        ChannelFuture channelfuture = channelhandlercontext.channel().writeAndFlush(httpresponse);

        if (!HttpHeaders.isKeepAlive(httprequest) || httpresponse.getStatus().code() != 200) {
            channelfuture.addListener(ChannelFutureListener.CLOSE);
        }

    }

    private static String getWebSocketLocation(ChannelPipeline channelpipeline, HttpRequest httprequest, String s) {
        String s1 = "ws";

        if (channelpipeline.get(SslHandler.class) != null) {
            s1 = "wss";
        }

        return s1 + "://" + httprequest.headers().get("Host") + s;
    }
}
