package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class WebSocketServerHandshaker {

    protected static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker.class);
    private final String uri;
    private final String[] subprotocols;
    private final WebSocketVersion version;
    private final int maxFramePayloadLength;
    private String selectedSubprotocol;
    public static final String SUB_PROTOCOL_WILDCARD = "*";

    protected WebSocketServerHandshaker(WebSocketVersion websocketversion, String s, String s1, int i) {
        this.version = websocketversion;
        this.uri = s;
        if (s1 != null) {
            String[] astring = StringUtil.split(s1, ',');

            for (int j = 0; j < astring.length; ++j) {
                astring[j] = astring[j].trim();
            }

            this.subprotocols = astring;
        } else {
            this.subprotocols = EmptyArrays.EMPTY_STRINGS;
        }

        this.maxFramePayloadLength = i;
    }

    public String uri() {
        return this.uri;
    }

    public Set<String> subprotocols() {
        LinkedHashSet linkedhashset = new LinkedHashSet();

        Collections.addAll(linkedhashset, this.subprotocols);
        return linkedhashset;
    }

    public WebSocketVersion version() {
        return this.version;
    }

    public int maxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }

    public ChannelFuture handshake(Channel channel, FullHttpRequest fullhttprequest) {
        return this.handshake(channel, fullhttprequest, (HttpHeaders) null, channel.newPromise());
    }

    public final ChannelFuture handshake(Channel channel, FullHttpRequest fullhttprequest, HttpHeaders httpheaders, final ChannelPromise channelpromise) {
        if (WebSocketServerHandshaker.logger.isDebugEnabled()) {
            WebSocketServerHandshaker.logger.debug(String.format("%s WS Version %s server handshake", channel, this.version()));
        }

        FullHttpResponse fullhttpresponse = this.newHandshakeResponse(fullhttprequest, httpheaders);

        channel.writeAndFlush(fullhttpresponse).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelfuture) throws Exception {
                if (channelfuture.isSuccess()) {
                    ChannelPipeline channelpipeline = channelfuture.channel().pipeline();

                    if (channelpipeline.get(HttpObjectAggregator.class) != null) {
                        channelpipeline.remove(HttpObjectAggregator.class);
                    }

                    if (channelpipeline.get(HttpContentCompressor.class) != null) {
                        channelpipeline.remove(HttpContentCompressor.class);
                    }

                    ChannelHandlerContext channelhandlercontext = channelpipeline.context(HttpRequestDecoder.class);

                    if (channelhandlercontext == null) {
                        channelhandlercontext = channelpipeline.context(HttpServerCodec.class);
                        if (channelhandlercontext == null) {
                            channelpromise.setFailure(new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
                            return;
                        }

                        channelpipeline.addBefore(channelhandlercontext.name(), "wsdecoder", WebSocketServerHandshaker.this.newWebsocketDecoder());
                        channelpipeline.replace(channelhandlercontext.name(), "wsencoder", WebSocketServerHandshaker.this.newWebSocketEncoder());
                    } else {
                        channelpipeline.replace(channelhandlercontext.name(), "wsdecoder", WebSocketServerHandshaker.this.newWebsocketDecoder());
                        channelpipeline.replace(HttpResponseEncoder.class, "wsencoder", WebSocketServerHandshaker.this.newWebSocketEncoder());
                    }

                    channelpromise.setSuccess();
                } else {
                    channelpromise.setFailure(channelfuture.cause());
                }

            }
        });
        return channelpromise;
    }

    protected abstract FullHttpResponse newHandshakeResponse(FullHttpRequest fullhttprequest, HttpHeaders httpheaders);

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
            return channel.writeAndFlush(closewebsocketframe, channelpromise).addListener(ChannelFutureListener.CLOSE);
        }
    }

    protected String selectSubprotocol(String s) {
        if (s != null && this.subprotocols.length != 0) {
            String[] astring = StringUtil.split(s, ',');
            String[] astring1 = astring;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String s1 = astring1[j];
                String s2 = s1.trim();
                String[] astring2 = this.subprotocols;
                int k = astring2.length;

                for (int l = 0; l < k; ++l) {
                    String s3 = astring2[l];

                    if ("*".equals(s3) || s2.equals(s3)) {
                        this.selectedSubprotocol = s2;
                        return s2;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public String selectedSubprotocol() {
        return this.selectedSubprotocol;
    }

    protected abstract WebSocketFrameDecoder newWebsocketDecoder();

    protected abstract WebSocketFrameEncoder newWebSocketEncoder();
}
