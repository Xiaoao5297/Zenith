package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import java.util.Iterator;
import java.util.Set;

public class SpdyFrameEncoder extends MessageToByteEncoder<SpdyFrame> {

    private final int version;
    private final SpdyHeaderBlockEncoder headerBlockEncoder;

    public SpdyFrameEncoder(int i) {
        this(i, 6, 15, 8);
    }

    public SpdyFrameEncoder(int i, int j, int k, int l) {
        this(i, SpdyHeaderBlockEncoder.newInstance(i, j, k, l));
    }

    protected SpdyFrameEncoder(int i, SpdyHeaderBlockEncoder spdyheaderblockencoder) {
        if (i >= 2 && i <= 3) {
            this.version = i;
            this.headerBlockEncoder = spdyheaderblockencoder;
        } else {
            throw new IllegalArgumentException("unknown version: " + i);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.channel().closeFuture().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelfuture) throws Exception {
                SpdyFrameEncoder.this.headerBlockEncoder.end();
            }
        });
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, SpdyFrame spdyframe, ByteBuf bytebuf) throws Exception {
        ByteBuf bytebuf1;
        int i;

        if (spdyframe instanceof SpdyDataFrame) {
            SpdyDataFrame spdydataframe = (SpdyDataFrame) spdyframe;

            bytebuf1 = spdydataframe.content();
            i = spdydataframe.isLast() ? 1 : 0;
            bytebuf.ensureWritable(8 + bytebuf1.readableBytes());
            bytebuf.writeInt(spdydataframe.getStreamId() & Integer.MAX_VALUE);
            bytebuf.writeByte(i);
            bytebuf.writeMedium(bytebuf1.readableBytes());
            bytebuf.writeBytes(bytebuf1, bytebuf1.readerIndex(), bytebuf1.readableBytes());
        } else {
            int j;
            int k;

            if (spdyframe instanceof SpdySynStreamFrame) {
                SpdySynStreamFrame spdysynstreamframe = (SpdySynStreamFrame) spdyframe;

                bytebuf1 = this.headerBlockEncoder.encode(channelhandlercontext, spdysynstreamframe);

                try {
                    i = spdysynstreamframe.isLast() ? 1 : 0;
                    if (spdysynstreamframe.isUnidirectional()) {
                        i = (byte) (i | 2);
                    }

                    j = bytebuf1.readableBytes();
                    if (this.version < 3) {
                        k = j == 0 ? 12 : 10 + j;
                    } else {
                        k = 10 + j;
                    }

                    bytebuf.ensureWritable(8 + k);
                    bytebuf.writeShort(this.version | '\u8000');
                    bytebuf.writeShort(1);
                    bytebuf.writeByte(i);
                    bytebuf.writeMedium(k);
                    bytebuf.writeInt(spdysynstreamframe.getStreamId());
                    bytebuf.writeInt(spdysynstreamframe.getAssociatedToStreamId());
                    if (this.version < 3) {
                        byte b0 = spdysynstreamframe.getPriority();

                        if (b0 > 3) {
                            b0 = 3;
                        }

                        bytebuf.writeShort((b0 & 255) << 14);
                    } else {
                        bytebuf.writeShort((spdysynstreamframe.getPriority() & 255) << 13);
                    }

                    if (this.version < 3 && bytebuf1.readableBytes() == 0) {
                        bytebuf.writeShort(0);
                    }

                    bytebuf.writeBytes(bytebuf1, bytebuf1.readerIndex(), j);
                } finally {
                    bytebuf1.release();
                }
            } else if (spdyframe instanceof SpdySynReplyFrame) {
                SpdySynReplyFrame spdysynreplyframe = (SpdySynReplyFrame) spdyframe;

                bytebuf1 = this.headerBlockEncoder.encode(channelhandlercontext, spdysynreplyframe);

                try {
                    i = spdysynreplyframe.isLast() ? 1 : 0;
                    j = bytebuf1.readableBytes();
                    if (this.version < 3) {
                        k = j == 0 ? 8 : 6 + j;
                    } else {
                        k = 4 + j;
                    }

                    bytebuf.ensureWritable(8 + k);
                    bytebuf.writeShort(this.version | '\u8000');
                    bytebuf.writeShort(2);
                    bytebuf.writeByte(i);
                    bytebuf.writeMedium(k);
                    bytebuf.writeInt(spdysynreplyframe.getStreamId());
                    if (this.version < 3) {
                        if (j == 0) {
                            bytebuf.writeInt(0);
                        } else {
                            bytebuf.writeShort(0);
                        }
                    }

                    bytebuf.writeBytes(bytebuf1, bytebuf1.readerIndex(), j);
                } finally {
                    bytebuf1.release();
                }
            } else if (spdyframe instanceof SpdyRstStreamFrame) {
                SpdyRstStreamFrame spdyrststreamframe = (SpdyRstStreamFrame) spdyframe;

                bytebuf.ensureWritable(16);
                bytebuf.writeShort(this.version | '\u8000');
                bytebuf.writeShort(3);
                bytebuf.writeInt(8);
                bytebuf.writeInt(spdyrststreamframe.getStreamId());
                bytebuf.writeInt(spdyrststreamframe.getStatus().getCode());
            } else {
                int l;

                if (spdyframe instanceof SpdySettingsFrame) {
                    SpdySettingsFrame spdysettingsframe = (SpdySettingsFrame) spdyframe;

                    l = spdysettingsframe.clearPreviouslyPersistedSettings() ? 1 : 0;
                    Set set = spdysettingsframe.getIds();

                    j = set.size();
                    k = 4 + j * 8;
                    bytebuf.ensureWritable(8 + k);
                    bytebuf.writeShort(this.version | '\u8000');
                    bytebuf.writeShort(4);
                    bytebuf.writeByte(l);
                    bytebuf.writeMedium(k);
                    bytebuf.writeInt(j);

                    int i1;

                    for (Iterator iterator = set.iterator(); iterator.hasNext(); bytebuf.writeInt(spdysettingsframe.getValue(i1))) {
                        Integer integer = (Integer) iterator.next();

                        i1 = integer;
                        byte b1 = 0;

                        if (spdysettingsframe.isPersistValue(i1)) {
                            b1 = (byte) (b1 | 1);
                        }

                        if (spdysettingsframe.isPersisted(i1)) {
                            b1 = (byte) (b1 | 2);
                        }

                        if (this.version < 3) {
                            bytebuf.writeByte(i1 & 255);
                            bytebuf.writeByte(i1 >> 8 & 255);
                            bytebuf.writeByte(i1 >> 16 & 255);
                            bytebuf.writeByte(b1);
                        } else {
                            bytebuf.writeByte(b1);
                            bytebuf.writeMedium(i1);
                        }
                    }
                } else if (spdyframe instanceof SpdyPingFrame) {
                    SpdyPingFrame spdypingframe = (SpdyPingFrame) spdyframe;

                    bytebuf.ensureWritable(12);
                    bytebuf.writeShort(this.version | '\u8000');
                    bytebuf.writeShort(6);
                    bytebuf.writeInt(4);
                    bytebuf.writeInt(spdypingframe.getId());
                } else if (spdyframe instanceof SpdyGoAwayFrame) {
                    SpdyGoAwayFrame spdygoawayframe = (SpdyGoAwayFrame) spdyframe;

                    l = this.version < 3 ? 4 : 8;
                    bytebuf.ensureWritable(8 + l);
                    bytebuf.writeShort(this.version | '\u8000');
                    bytebuf.writeShort(7);
                    bytebuf.writeInt(l);
                    bytebuf.writeInt(spdygoawayframe.getLastGoodStreamId());
                    if (this.version >= 3) {
                        bytebuf.writeInt(spdygoawayframe.getStatus().getCode());
                    }
                } else if (spdyframe instanceof SpdyHeadersFrame) {
                    SpdyHeadersFrame spdyheadersframe = (SpdyHeadersFrame) spdyframe;

                    bytebuf1 = this.headerBlockEncoder.encode(channelhandlercontext, spdyheadersframe);

                    try {
                        i = spdyheadersframe.isLast() ? 1 : 0;
                        j = bytebuf1.readableBytes();
                        if (this.version < 3) {
                            k = j == 0 ? 4 : 6 + j;
                        } else {
                            k = 4 + j;
                        }

                        bytebuf.ensureWritable(8 + k);
                        bytebuf.writeShort(this.version | '\u8000');
                        bytebuf.writeShort(8);
                        bytebuf.writeByte(i);
                        bytebuf.writeMedium(k);
                        bytebuf.writeInt(spdyheadersframe.getStreamId());
                        if (this.version < 3 && j != 0) {
                            bytebuf.writeShort(0);
                        }

                        bytebuf.writeBytes(bytebuf1, bytebuf1.readerIndex(), j);
                    } finally {
                        bytebuf1.release();
                    }
                } else {
                    if (!(spdyframe instanceof SpdyWindowUpdateFrame)) {
                        throw new UnsupportedMessageTypeException(spdyframe, new Class[0]);
                    }

                    SpdyWindowUpdateFrame spdywindowupdateframe = (SpdyWindowUpdateFrame) spdyframe;

                    bytebuf.ensureWritable(16);
                    bytebuf.writeShort(this.version | '\u8000');
                    bytebuf.writeShort(9);
                    bytebuf.writeInt(8);
                    bytebuf.writeInt(spdywindowupdateframe.getStreamId());
                    bytebuf.writeInt(spdywindowupdateframe.getDeltaWindowSize());
                }
            }
        }

    }
}
