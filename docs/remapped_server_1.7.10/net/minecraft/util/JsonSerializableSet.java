package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Iterator;
import java.util.Set;

public class JsonSerializableSet extends ForwardingSet implements IJsonSerializable {

    private final Set field_151004_a = Sets.newHashSet();

    public JsonSerializableSet() {}

    @Override
    public void func_152753_a(JsonElement jsonelement) {
        if (jsonelement.isJsonArray()) {
            Iterator iterator = jsonelement.getAsJsonArray().iterator();

            while (iterator.hasNext()) {
                JsonElement jsonelement1 = (JsonElement) iterator.next();

                this.add(jsonelement1.getAsString());
            }
        }

    }

    @Override
    public JsonElement func_151003_a() {
        JsonArray jsonarray = new JsonArray();
        Iterator iterator = this.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            jsonarray.add(new JsonPrimitive(s));
        }

        return jsonarray;
    }

    @Override
    protected Set delegate() {
        return this.field_151004_a;
    }
}
