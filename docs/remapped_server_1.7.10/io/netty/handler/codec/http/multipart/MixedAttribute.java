package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedAttribute implements Attribute {

    private Attribute attribute;
    private final long limitSize;

    public MixedAttribute(String s, long i) {
        this.limitSize = i;
        this.attribute = new MemoryAttribute(s);
    }

    public MixedAttribute(String s, String s1, long i) {
        this.limitSize = i;
        if ((long) s1.length() > this.limitSize) {
            try {
                this.attribute = new DiskAttribute(s, s1);
            } catch (IOException ioexception) {
                try {
                    this.attribute = new MemoryAttribute(s, s1);
                } catch (IOException ioexception1) {
                    throw new IllegalArgumentException(ioexception);
                }
            }
        } else {
            try {
                this.attribute = new MemoryAttribute(s, s1);
            } catch (IOException ioexception2) {
                throw new IllegalArgumentException(ioexception2);
            }
        }

    }

    @Override
    public void addContent(ByteBuf bytebuf, boolean flag) throws IOException {
        if (this.attribute instanceof MemoryAttribute && this.attribute.length() + (long) bytebuf.readableBytes() > this.limitSize) {
            DiskAttribute diskattribute = new DiskAttribute(this.attribute.getName());

            if (((MemoryAttribute) this.attribute).getByteBuf() != null) {
                diskattribute.addContent(((MemoryAttribute) this.attribute).getByteBuf(), false);
            }

            this.attribute = diskattribute;
        }

        this.attribute.addContent(bytebuf, flag);
    }

    @Override
    public void delete() {
        this.attribute.delete();
    }

    @Override
    public byte[] get() throws IOException {
        return this.attribute.get();
    }

    @Override
    public ByteBuf getByteBuf() throws IOException {
        return this.attribute.getByteBuf();
    }

    @Override
    public Charset getCharset() {
        return this.attribute.getCharset();
    }

    @Override
    public String getString() throws IOException {
        return this.attribute.getString();
    }

    @Override
    public String getString(Charset charset) throws IOException {
        return this.attribute.getString(charset);
    }

    @Override
    public boolean isCompleted() {
        return this.attribute.isCompleted();
    }

    @Override
    public boolean isInMemory() {
        return this.attribute.isInMemory();
    }

    @Override
    public long length() {
        return this.attribute.length();
    }

    @Override
    public boolean renameTo(File file) throws IOException {
        return this.attribute.renameTo(file);
    }

    @Override
    public void setCharset(Charset charset) {
        this.attribute.setCharset(charset);
    }

    @Override
    public void setContent(ByteBuf bytebuf) throws IOException {
        if ((long) bytebuf.readableBytes() > this.limitSize && this.attribute instanceof MemoryAttribute) {
            this.attribute = new DiskAttribute(this.attribute.getName());
        }

        this.attribute.setContent(bytebuf);
    }

    @Override
    public void setContent(File file) throws IOException {
        if (file.length() > this.limitSize && this.attribute instanceof MemoryAttribute) {
            this.attribute = new DiskAttribute(this.attribute.getName());
        }

        this.attribute.setContent(file);
    }

    @Override
    public void setContent(InputStream inputstream) throws IOException {
        if (this.attribute instanceof MemoryAttribute) {
            this.attribute = new DiskAttribute(this.attribute.getName());
        }

        this.attribute.setContent(inputstream);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return this.attribute.getHttpDataType();
    }

    @Override
    public String getName() {
        return this.attribute.getName();
    }

    public int compareTo(InterfaceHttpData interfacehttpdata) {
        return this.attribute.compareTo(interfacehttpdata);
    }

    public String toString() {
        return "Mixed: " + this.attribute.toString();
    }

    @Override
    public String getValue() throws IOException {
        return this.attribute.getValue();
    }

    @Override
    public void setValue(String s) throws IOException {
        this.attribute.setValue(s);
    }

    @Override
    public ByteBuf getChunk(int i) throws IOException {
        return this.attribute.getChunk(i);
    }

    @Override
    public File getFile() throws IOException {
        return this.attribute.getFile();
    }

    @Override
    public Attribute copy() {
        return this.attribute.copy();
    }

    @Override
    public Attribute duplicate() {
        return this.attribute.duplicate();
    }

    @Override
    public ByteBuf content() {
        return this.attribute.content();
    }

    @Override
    public int refCnt() {
        return this.attribute.refCnt();
    }

    @Override
    public Attribute retain() {
        this.attribute.retain();
        return this;
    }

    @Override
    public Attribute retain(int i) {
        this.attribute.retain(i);
        return this;
    }

    @Override
    public boolean release() {
        return this.attribute.release();
    }

    @Override
    public boolean release(int i) {
        return this.attribute.release(i);
    }
}
