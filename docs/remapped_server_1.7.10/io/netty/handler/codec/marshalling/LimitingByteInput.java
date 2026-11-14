package io.netty.handler.codec.marshalling;

import java.io.IOException;
import org.jboss.marshalling.ByteInput;

class LimitingByteInput implements ByteInput {

    private static final LimitingByteInput.TooBigObjectException EXCEPTION = new LimitingByteInput.TooBigObjectException();
    private final ByteInput input;
    private final long limit;
    private long read;

    public LimitingByteInput(ByteInput byteinput, long i) {
        if (i <= 0L) {
            throw new IllegalArgumentException("The limit MUST be > 0");
        } else {
            this.input = byteinput;
            this.limit = i;
        }
    }

    public void close() throws IOException {}

    public int available() throws IOException {
        return this.readable(this.input.available());
    }

    public int read() throws IOException {
        int i = this.readable(1);

        if (i > 0) {
            int j = this.input.read();

            ++this.read;
            return j;
        } else {
            throw LimitingByteInput.EXCEPTION;
        }
    }

    public int read(byte[] abyte) throws IOException {
        return this.read(abyte, 0, abyte.length);
    }

    public int read(byte[] abyte, int i, int j) throws IOException {
        int k = this.readable(j);

        if (k > 0) {
            int l = this.input.read(abyte, i, k);

            this.read += (long) l;
            return l;
        } else {
            throw LimitingByteInput.EXCEPTION;
        }
    }

    public long skip(long i) throws IOException {
        int j = this.readable((int) i);

        if (j > 0) {
            long k = this.input.skip((long) j);

            this.read += k;
            return k;
        } else {
            throw LimitingByteInput.EXCEPTION;
        }
    }

    private int readable(int i) {
        return (int) Math.min((long) i, this.limit - this.read);
    }

    static final class TooBigObjectException extends IOException {

        private static final long serialVersionUID = 1L;

        TooBigObjectException() {}
    }
}
