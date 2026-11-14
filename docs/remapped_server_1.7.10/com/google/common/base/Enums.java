package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.lang.reflect.Field;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
@Beta
public final class Enums {

    private Enums() {}

    @GwtIncompatible("reflection")
    public static Field getField(Enum<?> oenum) {
        Class oclass = oenum.getDeclaringClass();

        try {
            return oclass.getDeclaredField(oenum.name());
        } catch (NoSuchFieldException nosuchfieldexception) {
            throw new AssertionError(nosuchfieldexception);
        }
    }

    /** @deprecated */
    @Deprecated
    public static <T extends Enum<T>> Function<String, T> valueOfFunction(Class<T> oclass) {
        return new Enums.ValueOfFunction<>(oclass);
    }

    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> oclass, String s) {
        Preconditions.checkNotNull(oclass);
        Preconditions.checkNotNull(s);

        try {
            return Optional.of(Enum.valueOf(oclass, s));
        } catch (IllegalArgumentException illegalargumentexception) {
            return Optional.absent();
        }
    }

    public static <T extends Enum<T>> Converter<String, T> stringConverter(Class<T> oclass) {
        return new Enums.StringConverter<>(oclass);
    }

    private static final class StringConverter<T extends Enum<T>> extends Converter<String, T> implements Serializable {

        private final Class<T> enumClass;
        private static final long serialVersionUID = 0L;

        StringConverter(Class<T> oclass) {
            this.enumClass = (Class) Preconditions.checkNotNull(oclass);
        }

        protected T doForward(String s) {
            return Enum.valueOf(this.enumClass, s);
        }

        protected String doBackward(T t0) {
            return t0.name();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Enums.StringConverter) {
                Enums.StringConverter enums_stringconverter = (Enums.StringConverter) object;

                return this.enumClass.equals(enums_stringconverter.enumClass);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.enumClass.hashCode();
        }

        public String toString() {
            return "Enums.stringConverter(" + this.enumClass.getName() + ".class)";
        }
    }

    private static final class ValueOfFunction<T extends Enum<T>> implements Function<String, T>, Serializable {

        private final Class<T> enumClass;
        private static final long serialVersionUID = 0L;

        private ValueOfFunction(Class<T> oclass) {
            this.enumClass = (Class) Preconditions.checkNotNull(oclass);
        }

        public T apply(String s) {
            try {
                return Enum.valueOf(this.enumClass, s);
            } catch (IllegalArgumentException illegalargumentexception) {
                return null;
            }
        }

        @Override
        public boolean equals(@Nullable Object object) {
            return object instanceof Enums.ValueOfFunction && this.enumClass.equals(((Enums.ValueOfFunction) object).enumClass);
        }

        public int hashCode() {
            return this.enumClass.hashCode();
        }

        public String toString() {
            return "Enums.valueOf(" + this.enumClass + ")";
        }
    }
}
