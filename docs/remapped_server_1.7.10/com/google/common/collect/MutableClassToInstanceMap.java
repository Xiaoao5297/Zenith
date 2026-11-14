package com.google.common.collect;

import com.google.common.primitives.Primitives;
import java.util.HashMap;
import java.util.Map;

public final class MutableClassToInstanceMap<B> extends MapConstraints.ConstrainedMap<Class<? extends B>, B> implements ClassToInstanceMap<B> {

    private static final MapConstraint<Class<?>, Object> VALUE_CAN_BE_CAST_TO_KEY = new MapConstraint<Class<?>, Object>() {
        public void checkKeyValue(Class<?> oclass, Object object) {
            MutableClassToInstanceMap.cast(oclass, object);
        }
    };
    private static final long serialVersionUID = 0L;

    public static <B> MutableClassToInstanceMap<B> create() {
        return new MutableClassToInstanceMap<>(new HashMap());
    }

    public static <B> MutableClassToInstanceMap<B> create(Map<Class<? extends B>, B> map) {
        return new MutableClassToInstanceMap<>(map);
    }

    private MutableClassToInstanceMap(Map<Class<? extends B>, B> map) {
        super(map, MutableClassToInstanceMap.VALUE_CAN_BE_CAST_TO_KEY);
    }

    @Override
    public <T extends B> T putInstance(Class<T> oclass, T t0) {
        return cast(oclass, this.put(oclass, t0));
    }

    @Override
    public <T extends B> T getInstance(Class<T> oclass) {
        return cast(oclass, this.get(oclass));
    }

    private static <B, T extends B> T cast(Class<T> oclass, B b0) {
        return Primitives.wrap(oclass).cast(b0);
    }
}
