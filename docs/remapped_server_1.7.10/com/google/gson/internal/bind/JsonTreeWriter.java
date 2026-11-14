package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class JsonTreeWriter extends JsonWriter {

    private static final Writer UNWRITABLE_WRITER = new Writer() {
        public void write(char[] achar, int i, int j) {
            throw new AssertionError();
        }

        public void flush() throws IOException {
            throw new AssertionError();
        }

        public void close() throws IOException {
            throw new AssertionError();
        }
    };
    private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive("closed");
    private final List<JsonElement> stack = new ArrayList();
    private String pendingName;
    private JsonElement product;

    public JsonTreeWriter() {
        super(JsonTreeWriter.UNWRITABLE_WRITER);
        this.product = JsonNull.INSTANCE;
    }

    public JsonElement get() {
        if (!this.stack.isEmpty()) {
            throw new IllegalStateException("Expected one JSON element but was " + this.stack);
        } else {
            return this.product;
        }
    }

    private JsonElement peek() {
        return (JsonElement) this.stack.get(this.stack.size() - 1);
    }

    private void put(JsonElement jsonelement) {
        if (this.pendingName != null) {
            if (!jsonelement.isJsonNull() || this.getSerializeNulls()) {
                JsonObject jsonobject = (JsonObject) this.peek();

                jsonobject.add(this.pendingName, jsonelement);
            }

            this.pendingName = null;
        } else if (this.stack.isEmpty()) {
            this.product = jsonelement;
        } else {
            JsonElement jsonelement1 = this.peek();

            if (!(jsonelement1 instanceof JsonArray)) {
                throw new IllegalStateException();
            }

            ((JsonArray) jsonelement1).add(jsonelement);
        }

    }

    @Override
    public JsonWriter beginArray() throws IOException {
        JsonArray jsonarray = new JsonArray();

        this.put(jsonarray);
        this.stack.add(jsonarray);
        return this;
    }

    @Override
    public JsonWriter endArray() throws IOException {
        if (!this.stack.isEmpty() && this.pendingName == null) {
            JsonElement jsonelement = this.peek();

            if (jsonelement instanceof JsonArray) {
                this.stack.remove(this.stack.size() - 1);
                return this;
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public JsonWriter beginObject() throws IOException {
        JsonObject jsonobject = new JsonObject();

        this.put(jsonobject);
        this.stack.add(jsonobject);
        return this;
    }

    @Override
    public JsonWriter endObject() throws IOException {
        if (!this.stack.isEmpty() && this.pendingName == null) {
            JsonElement jsonelement = this.peek();

            if (jsonelement instanceof JsonObject) {
                this.stack.remove(this.stack.size() - 1);
                return this;
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public JsonWriter name(String s) throws IOException {
        if (!this.stack.isEmpty() && this.pendingName == null) {
            JsonElement jsonelement = this.peek();

            if (jsonelement instanceof JsonObject) {
                this.pendingName = s;
                return this;
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public JsonWriter value(String s) throws IOException {
        if (s == null) {
            return this.nullValue();
        } else {
            this.put(new JsonPrimitive(s));
            return this;
        }
    }

    @Override
    public JsonWriter nullValue() throws IOException {
        this.put(JsonNull.INSTANCE);
        return this;
    }

    @Override
    public JsonWriter value(boolean flag) throws IOException {
        this.put(new JsonPrimitive(flag));
        return this;
    }

    @Override
    public JsonWriter value(double d0) throws IOException {
        if (!this.isLenient() && (Double.isNaN(d0) || Double.isInfinite(d0))) {
            throw new IllegalArgumentException("JSON forbids NaN and infinities: " + d0);
        } else {
            this.put(new JsonPrimitive(d0));
            return this;
        }
    }

    @Override
    public JsonWriter value(long i) throws IOException {
        this.put(new JsonPrimitive(i));
        return this;
    }

    @Override
    public JsonWriter value(Number number) throws IOException {
        if (number == null) {
            return this.nullValue();
        } else {
            if (!this.isLenient()) {
                double d0 = number.doubleValue();

                if (Double.isNaN(d0) || Double.isInfinite(d0)) {
                    throw new IllegalArgumentException("JSON forbids NaN and infinities: " + number);
                }
            }

            this.put(new JsonPrimitive(number));
            return this;
        }
    }

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {
        if (!this.stack.isEmpty()) {
            throw new IOException("Incomplete document");
        } else {
            this.stack.add(JsonTreeWriter.SENTINEL_CLOSED);
        }
    }
}
