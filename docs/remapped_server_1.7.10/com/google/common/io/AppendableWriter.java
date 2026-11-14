package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.Nullable;

class AppendableWriter extends Writer {

    private final Appendable target;
    private boolean closed;

    AppendableWriter(Appendable appendable) {
        this.target = (Appendable) Preconditions.checkNotNull(appendable);
    }

    public void write(char[] achar, int i, int j) throws IOException {
        this.checkNotClosed();
        this.target.append(new String(achar, i, j));
    }

    public void flush() throws IOException {
        this.checkNotClosed();
        if (this.target instanceof Flushable) {
            ((Flushable) this.target).flush();
        }

    }

    public void close() throws IOException {
        this.closed = true;
        if (this.target instanceof Closeable) {
            ((Closeable) this.target).close();
        }

    }

    public void write(int i) throws IOException {
        this.checkNotClosed();
        this.target.append((char) i);
    }

    public void write(@Nullable String s) throws IOException {
        this.checkNotClosed();
        this.target.append(s);
    }

    public void write(@Nullable String s, int i, int j) throws IOException {
        this.checkNotClosed();
        this.target.append(s, i, i + j);
    }

    public Writer append(char c0) throws IOException {
        this.checkNotClosed();
        this.target.append(c0);
        return this;
    }

    public Writer append(@Nullable CharSequence charsequence) throws IOException {
        this.checkNotClosed();
        this.target.append(charsequence);
        return this;
    }

    public Writer append(@Nullable CharSequence charsequence, int i, int j) throws IOException {
        this.checkNotClosed();
        this.target.append(charsequence, i, j);
        return this;
    }

    private void checkNotClosed() throws IOException {
        if (this.closed) {
            throw new IOException("Cannot write to a closed writer.");
        }
    }
}
