package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

final class TreeTypeAdapter<T> extends TypeAdapter<T> {

    private final JsonSerializer<T> serializer;
    private final JsonDeserializer<T> deserializer;
    private final Gson gson;
    private final TypeToken<T> typeToken;
    private final TypeAdapterFactory skipPast;
    private TypeAdapter<T> delegate;

    private TreeTypeAdapter(JsonSerializer<T> jsonserializer, JsonDeserializer<T> jsondeserializer, Gson gson, TypeToken<T> typetoken, TypeAdapterFactory typeadapterfactory) {
        this.serializer = jsonserializer;
        this.deserializer = jsondeserializer;
        this.gson = gson;
        this.typeToken = typetoken;
        this.skipPast = typeadapterfactory;
    }

    @Override
    public T read(JsonReader jsonreader) throws IOException {
        if (this.deserializer == null) {
            return this.delegate().read(jsonreader);
        } else {
            JsonElement jsonelement = Streams.parse(jsonreader);

            return jsonelement.isJsonNull() ? null : this.deserializer.deserialize(jsonelement, this.typeToken.getType(), this.gson.deserializationContext);
        }
    }

    @Override
    public void write(JsonWriter jsonwriter, T t0) throws IOException {
        if (this.serializer == null) {
            this.delegate().write(jsonwriter, t0);
        } else if (t0 == null) {
            jsonwriter.nullValue();
        } else {
            JsonElement jsonelement = this.serializer.serialize(t0, this.typeToken.getType(), this.gson.serializationContext);

            Streams.write(jsonelement, jsonwriter);
        }
    }

    private TypeAdapter<T> delegate() {
        TypeAdapter typeadapter = this.delegate;

        return typeadapter != null ? typeadapter : (this.delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken));
    }

    public static TypeAdapterFactory newFactory(TypeToken<?> typetoken, Object object) {
        return new TreeTypeAdapter.SingleTypeFactory(object, typetoken, false, (Class) null);
    }

    public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken<?> typetoken, Object object) {
        boolean flag = typetoken.getType() == typetoken.getRawType();

        return new TreeTypeAdapter.SingleTypeFactory(object, typetoken, flag, (Class) null);
    }

    public static TypeAdapterFactory newTypeHierarchyFactory(Class<?> oclass, Object object) {
        return new TreeTypeAdapter.SingleTypeFactory(object, (TypeToken) null, false, oclass);
    }

    private static class SingleTypeFactory implements TypeAdapterFactory {

        private final TypeToken<?> exactType;
        private final boolean matchRawType;
        private final Class<?> hierarchyType;
        private final JsonSerializer<?> serializer;
        private final JsonDeserializer<?> deserializer;

        private SingleTypeFactory(Object object, TypeToken<?> typetoken, boolean flag, Class<?> oclass) {
            this.serializer = object instanceof JsonSerializer ? (JsonSerializer) object : null;
            this.deserializer = object instanceof JsonDeserializer ? (JsonDeserializer) object : null;
            $Gson$Preconditions.checkArgument(this.serializer != null || this.deserializer != null);
            this.exactType = typetoken;
            this.matchRawType = flag;
            this.hierarchyType = oclass;
        }

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
            boolean flag = this.exactType != null ? this.exactType.equals(typetoken) || this.matchRawType && this.exactType.getType() == typetoken.getRawType() : this.hierarchyType.isAssignableFrom(typetoken.getRawType());

            return flag ? new TreeTypeAdapter<>(this.serializer, this.deserializer, gson, typetoken, this) : null;
        }
    }
}
