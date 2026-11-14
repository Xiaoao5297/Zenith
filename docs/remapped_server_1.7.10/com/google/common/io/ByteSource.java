package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

public abstract class ByteSource implements InputSupplier<InputStream> {

    private static final int BUF_SIZE = 4096;
    private static final byte[] countBuffer = new byte[4096];

    protected ByteSource() {}

    public CharSource asCharSource(Charset charset) {
        return new ByteSource.AsCharSource(charset);
    }

    public abstract InputStream openStream() throws IOException;

    /** @deprecated */
    @Deprecated
    @Override
    public final InputStream getInput() throws IOException {
        return this.openStream();
    }

    public InputStream openBufferedStream() throws IOException {
        InputStream inputstream = this.openStream();

        return inputstream instanceof BufferedInputStream ? (BufferedInputStream) inputstream : new BufferedInputStream(inputstream);
    }

    public ByteSource slice(long i, long j) {
        return new ByteSource.SlicedByteSource(i, j);
    }

    public boolean isEmpty() throws IOException {
        Closer closer = Closer.create();

        boolean flag;

        try {
            InputStream inputstream = (InputStream) closer.register(this.openStream());

            flag = inputstream.read() == -1;
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return flag;
    }

    public long size() throws IOException {
        Closer closer = Closer.create();

        InputStream inputstream;
        long i;

        try {
            inputstream = (InputStream) closer.register(this.openStream());
            i = this.countBySkipping(inputstream);
            return i;
        } catch (IOException ioexception) {
            ;
        } finally {
            closer.close();
        }

        closer = Closer.create();

        try {
            inputstream = (InputStream) closer.register(this.openStream());
            i = this.countByReading(inputstream);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return i;
    }

    private long countBySkipping(InputStream inputstream) throws IOException {
        long i = 0L;

        while (true) {
            while (true) {
                long j = inputstream.skip((long) Math.min(inputstream.available(), Integer.MAX_VALUE));

                if (j <= 0L) {
                    if (inputstream.read() == -1) {
                        return i;
                    }

                    if (i == 0L && inputstream.available() == 0) {
                        throw new IOException();
                    }

                    ++i;
                } else {
                    i += j;
                }
            }
        }
    }

    private long countByReading(InputStream inputstream) throws IOException {
        long i;
        long j;

        for (i = 0L; (j = (long) inputstream.read(ByteSource.countBuffer)) != -1L; i += j) {
            ;
        }

        return i;
    }

    public long copyTo(OutputStream outputstream) throws IOException {
        Preconditions.checkNotNull(outputstream);
        Closer closer = Closer.create();

        long i;

        try {
            InputStream inputstream = (InputStream) closer.register(this.openStream());

            i = ByteStreams.copy(inputstream, outputstream);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return i;
    }

    public long copyTo(ByteSink bytesink) throws IOException {
        Preconditions.checkNotNull(bytesink);
        Closer closer = Closer.create();

        long i;

        try {
            InputStream inputstream = (InputStream) closer.register(this.openStream());
            OutputStream outputstream = (OutputStream) closer.register(bytesink.openStream());

            i = ByteStreams.copy(inputstream, outputstream);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return i;
    }

    public byte[] read() throws IOException {
        Closer closer = Closer.create();

        byte[] abyte;

        try {
            InputStream inputstream = (InputStream) closer.register(this.openStream());

            abyte = ByteStreams.toByteArray(inputstream);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return abyte;
    }

    @Beta
    public <T> T read(ByteProcessor<T> byteprocessor) throws IOException {
        Preconditions.checkNotNull(byteprocessor);
        Closer closer = Closer.create();

        Object object;

        try {
            InputStream inputstream = (InputStream) closer.register(this.openStream());

            object = ByteStreams.readBytes(inputstream, byteprocessor);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return object;
    }

    public HashCode hash(HashFunction hashfunction) throws IOException {
        Hasher hasher = hashfunction.newHasher();

        this.copyTo(Funnels.asOutputStream(hasher));
        return hasher.hash();
    }

    public boolean contentEquals(ByteSource bytesource) throws IOException {
        Preconditions.checkNotNull(bytesource);
        byte[] abyte = new byte[4096];
        byte[] abyte1 = new byte[4096];
        Closer closer = Closer.create();

        boolean flag;

        try {
            InputStream inputstream = (InputStream) closer.register(this.openStream());
            InputStream inputstream1 = (InputStream) closer.register(bytesource.openStream());

            int i;

            do {
                i = ByteStreams.read(inputstream, abyte, 0, 4096);
                int j = ByteStreams.read(inputstream1, abyte1, 0, 4096);

                if (i != j || !Arrays.equals(abyte, abyte1)) {
                    flag = false;
                    return flag;
                }
            } while (i == 4096);

            flag = true;
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return flag;
    }

    public static ByteSource concat(Iterable<? extends ByteSource> iterable) {
        return new ByteSource.ConcatenatedByteSource(iterable);
    }

    public static ByteSource concat(Iterator<? extends ByteSource> iterator) {
        return concat((Iterable) ImmutableList.copyOf(iterator));
    }

    public static ByteSource concat(ByteSource... abytesource) {
        return concat((Iterable) ImmutableList.copyOf((Object[]) abytesource));
    }

    public static ByteSource wrap(byte[] abyte) {
        return new ByteSource.ByteArrayByteSource(abyte);
    }

    public static ByteSource empty() {
        return ByteSource.EmptyByteSource.INSTANCE;
    }

    private static final class ConcatenatedByteSource extends ByteSource {

        private final Iterable<? extends ByteSource> sources;

        ConcatenatedByteSource(Iterable<? extends ByteSource> iterable) {
            this.sources = (Iterable) Preconditions.checkNotNull(iterable);
        }

        @Override
        public InputStream openStream() throws IOException {
            return new MultiInputStream(this.sources.iterator());
        }

        @Override
        public boolean isEmpty() throws IOException {
            Iterator iterator = this.sources.iterator();

            ByteSource bytesource;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                bytesource = (ByteSource) iterator.next();
            } while (bytesource.isEmpty());

            return false;
        }

        @Override
        public long size() throws IOException {
            long i = 0L;

            ByteSource bytesource;

            for (Iterator iterator = this.sources.iterator(); iterator.hasNext(); i += bytesource.size()) {
                bytesource = (ByteSource) iterator.next();
            }

            return i;
        }

        public String toString() {
            return "ByteSource.concat(" + this.sources + ")";
        }
    }

    private static final class EmptyByteSource extends ByteSource.ByteArrayByteSource {

        private static final ByteSource.EmptyByteSource INSTANCE = new ByteSource.EmptyByteSource();

        private EmptyByteSource() {
            super(new byte[0]);
        }

        @Override
        public CharSource asCharSource(Charset charset) {
            Preconditions.checkNotNull(charset);
            return CharSource.empty();
        }

        @Override
        public byte[] read() {
            return this.bytes;
        }

        @Override
        public String toString() {
            return "ByteSource.empty()";
        }
    }

    private static class ByteArrayByteSource extends ByteSource {

        protected final byte[] bytes;

        protected ByteArrayByteSource(byte[] abyte) {
            this.bytes = (byte[]) Preconditions.checkNotNull(abyte);
        }

        @Override
        public InputStream openStream() {
            return new ByteArrayInputStream(this.bytes);
        }

        @Override
        public InputStream openBufferedStream() throws IOException {
            return this.openStream();
        }

        @Override
        public boolean isEmpty() {
            return this.bytes.length == 0;
        }

        @Override
        public long size() {
            return (long) this.bytes.length;
        }

        @Override
        public byte[] read() {
            return (byte[]) this.bytes.clone();
        }

        @Override
        public long copyTo(OutputStream outputstream) throws IOException {
            outputstream.write(this.bytes);
            return (long) this.bytes.length;
        }

        @Override
        public <T> T read(ByteProcessor<T> byteprocessor) throws IOException {
            byteprocessor.processBytes(this.bytes, 0, this.bytes.length);
            return byteprocessor.getResult();
        }

        @Override
        public HashCode hash(HashFunction hashfunction) throws IOException {
            return hashfunction.hashBytes(this.bytes);
        }

        public String toString() {
            return "ByteSource.wrap(" + BaseEncoding.base16().encode(this.bytes) + ")";
        }
    }

    private final class SlicedByteSource extends ByteSource {

        private final long offset;
        private final long length;

        private SlicedByteSource(long i, long j) {
            Preconditions.checkArgument(i >= 0L, "offset (%s) may not be negative", i);
            Preconditions.checkArgument(j >= 0L, "length (%s) may not be negative", j);
            this.offset = i;
            this.length = j;
        }

        @Override
        public InputStream openStream() throws IOException {
            return this.sliceStream(ByteSource.this.openStream());
        }

        @Override
        public InputStream openBufferedStream() throws IOException {
            return this.sliceStream(ByteSource.this.openBufferedStream());
        }

        private InputStream sliceStream(InputStream inputstream) throws IOException {
            if (this.offset > 0L) {
                try {
                    ByteStreams.skipFully(inputstream, this.offset);
                } catch (Throwable throwable) {
                    Throwable throwable1 = throwable;
                    Closer closer = Closer.create();

                    closer.register(inputstream);

                    try {
                        throw closer.rethrow(throwable1);
                    } finally {
                        closer.close();
                    }
                }
            }

            return ByteStreams.limit(inputstream, this.length);
        }

        @Override
        public ByteSource slice(long i, long j) {
            Preconditions.checkArgument(i >= 0L, "offset (%s) may not be negative", i);
            Preconditions.checkArgument(j >= 0L, "length (%s) may not be negative", j);
            long k = this.length - i;

            return ByteSource.this.slice(this.offset + i, Math.min(j, k));
        }

        @Override
        public boolean isEmpty() throws IOException {
            return this.length == 0L || super.isEmpty();
        }

        public String toString() {
            return ByteSource.this.toString() + ".slice(" + this.offset + ", " + this.length + ")";
        }
    }

    private final class AsCharSource extends CharSource {

        private final Charset charset;

        private AsCharSource(Charset charset) {
            this.charset = (Charset) Preconditions.checkNotNull(charset);
        }

        @Override
        public Reader openStream() throws IOException {
            return new InputStreamReader(ByteSource.this.openStream(), this.charset);
        }

        public String toString() {
            return ByteSource.this.toString() + ".asCharSource(" + this.charset + ")";
        }
    }
}
