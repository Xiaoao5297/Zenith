package com.google.gson.reflect;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class TypeToken<T> {

    final Class<? super T> rawType;
    final Type type;
    final int hashCode;

    protected TypeToken() {
        this.type = getSuperclassTypeParameter(this.getClass());
        this.rawType = $Gson$Types.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    TypeToken(Type type) {
        this.type = $Gson$Types.canonicalize((Type) $Gson$Preconditions.checkNotNull(type));
        this.rawType = $Gson$Types.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    static Type getSuperclassTypeParameter(Class<?> oclass) {
        Type type = oclass.getGenericSuperclass();

        if (type instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        } else {
            ParameterizedType parameterizedtype = (ParameterizedType) type;

            return $Gson$Types.canonicalize(parameterizedtype.getActualTypeArguments()[0]);
        }
    }

    public final Class<? super T> getRawType() {
        return this.rawType;
    }

    public final Type getType() {
        return this.type;
    }

    /** @deprecated */
    @Deprecated
    public boolean isAssignableFrom(Class<?> oclass) {
        return this.isAssignableFrom((Type) oclass);
    }

    /** @deprecated */
    @Deprecated
    public boolean isAssignableFrom(Type type) {
        if (type == null) {
            return false;
        } else if (this.type.equals(type)) {
            return true;
        } else if (this.type instanceof Class) {
            return this.rawType.isAssignableFrom($Gson$Types.getRawType(type));
        } else if (this.type instanceof ParameterizedType) {
            return isAssignableFrom(type, (ParameterizedType) this.type, new HashMap());
        } else if (!(this.type instanceof GenericArrayType)) {
            throw buildUnexpectedTypeError(this.type, Class.class, ParameterizedType.class, GenericArrayType.class);
        } else {
            return this.rawType.isAssignableFrom($Gson$Types.getRawType(type)) && isAssignableFrom(type, (GenericArrayType) this.type);
        }
    }

    /** @deprecated */
    @Deprecated
    public boolean isAssignableFrom(TypeToken<?> typetoken) {
        return this.isAssignableFrom(typetoken.getType());
    }

    private static boolean isAssignableFrom(Type type, GenericArrayType genericarraytype) {
        Type type1 = genericarraytype.getGenericComponentType();

        if (!(type1 instanceof ParameterizedType)) {
            return true;
        } else {
            Object object = type;

            if (type instanceof GenericArrayType) {
                object = ((GenericArrayType) type).getGenericComponentType();
            } else if (type instanceof Class) {
                Class oclass;

                for (oclass = (Class) type; oclass.isArray(); oclass = oclass.getComponentType()) {
                    ;
                }

                object = oclass;
            }

            return isAssignableFrom((Type) object, (ParameterizedType) type1, new HashMap());
        }
    }

    private static boolean isAssignableFrom(Type type, ParameterizedType parameterizedtype, Map<String, Type> map) {
        if (type == null) {
            return false;
        } else if (parameterizedtype.equals(type)) {
            return true;
        } else {
            Class oclass = $Gson$Types.getRawType(type);
            ParameterizedType parameterizedtype1 = null;

            if (type instanceof ParameterizedType) {
                parameterizedtype1 = (ParameterizedType) type;
            }

            Type[] atype;
            Type type1;
            int i;

            if (parameterizedtype1 != null) {
                atype = parameterizedtype1.getActualTypeArguments();
                TypeVariable[] atypevariable = oclass.getTypeParameters();

                for (i = 0; i < atype.length; ++i) {
                    type1 = atype[i];

                    TypeVariable typevariable;
                    TypeVariable typevariable1;

                    for (typevariable1 = atypevariable[i]; type1 instanceof TypeVariable; type1 = (Type) map.get(typevariable.getName())) {
                        typevariable = (TypeVariable) type1;
                    }

                    map.put(typevariable1.getName(), type1);
                }

                if (typeEquals(parameterizedtype1, parameterizedtype, map)) {
                    return true;
                }
            }

            atype = oclass.getGenericInterfaces();
            int j = atype.length;

            for (i = 0; i < j; ++i) {
                type1 = atype[i];
                if (isAssignableFrom(type1, parameterizedtype, new HashMap(map))) {
                    return true;
                }
            }

            Type type2 = oclass.getGenericSuperclass();

            return isAssignableFrom(type2, parameterizedtype, new HashMap(map));
        }
    }

    private static boolean typeEquals(ParameterizedType parameterizedtype, ParameterizedType parameterizedtype1, Map<String, Type> map) {
        if (parameterizedtype.getRawType().equals(parameterizedtype1.getRawType())) {
            Type[] atype = parameterizedtype.getActualTypeArguments();
            Type[] atype1 = parameterizedtype1.getActualTypeArguments();

            for (int i = 0; i < atype.length; ++i) {
                if (!matches(atype[i], atype1[i], map)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static AssertionError buildUnexpectedTypeError(Type type, Class<?>... aclass) {
        StringBuilder stringbuilder = new StringBuilder("Unexpected type. Expected one of: ");
        Class[] aclass1 = aclass;
        int i = aclass.length;

        for (int j = 0; j < i; ++j) {
            Class oclass = aclass1[j];

            stringbuilder.append(oclass.getName()).append(", ");
        }

        stringbuilder.append("but got: ").append(type.getClass().getName()).append(", for type token: ").append(type.toString()).append('.');
        return new AssertionError(stringbuilder.toString());
    }

    private static boolean matches(Type type, Type type1, Map<String, Type> map) {
        return type1.equals(type) || type instanceof TypeVariable && type1.equals(map.get(((TypeVariable) type).getName()));
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public final boolean equals(Object object) {
        return object instanceof TypeToken && $Gson$Types.equals(this.type, ((TypeToken) object).type);
    }

    public final String toString() {
        return $Gson$Types.typeToString(this.type);
    }

    public static TypeToken<?> get(Type type) {
        return new TypeToken<>(type);
    }

    public static <T> TypeToken<T> get(Class<T> oclass) {
        return new TypeToken<>(oclass);
    }
}
