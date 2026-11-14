package net.minecraft.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.util.Map;

class EnumTypeAdapterFactory$1 extends TypeAdapter {

    EnumTypeAdapterFactory$1(EnumTypeAdapterFactory enumtypeadapterfactory, Map map) {
        this.field_151230_b = enumtypeadapterfactory;
        this.field_151231_a = map;
    }

    @Override
    public void write(JsonWriter jsonwriter, Object object) {
        if (object == null) {
            jsonwriter.nullValue();
        } else {
            jsonwriter.value(EnumTypeAdapterFactory.access$000(this.field_151230_b, object));
        }

    }

    @Override
    public Object read(JsonReader jsonreader) {
        if (jsonreader.peek() == JsonToken.NULL) {
            jsonreader.nextNull();
            return null;
        } else {
            return this.field_151231_a.get(jsonreader.nextString());
        }
    }
}
