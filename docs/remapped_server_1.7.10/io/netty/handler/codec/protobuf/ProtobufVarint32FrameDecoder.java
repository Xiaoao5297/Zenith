package io.netty.handler.codec.protobuf;

import com.google.protobuf.CodedInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class ProtobufVarint32FrameDecoder extends ByteToMessageDecoder {

    public ProtobufVarint32FrameDecoder() {}

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        bytebuf.markReaderIndex();
        byte[] abyte = new byte[5];

        for (int i = 0; i < abyte.length; ++i) {
            if (!bytebuf.isReadable()) {
                bytebuf.resetReaderIndex();
                return;
            }

            abyte[i] = bytebuf.readByte();
            if (abyte[i] >= 0) {
                int j = CodedInputStream.newInstance(abyte, 0, i + 1).readRawVarint32();

                if (j < 0) {
                    throw new CorruptedFrameException("negative length: " + j);
                }

                if (bytebuf.readableBytes() < j) {
                    bytebuf.resetReaderIndex();
                    return;
                }

                list.add(bytebuf.readBytes(j));
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 32-bit");
    }
}
