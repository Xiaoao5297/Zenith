package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Primitives;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public abstract class TypeToken<T> extends TypeCapture<T> implements Serializable {

    private final Type runtimeType;
    private transient TypeResolver typeResolver;

    protected TypeToken() {
        this.runtimeType = this.capture();
        Preconditions.checkState(!(this.runtimeType instanceof TypeVariable), "Cannot construct a TypeToken for a type variable.\nYou probably meant to call new TypeToken<%s>(getClass()) that can resolve the type variable for you.\nIf you do need to create a TypeToken of a type variable, please use TypeToken.of() instead.", this.runtimeType);
    }

    protected TypeToken(Class<?> oclass) {
        Type type = super.capture();

        if (type instanceof Class) {
            this.runtimeType = type;
        } else {
            this.runtimeType = of(oclass).resolveType(type).runtimeType;
        }

    }

    private TypeToken(Type type) {
        this.runtimeType = (Type) Preconditions.checkNotNull(type);
    }

    public static <T> TypeToken<T> of(Class<T> oclass) {
        return new TypeToken.SimpleTypeToken<>(oclass);
    }

    public static TypeToken<?> of(Type type) {
        return new TypeToken.SimpleTypeToken<>(type);
    }

    public final Class<? super T> getRawType() {
        Class oclass = getRawType(this.runtimeType);

        return oclass;
    }

    private ImmutableSet<Class<? super T>> getImmediateRawTypes() {
        ImmutableSet immutableset = getRawTypes(this.runtimeType);

        return immutableset;
    }

    public final Type getType() {
        return this.runtimeType;
    }

    public final <X> TypeToken<T> where(TypeParameter<X> typeparameter, TypeToken<X> typetoken) {
        TypeResolver typeresolver = (new TypeResolver()).where(ImmutableMap.of(typeparameter.typeVariable, typetoken.runtimeType));

        return new TypeToken.SimpleTypeToken<>(typeresolver.resolveType(this.runtimeType));
    }

    public final <X> TypeToken<T> where(TypeParameter<X> typeparameter, Class<X> oclass) {
        return this.where(typeparameter, of(oclass));
    }

    public final TypeToken<?> resolveType(Type type) {
        Preconditions.checkNotNull(type);
        TypeResolver typeresolver = this.typeResolver;

        if (typeresolver == null) {
            typeresolver = this.typeResolver = TypeResolver.accordingTo(this.runtimeType);
        }

        return of(typeresolver.resolveType(type));
    }

    private Type[] resolveInPlace(Type[] atype) {
        for (int i = 0; i < atype.length; ++i) {
            atype[i] = this.resolveType(atype[i]).getType();
        }

        return atype;
    }

    private TypeToken<?> resolveSupertype(Type type) {
        TypeToken typetoken = this.resolveType(type);

        typetoken.typeResolver = this.typeResolver;
        return typetoken;
    }

    @Nullable
    final TypeToken<? super T> getGenericSuperclass() {
        if (this.runtimeType instanceof TypeVariable) {
            return this.boundAsSuperclass(((TypeVariable) this.runtimeType).getBounds()[0]);
        } else if (this.runtimeType instanceof WildcardType) {
            return this.boundAsSuperclass(((WildcardType) this.runtimeType).getUpperBounds()[0]);
        } else {
            Type type = this.getRawType().getGenericSuperclass();

            if (type == null) {
                return null;
            } else {
                TypeToken typetoken = this.resolveSupertype(type);

                return typetoken;
            }
        }
    }

    @Nullable
    private TypeToken<? super T> boundAsSuperclass(Type type) {
        TypeToken typetoken = of(type);

        return typetoken.getRawType().isInterface() ? null : typetoken;
    }

    final ImmutableList<TypeToken<? super T>> getGenericInterfaces() {
        if (this.runtimeType instanceof TypeVariable) {
            return this.boundsAsInterfaces(((TypeVariable) this.runtimeType).getBounds());
        } else if (this.runtimeType instanceof WildcardType) {
            return this.boundsAsInterfaces(((WildcardType) this.runtimeType).getUpperBounds());
        } else {
            ImmutableList.Builder immutablelist_builder = ImmutableList.builder();
            Type[] atype = this.getRawType().getGenericInterfaces();
            int i = atype.length;

            for (int j = 0; j < i; ++j) {
                Type type = atype[j];
                TypeToken typetoken = this.resolveSupertype(type);

                immutablelist_builder.add((Object) typetoken);
            }

            return immutablelist_builder.build();
        }
    }

    private ImmutableList<TypeToken<? super T>> boundsAsInterfaces(Type[] atype) {
        ImmutableList.Builder immutablelist_builder = ImmutableList.builder();
        Type[] atype1 = atype;
        int i = atype.length;

        for (int j = 0; j < i; ++j) {
            Type type = atype1[j];
            TypeToken typetoken = of(type);

            if (typetoken.getRawType().isInterface()) {
                immutablelist_builder.add((Object) typetoken);
            }
        }

        return immutablelist_builder.build();
    }

    public final TypeToken<T>.TypeSet getTypes() {
        return new TypeToken.TypeSet();
    }

    public final TypeToken<? super T> getSupertype(Class<? super T> oclass) {
        Preconditions.checkArgument(oclass.isAssignableFrom(this.getRawType()), "%s is not a super class of %s", oclass, this);
        if (this.runtimeType instanceof TypeVariable) {
            return this.getSupertypeFromUpperBounds(oclass, ((TypeVariable) this.runtimeType).getBounds());
        } else if (this.runtimeType instanceof WildcardType) {
            return this.getSupertypeFromUpperBounds(oclass, ((WildcardType) this.runtimeType).getUpperBounds());
        } else if (oclass.isArray()) {
            return this.getArraySupertype(oclass);
        } else {
            TypeToken typetoken = this.resolveSupertype(toGenericType(oclass).runtimeType);

            return typetoken;
        }
    }

    public final TypeToken<? extends T> getSubtype(Class<?> oclass) {
        Preconditions.checkArgument(!(this.runtimeType instanceof TypeVariable), "Cannot get subtype of type variable <%s>", this);
        if (this.runtimeType instanceof WildcardType) {
            return this.getSubtypeFromLowerBounds(oclass, ((WildcardType) this.runtimeType).getLowerBounds());
        } else {
            Preconditions.checkArgument(this.getRawType().isAssignableFrom(oclass), "%s isn't a subclass of %s", oclass, this);
            if (this.isArray()) {
                return this.getArraySubtype(oclass);
            } else {
                TypeToken typetoken = of(this.resolveTypeArgsForSubclass(oclass));

                return typetoken;
            }
        }
    }

    public final boolean isAssignableFrom(TypeToken<?> typetoken) {
        return this.isAssignableFrom(typetoken.runtimeType);
    }

    public final boolean isAssignableFrom(Type type) {
        return isAssignable((Type) Preconditions.checkNotNull(type), this.runtimeType);
    }

    public final boolean isArray() {
        return this.getComponentType() != null;
    }

    public final boolean isPrimitive() {
        return this.runtimeType instanceof Class && ((Class) this.runtimeType).isPrimitive();
    }

    public final TypeToken<T> wrap() {
        if (this.isPrimitive()) {
            Class oclass = (Class) this.runtimeType;

            return of(Primitives.wrap(oclass));
        } else {
            return this;
        }
    }

    private boolean isWrapper() {
        return Primitives.allWrapperTypes().contains(this.runtimeType);
    }

    public final TypeToken<T> unwrap() {
        if (this.isWrapper()) {
            Class oclass = (Class) this.runtimeType;

            return of(Primitives.unwrap(oclass));
        } else {
            return this;
        }
    }

    @Nullable
    public final TypeToken<?> getComponentType() {
        Type type = Types.getComponentType(this.runtimeType);

        return type == null ? null : of(type);
    }

    public final Invokable<T, Object> method(Method method) {
        Preconditions.checkArgument(of(method.getDeclaringClass()).isAssignableFrom(this), "%s not declared by %s", method, this);
        return new Invokable.MethodInvokable<T>(method) {
            @Override
            Type getGenericReturnType() {
                return TypeToken.this.resolveType(super.getGenericReturnType()).getType();
            }

            @Override
            Type[] getGenericParameterTypes() {
                return TypeToken.this.resolveInPlace(super.getGenericParameterTypes());
            }

            @Override
            Type[] getGenericExceptionTypes() {
                return TypeToken.this.resolveInPlace(super.getGenericExceptionTypes());
            }

            @Override
            public TypeToken<T> getOwnerType() {
                return TypeToken.this;
            }

            @Override
            public String toString() {
                return this.getOwnerType() + "." + super.toString();
            }
        };
    }

    public final Invokable<T, T> constructor(Constructor<?> constructor) {
        Preconditions.checkArgument(constructor.getDeclaringClass() == this.getRawType(), "%s not declared by %s", constructor, this.getRawType());
        return new Invokable.ConstructorInvokable<T>(constructor) {
            @Override
            Type getGenericReturnType() {
                return TypeToken.this.resolveType(super.getGenericReturnType()).getType();
            }

            @Override
            Type[] getGenericParameterTypes() {
                return TypeToken.this.resolveInPlace(super.getGenericParameterTypes());
            }

            @Override
            Type[] getGenericExceptionTypes() {
                return TypeToken.this.resolveInPlace(super.getGenericExceptionTypes());
            }

            @Override
            public TypeToken<T> getOwnerType() {
                return TypeToken.this;
            }

            @Override
            public String toString() {
                return this.getOwnerType() + "(" + Joiner.on(", ").join((Object[]) this.getGenericParameterTypes()) + ")";
            }
        };
    }

    public boolean equals(@Nullable Object object) {
        if (object instanceof TypeToken) {
            TypeToken typetoken = (TypeToken) object;

            return this.runtimeType.equals(typetoken.runtimeType);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.runtimeType.hashCode();
    }

    public String toString() {
        return Types.toString(this.runtimeType);
    }

    protected Object writeReplace() {
        return of((new TypeResolver()).resolveType(this.runtimeType));
    }

    final TypeToken<T> rejectTypeVariables() {
        (new TypeVisitor() {
            @Override
            void visitTypeVariable(TypeVariable<?> typevariable) {
                throw new IllegalArgumentException(TypeToken.this.runtimeType + "contains a type variable and is not safe for the operation");
            }

            @Override
            void visitWildcardType(WildcardType wildcardtype) {
                this.visit(wildcardtype.getLowerBounds());
                this.visit(wildcardtype.getUpperBounds());
            }

            @Override
            void visitParameterizedType(ParameterizedType parameterizedtype) {
                this.visit(parameterizedtype.getActualTypeArguments());
                this.visit(new Type[]{parameterizedtype.getOwnerType()});
            }

            @Override
            void visitGenericArrayType(GenericArrayType genericarraytype) {
                this.visit(new Type[]{genericarraytype.getGenericComponentType()});
            }
        }).visit(new Type[]{this.runtimeType});
        return this;
    }

    private static boolean isAssignable(Type type, Type type1) {
        return type1.equals(type) ? true : (type1 instanceof WildcardType ? isAssignableToWildcardType(type, (WildcardType) type1) : (type instanceof TypeVariable ? isAssignableFromAny(((TypeVariable) type).getBounds(), type1) : (type instanceof WildcardType ? isAssignableFromAny(((WildcardType) type).getUpperBounds(), type1) : (type instanceof GenericArrayType ? isAssignableFromGenericArrayType((GenericArrayType) type, type1) : (type1 instanceof Class ? isAssignableToClass(type, (Class) type1) : (type1 instanceof ParameterizedType ? isAssignableToParameterizedType(type, (ParameterizedType) type1) : (type1 instanceof GenericArrayType ? isAssignableToGenericArrayType(type, (GenericArrayType) type1) : false)))))));
    }

    private static boolean isAssignableFromAny(Type[] atype, Type type) {
        Type[] atype1 = atype;
        int i = atype.length;

        for (int j = 0; j < i; ++j) {
            Type type1 = atype1[j];

            if (isAssignable(type1, type)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAssignableToClass(Type type, Class<?> oclass) {
        return oclass.isAssignableFrom(getRawType(type));
    }

    private static boolean isAssignableToWildcardType(Type type, WildcardType wildcardtype) {
        return isAssignable(type, supertypeBound(wildcardtype)) && isAssignableBySubtypeBound(type, wildcardtype);
    }

    private static boolean isAssignableBySubtypeBound(Type type, WildcardType wildcardtype) {
        Type type1 = subtypeBound(wildcardtype);

        if (type1 == null) {
            return true;
        } else {
            Type type2 = subtypeBound(type);

            return type2 == null ? false : isAssignable(type1, type2);
        }
    }

    private static boolean isAssignableToParameterizedType(Type type, ParameterizedType parameterizedtype) {
        Class oclass = getRawType(parameterizedtype);

        if (!oclass.isAssignableFrom(getRawType(type))) {
            return false;
        } else {
            TypeVariable[] atypevariable = oclass.getTypeParameters();
            Type[] atype = parameterizedtype.getActualTypeArguments();
            TypeToken typetoken = of(type);

            for (int i = 0; i < atypevariable.length; ++i) {
                Type type1 = typetoken.resolveType(atypevariable[i]).runtimeType;

                if (!matchTypeArgument(type1, atype[i])) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean isAssignableToGenericArrayType(Type type, GenericArrayType genericarraytype) {
        if (type instanceof Class) {
            Class oclass = (Class) type;

            return !oclass.isArray() ? false : isAssignable(oclass.getComponentType(), genericarraytype.getGenericComponentType());
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericarraytype1 = (GenericArrayType) type;

            return isAssignable(genericarraytype1.getGenericComponentType(), genericarraytype.getGenericComponentType());
        } else {
            return false;
        }
    }

    private static boolean isAssignableFromGenericArrayType(GenericArrayType genericarraytype, Type type) {
        if (type instanceof Class) {
            Class oclass = (Class) type;

            return !oclass.isArray() ? oclass == Object.class : isAssignable(genericarraytype.getGenericComponentType(), oclass.getComponentType());
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericarraytype1 = (GenericArrayType) type;

            return isAssignable(genericarraytype.getGenericComponentType(), genericarraytype1.getGenericComponentType());
        } else {
            return false;
        }
    }

    private static boolean matchTypeArgument(Type type, Type type1) {
        return type.equals(type1) ? true : (type1 instanceof WildcardType ? isAssignableToWildcardType(type, (WildcardType) type1) : false);
    }

    private static Type supertypeBound(Type type) {
        return type instanceof WildcardType ? supertypeBound((WildcardType) type) : type;
    }

    private static Type supertypeBound(WildcardType wildcardtype) {
        Type[] atype = wildcardtype.getUpperBounds();

        if (atype.length == 1) {
            return supertypeBound(atype[0]);
        } else if (atype.length == 0) {
            return Object.class;
        } else {
            throw new AssertionError("There should be at most one upper bound for wildcard type: " + wildcardtype);
        }
    }

    @Nullable
    private static Type subtypeBound(Type type) {
        return type instanceof WildcardType ? subtypeBound((WildcardType) type) : type;
    }

    @Nullable
    private static Type subtypeBound(WildcardType wildcardtype) {
        Type[] atype = wildcardtype.getLowerBounds();

        if (atype.length == 1) {
            return subtypeBound(atype[0]);
        } else if (atype.length == 0) {
            return null;
        } else {
            throw new AssertionError("Wildcard should have at most one lower bound: " + wildcardtype);
        }
    }

    @VisibleForTesting
    static Class<?> getRawType(Type type) {
        return (Class) getRawTypes(type).iterator().next();
    }

    @VisibleForTesting
    static ImmutableSet<Class<?>> getRawTypes(Type type) {
        Preconditions.checkNotNull(type);
        final ImmutableSet.Builder immutableset_builder = ImmutableSet.builder();

        (new TypeVisitor() {
            @Override
            void visitTypeVariable(TypeVariable<?> typevariable) {
                this.visit(typevariable.getBounds());
            }

            @Override
            void visitWildcardType(WildcardType wildcardtype) {
                this.visit(wildcardtype.getUpperBounds());
            }

            @Override
            void visitParameterizedType(ParameterizedType parameterizedtype) {
                immutableset_builder.add((Object) ((Class) parameterizedtype.getRawType()));
            }

            @Override
            void visitClass(Class<?> oclass) {
                immutableset_builder.add((Object) oclass);
            }

            @Override
            void visitGenericArrayType(GenericArrayType genericarraytype) {
                immutableset_builder.add((Object) Types.getArrayClass(TypeToken.getRawType(genericarraytype.getGenericComponentType())));
            }
        }).visit(new Type[]{type});
        return immutableset_builder.build();
    }

    @VisibleForTesting
    static <T> TypeToken<? extends T> toGenericType(Class<T> oclass) {
        TypeToken typetoken;

        if (oclass.isArray()) {
            Type type = Types.newArrayType(toGenericType(oclass.getComponentType()).runtimeType);

            typetoken = of(type);
            return typetoken;
        } else {
            TypeVariable[] atypevariable = oclass.getTypeParameters();

            if (atypevariable.length > 0) {
                typetoken = of((Type) Types.newParameterizedType(oclass, atypevariable));
                return typetoken;
            } else {
                return of(oclass);
            }
        }
    }

    private TypeToken<? super T> getSupertypeFromUpperBounds(Class<? super T> oclass, Type[] atype) {
        Type[] atype1 = atype;
        int i = atype.length;

        for (int j = 0; j < i; ++j) {
            Type type = atype1[j];
            TypeToken typetoken = of(type);

            if (of(oclass).isAssignableFrom(typetoken)) {
                TypeToken typetoken1 = typetoken.getSupertype(oclass);

                return typetoken1;
            }
        }

        throw new IllegalArgumentException(oclass + " isn't a super type of " + this);
    }

    private TypeToken<? extends T> getSubtypeFromLowerBounds(Class<?> oclass, Type[] atype) {
        int i = atype.length;
        byte b0 = 0;

        if (b0 < i) {
            Type type = atype[b0];
            TypeToken typetoken = of(type);

            return typetoken.getSubtype(oclass);
        } else {
            throw new IllegalArgumentException(oclass + " isn't a subclass of " + this);
        }
    }

    private TypeToken<? super T> getArraySupertype(Class<? super T> oclass) {
        TypeToken typetoken = (TypeToken) Preconditions.checkNotNull(this.getComponentType(), "%s isn't a super type of %s", oclass, this);
        TypeToken typetoken1 = typetoken.getSupertype(oclass.getComponentType());
        TypeToken typetoken2 = of(newArrayClassOrGenericArrayType(typetoken1.runtimeType));

        return typetoken2;
    }

    private TypeToken<? extends T> getArraySubtype(Class<?> oclass) {
        TypeToken typetoken = this.getComponentType().getSubtype(oclass.getComponentType());
        TypeToken typetoken1 = of(newArrayClassOrGenericArrayType(typetoken.runtimeType));

        return typetoken1;
    }

    private Type resolveTypeArgsForSubclass(Class<?> oclass) {
        if (this.runtimeType instanceof Class) {
            return oclass;
        } else {
            TypeToken typetoken = toGenericType(oclass);
            Type type = typetoken.getSupertype(this.getRawType()).runtimeType;

            return (new TypeResolver()).where(type, this.runtimeType).resolveType(typetoken.runtimeType);
        }
    }

    private static Type newArrayClassOrGenericArrayType(Type type) {
        return Types.JavaVersion.JAVA7.newArrayType(type);
    }

    private abstract static class TypeCollector<K> {

        static final TypeToken.TypeCollector<TypeToken<?>> FOR_GENERIC_TYPE = new TypeToken.TypeCollector<TypeToken<?>>() {
            Class<?> getRawType(TypeToken<?> typetoken) {
                return typetoken.getRawType();
            }

            Iterable<? extends TypeToken<?>> getInterfaces(TypeToken<?> typetoken) {
                return typetoken.getGenericInterfaces();
            }

            @Nullable
            TypeToken<?> getSuperclass(TypeToken<?> typetoken) {
                return typetoken.getGenericSuperclass();
            }
        };
        static final TypeToken.TypeCollector<Class<?>> FOR_RAW_TYPE = new TypeToken.TypeCollector<Class<?>>() {
            Class<?> getRawType(Class<?> oclass) {
                return oclass;
            }

            Iterable<? extends Class<?>> getInterfaces(Class<?> oclass) {
                return Arrays.asList(oclass.getInterfaces());
            }

            @Nullable
            Class<?> getSuperclass(Class<?> oclass) {
                return oclass.getSuperclass();
            }
        };

        private TypeCollector() {}

        final TypeToken.TypeCollector<K> classesOnly() {
            return new TypeToken.TypeCollector.ForwardingTypeCollector<K>(this) {
                @Override
                Iterable<? extends K> getInterfaces(K k0) {
                    return ImmutableSet.of();
                }

                @Override
                ImmutableList<K> collectTypes(Iterable<? extends K> iterable) {
                    ImmutableList.Builder immutablelist_builder = ImmutableList.builder();
                    Iterator iterator = iterable.iterator();

                    while (iterator.hasNext()) {
                        Object object = iterator.next();

                        if (!this.getRawType(object).isInterface()) {
                            immutablelist_builder.add(object);
                        }
                    }

                    return super.collectTypes((Iterable) immutablelist_builder.build());
                }
            };
        }

        final ImmutableList<K> collectTypes(K k0) {
            return this.collectTypes((Iterable) ImmutableList.of(k0));
        }

        ImmutableList<K> collectTypes(Iterable<? extends K> iterable) {
            HashMap hashmap = Maps.newHashMap();
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                this.collectTypes(object, hashmap);
            }

            return sortKeysByValue(hashmap, Ordering.natural().reverse());
        }

        private int collectTypes(K k0, Map<? super K, Integer> map) {
            Integer integer = (Integer) map.get(this);

            if (integer != null) {
                return integer;
            } else {
                int i = this.getRawType(k0).isInterface() ? 1 : 0;

                Object object;

                for (Iterator iterator = this.getInterfaces(k0).iterator(); iterator.hasNext(); i = Math.max(i, this.collectTypes(object, map))) {
                    object = iterator.next();
                }

                Object object1 = this.getSuperclass(k0);

                if (object1 != null) {
                    i = Math.max(i, this.collectTypes(object1, map));
                }

                map.put(k0, i + 1);
                return i + 1;
            }
        }

        private static <K, V> ImmutableList<K> sortKeysByValue(final Map<K, V> map, final Comparator<? super V> comparator) {
            Ordering ordering = new Ordering<K>() {
                @Override
                public int compare(K k0, K k1) {
                    return comparator.compare(map.get(k0), map.get(k1));
                }
            };

            return ordering.immutableSortedCopy(map.keySet());
        }

        abstract Class<?> getRawType(K k0);

        abstract Iterable<? extends K> getInterfaces(K k0);

        @Nullable
        abstract K getSuperclass(K k0);

        private static class ForwardingTypeCollector<K> extends TypeToken.TypeCollector<K> {

            private final TypeToken.TypeCollector<K> delegate;

            ForwardingTypeCollector(TypeToken.TypeCollector<K> typetoken_typecollector) {
                super(null);
                this.delegate = typetoken_typecollector;
            }

            @Override
            Class<?> getRawType(K k0) {
                return this.delegate.getRawType(k0);
            }

            @Override
            Iterable<? extends K> getInterfaces(K k0) {
                return this.delegate.getInterfaces(k0);
            }

            @Override
            K getSuperclass(K k0) {
                return this.delegate.getSuperclass(k0);
            }
        }
    }

    private static final class SimpleTypeToken<T> extends TypeToken<T> {

        private static final long serialVersionUID = 0L;

        SimpleTypeToken(Type type) {
            super(type, null);
        }
    }

    private static enum TypeFilter implements Predicate<TypeToken<?>> {

        IGNORE_TYPE_VARIABLE_OR_WILDCARD {
            public boolean apply(TypeToken<?> typetoken) {
                return !(typetoken.runtimeType instanceof TypeVariable) && !(typetoken.runtimeType instanceof WildcardType);
            }
        },
        INTERFACE_ONLY {
            public boolean apply(TypeToken<?> typetoken) {
                return typetoken.getRawType().isInterface();
            }
        };

        private TypeFilter() {}
    }

    private final class ClassSet extends TypeToken<T>.TypeSet {

        private transient ImmutableSet<TypeToken<? super T>> classes;
        private static final long serialVersionUID = 0L;

        private ClassSet() {
            super();
        }

        @Override
        protected Set<TypeToken<? super T>> delegate() {
            ImmutableSet immutableset = this.classes;

            if (immutableset == null) {
                ImmutableList immutablelist = TypeToken.TypeCollector.FOR_GENERIC_TYPE.classesOnly().collectTypes((Object) TypeToken.this);

                return this.classes = FluentIterable.from((Iterable) immutablelist).filter((Predicate) TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet();
            } else {
                return immutableset;
            }
        }

        @Override
        public TypeToken<T>.TypeSet classes() {
            return this;
        }

        @Override
        public Set<Class<? super T>> rawTypes() {
            ImmutableList immutablelist = TypeToken.TypeCollector.FOR_RAW_TYPE.classesOnly().collectTypes((Iterable) TypeToken.this.getImmediateRawTypes());

            return ImmutableSet.copyOf((Collection) immutablelist);
        }

        @Override
        public TypeToken<T>.TypeSet interfaces() {
            throw new UnsupportedOperationException("classes().interfaces() not supported.");
        }

        private Object readResolve() {
            return TypeToken.this.getTypes().classes();
        }
    }

    private final class InterfaceSet extends TypeToken<T>.TypeSet {

        private final transient TypeToken<T>.TypeSet allTypes;
        private transient ImmutableSet<TypeToken<? super T>> interfaces;
        private static final long serialVersionUID = 0L;

        InterfaceSet(TypeToken<T>.TypeSet typetoken_typeset) {
            super();
            this.allTypes = typetoken_typeset;
        }

        @Override
        protected Set<TypeToken<? super T>> delegate() {
            ImmutableSet immutableset = this.interfaces;

            return immutableset == null ? (this.interfaces = FluentIterable.from((Iterable) this.allTypes).filter((Predicate) TypeToken.TypeFilter.INTERFACE_ONLY).toSet()) : immutableset;
        }

        @Override
        public TypeToken<T>.TypeSet interfaces() {
            return this;
        }

        @Override
        public Set<Class<? super T>> rawTypes() {
            ImmutableList immutablelist = TypeToken.TypeCollector.FOR_RAW_TYPE.collectTypes((Iterable) TypeToken.this.getImmediateRawTypes());

            return FluentIterable.from((Iterable) immutablelist).filter(new Predicate<Class<?>>() {
                public boolean apply(Class<?> oclass) {
                    return oclass.isInterface();
                }
            }).toSet();
        }

        @Override
        public TypeToken<T>.TypeSet classes() {
            throw new UnsupportedOperationException("interfaces().classes() not supported.");
        }

        private Object readResolve() {
            return TypeToken.this.getTypes().interfaces();
        }
    }

    public class TypeSet extends ForwardingSet<TypeToken<? super T>> implements Serializable {

        private transient ImmutableSet<TypeToken<? super T>> types;
        private static final long serialVersionUID = 0L;

        TypeSet() {}

        public TypeToken<T>.TypeSet interfaces() {
            return TypeToken.this.new InterfaceSet(this);
        }

        public TypeToken<T>.TypeSet classes() {
            return TypeToken.this.new ClassSet();
        }

        @Override
        protected Set<TypeToken<? super T>> delegate() {
            ImmutableSet immutableset = this.types;

            if (immutableset == null) {
                ImmutableList immutablelist = TypeToken.TypeCollector.FOR_GENERIC_TYPE.collectTypes((Object) TypeToken.this);

                return this.types = FluentIterable.from((Iterable) immutablelist).filter((Predicate) TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet();
            } else {
                return immutableset;
            }
        }

        public Set<Class<? super T>> rawTypes() {
            ImmutableList immutablelist = TypeToken.TypeCollector.FOR_RAW_TYPE.collectTypes((Iterable) TypeToken.this.getImmediateRawTypes());

            return ImmutableSet.copyOf((Collection) immutablelist);
        }
    }
}
