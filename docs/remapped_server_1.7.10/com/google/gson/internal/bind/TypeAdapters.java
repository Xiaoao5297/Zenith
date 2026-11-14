package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;

public final class TypeAdapters {

    public static final TypeAdapter<Class> CLASS = new TypeAdapter<Class>() {
        public void write(JsonWriter jsonwriter, Class oclass) throws IOException {
            if (oclass == null) {
                jsonwriter.nullValue();
            } else {
                throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + oclass.getName() + ". Forgot to register a type adapter?");
            }
        }

        @Override
        public Class read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
            }
        }
    };
    public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, TypeAdapters.CLASS);
    public static final TypeAdapter<BitSet> BIT_SET = new TypeAdapter<BitSet>() {
        @Override
        public BitSet read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                BitSet bitset = new BitSet();

                jsonreader.beginArray();
                int i = 0;

                for (JsonToken jsontoken = jsonreader.peek(); jsontoken != JsonToken.END_ARRAY; jsontoken = jsonreader.peek()) {
                    boolean flag;

                    switch (jsontoken) {
                        case NUMBER:
                            flag = jsonreader.nextInt() != 0;
                            break;
                        case BOOLEAN:
                            flag = jsonreader.nextBoolean();
                            break;
                        case STRING:
                            String s = jsonreader.nextString();

                            try {
                                flag = Integer.parseInt(s) != 0;
                                break;
                            } catch (NumberFormatException numberformatexception) {
                                throw new JsonSyntaxException("Error: Expecting: bitset number value (1, 0), Found: " + s);
                            }
                        default:
                            throw new JsonSyntaxException("Invalid bitset value type: " + jsontoken);
                    }

                    if (flag) {
                        bitset.set(i);
                    }

                    ++i;
                }

                jsonreader.endArray();
                return bitset;
            }
        }

        public void write(JsonWriter jsonwriter, BitSet bitset) throws IOException {
            if (bitset == null) {
                jsonwriter.nullValue();
            } else {
                jsonwriter.beginArray();

                for (int i = 0; i < bitset.length(); ++i) {
                    int j = bitset.get(i) ? 1 : 0;

                    jsonwriter.value((long) j);
                }

                jsonwriter.endArray();
            }
        }
    };
    public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, TypeAdapters.BIT_SET);
    public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
        @Override
        public Boolean read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return jsonreader.peek() == JsonToken.STRING ? Boolean.parseBoolean(jsonreader.nextString()) : jsonreader.nextBoolean();
            }
        }

        public void write(JsonWriter jsonwriter, Boolean obool) throws IOException {
            if (obool == null) {
                jsonwriter.nullValue();
            } else {
                jsonwriter.value(obool);
            }
        }
    };
    public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
        @Override
        public Boolean read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return Boolean.valueOf(jsonreader.nextString());
            }
        }

        public void write(JsonWriter jsonwriter, Boolean obool) throws IOException {
            jsonwriter.value(obool == null ? "null" : obool.toString());
        }
    };
    public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(Boolean.TYPE, Boolean.class, TypeAdapters.BOOLEAN);
    public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                try {
                    int i = jsonreader.nextInt();

                    return (byte) i;
                } catch (NumberFormatException numberformatexception) {
                    throw new JsonSyntaxException(numberformatexception);
                }
            }
        }

        public void write(JsonWriter jsonwriter, Number number) throws IOException {
            jsonwriter.value(number);
        }
    };
    public static final TypeAdapterFactory BYTE_FACTORY = newFactory(Byte.TYPE, Byte.class, TypeAdapters.BYTE);
    public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                try {
                    return (short) jsonreader.nextInt();
                } catch (NumberFormatException numberformatexception) {
                    throw new JsonSyntaxException(numberformatexception);
                }
            }
        }

        public void write(JsonWriter jsonwriter, Number number) throws IOException {
            jsonwriter.value(number);
        }
    };
    public static final TypeAdapterFactory SHORT_FACTORY = newFactory(Short.TYPE, Short.class, TypeAdapters.SHORT);
    public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                try {
                    return jsonreader.nextInt();
                } catch (NumberFormatException numberformatexception) {
                    throw new JsonSyntaxException(numberformatexception);
                }
            }
        }

        public void write(JsonWriter jsonwriter, Number number) throws IOException {
            jsonwriter.value(number);
        }
    };
    public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(Integer.TYPE, Integer.class, TypeAdapters.INTEGER);
    public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                try {
                    return jsonreader.nextLong();
                } catch (NumberFormatException numberformatexception) {
                    throw new JsonSyntaxException(numberformatexception);
                }
            }
        }

        public void write(JsonWriter jsonwriter, Number number) throws IOException {
            jsonwriter.value(number);
        }
    };
    public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return (float) jsonreader.nextDouble();
            }
        }

        public void write(JsonWriter jsonwriter, Number number) throws IOException {
            jsonwriter.value(number);
        }
    };
    public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return jsonreader.nextDouble();
            }
        }

        public void write(JsonWriter jsonwriter, Number number) throws IOException {
            jsonwriter.value(number);
        }
    };
    public static final TypeAdapter<Number> NUMBER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader jsonreader) throws IOException {
            JsonToken jsontoken = jsonreader.peek();

            switch (jsontoken) {
                case NUMBER:
                    return new LazilyParsedNumber(jsonreader.nextString());
                case NULL:
                    jsonreader.nextNull();
                    return null;
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsontoken);
            }
        }

        public void write(JsonWriter jsonwriter, Number number) throws IOException {
            jsonwriter.value(number);
        }
    };
    public static final TypeAdapterFactory NUMBER_FACTORY = newFactory(Number.class, TypeAdapters.NUMBER);
    public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>() {
        @Override
        public Character read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                String s = jsonreader.nextString();

                if (s.length() != 1) {
                    throw new JsonSyntaxException("Expecting character, got: " + s);
                } else {
                    return s.charAt(0);
                }
            }
        }

        public void write(JsonWriter jsonwriter, Character character) throws IOException {
            jsonwriter.value(character == null ? null : String.valueOf(character));
        }
    };
    public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(Character.TYPE, Character.class, TypeAdapters.CHARACTER);
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        @Override
        public String read(JsonReader jsonreader) throws IOException {
            JsonToken jsontoken = jsonreader.peek();

            if (jsontoken == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return jsontoken == JsonToken.BOOLEAN ? Boolean.toString(jsonreader.nextBoolean()) : jsonreader.nextString();
            }
        }

        public void write(JsonWriter jsonwriter, String s) throws IOException {
            jsonwriter.value(s);
        }
    };
    public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
        @Override
        public BigDecimal read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                try {
                    return new BigDecimal(jsonreader.nextString());
                } catch (NumberFormatException numberformatexception) {
                    throw new JsonSyntaxException(numberformatexception);
                }
            }
        }

        public void write(JsonWriter jsonwriter, BigDecimal bigdecimal) throws IOException {
            jsonwriter.value((Number) bigdecimal);
        }
    };
    public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>() {
        @Override
        public BigInteger read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                try {
                    return new BigInteger(jsonreader.nextString());
                } catch (NumberFormatException numberformatexception) {
                    throw new JsonSyntaxException(numberformatexception);
                }
            }
        }

        public void write(JsonWriter jsonwriter, BigInteger biginteger) throws IOException {
            jsonwriter.value((Number) biginteger);
        }
    };
    public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, TypeAdapters.STRING);
    public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>() {
        @Override
        public StringBuilder read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return new StringBuilder(jsonreader.nextString());
            }
        }

        public void write(JsonWriter jsonwriter, StringBuilder stringbuilder) throws IOException {
            jsonwriter.value(stringbuilder == null ? null : stringbuilder.toString());
        }
    };
    public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, TypeAdapters.STRING_BUILDER);
    public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>() {
        @Override
        public StringBuffer read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return new StringBuffer(jsonreader.nextString());
            }
        }

        public void write(JsonWriter jsonwriter, StringBuffer stringbuffer) throws IOException {
            jsonwriter.value(stringbuffer == null ? null : stringbuffer.toString());
        }
    };
    public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, TypeAdapters.STRING_BUFFER);
    public static final TypeAdapter<URL> URL = new TypeAdapter<URL>() {
        @Override
        public URL read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                String s = jsonreader.nextString();

                return "null".equals(s) ? null : new URL(s);
            }
        }

        public void write(JsonWriter jsonwriter, URL url) throws IOException {
            jsonwriter.value(url == null ? null : url.toExternalForm());
        }
    };
    public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, TypeAdapters.URL);
    public static final TypeAdapter<URI> URI = new TypeAdapter<URI>() {
        @Override
        public URI read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                try {
                    String s = jsonreader.nextString();

                    return "null".equals(s) ? null : new URI(s);
                } catch (URISyntaxException urisyntaxexception) {
                    throw new JsonIOException(urisyntaxexception);
                }
            }
        }

        public void write(JsonWriter jsonwriter, URI uri) throws IOException {
            jsonwriter.value(uri == null ? null : uri.toASCIIString());
        }
    };
    public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, TypeAdapters.URI);
    public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>() {
        @Override
        public InetAddress read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return InetAddress.getByName(jsonreader.nextString());
            }
        }

        public void write(JsonWriter jsonwriter, InetAddress inetaddress) throws IOException {
            jsonwriter.value(inetaddress == null ? null : inetaddress.getHostAddress());
        }
    };
    public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, TypeAdapters.INET_ADDRESS);
    public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>() {
        @Override
        public UUID read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return java.util.UUID.fromString(jsonreader.nextString());
            }
        }

        public void write(JsonWriter jsonwriter, UUID uuid) throws IOException {
            jsonwriter.value(uuid == null ? null : uuid.toString());
        }
    };
    public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, TypeAdapters.UUID);
    public static final TypeAdapterFactory TIMESTAMP_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
            if (typetoken.getRawType() != Timestamp.class) {
                return null;
            } else {
                final TypeAdapter typeadapter = gson.getAdapter(Date.class);

                return new TypeAdapter<Timestamp>() {
                    @Override
                    public Timestamp read(JsonReader jsonreader) throws IOException {
                        Date date = (Date) typeadapter.read(jsonreader);

                        return date != null ? new Timestamp(date.getTime()) : null;
                    }

                    public void write(JsonWriter jsonwriter, Timestamp timestamp) throws IOException {
                        typeadapter.write(jsonwriter, timestamp);
                    }
                };
            }
        }
    };
    public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>() {
        private static final String YEAR = "year";
        private static final String MONTH = "month";
        private static final String DAY_OF_MONTH = "dayOfMonth";
        private static final String HOUR_OF_DAY = "hourOfDay";
        private static final String MINUTE = "minute";
        private static final String SECOND = "second";

        @Override
        public Calendar read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                jsonreader.beginObject();
                int i = 0;
                int j = 0;
                int k = 0;
                int l = 0;
                int i1 = 0;
                int j1 = 0;

                while (jsonreader.peek() != JsonToken.END_OBJECT) {
                    String s = jsonreader.nextName();
                    int k1 = jsonreader.nextInt();

                    if ("year".equals(s)) {
                        i = k1;
                    } else if ("month".equals(s)) {
                        j = k1;
                    } else if ("dayOfMonth".equals(s)) {
                        k = k1;
                    } else if ("hourOfDay".equals(s)) {
                        l = k1;
                    } else if ("minute".equals(s)) {
                        i1 = k1;
                    } else if ("second".equals(s)) {
                        j1 = k1;
                    }
                }

                jsonreader.endObject();
                return new GregorianCalendar(i, j, k, l, i1, j1);
            }
        }

        public void write(JsonWriter jsonwriter, Calendar calendar) throws IOException {
            if (calendar == null) {
                jsonwriter.nullValue();
            } else {
                jsonwriter.beginObject();
                jsonwriter.name("year");
                jsonwriter.value((long) calendar.get(1));
                jsonwriter.name("month");
                jsonwriter.value((long) calendar.get(2));
                jsonwriter.name("dayOfMonth");
                jsonwriter.value((long) calendar.get(5));
                jsonwriter.name("hourOfDay");
                jsonwriter.value((long) calendar.get(11));
                jsonwriter.name("minute");
                jsonwriter.value((long) calendar.get(12));
                jsonwriter.name("second");
                jsonwriter.value((long) calendar.get(13));
                jsonwriter.endObject();
            }
        }
    };
    public static final TypeAdapterFactory CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, TypeAdapters.CALENDAR);
    public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>() {
        @Override
        public Locale read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                String s = jsonreader.nextString();
                StringTokenizer stringtokenizer = new StringTokenizer(s, "_");
                String s1 = null;
                String s2 = null;
                String s3 = null;

                if (stringtokenizer.hasMoreElements()) {
                    s1 = stringtokenizer.nextToken();
                }

                if (stringtokenizer.hasMoreElements()) {
                    s2 = stringtokenizer.nextToken();
                }

                if (stringtokenizer.hasMoreElements()) {
                    s3 = stringtokenizer.nextToken();
                }

                return s2 == null && s3 == null ? new Locale(s1) : (s3 == null ? new Locale(s1, s2) : new Locale(s1, s2, s3));
            }
        }

        public void write(JsonWriter jsonwriter, Locale locale) throws IOException {
            jsonwriter.value(locale == null ? null : locale.toString());
        }
    };
    public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, TypeAdapters.LOCALE);
    public static final TypeAdapter<JsonElement> JSON_ELEMENT = new TypeAdapter<JsonElement>() {
        @Override
        public JsonElement read(JsonReader jsonreader) throws IOException {
            switch (jsonreader.peek()) {
                case NUMBER:
                    String s = jsonreader.nextString();

                    return new JsonPrimitive(new LazilyParsedNumber(s));
                case BOOLEAN:
                    return new JsonPrimitive(jsonreader.nextBoolean());
                case STRING:
                    return new JsonPrimitive(jsonreader.nextString());
                case NULL:
                    jsonreader.nextNull();
                    return JsonNull.INSTANCE;
                case BEGIN_ARRAY:
                    JsonArray jsonarray = new JsonArray();

                    jsonreader.beginArray();

                    while (jsonreader.hasNext()) {
                        jsonarray.add(this.read(jsonreader));
                    }

                    jsonreader.endArray();
                    return jsonarray;
                case BEGIN_OBJECT:
                    JsonObject jsonobject = new JsonObject();

                    jsonreader.beginObject();

                    while (jsonreader.hasNext()) {
                        jsonobject.add(jsonreader.nextName(), this.read(jsonreader));
                    }

                    jsonreader.endObject();
                    return jsonobject;
                case END_DOCUMENT:
                case NAME:
                case END_OBJECT:
                case END_ARRAY:
                default:
                    throw new IllegalArgumentException();
            }
        }

        public void write(JsonWriter jsonwriter, JsonElement jsonelement) throws IOException {
            if (jsonelement != null && !jsonelement.isJsonNull()) {
                if (jsonelement.isJsonPrimitive()) {
                    JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();

                    if (jsonprimitive.isNumber()) {
                        jsonwriter.value(jsonprimitive.getAsNumber());
                    } else if (jsonprimitive.isBoolean()) {
                        jsonwriter.value(jsonprimitive.getAsBoolean());
                    } else {
                        jsonwriter.value(jsonprimitive.getAsString());
                    }
                } else {
                    Iterator iterator;

                    if (jsonelement.isJsonArray()) {
                        jsonwriter.beginArray();
                        iterator = jsonelement.getAsJsonArray().iterator();

                        while (iterator.hasNext()) {
                            JsonElement jsonelement1 = (JsonElement) iterator.next();

                            this.write(jsonwriter, jsonelement1);
                        }

                        jsonwriter.endArray();
                    } else {
                        if (!jsonelement.isJsonObject()) {
                            throw new IllegalArgumentException("Couldn't write " + jsonelement.getClass());
                        }

                        jsonwriter.beginObject();
                        iterator = jsonelement.getAsJsonObject().entrySet().iterator();

                        while (iterator.hasNext()) {
                            Entry entry = (Entry) iterator.next();

                            jsonwriter.name((String) entry.getKey());
                            this.write(jsonwriter, (JsonElement) entry.getValue());
                        }

                        jsonwriter.endObject();
                    }
                }
            } else {
                jsonwriter.nullValue();
            }

        }
    };
    public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, TypeAdapters.JSON_ELEMENT);
    public static final TypeAdapterFactory ENUM_FACTORY = newEnumTypeHierarchyFactory();

    private TypeAdapters() {}

    public static TypeAdapterFactory newEnumTypeHierarchyFactory() {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
                Class oclass = typetoken.getRawType();

                if (Enum.class.isAssignableFrom(oclass) && oclass != Enum.class) {
                    if (!oclass.isEnum()) {
                        oclass = oclass.getSuperclass();
                    }

                    return new TypeAdapters.EnumTypeAdapter<>(oclass);
                } else {
                    return null;
                }
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> typetoken, final TypeAdapter<TT> typeadapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken1) {
                return typetoken1.equals(typetoken) ? typeadapter : null;
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(final Class<TT> oclass, final TypeAdapter<TT> typeadapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
                return typetoken.getRawType() == oclass ? typeadapter : null;
            }

            public String toString() {
                return "Factory[type=" + oclass.getName() + ",adapter=" + typeadapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(final Class<TT> oclass, final Class<TT> oclass1, final TypeAdapter<? super TT> typeadapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
                Class oclass2 = typetoken.getRawType();

                return oclass2 != oclass && oclass2 != oclass1 ? null : typeadapter;
            }

            public String toString() {
                return "Factory[type=" + oclass1.getName() + "+" + oclass.getName() + ",adapter=" + typeadapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> oclass, final Class<? extends TT> oclass1, final TypeAdapter<? super TT> typeadapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
                Class oclass2 = typetoken.getRawType();

                return oclass2 != oclass && oclass2 != oclass1 ? null : typeadapter;
            }

            public String toString() {
                return "Factory[type=" + oclass.getName() + "+" + oclass1.getName() + ",adapter=" + typeadapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newTypeHierarchyFactory(final Class<TT> oclass, final TypeAdapter<TT> typeadapter) {
        return new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typetoken) {
                return oclass.isAssignableFrom(typetoken.getRawType()) ? typeadapter : null;
            }

            public String toString() {
                return "Factory[typeHierarchy=" + oclass.getName() + ",adapter=" + typeadapter + "]";
            }
        };
    }

    private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {

        private final Map<String, T> nameToConstant = new HashMap();
        private final Map<T, String> constantToName = new HashMap();

        public EnumTypeAdapter(Class<T> oclass) {
            try {
                Enum[] aenum = (Enum[]) oclass.getEnumConstants();
                int i = aenum.length;

                for (int j = 0; j < i; ++j) {
                    Enum oenum = aenum[j];
                    String s = oenum.name();
                    SerializedName serializedname = (SerializedName) oclass.getField(s).getAnnotation(SerializedName.class);

                    if (serializedname != null) {
                        s = serializedname.value();
                    }

                    this.nameToConstant.put(s, oenum);
                    this.constantToName.put(oenum, s);
                }

            } catch (NoSuchFieldException nosuchfieldexception) {
                throw new AssertionError();
            }
        }

        @Override
        public T read(JsonReader jsonreader) throws IOException {
            if (jsonreader.peek() == JsonToken.NULL) {
                jsonreader.nextNull();
                return null;
            } else {
                return (Enum) this.nameToConstant.get(jsonreader.nextString());
            }
        }

        public void write(JsonWriter jsonwriter, T t0) throws IOException {
            jsonwriter.value(t0 == null ? null : (String) this.constantToName.get(t0));
        }
    }
}
