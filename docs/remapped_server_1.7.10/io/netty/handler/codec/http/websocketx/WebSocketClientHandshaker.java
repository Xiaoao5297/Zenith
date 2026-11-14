package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import java.net.URI;

public abstract class WebSocketClientHandshaker {

    private final URI uri;
    private final WebSocketVersion version;
    private boolean handshakeComplete;
    private final String expectedSubprotocol;
    private String actualSubprotocol;
    protected final HttpHeaders customHeaders;
    private final int maxFramePayloadLength;

    protected WebSocketClientHandshaker(URI uri, WebSocketVersion websocketversion, String s, HttpHeaders httpheaders, int i) {
        this.uri = uri;
        this.version = websocketversion;
        this.expectedSubprotocol = s;
        this.customHeaders = httpheaders;
        this.maxFramePayloadLength = i;
    }

    public URI uri() {
        return this.uri;
    }

    public WebSocketVersion version() {
        return this.version;
    }

    public int maxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }

    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }

    private void setHandshakeComplete() {
        this.handshakeComplete = true;
    }

    public String expectedSubprotocol() {
        return this.expectedSubprotocol;
    }

    public String actualSubprotocol() {
        return this.actualSubprotocol;
    }

    private void setActualSubprotocol(String s) {
        this.actualSubprotocol = s;
    }

    public ChannelFuture handshake(Channel channel) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else {
            return this.handshake(channel, channel.newPromise());
        }
    }

    public final ChannelFuture handshake(Channel channel, final ChannelPromise channelpromise) {
        FullHttpRequest fullhttprequest = this.newHandshakeRequest();
        HttpResponseDecoder httpresponsedecoder = (HttpResponseDecoder) channel.pipeline().get(HttpResponseDecoder.class);

        if (httpresponsedecoder == null) {
            HttpClientCodec httpclientcodec = (HttpClientCodec) channel.pipeline().get(HttpClientCodec.class);

            if (httpclientcodec == null) {
                channelpromise.setFailure(new IllegalStateException("ChannelPipeline does not contain a HttpResponseDecoder or HttpClientCodec"));
                return channelpromise;
            }

            httpclientcodec.setSingleDecode(true);
        } else {
            httpresponsedecoder.setSingleDecode(true);
        }

        channel.writeAndFlush(fullhttprequest).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelfuture) {
                if (channelfuture.isSuccess()) {
                    ChannelPipeline channelpipeline = channelfuture.channel().pipeline();
                    ChannelHandlerContext channelhandlercontext = channelpipeline.context(HttpRequestEncoder.class);

                    if (channelhandlercontext == null) {
                        channelhandlercontext = channelpipeline.context(HttpClientCodec.class);
                    }

                    if (channelhandlercontext == null) {
                        channelpromise.setFailure(new IllegalStateException("ChannelPipeline does not contain a HttpRequestEncoder or HttpClientCodec"));
                        return;
                    }

                    channelpipeline.addAfter(channelhandlercontext.name(), "ws-encoder", WebSocketClientHandshaker.this.newWebSocketEncoder());
                    channelpromise.setSuccess();
                } else {
                    channelpromise.setFailure(channelfuture.cause());
                }

            }
        });
        return channelpromise;
    }

    protected abstract FullHttpRequest newHandshakeRequest();

    public final void finishHandshake(Channel channel, FullHttpResponse fullhttpresponse) {
        this.verify(fullhttpresponse);
        this.setActualSubprotocol(fullhttpresponse.headers().get("Sec-WebSocket-Protocol"));
        this.setHandshakeComplete();
        ChannelPipeline channelpipeline = channel.pipeline();
        HttpContentDecompressor httpcontentdecompressor = (HttpContentDecompressor) channelpipeline.get(HttpContentDecompressor.class);

        if (httpcontentdecompressor != null) {
            channelpipeline.remove((ChannelHandler) httpcontentdecompressor);
        }

        ChannelHandlerContext channelhandlercontext = channelpipeline.context(HttpResponseDecoder.class);

        if (channelhandlercontext == null) {
            channelhandlercontext = channelpipeline.context(HttpClientCodec.class);
            if (channelhandlercontext == null) {
                throw new IllegalStateException("ChannelPipeline does not contain a HttpRequestEncoder or HttpClientCodec");
            }

            channelpipeline.replace(channelhandlercontext.name(), "ws-decoder", this.newWebsocketDecoder());
        } else {
            if (channelpipeline.get(HttpRequestEncoder.class) != null) {
                channelpipeline.remove(HttpRequestEncoder.class);
            }

            channelpipeline.replace(channelhandlercontext.name(), "ws-decoder", this.newWebsocketDecoder());
        }

    }

    protected abstract void verify(FullHttpResponse fullhttpresponse);

    protected abstract WebSocketFrameDecoder newWebsocketDecoder();

    protected abstract WebSocketFrameEncoder newWebSocketEncoder();

    public ChannelFuture close(Channel channel, CloseWebSocketFrame closewebsocketframe) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else {
            return this.close(channel, closewebsocketframe, channel.newPromise());
        }
    }

    public ChannelFuture close(Channel channel, CloseWebSocketFrame closewebsocketframe, ChannelPromise channelpromise) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else {
            return channel.writeAndFlush(closewebsocketframe, channelpromise);
        }
    }
}
