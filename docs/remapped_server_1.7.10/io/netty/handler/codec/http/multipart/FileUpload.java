package io.netty.handler.codec.http.multipart;

public interface FileUpload extends HttpData {

    String getFilename();

    void setFilename(String s);

    void setContentType(String s);

    String getContentType();

    void setContentTransferEncoding(String s);

    String getContentTransferEncoding();

    @Override
    FileUpload copy();

    @Override
    FileUpload duplicate();

    @Override
    FileUpload retain();

    @Override
    FileUpload retain(int i);
}
