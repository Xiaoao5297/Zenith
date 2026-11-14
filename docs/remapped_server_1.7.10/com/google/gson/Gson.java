package com.google.gson;

import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import com.google.gson.internal.bind.TimeTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Gson {

    static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
    private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
    private final ThreadLocal<Map<TypeToken<?>, Gson.FutureTypeAdapter<?>>> calls;
    private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache;
    private final List<TypeAdapterFactory> factories;
    private final ConstructorConstructor constructorConstructor;
    private final boolean serializeNulls;
    private final boolean htmlSafe;
    private final boolean generateNonExecutableJson;
    private final boolean prettyPrinting;
    final JsonDeserializationContext deserializationContext;
    final JsonSerializationContext serializationContext;

    public Gson() {
        this(Excluder.DEFAULT, FieldNamingPolicy.IDENTITY, Collections.emptyMap(), false, false, false, true, false, false, LongSerializationPolicy.DEFAULT, Collections.emptyList());
    }

    Gson(Excluder excluder, FieldNamingStrategy fieldnamingstrategy, Map<Type, InstanceCreator<?>> map, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4, boolean flag5, LongSerializationPolicy longserializationpolicy, List<TypeAdapterFactory> list) {
        this.calls = new ThreadLocal();
        this.typeTokenCache = Collections.synchronizedMap(new HashMap());
        this.deserializationContext = new JsonDeserializationContext() {
            @Override
            public <T> T deserialize(JsonElement jsonelement, Type type) throws JsonParseException {
                return Gson.this.fromJson(jsonelement, type);
            }
        };
        this.serializationContext = new JsonSerializationContext() {
            @Override
            public JsonElement serialize(Object object) {
                return Gson.this.toJsonTree(object);
            }

            @Override
            public JsonElement serialize(Object object, Type type) {
                return Gson.this.toJsonTree(object, type);
            }
        };
        this.constructorConstructor = new ConstructorConstructor(map);
        this.serializeNulls = flag;
        this.generateNonExecutableJson = flag2;
        this.htmlSafe = flag3;
        this.prettyPrinting = flag4;
        ArrayList arraylist = new ArrayList();

        arraylist.add(TypeAdapters.JSON_ELEMENT_FACTORY);
        arraylist.add(ObjectTypeAdapter.FACTORY);
        arraylist.add(excluder);
        arraylist.addAll(list);
        arraylist.add(TypeAdapters.STRING_FACTORY);
        arraylist.add(TypeAdapters.INTEGER_FACTORY);
        arraylist.add(TypeAdapters.BOOLEAN_FACTORY);
        arraylist.add(TypeAdapters.BYTE_FACTORY);
        arraylist.add(TypeAdapters.SHORT_FACTORY);
        arraylist.add(TypeAdapters.newFactory(Long.TYPE, Long.class, this.longAdapter(longserializationpolicy)));
        arraylist.add(TypeAdapters.newFactory(Double.TYPE, Double.class, this.doubleAdapter(flag5)));
        arraylist.add(TypeAdapters.newFactory(Float.TYPE, Float.class, this.floatAdapter(flag5)));
        arraylist.add(TypeAdapters.NUMBER_FACTORY);
        arraylist.add(TypeAdapters.CHARACTER_FACTORY);
        arraylist.add(TypeAdapters.STRING_BUILDER_FACTORY);
        arraylist.add(TypeAdapters.STRING_BUFFER_FACTORY);
        arraylist.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
        arraylist.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
        arraylist.add(TypeAdapters.URL_FACTORY);
        arraylist.add(TypeAdapters.URI_FACTORY);
        arraylist.add(TypeAdapters.UUID_FACTORY);
        arraylist.add(TypeAdapters.LOCALE_FACTORY);
        arraylist.add(TypeAdapters.INET_ADDRESS_FACTORY);
        arraylist.add(TypeAdapters.BIT_SET_FACTORY);
        arraylist.add(DateTypeAdapter.FACTORY);
        arraylist.add(TypeAdapters.CALENDAR_FACTORY);
        arraylist.add(TimeTypeAdapter.FACTORY);
        arraylist.add(SqlDateTypeAdapter.FACTORY);
        arraylist.add(TypeAdapters.TIMESTAMP_FACTORY);
        arraylist.add(ArrayTypeAdapter.FACTORY);
        arraylist.add(TypeAdapters.ENUM_FACTORY);
        arraylist.add(TypeAdapters.CLASS_FACTORY);
        arraylist.add(new CollectionTypeAdapterFactory(this.constructorConstructor));
        arraylist.add(new MapTypeAdapterFactory(this.constructorConstructor, flag1));
        arraylist.add(new ReflectiveTypeAdapterFactory(this.constructorConstructor, fieldnamingstrategy, excluder));
        this.factories = Collections.unmodifiableList(arraylist);
    }

    private TypeAdapter<Number> doubleAdapter(boolean flag) {
        return flag ? TypeAdapters.DOUBLE : new TypeAdapter<Number>() {
            @Override
            public Double read(JsonReader jsonreader) throws IOException {
                if (jsonreader.peek() == JsonToken.NULL) {
                    jsonreader.nextNull();
                    return null;
                } else {
                    return jsonreader.nextDouble();
                }
            }

            public void write(JsonWriter jsonwriter, Number number) throws IOException {
                if (number == null) {
                    jsonwriter.nullValue();
                } else {
                    double d0 = number.doubleValue();

                    Gson.this.checkValidFloatingPoint(d0);
                    jsonwriter.value(number);
                }
            }
        };
    }

    private TypeAdapter<Number> floatAdapter(boolean flag) {
        return flag ? TypeAdapters.FLOAT : new TypeAdapter<Number>() {
            @Override
            public Float read(JsonReader jsonreader) throws IOException {
                if (jsonreader.peek() == JsonToken.NULL) {
                    jsonreader.nextNull();
                    return null;
                } else {
                    return (float) jsonreader.nextDouble();
                }
            }

            public void write(JsonWriter jsonwriter, Number number) throws IOException {
                if (number == null) {
                    jsonwriter.nullValue();
                } else {
                    float f = number.floatValue();

                    Gson.this.checkValidFloatingPoint((double) f);
                    jsonwriter.value(number);
                }
            }
        };
    }

    private void checkValidFloatingPoint(double d0) {
        if (Double.isNaN(d0) || Double.isInfinite(d0)) {
            throw new IllegalArgumentException(d0 + " is not a valid double value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
        }
    }

    private TypeAdapter<Number> longAdapter(LongSerializationPolicy longserializationpolicy) {
        return longserializationpolicy == LongSerializationPolicy.DEFAULT ? TypeAdapters.LONG : new TypeAdapter<Number>() {
            @Override
            public Number read(JsonReader jsonreader) throws IOException {
                if (jsonreader.peek() == JsonToken.NULL) {
                    jsonreader.nextNull();
                    return null;
                } else {
                    return jsonreader.nextLong();
                }
            }

            public void write(JsonWriter jsonwriter, Number number) throws IOException {
                if (number == null) {
                    jsonwriter.nullValue();
                } else {
                    jsonwriter.value(number.toString());
                }
            }
        };
    }

    public <T> TypeAdapter<T> getAdapter(TypeToken<T> typetoken) {
        TypeAdapter typeadapter = (TypeAdapter) this.typeTokenCache.get(typetoken);

        if (typeadapter != null) {
            return typeadapter;
        } else {
            Object object = (Map) this.calls.get();
            boolean flag = false;

            if (object == null) {
                object = new HashMap();
                this.calls.set(object);
                flag = true;
            }

            Gson.FutureTypeAdapter gson_futuretypeadapter = (Gson.FutureTypeAdapter) ((Map) object).get(typetoken);

            if (gson_futuretypeadapter != null) {
                return gson_futuretypeadapter;
            } else {
                TypeAdapter typeadapter1;

                try {
                    Gson.FutureTypeAdapter gson_futuretypeadapter1 = new Gson.FutureTypeAdapter<>();

                    ((Map) object).put(typetoken, gson_futuretypeadapter1);
                    Iterator iterator = this.factories.iterator();

                    TypeAdapter typeadapter2;

                    do {
                        if (!iterator.hasNext()) {
                            throw new IllegalArgumentException("GSON cannot handle " + typetoken);
                        }

                        TypeAdapterFactory typeadapterfactory = (TypeAdapterFactory) iterator.next();

                        typeadapter2 = typeadapterfactory.create(this, typetoken);
                    } while (typeadapter2 == null);

                    gson_futuretypeadapter1.setDelegate(typeadapter2);
                    this.typeTokenCache.put(typetoken, typeadapter2);
                    typeadapter1 = typeadapter2;
                } finally {
                    ((Map) object).remove(typetoken);
                    if (flag) {
                        this.calls.remove();
                    }

                }

                return typeadapter1;
            }
        }
    }

    public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory typeadapterfactory, TypeToken<T> typetoken) {
        boolean flag = false;
        Iterator iterator = this.factories.iterator();

        while (iterator.hasNext()) {
            TypeAdapterFactory typeadapterfactory1 = (TypeAdapterFactory) iterator.next();

            if (!flag) {
                if (typeadapterfactory1 == typeadapterfactory) {
                    flag = true;
                }
            } else {
                TypeAdapter typeadapter = typeadapterfactory1.create(this, typetoken);

                if (typeadapter != null) {
                    return typeadapter;
                }
            }
        }

        throw new IllegalArgumentException("GSON cannot serialize " + typetoken);
    }

    public <T> TypeAdapter<T> getAdapter(Class<T> oclass) {
        return this.getAdapter(TypeToken.get(oclass));
    }

    public JsonElement toJsonTree(Object object) {
        return (JsonElement) (object == null ? JsonNull.INSTANCE : this.toJsonTree(object, object.getClass()));
    }

    public JsonElement toJsonTree(Object object, Type type) {
        JsonTreeWriter jsontreewriter = new JsonTreeWriter();

        this.toJson(object, type, (JsonWriter) jsontreewriter);
        return jsontreewriter.get();
    }

    public String toJson(Object object) {
        return object == null ? this.toJson((JsonElement) JsonNull.INSTANCE) : this.toJson(object, (Type) object.getClass());
    }

    public String toJson(Object object, Type type) {
        StringWriter stringwriter = new StringWriter();

        this.toJson(object, type, (Appendable) stringwriter);
        return stringwriter.toString();
    }

    public void toJson(Object object, Appendable appendable) throws JsonIOException {
        if (object != null) {
            this.toJson(object, object.getClass(), appendable);
        } else {
            this.toJson((JsonElement) JsonNull.INSTANCE, appendable);
        }

    }

    public void toJson(Object object, Type type, Appendable appendable) throws JsonIOException {
        try {
            JsonWriter jsonwriter = this.newJsonWriter(Streams.writerForAppendable(appendable));

            this.toJson(object, type, jsonwriter);
        } catch (IOException ioexception) {
            throw new JsonIOException(ioexception);
        }
    }

    public void toJson(Object object, Type type, JsonWriter jsonwriter) throws JsonIOException {
        TypeAdapter typeadapter = this.getAdapter(TypeToken.get(type));
        boolean flag = jsonwriter.isLenient();

        jsonwriter.setLenient(true);
        boolean flag1 = jsonwriter.isHtmlSafe();

        jsonwriter.setHtmlSafe(this.htmlSafe);
        boolean flag2 = jsonwriter.getSerializeNulls();

        jsonwriter.setSerializeNulls(this.serializeNulls);

        try {
            typeadapter.write(jsonwriter, object);
        } catch (IOException ioexception) {
            throw new JsonIOException(ioexception);
        } finally {
            jsonwriter.setLenient(flag);
            jsonwriter.setHtmlSafe(flag1);
            jsonwriter.setSerializeNulls(flag2);
        }

    }

    public String toJson(JsonElement jsonelement) {
        StringWriter stringwriter = new StringWriter();

        this.toJson(jsonelement, (Appendable) stringwriter);
        return stringwriter.toString();
    }

    public void toJson(JsonElement jsonelement, Appendable appendable) throws JsonIOException {
        try {
            JsonWriter jsonwriter = this.newJsonWriter(Streams.writerForAppendable(appendable));

            this.toJson(jsonelement, jsonwriter);
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }
    }

    private JsonWriter newJsonWriter(Writer writer) throws IOException {
        if (this.generateNonExecutableJson) {
            writer.write(")]}'\n");
        }

        JsonWriter jsonwriter = new JsonWriter(writer);

        if (this.prettyPrinting) {
            jsonwriter.setIndent("  ");
        }

        jsonwriter.setSerializeNulls(this.serializeNulls);
        return jsonwriter;
    }

    public void toJson(JsonElement jsonelement, JsonWriter jsonwriter) throws JsonIOException {
        boolean flag = jsonwriter.isLenient();

        jsonwriter.setLenient(true);
        boolean flag1 = jsonwriter.isHtmlSafe();

        jsonwriter.setHtmlSafe(this.htmlSafe);
        boolean flag2 = jsonwriter.getSerializeNulls();

        jsonwriter.setSerializeNulls(this.serializeNulls);

        try {
            Streams.write(jsonelement, jsonwriter);
        } catch (IOException ioexception) {
            throw new JsonIOException(ioexception);
        } finally {
            jsonwriter.setLenient(flag);
            jsonwriter.setHtmlSafe(flag1);
            jsonwriter.setSerializeNulls(flag2);
        }

    }

    public <T> T fromJson(String s, Class<T> oclass) throws JsonSyntaxException {
        Object object = this.fromJson(s, (Type) oclass);

        return Primitives.wrap(oclass).cast(object);
    }

    public <T> T fromJson(String s, Type type) throws JsonSyntaxException {
        if (s == null) {
            return null;
        } else {
            StringReader stringreader = new StringReader(s);
            Object object = this.fromJson((Reader) stringreader, type);

            return object;
        }
    }

    public <T> T fromJson(Reader reader, Class<T> oclass) throws JsonSyntaxException, JsonIOException {
        JsonReader jsonreader = new JsonReader(reader);
        Object object = this.fromJson(jsonreader, (Type) oclass);

        assertFullConsumption(object, jsonreader);
        return Primitives.wrap(oclass).cast(object);
    }

    public <T> T fromJson(Reader reader, Type type) throws JsonIOException, JsonSyntaxException {
        JsonReader jsonreader = new JsonReader(reader);
        Object object = this.fromJson(jsonreader, type);

        assertFullConsumption(object, jsonreader);
        return object;
    }

    private static void assertFullConsumption(Object object, JsonReader jsonreader) {
        try {
            if (object != null && jsonreader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }
        } catch (MalformedJsonException malformedjsonexception) {
            throw new JsonSyntaxException(malformedjsonexception);
        } catch (IOException ioexception) {
            throw new JsonIOException(ioexception);
        }
    }

    public <T> T fromJson(JsonReader jsonreader, Type type) throws JsonIOException, JsonSyntaxException {
        boolean flag = true;
        boolean flag1 = jsonreader.isLenient();

        jsonreader.setLenient(true);

        TypeAdapter typeadapter;

        try {
            jsonreader.peek();
            flag = false;
            TypeToken typetoken = TypeToken.get(type);

            typeadapter = this.getAdapter(typetoken);
            Object object = typeadapter.read(jsonreader);
            Object object1 = object;

            return object1;
        } catch (EOFException eofexception) {
            if (!flag) {
                throw new JsonSyntaxException(eofexception);
            }

            typeadapter = null;
        } catch (IllegalStateException illegalstateexception) {
            throw new JsonSyntaxException(illegalstateexception);
        } catch (IOException ioexception) {
            throw new JsonSyntaxException(ioexception);
        } finally {
            jsonreader.setLenient(flag1);
        }

        return typeadapter;
    }

    public <T> T fromJson(JsonElement jsonelement, Class<T> oclass) throws JsonSyntaxException {
        Object object = this.fromJson(jsonelement, (Type) oclass);

        return Primitives.wrap(oclass).cast(object);
    }

    public <T> T fromJson(JsonElement jsonelement, Type type) throws JsonSyntaxException {
        return jsonelement == null ? null : this.fromJson((JsonReader) (new JsonTreeReader(jsonelement)), type);
    }

    public String toString() {
        return "{serializeNulls:" + this.serializeNulls + "factories:" + this.factories + ",instanceCreators:" + this.constructorConstructor + "}";
    }

    static class FutureTypeAdapter<T> extends TypeAdapter<T> {

        private TypeAdapter<T> delegate;

        FutureTypeAdapter() {}

        public void setDelegate(TypeAdapter<T> typeadapter) {
            if (this.delegate != null) {
                throw new AssertionError();
            } else {
                this.delegate = typeadapter;
            }
        }

        @Override
        public T read(JsonReader jsonreader) throws IOException {
            if (this.delegate == null) {
                throw new IllegalStateException();
            } else {
                return this.delegate.read(jsonreader);
            }
        }

        @Override
        public void write(JsonWriter jsonwriter, T t0) throws IOException {
            if (this.delegate == null) {
                throw new IllegalStateException();
            } else {
                this.delegate.write(jsonwriter, t0);
            }
        }
    }
}
