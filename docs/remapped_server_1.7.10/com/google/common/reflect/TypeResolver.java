package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

@Beta
public final class TypeResolver {

    private final TypeResolver.TypeTable typeTable;

    public TypeResolver() {
        this.typeTable = new TypeResolver.TypeTable();
    }

    private TypeResolver(TypeResolver.TypeTable typeresolver_typetable) {
        this.typeTable = typeresolver_typetable;
    }

    static TypeResolver accordingTo(Type type) {
        return (new TypeResolver()).where(TypeResolver.TypeMappingIntrospector.getTypeMappings(type));
    }

    public TypeResolver where(Type type, Type type1) {
        HashMap hashmap = Maps.newHashMap();

        populateTypeMappings(hashmap, (Type) Preconditions.checkNotNull(type), (Type) Preconditions.checkNotNull(type1));
        return this.where(hashmap);
    }

    TypeResolver where(Map<? extends TypeVariable<?>, ? extends Type> map) {
        return new TypeResolver(this.typeTable.where(map));
    }

    private static void populateTypeMappings(final Map<TypeVariable<?>, Type> map, Type type, final Type type1) {
        if (!type.equals(type1)) {
            (new TypeVisitor() {
                @Override
                void visitTypeVariable(TypeVariable<?> typevariable) {
                    map.put(typevariable, type1);
                }

                @Override
                void visitWildcardType(WildcardType wildcardtype) {
                    WildcardType wildcardtype1 = (WildcardType) TypeResolver.expectArgument(WildcardType.class, type1);
                    Type[] atype = wildcardtype.getUpperBounds();
                    Type[] atype1 = wildcardtype1.getUpperBounds();
                    Type[] atype2 = wildcardtype.getLowerBounds();
                    Type[] atype3 = wildcardtype1.getLowerBounds();

                    Preconditions.checkArgument(atype.length == atype1.length && atype2.length == atype3.length, "Incompatible type: %s vs. %s", wildcardtype, type1);

                    int i;

                    for (i = 0; i < atype.length; ++i) {
                        TypeResolver.populateTypeMappings(map, atype[i], atype1[i]);
                    }

                    for (i = 0; i < atype2.length; ++i) {
                        TypeResolver.populateTypeMappings(map, atype2[i], atype3[i]);
                    }

                }

                @Override
                void visitParameterizedType(ParameterizedType parameterizedtype) {
                    ParameterizedType parameterizedtype1 = (ParameterizedType) TypeResolver.expectArgument(ParameterizedType.class, type1);

                    Preconditions.checkArgument(parameterizedtype.getRawType().equals(parameterizedtype1.getRawType()), "Inconsistent raw type: %s vs. %s", parameterizedtype, type1);
                    Type[] atype = parameterizedtype.getActualTypeArguments();
                    Type[] atype1 = parameterizedtype1.getActualTypeArguments();

                    Preconditions.checkArgument(atype.length == atype1.length, "%s not compatible with %s", parameterizedtype, parameterizedtype1);

                    for (int i = 0; i < atype.length; ++i) {
                        TypeResolver.populateTypeMappings(map, atype[i], atype1[i]);
                    }

                }

                @Override
                void visitGenericArrayType(GenericArrayType genericarraytype) {
                    Type type2 = Types.getComponentType(type1);

                    Preconditions.checkArgument(type2 != null, "%s is not an array type.", type1);
                    TypeResolver.populateTypeMappings(map, genericarraytype.getGenericComponentType(), type2);
                }

                @Override
                void visitClass(Class<?> oclass) {
                    throw new IllegalArgumentException("No type mapping from " + oclass);
                }
            }).visit(new Type[]{type});
        }
    }

