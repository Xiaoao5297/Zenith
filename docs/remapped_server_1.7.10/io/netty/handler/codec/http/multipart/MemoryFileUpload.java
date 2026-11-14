package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import java.io.IOException;
import java.nio.charset.Charset;

public class MemoryFileUpload extends AbstractMemoryHttpData implements FileUpload {

    private String filename;
    private String contentType;
    private String contentTransferEncoding;

    public MemoryFileUpload(String s, String s1, String s2, String s3, Charset charset, long i) {
        super(s, charset, i);
        this.setFilename(s1);
        this.setContentType(s2);
        this.setContentTransferEncoding(s3);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.FileUpload;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public void setFilename(String s) {
        if (s == null) {
            throw new NullPointerException("filename");
        } else {
            this.filename = s;
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
        if (!(interfacehttpdata instanceof FileUpload)) {
            throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + interfacehttpdata.getHttpDataType());
        } else {
            return this.compareTo((FileUpload) interfacehttpdata);
        }
    }

    public int compareTo(FileUpload fileupload) {
        int i = this.getName().compareToIgnoreCase(fileupload.getName());

        return i != 0 ? i : i;
    }

    @Override
    public void setContentType(String s) {
        if (s == null) {
            throw new NullPointerException("contentType");
        } else {
            this.contentType = s;
        }
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getContentTransferEncoding() {
        return this.contentTransferEncoding;
    }

    @Override
    public void setContentTransferEncoding(String s) {
        this.contentTransferEncoding = s;
    }

    public String toString() {
        return "Content-Disposition: form-data; name=\"" + this.getName() + "\"; " + "filename" + "=\"" + this.filename + "\"\r\n" + "Content-Type" + ": " + this.contentType + (this.charset != null ? "; charset=" + this.charset + "\r\n" : "\r\n") + "Content-Length" + ": " + this.length() + "\r\n" + "Completed: " + this.isCompleted() + "\r\nIsInMemory: " + this.isInMemory();
    }

    @Override
    public FileUpload copy() {
        MemoryFileUpload memoryfileupload = new MemoryFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size);
        ByteBuf bytebuf = this.content();

        if (bytebuf != null) {
            try {
                memoryfileupload.setContent(bytebuf.copy());
                return memoryfileupload;
            } catch (IOException ioexception) {
                throw new ChannelException(ioexception);
            }
        } else {
            return memoryfileupload;
        }
    }

    @Override
    public FileUpload duplicate() {
        MemoryFileUpload memoryfileupload = new MemoryFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size);
        ByteBuf bytebuf = this.content();

        if (bytebuf != null) {
            try {
                memoryfileupload.setContent(bytebuf.duplicate());
                return memoryfileupload;
            } catch (IOException ioexception) {
                throw new ChannelException(ioexception);
            }
        } else {
            return memoryfileupload;
        }
    }

    @Override
    public FileUpload retain() {
        super.retain();
        return this;
    }

    @Override
    public FileUpload retain(int i) {
        super.retain(i);
        return this;
    }
}
