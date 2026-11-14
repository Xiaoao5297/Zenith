package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

final class CharSequenceReader extends Reader {

    private CharSequence seq;
    private int pos;
    private int mark;

    public CharSequenceReader(CharSequence charsequence) {
        this.seq = (CharSequence) Preconditions.checkNotNull(charsequence);
    }

    private void checkOpen() throws IOException {
        if (this.seq == null) {
            throw new IOException("reader closed");
        }
    }

    private boolean hasRemaining() {
        return this.remaining() > 0;
    }

    private int remaining() {
        return this.seq.length() - this.pos;
    }

    public synchronized int read(CharBuffer charbuffer) throws IOException {
        Preconditions.checkNotNull(charbuffer);
        this.checkOpen();
        if (!this.hasRemaining()) {
            return -1;
        } else {
            int i = Math.min(charbuffer.remaining(), this.remaining());

            for (int j = 0; j < i; ++j) {
                charbuffer.put(this.seq.charAt(this.pos++));
            }

            return i;
        }
    }

    public synchronized int read() throws IOException {
        this.checkOpen();
        return this.hasRemaining() ? this.seq.charAt(this.pos++) : -1;
    }

    public synchronized int read(char[] achar, int i, int j) throws IOException {
        Preconditions.checkPositionIndexes(i, i + j, achar.length);
        this.checkOpen();
        if (!this.hasRemaining()) {
            return -1;
        } else {
            int k = Math.min(j, this.remaining());

            for (int l = 0; l < k; ++l) {
                achar[i + l] = this.seq.charAt(this.pos++);
            }

            return k;
        }
    }

    public synchronized long skip(long i) throws IOException {
        Preconditions.checkArgument(i >= 0L, "n (%s) may not be negative", i);
        this.checkOpen();
        int j = (int) Math.min((long) this.remaining(), i);

        this.pos += j;
        return (long) j;
    }

    public synchronized boolean ready() throws IOException {
        this.checkOpen();
        return true;
    }

    public boolean markSupported() {
        return true;
    }

    public synchronized void mark(int i) throws IOException {
        Preconditions.checkArgument(i >= 0, "readAheadLimit (%s) may not be negative", i);
        this.checkOpen();
        this.mark = this.pos;
    }

    public synchronized void reset() throws IOException {
        this.checkOpen();
        this.pos = this.mark;
    }

    public synchronized void close() throws IOException {
        this.seq = null;
    }
}
