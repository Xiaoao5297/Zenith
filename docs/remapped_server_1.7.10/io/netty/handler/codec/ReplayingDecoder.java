package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Signal;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ReplayingDecoder<S> extends ByteToMessageDecoder {

    static final Signal REPLAY = new Signal(ReplayingDecoder.class.getName() + ".REPLAY");
    private final ReplayingDecoderBuffer replayable;
    private S state;
    private int checkpoint;

    protected ReplayingDecoder() {
        this((Object) null);
    }

    protected ReplayingDecoder(S s0) {
        this.replayable = new ReplayingDecoderBuffer();
        this.checkpoint = -1;
        this.state = s0;
    }

    protected void checkpoint() {
        this.checkpoint = this.internalBuffer().readerIndex();
    }

    protected void checkpoint(S s0) {
        this.checkpoint();
        this.state(s0);
    }

    protected S state() {
        return this.state;
    }

    protected S state(S s0) {
        Object object = this.state;

        this.state = s0;
        return object;
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        RecyclableArrayList recyclablearraylist = RecyclableArrayList.newInstance();
        boolean flag = false;

        int i;
        int j;
        label146:
        {
            try {
                flag = true;
                this.replayable.terminate();
                this.callDecode(channelhandlercontext, this.internalBuffer(), recyclablearraylist);
                this.decodeLast(channelhandlercontext, this.replayable, recyclablearraylist);
                flag = false;
                break label146;
            } catch (Signal signal) {
                signal.expect(ReplayingDecoder.REPLAY);
                flag = false;
            } catch (DecoderException decoderexception) {
                throw decoderexception;
            } catch (Exception exception) {
                throw new DecoderException(exception);
            } finally {
                if (flag) {
                    if (this.cumulation != null) {
                        this.cumulation.release();
                        this.cumulation = null;
                    }

                    int k = recyclablearraylist.size();

                    for (int l = 0; l < k; ++l) {
                        channelhandlercontext.fireChannelRead(recyclablearraylist.get(l));
                    }

                    channelhandlercontext.fireChannelInactive();
                    recyclablearraylist.recycle();
                }
            }

            if (this.cumulation != null) {
                this.cumulation.release();
                this.cumulation = null;
            }

            i = recyclablearraylist.size();

            for (j = 0; j < i; ++j) {
                channelhandlercontext.fireChannelRead(recyclablearraylist.get(j));
            }

            channelhandlercontext.fireChannelInactive();
            recyclablearraylist.recycle();
            return;
        }

        if (this.cumulation != null) {
            this.cumulation.release();
            this.cumulation = null;
        }

        i = recyclablearraylist.size();

        for (j = 0; j < i; ++j) {
            channelhandlercontext.fireChannelRead(recyclablearraylist.get(j));
        }

        channelhandlercontext.fireChannelInactive();
        recyclablearraylist.recycle();
    }

    @Override
    protected void callDecode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) {
        this.replayable.setCumulation(bytebuf);

        try {
            while (bytebuf.isReadable()) {
                int i = this.checkpoint = bytebuf.readerIndex();
                int j = list.size();
                Object object = this.state;
                int k = bytebuf.readableBytes();

                try {
                    this.decode(channelhandlercontext, this.replayable, list);
                    if (channelhandlercontext.isRemoved()) {
                        break;
                    }

                    if (j == list.size()) {
                        if (k == bytebuf.readableBytes() && object == this.state) {
                            throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() must consume the inbound " + "data or change its state if it did not decode anything.");
                        }
                        continue;
                    }
                } catch (Signal signal) {
                    signal.expect(ReplayingDecoder.REPLAY);
                    if (!channelhandlercontext.isRemoved()) {
                        int l = this.checkpoint;

                        if (l >= 0) {
                            bytebuf.readerIndex(l);
                        }
                    }
                    break;
                }

                if (i == bytebuf.readerIndex() && object == this.state) {
                    throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() method must consume the inbound data " + "or change its state if it decoded something.");
                }

                if (this.isSingleDecode()) {
                    break;
                }
            }

        } catch (DecoderException decoderexception) {
            throw decoderexception;
        } catch (Throwable throwable) {
            throw new DecoderException(throwable);
        }
    }
}
