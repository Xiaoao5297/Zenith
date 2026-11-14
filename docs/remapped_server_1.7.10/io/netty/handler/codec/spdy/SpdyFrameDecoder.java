package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class SpdyFrameDecoder extends ByteToMessageDecoder {

    private static final SpdyProtocolException INVALID_FRAME = new SpdyProtocolException("Received invalid frame");
    private final int spdyVersion;
    private final int maxChunkSize;
    private final SpdyHeaderBlockDecoder headerBlockDecoder;
    private SpdyFrameDecoder.State state;
    private SpdySettingsFrame spdySettingsFrame;
    private SpdyHeadersFrame spdyHeadersFrame;
    private byte flags;
    private int length;
    private int version;
    private int type;
    private int streamId;

    public SpdyFrameDecoder(int i) {
        this(i, 8192, 16384);
    }

    public SpdyFrameDecoder(int i, int j, int k) {
        this(i, j, SpdyHeaderBlockDecoder.newInstance(i, k));
    }

    protected SpdyFrameDecoder(int i, int j, SpdyHeaderBlockDecoder spdyheaderblockdecoder) {
        if (i >= 2 && i <= 3) {
            if (j <= 0) {
                throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + j);
            } else {
                this.spdyVersion = i;
                this.maxChunkSize = j;
                this.headerBlockDecoder = spdyheaderblockdecoder;
                this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
            }
        } else {
            throw new IllegalArgumentException("unsupported version: " + i);
        }
    }

    @Override
    public void decodeLast(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        try {
            this.decode(channelhandlercontext, bytebuf, list);
        } finally {
            this.headerBlockDecoder.end();
        }

    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        int i;
        int j;

        switch (this.state) {
            case READ_COMMON_HEADER:
                this.state = this.readCommonHeader(bytebuf);
                if (this.state == SpdyFrameDecoder.State.FRAME_ERROR) {
                    if (this.version != this.spdyVersion) {
                        fireProtocolException(channelhandlercontext, "Unsupported version: " + this.version);
                    } else {
                        fireInvalidFrameException(channelhandlercontext);
                    }
                }

                if (this.length == 0) {
                    if (this.state == SpdyFrameDecoder.State.READ_DATA_FRAME) {
                        DefaultSpdyDataFrame defaultspdydataframe = new DefaultSpdyDataFrame(this.streamId);

                        defaultspdydataframe.setLast((this.flags & 1) != 0);
                        this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                        list.add(defaultspdydataframe);
                        return;
                    }

                    this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                }

                return;
            case READ_CONTROL_FRAME:
                try {
                    Object object = this.readControlFrame(bytebuf);

                    if (object != null) {
                        this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                        list.add(object);
                    }

                    return;
                } catch (IllegalArgumentException illegalargumentexception) {
                    this.state = SpdyFrameDecoder.State.FRAME_ERROR;
                    fireInvalidFrameException(channelhandlercontext);
                    return;
                }
            case READ_SETTINGS_FRAME:
                int k;

                if (this.spdySettingsFrame == null) {
                    if (bytebuf.readableBytes() < 4) {
                        return;
                    }

                    k = SpdyCodecUtil.getUnsignedInt(bytebuf, bytebuf.readerIndex());
                    bytebuf.skipBytes(4);
                    this.length -= 4;
                    if ((this.length & 7) != 0 || this.length >> 3 != k) {
                        this.state = SpdyFrameDecoder.State.FRAME_ERROR;
                        fireInvalidFrameException(channelhandlercontext);
                        return;
                    }

                    this.spdySettingsFrame = new DefaultSpdySettingsFrame();
                    boolean flag = (this.flags & 1) != 0;

                    this.spdySettingsFrame.setClearPreviouslyPersistedSettings(flag);
                }

                k = Math.min(bytebuf.readableBytes() >> 3, this.length >> 3);

                for (i = 0; i < k; ++i) {
                    int l;
                    byte b0;

                    if (this.version < 3) {
                        l = bytebuf.readByte() & 255 | (bytebuf.readByte() & 255) << 8 | (bytebuf.readByte() & 255) << 16;
                        b0 = bytebuf.readByte();
                    } else {
                        b0 = bytebuf.readByte();
                        l = SpdyCodecUtil.getUnsignedMedium(bytebuf, bytebuf.readerIndex());
                        bytebuf.skipBytes(3);
                    }

                    j = SpdyCodecUtil.getSignedInt(bytebuf, bytebuf.readerIndex());
                    bytebuf.skipBytes(4);
                    if (l == 0) {
                        this.state = SpdyFrameDecoder.State.FRAME_ERROR;
                        this.spdySettingsFrame = null;
                        fireInvalidFrameException(channelhandlercontext);
                        return;
                    }

                    if (!this.spdySettingsFrame.isSet(l)) {
                        boolean flag1 = (b0 & 1) != 0;
                        boolean flag2 = (b0 & 2) != 0;

                        this.spdySettingsFrame.setValue(l, j, flag1, flag2);
                    }
                }

                this.length -= 8 * k;
                if (this.length == 0) {
                    this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                    SpdySettingsFrame spdysettingsframe = this.spdySettingsFrame;

                    this.spdySettingsFrame = null;
                    list.add(spdysettingsframe);
                    return;
                } else {
                    return;
                }
            case READ_HEADER_BLOCK_FRAME:
                try {
                    this.spdyHeadersFrame = this.readHeaderBlockFrame(bytebuf);
                    if (this.spdyHeadersFrame != null) {
                        if (this.length == 0) {
                            this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                            SpdyHeadersFrame spdyheadersframe = this.spdyHeadersFrame;

                            this.spdyHeadersFrame = null;
                            list.add(spdyheadersframe);
                            return;
                        }

                        this.state = SpdyFrameDecoder.State.READ_HEADER_BLOCK;
                    }

                    return;
                } catch (IllegalArgumentException illegalargumentexception1) {
                    this.state = SpdyFrameDecoder.State.FRAME_ERROR;
                    fireInvalidFrameException(channelhandlercontext);
                    return;
                }
            case READ_HEADER_BLOCK:
                i = Math.min(bytebuf.readableBytes(), this.length);
                ByteBuf bytebuf1 = bytebuf.slice(bytebuf.readerIndex(), i);

                try {
                    this.headerBlockDecoder.decode(bytebuf1, this.spdyHeadersFrame);
                } catch (Exception exception) {
                    this.state = SpdyFrameDecoder.State.FRAME_ERROR;
                    this.spdyHeadersFrame = null;
                    channelhandlercontext.fireExceptionCaught(exception);
                    return;
                }

                int i1 = i - bytebuf1.readableBytes();

                bytebuf.skipBytes(i1);
                this.length -= i1;
                SpdyHeadersFrame spdyheadersframe1;

                if (this.spdyHeadersFrame != null && (this.spdyHeadersFrame.isInvalid() || this.spdyHeadersFrame.isTruncated())) {
                    spdyheadersframe1 = this.spdyHeadersFrame;
                    this.spdyHeadersFrame = null;
                    if (this.length == 0) {
                        this.headerBlockDecoder.reset();
                        this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                    }

                    list.add(spdyheadersframe1);
                    return;
                }

                if (this.length == 0) {
                    spdyheadersframe1 = this.spdyHeadersFrame;
                    this.spdyHeadersFrame = null;
                    this.headerBlockDecoder.reset();
                    this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                    if (spdyheadersframe1 != null) {
                        list.add(spdyheadersframe1);
                    }
                }

                return;
            case READ_DATA_FRAME:
                if (this.streamId == 0) {
                    this.state = SpdyFrameDecoder.State.FRAME_ERROR;
                    fireProtocolException(channelhandlercontext, "Received invalid data frame");
                    return;
                } else {
                    j = Math.min(this.maxChunkSize, this.length);
                    if (bytebuf.readableBytes() < j) {
                        return;
                    }

                    ByteBuf bytebuf2 = channelhandlercontext.alloc().buffer(j);

                    bytebuf2.writeBytes(bytebuf, j);
                    DefaultSpdyDataFrame defaultspdydataframe1 = new DefaultSpdyDataFrame(this.streamId, bytebuf2);

                    this.length -= j;
                    if (this.length == 0) {
                        defaultspdydataframe1.setLast((this.flags & 1) != 0);
                        this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                    }

                    list.add(defaultspdydataframe1);
                    return;
                }
            case DISCARD_FRAME:
                int j1 = Math.min(bytebuf.readableBytes(), this.length);

                bytebuf.skipBytes(j1);
                this.length -= j1;
                if (this.length == 0) {
                    this.state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                }

                return;
            case FRAME_ERROR:
                bytebuf.skipBytes(bytebuf.readableBytes());
                return;
            default:
                throw new Error("Shouldn't reach here.");
        }
    }

    private SpdyFrameDecoder.State readCommonHeader(ByteBuf bytebuf) {
        if (bytebuf.readableBytes() < 8) {
            return SpdyFrameDecoder.State.READ_COMMON_HEADER;
        } else {
            int i = bytebuf.readerIndex();
            int j = i + 4;
            int k = i + 5;

            bytebuf.skipBytes(8);
            boolean flag = (bytebuf.getByte(i) & 128) != 0;

            this.flags = bytebuf.getByte(j);
            this.length = SpdyCodecUtil.getUnsignedMedium(bytebuf, k);
            if (flag) {
                this.version = SpdyCodecUtil.getUnsignedShort(bytebuf, i) & 32767;
                int l = i + 2;

                this.type = SpdyCodecUtil.getUnsignedShort(bytebuf, l);
                this.streamId = 0;
            } else {
                this.version = this.spdyVersion;
                this.type = 0;
                this.streamId = SpdyCodecUtil.getUnsignedInt(bytebuf, i);
            }

            if (this.version == this.spdyVersion && this.isValidFrameHeader()) {
                SpdyFrameDecoder.State spdyframedecoder_state;

                if (this.willGenerateFrame()) {
                    switch (this.type) {
                        case 0:
                            spdyframedecoder_state = SpdyFrameDecoder.State.READ_DATA_FRAME;
                            break;
                        case 1:
                        case 2:
                        case 8:
                            spdyframedecoder_state = SpdyFrameDecoder.State.READ_HEADER_BLOCK_FRAME;
                            break;
                        case 3:
                        case 5:
                        case 6:
                        case 7:
                        default:
                            spdyframedecoder_state = SpdyFrameDecoder.State.READ_CONTROL_FRAME;
                            break;
                        case 4:
                            spdyframedecoder_state = SpdyFrameDecoder.State.READ_SETTINGS_FRAME;
                    }
                } else if (this.length != 0) {
                    spdyframedecoder_state = SpdyFrameDecoder.State.DISCARD_FRAME;
                } else {
                    spdyframedecoder_state = SpdyFrameDecoder.State.READ_COMMON_HEADER;
                }

                return spdyframedecoder_state;
            } else {
                return SpdyFrameDecoder.State.FRAME_ERROR;
            }
        }
    }

    private Object readControlFrame(ByteBuf bytebuf) {
        int i;
        int j;

        switch (this.type) {
            case 3:
                if (bytebuf.readableBytes() < 8) {
                    return null;
                }

                i = SpdyCodecUtil.getUnsignedInt(bytebuf, bytebuf.readerIndex());
                j = SpdyCodecUtil.getSignedInt(bytebuf, bytebuf.readerIndex() + 4);
                bytebuf.skipBytes(8);
                return new DefaultSpdyRstStreamFrame(i, j);
            case 4:
            case 5:
            case 8:
            default:
                throw new Error("Shouldn't reach here.");
            case 6:
                if (bytebuf.readableBytes() < 4) {
                    return null;
                }

                int k = SpdyCodecUtil.getSignedInt(bytebuf, bytebuf.readerIndex());

                bytebuf.skipBytes(4);
                return new DefaultSpdyPingFrame(k);
            case 7:
                int l = this.version < 3 ? 4 : 8;

                if (bytebuf.readableBytes() < l) {
                    return null;
                } else {
                    int i1 = SpdyCodecUtil.getUnsignedInt(bytebuf, bytebuf.readerIndex());

                    bytebuf.skipBytes(4);
                    if (this.version < 3) {
                        return new DefaultSpdyGoAwayFrame(i1);
                    }

                    j = SpdyCodecUtil.getSignedInt(bytebuf, bytebuf.readerIndex());
                    bytebuf.skipBytes(4);
                    return new DefaultSpdyGoAwayFrame(i1, j);
                }
            case 9:
                if (bytebuf.readableBytes() < 8) {
                    return null;
                } else {
                    i = SpdyCodecUtil.getUnsignedInt(bytebuf, bytebuf.readerIndex());
                    int j1 = SpdyCodecUtil.getUnsignedInt(bytebuf, bytebuf.readerIndex() + 4);

                    bytebuf.skipBytes(8);
                    return new DefaultSpdyWindowUpdateFrame(i, j1);
                }
        }
    }

    private SpdyHeadersFrame readHeaderBlockFrame(ByteBuf bytebuf) {
        int i;
        int j;

        switch (this.type) {
            case 1:
                i = this.version < 3 ? 12 : 10;
                if (bytebuf.readableBytes() < i) {
                    return null;
                }

                int k = bytebuf.readerIndex();

                j = SpdyCodecUtil.getUnsignedInt(bytebuf, k);
                int l = SpdyCodecUtil.getUnsignedInt(bytebuf, k + 4);
                byte b0 = (byte) (bytebuf.getByte(k + 8) >> 5 & 7);

                if (this.version < 3) {
                    b0 = (byte) (b0 >> 1);
                }

                bytebuf.skipBytes(10);
                this.length -= 10;
                if (this.version < 3 && this.length == 2 && bytebuf.getShort(bytebuf.readerIndex()) == 0) {
                    bytebuf.skipBytes(2);
                    this.length = 0;
                }

                DefaultSpdySynStreamFrame defaultspdysynstreamframe = new DefaultSpdySynStreamFrame(j, l, b0);

                defaultspdysynstreamframe.setLast((this.flags & 1) != 0);
                defaultspdysynstreamframe.setUnidirectional((this.flags & 2) != 0);
                return defaultspdysynstreamframe;
            case 2:
                i = this.version < 3 ? 8 : 4;
                if (bytebuf.readableBytes() < i) {
                    return null;
                }

                j = SpdyCodecUtil.getUnsignedInt(bytebuf, bytebuf.readerIndex());
                bytebuf.skipBytes(4);
                this.length -= 4;
                if (this.version < 3) {
                    bytebuf.skipBytes(2);
                    this.length -= 2;
                }

                if (this.version < 3 && this.length == 2 && bytebuf.getShort(bytebuf.readerIndex()) == 0) {
                    bytebuf.skipBytes(2);
                    this.length = 0;
                }

                DefaultSpdySynReplyFrame defaultspdysynreplyframe = new DefaultSpdySynReplyFrame(j);

                defaultspdysynreplyframe.setLast((this.flags & 1) != 0);
                return defaultspdysynreplyframe;
            case 8:
                if (bytebuf.readableBytes() < 4) {
                    return null;
                } else {
                    if (this.version < 3 && this.length > 4 && bytebuf.readableBytes() < 8) {
                        return null;
                    }

                    j = SpdyCodecUtil.getUnsignedInt(bytebuf, bytebuf.readerIndex());
                    bytebuf.skipBytes(4);
                    this.length -= 4;
                    if (this.version < 3 && this.length != 0) {
                        bytebuf.skipBytes(2);
                        this.length -= 2;
                    }

                    if (this.version < 3 && this.length == 2 && bytebuf.getShort(bytebuf.readerIndex()) == 0) {
                        bytebuf.skipBytes(2);
                        this.length = 0;
                    }

                    DefaultSpdyHeadersFrame defaultspdyheadersframe = new DefaultSpdyHeadersFrame(j);

                    defaultspdyheadersframe.setLast((this.flags & 1) != 0);
                    return defaultspdyheadersframe;
                }
            default:
                throw new Error("Shouldn't reach here.");
        }
    }

    private boolean isValidFrameHeader() {
        switch (this.type) {
            case 0:
                return this.streamId != 0;
            case 1:
                return this.version < 3 ? this.length >= 12 : this.length >= 10;
            case 2:
                return this.version < 3 ? this.length >= 8 : this.length >= 4;
            case 3:
                return this.flags == 0 && this.length == 8;
            case 4:
                return this.length >= 4;
            case 5:
            default:
                return true;
            case 6:
                return this.length == 4;
            case 7:
                return this.version < 3 ? this.length == 4 : this.length == 8;
            case 8:
                if (this.version >= 3) {
                    return this.length >= 4;
                }

                return this.length == 4 || this.length >= 8;
            case 9:
                return this.length == 8;
        }
    }

    private boolean willGenerateFrame() {
        switch (this.type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
            case 7:
            case 8:
            case 9:
                return true;
            case 5:
            default:
                return false;
        }
    }

    private static void fireInvalidFrameException(ChannelHandlerContext channelhandlercontext) {
        channelhandlercontext.fireExceptionCaught(SpdyFrameDecoder.INVALID_FRAME);
    }

    private static void fireProtocolException(ChannelHandlerContext channelhandlercontext, String s) {
        channelhandlercontext.fireExceptionCaught(new SpdyProtocolException(s));
    }

    private static enum State {

        READ_COMMON_HEADER, READ_CONTROL_FRAME, READ_SETTINGS_FRAME, READ_HEADER_BLOCK_FRAME, READ_HEADER_BLOCK, READ_DATA_FRAME, DISCARD_FRAME, FRAME_ERROR;

        private State() {}
    }
}
