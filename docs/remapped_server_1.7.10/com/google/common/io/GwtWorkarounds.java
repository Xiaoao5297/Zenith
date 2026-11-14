package com.google.common.io;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

@GwtCompatible(emulated = true)
final class GwtWorkarounds {

    private GwtWorkarounds() {}

    @GwtIncompatible("Reader")
    static GwtWorkarounds.CharInput asCharInput(final Reader reader) {
        Preconditions.checkNotNull(reader);
        return new GwtWorkarounds.CharInput() {
            @Override
            public int read() throws IOException {
                return reader.read();
            }

            @Override
            public void close() throws IOException {
                reader.close();
            }
        };
    }

    static GwtWorkarounds.CharInput asCharInput(final CharSequence charsequence) {
        Preconditions.checkNotNull(charsequence);
        return new GwtWorkarounds.CharInput() {
            int index = 0;

            @Override
            public int read() {
                return this.index < charsequence.length() ? charsequence.charAt(this.index++) : -1;
            }

            @Override
            public void close() {
                this.index = charsequence.length();
            }
        };
    }

    @GwtIncompatible("InputStream")
    static InputStream asInputStream(final GwtWorkarounds.ByteInput gwtworkarounds_byteinput) {
        Preconditions.checkNotNull(gwtworkarounds_byteinput);
        return new InputStream() {
            public int read() throws IOException {
                return gwtworkarounds_byteinput.read();
            }

            public int read(byte[] abyte, int i, int j) throws IOException {
                Preconditions.checkNotNull(abyte);
                Preconditions.checkPositionIndexes(i, i + j, abyte.length);
                if (j == 0) {
                    return 0;
                } else {
                    int k = this.read();

                    if (k == -1) {
                        return -1;
                    } else {
                        abyte[i] = (byte) k;

                        for (int l = 1; l < j; ++l) {
                            int i1 = this.read();

                            if (i1 == -1) {
                                return l;
                            }

                            abyte[i + l] = (byte) i1;
                        }

                        return j;
                    }
                }
            }

            public void close() throws IOException {
                gwtworkarounds_byteinput.close();
            }
        };
    }

    @GwtIncompatible("OutputStream")
    static OutputStream asOutputStream(final GwtWorkarounds.ByteOutput gwtworkarounds_byteoutput) {
        Preconditions.checkNotNull(gwtworkarounds_byteoutput);
        return new OutputStream() {
            public void write(int i) throws IOException {
                gwtworkarounds_byteoutput.write((byte) i);
            }

            public void flush() throws IOException {
                gwtworkarounds_byteoutput.flush();
            }

            public void close() throws IOException {
                gwtworkarounds_byteoutput.close();
            }
        };
    }

    @GwtIncompatible("Writer")
    static GwtWorkarounds.CharOutput asCharOutput(final Writer writer) {
        Preconditions.checkNotNull(writer);
        return new GwtWorkarounds.CharOutput() {
            @Override
            public void write(char c0) throws IOException {
                writer.append(c0);
            }

            @Override
            public void flush() throws IOException {
                writer.flush();
            }

            @Override
            public void close() throws IOException {
                writer.close();
            }
        };
    }

    static GwtWorkarounds.CharOutput stringBuilderOutput(int i) {
        final StringBuilder stringbuilder = new StringBuilder(i);

        return new GwtWorkarounds.CharOutput() {
            @Override
            public void write(char c0) {
                stringbuilder.append(c0);
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}

            public String toString() {
                return stringbuilder.toString();
            }
        };
    }

    interface CharOutput {

        void write(char c0) throws IOException;

        void flush() throws IOException;

        void close() throws IOException;
    }

    interface ByteOutput {

        void write(byte b0) throws IOException;

        void flush() throws IOException;

        void close() throws IOException;
    }

    interface ByteInput {

        int read() throws IOException;

        void close() throws IOException;
    }

    interface CharInput {

        int read() throws IOException;

        void close() throws IOException;
    }
}
