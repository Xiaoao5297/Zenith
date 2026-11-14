package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;

@Beta
public final class CountingInputStream extends FilterInputStream {

    private long count;
    private long mark = -1L;

    public CountingInputStream(@Nullable InputStream inputstream) {
        super(inputstream);
    }

    public long getCount() {
        return this.count;
    }

    public int read() throws IOException {
        int i = this.in.read();

        if (i != -1) {
            ++this.count;
        }

        return i;
    }

    public int read(byte[] abyte, int i, int j) throws IOException {
        int k = this.in.read(abyte, i, j);

        if (k != -1) {
            this.count += (long) k;
        }

        return k;
    }

    public long skip(long i) throws IOException {
        long j = this.in.skip(i);

        this.count += j;
        return j;
    }

    public synchronized void mark(int i) {
        this.in.mark(i);
        this.mark = this.count;
    }

    public synchronized void reset() throws IOException {
        if (!this.in.markSupported()) {
            throw new IOException("Mark not supported");
        } else if (this.mark == -1L) {
            throw new IOException("Mark not set");
        } else {
            this.in.reset();
            this.count = this.mark;
        }
    }
}
