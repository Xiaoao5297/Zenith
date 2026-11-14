package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public final class ArrayTypeAdapter<E> extends TypeAdapter<Object> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
            Type type = typetoken.getType();

            if (!(type instanceof GenericArrayType) && (!(type instanceof Class) || !((Class) type).isArray())) {
                return null;
            } else {
                Type type1 = $Gson$Types.getArrayComponentType(type);
                TypeAdapter typeadapter = gson.getAdapter(TypeToken.get(type1));

                return new ArrayTypeAdapter<>(gson, typeadapter, $Gson$Types.getRawType(type1));
            }
        }
    };
    private final Class<E> componentType;
    private final TypeAdapter<E> componentTypeAdapter;

    public ArrayTypeAdapter(Gson gson, TypeAdapter<E> typeadapter, Class<E> oclass) {
        this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(gson, typeadapter, oclass);
        this.componentType = oclass;
    }

    @Override
    public Object read(JsonReader jsonreader) throws IOException {
        if (jsonreader.peek() == JsonToken.NULL) {
            jsonreader.nextNull();
            return null;
        } else {
            ArrayList arraylist = new ArrayList();

            jsonreader.beginArray();

            Object object;

            while (jsonreader.hasNext()) {
                object = this.componentTypeAdapter.read(jsonreader);
                arraylist.add(object);
            }

            jsonreader.endArray();
            object = Array.newInstance(this.componentType, arraylist.size());

            for (int i = 0; i < arraylist.size(); ++i) {
                Array.set(object, i, arraylist.get(i));
            }

            return object;
        }
    }

    @Override
    public void write(JsonWriter jsonwriter, Object object) throws IOException {
        if (object == null) {
            jsonwriter.nullValue();
        } else {
            jsonwriter.beginArray();
            int i = 0;

            for (int j = Array.getLength(object); i < j; ++i) {
                Object object1 = Array.get(object, i);

                this.componentTypeAdapter.write(jsonwriter, object1);
            }

            jsonwriter.endArray();
        }
    }
}
