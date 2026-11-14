package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GsonBuilder {

    private Excluder excluder;
    private LongSerializationPolicy longSerializationPolicy;
    private FieldNamingStrategy fieldNamingPolicy;
    private final Map<Type, InstanceCreator<?>> instanceCreators;
    private final List<TypeAdapterFactory> factories;
    private final List<TypeAdapterFactory> hierarchyFactories;
    private boolean serializeNulls;
    private String datePattern;
    private int dateStyle;
    private int timeStyle;
    private boolean complexMapKeySerialization;
    private boolean serializeSpecialFloatingPointValues;
    private boolean escapeHtmlChars;
    private boolean prettyPrinting;
    private boolean generateNonExecutableJson;

    public GsonBuilder() {
        this.excluder = Excluder.DEFAULT;
        this.longSerializationPolicy = LongSerializationPolicy.DEFAULT;
        this.fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
        this.instanceCreators = new HashMap();
        this.factories = new ArrayList();
        this.hierarchyFactories = new ArrayList();
        this.dateStyle = 2;
        this.timeStyle = 2;
        this.escapeHtmlChars = true;
    }

    public GsonBuilder setVersion(double d0) {
        this.excluder = this.excluder.withVersion(d0);
        return this;
    }

    public GsonBuilder excludeFieldsWithModifiers(int... aint) {
        this.excluder = this.excluder.withModifiers(aint);
        return this;
    }

    public GsonBuilder generateNonExecutableJson() {
        this.generateNonExecutableJson = true;
        return this;
    }

    public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
        this.excluder = this.excluder.excludeFieldsWithoutExposeAnnotation();
        return this;
    }

    public GsonBuilder serializeNulls() {
        this.serializeNulls = true;
        return this;
    }

    public GsonBuilder enableComplexMapKeySerialization() {
        this.complexMapKeySerialization = true;
        return this;
    }

    public GsonBuilder disableInnerClassSerialization() {
        this.excluder = this.excluder.disableInnerClassSerialization();
        return this;
    }

    public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy longserializationpolicy) {
        this.longSerializationPolicy = longserializationpolicy;
        return this;
    }

    public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy fieldnamingpolicy) {
        this.fieldNamingPolicy = fieldnamingpolicy;
        return this;
    }

    public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy fieldnamingstrategy) {
        this.fieldNamingPolicy = fieldnamingstrategy;
        return this;
    }

    public GsonBuilder setExclusionStrategies(ExclusionStrategy... aexclusionstrategy) {
        ExclusionStrategy[] aexclusionstrategy1 = aexclusionstrategy;
        int i = aexclusionstrategy.length;

        for (int j = 0; j < i; ++j) {
            ExclusionStrategy exclusionstrategy = aexclusionstrategy1[j];

            this.excluder = this.excluder.withExclusionStrategy(exclusionstrategy, true, true);
        }

        return this;
    }

    public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy exclusionstrategy) {
        this.excluder = this.excluder.withExclusionStrategy(exclusionstrategy, true, false);
        return this;
    }

    public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy exclusionstrategy) {
        this.excluder = this.excluder.withExclusionStrategy(exclusionstrategy, false, true);
        return this;
    }

    public GsonBuilder setPrettyPrinting() {
        this.prettyPrinting = true;
        return this;
    }

    public GsonBuilder disableHtmlEscaping() {
        this.escapeHtmlChars = false;
        return this;
    }

    public GsonBuilder setDateFormat(String s) {
        this.datePattern = s;
        return this;
    }

    public GsonBuilder setDateFormat(int i) {
        this.dateStyle = i;
        this.datePattern = null;
        return this;
    }

    public GsonBuilder setDateFormat(int i, int j) {
        this.dateStyle = i;
        this.timeStyle = j;
        this.datePattern = null;
        return this;
    }

    public GsonBuilder registerTypeAdapter(Type type, Object object) {
        $Gson$Preconditions.checkArgument(object instanceof JsonSerializer || object instanceof JsonDeserializer || object instanceof InstanceCreator || object instanceof TypeAdapter);
        if (object instanceof InstanceCreator) {
            this.instanceCreators.put(type, (InstanceCreator) object);
        }

        if (object instanceof JsonSerializer || object instanceof JsonDeserializer) {
            TypeToken typetoken = TypeToken.get(type);

            this.factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(typetoken, object));
        }

        if (object instanceof TypeAdapter) {
            this.factories.add(TypeAdapters.newFactory(TypeToken.get(type), (TypeAdapter) object));
        }

        return this;
    }

    public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory typeadapterfactory) {
        this.factories.add(typeadapterfactory);
        return this;
    }

    public GsonBuilder registerTypeHierarchyAdapter(Class<?> oclass, Object object) {
        $Gson$Preconditions.checkArgument(object instanceof JsonSerializer || object instanceof JsonDeserializer || object instanceof TypeAdapter);
        if (object instanceof JsonDeserializer || object instanceof JsonSerializer) {
            this.hierarchyFactories.add(0, TreeTypeAdapter.newTypeHierarchyFactory(oclass, object));
        }

        if (object instanceof TypeAdapter) {
            this.factories.add(TypeAdapters.newTypeHierarchyFactory(oclass, (TypeAdapter) object));
        }

        return this;
    }

    public GsonBuilder serializeSpecialFloatingPointValues() {
        this.serializeSpecialFloatingPointValues = true;
        return this;
    }

    public Gson create() {
        ArrayList arraylist = new ArrayList();

        arraylist.addAll(this.factories);
        Collections.reverse(arraylist);
        arraylist.addAll(this.hierarchyFactories);
        this.addTypeAdaptersForDate(this.datePattern, this.dateStyle, this.timeStyle, arraylist);
        return new Gson(this.excluder, this.fieldNamingPolicy, this.instanceCreators, this.serializeNulls, this.complexMapKeySerialization, this.generateNonExecutableJson, this.escapeHtmlChars, this.prettyPrinting, this.serializeSpecialFloatingPointValues, this.longSerializationPolicy, arraylist);
    }

    private void addTypeAdaptersForDate(String s, int i, int j, List<TypeAdapterFactory> list) {
        DefaultDateTypeAdapter defaultdatetypeadapter;

        if (s != null && !"".equals(s.trim())) {
            defaultdatetypeadapter = new DefaultDateTypeAdapter(s);
        } else {
            if (i == 2 || j == 2) {
                return;
            }

            defaultdatetypeadapter = new DefaultDateTypeAdapter(i, j);
        }

        list.add(TreeTypeAdapter.newFactory(TypeToken.get(Date.class), defaultdatetypeadapter));
        list.add(TreeTypeAdapter.newFactory(TypeToken.get(Timestamp.class), defaultdatetypeadapter));
        list.add(TreeTypeAdapter.newFactory(TypeToken.get(java.sql.Date.class), defaultdatetypeadapter));
    }
}
