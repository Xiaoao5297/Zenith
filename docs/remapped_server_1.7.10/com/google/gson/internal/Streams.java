package com.google.gson.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class Streams {

    public Streams() {}

    public static JsonElement parse(JsonReader jsonreader) throws JsonParseException {
        boolean flag = true;

        try {
            jsonreader.peek();
            flag = false;
            return (JsonElement) TypeAdapters.JSON_ELEMENT.read(jsonreader);
        } catch (EOFException eofexception) {
            if (flag) {
                return JsonNull.INSTANCE;
            } else {
                throw new JsonSyntaxException(eofexception);
            }
        } catch (MalformedJsonException malformedjsonexception) {
            throw new JsonSyntaxException(malformedjsonexception);
        } catch (IOException ioexception) {
            throw new JsonIOException(ioexception);
        } catch (NumberFormatException numberformatexception) {
            throw new JsonSyntaxException(numberformatexception);
        }
    }

    public static void write(JsonElement jsonelement, JsonWriter jsonwriter) throws IOException {
        TypeAdapters.JSON_ELEMENT.write(jsonwriter, jsonelement);
    }

    public static Writer writerForAppendable(Appendable appendable) {
        return (Writer) (appendable instanceof Writer ? (Writer) appendable : new Streams.AppendableWriter(appendable));
    }

    private static final class AppendableWriter extends Writer {

        private final Appendable appendable;
        private final Streams.AppendableWriter.CurrentWrite currentWrite;

        private AppendableWriter(Appendable appendable) {
            this.currentWrite = new Streams.AppendableWriter.CurrentWrite();
            this.appendable = appendable;
        }

        public void write(char[] achar, int i, int j) throws IOException {
            this.currentWrite.chars = achar;
            this.appendable.append(this.currentWrite, i, i + j);
        }

        public void write(int i) throws IOException {
            this.appendable.append((char) i);
        }

        public void flush() {}

        public void close() {}

        static class CurrentWrite implements CharSequence {

            char[] chars;

            CurrentWrite() {}

            public int length() {
                return this.chars.length;
            }

            public char charAt(int i) {
                return this.chars[i];
            }

            public CharSequence subSequence(int i, int j) {
                return new String(this.chars, i, j - i);
            }
        }
    }
}
