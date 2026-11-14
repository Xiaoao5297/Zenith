package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.Locale;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {

    public EnumTypeAdapterFactory() {}

    @Override
    public TypeAdapter create(Gson gson, TypeToken typetoken) {
        Class oclass = typetoken.getRawType();

        if (!oclass.isEnum()) {
            return null;
        } else {
            HashMap hashmap = new HashMap();
            Object[] aobject = oclass.getEnumConstants();
            int i = aobject.length;

            for (int j = 0; j < i; ++j) {
                Object object = aobject[j];

                hashmap.put(this.func_151232_a(object), object);
            }

            return new EnumTypeAdapterFactory$1(this, hashmap);
        }
    }

    private String func_151232_a(Object object) {
        return object instanceof Enum ? ((Enum) object).name().toLowerCase(Locale.US) : object.toString().toLowerCase(Locale.US);
    }
}
