package com.google.common.reflect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
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
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

final class Types {

    private static final Function<Type, String> TYPE_TO_STRING = new Function<Type, String>() {
        public String apply(Type type) {
            return Types.toString(type);
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on(", ").useForNull("null");

    static Type newArrayType(Type type) {
        if (type instanceof WildcardType) {
            WildcardType wildcardtype = (WildcardType) type;
            Type[] atype = wildcardtype.getLowerBounds();

            Preconditions.checkArgument(atype.length <= 1, "Wildcard cannot have more than one lower bounds.");
            if (atype.length == 1) {
                return supertypeOf(newArrayType(atype[0]));
            } else {
                Type[] atype1 = wildcardtype.getUpperBounds();

                Preconditions.checkArgument(atype1.length == 1, "Wildcard should have only one upper bound.");
                return subtypeOf(newArrayType(atype1[0]));
            }
        } else {
            return Types.JavaVersion.CURRENT.newArrayType(type);
        }
    }

    static ParameterizedType newParameterizedTypeWithOwner(@Nullable Type type, Class<?> oclass, Type... atype) {
        if (type == null) {
            return newParameterizedType(oclass, atype);
        } else {
            Preconditions.checkNotNull(atype);
            Preconditions.checkArgument(oclass.getEnclosingClass() != null, "Owner type for unenclosed %s", oclass);
            return new Types.ParameterizedTypeImpl(type, oclass, atype);
        }
    }

    static ParameterizedType newParameterizedType(Class<?> oclass, Type... atype) {
        return new Types.ParameterizedTypeImpl(Types.ClassOwnership.JVM_BEHAVIOR.getOwnerType(oclass), oclass, atype);
    }

    static <D extends GenericDeclaration> TypeVariable<D> newTypeVariable(D d0, String s, Type... atype) {
        return new Types.TypeVariableImpl<>(d0, s, atype.length == 0 ? new Type[]{Object.class} : atype);
    }

    @VisibleForTesting
    static WildcardType subtypeOf(Type type) {
        return new Types.WildcardTypeImpl(new Type[0], new Type[]{type});
    }

    @VisibleForTesting
    static WildcardType supertypeOf(Type type) {
        return new Types.WildcardTypeImpl(new Type[]{type}, new Type[]{Object.class});
    }

    static String toString(Type type) {
        return type instanceof Class ? ((Class) type).getName() : type.toString();
    }

    @Nullable
    static Type getComponentType(Type type) {
        Preconditions.checkNotNull(type);
        final AtomicReference atomicreference = new AtomicReference();

        (new TypeVisitor() {
            @Override
            void visitTypeVariable(TypeVariable<?> typevariable) {
                atomicreference.set(Types.subtypeOfComponentType(typevariable.getBounds()));
            }

            @Override
            void visitWildcardType(WildcardType wildcardtype) {
                atomicreference.set(Types.subtypeOfComponentType(wildcardtype.getUpperBounds()));
            }

            @Override
            void visitGenericArrayType(GenericArrayType genericarraytype) {
                atomicreference.set(genericarraytype.getGenericComponentType());
            }

            @Override
            void visitClass(Class<?> oclass) {
                atomicreference.set(oclass.getComponentType());
            }
        }).visit(new Type[]{type});
        return (Type) atomicreference.get();
    }

    @Nullable
    private static Type subtypeOfComponentType(Type[] atype) {
        Type[] atype1 = atype;
        int i = atype.length;

        for (int j = 0; j < i; ++j) {
            Type type = atype1[j];
            Type type1 = getComponentType(type);

            if (type1 != null) {
                if (type1 instanceof Class) {
                    Class oclass = (Class) type1;

                    if (oclass.isPrimitive()) {
                        return oclass;
                    }
                }

                return subtypeOf(type1);
            }
        }

        return null;
    }

    private static Type[] toArray(Collection<Type> collection) {
        return (Type[]) collection.toArray(new Type[collection.size()]);
    }

    private static Iterable<Type> filterUpperBounds(Iterable<Type> iterable) {
        return Iterables.filter(iterable, Predicates.not(Predicates.equalTo(Object.class)));
    }

    private static void disallowPrimitiveType(Type[] atype, String s) {
        Type[] atype1 = atype;
        int i = atype.length;

        for (int j = 0; j < i; ++j) {
            Type type = atype1[j];

            if (type instanceof Class) {
                Class oclass = (Class) type;

                Preconditions.checkArgument(!oclass.isPrimitive(), "Primitive type '%s' used as %s", oclass, s);
            }
        }

    }

    static Class<?> getArrayClass(Class<?> oclass) {
        return Array.newInstance(oclass, 0).getClass();
    }

    private Types() {}

    static enum JavaVersion {

        JAVA6 {
            @Override
            GenericArrayType newArrayType(Type type) {
                return new Types.GenericArrayTypeImpl(type);
            }

            @Override
            Type usedInGenericType(Type type) {
                Preconditions.checkNotNull(type);
                if (type instanceof Class) {
                    Class oclass = (Class) type;

                    if (oclass.isArray()) {
                        return new Types.GenericArrayTypeImpl(oclass.getComponentType());
                    }
                }

                return type;
            }
        },
        JAVA7 {
            @Override
            Type newArrayType(Type type) {
                return (Type) (type instanceof Class ? Types.getArrayClass((Class) type) : new Types.GenericArrayTypeImpl(type));
            }

            @Override
            Type usedInGenericType(Type type) {
                return (Type) Preconditions.checkNotNull(type);
            }
        };

        static final Types.JavaVersion CURRENT = (new TypeCapture<int[]>() {
        }).capture() instanceof Class ? Types.JavaVersion.JAVA7 : Types.JavaVersion.JAVA6;

        private JavaVersion() {}

        abstract Type newArrayType(Type type);

        abstract Type usedInGenericType(Type type);

        final ImmutableList<Type> usedInGenericType(Type[] atype) {
            ImmutableList.Builder immutablelist_builder = ImmutableList.builder();
            Type[] atype1 = atype;
            int i = atype.length;

            for (int j = 0; j < i; ++j) {
                Type type = atype1[j];

                immutablelist_builder.add((Object) this.usedInGenericType(type));
            }

            return immutablelist_builder.build();
        }
    }

    static final class WildcardTypeImpl implements WildcardType, Serializable {

        private final ImmutableList<Type> lowerBounds;
        private final ImmutableList<Type> upperBounds;
        private static final long serialVersionUID = 0L;

        WildcardTypeImpl(Type[] atype, Type[] atype1) {
            Types.disallowPrimitiveType(atype, "lower bound for wildcard");
            Types.disallowPrimitiveType(atype1, "upper bound for wildcard");
            this.lowerBounds = Types.JavaVersion.CURRENT.usedInGenericType(atype);
            this.upperBounds = Types.JavaVersion.CURRENT.usedInGenericType(atype1);
        }

        public Type[] getLowerBounds() {
            return Types.toArray(this.lowerBounds);
        }

        public Type[] getUpperBounds() {
            return Types.toArray(this.upperBounds);
        }

        public boolean equals(Object object) {
            if (!(object instanceof WildcardType)) {
                return false;
            } else {
                WildcardType wildcardtype = (WildcardType) object;

                return this.lowerBounds.equals(Arrays.asList(wildcardtype.getLowerBounds())) && this.upperBounds.equals(Arrays.asList(wildcardtype.getUpperBounds()));
            }
        }

        public int hashCode() {
            return this.lowerBounds.hashCode() ^ this.upperBounds.hashCode();
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder("?");
            Iterator iterator = this.lowerBounds.iterator();

            Type type;

            while (iterator.hasNext()) {
                type = (Type) iterator.next();
                stringbuilder.append(" super ").append(Types.toString(type));
            }

            iterator = Types.filterUpperBounds(this.upperBounds).iterator();

            while (iterator.hasNext()) {
                type = (Type) iterator.next();
                stringbuilder.append(" extends ").append(Types.toString(type));
            }

            return stringbuilder.toString();
        }
    }

    private static final class TypeVariableImpl<D extends GenericDeclaration> implements TypeVariable<D> {

        private final D genericDeclaration;
        private final String name;
        private final ImmutableList<Type> bounds;

        TypeVariableImpl(D d0, String s, Type[] atype) {
            Types.disallowPrimitiveType(atype, "bound for type variable");
            this.genericDeclaration = (GenericDeclaration) Preconditions.checkNotNull(d0);
            this.name = (String) Preconditions.checkNotNull(s);
            this.bounds = ImmutableList.copyOf((Object[]) atype);
        }

        public Type[] getBounds() {
            return Types.toArray(this.bounds);
        }

        public D getGenericDeclaration() {
            return this.genericDeclaration;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public int hashCode() {
            return this.genericDeclaration.hashCode() ^ this.name.hashCode();
        }

        public boolean equals(Object object) {
            if (!(object instanceof TypeVariable)) {
                return false;
            } else {
                TypeVariable typevariable = (TypeVariable) object;

                return this.name.equals(typevariable.getName()) && this.genericDeclaration.equals(typevariable.getGenericDeclaration());
            }
        }
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {

        private final Type ownerType;
        private final ImmutableList<Type> argumentsList;
        private final Class<?> rawType;
        private static final long serialVersionUID = 0L;

        ParameterizedTypeImpl(@Nullable Type type, Class<?> oclass, Type[] atype) {
            Preconditions.checkNotNull(oclass);
            Preconditions.checkArgument(atype.length == oclass.getTypeParameters().length);
            Types.disallowPrimitiveType(atype, "type parameter");
            this.ownerType = type;
            this.rawType = oclass;
            this.argumentsList = Types.JavaVersion.CURRENT.usedInGenericType(atype);
        }

        public Type[] getActualTypeArguments() {
            return Types.toArray(this.argumentsList);
        }

        public Type getRawType() {
            return this.rawType;
        }

        public Type getOwnerType() {
            return this.ownerType;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder();

            if (this.ownerType != null) {
                stringbuilder.append(Types.toString(this.ownerType)).append('.');
            }

            stringbuilder.append(this.rawType.getName()).append('<').append(Types.COMMA_JOINER.join(Iterables.transform(this.argumentsList, Types.TYPE_TO_STRING))).append('>');
            return stringbuilder.toString();
        }

        public int hashCode() {
            return (this.ownerType == null ? 0 : this.ownerType.hashCode()) ^ this.argumentsList.hashCode() ^ this.rawType.hashCode();
        }

        public boolean equals(Object object) {
            if (!(object instanceof ParameterizedType)) {
                return false;
            } else {
                ParameterizedType parameterizedtype = (ParameterizedType) object;

                return this.getRawType().equals(parameterizedtype.getRawType()) && Objects.equal(this.getOwnerType(), parameterizedtype.getOwnerType()) && Arrays.equals(this.getActualTypeArguments(), parameterizedtype.getActualTypeArguments());
            }
        }
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {

        private final Type componentType;
        private static final long serialVersionUID = 0L;

        GenericArrayTypeImpl(Type type) {
            this.componentType = Types.JavaVersion.CURRENT.usedInGenericType(type);
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        public String toString() {
            return Types.toString(this.componentType) + "[]";
        }

        public int hashCode() {
            return this.componentType.hashCode();
        }

        public boolean equals(Object object) {
            if (object instanceof GenericArrayType) {
                GenericArrayType genericarraytype = (GenericArrayType) object;

                return Objects.equal(this.getGenericComponentType(), genericarraytype.getGenericComponentType());
            } else {
                return false;
            }
        }
    }

    private static enum ClassOwnership {

        OWNED_BY_ENCLOSING_CLASS {
            @Nullable
            @Override
            Class<?> getOwnerType(Class<?> oclass) {
                return oclass.getEnclosingClass();
            }
        },
        LOCAL_CLASS_HAS_NO_OWNER {
            @Nullable
            @Override
            Class<?> getOwnerType(Class<?> oclass) {
                return oclass.isLocalClass() ? null : oclass.getEnclosingClass();
            }
        };

        static final Types.ClassOwnership JVM_BEHAVIOR = detectJvmBehavior();

        private ClassOwnership() {}

        @Nullable
        abstract Class<?> getOwnerType(Class<?> oclass);

        private static Types.ClassOwnership detectJvmBehavior() {
            class 1LocalClass<T> {

                _LocalClass/* $FF was: 1LocalClass*/() {
}
            }

            Class oclass = (new 1LocalClass<String>() {
            }).getClass();
            ParameterizedType parameterizedtype = (ParameterizedType)oclass.getGenericSuperclass();
            Types.ClassOwnership[] atypes_classownership = values();
            int i = atypes_classownership.length;

            for(int j = 0; j < i; ++j) {
                Types.ClassOwnership types_classownership = atypes_classownership[j];

                if (types_classownership.getOwnerType(1LocalClass.class) == parameterizedtype.getOwnerType()) {
                    return types_classownership;
                }
            }

            throw new AssertionError();
        }
    }
}
