package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;

final class UTF8Output {

    private static final int UTF8_ACCEPT = 0;
    private static final int UTF8_REJECT = 12;
    private static final byte[] TYPES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
    private static final byte[] STATES = new byte[]{0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12};
    private int state = 0;
    private int codep;
    private final StringBuilder stringBuilder;

    UTF8Output(ByteBuf bytebuf) {
        this.stringBuilder = new StringBuilder(bytebuf.readableBytes());
        this.write(bytebuf);
    }

    public void write(ByteBuf bytebuf) {
        for (int i = bytebuf.readerIndex(); i < bytebuf.writerIndex(); ++i) {
            this.write(bytebuf.getByte(i));
        }

    }

    public void write(byte[] abyte) {
        byte[] abyte1 = abyte;
        int i = abyte.length;

        for (int j = 0; j < i; ++j) {
            byte b0 = abyte1[j];

            this.write(b0);
        }

    }

    public void write(int i) {
        byte b0 = UTF8Output.TYPES[i & 255];

        this.codep = this.state != 0 ? i & 63 | this.codep << 6 : 255 >> b0 & i;
        this.state = UTF8Output.STATES[this.state + b0];
        if (this.state == 0) {
            this.stringBuilder.append((char) this.codep);
        } else if (this.state == 12) {
            throw new UTF8Exception("bytes are not UTF-8");
        }

    }

    public String toString() {
        if (this.state != 0) {
            throw new UTF8Exception("bytes are not UTF-8");
        } else {
            return this.stringBuilder.toString();
        }
    }
}
