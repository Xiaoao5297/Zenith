package com.google.gson;

public final class JsonNull extends JsonElement {

    public static final JsonNull INSTANCE = new JsonNull();

    /** @deprecated */
    @Deprecated
    public JsonNull() {}

    @Override
    JsonNull deepCopy() {
        return JsonNull.INSTANCE;
    }

    public int hashCode() {
        return JsonNull.class.hashCode();
    }

    public boolean equals(Object object) {
        return this == object || object instanceof JsonNull;
    }
}
