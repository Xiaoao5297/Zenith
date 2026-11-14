package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class CompatibleObjectEncoder extends MessageToByteEncoder<Serializable> {

    private static final AttributeKey<ObjectOutputStream> OOS = new AttributeKey<>(CompatibleObjectEncoder.class.getName() + ".oos");
    private final int resetInterval;
    private int writtenObjects;

    public CompatibleObjectEncoder() {
        this(16);
    }

    public CompatibleObjectEncoder(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("resetInterval: " + i);
        } else {
            this.resetInterval = i;
        }
    }

    protected ObjectOutputStream newObjectOutputStream(OutputStream outputstream) throws Exception {
        return new ObjectOutputStream(outputstream);
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, Serializable serializable, ByteBuf bytebuf) throws Exception {
        Attribute attribute = channelhandlercontext.attr(CompatibleObjectEncoder.OOS);
        ObjectOutputStream objectoutputstream = (ObjectOutputStream) attribute.get();

        if (objectoutputstream == null) {
            objectoutputstream = this.newObjectOutputStream(new ByteBufOutputStream(bytebuf));
            ObjectOutputStream objectoutputstream1 = (ObjectOutputStream) attribute.setIfAbsent(objectoutputstream);

            if (objectoutputstream1 != null) {
                objectoutputstream = objectoutputstream1;
            }
        }

        synchronized (objectoutputstream) {
            if (this.resetInterval != 0) {
                ++this.writtenObjects;
                if (this.writtenObjects % this.resetInterval == 0) {
                    objectoutputstream.reset();
                }
            }

            objectoutputstream.writeObject(serializable);
            objectoutputstream.flush();
        }
    }
}
