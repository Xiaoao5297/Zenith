package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.internal.EmptyArrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractDiskHttpData extends AbstractHttpData {

    protected File file;
    private boolean isRenamed;
    private FileChannel fileChannel;

    protected AbstractDiskHttpData(String s, Charset charset, long i) {
        super(s, charset, i);
    }

    protected abstract String getDiskFilename();

    protected abstract String getPrefix();

    protected abstract String getBaseDirectory();

    protected abstract String getPostfix();

    protected abstract boolean deleteOnExit();

    private File tempFile() throws IOException {
        String s = this.getDiskFilename();
        String s1;

        if (s != null) {
            s1 = '_' + s;
        } else {
            s1 = this.getPostfix();
        }

        File file;

        if (this.getBaseDirectory() == null) {
            file = File.createTempFile(this.getPrefix(), s1);
        } else {
            file = File.createTempFile(this.getPrefix(), s1, new File(this.getBaseDirectory()));
        }

        if (this.deleteOnExit()) {
            file.deleteOnExit();
        }

        return file;
    }

    @Override
    public void setContent(ByteBuf bytebuf) throws IOException {
        if (bytebuf == null) {
            throw new NullPointerException("buffer");
        } else {
            try {
                this.size = (long) bytebuf.readableBytes();
                if (this.definedSize > 0L && this.definedSize < this.size) {
                    throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
                }

                if (this.file == null) {
                    this.file = this.tempFile();
                }

                if (bytebuf.readableBytes() != 0) {
                    FileOutputStream fileoutputstream = new FileOutputStream(this.file);
                    FileChannel filechannel = fileoutputstream.getChannel();
                    ByteBuffer bytebuffer = bytebuf.nioBuffer();

                    int i;

                    for (i = 0; (long) i < this.size; i += filechannel.write(bytebuffer)) {
                        ;
                    }

                    bytebuf.readerIndex(bytebuf.readerIndex() + i);
                    filechannel.force(false);
                    filechannel.close();
                    fileoutputstream.close();
                    this.completed = true;
                    return;
                }

                this.file.createNewFile();
            } finally {
                bytebuf.release();
            }

        }
    }

    @Override
    public void addContent(ByteBuf bytebuf, boolean flag) throws IOException {
        if (bytebuf != null) {
            try {
                int i = bytebuf.readableBytes();

                if (this.definedSize > 0L && this.definedSize < this.size + (long) i) {
                    throw new IOException("Out of size: " + (this.size + (long) i) + " > " + this.definedSize);
                }

                ByteBuffer bytebuffer = bytebuf.nioBufferCount() == 1 ? bytebuf.nioBuffer() : bytebuf.copy().nioBuffer();
                int j = 0;

                if (this.file == null) {
                    this.file = this.tempFile();
                }

                if (this.fileChannel == null) {
                    FileOutputStream fileoutputstream = new FileOutputStream(this.file);

                    this.fileChannel = fileoutputstream.getChannel();
                }

                while (j < i) {
                    j += this.fileChannel.write(bytebuffer);
                }

                this.size += (long) i;
                bytebuf.readerIndex(bytebuf.readerIndex() + j);
            } finally {
                bytebuf.release();
            }
        }

        if (flag) {
            if (this.file == null) {
                this.file = this.tempFile();
            }

            if (this.fileChannel == null) {
                FileOutputStream fileoutputstream1 = new FileOutputStream(this.file);

                this.fileChannel = fileoutputstream1.getChannel();
            }

            this.fileChannel.force(false);
            this.fileChannel.close();
            this.fileChannel = null;
            this.completed = true;
        } else if (bytebuf == null) {
            throw new NullPointerException("buffer");
        }

    }

    @Override
    public void setContent(File file) throws IOException {
        if (this.file != null) {
            this.delete();
        }

        this.file = file;
        this.size = file.length();
        this.isRenamed = true;
        this.completed = true;
    }

    @Override
    public void setContent(InputStream inputstream) throws IOException {
        if (inputstream == null) {
            throw new NullPointerException("inputStream");
        } else {
            if (this.file != null) {
                this.delete();
            }

            this.file = this.tempFile();
            FileOutputStream fileoutputstream = new FileOutputStream(this.file);
            FileChannel filechannel = fileoutputstream.getChannel();
            byte[] abyte = new byte[16384];
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte);
            int i = inputstream.read(abyte);

            int j;

            for (j = 0; i > 0; i = inputstream.read(abyte)) {
                bytebuffer.position(i).flip();
                j += filechannel.write(bytebuffer);
            }

            filechannel.force(false);
            filechannel.close();
            this.size = (long) j;
            if (this.definedSize > 0L && this.definedSize < this.size) {
                this.file.delete();
                this.file = null;
                throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
            } else {
                this.isRenamed = true;
                this.completed = true;
            }
        }
    }

    @Override
    public void delete() {
        if (!this.isRenamed && this.file != null) {
            this.file.delete();
        }

    }

    @Override
    public byte[] get() throws IOException {
        return this.file == null ? EmptyArrays.EMPTY_BYTES : readFrom(this.file);
    }

    @Override
    public ByteBuf getByteBuf() throws IOException {
        if (this.file == null) {
            return Unpooled.EMPTY_BUFFER;
        } else {
            byte[] abyte = readFrom(this.file);

            return Unpooled.wrappedBuffer(abyte);
        }
    }

    @Override
    public ByteBuf getChunk(int i) throws IOException {
        if (this.file != null && i != 0) {
            if (this.fileChannel == null) {
                FileInputStream fileinputstream = new FileInputStream(this.file);

                this.fileChannel = fileinputstream.getChannel();
            }

            int j = 0;

            int k;
            ByteBuffer bytebuffer;

            for (bytebuffer = ByteBuffer.allocate(i); j < i; j += k) {
                k = this.fileChannel.read(bytebuffer);
                if (k == -1) {
                    this.fileChannel.close();
                    this.fileChannel = null;
                    break;
                }
            }

            if (j == 0) {
                return Unpooled.EMPTY_BUFFER;
            } else {
                bytebuffer.flip();
                ByteBuf bytebuf = Unpooled.wrappedBuffer(bytebuffer);

                bytebuf.readerIndex(0);
                bytebuf.writerIndex(j);
                return bytebuf;
            }
        } else {
            return Unpooled.EMPTY_BUFFER;
        }
    }

    @Override
    public String getString() throws IOException {
        return this.getString(HttpConstants.DEFAULT_CHARSET);
    }

    @Override
    public String getString(Charset charset) throws IOException {
        if (this.file == null) {
            return "";
        } else {
            byte[] abyte;

            if (charset == null) {
                abyte = readFrom(this.file);
                return new String(abyte, HttpConstants.DEFAULT_CHARSET.name());
            } else {
                abyte = readFrom(this.file);
                return new String(abyte, charset.name());
            }
        }
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public boolean renameTo(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("dest");
        } else if (!this.file.renameTo(file)) {
            FileInputStream fileinputstream = new FileInputStream(this.file);
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            FileChannel filechannel = fileinputstream.getChannel();
            FileChannel filechannel1 = fileoutputstream.getChannel();
            int i = 8196;

            long j;

            for (j = 0L; j < this.size; j += filechannel.transferTo(j, (long) i, filechannel1)) {
                if ((long) i < this.size - j) {
                    i = (int) (this.size - j);
                }
            }

            filechannel.close();
            filechannel1.close();
            if (j == this.size) {
                this.file.delete();
                this.file = file;
                this.isRenamed = true;
                return true;
            } else {
                file.delete();
                return false;
            }
        } else {
            this.file = file;
            this.isRenamed = true;
            return true;
        }
    }

    private static byte[] readFrom(File file) throws IOException {
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
            return abyte;
        }
    }

    @Override
    public File getFile() throws IOException {
        return this.file;
    }
}
