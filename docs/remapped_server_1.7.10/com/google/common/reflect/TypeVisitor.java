package com.google.common.reflect;

import com.google.common.collect.Sets;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Set;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
abstract class TypeVisitor {

    private final Set<Type> visited = Sets.newHashSet();

    TypeVisitor() {}

    public final void visit(Type... atype) {
        Type[] atype1 = atype;
        int i = atype.length;

        for (int j = 0; j < i; ++j) {
            Type type = atype1[j];

            if (type != null && this.visited.add(type)) {
                boolean flag = false;

                try {
                    if (type instanceof TypeVariable) {
                        this.visitTypeVariable((TypeVariable) type);
                    } else if (type instanceof WildcardType) {
                        this.visitWildcardType((WildcardType) type);
                    } else if (type instanceof ParameterizedType) {
                        this.visitParameterizedType((ParameterizedType) type);
                    } else if (type instanceof Class) {
                        this.visitClass((Class) type);
                    } else {
                        if (!(type instanceof GenericArrayType)) {
                            throw new AssertionError("Unknown type: " + type);
                        }

                        this.visitGenericArrayType((GenericArrayType) type);
                    }

                    flag = true;
                } finally {
                    if (!flag) {
                        this.visited.remove(type);
                    }

                }
            }
        }

    }

    void visitClass(Class<?> oclass) {}

    void visitGenericArrayType(GenericArrayType genericarraytype) {}

    void visitParameterizedType(ParameterizedType parameterizedtype) {}

    void visitTypeVariable(TypeVariable<?> typevariable) {}

    void visitWildcardType(WildcardType wildcardtype) {}
}
