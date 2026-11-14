package com.google.gson;

import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public abstract class TypeAdapter<T> {

    public TypeAdapter() {}

    public abstract void write(JsonWriter jsonwriter, T t0) throws IOException;

    public final void toJson(Writer writer, T t0) throws IOException {
        JsonWriter jsonwriter = new JsonWriter(writer);

        this.write(jsonwriter, t0);
    }

    public final TypeAdapter<T> nullSafe() {
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter jsonwriter, T t0) throws IOException {
                if (t0 == null) {
                    jsonwriter.nullValue();
                } else {
                    TypeAdapter.this.write(jsonwriter, t0);
                }

            }

            @Override
            public T read(JsonReader jsonreader) throws IOException {
                if (jsonreader.peek() == JsonToken.NULL) {
                    jsonreader.nextNull();
                    return null;
                } else {
                    return TypeAdapter.this.read(jsonreader);
                }
            }
        };
    }

    public final String toJson(T t0) throws IOException {
        StringWriter stringwriter = new StringWriter();

        this.toJson(stringwriter, t0);
        return stringwriter.toString();
    }

    public final JsonElement toJsonTree(T t0) {
        try {
            JsonTreeWriter jsontreewriter = new JsonTreeWriter();

            this.write(jsontreewriter, t0);
            return jsontreewriter.get();
        } catch (IOException ioexception) {
            throw new JsonIOException(ioexception);
        }
    }

    public abstract T read(JsonReader jsonreader) throws IOException;

    public final T fromJson(Reader reader) throws IOException {
        JsonReader jsonreader = new JsonReader(reader);

        return this.read(jsonreader);
    }

    public final T fromJson(String s) throws IOException {
        return this.fromJson((Reader) (new StringReader(s)));
    }

    public final T fromJsonTree(JsonElement jsonelement) {
        try {
            JsonTreeReader jsontreereader = new JsonTreeReader(jsonelement);

            return this.read(jsontreereader);
        } catch (IOException ioexception) {
            throw new JsonIOException(ioexception);
        }
    }
}
