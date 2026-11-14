package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

abstract class WebSocketProtocolHandler extends MessageToMessageDecoder<WebSocketFrame> {

    WebSocketProtocolHandler() {}

    protected void decode(ChannelHandlerContext channelhandlercontext, WebSocketFrame websocketframe, List<Object> list) throws Exception {
        if (websocketframe instanceof PingWebSocketFrame) {
            websocketframe.content().retain();
            channelhandlercontext.channel().writeAndFlush(new PongWebSocketFrame(websocketframe.content()));
        } else if (!(websocketframe instanceof PongWebSocketFrame)) {
            list.add(websocketframe.retain());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        channelhandlercontext.close();
    }
}
