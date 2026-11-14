package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import javax.annotation.Nullable;

class MultiReader extends Reader {

    private final Iterator<? extends CharSource> it;
    private Reader current;

    MultiReader(Iterator<? extends CharSource> iterator) throws IOException {
        this.it = iterator;
        this.advance();
    }

    private void advance() throws IOException {
        this.close();
        if (this.it.hasNext()) {
            this.current = ((CharSource) this.it.next()).openStream();
        }

    }

    public int read(@Nullable char[] achar, int i, int j) throws IOException {
        if (this.current == null) {
            return -1;
        } else {
            int k = this.current.read(achar, i, j);

            if (k == -1) {
                this.advance();
                return this.read(achar, i, j);
            } else {
                return k;
            }
        }
    }

    public long skip(long i) throws IOException {
        Preconditions.checkArgument(i >= 0L, "n is negative");
        if (i > 0L) {
            while (this.current != null) {
                long j = this.current.skip(i);

                if (j > 0L) {
                    return j;
                }

                this.advance();
            }
        }

        return 0L;
    }

    public boolean ready() throws IOException {
        return this.current != null && this.current.ready();
    }

    public void close() throws IOException {
        if (this.current != null) {
            try {
                this.current.close();
            } finally {
                this.current = null;
            }
        }

    }
}
