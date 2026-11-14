package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractMemoryHttpData extends AbstractHttpData {

    private ByteBuf byteBuf;
    private int chunkPosition;
    protected boolean isRenamed;

    protected AbstractMemoryHttpData(String s, Charset charset, long i) {
        super(s, charset, i);
    }

    @Override
    public void setContent(ByteBuf bytebuf) throws IOException {
        if (bytebuf == null) {
            throw new NullPointerException("buffer");
        } else {
            long i = (long) bytebuf.readableBytes();

            if (this.definedSize > 0L && this.definedSize < i) {
                throw new IOException("Out of size: " + i + " > " + this.definedSize);
            } else {
                if (this.byteBuf != null) {
                    this.byteBuf.release();
                }

                this.byteBuf = bytebuf;
                this.size = i;
                this.completed = true;
            }
        }
    }

    @Override
    public void setContent(InputStream inputstream) throws IOException {
        if (inputstream == null) {
            throw new NullPointerException("inputStream");
        } else {
            ByteBuf bytebuf = Unpooled.buffer();
            byte[] abyte = new byte[16384];
            int i = inputstream.read(abyte);

            int j;

            for (j = 0; i > 0; i = inputstream.read(abyte)) {
                bytebuf.writeBytes(abyte, 0, i);
                j += i;
            }

            this.size = (long) j;
            if (this.definedSize > 0L && this.definedSize < this.size) {
                throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
            } else {
                if (this.byteBuf != null) {
                    this.byteBuf.release();
                }

                this.byteBuf = bytebuf;
                this.completed = true;
            }
        }
    }

    @Override
    public void addContent(ByteBuf bytebuf, boolean flag) throws IOException {
        if (bytebuf != null) {
            long i = (long) bytebuf.readableBytes();

            if (this.definedSize > 0L && this.definedSize < this.size + i) {
                throw new IOException("Out of size: " + (this.size + i) + " > " + this.definedSize);
            }

            this.size += i;
            if (this.byteBuf == null) {
                this.byteBuf = bytebuf;
            } else {
                CompositeByteBuf compositebytebuf;

                if (this.byteBuf instanceof CompositeByteBuf) {
                    compositebytebuf = (CompositeByteBuf) this.byteBuf;
                    compositebytebuf.addComponent(bytebuf);
                    compositebytebuf.writerIndex(compositebytebuf.writerIndex() + bytebuf.readableBytes());
                } else {
                    compositebytebuf = Unpooled.compositeBuffer(Integer.MAX_VALUE);
                    compositebytebuf.addComponents(this.byteBuf, bytebuf);
                    compositebytebuf.writerIndex(this.byteBuf.readableBytes() + bytebuf.readableBytes());
                    this.byteBuf = compositebytebuf;
                }
            }
        }

        if (flag) {
            this.completed = true;
        } else if (bytebuf == null) {
            throw new NullPointerException("buffer");
        }

    }

    @Override
    public void setContent(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file");
        } else {
            long i = file.length();

            if (i > 2147483647L) {
                throw new IllegalArgumentException("File too big to be loaded in memory");
            } else {
                FileInputStream fileinputstream = new FileInputStream(file);
                FileChannel filechannel = fileinputstream.getChannel();
                byte[] abyte = new byte[(int) i];
                ByteBuffer bytebuffer = ByteBuffer.wrap(abyte);

                for (int j = 0; (long) j < i; j += filechannel.read(bytebuffer)) {
                    ;
                }

                filechannel.close();
                fileinputstream.close();
                bytebuffer.flip();
                if (this.byteBuf != null) {
                    this.byteBuf.release();
                }

                this.byteBuf = Unpooled.wrappedBuffer(Integer.MAX_VALUE, bytebuffer);
                this.size = i;
                this.completed = true;
            }
        }
    }

    @Override
    public void delete() {}

    @Override
    public byte[] get() {
        if (this.byteBuf == null) {
            return Unpooled.EMPTY_BUFFER.array();
        } else {
            byte[] abyte = new byte[this.byteBuf.readableBytes()];

            this.byteBuf.getBytes(this.byteBuf.readerIndex(), abyte);
            return abyte;
        }
    }

    @Override
    public String getString() {
        return this.getString(HttpConstants.DEFAULT_CHARSET);
    }

    @Override
    public String getString(Charset charset) {
        if (this.byteBuf == null) {
            return "";
        } else {
            if (charset == null) {
                charset = HttpConstants.DEFAULT_CHARSET;
            }

            return this.byteBuf.toString(charset);
        }
    }

    @Override
    public ByteBuf getByteBuf() {
        return this.byteBuf;
    }

    @Override
    public ByteBuf getChunk(int i) throws IOException {
        if (this.byteBuf != null && i != 0 && this.byteBuf.readableBytes() != 0) {
            int j = this.byteBuf.readableBytes() - this.chunkPosition;

            if (j == 0) {
                this.chunkPosition = 0;
                return Unpooled.EMPTY_BUFFER;
            } else {
                int k = i;

                if (j < i) {
                    k = j;
                }

                ByteBuf bytebuf = this.byteBuf.slice(this.chunkPosition, k);

                this.chunkPosition += k;
                return bytebuf;
            }
        } else {
            this.chunkPosition = 0;
            return Unpooled.EMPTY_BUFFER;
        }
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public boolean renameTo(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("dest");
        } else if (this.byteBuf == null) {
            file.createNewFile();
            this.isRenamed = true;
            return true;
        } else {
            int i = this.byteBuf.readableBytes();
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            FileChannel filechannel = fileoutputstream.getChannel();
            int j = 0;

            if (this.byteBuf.nioBufferCount() == 1) {
                for (ByteBuffer bytebuffer = this.byteBuf.nioBuffer(); j < i; j += filechannel.write(bytebuffer)) {
                    ;
                }
            } else {
                for (ByteBuffer[] abytebuffer = this.byteBuf.nioBuffers(); j < i; j = (int) ((long) j + filechannel.write(abytebuffer))) {
                    ;
                }
            }

            filechannel.force(false);
            filechannel.close();
            fileoutputstream.close();
            this.isRenamed = true;
            return j == i;
        }
    }

    @Override
    public File getFile() throws IOException {
        throw new IOException("Not represented by a file");
    }
}
