package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public abstract class ByteSink implements OutputSupplier<OutputStream> {

    protected ByteSink() {}

    public CharSink asCharSink(Charset charset) {
        return new ByteSink.AsCharSink(charset);
    }

    public abstract OutputStream openStream() throws IOException;

    /** @deprecated */
    @Deprecated
    @Override
    public final OutputStream getOutput() throws IOException {
        return this.openStream();
    }

    public OutputStream openBufferedStream() throws IOException {
        OutputStream outputstream = this.openStream();

        return outputstream instanceof BufferedOutputStream ? (BufferedOutputStream) outputstream : new BufferedOutputStream(outputstream);
    }

    public void write(byte[] abyte) throws IOException {
        Preconditions.checkNotNull(abyte);
        Closer closer = Closer.create();

        try {
            OutputStream outputstream = (OutputStream) closer.register(this.openStream());

            outputstream.write(abyte);
            outputstream.flush();
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

    }

    public long writeFrom(InputStream inputstream) throws IOException {
        Preconditions.checkNotNull(inputstream);
        Closer closer = Closer.create();

        long i;

        try {
            OutputStream outputstream = (OutputStream) closer.register(this.openStream());
            long j = ByteStreams.copy(inputstream, outputstream);

            outputstream.flush();
            i = j;
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return i;
    }

    private final class AsCharSink extends CharSink {

        private final Charset charset;

        private AsCharSink(Charset charset) {
            this.charset = (Charset) Preconditions.checkNotNull(charset);
        }

        @Override
        public Writer openStream() throws IOException {
            return new OutputStreamWriter(ByteSink.this.openStream(), this.charset);
        }

        public String toString() {
            return ByteSink.this.toString() + ".asCharSink(" + this.charset + ")";
        }
    }
}
