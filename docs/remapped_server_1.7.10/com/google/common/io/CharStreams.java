package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Beta
public final class CharStreams {

    private static final int BUF_SIZE = 2048;

    private CharStreams() {}

    /** @deprecated */
    @Deprecated
    public static InputSupplier<StringReader> newReaderSupplier(String s) {
        return asInputSupplier(CharSource.wrap(s));
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<InputStreamReader> newReaderSupplier(InputSupplier<? extends InputStream> inputsupplier, Charset charset) {
        return asInputSupplier(ByteStreams.asByteSource(inputsupplier).asCharSource(charset));
    }

    /** @deprecated */
    @Deprecated
    public static OutputSupplier<OutputStreamWriter> newWriterSupplier(OutputSupplier<? extends OutputStream> outputsupplier, Charset charset) {
        return asOutputSupplier(ByteStreams.asByteSink(outputsupplier).asCharSink(charset));
    }

    /** @deprecated */
    @Deprecated
    public static <W extends Appendable & Closeable> void write(CharSequence charsequence, OutputSupplier<W> outputsupplier) throws IOException {
        asCharSink(outputsupplier).write(charsequence);
    }

    /** @deprecated */
    @Deprecated
    public static <R extends Readable & Closeable, W extends Appendable & Closeable> long copy(InputSupplier<R> inputsupplier, OutputSupplier<W> outputsupplier) throws IOException {
        return asCharSource(inputsupplier).copyTo(asCharSink(outputsupplier));
    }

    /** @deprecated */
    @Deprecated
    public static <R extends Readable & Closeable> long copy(InputSupplier<R> inputsupplier, Appendable appendable) throws IOException {
        return asCharSource(inputsupplier).copyTo(appendable);
    }

    public static long copy(Readable readable, Appendable appendable) throws IOException {
        Preconditions.checkNotNull(readable);
        Preconditions.checkNotNull(appendable);
        CharBuffer charbuffer = CharBuffer.allocate(2048);
        long i = 0L;

        while (readable.read(charbuffer) != -1) {
            charbuffer.flip();
            appendable.append(charbuffer);
            i += (long) charbuffer.remaining();
            charbuffer.clear();
        }

        return i;
    }

    public static String toString(Readable readable) throws IOException {
        return toStringBuilder(readable).toString();
    }

    /** @deprecated */
    @Deprecated
    public static <R extends Readable & Closeable> String toString(InputSupplier<R> inputsupplier) throws IOException {
        return asCharSource(inputsupplier).read();
    }

    private static StringBuilder toStringBuilder(Readable readable) throws IOException {
        StringBuilder stringbuilder = new StringBuilder();

        copy(readable, (Appendable) stringbuilder);
        return stringbuilder;
    }

    /** @deprecated */
    @Deprecated
    public static <R extends Readable & Closeable> String readFirstLine(InputSupplier<R> inputsupplier) throws IOException {
        return asCharSource(inputsupplier).readFirstLine();
    }

    /** @deprecated */
    @Deprecated
    public static <R extends Readable & Closeable> List<String> readLines(InputSupplier<R> inputsupplier) throws IOException {
        Closer closer = Closer.create();

        List list;

        try {
            Readable readable = (Readable) closer.register((Closeable) inputsupplier.getInput());

            list = readLines(readable);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return list;
    }

    public static List<String> readLines(Readable readable) throws IOException {
        ArrayList arraylist = new ArrayList();
        LineReader linereader = new LineReader(readable);

        String s;

        while ((s = linereader.readLine()) != null) {
            arraylist.add(s);
        }

        return arraylist;
    }

    public static <T> T readLines(Readable readable, LineProcessor<T> lineprocessor) throws IOException {
        Preconditions.checkNotNull(readable);
        Preconditions.checkNotNull(lineprocessor);
        LineReader linereader = new LineReader(readable);

        String s;

        while ((s = linereader.readLine()) != null && lineprocessor.processLine(s)) {
            ;
        }

        return lineprocessor.getResult();
    }

    /** @deprecated */
    @Deprecated
    public static <R extends Readable & Closeable, T> T readLines(InputSupplier<R> inputsupplier, LineProcessor<T> lineprocessor) throws IOException {
        Preconditions.checkNotNull(inputsupplier);
        Preconditions.checkNotNull(lineprocessor);
        Closer closer = Closer.create();

        Object object;

        try {
            Readable readable = (Readable) closer.register((Closeable) inputsupplier.getInput());

            object = readLines(readable, lineprocessor);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return object;
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<Reader> join(Iterable<? extends InputSupplier<? extends Reader>> iterable) {
        Preconditions.checkNotNull(iterable);
        Iterable iterable1 = Iterables.transform(iterable, new Function<InputSupplier<? extends Reader>, CharSource>() {
            public CharSource apply(InputSupplier<? extends Reader> inputsupplier) {
                return CharStreams.asCharSource(inputsupplier);
            }
        });

        return asInputSupplier(CharSource.concat(iterable1));
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<Reader> join(InputSupplier<? extends Reader>... ainputsupplier) {
        return join((Iterable) Arrays.asList(ainputsupplier));
    }

    public static void skipFully(Reader reader, long i) throws IOException {
        Preconditions.checkNotNull(reader);

        while (i > 0L) {
            long j = reader.skip(i);

            if (j == 0L) {
                if (reader.read() == -1) {
                    throw new EOFException();
                }

                --i;
            } else {
                i -= j;
            }
        }

    }

    public static Writer nullWriter() {
        return CharStreams.NullWriter.INSTANCE;
    }

    public static Writer asWriter(Appendable appendable) {
        return (Writer) (appendable instanceof Writer ? (Writer) appendable : new AppendableWriter(appendable));
    }

    static Reader asReader(final Readable readable) {
        Preconditions.checkNotNull(readable);
        return readable instanceof Reader ? (Reader) readable : new Reader() {
            public int read(char[] achar, int i, int j) throws IOException {
                return this.read(CharBuffer.wrap(achar, i, j));
            }

            public int read(CharBuffer charbuffer) throws IOException {
                return readable.read(charbuffer);
            }

            public void close() throws IOException {
                if (readable instanceof Closeable) {
                    ((Closeable) readable).close();
                }

            }
        };
    }

    /** @deprecated */
    @Deprecated
    public static CharSource asCharSource(final InputSupplier<? extends Readable> inputsupplier) {
        Preconditions.checkNotNull(inputsupplier);
        return new CharSource() {
            @Override
            public Reader openStream() throws IOException {
                return CharStreams.asReader((Readable) inputsupplier.getInput());
            }

            public String toString() {
                return "CharStreams.asCharSource(" + inputsupplier + ")";
            }
        };
    }

    /** @deprecated */
    @Deprecated
    public static CharSink asCharSink(final OutputSupplier<? extends Appendable> outputsupplier) {
        Preconditions.checkNotNull(outputsupplier);
        return new CharSink() {
            @Override
            public Writer openStream() throws IOException {
                return CharStreams.asWriter((Appendable) outputsupplier.getOutput());
            }

            public String toString() {
                return "CharStreams.asCharSink(" + outputsupplier + ")";
            }
        };
    }

    static <R extends Reader> InputSupplier<R> asInputSupplier(CharSource charsource) {
        return (InputSupplier) Preconditions.checkNotNull(charsource);
    }

    static <W extends Writer> OutputSupplier<W> asOutputSupplier(CharSink charsink) {
        return (OutputSupplier) Preconditions.checkNotNull(charsink);
    }

    private static final class NullWriter extends Writer {

        private static final CharStreams.NullWriter INSTANCE = new CharStreams.NullWriter();

        private NullWriter() {}

        public void write(int i) {}

        public void write(char[] achar) {
            Preconditions.checkNotNull(achar);
        }

        public void write(char[] achar, int i, int j) {
            Preconditions.checkPositionIndexes(i, i + j, achar.length);
        }

        public void write(String s) {
            Preconditions.checkNotNull(s);
        }

        public void write(String s, int i, int j) {
            Preconditions.checkPositionIndexes(i, i + j, s.length());
        }

        public Writer append(CharSequence charsequence) {
            Preconditions.checkNotNull(charsequence);
            return this;
        }

        public Writer append(CharSequence charsequence, int i, int j) {
            Preconditions.checkPositionIndexes(i, j, charsequence.length());
            return this;
        }

        public Writer append(char c0) {
            return this;
        }

        public void flush() {}

        public void close() {}

        public String toString() {
            return "CharStreams.nullWriter()";
        }
    }
}