    public Type resolveType(Type type) {
        Preconditions.checkNotNull(type);
        if (type instanceof TypeVariable) {
            return this.typeTable.resolve((TypeVariable) type);
        } else if (type instanceof ParameterizedType) {
            return this.resolveParameterizedType((ParameterizedType) type);
        } else if (type instanceof GenericArrayType) {
            return this.resolveGenericArrayType((GenericArrayType) type);
        } else if (type instanceof WildcardType) {
            WildcardType wildcardtype = (WildcardType) type;

            return new Types.WildcardTypeImpl(this.resolveTypes(wildcardtype.getLowerBounds()), this.resolveTypes(wildcardtype.getUpperBounds()));
        } else {
            return type;
        }
    }

    private Type[] resolveTypes(Type[] atype) {
        Type[] atype1 = new Type[atype.length];

        for (int i = 0; i < atype.length; ++i) {
            atype1[i] = this.resolveType(atype[i]);
        }

        return atype1;
    }

    private Type resolveGenericArrayType(GenericArrayType genericarraytype) {
        Type type = this.resolveType(genericarraytype.getGenericComponentType());

        return Types.newArrayType(type);
    }

    private ParameterizedType resolveParameterizedType(ParameterizedType parameterizedtype) {
        Type type = parameterizedtype.getOwnerType();
        Type type1 = type == null ? null : this.resolveType(type);
        Type type2 = this.resolveType(parameterizedtype.getRawType());
        Type[] atype = parameterizedtype.getActualTypeArguments();
        Type[] atype1 = new Type[atype.length];

        for (int i = 0; i < atype.length; ++i) {
            atype1[i] = this.resolveType(atype[i]);
        }

        return Types.newParameterizedTypeWithOwner(type1, (Class) type2, atype1);
    }

    private static <T> T expectArgument(Class<T> oclass, Object object) {
        try {
            return oclass.cast(object);
        } catch (ClassCastException classcastexception) {
            throw new IllegalArgumentException(object + " is not a " + oclass.getSimpleName());
        }
    }

    private static final class WildcardCapturer {

        private final AtomicInteger id;

        private WildcardCapturer() {
            this.id = new AtomicInteger();
        }

        Type capture(Type type) {
            Preconditions.checkNotNull(type);
            if (type instanceof Class) {
                return type;
            } else if (type instanceof TypeVariable) {
                return type;
            } else if (type instanceof GenericArrayType) {
                GenericArrayType genericarraytype = (GenericArrayType) type;

                return Types.newArrayType(this.capture(genericarraytype.getGenericComponentType()));
            } else if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedtype = (ParameterizedType) type;

                return Types.newParameterizedTypeWithOwner(this.captureNullable(parameterizedtype.getOwnerType()), (Class) parameterizedtype.getRawType(), this.capture(parameterizedtype.getActualTypeArguments()));
            } else if (type instanceof WildcardType) {
                WildcardType wildcardtype = (WildcardType) type;
                Type[] atype = wildcardtype.getLowerBounds();

                if (atype.length == 0) {
                    Type[] atype1 = wildcardtype.getUpperBounds();
                    String s = "capture#" + this.id.incrementAndGet() + "-of ? extends " + Joiner.on('&').join((Object[]) atype1);

                    return Types.newTypeVariable(TypeResolver.WildcardCapturer.class, s, wildcardtype.getUpperBounds());
                } else {
                    return type;
                }
            } else {
                throw new AssertionError("must have been one of the known types");
            }
        }

        private Type captureNullable(@Nullable Type type) {
            return type == null ? null : this.capture(type);
        }

