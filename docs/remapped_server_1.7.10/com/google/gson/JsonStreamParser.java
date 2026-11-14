package com.google.gson;

import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class JsonStreamParser implements Iterator<JsonElement> {

    private final JsonReader parser;
    private final Object lock;

    public JsonStreamParser(String s) {
        this((Reader) (new StringReader(s)));
    }

    public JsonStreamParser(Reader reader) {
        this.parser = new JsonReader(reader);
        this.parser.setLenient(true);
        this.lock = new Object();
    }

    public JsonElement next() throws JsonParseException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        } else {
            try {
                return Streams.parse(this.parser);
            } catch (StackOverflowError stackoverflowerror) {
                throw new JsonParseException("Failed parsing JSON source to Json", stackoverflowerror);
            } catch (OutOfMemoryError outofmemoryerror) {
                throw new JsonParseException("Failed parsing JSON source to Json", outofmemoryerror);
            } catch (JsonParseException jsonparseexception) {
                throw jsonparseexception.getCause() instanceof EOFException ? new NoSuchElementException() : jsonparseexception;
            }
        }
    }

    public boolean hasNext() {
        Object object = this.lock;

        synchronized (this.lock) {
            boolean flag;

            try {
                flag = this.parser.peek() != JsonToken.END_DOCUMENT;
            } catch (MalformedJsonException malformedjsonexception) {
                throw new JsonSyntaxException(malformedjsonexception);
            } catch (IOException ioexception) {
                throw new JsonIOException(ioexception);
            }

            return flag;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
