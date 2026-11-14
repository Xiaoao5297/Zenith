package com.google.gson;

public enum LongSerializationPolicy {

    DEFAULT {
        @Override
        public JsonElement serialize(Long olong) {
            return new JsonPrimitive(olong);
        }
    },
    STRING {
        @Override
        public JsonElement serialize(Long olong) {
            return new JsonPrimitive(String.valueOf(olong));
        }
    };

    private LongSerializationPolicy() {}

    public abstract JsonElement serialize(Long olong);
}
