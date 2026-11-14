package com.google.gson;

import java.lang.reflect.Type;

public interface JsonSerializationContext {

    JsonElement serialize(Object object);

    JsonElement serialize(Object object, Type type);
}
