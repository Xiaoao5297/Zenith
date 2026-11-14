package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.List;

public class WebSocket08FrameDecoder extends ReplayingDecoder<WebSocket08FrameDecoder.State> implements WebSocketFrameDecoder {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private UTF8Output fragmentedFramesText;
    private int fragmentedFramesCount;
    private final long maxFramePayloadLength;
    private boolean frameFinalFlag;
    private int frameRsv;
    private int frameOpcode;
    private long framePayloadLength;
    private ByteBuf framePayload;
    private int framePayloadBytesRead;
    private byte[] maskingKey;
    private ByteBuf payloadBuffer;
    private final boolean allowExtensions;
    private final boolean maskedPayload;
    private boolean receivedClosingHandshake;

    public WebSocket08FrameDecoder(boolean flag, boolean flag1, int i) {
        super(WebSocket08FrameDecoder.State.FRAME_START);
        this.maskedPayload = flag;
        this.allowExtensions = flag1;
        this.maxFramePayloadLength = (long) i;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (this.receivedClosingHandshake) {
            bytebuf.skipBytes(this.actualReadableBytes());
        } else {
            try {
                switch ((WebSocket08FrameDecoder.State) this.state()) {
                    case FRAME_START:
                        this.framePayloadBytesRead = 0;
                        this.framePayloadLength = -1L;
                        this.framePayload = null;
                        this.payloadBuffer = null;
                        byte b0 = bytebuf.readByte();

                        this.frameFinalFlag = (b0 & 128) != 0;
                        this.frameRsv = (b0 & 112) >> 4;
                        this.frameOpcode = b0 & 15;
                        if (WebSocket08FrameDecoder.logger.isDebugEnabled()) {
                            WebSocket08FrameDecoder.logger.debug("Decoding WebSocket Frame opCode={}", (Object) this.frameOpcode);
                        }

                        b0 = bytebuf.readByte();
                        boolean flag = (b0 & 128) != 0;
                        int i = b0 & 127;

                        if (this.frameRsv != 0 && !this.allowExtensions) {
                            this.protocolViolation(channelhandlercontext, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
                            return;
                        } else if (this.maskedPayload && !flag) {
                            this.protocolViolation(channelhandlercontext, "unmasked client to server frame");
                            return;
                        } else {
                            if (this.frameOpcode > 7) {
                                if (!this.frameFinalFlag) {
                                    this.protocolViolation(channelhandlercontext, "fragmented control frame");
                                    return;
                                }

                                if (i > 125) {
                                    this.protocolViolation(channelhandlercontext, "control frame with payload length > 125 octets");
                                    return;
                                }

                                if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                                    this.protocolViolation(channelhandlercontext, "control frame using reserved opcode " + this.frameOpcode);
                                    return;
                                }

                                if (this.frameOpcode == 8 && i == 1) {
                                    this.protocolViolation(channelhandlercontext, "received close control frame with payload len 1");
                                    return;
                                }
                            } else {
                                if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                                    this.protocolViolation(channelhandlercontext, "data frame using reserved opcode " + this.frameOpcode);
                                    return;
                                }

                                if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                                    this.protocolViolation(channelhandlercontext, "received continuation data frame outside fragmented message");
                                    return;
                                }

                                if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0 && this.frameOpcode != 9) {
                                    this.protocolViolation(channelhandlercontext, "received non-continuation data frame while inside fragmented message");
                                    return;
                                }
                            }

                            if (i == 126) {
                                this.framePayloadLength = (long) bytebuf.readUnsignedShort();
                                if (this.framePayloadLength < 126L) {
                                    this.protocolViolation(channelhandlercontext, "invalid data frame length (not using minimal length encoding)");
                                    return;
                                }
                            } else if (i == 127) {
                                this.framePayloadLength = bytebuf.readLong();
                                if (this.framePayloadLength < 65536L) {
                                    this.protocolViolation(channelhandlercontext, "invalid data frame length (not using minimal length encoding)");
                                    return;
                                }
                            } else {
                                this.framePayloadLength = (long) i;
                            }

                            if (this.framePayloadLength > this.maxFramePayloadLength) {
                                this.protocolViolation(channelhandlercontext, "Max frame length of " + this.maxFramePayloadLength + " has been exceeded.");
                                return;
                            } else {
                                if (WebSocket08FrameDecoder.logger.isDebugEnabled()) {
                                    WebSocket08FrameDecoder.logger.debug("Decoding WebSocket Frame length={}", (Object) this.framePayloadLength);
                                }

                                this.checkpoint(WebSocket08FrameDecoder.State.MASKING_KEY);
                            }
                        }
                    case MASKING_KEY:
                        if (this.maskedPayload) {
                            if (this.maskingKey == null) {
                                this.maskingKey = new byte[4];
                            }

                            bytebuf.readBytes(this.maskingKey);
                        }

                        this.checkpoint(WebSocket08FrameDecoder.State.PAYLOAD);
                    case PAYLOAD:
                        int j = this.actualReadableBytes();
                        long k = (long) (this.framePayloadBytesRead + j);

                        if (k == this.framePayloadLength) {
                            this.payloadBuffer = channelhandlercontext.alloc().buffer(j);
                            this.payloadBuffer.writeBytes(bytebuf, j);
                        } else {
                            if (k < this.framePayloadLength) {
                                if (this.framePayload == null) {
                                    this.framePayload = channelhandlercontext.alloc().buffer(toFrameLength(this.framePayloadLength));
                                }

                                this.framePayload.writeBytes(bytebuf, j);
                                this.framePayloadBytesRead += j;
                                return;
                            }

                            if (k > this.framePayloadLength) {
                                if (this.framePayload == null) {
                                    this.framePayload = channelhandlercontext.alloc().buffer(toFrameLength(this.framePayloadLength));
                                }

                                this.framePayload.writeBytes(bytebuf, toFrameLength(this.framePayloadLength - (long) this.framePayloadBytesRead));
                            }
                        }

                        this.checkpoint(WebSocket08FrameDecoder.State.FRAME_START);
                        if (this.framePayload == null) {
                            this.framePayload = this.payloadBuffer;
                            this.payloadBuffer = null;
                        } else if (this.payloadBuffer != null) {
                            this.framePayload.writeBytes(this.payloadBuffer);
                            this.payloadBuffer.release();
                            this.payloadBuffer = null;
                        }

                        if (this.maskedPayload) {
                            this.unmask(this.framePayload);
                        }

                        if (this.frameOpcode == 9) {
                            list.add(new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                            this.framePayload = null;
                            return;
                        } else if (this.frameOpcode == 10) {
                            list.add(new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                            this.framePayload = null;
                            return;
                        } else if (this.frameOpcode == 8) {
                            this.checkCloseFrameBody(channelhandlercontext, this.framePayload);
                            this.receivedClosingHandshake = true;
                            list.add(new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                            this.framePayload = null;
                            return;
                        } else {
                            String s = null;

                            if (this.frameFinalFlag) {
                                if (this.frameOpcode != 9) {
                                    this.fragmentedFramesCount = 0;
                                    if (this.frameOpcode == 1 || this.fragmentedFramesText != null) {
                                        this.checkUTF8String(channelhandlercontext, this.framePayload);
                                        s = this.fragmentedFramesText.toString();
                                        this.fragmentedFramesText = null;
                                    }
                                }
                            } else {
                                if (this.fragmentedFramesCount == 0) {
                                    this.fragmentedFramesText = null;
                                    if (this.frameOpcode == 1) {
                                        this.checkUTF8String(channelhandlercontext, this.framePayload);
                                    }
                                } else if (this.fragmentedFramesText != null) {
                                    this.checkUTF8String(channelhandlercontext, this.framePayload);
                                }

                                ++this.fragmentedFramesCount;
                            }

                            if (this.frameOpcode == 1) {
                                list.add(new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                                this.framePayload = null;
                                return;
                            } else if (this.frameOpcode == 2) {
                                list.add(new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload));
                                this.framePayload = null;
                                return;
                            } else {
                                if (this.frameOpcode == 0) {
                                    list.add(new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload, s));
                                    this.framePayload = null;
                                    return;
                                }

                                throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
                            }
                        }
                    case CORRUPT:
                        bytebuf.readByte();
                        return;
                    default:
                        throw new Error("Shouldn't reach here.");
                }
            } catch (Exception exception) {
                if (this.payloadBuffer != null) {
                    if (this.payloadBuffer.refCnt() > 0) {
                        this.payloadBuffer.release();
                    }

                    this.payloadBuffer = null;
                }

                if (this.framePayload != null) {
                    if (this.framePayload.refCnt() > 0) {
                        this.framePayload.release();
                    }

                    this.framePayload = null;
                }

                throw exception;
            }
        }
    }

    private void unmask(ByteBuf bytebuf) {
        for (int i = bytebuf.readerIndex(); i < bytebuf.writerIndex(); ++i) {
            bytebuf.setByte(i, bytebuf.getByte(i) ^ this.maskingKey[i % 4]);
        }

    }

    private void protocolViolation(ChannelHandlerContext channelhandlercontext, String s) {
        this.checkpoint(WebSocket08FrameDecoder.State.CORRUPT);
        if (channelhandlercontext.channel().isActive()) {
            channelhandlercontext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        throw new CorruptedFrameException(s);
    }

    private static int toFrameLength(long i) {
        if (i > 2147483647L) {
            throw new TooLongFrameException("Length:" + i);
        } else {
            return (int) i;
        }
    }

    private void checkUTF8String(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) {
        try {
            if (this.fragmentedFramesText == null) {
                this.fragmentedFramesText = new UTF8Output(bytebuf);
            } else {
                this.fragmentedFramesText.write(bytebuf);
            }
        } catch (UTF8Exception utf8exception) {
            this.protocolViolation(channelhandlercontext, "invalid UTF-8 bytes");
        }

    }

    protected void checkCloseFrameBody(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) {
        if (bytebuf != null && bytebuf.isReadable()) {
            if (bytebuf.readableBytes() == 1) {
                this.protocolViolation(channelhandlercontext, "Invalid close frame body");
            }

            int i = bytebuf.readerIndex();

            bytebuf.readerIndex(0);
            short short0 = bytebuf.readShort();

            if (short0 >= 0 && short0 <= 999 || short0 >= 1004 && short0 <= 1006 || short0 >= 1012 && short0 <= 2999) {
                this.protocolViolation(channelhandlercontext, "Invalid close frame getStatus code: " + short0);
            }

            if (bytebuf.isReadable()) {
                try {
                    new UTF8Output(bytebuf);
                } catch (UTF8Exception utf8exception) {
                    this.protocolViolation(channelhandlercontext, "Invalid close frame reason text. Invalid UTF-8 bytes");
                }
            }

            bytebuf.readerIndex(i);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelInactive(channelhandlercontext);
        if (this.framePayload != null) {
            this.framePayload.release();
        }

        if (this.payloadBuffer != null) {
            this.payloadBuffer.release();
        }

    }

    static enum State {

        FRAME_START, MASKING_KEY, PAYLOAD, CORRUPT;

        private State() {}
    }
}
