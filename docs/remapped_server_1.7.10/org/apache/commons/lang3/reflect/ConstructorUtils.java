package org.apache.commons.lang3.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

public class ConstructorUtils {

    public ConstructorUtils() {}

    public static <T> T invokeConstructor(Class<T> oclass, Object... aobject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        aobject = ArrayUtils.nullToEmpty(aobject);
        Class[] aclass = ClassUtils.toClass(aobject);

        return invokeConstructor(oclass, aobject, aclass);
    }

    public static <T> T invokeConstructor(Class<T> oclass, Object[] aobject, Class<?>[] aclass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        aobject = ArrayUtils.nullToEmpty(aobject);
        aclass = ArrayUtils.nullToEmpty(aclass);
        Constructor constructor = getMatchingAccessibleConstructor(oclass, aclass);

        if (constructor == null) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + oclass.getName());
        } else {
            return constructor.newInstance(aobject);
        }
    }

    public static <T> T invokeExactConstructor(Class<T> oclass, Object... aobject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        aobject = ArrayUtils.nullToEmpty(aobject);
        Class[] aclass = ClassUtils.toClass(aobject);

        return invokeExactConstructor(oclass, aobject, aclass);
    }

    public static <T> T invokeExactConstructor(Class<T> oclass, Object[] aobject, Class<?>[] aclass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        aobject = ArrayUtils.nullToEmpty(aobject);
        aclass = ArrayUtils.nullToEmpty(aclass);
        Constructor constructor = getAccessibleConstructor(oclass, aclass);

        if (constructor == null) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + oclass.getName());
        } else {
            return constructor.newInstance(aobject);
        }
    }

    public static <T> Constructor<T> getAccessibleConstructor(Class<T> oclass, Class<?>... aclass) {
        Validate.notNull(oclass, "class cannot be null");

        try {
            return getAccessibleConstructor(oclass.getConstructor(aclass));
        } catch (NoSuchMethodException nosuchmethodexception) {
            return null;
        }
    }

    public static <T> Constructor<T> getAccessibleConstructor(Constructor<T> constructor) {
        Validate.notNull(constructor, "constructor cannot be null");
        return MemberUtils.isAccessible(constructor) && Modifier.isPublic(constructor.getDeclaringClass().getModifiers()) ? constructor : null;
    }

    public static <T> Constructor<T> getMatchingAccessibleConstructor(Class<T> oclass, Class<?>... aclass) {
        Validate.notNull(oclass, "class cannot be null");

        Constructor constructor;

        try {
            constructor = oclass.getConstructor(aclass);
            MemberUtils.setAccessibleWorkaround(constructor);
            return constructor;
        } catch (NoSuchMethodException nosuchmethodexception) {
            constructor = null;
            Constructor[] aconstructor = oclass.getConstructors();
            Constructor[] aconstructor1 = aconstructor;
            int i = aconstructor.length;

            for (int j = 0; j < i; ++j) {
                Constructor constructor1 = aconstructor1[j];

                if (ClassUtils.isAssignable(aclass, constructor1.getParameterTypes(), true)) {
                    constructor1 = getAccessibleConstructor(constructor1);
                    if (constructor1 != null) {
                        MemberUtils.setAccessibleWorkaround(constructor1);
                        if (constructor == null || MemberUtils.compareParameterTypes(constructor1.getParameterTypes(), constructor.getParameterTypes(), aclass) < 0) {
                            constructor = constructor1;
                        }
                    }
                }
            }

            return constructor;
        }
    }
}
