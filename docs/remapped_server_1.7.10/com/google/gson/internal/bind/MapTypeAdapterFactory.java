package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class MapTypeAdapterFactory implements TypeAdapterFactory {

    private final ConstructorConstructor constructorConstructor;
    private final boolean complexMapKeySerialization;

    public MapTypeAdapterFactory(ConstructorConstructor constructorconstructor, boolean flag) {
        this.constructorConstructor = constructorconstructor;
        this.complexMapKeySerialization = flag;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
        Type type = typetoken.getType();
        Class oclass = typetoken.getRawType();

        if (!Map.class.isAssignableFrom(oclass)) {
            return null;
        } else {
            Class oclass1 = $Gson$Types.getRawType(type);
            Type[] atype = $Gson$Types.getMapKeyAndValueTypes(type, oclass1);
            TypeAdapter typeadapter = this.getKeyAdapter(gson, atype[0]);
            TypeAdapter typeadapter1 = gson.getAdapter(TypeToken.get(atype[1]));
            ObjectConstructor objectconstructor = this.constructorConstructor.get(typetoken);
            MapTypeAdapterFactory.Adapter maptypeadapterfactory_adapter = new MapTypeAdapterFactory.Adapter<>(gson, atype[0], typeadapter, atype[1], typeadapter1, objectconstructor);

            return maptypeadapterfactory_adapter;
        }
    }

    private TypeAdapter<?> getKeyAdapter(Gson gson, Type type) {
        return type != Boolean.TYPE && type != Boolean.class ? gson.getAdapter(TypeToken.get(type)) : TypeAdapters.BOOLEAN_AS_STRING;
    }

    private final class Adapter<K, V> extends TypeAdapter<Map<K, V>> {

        private final TypeAdapter<K> keyTypeAdapter;
        private final TypeAdapter<V> valueTypeAdapter;
        private final ObjectConstructor<? extends Map<K, V>> constructor;

        public Adapter(Gson gson, Type type, TypeAdapter<K> typeadapter, Type type1, TypeAdapter<V> typeadapter1, ObjectConstructor<? extends Map<K, V>> objectconstructor) {
            this.keyTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(gson, typeadapter, type);
            this.valueTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(gson, typeadapter1, type1);
            this.constructor = objectconstructor;
        }

        @Override
        public Map<K, V> read(JsonReader jsonreader) throws IOException {
            JsonToken jsontoken = jsonreader.peek();

            if (jsontoken == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                Map map = (Map) this.constructor.construct();
                Object object;
                Object object1;
                Object object2;

                if (jsontoken == JsonToken.BEGIN_ARRAY) {
                    jsonreader.beginArray();

                    while (jsonreader.hasNext()) {
                        jsonreader.beginArray();
                        object = this.keyTypeAdapter.read(jsonreader);
                        object1 = this.valueTypeAdapter.read(jsonreader);
                        object2 = map.put(object, object1);
                        if (object2 != null) {
                            throw new JsonSyntaxException("duplicate key: " + object);
                        }

                        jsonreader.endArray();
                    }

                    jsonreader.endArray();
                } else {
                    jsonreader.beginObject();

                    while (jsonreader.hasNext()) {
                        JsonReaderInternalAccess.INSTANCE.promoteNameToValue(jsonreader);
                        object = this.keyTypeAdapter.read(jsonreader);
                        object1 = this.valueTypeAdapter.read(jsonreader);
                        object2 = map.put(object, object1);
                        if (object2 != null) {
                            throw new JsonSyntaxException("duplicate key: " + object);
                        }
                    }

                    jsonreader.endObject();
                }

                return map;
            }
        }

        public void write(JsonWriter jsonwriter, Map<K, V> map) throws IOException {
            if (map == null) {
                jsonwriter.nullValue();
            } else if (!MapTypeAdapterFactory.this.complexMapKeySerialization) {
                jsonwriter.beginObject();
                Iterator iterator = map.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    jsonwriter.name(String.valueOf(entry.getKey()));
                    this.valueTypeAdapter.write(jsonwriter, entry.getValue());
                }

                jsonwriter.endObject();
            } else {
                boolean flag = false;
                ArrayList arraylist = new ArrayList(map.size());
                ArrayList arraylist1 = new ArrayList(map.size());

                JsonElement jsonelement;

                for (Iterator iterator1 = map.entrySet().iterator(); iterator1.hasNext(); flag |= jsonelement.isJsonArray() || jsonelement.isJsonObject()) {
                    Entry entry1 = (Entry) iterator1.next();

                    jsonelement = this.keyTypeAdapter.toJsonTree(entry1.getKey());
                    arraylist.add(jsonelement);
                    arraylist1.add(entry1.getValue());
                }

                int i;

                if (flag) {
                    jsonwriter.beginArray();

                    for (i = 0; i < arraylist.size(); ++i) {
                        jsonwriter.beginArray();
                        Streams.write((JsonElement) arraylist.get(i), jsonwriter);
                        this.valueTypeAdapter.write(jsonwriter, arraylist1.get(i));
                        jsonwriter.endArray();
                    }

                    jsonwriter.endArray();
                } else {
                    jsonwriter.beginObject();

                    for (i = 0; i < arraylist.size(); ++i) {
                        JsonElement jsonelement1 = (JsonElement) arraylist.get(i);

                        jsonwriter.name(this.keyToString(jsonelement1));
                        this.valueTypeAdapter.write(jsonwriter, arraylist1.get(i));
                    }

                    jsonwriter.endObject();
                }

            }
        }

        private String keyToString(JsonElement jsonelement) {
            if (jsonelement.isJsonPrimitive()) {
                JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();

                if (jsonprimitive.isNumber()) {
                    return String.valueOf(jsonprimitive.getAsNumber());
                } else if (jsonprimitive.isBoolean()) {
                    return Boolean.toString(jsonprimitive.getAsBoolean());
                } else if (jsonprimitive.isString()) {
                    return jsonprimitive.getAsString();
                } else {
                    throw new AssertionError();
                }
            } else if (jsonelement.isJsonNull()) {
                return "null";
            } else {
                throw new AssertionError();
            }
        }
    }
}
