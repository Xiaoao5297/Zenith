package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.annotation.Nullable;

final class MultiInputStream extends InputStream {

    private Iterator<? extends ByteSource> it;
    private InputStream in;

    public MultiInputStream(Iterator<? extends ByteSource> iterator) throws IOException {
        this.it = (Iterator) Preconditions.checkNotNull(iterator);
        this.advance();
    }

    public void close() throws IOException {
        if (this.in != null) {
            try {
                this.in.close();
            } finally {
                this.in = null;
            }
        }

    }

    private void advance() throws IOException {
        this.close();
        if (this.it.hasNext()) {
            this.in = ((ByteSource) this.it.next()).openStream();
        }

    }

    public int available() throws IOException {
        return this.in == null ? 0 : this.in.available();
    }

    public boolean markSupported() {
        return false;
    }

    public int read() throws IOException {
        if (this.in == null) {
            return -1;
        } else {
            int i = this.in.read();

            if (i == -1) {
                this.advance();
                return this.read();
            } else {
                return i;
            }
        }
    }

    public int read(@Nullable byte[] abyte, int i, int j) throws IOException {
        if (this.in == null) {
            return -1;
        } else {
            int k = this.in.read(abyte, i, j);

            if (k == -1) {
                this.advance();
                return this.read(abyte, i, j);
            } else {
                return k;
            }
        }
    }

    public long skip(long i) throws IOException {
        if (this.in != null && i > 0L) {
            long j = this.in.skip(i);

            return j != 0L ? j : (this.read() == -1 ? 0L : 1L + this.in.skip(i - 1L));
        } else {
            return 0L;
        }
    }
}
