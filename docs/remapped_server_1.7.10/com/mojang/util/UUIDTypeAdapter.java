package com.mojang.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.UUID;

public class UUIDTypeAdapter extends TypeAdapter<UUID> {

    public UUIDTypeAdapter() {}

    public void write(JsonWriter jsonwriter, UUID uuid) throws IOException {
        jsonwriter.value(fromUUID(uuid));
    }

    @Override
    public UUID read(JsonReader jsonreader) throws IOException {
        return fromString(jsonreader.nextString());
    }

    public static String fromUUID(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static UUID fromString(String s) {
        return UUID.fromString(s.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
