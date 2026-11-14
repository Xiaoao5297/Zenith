package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.RoundingMode;
import java.util.Arrays;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@Beta
@GwtCompatible(emulated = true)
public abstract class BaseEncoding {

    private static final BaseEncoding BASE64 = new BaseEncoding.StandardBaseEncoding("base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", '=');
    private static final BaseEncoding BASE64_URL = new BaseEncoding.StandardBaseEncoding("base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", '=');
    private static final BaseEncoding BASE32 = new BaseEncoding.StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", '=');
    private static final BaseEncoding BASE32_HEX = new BaseEncoding.StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", '=');
    private static final BaseEncoding BASE16 = new BaseEncoding.StandardBaseEncoding("base16()", "0123456789ABCDEF", (Character) null);

    BaseEncoding() {}

    public String encode(byte[] abyte) {
        return this.encode((byte[]) Preconditions.checkNotNull(abyte), 0, abyte.length);
    }

    public final String encode(byte[] abyte, int i, int j) {
        Preconditions.checkNotNull(abyte);
        Preconditions.checkPositionIndexes(i, i + j, abyte.length);
        GwtWorkarounds.CharOutput gwtworkarounds_charoutput = GwtWorkarounds.stringBuilderOutput(this.maxEncodedSize(j));
        GwtWorkarounds.ByteOutput gwtworkarounds_byteoutput = this.encodingStream(gwtworkarounds_charoutput);

        try {
            for (int k = 0; k < j; ++k) {
                gwtworkarounds_byteoutput.write(abyte[i + k]);
            }

            gwtworkarounds_byteoutput.close();
        } catch (IOException ioexception) {
            throw new AssertionError("impossible");
        }

        return gwtworkarounds_charoutput.toString();
    }

    @GwtIncompatible("Writer,OutputStream")
    public final OutputStream encodingStream(Writer writer) {
        return GwtWorkarounds.asOutputStream(this.encodingStream(GwtWorkarounds.asCharOutput(writer)));
    }

    @GwtIncompatible("ByteSink,CharSink")
    public final ByteSink encodingSink(final CharSink charsink) {
        Preconditions.checkNotNull(charsink);
        return new ByteSink() {
            @Override
            public OutputStream openStream() throws IOException {
                return BaseEncoding.this.encodingStream(charsink.openStream());
            }
        };
    }

    private static byte[] extract(byte[] abyte, int i) {
        if (i == abyte.length) {
            return abyte;
        } else {
            byte[] abyte1 = new byte[i];

            System.arraycopy(abyte, 0, abyte1, 0, i);
            return abyte1;
        }
    }

    public final byte[] decode(CharSequence charsequence) {
        try {
            return this.decodeChecked(charsequence);
        } catch (BaseEncoding.DecodingException baseencoding_decodingexception) {
            throw new IllegalArgumentException(baseencoding_decodingexception);
        }
    }

    final byte[] decodeChecked(CharSequence charsequence) throws BaseEncoding.DecodingException {
        String s = this.padding().trimTrailingFrom(charsequence);
        GwtWorkarounds.ByteInput gwtworkarounds_byteinput = this.decodingStream(GwtWorkarounds.asCharInput((CharSequence) s));
        byte[] abyte = new byte[this.maxDecodedSize(s.length())];
        int i = 0;

        try {
            for (int j = gwtworkarounds_byteinput.read(); j != -1; j = gwtworkarounds_byteinput.read()) {
                abyte[i++] = (byte) j;
            }
        } catch (BaseEncoding.DecodingException baseencoding_decodingexception) {
            throw baseencoding_decodingexception;
        } catch (IOException ioexception) {
            throw new AssertionError(ioexception);
        }

        return extract(abyte, i);
    }

    @GwtIncompatible("Reader,InputStream")
    public final InputStream decodingStream(Reader reader) {
        return GwtWorkarounds.asInputStream(this.decodingStream(GwtWorkarounds.asCharInput(reader)));
    }

