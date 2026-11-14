package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class ObjectTypeAdapter extends TypeAdapter<Object> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
            return typetoken.getRawType() == Object.class ? new ObjectTypeAdapter(gson) : null;
        }
    };
    private final Gson gson;

    private ObjectTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Object read(JsonReader jsonreader) throws IOException {
        JsonToken jsontoken = jsonreader.peek();

        switch (jsontoken) {
            case BEGIN_ARRAY:
                ArrayList arraylist = new ArrayList();

                jsonreader.beginArray();

                while (jsonreader.hasNext()) {
                    arraylist.add(this.read(jsonreader));
                }

                jsonreader.endArray();
                return arraylist;
            case BEGIN_OBJECT:
                LinkedTreeMap linkedtreemap = new LinkedTreeMap<>();

                jsonreader.beginObject();

                while (jsonreader.hasNext()) {
                    linkedtreemap.put(jsonreader.nextName(), this.read(jsonreader));
                }

                jsonreader.endObject();
                return linkedtreemap;
            case STRING:
                return jsonreader.nextString();
            case NUMBER:
                return jsonreader.nextDouble();
            case BOOLEAN:
                return jsonreader.nextBoolean();
            case NULL:
                jsonreader.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void write(JsonWriter jsonwriter, Object object) throws IOException {
        if (object == null) {
            jsonwriter.nullValue();
        } else {
            TypeAdapter typeadapter = this.gson.getAdapter(object.getClass());

            if (typeadapter instanceof ObjectTypeAdapter) {
                jsonwriter.beginObject();
                jsonwriter.endObject();
            } else {
                typeadapter.write(jsonwriter, object);
            }
        }
    }
}
