package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

@Beta
public final class ImmutableTypeToInstanceMap<B> extends ForwardingMap<TypeToken<? extends B>, B> implements TypeToInstanceMap<B> {

    private final ImmutableMap<TypeToken<? extends B>, B> delegate;

    public static <B> ImmutableTypeToInstanceMap<B> of() {
        return new ImmutableTypeToInstanceMap<>(ImmutableMap.of());
    }

    public static <B> ImmutableTypeToInstanceMap.Builder<B> builder() {
        return new ImmutableTypeToInstanceMap.Builder<>();
    }

    private ImmutableTypeToInstanceMap(ImmutableMap<TypeToken<? extends B>, B> immutablemap) {
        this.delegate = immutablemap;
    }

    @Override
    public <T extends B> T getInstance(TypeToken<T> typetoken) {
        return this.trustedGet(typetoken.rejectTypeVariables());
    }

    @Override
    public <T extends B> T putInstance(TypeToken<T> typetoken, T t0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends B> T getInstance(Class<T> oclass) {
        return this.trustedGet(TypeToken.of(oclass));
    }

    @Override
    public <T extends B> T putInstance(Class<T> oclass, T t0) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Map<TypeToken<? extends B>, B> delegate() {
        return this.delegate;
    }

    private <T extends B> T trustedGet(TypeToken<T> typetoken) {
        return this.delegate.get(typetoken);
    }

    @Beta
    public static final class Builder<B> {

        private final ImmutableMap.Builder<TypeToken<? extends B>, B> mapBuilder;

        private Builder() {
            this.mapBuilder = ImmutableMap.builder();
        }

        public <T extends B> ImmutableTypeToInstanceMap.Builder<B> put(Class<T> oclass, T t0) {
            this.mapBuilder.put(TypeToken.of(oclass), t0);
            return this;
        }

        public <T extends B> ImmutableTypeToInstanceMap.Builder<B> put(TypeToken<T> typetoken, T t0) {
            this.mapBuilder.put(typetoken.rejectTypeVariables(), t0);
            return this;
        }

        public ImmutableTypeToInstanceMap<B> build() {
            return new ImmutableTypeToInstanceMap<>(this.mapBuilder.build());
        }
    }
}
