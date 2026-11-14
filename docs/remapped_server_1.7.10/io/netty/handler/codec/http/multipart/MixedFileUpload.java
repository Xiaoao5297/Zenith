package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedFileUpload implements FileUpload {

    private FileUpload fileUpload;
    private final long limitSize;
    private final long definedSize;

    public MixedFileUpload(String s, String s1, String s2, String s3, Charset charset, long i, long j) {
        this.limitSize = j;
        if (i > this.limitSize) {
            this.fileUpload = new DiskFileUpload(s, s1, s2, s3, charset, i);
        } else {
            this.fileUpload = new MemoryFileUpload(s, s1, s2, s3, charset, i);
        }

        this.definedSize = i;
    }

    @Override
    public void addContent(ByteBuf bytebuf, boolean flag) throws IOException {
        if (this.fileUpload instanceof MemoryFileUpload && this.fileUpload.length() + (long) bytebuf.readableBytes() > this.limitSize) {
            DiskFileUpload diskfileupload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
            ByteBuf bytebuf1 = this.fileUpload.getByteBuf();

            if (bytebuf1 != null && bytebuf1.isReadable()) {
                diskfileupload.addContent(bytebuf1.retain(), false);
            }

            this.fileUpload.release();
            this.fileUpload = diskfileupload;
        }

        this.fileUpload.addContent(bytebuf, flag);
    }

    @Override
    public void delete() {
        this.fileUpload.delete();
    }

    @Override
    public byte[] get() throws IOException {
        return this.fileUpload.get();
    }

    @Override
    public ByteBuf getByteBuf() throws IOException {
        return this.fileUpload.getByteBuf();
    }

    @Override
    public Charset getCharset() {
        return this.fileUpload.getCharset();
    }

    @Override
    public String getContentType() {
        return this.fileUpload.getContentType();
    }

    @Override
    public String getContentTransferEncoding() {
        return this.fileUpload.getContentTransferEncoding();
    }

    @Override
    public String getFilename() {
        return this.fileUpload.getFilename();
    }

    @Override
    public String getString() throws IOException {
        return this.fileUpload.getString();
    }

    @Override
    public String getString(Charset charset) throws IOException {
        return this.fileUpload.getString(charset);
    }

    @Override
    public boolean isCompleted() {
        return this.fileUpload.isCompleted();
    }

    @Override
    public boolean isInMemory() {
        return this.fileUpload.isInMemory();
    }

    @Override
    public long length() {
        return this.fileUpload.length();
    }

    @Override
    public boolean renameTo(File file) throws IOException {
        return this.fileUpload.renameTo(file);
    }

    @Override
    public void setCharset(Charset charset) {
        this.fileUpload.setCharset(charset);
    }

    @Override
    public void setContent(ByteBuf bytebuf) throws IOException {
        if ((long) bytebuf.readableBytes() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
            FileUpload fileupload = this.fileUpload;

            this.fileUpload = new DiskFileUpload(fileupload.getName(), fileupload.getFilename(), fileupload.getContentType(), fileupload.getContentTransferEncoding(), fileupload.getCharset(), this.definedSize);
            fileupload.release();
        }

        this.fileUpload.setContent(bytebuf);
    }

    @Override
    public void setContent(File file) throws IOException {
        if (file.length() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
            FileUpload fileupload = this.fileUpload;

            this.fileUpload = new DiskFileUpload(fileupload.getName(), fileupload.getFilename(), fileupload.getContentType(), fileupload.getContentTransferEncoding(), fileupload.getCharset(), this.definedSize);
            fileupload.release();
        }

        this.fileUpload.setContent(file);
    }

    @Override
    public void setContent(InputStream inputstream) throws IOException {
        if (this.fileUpload instanceof MemoryFileUpload) {
            FileUpload fileupload = this.fileUpload;

            this.fileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
            fileupload.release();
        }

        this.fileUpload.setContent(inputstream);
    }

    @Override
    public void setContentType(String s) {
        this.fileUpload.setContentType(s);
    }

    @Override
    public void setContentTransferEncoding(String s) {
        this.fileUpload.setContentTransferEncoding(s);
    }

    @Override
    public void setFilename(String s) {
        this.fileUpload.setFilename(s);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return this.fileUpload.getHttpDataType();
    }

    @Override
    public String getName() {
        return this.fileUpload.getName();
    }

    public int compareTo(InterfaceHttpData interfacehttpdata) {
        return this.fileUpload.compareTo(interfacehttpdata);
    }

    public String toString() {
        return "Mixed: " + this.fileUpload.toString();
    }

    @Override
    public ByteBuf getChunk(int i) throws IOException {
        return this.fileUpload.getChunk(i);
    }

    @Override
    public File getFile() throws IOException {
        return this.fileUpload.getFile();
    }

    @Override
    public FileUpload copy() {
        return this.fileUpload.copy();
    }

    @Override
    public FileUpload duplicate() {
        return this.fileUpload.duplicate();
    }

    @Override
    public ByteBuf content() {
        return this.fileUpload.content();
    }

    @Override
    public int refCnt() {
        return this.fileUpload.refCnt();
    }

    @Override
    public FileUpload retain() {
        this.fileUpload.retain();
        return this;
    }

    @Override
    public FileUpload retain(int i) {
        this.fileUpload.retain(i);
        return this;
    }

    @Override
    public boolean release() {
        return this.fileUpload.release();
    }

    @Override
    public boolean release(int i) {
        return this.fileUpload.release(i);
    }
}
