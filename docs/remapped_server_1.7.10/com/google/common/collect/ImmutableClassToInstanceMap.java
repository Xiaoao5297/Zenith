package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public final class ImmutableClassToInstanceMap<B> extends ForwardingMap<Class<? extends B>, B> implements ClassToInstanceMap<B>, Serializable {

    private final ImmutableMap<Class<? extends B>, B> delegate;

    public static <B> ImmutableClassToInstanceMap.Builder<B> builder() {
        return new ImmutableClassToInstanceMap.Builder<>();
    }

    public static <B, S extends B> ImmutableClassToInstanceMap<B> copyOf(Map<? extends Class<? extends S>, ? extends S> map) {
        if (map instanceof ImmutableClassToInstanceMap) {
            ImmutableClassToInstanceMap immutableclasstoinstancemap = (ImmutableClassToInstanceMap) map;

            return immutableclasstoinstancemap;
        } else {
            return (new ImmutableClassToInstanceMap.Builder<>()).putAll(map).build();
        }
    }

    private ImmutableClassToInstanceMap(ImmutableMap<Class<? extends B>, B> immutablemap) {
        this.delegate = immutablemap;
    }

    @Override
    protected Map<Class<? extends B>, B> delegate() {
        return this.delegate;
    }

    @Nullable
    @Override
    public <T extends B> T getInstance(Class<T> oclass) {
        return this.delegate.get(Preconditions.checkNotNull(oclass));
    }

    /** @deprecated */
    @Deprecated
    @Override
    public <T extends B> T putInstance(Class<T> oclass, T t0) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder<B> {

        private final ImmutableMap.Builder<Class<? extends B>, B> mapBuilder = ImmutableMap.builder();

        public Builder() {}

        public <T extends B> ImmutableClassToInstanceMap.Builder<B> put(Class<T> oclass, T t0) {
            this.mapBuilder.put(oclass, t0);
            return this;
        }

        public <T extends B> ImmutableClassToInstanceMap.Builder<B> putAll(Map<? extends Class<? extends T>, ? extends T> map) {
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                Class oclass = (Class) entry.getKey();
                Object object = entry.getValue();

                this.mapBuilder.put(oclass, cast(oclass, object));
            }

            return this;
        }

        private static <B, T extends B> T cast(Class<T> oclass, B b0) {
            return Primitives.wrap(oclass).cast(b0);
        }

        public ImmutableClassToInstanceMap<B> build() {
            return new ImmutableClassToInstanceMap<>(this.mapBuilder.build());
        }
    }
}
