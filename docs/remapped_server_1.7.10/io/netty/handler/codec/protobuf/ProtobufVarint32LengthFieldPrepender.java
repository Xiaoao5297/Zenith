package io.netty.handler.codec.protobuf;

import com.google.protobuf.CodedOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class ProtobufVarint32LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {

    public ProtobufVarint32LengthFieldPrepender() {}

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) throws Exception {
        int i = bytebuf.readableBytes();
        int j = CodedOutputStream.computeRawVarint32Size(i);

        bytebuf1.ensureWritable(j + i);
        CodedOutputStream codedoutputstream = CodedOutputStream.newInstance(new ByteBufOutputStream(bytebuf1));

        codedoutputstream.writeRawVarint32(i);
        codedoutputstream.flush();
        bytebuf1.writeBytes(bytebuf, bytebuf.readerIndex(), i);
    }
}