    @GwtIncompatible("ByteSource,CharSource")
    public final ByteSource decodingSource(final CharSource charsource) {
        Preconditions.checkNotNull(charsource);
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return BaseEncoding.this.decodingStream(charsource.openStream());
            }
        };
    }

    abstract int maxEncodedSize(int i);

    abstract GwtWorkarounds.ByteOutput encodingStream(GwtWorkarounds.CharOutput gwtworkarounds_charoutput);

    abstract int maxDecodedSize(int i);

    abstract GwtWorkarounds.ByteInput decodingStream(GwtWorkarounds.CharInput gwtworkarounds_charinput);

    abstract CharMatcher padding();

    @CheckReturnValue
    public abstract BaseEncoding omitPadding();

    @CheckReturnValue
    public abstract BaseEncoding withPadChar(char c0);

    @CheckReturnValue
    public abstract BaseEncoding withSeparator(String s, int i);

    @CheckReturnValue
    public abstract BaseEncoding upperCase();

    @CheckReturnValue
    public abstract BaseEncoding lowerCase();

    public static BaseEncoding base64() {
        return BaseEncoding.BASE64;
    }

    public static BaseEncoding base64Url() {
        return BaseEncoding.BASE64_URL;
    }

    public static BaseEncoding base32() {
        return BaseEncoding.BASE32;
    }

    public static BaseEncoding base32Hex() {
        return BaseEncoding.BASE32_HEX;
    }

    public static BaseEncoding base16() {
        return BaseEncoding.BASE16;
    }

    static GwtWorkarounds.CharInput ignoringInput(final GwtWorkarounds.CharInput gwtworkarounds_charinput, final CharMatcher charmatcher) {
        Preconditions.checkNotNull(gwtworkarounds_charinput);
        Preconditions.checkNotNull(charmatcher);
        return new GwtWorkarounds.CharInput() {
            @Override
            public int read() throws IOException {
                int i;

                do {
                    i = gwtworkarounds_charinput.read();
                } while (i != -1 && charmatcher.matches((char) i));

                return i;
            }

            @Override
            public void close() throws IOException {
                gwtworkarounds_charinput.close();
            }
        };
    }

    static GwtWorkarounds.CharOutput separatingOutput(final GwtWorkarounds.CharOutput gwtworkarounds_charoutput, final String s, final int i) {
        Preconditions.checkNotNull(gwtworkarounds_charoutput);
        Preconditions.checkNotNull(s);
        Preconditions.checkArgument(i > 0);
        return new GwtWorkarounds.CharOutput() {
            int charsUntilSeparator = i;

            @Override
            public void write(char c0) throws IOException {
                if (this.charsUntilSeparator == 0) {
                    for (int j = 0; j < s.length(); ++j) {
                        gwtworkarounds_charoutput.write(s.charAt(j));
                    }

                    this.charsUntilSeparator = i;
                }

                gwtworkarounds_charoutput.write(c0);
                --this.charsUntilSeparator;
            }

            @Override
            public void flush() throws IOException {
                gwtworkarounds_charoutput.flush();
            }

            @Override
            public void close() throws IOException {
                gwtworkarounds_charoutput.close();
            }
        };
    }

    static final class SeparatedBaseEncoding extends BaseEncoding {

        private final BaseEncoding delegate;
        private final String separator;
        private final int afterEveryChars;
        private final CharMatcher separatorChars;

        SeparatedBaseEncoding(BaseEncoding baseencoding, String s, int i) {
            this.delegate = (BaseEncoding) Preconditions.checkNotNull(baseencoding);
            this.separator = (String) Preconditions.checkNotNull(s);
            this.afterEveryChars = i;
            Preconditions.checkArgument(i > 0, "Cannot add a separator after every %s chars", i);
            this.separatorChars = CharMatcher.anyOf(s).precomputed();
        }

        @Override
        CharMatcher padding() {
            return this.delegate.padding();
        }

        @Override
        int maxEncodedSize(int i) {
            int j = this.delegate.maxEncodedSize(i);

            return j + this.separator.length() * IntMath.divide(Math.max(0, j - 1), this.afterEveryChars, RoundingMode.FLOOR);
        }

        @Override
        GwtWorkarounds.ByteOutput encodingStream(GwtWorkarounds.CharOutput gwtworkarounds_charoutput) {
            return this.delegate.encodingStream(separatingOutput(gwtworkarounds_charoutput, this.separator, this.afterEveryChars));
        }

        @Override
        int maxDecodedSize(int i) {
            return this.delegate.maxDecodedSize(i);
        }

        @Override
        GwtWorkarounds.ByteInput decodingStream(GwtWorkarounds.CharInput gwtworkarounds_charinput) {
            return this.delegate.decodingStream(ignoringInput(gwtworkarounds_charinput, this.separatorChars));
        }

        @Override
        public BaseEncoding omitPadding() {
            return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
        }

        @Override
        public BaseEncoding withPadChar(char c0) {
            return this.delegate.withPadChar(c0).withSeparator(this.separator, this.afterEveryChars);
        }

        @Override
        public BaseEncoding withSeparator(String s, int i) {
            throw new UnsupportedOperationException("Already have a separator");
        }

        @Override
        public BaseEncoding upperCase() {
            return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
        }

        @Override
        public BaseEncoding lowerCase() {
            return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
        }

        public String toString() {
            return this.delegate.toString() + ".withSeparator(\"" + this.separator + "\", " + this.afterEveryChars + ")";
        }
    }

    static final class StandardBaseEncoding extends BaseEncoding {

        private final BaseEncoding.Alphabet alphabet;
        @Nullable
        private final Character paddingChar;
        private transient BaseEncoding upperCase;
        private transient BaseEncoding lowerCase;

        StandardBaseEncoding(String s, String s1, @Nullable Character character) {
            this(new BaseEncoding.Alphabet(s, s1.toCharArray()), character);
        }

        StandardBaseEncoding(BaseEncoding.Alphabet baseencoding_alphabet, @Nullable Character character) {
            this.alphabet = (BaseEncoding.Alphabet) Preconditions.checkNotNull(baseencoding_alphabet);
            Preconditions.checkArgument(character == null || !baseencoding_alphabet.matches(character), "Padding character %s was already in alphabet", character);
            this.paddingChar = character;
        }

        @Override
        CharMatcher padding() {
            return this.paddingChar == null ? CharMatcher.NONE : CharMatcher.is(this.paddingChar);
        }

        @Override
        int maxEncodedSize(int i) {
            return this.alphabet.charsPerChunk * IntMath.divide(i, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
        }

        @Override
        GwtWorkarounds.ByteOutput encodingStream(final GwtWorkarounds.CharOutput gwtworkarounds_charoutput) {
            Preconditions.checkNotNull(gwtworkarounds_charoutput);
            return new GwtWorkarounds.ByteOutput() {
                int bitBuffer = 0;
                int bitBufferLength = 0;
                int writtenChars = 0;

                @Override
                public void write(byte b0) throws IOException {
                    this.bitBuffer <<= 8;
                    this.bitBuffer |= b0 & 255;

                    for (this.bitBufferLength += 8; this.bitBufferLength >= StandardBaseEncoding.this.alphabet.bitsPerChar; this.bitBufferLength -= StandardBaseEncoding.this.alphabet.bitsPerChar) {
                        int i = this.bitBuffer >> this.bitBufferLength - StandardBaseEncoding.this.alphabet.bitsPerChar & StandardBaseEncoding.this.alphabet.mask;

                        gwtworkarounds_charoutput.write(StandardBaseEncoding.this.alphabet.encode(i));
                        ++this.writtenChars;
                    }

                }

                @Override
                public void flush() throws IOException {
                    gwtworkarounds_charoutput.flush();
                }

                @Override
                public void close() throws IOException {
                    if (this.bitBufferLength > 0) {
                        int i = this.bitBuffer << StandardBaseEncoding.this.alphabet.bitsPerChar - this.bitBufferLength & StandardBaseEncoding.this.alphabet.mask;

                        gwtworkarounds_charoutput.write(StandardBaseEncoding.this.alphabet.encode(i));
                        ++this.writtenChars;
                        if (StandardBaseEncoding.this.paddingChar != null) {
                            while (this.writtenChars % StandardBaseEncoding.this.alphabet.charsPerChunk != 0) {
                                gwtworkarounds_charoutput.write(StandardBaseEncoding.this.paddingChar);
                                ++this.writtenChars;
                            }
                        }
                    }

                    gwtworkarounds_charoutput.close();
                }
            };
        }

        @Override
        int maxDecodedSize(int i) {
            return (int) (((long) this.alphabet.bitsPerChar * (long) i + 7L) / 8L);
        }

        @Override
        GwtWorkarounds.ByteInput decodingStream(final GwtWorkarounds.CharInput gwtworkarounds_charinput) {
            Preconditions.checkNotNull(gwtworkarounds_charinput);
            return new GwtWorkarounds.ByteInput() {
                int bitBuffer = 0;
                int bitBufferLength = 0;
                int readChars = 0;
                boolean hitPadding = false;
                final CharMatcher paddingMatcher = StandardBaseEncoding.this.padding();

                @Override
                public int read() throws IOException {
                    while (true) {
                        int i = gwtworkarounds_charinput.read();

                        if (i == -1) {
                            if (!this.hitPadding && !StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars)) {
                                throw new BaseEncoding.DecodingException("Invalid input length " + this.readChars);
                            }

                            return -1;
                        }

                        ++this.readChars;
                        char c0 = (char) i;

                        if (!this.paddingMatcher.matches(c0)) {
                            if (this.hitPadding) {
                                throw new BaseEncoding.DecodingException("Expected padding character but found '" + c0 + "' at index " + this.readChars);
                            }

                            this.bitBuffer <<= StandardBaseEncoding.this.alphabet.bitsPerChar;
                            this.bitBuffer |= StandardBaseEncoding.this.alphabet.decode(c0);
                            this.bitBufferLength += StandardBaseEncoding.this.alphabet.bitsPerChar;
                            if (this.bitBufferLength >= 8) {
                                this.bitBufferLength -= 8;
                                return this.bitBuffer >> this.bitBufferLength & 255;
                            }
                        } else {
                            if (!this.hitPadding && (this.readChars == 1 || !StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars - 1))) {
                                throw new BaseEncoding.DecodingException("Padding cannot start at index " + this.readChars);
                            }

                            this.hitPadding = true;
                        }
                    }
                }

                @Override
                public void close() throws IOException {
                    gwtworkarounds_charinput.close();
                }
            };
        }

        @Override
        public BaseEncoding omitPadding() {
            return this.paddingChar == null ? this : new BaseEncoding.StandardBaseEncoding(this.alphabet, (Character) null);
        }

        @Override
        public BaseEncoding withPadChar(char c0) {
            return 8 % this.alphabet.bitsPerChar != 0 && (this.paddingChar == null || this.paddingChar != c0) ? new BaseEncoding.StandardBaseEncoding(this.alphabet, c0) : this;
        }

        @Override
        public BaseEncoding withSeparator(String s, int i) {
            Preconditions.checkNotNull(s);
            Preconditions.checkArgument(this.padding().or(this.alphabet).matchesNoneOf(s), "Separator cannot contain alphabet or padding characters");
            return new BaseEncoding.SeparatedBaseEncoding(this, s, i);
        }

        @Override
        public BaseEncoding upperCase() {
            BaseEncoding baseencoding = this.upperCase;

            if (baseencoding == null) {
                BaseEncoding.Alphabet baseencoding_alphabet = this.alphabet.upperCase();

                baseencoding = this.upperCase = baseencoding_alphabet == this.alphabet ? this : new BaseEncoding.StandardBaseEncoding(baseencoding_alphabet, this.paddingChar);
            }

            return baseencoding;
        }

        @Override
        public BaseEncoding lowerCase() {
            BaseEncoding baseencoding = this.lowerCase;

            if (baseencoding == null) {
                BaseEncoding.Alphabet baseencoding_alphabet = this.alphabet.lowerCase();

                baseencoding = this.lowerCase = baseencoding_alphabet == this.alphabet ? this : new BaseEncoding.StandardBaseEncoding(baseencoding_alphabet, this.paddingChar);
            }

            return baseencoding;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder("BaseEncoding.");

            stringbuilder.append(this.alphabet.toString());
            if (8 % this.alphabet.bitsPerChar != 0) {
                if (this.paddingChar == null) {
                    stringbuilder.append(".omitPadding()");
                } else {
                    stringbuilder.append(".withPadChar(").append(this.paddingChar).append(')');
                }
            }

            return stringbuilder.toString();
        }
    }

    private static final class Alphabet extends CharMatcher {

        private final String name;
        private final char[] chars;
        final int mask;
        final int bitsPerChar;
        final int charsPerChunk;
        final int bytesPerChunk;
        private final byte[] decodabet;
        private final boolean[] validPadding;

        Alphabet(String s, char[] achar) {
            this.name = (String) Preconditions.checkNotNull(s);
            this.chars = (char[]) Preconditions.checkNotNull(achar);

            try {
                this.bitsPerChar = IntMath.log2(achar.length, RoundingMode.UNNECESSARY);
            } catch (ArithmeticException arithmeticexception) {
                throw new IllegalArgumentException("Illegal alphabet length " + achar.length, arithmeticexception);
            }

            int i = Math.min(8, Integer.lowestOneBit(this.bitsPerChar));

            this.charsPerChunk = 8 / i;
            this.bytesPerChunk = this.bitsPerChar / i;
            this.mask = achar.length - 1;
            byte[] abyte = new byte[128];

            Arrays.fill(abyte, (byte) -1);

            int j;

            for (int k = 0; k < achar.length; ++k) {
                j = achar[k];
                Preconditions.checkArgument(CharMatcher.ASCII.matches((char) j), "Non-ASCII character: %s", Character.valueOf((char) j));
                Preconditions.checkArgument(abyte[j] == -1, "Duplicate character: %s", Character.valueOf((char) j));
                abyte[j] = (byte) k;
            }

            this.decodabet = abyte;
            boolean[] aboolean = new boolean[this.charsPerChunk];

            for (j = 0; j < this.bytesPerChunk; ++j) {
                aboolean[IntMath.divide(j * 8, this.bitsPerChar, RoundingMode.CEILING)] = true;
            }

            this.validPadding = aboolean;
        }

        char encode(int i) {
            return this.chars[i];
        }

        boolean isValidPaddingStartPosition(int i) {
            return this.validPadding[i % this.charsPerChunk];
        }

        int decode(char c0) throws IOException {
            if (c0 <= 127 && this.decodabet[c0] != -1) {
                return this.decodabet[c0];
            } else {
                throw new BaseEncoding.DecodingException("Unrecognized character: " + c0);
            }
        }

        private boolean hasLowerCase() {
            char[] achar = this.chars;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar[j];

                if (Ascii.isLowerCase(c0)) {
                    return true;
                }
            }

            return false;
        }

        private boolean hasUpperCase() {
            char[] achar = this.chars;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar[j];

                if (Ascii.isUpperCase(c0)) {
                    return true;
                }
            }

            return false;
        }

        BaseEncoding.Alphabet upperCase() {
            if (!this.hasLowerCase()) {
                return this;
            } else {
                Preconditions.checkState(!this.hasUpperCase(), "Cannot call upperCase() on a mixed-case alphabet");
                char[] achar = new char[this.chars.length];

                for (int i = 0; i < this.chars.length; ++i) {
                    achar[i] = Ascii.toUpperCase(this.chars[i]);
                }

                return new BaseEncoding.Alphabet(this.name + ".upperCase()", achar);
            }
        }

        BaseEncoding.Alphabet lowerCase() {
            if (!this.hasUpperCase()) {
                return this;
            } else {
                Preconditions.checkState(!this.hasLowerCase(), "Cannot call lowerCase() on a mixed-case alphabet");
                char[] achar = new char[this.chars.length];

                for (int i = 0; i < this.chars.length; ++i) {
                    achar[i] = Ascii.toLowerCase(this.chars[i]);
                }

                return new BaseEncoding.Alphabet(this.name + ".lowerCase()", achar);
            }
        }

        @Override
        public boolean matches(char c0) {
            return CharMatcher.ASCII.matches(c0) && this.decodabet[c0] != -1;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static final class DecodingException extends IOException {

        DecodingException(String s) {
            super(s);
        }

        DecodingException(Throwable throwable) {
            super(throwable);
        }
    }
}
