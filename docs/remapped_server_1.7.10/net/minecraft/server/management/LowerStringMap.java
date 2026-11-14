package net.minecraft.server.management;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LowerStringMap implements Map {

    private final Map field_76117_a = new LinkedHashMap();

    public LowerStringMap() {}

    public int size() {
        return this.field_76117_a.size();
    }

    public boolean isEmpty() {
        return this.field_76117_a.isEmpty();
    }

    public boolean containsKey(Object object) {
        return this.field_76117_a.containsKey(object.toString().toLowerCase());
    }

    public boolean containsValue(Object object) {
        return this.field_76117_a.containsKey(object);
    }

    public Object get(Object object) {
        return this.field_76117_a.get(object.toString().toLowerCase());
    }

    public Object put(String s, Object object) {
        return this.field_76117_a.put(s.toLowerCase(), object);
    }

    public Object remove(Object object) {
        return this.field_76117_a.remove(object.toString().toLowerCase());
    }

    public void putAll(Map map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((String) entry.getKey(), entry.getValue());
        }

    }

    public void clear() {
        this.field_76117_a.clear();
    }

    public Set keySet() {
        return this.field_76117_a.keySet();
    }

    public Collection values() {
        return this.field_76117_a.values();
    }

    public Set entrySet() {
        return this.field_76117_a.entrySet();
    }
}
