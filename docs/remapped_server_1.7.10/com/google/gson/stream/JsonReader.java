package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader implements Closeable {

    private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_EOF = 17;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private final Reader in;
    private boolean lenient = false;
    private final char[] buffer = new char[1024];
    private int pos = 0;
    private int limit = 0;
    private int lineNumber = 0;
    private int lineStart = 0;
    private int peeked = 0;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int[] stack = new int[32];
    private int stackSize = 0;

    public JsonReader(Reader reader) {
        this.stack[this.stackSize++] = 6;
        if (reader == null) {
            throw new NullPointerException("in == null");
        } else {
            this.in = reader;
        }
    }

    public final void setLenient(boolean flag) {
        this.lenient = flag;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    public void beginArray() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        if (i == 3) {
            this.push(1);
            this.peeked = 0;
        } else {
            throw new IllegalStateException("Expected BEGIN_ARRAY but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
    }

    public void endArray() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        if (i == 4) {
            --this.stackSize;
            this.peeked = 0;
        } else {
            throw new IllegalStateException("Expected END_ARRAY but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
    }

    public void beginObject() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        if (i == 1) {
            this.push(3);
            this.peeked = 0;
        } else {
            throw new IllegalStateException("Expected BEGIN_OBJECT but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
    }

    public void endObject() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        if (i == 2) {
            --this.stackSize;
            this.peeked = 0;
        } else {
            throw new IllegalStateException("Expected END_OBJECT but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
    }

    public boolean hasNext() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        return i != 2 && i != 4;
    }

    public JsonToken peek() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        switch (i) {
            case 1:
                return JsonToken.BEGIN_OBJECT;
            case 2:
                return JsonToken.END_OBJECT;
            case 3:
                return JsonToken.BEGIN_ARRAY;
            case 4:
                return JsonToken.END_ARRAY;
            case 5:
            case 6:
                return JsonToken.BOOLEAN;
            case 7:
                return JsonToken.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return JsonToken.STRING;
            case 12:
            case 13:
            case 14:
                return JsonToken.NAME;
            case 15:
            case 16:
                return JsonToken.NUMBER;
            case 17:
                return JsonToken.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    private int doPeek() throws IOException {
        int i = this.stack[this.stackSize - 1];
        int j;

        if (i == 1) {
            this.stack[this.stackSize - 1] = 2;
        } else if (i == 2) {
            j = this.nextNonWhitespace(true);
            switch (j) {
                case 44:
                    break;
                case 59:
                    this.checkLenient();
                    break;
                case 93:
                    return this.peeked = 4;
                default:
                    throw this.syntaxError("Unterminated array");
            }
        } else {
            if (i == 3 || i == 5) {
                this.stack[this.stackSize - 1] = 4;
                if (i == 5) {
                    j = this.nextNonWhitespace(true);
                    switch (j) {
                        case 44:
                            break;
                        case 59:
                            this.checkLenient();
                            break;
                        case 125:
                            return this.peeked = 2;
                        default:
                            throw this.syntaxError("Unterminated object");
                    }
                }

                j = this.nextNonWhitespace(true);
                switch (j) {
                    case 34:
                        return this.peeked = 13;
                    case 39:
                        this.checkLenient();
                        return this.peeked = 12;
                    case 125:
                        if (i != 5) {
                            return this.peeked = 2;
                        }

                        throw this.syntaxError("Expected name");
                    default:
                        this.checkLenient();
                        --this.pos;
                        if (this.isLiteral((char) j)) {
                            return this.peeked = 14;
                        }

                        throw this.syntaxError("Expected name");
                }
            }

            if (i == 4) {
                this.stack[this.stackSize - 1] = 5;
                j = this.nextNonWhitespace(true);
                switch (j) {
                    case 58:
                        break;
                    case 61:
                        this.checkLenient();
                        if ((this.pos < this.limit || this.fillBuffer(1)) && this.buffer[this.pos] == '>') {
                            ++this.pos;
                        }
                        break;
                    default:
                        throw this.syntaxError("Expected ':'");
                }
            } else if (i == 6) {
                if (this.lenient) {
                    this.consumeNonExecutePrefix();
                }

                this.stack[this.stackSize - 1] = 7;
            } else if (i == 7) {
                j = this.nextNonWhitespace(false);
                if (j == -1) {
                    return this.peeked = 17;
                }

                this.checkLenient();
                --this.pos;
            } else if (i == 8) {
                throw new IllegalStateException("JsonReader is closed");
            }
        }

        j = this.nextNonWhitespace(true);
        switch (j) {
            case 34:
                if (this.stackSize == 1) {
                    this.checkLenient();
                }

                return this.peeked = 9;
            case 39:
                this.checkLenient();
                return this.peeked = 8;
            case 91:
                return this.peeked = 3;
            case 93:
                if (i == 1) {
                    return this.peeked = 4;
                }
            case 44:
            case 59:
                if (i != 1 && i != 2) {
                    throw this.syntaxError("Unexpected value");
                }

                this.checkLenient();
                --this.pos;
                return this.peeked = 7;
            case 123:
                return this.peeked = 1;
            default:
                --this.pos;
                if (this.stackSize == 1) {
                    this.checkLenient();
                }

                int k = this.peekKeyword();

                if (k != 0) {
                    return k;
                } else {
                    k = this.peekNumber();
                    if (k != 0) {
                        return k;
                    } else if (!this.isLiteral(this.buffer[this.pos])) {
                        throw this.syntaxError("Expected value");
                    } else {
                        this.checkLenient();
                        return this.peeked = 10;
                    }
                }
        }
    }

    private int peekKeyword() throws IOException {
        char c0 = this.buffer[this.pos];
        String s;
        String s1;
        byte b0;

        if (c0 != 't' && c0 != 'T') {
            if (c0 != 'f' && c0 != 'F') {
                if (c0 != 'n' && c0 != 'N') {
                    return 0;
                }

                s = "null";
                s1 = "NULL";
                b0 = 7;
            } else {
                s = "false";
                s1 = "FALSE";
                b0 = 6;
            }
        } else {
            s = "true";
            s1 = "TRUE";
            b0 = 5;
        }

        int i = s.length();

        for (int j = 1; j < i; ++j) {
            if (this.pos + j >= this.limit && !this.fillBuffer(j + 1)) {
                return 0;
            }

            c0 = this.buffer[this.pos + j];
            if (c0 != s.charAt(j) && c0 != s1.charAt(j)) {
                return 0;
            }
        }

        if ((this.pos + i < this.limit || this.fillBuffer(i + 1)) && this.isLiteral(this.buffer[this.pos + i])) {
            return 0;
        } else {
            this.pos += i;
            return this.peeked = b0;
        }
    }

    private int peekNumber() throws IOException {
        char[] achar = this.buffer;
        int i = this.pos;
        int j = this.limit;
        long k = 0L;
        boolean flag = false;
        boolean flag1 = true;
        byte b0 = 0;
        int l = 0;

        label122:
        while (true) {
            if (i + l == j) {
                if (l == achar.length) {
                    return 0;
                }

                if (!this.fillBuffer(l + 1)) {
                    break;
                }

                i = this.pos;
                j = this.limit;
            }

            char c0 = achar[i + l];

            switch (c0) {
                case '+':
                    if (b0 != 5) {
                        return 0;
                    }

                    b0 = 6;
                    break;
                case '-':
                    if (b0 == 0) {
                        flag = true;
                        b0 = 1;
                    } else {
                        if (b0 != 5) {
                            return 0;
                        }

                        b0 = 6;
                    }
                    break;
                case '.':
                    if (b0 != 2) {
                        return 0;
                    }

                    b0 = 3;
                    break;
                case 'E':
                case 'e':
                    if (b0 != 2 && b0 != 4) {
                        return 0;
                    }

                    b0 = 5;
                    break;
                default:
                    if (c0 < '0' || c0 > '9') {
                        if (this.isLiteral(c0)) {
                            return 0;
                        }
                        break label122;
                    }

                    if (b0 != 1 && b0 != 0) {
                        if (b0 == 2) {
                            if (k == 0L) {
                                return 0;
                            }

                            long i1 = k * 10L - (long) (c0 - 48);

                            flag1 &= k > -922337203685477580L || k == -922337203685477580L && i1 < k;
                            k = i1;
                        } else if (b0 == 3) {
                            b0 = 4;
                        } else if (b0 == 5 || b0 == 6) {
                            b0 = 7;
                        }
                    } else {
                        k = (long) (-(c0 - 48));
                        b0 = 2;
                    }
            }

            ++l;
        }

        if (b0 == 2 && flag1 && (k != Long.MIN_VALUE || flag)) {
            this.peekedLong = flag ? k : -k;
            this.pos += l;
            return this.peeked = 15;
        } else if (b0 != 2 && b0 != 4 && b0 != 7) {
            return 0;
        } else {
            this.peekedNumberLength = l;
            return this.peeked = 16;
        }
    }

    private boolean isLiteral(char c0) throws IOException {
        switch (c0) {
            case '#':
            case '/':
            case ';':
            case '=':
            case '\\':
                this.checkLenient();
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case '[':
            case ']':
            case '{':
            case '}':
                return false;
            default:
                return true;
        }
    }

    public String nextName() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        String s;

        if (i == 14) {
            s = this.nextUnquotedValue();
        } else if (i == 12) {
            s = this.nextQuotedValue('\'');
        } else {
            if (i != 13) {
                throw new IllegalStateException("Expected a name but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            }

            s = this.nextQuotedValue('"');
        }

        this.peeked = 0;
        return s;
    }

    public String nextString() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        String s;

        if (i == 10) {
            s = this.nextUnquotedValue();
        } else if (i == 8) {
            s = this.nextQuotedValue('\'');
        } else if (i == 9) {
            s = this.nextQuotedValue('"');
        } else if (i == 11) {
            s = this.peekedString;
            this.peekedString = null;
        } else if (i == 15) {
            s = Long.toString(this.peekedLong);
        } else {
            if (i != 16) {
                throw new IllegalStateException("Expected a string but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            }

            s = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        }

        this.peeked = 0;
        return s;
    }

    public boolean nextBoolean() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        if (i == 5) {
            this.peeked = 0;
            return true;
        } else if (i == 6) {
            this.peeked = 0;
            return false;
        } else {
            throw new IllegalStateException("Expected a boolean but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
    }

    public void nextNull() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        if (i == 7) {
            this.peeked = 0;
        } else {
            throw new IllegalStateException("Expected null but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
    }

    public double nextDouble() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        if (i == 15) {
            this.peeked = 0;
            return (double) this.peekedLong;
        } else {
            if (i == 16) {
                this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
                this.pos += this.peekedNumberLength;
            } else if (i != 8 && i != 9) {
                if (i == 10) {
                    this.peekedString = this.nextUnquotedValue();
                } else if (i != 11) {
                    throw new IllegalStateException("Expected a double but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
                }
            } else {
                this.peekedString = this.nextQuotedValue((char) (i == 8 ? '\'' : '"'));
            }

            this.peeked = 11;
            double d0 = Double.parseDouble(this.peekedString);

            if (!this.lenient && (Double.isNaN(d0) || Double.isInfinite(d0))) {
                throw new MalformedJsonException("JSON forbids NaN and infinities: " + d0 + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            } else {
                this.peekedString = null;
                this.peeked = 0;
                return d0;
            }
        }
    }

    public long nextLong() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        if (i == 15) {
            this.peeked = 0;
            return this.peekedLong;
        } else {
            if (i == 16) {
                this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
                this.pos += this.peekedNumberLength;
            } else {
                if (i != 8 && i != 9) {
                    throw new IllegalStateException("Expected a long but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
                }

                this.peekedString = this.nextQuotedValue((char) (i == 8 ? '\'' : '"'));

                try {
                    long j = Long.parseLong(this.peekedString);

                    this.peeked = 0;
                    return j;
                } catch (NumberFormatException numberformatexception) {
                    ;
                }
            }

            this.peeked = 11;
            double d0 = Double.parseDouble(this.peekedString);
            long k = (long) d0;

            if ((double) k != d0) {
                throw new NumberFormatException("Expected a long but was " + this.peekedString + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            } else {
                this.peekedString = null;
                this.peeked = 0;
                return k;
            }
        }
    }

    private String nextQuotedValue(char c0) throws IOException {
        char[] achar = this.buffer;
        StringBuilder stringbuilder = new StringBuilder();

        do {
            int i = this.pos;
            int j = this.limit;
            int k = i;

            while (i < j) {
                char c1 = achar[i++];

                if (c1 == c0) {
                    this.pos = i;
                    stringbuilder.append(achar, k, i - k - 1);
                    return stringbuilder.toString();
                }

                if (c1 == '\\') {
                    this.pos = i;
                    stringbuilder.append(achar, k, i - k - 1);
                    stringbuilder.append(this.readEscapeCharacter());
                    i = this.pos;
                    j = this.limit;
                    k = i;
                } else if (c1 == '\n') {
                    ++this.lineNumber;
                    this.lineStart = i;
                }
            }

            stringbuilder.append(achar, k, i - k);
            this.pos = i;
        } while (this.fillBuffer(1));

        throw this.syntaxError("Unterminated string");
    }

    private String nextUnquotedValue() throws IOException {
        StringBuilder stringbuilder = null;
        int i = 0;

        label35:
        while (true) {
            if (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break label35;
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        this.checkLenient();
                        break label35;
                    default:
                        ++i;
                }
            } else if (i < this.buffer.length) {
                if (this.fillBuffer(i + 1)) {
                    continue;
                }
                break;
            } else {
                if (stringbuilder == null) {
                    stringbuilder = new StringBuilder();
                }

                stringbuilder.append(this.buffer, this.pos, i);
                this.pos += i;
                i = 0;
                if (!this.fillBuffer(1)) {
                    break;
                }
            }
        }

        String s;

        if (stringbuilder == null) {
            s = new String(this.buffer, this.pos, i);
        } else {
            stringbuilder.append(this.buffer, this.pos, i);
            s = stringbuilder.toString();
        }

        this.pos += i;
        return s;
    }

    private void skipQuotedValue(char c0) throws IOException {
        char[] achar = this.buffer;

        do {
            int i = this.pos;
            int j = this.limit;

            while (i < j) {
                char c1 = achar[i++];

                if (c1 == c0) {
                    this.pos = i;
                    return;
                }

                if (c1 == '\\') {
                    this.pos = i;
                    this.readEscapeCharacter();
                    i = this.pos;
                    j = this.limit;
                } else if (c1 == '\n') {
                    ++this.lineNumber;
                    this.lineStart = i;
                }
            }

            this.pos = i;
        } while (this.fillBuffer(1));

        throw this.syntaxError("Unterminated string");
    }

    private void skipUnquotedValue() throws IOException {
        do {
            int i = 0;

            while (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        this.checkLenient();
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        this.pos += i;
                        return;
                    default:
                        ++i;
                }
            }

            this.pos += i;
        } while (this.fillBuffer(1));

    }

    public int nextInt() throws IOException {
        int i = this.peeked;

        if (i == 0) {
            i = this.doPeek();
        }

        int j;

        if (i == 15) {
            j = (int) this.peekedLong;
            if (this.peekedLong != (long) j) {
                throw new NumberFormatException("Expected an int but was " + this.peekedLong + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            } else {
                this.peeked = 0;
                return j;
            }
        } else {
            if (i == 16) {
                this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
                this.pos += this.peekedNumberLength;
            } else {
                if (i != 8 && i != 9) {
                    throw new IllegalStateException("Expected an int but was " + this.peek() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
                }

                this.peekedString = this.nextQuotedValue((char) (i == 8 ? '\'' : '"'));

                try {
                    j = Integer.parseInt(this.peekedString);
                    this.peeked = 0;
                    return j;
                } catch (NumberFormatException numberformatexception) {
                    ;
                }
            }

            this.peeked = 11;
            double d0 = Double.parseDouble(this.peekedString);

            j = (int) d0;
            if ((double) j != d0) {
                throw new NumberFormatException("Expected an int but was " + this.peekedString + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            } else {
                this.peekedString = null;
                this.peeked = 0;
                return j;
            }
        }
    }

    public void close() throws IOException {
        this.peeked = 0;
        this.stack[0] = 8;
        this.stackSize = 1;
        this.in.close();
    }

    public void skipValue() throws IOException {
        int i = 0;

        do {
            int j = this.peeked;

            if (j == 0) {
                j = this.doPeek();
            }

            if (j == 3) {
                this.push(1);
                ++i;
            } else if (j == 1) {
                this.push(3);
                ++i;
            } else if (j == 4) {
                --this.stackSize;
                --i;
            } else if (j == 2) {
                --this.stackSize;
                --i;
            } else if (j != 14 && j != 10) {
                if (j != 8 && j != 12) {
                    if (j != 9 && j != 13) {
                        if (j == 16) {
                            this.pos += this.peekedNumberLength;
                        }
                    } else {
                        this.skipQuotedValue('"');
                    }
                } else {
                    this.skipQuotedValue('\'');
                }
            } else {
                this.skipUnquotedValue();
            }

            this.peeked = 0;
        } while (i != 0);

    }

    private void push(int i) {
        if (this.stackSize == this.stack.length) {
            int[] aint = new int[this.stackSize * 2];

            System.arraycopy(this.stack, 0, aint, 0, this.stackSize);
            this.stack = aint;
        }

        this.stack[this.stackSize++] = i;
    }

    private boolean fillBuffer(int i) throws IOException {
        char[] achar = this.buffer;

        this.lineStart -= this.pos;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(achar, this.pos, achar, 0, this.limit);
        } else {
            this.limit = 0;
        }

        this.pos = 0;

        do {
            int j;

            if ((j = this.in.read(achar, this.limit, achar.length - this.limit)) == -1) {
                return false;
            }

            this.limit += j;
            if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && achar[0] == '\ufeff') {
                ++this.pos;
                ++this.lineStart;
                ++i;
            }
        } while (this.limit < i);

        return true;
    }

    private int getLineNumber() {
        return this.lineNumber + 1;
    }

    private int getColumnNumber() {
        return this.pos - this.lineStart + 1;
    }

    private int nextNonWhitespace(boolean flag) throws IOException {
        char[] achar = this.buffer;
        int i = this.pos;
        int j = this.limit;

        while (true) {
            if (i == j) {
                this.pos = i;
                if (!this.fillBuffer(1)) {
                    if (flag) {
                        throw new EOFException("End of input at line " + this.getLineNumber() + " column " + this.getColumnNumber());
                    }

                    return -1;
                }

                i = this.pos;
                j = this.limit;
            }

            char c0 = achar[i++];

            if (c0 == '\n') {
                ++this.lineNumber;
                this.lineStart = i;
            } else if (c0 != ' ' && c0 != '\r' && c0 != '\t') {
                if (c0 == '/') {
                    this.pos = i;
                    if (i == j) {
                        --this.pos;
                        boolean flag1 = this.fillBuffer(2);

                        ++this.pos;
                        if (!flag1) {
                            return c0;
                        }
                    }

                    this.checkLenient();
                    char c1 = achar[this.pos];

                    switch (c1) {
                        case '*':
                            ++this.pos;
                            if (!this.skipTo("*/")) {
                                throw this.syntaxError("Unterminated comment");
                            }

                            i = this.pos + 2;
                            j = this.limit;
                            break;
                        case '/':
                            ++this.pos;
                            this.skipToEndOfLine();
                            i = this.pos;
                            j = this.limit;
                            break;
                        default:
                            return c0;
                    }
                } else {
                    if (c0 != '#') {
                        this.pos = i;
                        return c0;
                    }

                    this.pos = i;
                    this.checkLenient();
                    this.skipToEndOfLine();
                    i = this.pos;
                    j = this.limit;
                }
            }
        }
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw this.syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        while (this.pos < this.limit || this.fillBuffer(1)) {
            char c0 = this.buffer[this.pos++];

            if (c0 == '\n') {
                ++this.lineNumber;
                this.lineStart = this.pos;
            } else if (c0 != '\r') {
                continue;
            }
            break;
        }

    }

    private boolean skipTo(String s) throws IOException {
        label28:
        for (; this.pos + s.length() <= this.limit || this.fillBuffer(s.length()); ++this.pos) {
            if (this.buffer[this.pos] == '\n') {
                ++this.lineNumber;
                this.lineStart = this.pos + 1;
            } else {
                for (int i = 0; i < s.length(); ++i) {
                    if (this.buffer[this.pos + i] != s.charAt(i)) {
                        continue label28;
                    }
                }

                return true;
            }
        }

        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber();
    }

    private char readEscapeCharacter() throws IOException {
        if (this.pos == this.limit && !this.fillBuffer(1)) {
            throw this.syntaxError("Unterminated escape sequence");
        } else {
            char c0 = this.buffer[this.pos++];

            switch (c0) {
                case '\n':
                    ++this.lineNumber;
                    this.lineStart = this.pos;
                case '"':
                case '\'':
                case '\\':
                default:
                    return c0;
                case 'b':
                    return '\b';
                case 'f':
                    return '\f';
                case 'n':
                    return '\n';
                case 'r':
                    return '\r';
                case 't':
                    return '\t';
                case 'u':
                    if (this.pos + 4 > this.limit && !this.fillBuffer(4)) {
                        throw this.syntaxError("Unterminated escape sequence");
                    } else {
                        char c1 = 0;
                        int i = this.pos;

                        for (int j = i + 4; i < j; ++i) {
                            char c2 = this.buffer[i];

                            c1 = (char) (c1 << 4);
                            if (c2 >= '0' && c2 <= '9') {
                                c1 = (char) (c1 + (c2 - 48));
                            } else if (c2 >= 'a' && c2 <= 'f') {
                                c1 = (char) (c1 + c2 - 97 + 10);
                            } else {
                                if (c2 < 'A' || c2 > 'F') {
                                    throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
                                }

                                c1 = (char) (c1 + c2 - 65 + 10);
                            }
                        }

                        this.pos += 4;
                        return c1;
                    }
            }
        }
    }

    private IOException syntaxError(String s) throws IOException {
        throw new MalformedJsonException(s + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
    }

    private void consumeNonExecutePrefix() throws IOException {
        this.nextNonWhitespace(true);
        --this.pos;
        if (this.pos + JsonReader.NON_EXECUTE_PREFIX.length <= this.limit || this.fillBuffer(JsonReader.NON_EXECUTE_PREFIX.length)) {
            for (int i = 0; i < JsonReader.NON_EXECUTE_PREFIX.length; ++i) {
                if (this.buffer[this.pos + i] != JsonReader.NON_EXECUTE_PREFIX[i]) {
                    return;
                }
            }

            this.pos += JsonReader.NON_EXECUTE_PREFIX.length;
        }
    }

    static {
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
            @Override
            public void promoteNameToValue(JsonReader jsonreader) throws IOException {
                if (jsonreader instanceof JsonTreeReader) {
                    ((JsonTreeReader) jsonreader).promoteNameToValue();
                } else {
                    int i = jsonreader.peeked;

                    if (i == 0) {
                        i = jsonreader.doPeek();
                    }

                    if (i == 13) {
                        jsonreader.peeked = 9;
                    } else if (i == 12) {
                        jsonreader.peeked = 8;
                    } else {
                        if (i != 14) {
                            throw new IllegalStateException("Expected a name but was " + jsonreader.peek() + " " + " at line " + jsonreader.getLineNumber() + " column " + jsonreader.getColumnNumber());
                        }

                        jsonreader.peeked = 10;
                    }

                }
            }
        };
    }
}
