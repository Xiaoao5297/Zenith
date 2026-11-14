package com.google.gson;

import java.lang.reflect.Type;

public interface JsonDeserializer<T> {

    T deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException;
}
