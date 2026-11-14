package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

@Beta
public final class ByteStreams {

    private static final int BUF_SIZE = 4096;
    private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
        public void write(int i) {}

        public void write(byte[] abyte) {
            Preconditions.checkNotNull(abyte);
        }

        public void write(byte[] abyte, int i, int j) {
            Preconditions.checkNotNull(abyte);
        }

        public String toString() {
            return "ByteStreams.nullOutputStream()";
        }
    };

    private ByteStreams() {}

    /** @deprecated */
    @Deprecated
    public static InputSupplier<ByteArrayInputStream> newInputStreamSupplier(byte[] abyte) {
        return asInputSupplier(ByteSource.wrap(abyte));
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<ByteArrayInputStream> newInputStreamSupplier(byte[] abyte, int i, int j) {
        return asInputSupplier(ByteSource.wrap(abyte).slice((long) i, (long) j));
    }

    /** @deprecated */
    @Deprecated
    public static void write(byte[] abyte, OutputSupplier<? extends OutputStream> outputsupplier) throws IOException {
        asByteSink(outputsupplier).write(abyte);
    }

    /** @deprecated */
    @Deprecated
    public static long copy(InputSupplier<? extends InputStream> inputsupplier, OutputSupplier<? extends OutputStream> outputsupplier) throws IOException {
        return asByteSource(inputsupplier).copyTo(asByteSink(outputsupplier));
    }

    /** @deprecated */
    @Deprecated
    public static long copy(InputSupplier<? extends InputStream> inputsupplier, OutputStream outputstream) throws IOException {
        return asByteSource(inputsupplier).copyTo(outputstream);
    }

    /** @deprecated */
    @Deprecated
    public static long copy(InputStream inputstream, OutputSupplier<? extends OutputStream> outputsupplier) throws IOException {
        return asByteSink(outputsupplier).writeFrom(inputstream);
    }

    public static long copy(InputStream inputstream, OutputStream outputstream) throws IOException {
        Preconditions.checkNotNull(inputstream);
        Preconditions.checkNotNull(outputstream);
        byte[] abyte = new byte[4096];
        long i = 0L;

        while (true) {
            int j = inputstream.read(abyte);

            if (j == -1) {
                return i;
            }

            outputstream.write(abyte, 0, j);
            i += (long) j;
        }
    }

    public static long copy(ReadableByteChannel readablebytechannel, WritableByteChannel writablebytechannel) throws IOException {
        Preconditions.checkNotNull(readablebytechannel);
        Preconditions.checkNotNull(writablebytechannel);
        ByteBuffer bytebuffer = ByteBuffer.allocate(4096);
        long i = 0L;

        while (readablebytechannel.read(bytebuffer) != -1) {
            bytebuffer.flip();

            while (bytebuffer.hasRemaining()) {
                i += (long) writablebytechannel.write(bytebuffer);
            }

            bytebuffer.clear();
        }

        return i;
    }

    public static byte[] toByteArray(InputStream inputstream) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

        copy(inputstream, (OutputStream) bytearrayoutputstream);
        return bytearrayoutputstream.toByteArray();
    }

    static byte[] toByteArray(InputStream inputstream, int i) throws IOException {
        byte[] abyte = new byte[i];

        int j;
        int k;

        for (int l = i; l > 0; l -= k) {
            j = i - l;
            k = inputstream.read(abyte, j, l);
            if (k == -1) {
                return Arrays.copyOf(abyte, j);
            }
        }

        j = inputstream.read();
        if (j == -1) {
            return abyte;
        } else {
            ByteStreams.FastByteArrayOutputStream bytestreams_fastbytearrayoutputstream = new ByteStreams.FastByteArrayOutputStream();

            bytestreams_fastbytearrayoutputstream.write(j);
            copy(inputstream, (OutputStream) bytestreams_fastbytearrayoutputstream);
            byte[] abyte1 = new byte[abyte.length + bytestreams_fastbytearrayoutputstream.size()];

            System.arraycopy(abyte, 0, abyte1, 0, abyte.length);
            bytestreams_fastbytearrayoutputstream.writeTo(abyte1, abyte.length);
            return abyte1;
        }
    }

    /** @deprecated */
    @Deprecated
    public static byte[] toByteArray(InputSupplier<? extends InputStream> inputsupplier) throws IOException {
        return asByteSource(inputsupplier).read();
    }

    public static ByteArrayDataInput newDataInput(byte[] abyte) {
        return new ByteStreams.ByteArrayDataInputStream(abyte);
    }

    public static ByteArrayDataInput newDataInput(byte[] abyte, int i) {
        Preconditions.checkPositionIndex(i, abyte.length);
        return new ByteStreams.ByteArrayDataInputStream(abyte, i);
    }

    public static ByteArrayDataOutput newDataOutput() {
        return new ByteStreams.ByteArrayDataOutputStream();
    }

    public static ByteArrayDataOutput newDataOutput(int i) {
        Preconditions.checkArgument(i >= 0, "Invalid size: %s", i);
        return new ByteStreams.ByteArrayDataOutputStream(i);
    }

    public static OutputStream nullOutputStream() {
        return ByteStreams.NULL_OUTPUT_STREAM;
    }

    public static InputStream limit(InputStream inputstream, long i) {
        return new ByteStreams.LimitedInputStream(inputstream, i);
    }

    /** @deprecated */
    @Deprecated
    public static long length(InputSupplier<? extends InputStream> inputsupplier) throws IOException {
        return asByteSource(inputsupplier).size();
    }

    /** @deprecated */
    @Deprecated
    public static boolean equal(InputSupplier<? extends InputStream> inputsupplier, InputSupplier<? extends InputStream> inputsupplier1) throws IOException {
        return asByteSource(inputsupplier).contentEquals(asByteSource(inputsupplier1));
    }

    public static void readFully(InputStream inputstream, byte[] abyte) throws IOException {
        readFully(inputstream, abyte, 0, abyte.length);
    }

    public static void readFully(InputStream inputstream, byte[] abyte, int i, int j) throws IOException {
        int k = read(inputstream, abyte, i, j);

        if (k != j) {
            throw new EOFException("reached end of stream after reading " + k + " bytes; " + j + " bytes expected");
        }
    }

    public static void skipFully(InputStream inputstream, long i) throws IOException {
        long j = i;

        while (i > 0L) {
            long k = inputstream.skip(i);

            if (k == 0L) {
                if (inputstream.read() == -1) {
                    long l = j - i;

                    throw new EOFException("reached end of stream after skipping " + l + " bytes; " + j + " bytes expected");
                }

                --i;
            } else {
                i -= k;
            }
        }

    }

    /** @deprecated */
    @Deprecated
    public static <T> T readBytes(InputSupplier<? extends InputStream> inputsupplier, ByteProcessor<T> byteprocessor) throws IOException {
        Preconditions.checkNotNull(inputsupplier);
        Preconditions.checkNotNull(byteprocessor);
        Closer closer = Closer.create();

        Object object;

        try {
            InputStream inputstream = (InputStream) closer.register((Closeable) inputsupplier.getInput());

            object = readBytes(inputstream, byteprocessor);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return object;
    }

    public static <T> T readBytes(InputStream inputstream, ByteProcessor<T> byteprocessor) throws IOException {
        Preconditions.checkNotNull(inputstream);
        Preconditions.checkNotNull(byteprocessor);
        byte[] abyte = new byte[4096];

        int i;

        do {
            i = inputstream.read(abyte);
        } while (i != -1 && byteprocessor.processBytes(abyte, 0, i));

        return byteprocessor.getResult();
    }

    /** @deprecated */
    @Deprecated
    public static HashCode hash(InputSupplier<? extends InputStream> inputsupplier, HashFunction hashfunction) throws IOException {
        return asByteSource(inputsupplier).hash(hashfunction);
    }

    public static int read(InputStream inputstream, byte[] abyte, int i, int j) throws IOException {
        Preconditions.checkNotNull(inputstream);
        Preconditions.checkNotNull(abyte);
        if (j < 0) {
            throw new IndexOutOfBoundsException("len is negative");
        } else {
            int k;
            int l;

            for (l = 0; l < j; l += k) {
                k = inputstream.read(abyte, i + l, j - l);
                if (k == -1) {
                    break;
                }
            }

            return l;
        }
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<InputStream> slice(InputSupplier<? extends InputStream> inputsupplier, long i, long j) {
        return asInputSupplier(asByteSource(inputsupplier).slice(i, j));
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<InputStream> join(Iterable<? extends InputSupplier<? extends InputStream>> iterable) {
        Preconditions.checkNotNull(iterable);
        Iterable iterable1 = Iterables.transform(iterable, new Function<InputSupplier<? extends InputStream>, ByteSource>() {
            public ByteSource apply(InputSupplier<? extends InputStream> inputsupplier) {
                return ByteStreams.asByteSource(inputsupplier);
            }
        });

        return asInputSupplier(ByteSource.concat(iterable1));
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<InputStream> join(InputSupplier<? extends InputStream>... ainputsupplier) {
        return join((Iterable) Arrays.asList(ainputsupplier));
    }

    /** @deprecated */
    @Deprecated
    public static ByteSource asByteSource(final InputSupplier<? extends InputStream> inputsupplier) {
        Preconditions.checkNotNull(inputsupplier);
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return (InputStream) inputsupplier.getInput();
            }

            public String toString() {
                return "ByteStreams.asByteSource(" + inputsupplier + ")";
            }
        };
    }

    /** @deprecated */
    @Deprecated
    public static ByteSink asByteSink(final OutputSupplier<? extends OutputStream> outputsupplier) {
        Preconditions.checkNotNull(outputsupplier);
        return new ByteSink() {
            @Override
            public OutputStream openStream() throws IOException {
                return (OutputStream) outputsupplier.getOutput();
            }

            public String toString() {
                return "ByteStreams.asByteSink(" + outputsupplier + ")";
            }
        };
    }

    static <S extends InputStream> InputSupplier<S> asInputSupplier(ByteSource bytesource) {
        return (InputSupplier) Preconditions.checkNotNull(bytesource);
    }

    static <S extends OutputStream> OutputSupplier<S> asOutputSupplier(ByteSink bytesink) {
        return (OutputSupplier) Preconditions.checkNotNull(bytesink);
    }

    private static final class LimitedInputStream extends FilterInputStream {

        private long left;
        private long mark = -1L;

        LimitedInputStream(InputStream inputstream, long i) {
            super(inputstream);
            Preconditions.checkNotNull(inputstream);
            Preconditions.checkArgument(i >= 0L, "limit must be non-negative");
            this.left = i;
        }

        public int available() throws IOException {
            return (int) Math.min((long) this.in.available(), this.left);
        }

        public synchronized void mark(int i) {
            this.in.mark(i);
            this.mark = this.left;
        }

        public int read() throws IOException {
            if (this.left == 0L) {
                return -1;
            } else {
                int i = this.in.read();

                if (i != -1) {
                    --this.left;
                }

                return i;
            }
        }

        public int read(byte[] abyte, int i, int j) throws IOException {
            if (this.left == 0L) {
                return -1;
            } else {
                j = (int) Math.min((long) j, this.left);
                int k = this.in.read(abyte, i, j);

                if (k != -1) {
                    this.left -= (long) k;
                }

                return k;
            }
        }

        public synchronized void reset() throws IOException {
            if (!this.in.markSupported()) {
                throw new IOException("Mark not supported");
            } else if (this.mark == -1L) {
                throw new IOException("Mark not set");
            } else {
                this.in.reset();
                this.left = this.mark;
            }
        }

        public long skip(long i) throws IOException {
            i = Math.min(i, this.left);
            long j = this.in.skip(i);

            this.left -= j;
            return j;
        }
    }

    private static class ByteArrayDataOutputStream implements ByteArrayDataOutput {

        final DataOutput output;
        final ByteArrayOutputStream byteArrayOutputSteam;

        ByteArrayDataOutputStream() {
            this(new ByteArrayOutputStream());
        }

        ByteArrayDataOutputStream(int i) {
            this(new ByteArrayOutputStream(i));
        }

        ByteArrayDataOutputStream(ByteArrayOutputStream bytearrayoutputstream) {
            this.byteArrayOutputSteam = bytearrayoutputstream;
            this.output = new DataOutputStream(bytearrayoutputstream);
        }

        @Override
        public void write(int i) {
            try {
                this.output.write(i);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void write(byte[] abyte) {
            try {
                this.output.write(abyte);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void write(byte[] abyte, int i, int j) {
            try {
                this.output.write(abyte, i, j);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeBoolean(boolean flag) {
            try {
                this.output.writeBoolean(flag);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeByte(int i) {
            try {
                this.output.writeByte(i);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeBytes(String s) {
            try {
                this.output.writeBytes(s);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeChar(int i) {
            try {
                this.output.writeChar(i);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeChars(String s) {
            try {
                this.output.writeChars(s);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeDouble(double d0) {
            try {
                this.output.writeDouble(d0);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeFloat(float f) {
            try {
                this.output.writeFloat(f);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeInt(int i) {
            try {
                this.output.writeInt(i);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeLong(long i) {
            try {
                this.output.writeLong(i);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeShort(int i) {
            try {
                this.output.writeShort(i);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public void writeUTF(String s) {
            try {
                this.output.writeUTF(s);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public byte[] toByteArray() {
            return this.byteArrayOutputSteam.toByteArray();
        }
    }

    private static class ByteArrayDataInputStream implements ByteArrayDataInput {

        final DataInput input;

        ByteArrayDataInputStream(byte[] abyte) {
            this.input = new DataInputStream(new ByteArrayInputStream(abyte));
        }

        ByteArrayDataInputStream(byte[] abyte, int i) {
            this.input = new DataInputStream(new ByteArrayInputStream(abyte, i, abyte.length - i));
        }

        @Override
        public void readFully(byte[] abyte) {
            try {
                this.input.readFully(abyte);
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public void readFully(byte[] abyte, int i, int j) {
            try {
                this.input.readFully(abyte, i, j);
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public int skipBytes(int i) {
            try {
                return this.input.skipBytes(i);
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public boolean readBoolean() {
            try {
                return this.input.readBoolean();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public byte readByte() {
            try {
                return this.input.readByte();
            } catch (EOFException eofexception) {
                throw new IllegalStateException(eofexception);
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Override
        public int readUnsignedByte() {
            try {
                return this.input.readUnsignedByte();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public short readShort() {
            try {
                return this.input.readShort();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public int readUnsignedShort() {
            try {
                return this.input.readUnsignedShort();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public char readChar() {
            try {
                return this.input.readChar();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public int readInt() {
            try {
                return this.input.readInt();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public long readLong() {
            try {
                return this.input.readLong();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public float readFloat() {
            try {
                return this.input.readFloat();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public double readDouble() {
            try {
                return this.input.readDouble();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public String readLine() {
            try {
                return this.input.readLine();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }

        @Override
        public String readUTF() {
            try {
                return this.input.readUTF();
            } catch (IOException ioexception) {
                throw new IllegalStateException(ioexception);
            }
        }
    }

    private static final class FastByteArrayOutputStream extends ByteArrayOutputStream {

        private FastByteArrayOutputStream() {}

        void writeTo(byte[] abyte, int i) {
            System.arraycopy(this.buf, 0, abyte, i, this.count);
        }
    }
}