        private Type[] capture(Type[] atype) {
            Type[] atype1 = new Type[atype.length];

            for (int i = 0; i < atype.length; ++i) {
                atype1[i] = this.capture(atype[i]);
            }

            return atype1;
        }
    }

    private static final class TypeMappingIntrospector extends TypeVisitor {

        private static final TypeResolver.WildcardCapturer wildcardCapturer = new TypeResolver.WildcardCapturer();
        private final Map<TypeVariable<?>, Type> mappings = Maps.newHashMap();

        private TypeMappingIntrospector() {}

        static ImmutableMap<TypeVariable<?>, Type> getTypeMappings(Type type) {
            TypeResolver.TypeMappingIntrospector typeresolver_typemappingintrospector = new TypeResolver.TypeMappingIntrospector();

            typeresolver_typemappingintrospector.visit(new Type[]{TypeResolver.TypeMappingIntrospector.wildcardCapturer.capture(type)});
            return ImmutableMap.copyOf(typeresolver_typemappingintrospector.mappings);
        }

        @Override
        void visitClass(Class<?> oclass) {
            this.visit(new Type[]{oclass.getGenericSuperclass()});
            this.visit(oclass.getGenericInterfaces());
        }

        @Override
        void visitParameterizedType(ParameterizedType parameterizedtype) {
            Class oclass = (Class) parameterizedtype.getRawType();
            TypeVariable[] atypevariable = oclass.getTypeParameters();
            Type[] atype = parameterizedtype.getActualTypeArguments();

            Preconditions.checkState(atypevariable.length == atype.length);

            for (int i = 0; i < atypevariable.length; ++i) {
                this.map(atypevariable[i], atype[i]);
            }

            this.visit(new Type[]{oclass});
            this.visit(new Type[]{parameterizedtype.getOwnerType()});
        }

        @Override
        void visitTypeVariable(TypeVariable<?> typevariable) {
            this.visit(typevariable.getBounds());
        }

        @Override
        void visitWildcardType(WildcardType wildcardtype) {
            this.visit(wildcardtype.getUpperBounds());
        }

        private void map(TypeVariable<?> typevariable, Type type) {
            if (!this.mappings.containsKey(typevariable)) {
                for (Type type1 = type; type1 != null; type1 = (Type) this.mappings.get(type1)) {
                    if (typevariable.equals(type1)) {
                        for (Type type2 = type; type2 != null; type2 = (Type) this.mappings.remove(type2)) {
                            ;
                        }

                        return;
                    }
                }

                this.mappings.put(typevariable, type);
            }
        }
    }

    private static class TypeTable {

        private final ImmutableMap<TypeVariable<?>, Type> map;

        TypeTable() {
            this.map = ImmutableMap.of();
        }

        private TypeTable(ImmutableMap<TypeVariable<?>, Type> immutablemap) {
            this.map = immutablemap;
        }

        final TypeResolver.TypeTable where(Map<? extends TypeVariable<?>, ? extends Type> map) {
            ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();

            immutablemap_builder.putAll(this.map);
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                TypeVariable typevariable = (TypeVariable) entry.getKey();
                Type type = (Type) entry.getValue();

                Preconditions.checkArgument(!typevariable.equals(type), "Type variable %s bound to itself", typevariable);
                immutablemap_builder.put(typevariable, type);
            }

            return new TypeResolver.TypeTable(immutablemap_builder.build());
        }

        final Type resolve(final TypeVariable<?> typevariable) {
            TypeResolver.TypeTable typeresolver_typetable = new TypeResolver.TypeTable() {
                @Override
                public Type resolveInternal(TypeVariable<?> typevariable1, TypeResolver.TypeTable typeresolver_typetable1) {
                    return (Type) (typevariable1.getGenericDeclaration().equals(typevariable.getGenericDeclaration()) ? typevariable1 : TypeTable.this.resolveInternal(typevariable1, typeresolver_typetable1));
                }
            };

            return this.resolveInternal(typevariable, typeresolver_typetable);
        }

        Type resolveInternal(TypeVariable<?> typevariable, TypeResolver.TypeTable typeresolver_typetable) {
            Type type = (Type) this.map.get(typevariable);

            if (type == null) {
                Type[] atype = typevariable.getBounds();

                return atype.length == 0 ? typevariable : Types.newTypeVariable(typevariable.getGenericDeclaration(), typevariable.getName(), (new TypeResolver(typeresolver_typetable)).resolveTypes(atype));
            } else {
                return (new TypeResolver(typeresolver_typetable)).resolveType(type);
            }
        }
    }
}
