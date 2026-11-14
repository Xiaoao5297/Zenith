package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {

    private final Gson context;
    private final TypeAdapter<T> delegate;
    private final Type type;

    TypeAdapterRuntimeTypeWrapper(Gson gson, TypeAdapter<T> typeadapter, Type type) {
        this.context = gson;
        this.delegate = typeadapter;
        this.type = type;
    }

    @Override
    public T read(JsonReader jsonreader) throws IOException {
        return this.delegate.read(jsonreader);
    }

    @Override
    public void write(JsonWriter jsonwriter, T t0) throws IOException {
        TypeAdapter typeadapter = this.delegate;
        Type type = this.getRuntimeTypeIfMoreSpecific(this.type, t0);

        if (type != this.type) {
            TypeAdapter typeadapter1 = this.context.getAdapter(TypeToken.get(type));

            if (!(typeadapter1 instanceof ReflectiveTypeAdapterFactory.Adapter)) {
                typeadapter = typeadapter1;
            } else if (!(this.delegate instanceof ReflectiveTypeAdapterFactory.Adapter)) {
                typeadapter = this.delegate;
            } else {
                typeadapter = typeadapter1;
            }
        }

        typeadapter.write(jsonwriter, t0);
    }

    private Type getRuntimeTypeIfMoreSpecific(Type type, Object object) {
        if (object != null && (type == Object.class || type instanceof TypeVariable || type instanceof Class)) {
            type = object.getClass();
        }

        return (Type) type;
    }
}
