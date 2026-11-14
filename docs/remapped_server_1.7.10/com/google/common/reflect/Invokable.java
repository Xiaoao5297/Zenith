package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import javax.annotation.Nullable;

@Beta
public abstract class Invokable<T, R> extends Element implements GenericDeclaration {

    <M extends AccessibleObject & Member> Invokable(M m0) {
        super(m0);
    }

    public static Invokable<?, Object> from(Method method) {
        return new Invokable.MethodInvokable<>(method);
    }

    public static <T> Invokable<T, T> from(Constructor<T> constructor) {
        return new Invokable.ConstructorInvokable<>(constructor);
    }

    public abstract boolean isOverridable();

    public abstract boolean isVarArgs();

    public final R invoke(@Nullable T t0, Object... aobject) throws InvocationTargetException, IllegalAccessException {
        return this.invokeInternal(t0, (Object[]) Preconditions.checkNotNull(aobject));
    }

    public final TypeToken<? extends R> getReturnType() {
        return TypeToken.of(this.getGenericReturnType());
    }

    public final ImmutableList<Parameter> getParameters() {
        Type[] atype = this.getGenericParameterTypes();
        Annotation[][] aannotation = this.getParameterAnnotations();
        ImmutableList.Builder immutablelist_builder = ImmutableList.builder();

        for (int i = 0; i < atype.length; ++i) {
            immutablelist_builder.add((Object) (new Parameter(this, i, TypeToken.of(atype[i]), aannotation[i])));
        }

        return immutablelist_builder.build();
    }

    public final ImmutableList<TypeToken<? extends Throwable>> getExceptionTypes() {
        ImmutableList.Builder immutablelist_builder = ImmutableList.builder();
        Type[] atype = this.getGenericExceptionTypes();
        int i = atype.length;

        for (int j = 0; j < i; ++j) {
            Type type = atype[j];
            TypeToken typetoken = TypeToken.of(type);

            immutablelist_builder.add((Object) typetoken);
        }

        return immutablelist_builder.build();
    }

    public final <R1 extends R> Invokable<T, R1> returning(Class<R1> oclass) {
        return this.returning(TypeToken.of(oclass));
    }

    public final <R1 extends R> Invokable<T, R1> returning(TypeToken<R1> typetoken) {
        if (!typetoken.isAssignableFrom(this.getReturnType())) {
            throw new IllegalArgumentException("Invokable is known to return " + this.getReturnType() + ", not " + typetoken);
        } else {
            return this;
        }
    }

    @Override
    public final Class<? super T> getDeclaringClass() {
        return super.getDeclaringClass();
    }

    @Override
    public TypeToken<T> getOwnerType() {
        return TypeToken.of(this.getDeclaringClass());
    }

    abstract Object invokeInternal(@Nullable Object object, Object[] aobject) throws InvocationTargetException, IllegalAccessException;

    abstract Type[] getGenericParameterTypes();

    abstract Type[] getGenericExceptionTypes();

    abstract Annotation[][] getParameterAnnotations();

    abstract Type getGenericReturnType();

    static class ConstructorInvokable<T> extends Invokable<T, T> {

        final Constructor<?> constructor;

        ConstructorInvokable(Constructor<?> constructor) {
            super(constructor);
            this.constructor = constructor;
        }

        @Override
        final Object invokeInternal(@Nullable Object object, Object[] aobject) throws InvocationTargetException, IllegalAccessException {
            try {
                return this.constructor.newInstance(aobject);
            } catch (InstantiationException instantiationexception) {
                throw new RuntimeException(this.constructor + " failed.", instantiationexception);
            }
        }

        @Override
        Type getGenericReturnType() {
            Class oclass = this.getDeclaringClass();
            TypeVariable[] atypevariable = oclass.getTypeParameters();

            return (Type) (atypevariable.length > 0 ? Types.newParameterizedType(oclass, atypevariable) : oclass);
        }

        @Override
        Type[] getGenericParameterTypes() {
            Type[] atype = this.constructor.getGenericParameterTypes();

            if (atype.length > 0 && this.mayNeedHiddenThis()) {
                Class[] aclass = this.constructor.getParameterTypes();

                if (atype.length == aclass.length && aclass[0] == this.getDeclaringClass().getEnclosingClass()) {
                    return (Type[]) Arrays.copyOfRange(atype, 1, atype.length);
                }
            }

            return atype;
        }

        @Override
        Type[] getGenericExceptionTypes() {
            return this.constructor.getGenericExceptionTypes();
        }

        @Override
        final Annotation[][] getParameterAnnotations() {
            return this.constructor.getParameterAnnotations();
        }

        public final TypeVariable<?>[] getTypeParameters() {
            TypeVariable[] atypevariable = this.getDeclaringClass().getTypeParameters();
            TypeVariable[] atypevariable1 = this.constructor.getTypeParameters();
            TypeVariable[] atypevariable2 = new TypeVariable[atypevariable.length + atypevariable1.length];

            System.arraycopy(atypevariable, 0, atypevariable2, 0, atypevariable.length);
            System.arraycopy(atypevariable1, 0, atypevariable2, atypevariable.length, atypevariable1.length);
            return atypevariable2;
        }

        @Override
        public final boolean isOverridable() {
            return false;
        }

        @Override
        public final boolean isVarArgs() {
            return this.constructor.isVarArgs();
        }

        private boolean mayNeedHiddenThis() {
            Class oclass = this.constructor.getDeclaringClass();

            if (oclass.getEnclosingConstructor() != null) {
                return true;
            } else {
                Method method = oclass.getEnclosingMethod();

                return method != null ? !Modifier.isStatic(method.getModifiers()) : oclass.getEnclosingClass() != null && !Modifier.isStatic(oclass.getModifiers());
            }
        }
    }

    static class MethodInvokable<T> extends Invokable<T, Object> {

        final Method method;

        MethodInvokable(Method method) {
            super(method);
            this.method = method;
        }

        @Override
        final Object invokeInternal(@Nullable Object object, Object[] aobject) throws InvocationTargetException, IllegalAccessException {
            return this.method.invoke(object, aobject);
        }

        @Override
        Type getGenericReturnType() {
            return this.method.getGenericReturnType();
        }

        @Override
        Type[] getGenericParameterTypes() {
            return this.method.getGenericParameterTypes();
        }

        @Override
        Type[] getGenericExceptionTypes() {
            return this.method.getGenericExceptionTypes();
        }

        @Override
        final Annotation[][] getParameterAnnotations() {
            return this.method.getParameterAnnotations();
        }

        public final TypeVariable<?>[] getTypeParameters() {
            return this.method.getTypeParameters();
        }

        @Override
        public final boolean isOverridable() {
            return !this.isFinal() && !this.isPrivate() && !this.isStatic() && !Modifier.isFinal(this.getDeclaringClass().getModifiers());
        }

        @Override
        public final boolean isVarArgs() {
            return this.method.isVarArgs();
        }
    }
}
