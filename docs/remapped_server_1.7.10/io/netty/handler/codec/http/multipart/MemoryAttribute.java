package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import java.io.IOException;

public class MemoryAttribute extends AbstractMemoryHttpData implements Attribute {

    public MemoryAttribute(String s) {
        super(s, HttpConstants.DEFAULT_CHARSET, 0L);
    }

    public MemoryAttribute(String s, String s1) throws IOException {
        super(s, HttpConstants.DEFAULT_CHARSET, 0L);
        this.setValue(s1);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.Attribute;
    }

    @Override
    public String getValue() {
        return this.getByteBuf().toString(this.charset);
    }

    @Override
    public void setValue(String s) throws IOException {
        if (s == null) {
            throw new NullPointerException("value");
        } else {
            byte[] abyte = s.getBytes(this.charset.name());
            ByteBuf bytebuf = Unpooled.wrappedBuffer(abyte);

            if (this.definedSize > 0L) {
                this.definedSize = (long) bytebuf.readableBytes();
            }

            this.setContent(bytebuf);
        }
    }

    @Override
    public void addContent(ByteBuf bytebuf, boolean flag) throws IOException {
        int i = bytebuf.readableBytes();

        if (this.definedSize > 0L && this.definedSize < this.size + (long) i) {
            this.definedSize = this.size + (long) i;
        }

        super.addContent(bytebuf, flag);
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object object) {
        if (!(object instanceof Attribute)) {
            return false;
        } else {
            Attribute attribute = (Attribute) object;

            return this.getName().equalsIgnoreCase(attribute.getName());
        }
    }

    public int compareTo(InterfaceHttpData interfacehttpdata) {
        if (!(interfacehttpdata instanceof Attribute)) {
            throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + interfacehttpdata.getHttpDataType());
        } else {
            return this.compareTo((Attribute) interfacehttpdata);
        }
    }

    public int compareTo(Attribute attribute) {
        return this.getName().compareToIgnoreCase(attribute.getName());
    }

    public String toString() {
        return this.getName() + '=' + this.getValue();
    }

    @Override
    public Attribute copy() {
        MemoryAttribute memoryattribute = new MemoryAttribute(this.getName());

        memoryattribute.setCharset(this.getCharset());
        ByteBuf bytebuf = this.content();

        if (bytebuf != null) {
            try {
                memoryattribute.setContent(bytebuf.copy());
            } catch (IOException ioexception) {
                throw new ChannelException(ioexception);
            }
        }

        return memoryattribute;
    }

    @Override
    public Attribute duplicate() {
        MemoryAttribute memoryattribute = new MemoryAttribute(this.getName());

        memoryattribute.setCharset(this.getCharset());
        ByteBuf bytebuf = this.content();

        if (bytebuf != null) {
            try {
                memoryattribute.setContent(bytebuf.duplicate());
            } catch (IOException ioexception) {
                throw new ChannelException(ioexception);
            }
        }

        return memoryattribute;
    }

    @Override
    public Attribute retain() {
        super.retain();
        return this;
    }

    @Override
    public Attribute retain(int i) {
        super.retain(i);
        return this;
    }
}
