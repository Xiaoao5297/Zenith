package net.minecraft.server.management;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

final class PlayerProfileCache$2 implements ParameterizedType {

    PlayerProfileCache$2() {}

    public Type[] getActualTypeArguments() {
        return new Type[]{PlayerProfileCache$ProfileEntry.class};
    }

    public Type getRawType() {
        return List.class;
    }

    public Type getOwnerType() {
        return null;
    }
}
