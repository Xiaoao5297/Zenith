package com.mojang.authlib.properties;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;

public class PropertyMap extends ForwardingMultimap<String, Property> {

    private final Multimap<String, Property> properties = LinkedHashMultimap.create();

    public PropertyMap() {}

    @Override
    protected Multimap<String, Property> delegate() {
        return this.properties;
    }

    public static class Serializer implements JsonSerializer<PropertyMap>, JsonDeserializer<PropertyMap> {

        public Serializer() {}

        @Override
        public PropertyMap deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            PropertyMap propertymap = new PropertyMap();

            if (jsonelement instanceof JsonObject) {
                JsonObject jsonobject = (JsonObject) jsonelement;
                Iterator iterator = jsonobject.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    if (entry.getValue() instanceof JsonArray) {
                        Iterator iterator1 = ((JsonArray) entry.getValue()).iterator();

                        while (iterator1.hasNext()) {
                            JsonElement jsonelement1 = (JsonElement) iterator1.next();

                            propertymap.put(entry.getKey(), new Property((String) entry.getKey(), jsonelement1.getAsString()));
                        }
                    }
                }
            } else if (jsonelement instanceof JsonArray) {
                Iterator iterator2 = ((JsonArray) jsonelement).iterator();

                while (iterator2.hasNext()) {
                    JsonElement jsonelement2 = (JsonElement) iterator2.next();

                    if (jsonelement2 instanceof JsonObject) {
                        JsonObject jsonobject1 = (JsonObject) jsonelement2;
                        String s = jsonobject1.getAsJsonPrimitive("name").getAsString();
                        String s1 = jsonobject1.getAsJsonPrimitive("value").getAsString();

                        if (jsonobject1.has("signature")) {
                            propertymap.put(s, new Property(s, s1, jsonobject1.getAsJsonPrimitive("signature").getAsString()));
                        } else {
                            propertymap.put(s, new Property(s, s1));
                        }
                    }
                }
            }

            return propertymap;
        }

        public JsonElement serialize(PropertyMap propertymap, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonArray jsonarray = new JsonArray();

            JsonObject jsonobject;

            for (Iterator iterator = propertymap.values().iterator(); iterator.hasNext(); jsonarray.add(jsonobject)) {
                Property property = (Property) iterator.next();

                jsonobject = new JsonObject();
                jsonobject.addProperty("name", property.getName());
                jsonobject.addProperty("value", property.getValue());
                if (property.hasSignature()) {
                    jsonobject.addProperty("signature", property.getSignature());
                }
            }

            return jsonarray;
        }
    }
}
