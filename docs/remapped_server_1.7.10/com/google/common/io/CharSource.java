package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public abstract class CharSource implements InputSupplier<Reader> {

    protected CharSource() {}

    public abstract Reader openStream() throws IOException;

    /** @deprecated */
    @Deprecated
    @Override
    public final Reader getInput() throws IOException {
        return this.openStream();
    }

    public BufferedReader openBufferedStream() throws IOException {
        Reader reader = this.openStream();

        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public long copyTo(Appendable appendable) throws IOException {
        Preconditions.checkNotNull(appendable);
        Closer closer = Closer.create();

        long i;

        try {
            Reader reader = (Reader) closer.register(this.openStream());

            i = CharStreams.copy((Readable) reader, appendable);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return i;
    }

    public long copyTo(CharSink charsink) throws IOException {
        Preconditions.checkNotNull(charsink);
        Closer closer = Closer.create();

        long i;

        try {
            Reader reader = (Reader) closer.register(this.openStream());
            Writer writer = (Writer) closer.register(charsink.openStream());

            i = CharStreams.copy((Readable) reader, (Appendable) writer);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return i;
    }

    public String read() throws IOException {
        Closer closer = Closer.create();

        String s;

        try {
            Reader reader = (Reader) closer.register(this.openStream());

            s = CharStreams.toString((Readable) reader);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return s;
    }

    @Nullable
    public String readFirstLine() throws IOException {
        Closer closer = Closer.create();

        String s;

        try {
            BufferedReader bufferedreader = (BufferedReader) closer.register(this.openBufferedStream());

            s = bufferedreader.readLine();
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return s;
    }

    public ImmutableList<String> readLines() throws IOException {
        Closer closer = Closer.create();

        ImmutableList immutablelist;

        try {
            BufferedReader bufferedreader = (BufferedReader) closer.register(this.openBufferedStream());
            ArrayList arraylist = Lists.newArrayList();

            String s;

            while ((s = bufferedreader.readLine()) != null) {
                arraylist.add(s);
            }

            immutablelist = ImmutableList.copyOf((Collection) arraylist);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return immutablelist;
    }

    @Beta
    public <T> T readLines(LineProcessor<T> lineprocessor) throws IOException {
        Preconditions.checkNotNull(lineprocessor);
        Closer closer = Closer.create();

        Object object;

        try {
            Reader reader = (Reader) closer.register(this.openStream());

            object = CharStreams.readLines((Readable) reader, lineprocessor);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return object;
    }

    public boolean isEmpty() throws IOException {
        Closer closer = Closer.create();

        boolean flag;

        try {
            Reader reader = (Reader) closer.register(this.openStream());

            flag = reader.read() == -1;
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return flag;
    }

    public static CharSource concat(Iterable<? extends CharSource> iterable) {
        return new CharSource.ConcatenatedCharSource(iterable);
    }

    public static CharSource concat(Iterator<? extends CharSource> iterator) {
        return concat((Iterable) ImmutableList.copyOf(iterator));
    }

    public static CharSource concat(CharSource... acharsource) {
        return concat((Iterable) ImmutableList.copyOf((Object[]) acharsource));
    }

    public static CharSource wrap(CharSequence charsequence) {
        return new CharSource.CharSequenceCharSource(charsequence);
    }

    public static CharSource empty() {
        return CharSource.EmptyCharSource.INSTANCE;
    }

    private static final class ConcatenatedCharSource extends CharSource {

        private final Iterable<? extends CharSource> sources;

        ConcatenatedCharSource(Iterable<? extends CharSource> iterable) {
            this.sources = (Iterable) Preconditions.checkNotNull(iterable);
        }

        @Override
        public Reader openStream() throws IOException {
            return new MultiReader(this.sources.iterator());
        }

        @Override
        public boolean isEmpty() throws IOException {
            Iterator iterator = this.sources.iterator();

            CharSource charsource;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                charsource = (CharSource) iterator.next();
            } while (charsource.isEmpty());

            return false;
        }

        public String toString() {
            return "CharSource.concat(" + this.sources + ")";
        }
    }

    private static final class EmptyCharSource extends CharSource.CharSequenceCharSource {

        private static final CharSource.EmptyCharSource INSTANCE = new CharSource.EmptyCharSource();

        private EmptyCharSource() {
            super("");
        }

        @Override
        public String toString() {
            return "CharSource.empty()";
        }
    }

    private static class CharSequenceCharSource extends CharSource {

        private static final Splitter LINE_SPLITTER = Splitter.on(Pattern.compile("\r\n|\n|\r"));
        private final CharSequence seq;

        protected CharSequenceCharSource(CharSequence charsequence) {
            this.seq = (CharSequence) Preconditions.checkNotNull(charsequence);
        }

        @Override
        public Reader openStream() {
            return new CharSequenceReader(this.seq);
        }

        @Override
        public String read() {
            return this.seq.toString();
        }

        @Override
        public boolean isEmpty() {
            return this.seq.length() == 0;
        }

        private Iterable<String> lines() {
            return new Iterable<String>() {
                public Iterator<String> iterator() {
                    return new AbstractIterator<String>() {
                        Iterator<String> lines;

                        {
                            this.lines = CharSource.CharSequenceCharSource.LINE_SPLITTER.split(CharSequenceCharSource.this.seq).iterator();
                        }

                        @Override
                        protected String computeNext() {
                            if (this.lines.hasNext()) {
                                String s = (String) this.lines.next();

                                if (this.lines.hasNext() || !s.isEmpty()) {
                                    return s;
                                }
                            }

                            return (String) this.endOfData();
                        }
                    };
                }
            };
        }

        @Override
        public String readFirstLine() {
            Iterator iterator = this.lines().iterator();

            return iterator.hasNext() ? (String) iterator.next() : null;
        }

        @Override
        public ImmutableList<String> readLines() {
            return ImmutableList.copyOf(this.lines());
        }

        @Override
        public <T> T readLines(LineProcessor<T> lineprocessor) throws IOException {
            Iterator iterator = this.lines().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                if (!lineprocessor.processLine(s)) {
                    break;
                }
            }

            return lineprocessor.getResult();
        }

        public String toString() {
            Object object = this.seq.length() <= 15 ? this.seq : this.seq.subSequence(0, 12) + "...";

            return "CharSource.wrap(" + object + ")";
        }
    }
}
