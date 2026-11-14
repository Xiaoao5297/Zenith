package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.util.ReferenceCountUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpdyHttpResponseStreamIdHandler extends MessageToMessageCodec<Object, HttpMessage> {

    private static final Integer NO_ID = -1;
    private final Queue<Integer> ids = new LinkedList();

    public SpdyHttpResponseStreamIdHandler() {}

    @Override
    public boolean acceptInboundMessage(Object object) throws Exception {
        return object instanceof HttpMessage || object instanceof SpdyRstStreamFrame;
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, HttpMessage httpmessage, List<Object> list) throws Exception {
        Integer integer = (Integer) this.ids.poll();

        if (integer != null && integer != SpdyHttpResponseStreamIdHandler.NO_ID && !httpmessage.headers().contains("X-SPDY-Stream-ID")) {
            SpdyHttpHeaders.setStreamId(httpmessage, integer);
        }

        list.add(ReferenceCountUtil.retain(httpmessage));
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, Object object, List<Object> list) throws Exception {
        if (object instanceof HttpMessage) {
            boolean flag = ((HttpMessage) object).headers().contains("X-SPDY-Stream-ID");

            if (!flag) {
                this.ids.add(SpdyHttpResponseStreamIdHandler.NO_ID);
            } else {
                this.ids.add(SpdyHttpHeaders.getStreamId((HttpMessage) object));
            }
        } else if (object instanceof SpdyRstStreamFrame) {
            this.ids.remove(((SpdyRstStreamFrame) object).getStreamId());
        }

        list.add(ReferenceCountUtil.retain(object));
    }
}
