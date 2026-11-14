package io.netty.util.internal;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class TypeParameterMatcher {

    private static final TypeParameterMatcher NOOP = new NoOpTypeParameterMatcher();
    private static final Object TEST_OBJECT = new Object();
    private static final ThreadLocal<Map<Class<?>, TypeParameterMatcher>> getCache = new ThreadLocal<Map<Class<?>, TypeParameterMatcher>>() {
        protected Map<Class<?>, TypeParameterMatcher> initialValue() {
            return new IdentityHashMap();
        }
    };
    private static final ThreadLocal<Map<Class<?>, Map<String, TypeParameterMatcher>>> findCache = new ThreadLocal<Map<Class<?>, Map<String, TypeParameterMatcher>>>() {
        protected Map<Class<?>, Map<String, TypeParameterMatcher>> initialValue() {
            return new IdentityHashMap();
        }
    };

    public static TypeParameterMatcher get(Class<?> oclass) {
        Map map = (Map) TypeParameterMatcher.getCache.get();
        Object object = (TypeParameterMatcher) map.get(oclass);

        if (object == null) {
            if (oclass == Object.class) {
                object = TypeParameterMatcher.NOOP;
            } else if (PlatformDependent.hasJavassist()) {
                try {
                    object = JavassistTypeParameterMatcherGenerator.generate(oclass);
                    ((TypeParameterMatcher) object).match(TypeParameterMatcher.TEST_OBJECT);
                } catch (IllegalAccessError illegalaccesserror) {
                    object = null;
                } catch (Exception exception) {
                    object = null;
                }
            }

            if (object == null) {
                object = new TypeParameterMatcher.ReflectiveMatcher(oclass);
            }

            map.put(oclass, object);
        }

        return (TypeParameterMatcher) object;
    }

    public static TypeParameterMatcher find(Object object, Class<?> oclass, String s) {
        Map map = (Map) TypeParameterMatcher.findCache.get();
        Class oclass1 = object.getClass();
        Object object1 = (Map) map.get(oclass1);

        if (object1 == null) {
            object1 = new HashMap();
            map.put(oclass1, object1);
        }

        TypeParameterMatcher typeparametermatcher = (TypeParameterMatcher) ((Map) object1).get(s);

        if (typeparametermatcher == null) {
            typeparametermatcher = get(find0(object, oclass, s));
            ((Map) object1).put(s, typeparametermatcher);
        }

        return typeparametermatcher;
    }

    private static Class<?> find0(Object object, Class<?> oclass, String s) {
        Class oclass1 = object.getClass();
        Class oclass2 = oclass1;

        while (true) {
            while (oclass2.getSuperclass() != oclass) {
                oclass2 = oclass2.getSuperclass();
                if (oclass2 == null) {
                    return fail(oclass1, s);
                }
            }

            int i = -1;
            TypeVariable[] atypevariable = oclass2.getSuperclass().getTypeParameters();
            int j = 0;

            while (true) {
                if (j < atypevariable.length) {
                    if (!s.equals(atypevariable[j].getName())) {
                        ++j;
                        continue;
                    }

                    i = j;
                }

                if (i < 0) {
                    throw new IllegalStateException("unknown type parameter '" + s + "': " + oclass);
                }

                Type type = oclass2.getGenericSuperclass();

                if (!(type instanceof ParameterizedType)) {
                    return Object.class;
                }

                Type[] atype = ((ParameterizedType) type).getActualTypeArguments();
                Type type1 = atype[i];

                if (type1 instanceof ParameterizedType) {
                    type1 = ((ParameterizedType) type1).getRawType();
                }

                if (type1 instanceof Class) {
                    return (Class) type1;
                }

                if (type1 instanceof GenericArrayType) {
                    Type type2 = ((GenericArrayType) type1).getGenericComponentType();

                    if (type2 instanceof ParameterizedType) {
                        type2 = ((ParameterizedType) type2).getRawType();
                    }

                    if (type2 instanceof Class) {
                        return Array.newInstance((Class) type2, 0).getClass();
                    }
                }

                if (!(type1 instanceof TypeVariable)) {
                    return fail(oclass1, s);
                }

                TypeVariable typevariable = (TypeVariable) type1;

                oclass2 = oclass1;
                if (!(typevariable.getGenericDeclaration() instanceof Class)) {
                    return Object.class;
                }

                oclass = (Class) typevariable.getGenericDeclaration();
                s = typevariable.getName();
                if (!oclass.isAssignableFrom(oclass1)) {
                    return Object.class;
                }
                break;
            }
        }
    }

    private static Class<?> fail(Class<?> oclass, String s) {
        throw new IllegalStateException("cannot determine the type of the type parameter '" + s + "': " + oclass);
    }

    public abstract boolean match(Object object);

    protected TypeParameterMatcher() {}

    private static final class ReflectiveMatcher extends TypeParameterMatcher {

        private final Class<?> type;

        ReflectiveMatcher(Class<?> oclass) {
            this.type = oclass;
        }

        @Override
        public boolean match(Object object) {
            return this.type.isInstance(object);
        }
    }
}
