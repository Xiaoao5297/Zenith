package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import java.util.List;
import javax.net.ssl.SSLEngine;

public abstract class SpdyOrHttpChooser extends ByteToMessageDecoder {

    private final int maxSpdyContentLength;
    private final int maxHttpContentLength;

    protected SpdyOrHttpChooser(int i, int j) {
        this.maxSpdyContentLength = i;
        this.maxHttpContentLength = j;
    }

    protected abstract SpdyOrHttpChooser.SelectedProtocol getProtocol(SSLEngine sslengine);

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (this.initPipeline(channelhandlercontext)) {
            channelhandlercontext.pipeline().remove((ChannelHandler) this);
        }

    }

    private boolean initPipeline(ChannelHandlerContext channelhandlercontext) {
        SslHandler sslhandler = (SslHandler) channelhandlercontext.pipeline().get(SslHandler.class);

        if (sslhandler == null) {
            throw new IllegalStateException("SslHandler is needed for SPDY");
        } else {
            SpdyOrHttpChooser.SelectedProtocol spdyorhttpchooser_selectedprotocol = this.getProtocol(sslhandler.engine());

            switch (spdyorhttpchooser_selectedprotocol) {
                case UNKNOWN:
                    return false;
                case SPDY_2:
                    this.addSpdyHandlers(channelhandlercontext, 2);
                    break;
                case SPDY_3:
                    this.addSpdyHandlers(channelhandlercontext, 3);
                    break;
                case HTTP_1_0:
                case HTTP_1_1:
                    this.addHttpHandlers(channelhandlercontext);
                    break;
                default:
                    throw new IllegalStateException("Unknown SelectedProtocol");
            }

            return true;
        }
    }

    protected void addSpdyHandlers(ChannelHandlerContext channelhandlercontext, int i) {
        ChannelPipeline channelpipeline = channelhandlercontext.pipeline();

        channelpipeline.addLast("spdyDecoder", (ChannelHandler) (new SpdyFrameDecoder(i)));
        channelpipeline.addLast("spdyEncoder", (ChannelHandler) (new SpdyFrameEncoder(i)));
        channelpipeline.addLast("spdySessionHandler", (ChannelHandler) (new SpdySessionHandler(i, true)));
        channelpipeline.addLast("spdyHttpEncoder", (ChannelHandler) (new SpdyHttpEncoder(i)));
        channelpipeline.addLast("spdyHttpDecoder", (ChannelHandler) (new SpdyHttpDecoder(i, this.maxSpdyContentLength)));
        channelpipeline.addLast("spdyStreamIdHandler", (ChannelHandler) (new SpdyHttpResponseStreamIdHandler()));
        channelpipeline.addLast("httpRquestHandler", (ChannelHandler) this.createHttpRequestHandlerForSpdy());
    }

    protected void addHttpHandlers(ChannelHandlerContext channelhandlercontext) {
        ChannelPipeline channelpipeline = channelhandlercontext.pipeline();

        channelpipeline.addLast("httpRquestDecoder", (ChannelHandler) (new HttpRequestDecoder()));
        channelpipeline.addLast("httpResponseEncoder", (ChannelHandler) (new HttpResponseEncoder()));
        channelpipeline.addLast("httpChunkAggregator", (ChannelHandler) (new HttpObjectAggregator(this.maxHttpContentLength)));
        channelpipeline.addLast("httpRquestHandler", (ChannelHandler) this.createHttpRequestHandlerForHttp());
    }

    protected abstract ChannelInboundHandler createHttpRequestHandlerForHttp();

    protected ChannelInboundHandler createHttpRequestHandlerForSpdy() {
        return this.createHttpRequestHandlerForHttp();
    }

    public static enum SelectedProtocol {

        SPDY_2, SPDY_3, HTTP_1_1, HTTP_1_0, UNKNOWN;

        private SelectedProtocol() {}
    }
}
