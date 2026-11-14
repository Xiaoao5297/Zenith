package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public abstract class HttpContentDecoder extends MessageToMessageDecoder<HttpObject> {

    private EmbeddedChannel decoder;
    private HttpMessage message;
    private boolean decodeStarted;
    private boolean continueResponse;

    public HttpContentDecoder() {}

    protected void decode(ChannelHandlerContext channelhandlercontext, HttpObject httpobject, List<Object> list) throws Exception {
        if (httpobject instanceof HttpResponse && ((HttpResponse) httpobject).getStatus().code() == 100) {
            if (!(httpobject instanceof LastHttpContent)) {
                this.continueResponse = true;
            }

            list.add(ReferenceCountUtil.retain(httpobject));
        } else if (this.continueResponse) {
            if (httpobject instanceof LastHttpContent) {
                this.continueResponse = false;
            }

            list.add(ReferenceCountUtil.retain(httpobject));
        } else {
            if (httpobject instanceof HttpMessage) {
                assert this.message == null;

                this.message = (HttpMessage) httpobject;
                this.decodeStarted = false;
                this.cleanup();
            }

            if (httpobject instanceof HttpContent) {
                HttpContent httpcontent = (HttpContent) httpobject;

                if (!this.decodeStarted) {
                    this.decodeStarted = true;
                    HttpMessage httpmessage = this.message;
                    HttpHeaders httpheaders = httpmessage.headers();

                    this.message = null;
                    String s = httpheaders.get("Content-Encoding");

                    if (s != null) {
                        s = s.trim();
                    } else {
                        s = "identity";
                    }

                    if ((this.decoder = this.newContentDecoder(s)) != null) {
                        String s1 = this.getTargetContentEncoding(s);

                        if ("identity".equals(s1)) {
                            httpheaders.remove("Content-Encoding");
                        } else {
                            httpheaders.set("Content-Encoding", (Object) s1);
                        }

                        list.add(httpmessage);
                        this.decodeContent(httpcontent, list);
                        if (httpheaders.contains("Content-Length")) {
                            int i = 0;
                            int j = list.size();

                            for (int k = 0; k < j; ++k) {
                                Object object = list.get(k);

                                if (object instanceof HttpContent) {
                                    i += ((HttpContent) object).content().readableBytes();
                                }
                            }

                            httpheaders.set("Content-Length", (Object) Integer.toString(i));
                        }

                        return;
                    }

                    if (httpcontent instanceof LastHttpContent) {
                        this.decodeStarted = false;
                    }

                    list.add(httpmessage);
                    list.add(httpcontent.retain());
                    return;
                }

                if (this.decoder != null) {
                    this.decodeContent(httpcontent, list);
                } else {
                    if (httpcontent instanceof LastHttpContent) {
                        this.decodeStarted = false;
                    }

                    list.add(httpcontent.retain());
                }
            }

        }
    }

    private void decodeContent(HttpContent httpcontent, List<Object> list) {
        ByteBuf bytebuf = httpcontent.content();

        this.decode(bytebuf, list);
        if (httpcontent instanceof LastHttpContent) {
            this.finishDecode(list);
            LastHttpContent lasthttpcontent = (LastHttpContent) httpcontent;
            HttpHeaders httpheaders = lasthttpcontent.trailingHeaders();

            if (httpheaders.isEmpty()) {
                list.add(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                list.add(new ComposedLastHttpContent(httpheaders));
            }
        }

    }

    protected abstract EmbeddedChannel newContentDecoder(String s) throws Exception;

    protected String getTargetContentEncoding(String s) throws Exception {
        return "identity";
    }

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
        if (this.decoder != null) {
            if (this.decoder.finish()) {
                while (true) {
                    ByteBuf bytebuf = (ByteBuf) this.decoder.readOutbound();

                    if (bytebuf == null) {
                        break;
                    }

                    bytebuf.release();
                }
            }

            this.decoder = null;
        }

    }

    private void decode(ByteBuf bytebuf, List<Object> list) {
        this.decoder.writeInbound(bytebuf.retain());
        this.fetchDecoderOutput(list);
    }

    private void finishDecode(List<Object> list) {
        if (this.decoder.finish()) {
            this.fetchDecoderOutput(list);
        }

        this.decodeStarted = false;
        this.decoder = null;
    }

    private void fetchDecoderOutput(List<Object> list) {
        while (true) {
            ByteBuf bytebuf = (ByteBuf) this.decoder.readInbound();

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
}
