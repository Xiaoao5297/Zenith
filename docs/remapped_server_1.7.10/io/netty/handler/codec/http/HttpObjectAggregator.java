package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public class HttpObjectAggregator extends MessageToMessageDecoder<HttpObject> {

    public static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
    private static final ByteBuf CONTINUE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer((CharSequence) "HTTP/1.1 100 Continue\r\n\r\n", CharsetUtil.US_ASCII));
    private final int maxContentLength;
    private FullHttpMessage currentMessage;
    private boolean tooLongFrameFound;
    private int maxCumulationBufferComponents = 1024;
    private ChannelHandlerContext ctx;

    public HttpObjectAggregator(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxContentLength must be a positive integer: " + i);
        } else {
            this.maxContentLength = i;
        }
    }

    public final int getMaxCumulationBufferComponents() {
        return this.maxCumulationBufferComponents;
    }

    public final void setMaxCumulationBufferComponents(int i) {
        if (i < 2) {
            throw new IllegalArgumentException("maxCumulationBufferComponents: " + i + " (expected: >= 2)");
        } else if (this.ctx == null) {
            this.maxCumulationBufferComponents = i;
        } else {
            throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
        }
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, HttpObject httpobject, List<Object> list) throws Exception {
        FullHttpMessage fullhttpmessage = this.currentMessage;

        if (httpobject instanceof HttpMessage) {
            this.tooLongFrameFound = false;

            assert fullhttpmessage == null;

            HttpMessage httpmessage = (HttpMessage) httpobject;

            if (HttpHeaders.is100ContinueExpected(httpmessage)) {
                channelhandlercontext.writeAndFlush(HttpObjectAggregator.CONTINUE.duplicate());
            }

            if (!httpmessage.getDecoderResult().isSuccess()) {
                HttpHeaders.removeTransferEncodingChunked(httpmessage);
                this.currentMessage = null;
                list.add(ReferenceCountUtil.retain(httpmessage));
                return;
            }

            Object object;

            if (httpobject instanceof HttpRequest) {
                HttpRequest httprequest = (HttpRequest) httpobject;

                this.currentMessage = (FullHttpMessage) (object = new DefaultFullHttpRequest(httprequest.getProtocolVersion(), httprequest.getMethod(), httprequest.getUri(), Unpooled.compositeBuffer(this.maxCumulationBufferComponents)));
            } else {
                if (!(httpobject instanceof HttpResponse)) {
                    throw new Error();
                }

                HttpResponse httpresponse = (HttpResponse) httpobject;

                this.currentMessage = (FullHttpMessage) (object = new DefaultFullHttpResponse(httpresponse.getProtocolVersion(), httpresponse.getStatus(), Unpooled.compositeBuffer(this.maxCumulationBufferComponents)));
            }

            ((FullHttpMessage) object).headers().set(httpmessage.headers());
            HttpHeaders.removeTransferEncodingChunked((HttpMessage) object);
        } else {
            if (!(httpobject instanceof HttpContent)) {
                throw new Error();
            }

            if (this.tooLongFrameFound) {
                if (httpobject instanceof LastHttpContent) {
                    this.currentMessage = null;
                }

                return;
            }

            assert fullhttpmessage != null;

            HttpContent httpcontent = (HttpContent) httpobject;
            CompositeByteBuf compositebytebuf = (CompositeByteBuf) fullhttpmessage.content();

            if (compositebytebuf.readableBytes() > this.maxContentLength - httpcontent.content().readableBytes()) {
                this.tooLongFrameFound = true;
                fullhttpmessage.release();
                this.currentMessage = null;
                throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
            }

            if (httpcontent.content().isReadable()) {
                httpcontent.retain();
                compositebytebuf.addComponent(httpcontent.content());
                compositebytebuf.writerIndex(compositebytebuf.writerIndex() + httpcontent.content().readableBytes());
            }

            boolean flag;

            if (!httpcontent.getDecoderResult().isSuccess()) {
                fullhttpmessage.setDecoderResult(DecoderResult.failure(httpcontent.getDecoderResult().cause()));
                flag = true;
            } else {
                flag = httpcontent instanceof LastHttpContent;
            }

            if (flag) {
                this.currentMessage = null;
                if (httpcontent instanceof LastHttpContent) {
                    LastHttpContent lasthttpcontent = (LastHttpContent) httpcontent;

                    fullhttpmessage.headers().add(lasthttpcontent.trailingHeaders());
                }

                fullhttpmessage.headers().set("Content-Length", (Object) String.valueOf(compositebytebuf.readableBytes()));
                list.add(fullhttpmessage);
            }
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelInactive(channelhandlercontext);
        if (this.currentMessage != null) {
            this.currentMessage.release();
            this.currentMessage = null;
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.ctx = channelhandlercontext;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.handlerRemoved(channelhandlercontext);
        if (this.currentMessage != null) {
            this.currentMessage.release();
            this.currentMessage = null;
        }

    }
}
