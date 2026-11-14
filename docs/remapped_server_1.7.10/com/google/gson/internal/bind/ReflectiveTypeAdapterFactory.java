package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {

    private final ConstructorConstructor constructorConstructor;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final Excluder excluder;

    public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorconstructor, FieldNamingStrategy fieldnamingstrategy, Excluder excluder) {
        this.constructorConstructor = constructorconstructor;
        this.fieldNamingPolicy = fieldnamingstrategy;
        this.excluder = excluder;
    }

    public boolean excludeField(Field field, boolean flag) {
        return !this.excluder.excludeClass(field.getType(), flag) && !this.excluder.excludeField(field, flag);
    }

    private String getFieldName(Field field) {
        SerializedName serializedname = (SerializedName) field.getAnnotation(SerializedName.class);

        return serializedname == null ? this.fieldNamingPolicy.translateName(field) : serializedname.value();
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
        Class oclass = typetoken.getRawType();

        if (!Object.class.isAssignableFrom(oclass)) {
            return null;
        } else {
            ObjectConstructor objectconstructor = this.constructorConstructor.get(typetoken);

            return new ReflectiveTypeAdapterFactory.Adapter<>(objectconstructor, this.getBoundFields(gson, typetoken, oclass));
        }
    }

    private ReflectiveTypeAdapterFactory.BoundField createBoundField(final Gson gson, final Field field, String s, final TypeToken<?> typetoken, boolean flag, boolean flag1) {
        final boolean flag2 = Primitives.isPrimitive(typetoken.getRawType());

        return new ReflectiveTypeAdapterFactory.BoundField(s, flag, flag1) {
            final TypeAdapter<?> typeAdapter = gson.getAdapter(typetoken);

            @Override
            void write(JsonWriter jsonwriter, Object object) throws IOException, IllegalAccessException {
                Object object1 = field.get(object);
                TypeAdapterRuntimeTypeWrapper typeadapterruntimetypewrapper = new TypeAdapterRuntimeTypeWrapper<>(gson, this.typeAdapter, typetoken.getType());

                typeadapterruntimetypewrapper.write(jsonwriter, object1);
            }

            @Override
            void read(JsonReader jsonreader, Object object) throws IOException, IllegalAccessException {
                Object object1 = this.typeAdapter.read(jsonreader);

                if (object1 != null || !flag2) {
                    field.set(object, object1);
                }

            }
        };
    }

    private Map<String, ReflectiveTypeAdapterFactory.BoundField> getBoundFields(Gson gson, TypeToken<?> typetoken, Class<?> oclass) {
        LinkedHashMap linkedhashmap = new LinkedHashMap();

        if (oclass.isInterface()) {
            return linkedhashmap;
        } else {
            for (Type type = typetoken.getType(); oclass != Object.class; oclass = typetoken.getRawType()) {
                Field[] afield = oclass.getDeclaredFields();
                Field[] afield1 = afield;
                int i = afield.length;

                for (int j = 0; j < i; ++j) {
                    Field field = afield1[j];
                    boolean flag = this.excludeField(field, true);
                    boolean flag1 = this.excludeField(field, false);

                    if (flag || flag1) {
                        field.setAccessible(true);
                        Type type1 = $Gson$Types.resolve(typetoken.getType(), oclass, field.getGenericType());
                        ReflectiveTypeAdapterFactory.BoundField reflectivetypeadapterfactory_boundfield = this.createBoundField(gson, field, this.getFieldName(field), TypeToken.get(type1), flag, flag1);
                        ReflectiveTypeAdapterFactory.BoundField reflectivetypeadapterfactory_boundfield1 = (ReflectiveTypeAdapterFactory.BoundField) linkedhashmap.put(reflectivetypeadapterfactory_boundfield.name, reflectivetypeadapterfactory_boundfield);

                        if (reflectivetypeadapterfactory_boundfield1 != null) {
                            throw new IllegalArgumentException(type + " declares multiple JSON fields named " + reflectivetypeadapterfactory_boundfield1.name);
                        }
                    }
                }

                typetoken = TypeToken.get($Gson$Types.resolve(typetoken.getType(), oclass, oclass.getGenericSuperclass()));
            }

            return linkedhashmap;
        }
    }

    public static final class Adapter<T> extends TypeAdapter<T> {

        private final ObjectConstructor<T> constructor;
        private final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;

        private Adapter(ObjectConstructor<T> objectconstructor, Map<String, ReflectiveTypeAdapterFactory.BoundField> map) {
            this.constructor = objectconstructor;
            this.boundFields = map;
        }

        @Override
        public T read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                Object object = this.constructor.construct();

                try {
                    jsonreader.beginObject();

                    while (jsonreader.hasNext()) {
                        String s = jsonreader.nextName();
                        ReflectiveTypeAdapterFactory.BoundField reflectivetypeadapterfactory_boundfield = (ReflectiveTypeAdapterFactory.BoundField) this.boundFields.get(s);

                        if (reflectivetypeadapterfactory_boundfield != null && reflectivetypeadapterfactory_boundfield.deserialized) {
                            reflectivetypeadapterfactory_boundfield.read(jsonreader, object);
                        } else {
                            jsonreader.skipValue();
                        }
                    }
                } catch (IllegalStateException illegalstateexception) {
                    throw new JsonSyntaxException(illegalstateexception);
                } catch (IllegalAccessException illegalaccessexception) {
                    throw new AssertionError(illegalaccessexception);
                }

                jsonreader.endObject();
                return object;
            }
        }

        @Override
        public void write(JsonWriter jsonwriter, T t0) throws IOException {
            if (t0 == null) {
                jsonwriter.nullValue();
            } else {
                jsonwriter.beginObject();

                try {
                    Iterator iterator = this.boundFields.values().iterator();

                    while (iterator.hasNext()) {
                        ReflectiveTypeAdapterFactory.BoundField reflectivetypeadapterfactory_boundfield = (ReflectiveTypeAdapterFactory.BoundField) iterator.next();

                        if (reflectivetypeadapterfactory_boundfield.serialized) {
                            jsonwriter.name(reflectivetypeadapterfactory_boundfield.name);
                            reflectivetypeadapterfactory_boundfield.write(jsonwriter, t0);
                        }
                    }
                } catch (IllegalAccessException illegalaccessexception) {
                    throw new AssertionError();
                }

                jsonwriter.endObject();
            }
        }
    }

    abstract static class BoundField {

        final String name;
        final boolean serialized;
        final boolean deserialized;

        protected BoundField(String s, boolean flag, boolean flag1) {
            this.name = s;
            this.serialized = flag;
            this.deserialized = flag1;
        }

        abstract void write(JsonWriter jsonwriter, Object object) throws IOException, IllegalAccessException;

        abstract void read(JsonReader jsonreader, Object object) throws IOException, IllegalAccessException;
    }
}
