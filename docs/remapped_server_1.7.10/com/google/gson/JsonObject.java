package com.google.gson;

import com.google.gson.internal.LinkedTreeMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public final class JsonObject extends JsonElement {

    private final LinkedTreeMap<String, JsonElement> members = new LinkedTreeMap<>();

    public JsonObject() {}

    @Override
    JsonObject deepCopy() {
        JsonObject jsonobject = new JsonObject();
        Iterator iterator = this.members.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            jsonobject.add((String) entry.getKey(), ((JsonElement) entry.getValue()).deepCopy());
        }

        return jsonobject;
    }

    public void add(String s, JsonElement jsonelement) {
        if (jsonelement == null) {
            jsonelement = JsonNull.INSTANCE;
        }

        this.members.put(s, jsonelement);
    }

    public JsonElement remove(String s) {
        return (JsonElement) this.members.remove(s);
    }

    public void addProperty(String s, String s1) {
        this.add(s, this.createJsonElement(s1));
    }

    public void addProperty(String s, Number number) {
        this.add(s, this.createJsonElement(number));
    }

    public void addProperty(String s, Boolean obool) {
        this.add(s, this.createJsonElement(obool));
    }

    public void addProperty(String s, Character character) {
        this.add(s, this.createJsonElement(character));
    }

    private JsonElement createJsonElement(Object object) {
        return (JsonElement) (object == null ? JsonNull.INSTANCE : new JsonPrimitive(object));
    }

    public Set<Entry<String, JsonElement>> entrySet() {
        return this.members.entrySet();
    }

    public boolean has(String s) {
        return this.members.containsKey(s);
    }

    public JsonElement get(String s) {
        return (JsonElement) this.members.get(s);
    }

    public JsonPrimitive getAsJsonPrimitive(String s) {
        return (JsonPrimitive) this.members.get(s);
    }

    public JsonArray getAsJsonArray(String s) {
        return (JsonArray) this.members.get(s);
    }

    public JsonObject getAsJsonObject(String s) {
        return (JsonObject) this.members.get(s);
    }

    public boolean equals(Object object) {
        return object == this || object instanceof JsonObject && ((JsonObject) object).members.equals(this.members);
    }

    public int hashCode() {
        return this.members.hashCode();
    }
}
