package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class InternalAttribute extends AbstractReferenceCounted implements InterfaceHttpData {

    private final List<ByteBuf> value = new ArrayList();
    private final Charset charset;
    private int size;

    public InternalAttribute(Charset charset) {
        this.charset = charset;
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.InternalAttribute;
    }

    public void addValue(String s) {
        if (s == null) {
            throw new NullPointerException("value");
        } else {
            ByteBuf bytebuf = Unpooled.copiedBuffer((CharSequence) s, this.charset);

            this.value.add(bytebuf);
            this.size += bytebuf.readableBytes();
        }
    }

    public void addValue(String s, int i) {
        if (s == null) {
            throw new NullPointerException("value");
        } else {
            ByteBuf bytebuf = Unpooled.copiedBuffer((CharSequence) s, this.charset);

            this.value.add(i, bytebuf);
            this.size += bytebuf.readableBytes();
        }
    }

    public void setValue(String s, int i) {
        if (s == null) {
            throw new NullPointerException("value");
        } else {
            ByteBuf bytebuf = Unpooled.copiedBuffer((CharSequence) s, this.charset);
            ByteBuf bytebuf1 = (ByteBuf) this.value.set(i, bytebuf);

            if (bytebuf1 != null) {
                this.size -= bytebuf1.readableBytes();
            }

            this.size += bytebuf.readableBytes();
        }
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
        if (!(interfacehttpdata instanceof InternalAttribute)) {
            throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + interfacehttpdata.getHttpDataType());
        } else {
            return this.compareTo((InternalAttribute) interfacehttpdata);
        }
    }

    public int compareTo(InternalAttribute internalattribute) {
        return this.getName().compareToIgnoreCase(internalattribute.getName());
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = this.value.iterator();

        while (iterator.hasNext()) {
            ByteBuf bytebuf = (ByteBuf) iterator.next();

            stringbuilder.append(bytebuf.toString(this.charset));
        }

        return stringbuilder.toString();
    }

    public int size() {
        return this.size;
    }

    public ByteBuf toByteBuf() {
        return Unpooled.compositeBuffer().addComponents((Iterable) this.value).writerIndex(this.size()).readerIndex(0);
    }

    @Override
    public String getName() {
        return "InternalAttribute";
    }

    @Override
    protected void deallocate() {}
}
