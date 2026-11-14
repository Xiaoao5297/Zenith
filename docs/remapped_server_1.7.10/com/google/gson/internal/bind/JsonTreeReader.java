package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class JsonTreeReader extends JsonReader {

    private static final Reader UNREADABLE_READER = new Reader() {
        public int read(char[] achar, int i, int j) throws IOException {
            throw new AssertionError();
        }

        public void close() throws IOException {
            throw new AssertionError();
        }
    };
    private static final Object SENTINEL_CLOSED = new Object();
    private final List<Object> stack = new ArrayList();

    public JsonTreeReader(JsonElement jsonelement) {
        super(JsonTreeReader.UNREADABLE_READER);
        this.stack.add(jsonelement);
    }

    @Override
    public void beginArray() throws IOException {
        this.expect(JsonToken.BEGIN_ARRAY);
        JsonArray jsonarray = (JsonArray) this.peekStack();

        this.stack.add(jsonarray.iterator());
    }

    @Override
    public void endArray() throws IOException {
        this.expect(JsonToken.END_ARRAY);
        this.popStack();
        this.popStack();
    }

    @Override
    public void beginObject() throws IOException {
        this.expect(JsonToken.BEGIN_OBJECT);
        JsonObject jsonobject = (JsonObject) this.peekStack();

        this.stack.add(jsonobject.entrySet().iterator());
    }

    @Override
    public void endObject() throws IOException {
        this.expect(JsonToken.END_OBJECT);
        this.popStack();
        this.popStack();
    }

    @Override
    public boolean hasNext() throws IOException {
        JsonToken jsontoken = this.peek();

        return jsontoken != JsonToken.END_OBJECT && jsontoken != JsonToken.END_ARRAY;
    }

    @Override
    public JsonToken peek() throws IOException {
        if (this.stack.isEmpty()) {
            return JsonToken.END_DOCUMENT;
        } else {
            Object object = this.peekStack();

            if (object instanceof Iterator) {
                boolean flag = this.stack.get(this.stack.size() - 2) instanceof JsonObject;
                Iterator iterator = (Iterator) object;

                if (iterator.hasNext()) {
                    if (flag) {
                        return JsonToken.NAME;
                    } else {
                        this.stack.add(iterator.next());
                        return this.peek();
                    }
                } else {
                    return flag ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
                }
            } else if (object instanceof JsonObject) {
                return JsonToken.BEGIN_OBJECT;
            } else if (object instanceof JsonArray) {
                return JsonToken.BEGIN_ARRAY;
            } else if (object instanceof JsonPrimitive) {
                JsonPrimitive jsonprimitive = (JsonPrimitive) object;

                if (jsonprimitive.isString()) {
                    return JsonToken.STRING;
                } else if (jsonprimitive.isBoolean()) {
                    return JsonToken.BOOLEAN;
                } else if (jsonprimitive.isNumber()) {
                    return JsonToken.NUMBER;
                } else {
                    throw new AssertionError();
                }
            } else if (object instanceof JsonNull) {
                return JsonToken.NULL;
            } else if (object == JsonTreeReader.SENTINEL_CLOSED) {
                throw new IllegalStateException("JsonReader is closed");
            } else {
                throw new AssertionError();
            }
        }
    }

    private Object peekStack() {
        return this.stack.get(this.stack.size() - 1);
    }

    private Object popStack() {
        return this.stack.remove(this.stack.size() - 1);
    }

    private void expect(JsonToken jsontoken) throws IOException {
        if (this.peek() != jsontoken) {
            throw new IllegalStateException("Expected " + jsontoken + " but was " + this.peek());
        }
    }

    @Override
    public String nextName() throws IOException {
        this.expect(JsonToken.NAME);
        Iterator iterator = (Iterator) this.peekStack();
        Entry entry = (Entry) iterator.next();

        this.stack.add(entry.getValue());
        return (String) entry.getKey();
    }

    @Override
    public String nextString() throws IOException {
        JsonToken jsontoken = this.peek();

        if (jsontoken != JsonToken.STRING && jsontoken != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected " + JsonToken.STRING + " but was " + jsontoken);
        } else {
            return ((JsonPrimitive) this.popStack()).getAsString();
        }
    }

    @Override
    public boolean nextBoolean() throws IOException {
        this.expect(JsonToken.BOOLEAN);
        return ((JsonPrimitive) this.popStack()).getAsBoolean();
    }

    @Override
    public void nextNull() throws IOException {
        this.expect(JsonToken.NULL);
        this.popStack();
    }

    @Override
    public double nextDouble() throws IOException {
        JsonToken jsontoken = this.peek();

        if (jsontoken != JsonToken.NUMBER && jsontoken != JsonToken.STRING) {
            throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + jsontoken);
        } else {
            double d0 = ((JsonPrimitive) this.peekStack()).getAsDouble();

            if (!this.isLenient() && (Double.isNaN(d0) || Double.isInfinite(d0))) {
                throw new NumberFormatException("JSON forbids NaN and infinities: " + d0);
            } else {
                this.popStack();
                return d0;
            }
        }
    }

    @Override
    public long nextLong() throws IOException {
        JsonToken jsontoken = this.peek();

        if (jsontoken != JsonToken.NUMBER && jsontoken != JsonToken.STRING) {
            throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + jsontoken);
        } else {
            long i = ((JsonPrimitive) this.peekStack()).getAsLong();

            this.popStack();
            return i;
        }
    }

    @Override
    public int nextInt() throws IOException {
        JsonToken jsontoken = this.peek();

        if (jsontoken != JsonToken.NUMBER && jsontoken != JsonToken.STRING) {
            throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + jsontoken);
        } else {
            int i = ((JsonPrimitive) this.peekStack()).getAsInt();

            this.popStack();
            return i;
        }
    }

    @Override
    public void close() throws IOException {
        this.stack.clear();
        this.stack.add(JsonTreeReader.SENTINEL_CLOSED);
    }

    @Override
    public void skipValue() throws IOException {
        if (this.peek() == JsonToken.NAME) {
            this.nextName();
        } else {
            this.popStack();
        }

    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public void promoteNameToValue() throws IOException {
        this.expect(JsonToken.NAME);
        Iterator iterator = (Iterator) this.peekStack();
        Entry entry = (Entry) iterator.next();

        this.stack.add(entry.getValue());
        this.stack.add(new JsonPrimitive((String) entry.getKey()));
    }
}
