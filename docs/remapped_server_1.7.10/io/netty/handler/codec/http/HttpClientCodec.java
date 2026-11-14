package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.PrematureChannelClosureException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public final class HttpClientCodec extends CombinedChannelDuplexHandler<HttpResponseDecoder, HttpRequestEncoder> {

    private final Queue<HttpMethod> queue;
    private boolean done;
    private final AtomicLong requestResponseCounter;
    private final boolean failOnMissingResponse;

    public HttpClientCodec() {
        this(4096, 8192, 8192, false);
    }

    public void setSingleDecode(boolean flag) {
        ((HttpResponseDecoder) this.inboundHandler()).setSingleDecode(flag);
    }

    public boolean isSingleDecode() {
        return ((HttpResponseDecoder) this.inboundHandler()).isSingleDecode();
    }

    public HttpClientCodec(int i, int j, int k) {
        this(i, j, k, false);
    }

    public HttpClientCodec(int i, int j, int k, boolean flag) {
        this.queue = new ArrayDeque();
        this.requestResponseCounter = new AtomicLong();
        this.init(new HttpClientCodec.Decoder(i, j, k), new HttpClientCodec.Encoder());
        this.failOnMissingResponse = flag;
    }

    private final class Decoder extends HttpResponseDecoder {

        Decoder(int i, int j, int k) {
            super(i, j, k);
        }

        @Override
        protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
            int i;

            if (HttpClientCodec.this.done) {
                i = this.actualReadableBytes();
                if (i == 0) {
                    return;
                }

                list.add(bytebuf.readBytes(i));
            } else {
                i = list.size();
                super.decode(channelhandlercontext, bytebuf, list);
                if (HttpClientCodec.this.failOnMissingResponse) {
                    int j = list.size();

                    for (int k = i; k < j; ++k) {
                        this.decrement(list.get(k));
                    }
                }
            }

        }

        private void decrement(Object object) {
            if (object != null) {
                if (object instanceof LastHttpContent) {
                    HttpClientCodec.this.requestResponseCounter.decrementAndGet();
                }

            }
        }

        @Override
        protected boolean isContentAlwaysEmpty(HttpMessage httpmessage) {
            int i = ((HttpResponse) httpmessage).getStatus().code();

            if (i == 100) {
                return true;
            } else {
                HttpMethod httpmethod = (HttpMethod) HttpClientCodec.this.queue.poll();
                char c0 = httpmethod.name().charAt(0);

                switch (c0) {
                    case 'C':
                        if (i == 200 && HttpMethod.CONNECT.equals(httpmethod)) {
                            HttpClientCodec.this.done = true;
                            HttpClientCodec.this.queue.clear();
                            return true;
                        }
                        break;
                    case 'H':
                        if (HttpMethod.HEAD.equals(httpmethod)) {
                            return true;
                        }
                }

                return super.isContentAlwaysEmpty(httpmessage);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
            super.channelInactive(channelhandlercontext);
            if (HttpClientCodec.this.failOnMissingResponse) {
                long i = HttpClientCodec.this.requestResponseCounter.get();

                if (i > 0L) {
                    channelhandlercontext.fireExceptionCaught(new PrematureChannelClosureException("channel gone inactive with " + i + " missing response(s)"));
                }
            }

        }
    }

    private final class Encoder extends HttpRequestEncoder {

        private Encoder() {}

        @Override
        protected void encode(ChannelHandlerContext channelhandlercontext, Object object, List<Object> list) throws Exception {
            if (object instanceof HttpRequest && !HttpClientCodec.this.done) {
                HttpClientCodec.this.queue.offer(((HttpRequest) object).getMethod());
            }

            super.encode(channelhandlercontext, object, list);
            if (HttpClientCodec.this.failOnMissingResponse && object instanceof LastHttpContent) {
                HttpClientCodec.this.requestResponseCounter.incrementAndGet();
            }

        }
    }
}
