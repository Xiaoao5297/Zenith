package com.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

public final class $Gson$Types {

    static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    private $Gson$Types() {}

    public static ParameterizedType newParameterizedTypeWithOwner(Type type, Type type1, Type... atype) {
        return new $Gson$Types.ParameterizedTypeImpl(type, type1, atype);
    }

    public static GenericArrayType arrayOf(Type type) {
        return new $Gson$Types.GenericArrayTypeImpl(type);
    }

    public static WildcardType subtypeOf(Type type) {
        return new $Gson$Types.WildcardTypeImpl(new Type[]{type}, $Gson$Types.EMPTY_TYPE_ARRAY);
    }

    public static WildcardType supertypeOf(Type type) {
        return new $Gson$Types.WildcardTypeImpl(new Type[]{Object.class}, new Type[]{type});
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            Class oclass = (Class) type;

            return (Type) (oclass.isArray() ? new $Gson$Types.GenericArrayTypeImpl(canonicalize(oclass.getComponentType())) : oclass);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedtype = (ParameterizedType) type;

            return new $Gson$Types.ParameterizedTypeImpl(parameterizedtype.getOwnerType(), parameterizedtype.getRawType(), parameterizedtype.getActualTypeArguments());
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericarraytype = (GenericArrayType) type;

            return new $Gson$Types.GenericArrayTypeImpl(genericarraytype.getGenericComponentType());
        } else if (type instanceof WildcardType) {
            WildcardType wildcardtype = (WildcardType) type;

            return new $Gson$Types.WildcardTypeImpl(wildcardtype.getUpperBounds(), wildcardtype.getLowerBounds());
        } else {
            return type;
        }
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedtype = (ParameterizedType) type;
            Type type1 = parameterizedtype.getRawType();

