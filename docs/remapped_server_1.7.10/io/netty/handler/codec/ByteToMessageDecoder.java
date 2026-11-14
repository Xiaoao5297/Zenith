package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ByteToMessageDecoder extends ChannelInboundHandlerAdapter {

    ByteBuf cumulation;
    private boolean singleDecode;
    private boolean decodeWasNull;

    protected ByteToMessageDecoder() {
        if (this.getClass().isAnnotationPresent(ChannelHandler.Sharable.class)) {
            throw new IllegalStateException("@Sharable annotation is not allowed");
        }
    }

    public void setSingleDecode(boolean flag) {
        this.singleDecode = flag;
    }

    public boolean isSingleDecode() {
        return this.singleDecode;
    }

    protected int actualReadableBytes() {
        return this.internalBuffer().readableBytes();
    }

    protected ByteBuf internalBuffer() {
        return this.cumulation != null ? this.cumulation : Unpooled.EMPTY_BUFFER;
    }

    @Override
    public final void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {
        ByteBuf bytebuf = this.internalBuffer();
        int i = bytebuf.readableBytes();

        if (bytebuf.isReadable()) {
            ByteBuf bytebuf1 = bytebuf.readBytes(i);

            bytebuf.release();
            channelhandlercontext.fireChannelRead(bytebuf1);
        }

        this.cumulation = null;
        channelhandlercontext.fireChannelReadComplete();
        this.handlerRemoved0(channelhandlercontext);
    }

    protected void handlerRemoved0(ChannelHandlerContext channelhandlercontext) throws Exception {}

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        RecyclableArrayList recyclablearraylist = RecyclableArrayList.newInstance();
        boolean flag = false;

        try {
            flag = true;
            if (object instanceof ByteBuf) {
                ByteBuf bytebuf = (ByteBuf) object;

                if (this.cumulation == null) {
                    this.cumulation = bytebuf;

                    try {
                        this.callDecode(channelhandlercontext, this.cumulation, recyclablearraylist);
                    } finally {
                        if (this.cumulation != null && !this.cumulation.isReadable()) {
                            this.cumulation.release();
                            this.cumulation = null;
                        }

                    }

                    flag = false;
                } else {
                    try {
                        if (this.cumulation.writerIndex() > this.cumulation.maxCapacity() - bytebuf.readableBytes()) {
                            ByteBuf bytebuf1 = this.cumulation;

                            this.cumulation = channelhandlercontext.alloc().buffer(bytebuf1.readableBytes() + bytebuf.readableBytes());
                            this.cumulation.writeBytes(bytebuf1);
                            bytebuf1.release();
                        }

                        this.cumulation.writeBytes(bytebuf);
                        this.callDecode(channelhandlercontext, this.cumulation, recyclablearraylist);
                    } finally {
                        if (this.cumulation != null) {
                            if (!this.cumulation.isReadable()) {
                                this.cumulation.release();
                                this.cumulation = null;
                            } else {
                                this.cumulation.discardSomeReadBytes();
                            }
                        }

                        bytebuf.release();
                    }

                    flag = false;
                }
            } else {
                recyclablearraylist.add(object);
                flag = false;
            }
        } catch (DecoderException decoderexception) {
            throw decoderexception;
        } catch (Throwable throwable) {
            throw new DecoderException(throwable);
        } finally {
            if (flag) {
                int i = recyclablearraylist.size();

                if (i == 0) {
                    this.decodeWasNull = true;
                } else {
                    for (int j = 0; j < i; ++j) {
                        channelhandlercontext.fireChannelRead(recyclablearraylist.get(j));
                    }
                }

                recyclablearraylist.recycle();
            }
        }

        int k = recyclablearraylist.size();

        if (k == 0) {
            this.decodeWasNull = true;
        } else {
            for (int l = 0; l < k; ++l) {
                channelhandlercontext.fireChannelRead(recyclablearraylist.get(l));
            }
        }

        recyclablearraylist.recycle();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.decodeWasNull) {
            this.decodeWasNull = false;
            if (!channelhandlercontext.channel().config().isAutoRead()) {
                channelhandlercontext.read();
            }
        }

        channelhandlercontext.fireChannelReadComplete();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        RecyclableArrayList recyclablearraylist = RecyclableArrayList.newInstance();
        boolean flag = false;

        try {
            flag = true;
            if (this.cumulation != null) {
                this.callDecode(channelhandlercontext, this.cumulation, recyclablearraylist);
                this.decodeLast(channelhandlercontext, this.cumulation, recyclablearraylist);
                flag = false;
            } else {
                this.decodeLast(channelhandlercontext, Unpooled.EMPTY_BUFFER, recyclablearraylist);
                flag = false;
            }
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

                int i = recyclablearraylist.size();

                for (int j = 0; j < i; ++j) {
                    channelhandlercontext.fireChannelRead(recyclablearraylist.get(j));
                }

                channelhandlercontext.fireChannelInactive();
                recyclablearraylist.recycle();
            }
        }

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

    protected void callDecode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) {
        try {
            while (true) {
                if (bytebuf.isReadable()) {
                    int i = list.size();
                    int j = bytebuf.readableBytes();

                    this.decode(channelhandlercontext, bytebuf, list);
                    if (!channelhandlercontext.isRemoved()) {
                        if (i == list.size()) {
                            if (j != bytebuf.readableBytes()) {
                                continue;
                            }
                        } else {
                            if (j == bytebuf.readableBytes()) {
                                throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                            }

                            if (!this.isSingleDecode()) {
                                continue;
                            }
                        }
                    }
                }

                return;
            }
        } catch (DecoderException decoderexception) {
            throw decoderexception;
        } catch (Throwable throwable) {
            throw new DecoderException(throwable);
        }
    }

    protected abstract void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception;

    protected void decodeLast(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        this.decode(channelhandlercontext, bytebuf, list);
    }
}
