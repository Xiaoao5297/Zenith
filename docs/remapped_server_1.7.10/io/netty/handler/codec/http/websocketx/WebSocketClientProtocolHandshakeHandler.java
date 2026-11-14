package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;

class WebSocketClientProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {

    private final WebSocketClientHandshaker handshaker;

    public WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker websocketclienthandshaker) {
        this.handshaker = websocketclienthandshaker;
    }

    @Override
    public void channelActive(final ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.handshaker.handshake(channelhandlercontext.channel()).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelfuture) throws Exception {
                if (!channelfuture.isSuccess()) {
                    channelhandlercontext.fireExceptionCaught(channelfuture.cause());
                } else {
                    channelhandlercontext.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED);
                }

            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        if (!(object instanceof FullHttpResponse)) {
            channelhandlercontext.fireChannelRead(object);
        } else if (!this.handshaker.isHandshakeComplete()) {
            this.handshaker.finishHandshake(channelhandlercontext.channel(), (FullHttpResponse) object);
            channelhandlercontext.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE);
            channelhandlercontext.pipeline().remove((ChannelHandler) this);
        } else {
            throw new IllegalStateException("WebSocketClientHandshaker should have been non finished yet");
        }
    }
}
