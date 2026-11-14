package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class WebSocketFrameAggregator extends MessageToMessageDecoder<WebSocketFrame> {

    private final int maxFrameSize;
    private WebSocketFrame currentFrame;
    private boolean tooLongFrameFound;

    public WebSocketFrameAggregator(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("maxFrameSize must be > 0");
        } else {
            this.maxFrameSize = i;
        }
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, WebSocketFrame websocketframe, List<Object> list) throws Exception {
        CompositeByteBuf compositebytebuf;

        if (this.currentFrame == null) {
            this.tooLongFrameFound = false;
            if (websocketframe.isFinalFragment()) {
                list.add(websocketframe.retain());
            } else {
                compositebytebuf = channelhandlercontext.alloc().compositeBuffer().addComponent(websocketframe.content().retain());
                compositebytebuf.writerIndex(compositebytebuf.writerIndex() + websocketframe.content().readableBytes());
                if (websocketframe instanceof TextWebSocketFrame) {
                    this.currentFrame = new TextWebSocketFrame(true, websocketframe.rsv(), compositebytebuf);
                } else {
                    if (!(websocketframe instanceof BinaryWebSocketFrame)) {
                        throw new IllegalStateException("WebSocket frame was not of type TextWebSocketFrame or BinaryWebSocketFrame");
                    }

                    this.currentFrame = new BinaryWebSocketFrame(true, websocketframe.rsv(), compositebytebuf);
                }

            }
        } else if (websocketframe instanceof ContinuationWebSocketFrame) {
            if (this.tooLongFrameFound) {
                if (websocketframe.isFinalFragment()) {
                    this.currentFrame = null;
                }

            } else {
                compositebytebuf = (CompositeByteBuf) this.currentFrame.content();
                if (compositebytebuf.readableBytes() > this.maxFrameSize - websocketframe.content().readableBytes()) {
                    this.tooLongFrameFound = true;
                    throw new TooLongFrameException("WebSocketFrame length exceeded " + compositebytebuf + " bytes.");
                } else {
                    compositebytebuf.addComponent(websocketframe.content().retain());
                    compositebytebuf.writerIndex(compositebytebuf.writerIndex() + websocketframe.content().readableBytes());
                    if (websocketframe.isFinalFragment()) {
                        WebSocketFrame websocketframe1 = this.currentFrame;

                        this.currentFrame = null;
                        list.add(websocketframe1);
                    }
                }
            }
        } else {
            list.add(websocketframe.retain());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelInactive(channelhandlercontext);
        if (this.currentFrame != null) {
            this.currentFrame.release();
            this.currentFrame = null;
        }

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.handlerRemoved(channelhandlercontext);
        if (this.currentFrame != null) {
            this.currentFrame.release();
            this.currentFrame = null;
        }

    }
}
