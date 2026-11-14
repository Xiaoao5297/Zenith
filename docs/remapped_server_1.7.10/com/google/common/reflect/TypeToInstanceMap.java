package com.google.common.reflect;

import com.google.common.annotations.Beta;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
public interface TypeToInstanceMap<B> extends Map<TypeToken<? extends B>, B> {

    @Nullable
    <T extends B> T getInstance(Class<T> oclass);

    @Nullable
    <T extends B> T putInstance(Class<T> oclass, @Nullable T t0);

    @Nullable
    <T extends B> T getInstance(TypeToken<T> typetoken);

    @Nullable
    <T extends B> T putInstance(TypeToken<T> typetoken, @Nullable T t0);
}
