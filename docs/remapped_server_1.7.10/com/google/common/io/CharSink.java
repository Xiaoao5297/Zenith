package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public abstract class CharSink implements OutputSupplier<Writer> {

    protected CharSink() {}

    public abstract Writer openStream() throws IOException;

    /** @deprecated */
    @Deprecated
    @Override
    public final Writer getOutput() throws IOException {
        return this.openStream();
    }

    public Writer openBufferedStream() throws IOException {
        Writer writer = this.openStream();

        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    public void write(CharSequence charsequence) throws IOException {
        Preconditions.checkNotNull(charsequence);
        Closer closer = Closer.create();

        try {
            Writer writer = (Writer) closer.register(this.openStream());

            writer.append(charsequence);
            writer.flush();
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

    }

    public void writeLines(Iterable<? extends CharSequence> iterable) throws IOException {
        this.writeLines(iterable, System.getProperty("line.separator"));
    }

    public void writeLines(Iterable<? extends CharSequence> iterable, String s) throws IOException {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkNotNull(s);
        Closer closer = Closer.create();

        try {
            Writer writer = (Writer) closer.register(this.openBufferedStream());
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                CharSequence charsequence = (CharSequence) iterator.next();

                writer.append(charsequence).append(s);
            }

            writer.flush();
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

    }

    public long writeFrom(Readable readable) throws IOException {
        Preconditions.checkNotNull(readable);
        Closer closer = Closer.create();

        long i;

        try {
            Writer writer = (Writer) closer.register(this.openStream());
            long j = CharStreams.copy(readable, (Appendable) writer);

            writer.flush();
            i = j;
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return i;
    }
}
