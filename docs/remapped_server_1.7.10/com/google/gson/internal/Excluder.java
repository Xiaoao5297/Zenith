package com.google.gson.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class Excluder implements TypeAdapterFactory, Cloneable {

    private static final double IGNORE_VERSIONS = -1.0D;
    public static final Excluder DEFAULT = new Excluder();
    private double version = -1.0D;
    private int modifiers = 136;
    private boolean serializeInnerClasses = true;
    private boolean requireExpose;
    private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
    private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();

    public Excluder() {}

    protected Excluder clone() {
        try {
            return (Excluder) super.clone();
        } catch (CloneNotSupportedException clonenotsupportedexception) {
            throw new AssertionError();
        }
    }

    public Excluder withVersion(double d0) {
        Excluder excluder = this.clone();

        excluder.version = d0;
        return excluder;
    }

    public Excluder withModifiers(int... aint) {
        Excluder excluder = this.clone();

        excluder.modifiers = 0;
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint1[j];

            excluder.modifiers |= k;
        }

        return excluder;
    }

    public Excluder disableInnerClassSerialization() {
        Excluder excluder = this.clone();

        excluder.serializeInnerClasses = false;
        return excluder;
    }

    public Excluder excludeFieldsWithoutExposeAnnotation() {
        Excluder excluder = this.clone();

        excluder.requireExpose = true;
        return excluder;
    }

    public Excluder withExclusionStrategy(ExclusionStrategy exclusionstrategy, boolean flag, boolean flag1) {
        Excluder excluder = this.clone();

        if (flag) {
            excluder.serializationStrategies = new ArrayList(this.serializationStrategies);
            excluder.serializationStrategies.add(exclusionstrategy);
        }

        if (flag1) {
            excluder.deserializationStrategies = new ArrayList(this.deserializationStrategies);
            excluder.deserializationStrategies.add(exclusionstrategy);
        }

        return excluder;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typetoken) {
        Class oclass = typetoken.getRawType();
        final boolean flag = this.excludeClass(oclass, true);
        final boolean flag1 = this.excludeClass(oclass, false);

        return !flag && !flag1 ? null : new TypeAdapter<T>() {
            private TypeAdapter<T> delegate;

            @Override
            public T read(JsonReader jsonreader) throws IOException {
                if (flag1) {
                    jsonreader.skipValue();
                    return null;
                } else {
                    return this.delegate().read(jsonreader);
                }
            }

            @Override
            public void write(JsonWriter jsonwriter, T t0) throws IOException {
                if (flag) {
                    jsonwriter.nullValue();
                } else {
                    this.delegate().write(jsonwriter, t0);
                }
            }

            private TypeAdapter<T> delegate() {
                TypeAdapter typeadapter = this.delegate;

                return typeadapter != null ? typeadapter : (this.delegate = gson.getDelegateAdapter(Excluder.this, typetoken));
            }
        };
    }

    public boolean excludeField(Field field, boolean flag) {
        if ((this.modifiers & field.getModifiers()) != 0) {
            return true;
        } else if (this.version != -1.0D && !this.isValidVersion((Since) field.getAnnotation(Since.class), (Until) field.getAnnotation(Until.class))) {
            return true;
        } else if (field.isSynthetic()) {
            return true;
        } else {
            if (this.requireExpose) {
                label57:
                {
                    Expose expose = (Expose) field.getAnnotation(Expose.class);

                    if (expose != null) {
                        if (flag) {
                            if (expose.serialize()) {
                                break label57;
                            }
                        } else if (expose.deserialize()) {
                            break label57;
                        }
                    }

                    return true;
                }
            }

            if (!this.serializeInnerClasses && this.isInnerClass(field.getType())) {
                return true;
            } else if (this.isAnonymousOrLocal(field.getType())) {
                return true;
            } else {
                List list = flag ? this.serializationStrategies : this.deserializationStrategies;

                if (!list.isEmpty()) {
                    FieldAttributes fieldattributes = new FieldAttributes(field);
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        ExclusionStrategy exclusionstrategy = (ExclusionStrategy) iterator.next();

                        if (exclusionstrategy.shouldSkipField(fieldattributes)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public boolean excludeClass(Class<?> oclass, boolean flag) {
        if (this.version != -1.0D && !this.isValidVersion((Since) oclass.getAnnotation(Since.class), (Until) oclass.getAnnotation(Until.class))) {
            return true;
        } else if (!this.serializeInnerClasses && this.isInnerClass(oclass)) {
            return true;
        } else if (this.isAnonymousOrLocal(oclass)) {
            return true;
        } else {
            List list = flag ? this.serializationStrategies : this.deserializationStrategies;
            Iterator iterator = list.iterator();

            ExclusionStrategy exclusionstrategy;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                exclusionstrategy = (ExclusionStrategy) iterator.next();
            } while (!exclusionstrategy.shouldSkipClass(oclass));

            return true;
        }
    }

    private boolean isAnonymousOrLocal(Class<?> oclass) {
        return !Enum.class.isAssignableFrom(oclass) && (oclass.isAnonymousClass() || oclass.isLocalClass());
    }

    private boolean isInnerClass(Class<?> oclass) {
        return oclass.isMemberClass() && !this.isStatic(oclass);
    }

    private boolean isStatic(Class<?> oclass) {
        return (oclass.getModifiers() & 8) != 0;
    }

    private boolean isValidVersion(Since since, Until until) {
        return this.isValidSince(since) && this.isValidUntil(until);
    }

    private boolean isValidSince(Since since) {
        if (since != null) {
            double d0 = since.value();

            if (d0 > this.version) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidUntil(Until until) {
        if (until != null) {
            double d0 = until.value();

            if (d0 <= this.version) {
                return false;
            }
        }

        return true;
    }
}
