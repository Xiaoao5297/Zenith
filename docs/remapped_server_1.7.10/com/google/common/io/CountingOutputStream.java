package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nullable;

@Beta
public final class CountingOutputStream extends FilterOutputStream {

    private long count;

    public CountingOutputStream(@Nullable OutputStream outputstream) {
        super(outputstream);
    }

    public long getCount() {
        return this.count;
    }

    public void write(byte[] abyte, int i, int j) throws IOException {
        this.out.write(abyte, i, j);
        this.count += (long) j;
    }

    public void write(int i) throws IOException {
        this.out.write(i);
        ++this.count;
    }
}
