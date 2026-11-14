package com.google.gson.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class JsonWriter implements Closeable, Flushable {

    private static final String[] REPLACEMENT_CHARS = new String[128];
    private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
    private final Writer out;
    private int[] stack = new int[32];
    private int stackSize = 0;
    private String indent;
    private String separator;
    private boolean lenient;
    private boolean htmlSafe;
    private String deferredName;
    private boolean serializeNulls;

    public JsonWriter(Writer writer) {
        this.push(6);
        this.separator = ":";
        this.serializeNulls = true;
        if (writer == null) {
            throw new NullPointerException("out == null");
        } else {
            this.out = writer;
        }
    }

    public final void setIndent(String s) {
        if (s.length() == 0) {
            this.indent = null;
            this.separator = ":";
        } else {
            this.indent = s;
            this.separator = ": ";
        }

    }

    public final void setLenient(boolean flag) {
        this.lenient = flag;
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public final void setHtmlSafe(boolean flag) {
        this.htmlSafe = flag;
    }

    public final boolean isHtmlSafe() {
        return this.htmlSafe;
    }

    public final void setSerializeNulls(boolean flag) {
        this.serializeNulls = flag;
    }

    public final boolean getSerializeNulls() {
        return this.serializeNulls;
    }

    public JsonWriter beginArray() throws IOException {
        this.writeDeferredName();
        return this.open(1, "[");
    }

    public JsonWriter endArray() throws IOException {
        return this.close(1, 2, "]");
    }

    public JsonWriter beginObject() throws IOException {
        this.writeDeferredName();
        return this.open(3, "{");
    }

    public JsonWriter endObject() throws IOException {
        return this.close(3, 5, "}");
    }

    private JsonWriter open(int i, String s) throws IOException {
        this.beforeValue(true);
        this.push(i);
        this.out.write(s);
        return this;
    }

    private JsonWriter close(int i, int j, String s) throws IOException {
        int k = this.peek();

        if (k != j && k != i) {
            throw new IllegalStateException("Nesting problem.");
        } else if (this.deferredName != null) {
            throw new IllegalStateException("Dangling name: " + this.deferredName);
        } else {
            --this.stackSize;
            if (k == j) {
                this.newline();
            }

            this.out.write(s);
            return this;
        }
    }

    private void push(int i) {
        if (this.stackSize == this.stack.length) {
            int[] aint = new int[this.stackSize * 2];

            System.arraycopy(this.stack, 0, aint, 0, this.stackSize);
            this.stack = aint;
        }

        this.stack[this.stackSize++] = i;
    }

    private int peek() {
        if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        } else {
            return this.stack[this.stackSize - 1];
        }
    }

    private void replaceTop(int i) {
        this.stack[this.stackSize - 1] = i;
    }

    public JsonWriter name(String s) throws IOException {
        if (s == null) {
            throw new NullPointerException("name == null");
        } else if (this.deferredName != null) {
            throw new IllegalStateException();
        } else if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        } else {
            this.deferredName = s;
            return this;
        }
    }

    private void writeDeferredName() throws IOException {
        if (this.deferredName != null) {
            this.beforeName();
            this.string(this.deferredName);
            this.deferredName = null;
        }

    }

    public JsonWriter value(String s) throws IOException {
        if (s == null) {
            return this.nullValue();
        } else {
            this.writeDeferredName();
            this.beforeValue(false);
            this.string(s);
            return this;
        }
    }

    public JsonWriter nullValue() throws IOException {
        if (this.deferredName != null) {
            if (!this.serializeNulls) {
                this.deferredName = null;
                return this;
            }

            this.writeDeferredName();
        }

        this.beforeValue(false);
        this.out.write("null");
        return this;
    }

    public JsonWriter value(boolean flag) throws IOException {
        this.writeDeferredName();
        this.beforeValue(false);
        this.out.write(flag ? "true" : "false");
        return this;
    }

    public JsonWriter value(double d0) throws IOException {
        if (!Double.isNaN(d0) && !Double.isInfinite(d0)) {
            this.writeDeferredName();
            this.beforeValue(false);
            this.out.append(Double.toString(d0));
            return this;
        } else {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + d0);
        }
    }

    public JsonWriter value(long i) throws IOException {
        this.writeDeferredName();
        this.beforeValue(false);
        this.out.write(Long.toString(i));
        return this;
    }

    public JsonWriter value(Number number) throws IOException {
        if (number == null) {
            return this.nullValue();
        } else {
            this.writeDeferredName();
            String s = number.toString();

            if (!this.lenient && (s.equals("-Infinity") || s.equals("Infinity") || s.equals("NaN"))) {
                throw new IllegalArgumentException("Numeric values must be finite, but was " + number);
            } else {
                this.beforeValue(false);
                this.out.append(s);
                return this;
            }
        }
    }

    public void flush() throws IOException {
        if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        } else {
            this.out.flush();
        }
    }

    public void close() throws IOException {
        this.out.close();
        int i = this.stackSize;

        if (i <= 1 && (i != 1 || this.stack[i - 1] == 7)) {
            this.stackSize = 0;
        } else {
            throw new IOException("Incomplete document");
        }
    }

    private void string(String s) throws IOException {
        String[] astring = this.htmlSafe ? JsonWriter.HTML_SAFE_REPLACEMENT_CHARS : JsonWriter.REPLACEMENT_CHARS;

        this.out.write("\"");
        int i = 0;
        int j = s.length();

        for (int k = 0; k < j; ++k) {
            char c0 = s.charAt(k);
            String s1;

            if (c0 < 128) {
                s1 = astring[c0];
                if (s1 == null) {
                    continue;
                }
            } else if (c0 == 8232) {
                s1 = "\\u2028";
            } else {
                if (c0 != 8233) {
                    continue;
                }

                s1 = "\\u2029";
            }

            if (i < k) {
                this.out.write(s, i, k - i);
            }

            this.out.write(s1);
            i = k + 1;
        }

        if (i < j) {
            this.out.write(s, i, j - i);
        }

        this.out.write("\"");
    }

    private void newline() throws IOException {
        if (this.indent != null) {
            this.out.write("\n");
            int i = 1;

            for (int j = this.stackSize; i < j; ++i) {
                this.out.write(this.indent);
            }

        }
    }

    private void beforeName() throws IOException {
        int i = this.peek();

        if (i == 5) {
            this.out.write(44);
        } else if (i != 3) {
            throw new IllegalStateException("Nesting problem.");
        }

        this.newline();
        this.replaceTop(4);
    }

    private void beforeValue(boolean flag) throws IOException {
        switch (this.peek()) {
            case 1:
                this.replaceTop(2);
                this.newline();
                break;
            case 2:
                this.out.append(',');
                this.newline();
                break;
            case 3:
            case 5:
            default:
                throw new IllegalStateException("Nesting problem.");
            case 4:
                this.out.append(this.separator);
                this.replaceTop(5);
                break;
            case 7:
                if (!this.lenient) {
                    throw new IllegalStateException("JSON must have only one top-level value.");
                }
            case 6:
                if (!this.lenient && !flag) {
                    throw new IllegalStateException("JSON must start with an array or an object.");
                }

                this.replaceTop(7);
        }

    }

    static {
        for (int i = 0; i <= 31; ++i) {
            JsonWriter.REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
        }

        JsonWriter.REPLACEMENT_CHARS[34] = "\\\"";
        JsonWriter.REPLACEMENT_CHARS[92] = "\\\\";
        JsonWriter.REPLACEMENT_CHARS[9] = "\\t";
        JsonWriter.REPLACEMENT_CHARS[8] = "\\b";
        JsonWriter.REPLACEMENT_CHARS[10] = "\\n";
        JsonWriter.REPLACEMENT_CHARS[13] = "\\r";
        JsonWriter.REPLACEMENT_CHARS[12] = "\\f";
        HTML_SAFE_REPLACEMENT_CHARS = (String[]) JsonWriter.REPLACEMENT_CHARS.clone();
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[60] = "\\u003c";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
    }
}
