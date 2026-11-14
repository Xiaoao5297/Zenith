package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;
import java.util.List;

public class WebSocketClientProtocolHandler extends WebSocketProtocolHandler {

    private final WebSocketClientHandshaker handshaker;
    private final boolean handleCloseFrames;

    public WebSocketClientProtocolHandler(URI uri, WebSocketVersion websocketversion, String s, boolean flag, HttpHeaders httpheaders, int i, boolean flag1) {
        this(WebSocketClientHandshakerFactory.newHandshaker(uri, websocketversion, s, flag, httpheaders, i), flag1);
    }

    public WebSocketClientProtocolHandler(URI uri, WebSocketVersion websocketversion, String s, boolean flag, HttpHeaders httpheaders, int i) {
        this(uri, websocketversion, s, flag, httpheaders, i, true);
    }

    public WebSocketClientProtocolHandler(WebSocketClientHandshaker websocketclienthandshaker, boolean flag) {
        this.handshaker = websocketclienthandshaker;
        this.handleCloseFrames = flag;
    }

    public WebSocketClientProtocolHandler(WebSocketClientHandshaker websocketclienthandshaker) {
        this(websocketclienthandshaker, true);
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, WebSocketFrame websocketframe, List<Object> list) throws Exception {
        if (this.handleCloseFrames && websocketframe instanceof CloseWebSocketFrame) {
            channelhandlercontext.close();
        } else {
            super.decode(channelhandlercontext, websocketframe, list);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) {
        ChannelPipeline channelpipeline = channelhandlercontext.pipeline();

        if (channelpipeline.get(WebSocketClientProtocolHandshakeHandler.class) == null) {
            channelhandlercontext.pipeline().addBefore(channelhandlercontext.name(), WebSocketClientProtocolHandshakeHandler.class.getName(), new WebSocketClientProtocolHandshakeHandler(this.handshaker));
        }

    }

    public static enum ClientHandshakeStateEvent {

        HANDSHAKE_ISSUED, HANDSHAKE_COMPLETE;

        private ClientHandshakeStateEvent() {}
    }
}