            $Gson$Preconditions.checkArgument(type1 instanceof Class);
            return (Class) type1;
        } else if (type instanceof GenericArrayType) {
            Type type2 = ((GenericArrayType) type).getGenericComponentType();

            return Array.newInstance(getRawType(type2), 0).getClass();
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        } else {
            String s = type == null ? "null" : type.getClass().getName();

            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + s);
        }
    }

    static boolean equal(Object object, Object object1) {
        return object == object1 || object != null && object.equals(object1);
    }

    public static boolean equals(Type type, Type type1) {
        if (type == type1) {
            return true;
        } else if (type instanceof Class) {
            return type.equals(type1);
        } else if (type instanceof ParameterizedType) {
            if (!(type1 instanceof ParameterizedType)) {
                return false;
            } else {
                ParameterizedType parameterizedtype = (ParameterizedType) type;
                ParameterizedType parameterizedtype1 = (ParameterizedType) type1;

                return equal(parameterizedtype.getOwnerType(), parameterizedtype1.getOwnerType()) && parameterizedtype.getRawType().equals(parameterizedtype1.getRawType()) && Arrays.equals(parameterizedtype.getActualTypeArguments(), parameterizedtype1.getActualTypeArguments());
            }
        } else if (type instanceof GenericArrayType) {
            if (!(type1 instanceof GenericArrayType)) {
                return false;
            } else {
                GenericArrayType genericarraytype = (GenericArrayType) type;
                GenericArrayType genericarraytype1 = (GenericArrayType) type1;

                return equals(genericarraytype.getGenericComponentType(), genericarraytype1.getGenericComponentType());
            }
        } else if (type instanceof WildcardType) {
            if (!(type1 instanceof WildcardType)) {
                return false;
            } else {
                WildcardType wildcardtype = (WildcardType) type;
                WildcardType wildcardtype1 = (WildcardType) type1;

                return Arrays.equals(wildcardtype.getUpperBounds(), wildcardtype1.getUpperBounds()) && Arrays.equals(wildcardtype.getLowerBounds(), wildcardtype1.getLowerBounds());
            }
        } else if (type instanceof TypeVariable) {
            if (!(type1 instanceof TypeVariable)) {
                return false;
            } else {
                TypeVariable typevariable = (TypeVariable) type;
                TypeVariable typevariable1 = (TypeVariable) type1;

                return typevariable.getGenericDeclaration() == typevariable1.getGenericDeclaration() && typevariable.getName().equals(typevariable1.getName());
            }
        } else {
            return false;
        }
    }

    private static int hashCodeOrZero(Object object) {
        return object != null ? object.hashCode() : 0;
    }

    public static String typeToString(Type type) {
        return type instanceof Class ? ((Class) type).getName() : type.toString();
    }

    static Type getGenericSupertype(Type type, Class<?> oclass, Class<?> oclass1) {
        if (oclass1 == oclass) {
            return type;
        } else {
            if (oclass1.isInterface()) {
                Class[] aclass = oclass.getInterfaces();
                int i = 0;

                for (int j = aclass.length; i < j; ++i) {
                    if (aclass[i] == oclass1) {
                        return oclass.getGenericInterfaces()[i];
                    }

                    if (oclass1.isAssignableFrom(aclass[i])) {
                        return getGenericSupertype(oclass.getGenericInterfaces()[i], aclass[i], oclass1);
                    }
                }
            }

            if (!oclass.isInterface()) {
                while (oclass != Object.class) {
                    Class oclass2 = oclass.getSuperclass();

                    if (oclass2 == oclass1) {
                        return oclass.getGenericSuperclass();
                    }

                    if (oclass1.isAssignableFrom(oclass2)) {
                        return getGenericSupertype(oclass.getGenericSuperclass(), oclass2, oclass1);
                    }

                    oclass = oclass2;
                }
            }

            return oclass1;
        }
    }

    static Type getSupertype(Type type, Class<?> oclass, Class<?> oclass1) {
        $Gson$Preconditions.checkArgument(oclass1.isAssignableFrom(oclass));
        return resolve(type, oclass, getGenericSupertype(type, oclass, oclass1));
    }

    public static Type getArrayComponentType(Type type) {
        return (Type) (type instanceof GenericArrayType ? ((GenericArrayType) type).getGenericComponentType() : ((Class) type).getComponentType());
    }

    public static Type getCollectionElementType(Type type, Class<?> oclass) {
        Type type1 = getSupertype(type, oclass, Collection.class);

        if (type1 instanceof WildcardType) {
            type1 = ((WildcardType) type1).getUpperBounds()[0];
        }

        return (Type) (type1 instanceof ParameterizedType ? ((ParameterizedType) type1).getActualTypeArguments()[0] : Object.class);
    }

    public static Type[] getMapKeyAndValueTypes(Type type, Class<?> oclass) {
        if (type == Properties.class) {
            return new Type[]{String.class, String.class};
        } else {
            Type type1 = getSupertype(type, oclass, Map.class);

            if (type1 instanceof ParameterizedType) {
                ParameterizedType parameterizedtype = (ParameterizedType) type1;

                return parameterizedtype.getActualTypeArguments();
            } else {
                return new Type[]{Object.class, Object.class};
            }
        }
    }

    public static Type resolve(Type type, Class<?> oclass, Type type1) {
        while (true) {
            if (type1 instanceof TypeVariable) {
                TypeVariable typevariable = (TypeVariable) type1;

                type1 = resolveTypeVariable(type, oclass, typevariable);
                if (type1 != typevariable) {
                    continue;
                }

                return type1;
            }

            Type type2;

            if (type1 instanceof Class && ((Class) type1).isArray()) {
                Class oclass1 = (Class) type1;
                Class oclass2 = oclass1.getComponentType();

                type2 = resolve(type, oclass, oclass2);
                return (Type) (oclass2 == type2 ? oclass1 : arrayOf(type2));
            }

            Type type3;

            if (type1 instanceof GenericArrayType) {
                GenericArrayType genericarraytype = (GenericArrayType) type1;

                type3 = genericarraytype.getGenericComponentType();
                type2 = resolve(type, oclass, type3);
                return type3 == type2 ? genericarraytype : arrayOf(type2);
            }

            if (type1 instanceof ParameterizedType) {
                ParameterizedType parameterizedtype = (ParameterizedType) type1;

                type3 = parameterizedtype.getOwnerType();
                type2 = resolve(type, oclass, type3);
                boolean flag = type2 != type3;
                Type[] atype = parameterizedtype.getActualTypeArguments();
                int i = 0;

                for (int j = atype.length; i < j; ++i) {
                    Type type4 = resolve(type, oclass, atype[i]);

                    if (type4 != atype[i]) {
                        if (!flag) {
                            atype = (Type[]) atype.clone();
                            flag = true;
                        }

                        atype[i] = type4;
                    }
                }

                return flag ? newParameterizedTypeWithOwner(type2, parameterizedtype.getRawType(), atype) : parameterizedtype;
            }

            if (type1 instanceof WildcardType) {
                WildcardType wildcardtype = (WildcardType) type1;
                Type[] atype1 = wildcardtype.getLowerBounds();
                Type[] atype2 = wildcardtype.getUpperBounds();
                Type type5;

                if (atype1.length == 1) {
                    type5 = resolve(type, oclass, atype1[0]);
                    if (type5 != atype1[0]) {
                        return supertypeOf(type5);
                    }
                } else if (atype2.length == 1) {
                    type5 = resolve(type, oclass, atype2[0]);
                    if (type5 != atype2[0]) {
                        return subtypeOf(type5);
                    }
                }

                return wildcardtype;
            }

            return type1;
        }
    }

    static Type resolveTypeVariable(Type type, Class<?> oclass, TypeVariable<?> typevariable) {
        Class oclass1 = declaringClassOf(typevariable);

        if (oclass1 == null) {
            return typevariable;
        } else {
            Type type1 = getGenericSupertype(type, oclass, oclass1);

            if (type1 instanceof ParameterizedType) {
                int i = indexOf(oclass1.getTypeParameters(), typevariable);

                return ((ParameterizedType) type1).getActualTypeArguments()[i];
            } else {
                return typevariable;
            }
        }
    }

    private static int indexOf(Object[] aobject, Object object) {
        for (int i = 0; i < aobject.length; ++i) {
            if (object.equals(aobject[i])) {
                return i;
            }
        }

        throw new NoSuchElementException();
    }

    private static Class<?> declaringClassOf(TypeVariable<?> typevariable) {
        GenericDeclaration genericdeclaration = typevariable.getGenericDeclaration();

        return genericdeclaration instanceof Class ? (Class) genericdeclaration : null;
    }

    private static void checkNotPrimitive(Type type) {
        $Gson$Preconditions.checkArgument(!(type instanceof Class) || !((Class) type).isPrimitive());
    }

    private static final class WildcardTypeImpl implements WildcardType, Serializable {

        private final Type upperBound;
        private final Type lowerBound;
        private static final long serialVersionUID = 0L;

        public WildcardTypeImpl(Type[] atype, Type[] atype1) {
            $Gson$Preconditions.checkArgument(atype1.length <= 1);
            $Gson$Preconditions.checkArgument(atype.length == 1);
            if (atype1.length == 1) {
                $Gson$Preconditions.checkNotNull(atype1[0]);
                $Gson$Types.checkNotPrimitive(atype1[0]);
                $Gson$Preconditions.checkArgument(atype[0] == Object.class);
                this.lowerBound = $Gson$Types.canonicalize(atype1[0]);
                this.upperBound = Object.class;
            } else {
                $Gson$Preconditions.checkNotNull(atype[0]);
                $Gson$Types.checkNotPrimitive(atype[0]);
                this.lowerBound = null;
                this.upperBound = $Gson$Types.canonicalize(atype[0]);
            }

        }

        public Type[] getUpperBounds() {
            return new Type[]{this.upperBound};
        }

        public Type[] getLowerBounds() {
            return this.lowerBound != null ? new Type[]{this.lowerBound} : $Gson$Types.EMPTY_TYPE_ARRAY;
        }

        public boolean equals(Object object) {
            return object instanceof WildcardType && $Gson$Types.equals(this, (WildcardType) object);
        }

        public int hashCode() {
            return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
        }

        public String toString() {
            return this.lowerBound != null ? "? super " + $Gson$Types.typeToString(this.lowerBound) : (this.upperBound == Object.class ? "?" : "? extends " + $Gson$Types.typeToString(this.upperBound));
        }
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {

        private final Type componentType;
        private static final long serialVersionUID = 0L;

        public GenericArrayTypeImpl(Type type) {
            this.componentType = $Gson$Types.canonicalize(type);
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        public boolean equals(Object object) {
            return object instanceof GenericArrayType && $Gson$Types.equals(this, (GenericArrayType) object);
        }

        public int hashCode() {
            return this.componentType.hashCode();
        }

        public String toString() {
            return $Gson$Types.typeToString(this.componentType) + "[]";
        }
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {

        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;
        private static final long serialVersionUID = 0L;

        public ParameterizedTypeImpl(Type type, Type type1, Type... atype) {
            if (type1 instanceof Class) {
                Class oclass = (Class) type1;

                $Gson$Preconditions.checkArgument(type != null || oclass.getEnclosingClass() == null);
                $Gson$Preconditions.checkArgument(type == null || oclass.getEnclosingClass() != null);
            }

            this.ownerType = type == null ? null : $Gson$Types.canonicalize(type);
            this.rawType = $Gson$Types.canonicalize(type1);
            this.typeArguments = (Type[]) atype.clone();

            for (int i = 0; i < this.typeArguments.length; ++i) {
                $Gson$Preconditions.checkNotNull(this.typeArguments[i]);
                $Gson$Types.checkNotPrimitive(this.typeArguments[i]);
                this.typeArguments[i] = $Gson$Types.canonicalize(this.typeArguments[i]);
            }

        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public Type getRawType() {
            return this.rawType;
        }

        public Type getOwnerType() {
            return this.ownerType;
        }

        public boolean equals(Object object) {
            return object instanceof ParameterizedType && $Gson$Types.equals(this, (ParameterizedType) object);
        }

        public int hashCode() {
            return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ $Gson$Types.hashCodeOrZero(this.ownerType);
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder(30 * (this.typeArguments.length + 1));

            stringbuilder.append($Gson$Types.typeToString(this.rawType));
            if (this.typeArguments.length == 0) {
                return stringbuilder.toString();
            } else {
                stringbuilder.append("<").append($Gson$Types.typeToString(this.typeArguments[0]));

                for (int i = 1; i < this.typeArguments.length; ++i) {
                    stringbuilder.append(", ").append($Gson$Types.typeToString(this.typeArguments[i]));
                }

                return stringbuilder.append(">").toString();
            }
        }
    }
}
