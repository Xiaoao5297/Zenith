package io.netty.handler.codec.protobuf;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {

    public ProtobufEncoder() {}

    protected void encode(ChannelHandlerContext channelhandlercontext, MessageLiteOrBuilder messageliteorbuilder, List<Object> list) throws Exception {
        if (messageliteorbuilder instanceof MessageLite) {
            list.add(Unpooled.wrappedBuffer(((MessageLite) messageliteorbuilder).toByteArray()));
        } else {
            if (messageliteorbuilder instanceof Builder) {
                list.add(Unpooled.wrappedBuffer(((Builder) messageliteorbuilder).build().toByteArray()));
            }

        }
    }
}
