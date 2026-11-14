package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public interface HttpData extends InterfaceHttpData, ByteBufHolder {

    void setContent(ByteBuf bytebuf) throws IOException;

    void addContent(ByteBuf bytebuf, boolean flag) throws IOException;

    void setContent(File file) throws IOException;

    void setContent(InputStream inputstream) throws IOException;

    boolean isCompleted();

    long length();

    void delete();

    byte[] get() throws IOException;

    ByteBuf getByteBuf() throws IOException;

    ByteBuf getChunk(int i) throws IOException;

    String getString() throws IOException;

    String getString(Charset charset) throws IOException;

    void setCharset(Charset charset);

    Charset getCharset();

    boolean renameTo(File file) throws IOException;

    boolean isInMemory();

    File getFile() throws IOException;

    @Override
    HttpData copy();

    @Override
    HttpData duplicate();

    @Override
    HttpData retain();

    @Override
    HttpData retain(int i);
}
