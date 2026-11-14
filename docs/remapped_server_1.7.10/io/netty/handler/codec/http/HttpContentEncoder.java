package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class HttpContentEncoder extends MessageToMessageCodec<HttpRequest, HttpObject> {

    private final Queue<String> acceptEncodingQueue = new ArrayDeque();
    private String acceptEncoding;
    private EmbeddedChannel encoder;
    private HttpContentEncoder.State state;

    public HttpContentEncoder() {
        this.state = HttpContentEncoder.State.AWAIT_HEADERS;
    }

    @Override
    public boolean acceptOutboundMessage(Object object) throws Exception {
        return object instanceof HttpContent || object instanceof HttpResponse;
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, HttpRequest httprequest, List<Object> list) throws Exception {
        String s = httprequest.headers().get("Accept-Encoding");

        if (s == null) {
            s = "identity";
        }

        this.acceptEncodingQueue.add(s);
        list.add(ReferenceCountUtil.retain(httprequest));
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, HttpObject httpobject, List<Object> list) throws Exception {
        boolean flag = httpobject instanceof HttpResponse && httpobject instanceof LastHttpContent;

        switch (this.state) {
            case AWAIT_HEADERS:
                ensureHeaders(httpobject);

                assert this.encoder == null;

                HttpResponse httpresponse = (HttpResponse) httpobject;

                if (httpresponse.getStatus().code() == 100) {
                    if (flag) {
                        list.add(ReferenceCountUtil.retain(httpresponse));
                    } else {
                        list.add(httpresponse);
                        this.state = HttpContentEncoder.State.PASS_THROUGH;
                    }
                    break;
                } else {
                    this.acceptEncoding = (String) this.acceptEncodingQueue.poll();
                    if (this.acceptEncoding == null) {
                        throw new IllegalStateException("cannot send more responses than requests");
                    }

                    if (flag && !((ByteBufHolder) httpresponse).content().isReadable()) {
                        list.add(ReferenceCountUtil.retain(httpresponse));
                        break;
                    } else {
                        HttpContentEncoder.Result httpcontentencoder_result = this.beginEncode(httpresponse, this.acceptEncoding);

                        if (httpcontentencoder_result == null) {
                            if (flag) {
                                list.add(ReferenceCountUtil.retain(httpresponse));
                            } else {
                                list.add(httpresponse);
                                this.state = HttpContentEncoder.State.PASS_THROUGH;
                            }
                            break;
                        } else {
                            this.encoder = httpcontentencoder_result.contentEncoder();
                            httpresponse.headers().set("Content-Encoding", (Object) httpcontentencoder_result.targetContentEncoding());
                            httpresponse.headers().remove("Content-Length");
                            httpresponse.headers().set("Transfer-Encoding", (Object) "chunked");
                            if (!flag) {
                                list.add(httpresponse);
                                this.state = HttpContentEncoder.State.AWAIT_CONTENT;
                                break;
                            } else {
                                DefaultHttpResponse defaulthttpresponse = new DefaultHttpResponse(httpresponse.getProtocolVersion(), httpresponse.getStatus());

                                defaulthttpresponse.headers().set(httpresponse.headers());
                                list.add(defaulthttpresponse);
                            }
                        }
                    }
                }
            case AWAIT_CONTENT:
                ensureContent(httpobject);
                if (this.encodeContent((HttpContent) httpobject, list)) {
                    this.state = HttpContentEncoder.State.AWAIT_HEADERS;
                }
                break;
            case PASS_THROUGH:
                ensureContent(httpobject);
                list.add(ReferenceCountUtil.retain(httpobject));
                if (httpobject instanceof LastHttpContent) {
                    this.state = HttpContentEncoder.State.AWAIT_HEADERS;
                }
        }

    }

    private static void ensureHeaders(HttpObject httpobject) {
        if (!(httpobject instanceof HttpResponse)) {
            throw new IllegalStateException("unexpected message type: " + httpobject.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
        }
    }

    private static void ensureContent(HttpObject httpobject) {
        if (!(httpobject instanceof HttpContent)) {
            throw new IllegalStateException("unexpected message type: " + httpobject.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
        }
    }

    private boolean encodeContent(HttpContent httpcontent, List<Object> list) {
        ByteBuf bytebuf = httpcontent.content();

        this.encode(bytebuf, list);
        if (httpcontent instanceof LastHttpContent) {
            this.finishEncode(list);
            LastHttpContent lasthttpcontent = (LastHttpContent) httpcontent;
            HttpHeaders httpheaders = lasthttpcontent.trailingHeaders();

            if (httpheaders.isEmpty()) {
                list.add(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                list.add(new ComposedLastHttpContent(httpheaders));
            }

            return true;
        } else {
            return false;
        }
    }

    protected abstract HttpContentEncoder.Result beginEncode(HttpResponse httpresponse, String s) throws Exception;

    @Override
    public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.cleanup();
        super.handlerRemoved(channelhandlercontext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.cleanup();
        super.channelInactive(channelhandlercontext);
    }

    private void cleanup() {
        if (this.encoder != null) {
            if (this.encoder.finish()) {
                while (true) {
                    ByteBuf bytebuf = (ByteBuf) this.encoder.readOutbound();

                    if (bytebuf == null) {
                        break;
                    }

                    bytebuf.release();
                }
            }

            this.encoder = null;
        }

    }

    private void encode(ByteBuf bytebuf, List<Object> list) {
        this.encoder.writeOutbound(bytebuf.retain());
        this.fetchEncoderOutput(list);
    }

    private void finishEncode(List<Object> list) {
        if (this.encoder.finish()) {
            this.fetchEncoderOutput(list);
        }

        this.encoder = null;
    }

    private void fetchEncoderOutput(List<Object> list) {
        while (true) {
            ByteBuf bytebuf = (ByteBuf) this.encoder.readOutbound();

            if (bytebuf == null) {
                return;
            }

            if (!bytebuf.isReadable()) {
                bytebuf.release();
            } else {
                list.add(new DefaultHttpContent(bytebuf));
            }
        }
    }

    public static final class Result {

        private final String targetContentEncoding;
        private final EmbeddedChannel contentEncoder;

        public Result(String s, EmbeddedChannel embeddedchannel) {
            if (s == null) {
                throw new NullPointerException("targetContentEncoding");
            } else if (embeddedchannel == null) {
                throw new NullPointerException("contentEncoder");
            } else {
                this.targetContentEncoding = s;
                this.contentEncoder = embeddedchannel;
            }
        }

        public String targetContentEncoding() {
            return this.targetContentEncoding;
        }

        public EmbeddedChannel contentEncoder() {
            return this.contentEncoder;
        }
    }

    private static enum State {

        PASS_THROUGH, AWAIT_HEADERS, AWAIT_CONTENT;

        private State() {}
    }
}
