package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.FileRegion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.WritableByteChannel;

public abstract class OioByteStreamChannel extends AbstractOioByteChannel {

    private static final InputStream CLOSED_IN = new InputStream() {
        public int read() {
            return -1;
        }
    };
    private static final OutputStream CLOSED_OUT = new OutputStream() {
        public void write(int i) throws IOException {
            throw new ClosedChannelException();
        }
    };
    private InputStream is;
    private OutputStream os;
    private WritableByteChannel outChannel;

    protected OioByteStreamChannel(Channel channel) {
        super(channel);
    }

    protected final void activate(InputStream inputstream, OutputStream outputstream) {
        if (this.is != null) {
            throw new IllegalStateException("input was set already");
        } else if (this.os != null) {
            throw new IllegalStateException("output was set already");
        } else if (inputstream == null) {
            throw new NullPointerException("is");
        } else if (outputstream == null) {
            throw new NullPointerException("os");
        } else {
            this.is = inputstream;
            this.os = outputstream;
        }
    }

    @Override
    public boolean isActive() {
        InputStream inputstream = this.is;

        if (inputstream != null && inputstream != OioByteStreamChannel.CLOSED_IN) {
            OutputStream outputstream = this.os;

            return outputstream != null && outputstream != OioByteStreamChannel.CLOSED_OUT;
        } else {
            return false;
        }
    }

    @Override
    protected int available() {
        try {
            return this.is.available();
        } catch (IOException ioexception) {
            return 0;
        }
    }

    @Override
    protected int doReadBytes(ByteBuf bytebuf) throws Exception {
        int i = Math.max(1, Math.min(this.available(), bytebuf.maxWritableBytes()));

        return bytebuf.writeBytes(this.is, i);
    }

    @Override
    protected void doWriteBytes(ByteBuf bytebuf) throws Exception {
        OutputStream outputstream = this.os;

        if (outputstream == null) {
            throw new NotYetConnectedException();
        } else {
            bytebuf.readBytes(outputstream, bytebuf.readableBytes());
        }
    }

    @Override
    protected void doWriteFileRegion(FileRegion fileregion) throws Exception {
        OutputStream outputstream = this.os;

        if (outputstream == null) {
            throw new NotYetConnectedException();
        } else {
            if (this.outChannel == null) {
                this.outChannel = Channels.newChannel(outputstream);
            }

            long i = 0L;

            do {
                long j = fileregion.transferTo(this.outChannel, i);

                if (j == -1L) {
                    checkEOF(fileregion);
                    return;
                }

                i += j;
            } while (i < fileregion.count());

        }
    }

    @Override
    protected void doClose() throws Exception {
        InputStream inputstream = this.is;
        OutputStream outputstream = this.os;

        this.is = OioByteStreamChannel.CLOSED_IN;
        this.os = OioByteStreamChannel.CLOSED_OUT;

        try {
            if (inputstream != null) {
                inputstream.close();
            }
        } finally {
            if (outputstream != null) {
                outputstream.close();
            }

        }

    }
}
