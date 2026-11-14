package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Arrays;
import javax.annotation.Nullable;

@GwtCompatible
public final class Objects {

    private Objects() {}

    public static boolean equal(@Nullable Object object, @Nullable Object object1) {
        return object == object1 || object != null && object.equals(object1);
    }

    public static int hashCode(@Nullable Object... aobject) {
        return Arrays.hashCode(aobject);
    }

    public static Objects.ToStringHelper toStringHelper(Object object) {
        return new Objects.ToStringHelper(simpleName(object.getClass()));
    }

    public static Objects.ToStringHelper toStringHelper(Class<?> oclass) {
        return new Objects.ToStringHelper(simpleName(oclass));
    }

    public static Objects.ToStringHelper toStringHelper(String s) {
        return new Objects.ToStringHelper(s);
    }

    private static String simpleName(Class<?> oclass) {
        String s = oclass.getName();

        s = s.replaceAll("\\$[0-9]+", "\\$");
        int i = s.lastIndexOf(36);

        if (i == -1) {
            i = s.lastIndexOf(46);
        }

        return s.substring(i + 1);
    }

    public static <T> T firstNonNull(@Nullable T t0, @Nullable T t1) {
        return t0 != null ? t0 : Preconditions.checkNotNull(t1);
    }

    public static final class ToStringHelper {

        private final String className;
        private Objects.ToStringHelper.ValueHolder holderHead;
        private Objects.ToStringHelper.ValueHolder holderTail;
        private boolean omitNullValues;

        private ToStringHelper(String s) {
            this.holderHead = new Objects.ToStringHelper.ValueHolder();
            this.holderTail = this.holderHead;
            this.omitNullValues = false;
            this.className = (String) Preconditions.checkNotNull(s);
        }

        public Objects.ToStringHelper omitNullValues() {
            this.omitNullValues = true;
            return this;
        }

        public Objects.ToStringHelper add(String s, @Nullable Object object) {
            return this.addHolder(s, object);
        }

        public Objects.ToStringHelper add(String s, boolean flag) {
            return this.addHolder(s, String.valueOf(flag));
        }

        public Objects.ToStringHelper add(String s, char c0) {
            return this.addHolder(s, String.valueOf(c0));
        }

        public Objects.ToStringHelper add(String s, double d0) {
            return this.addHolder(s, String.valueOf(d0));
        }

        public Objects.ToStringHelper add(String s, float f) {
            return this.addHolder(s, String.valueOf(f));
        }

        public Objects.ToStringHelper add(String s, int i) {
            return this.addHolder(s, String.valueOf(i));
        }

        public Objects.ToStringHelper add(String s, long i) {
            return this.addHolder(s, String.valueOf(i));
        }

        public Objects.ToStringHelper addValue(@Nullable Object object) {
            return this.addHolder(object);
        }

        public Objects.ToStringHelper addValue(boolean flag) {
            return this.addHolder(String.valueOf(flag));
        }

        public Objects.ToStringHelper addValue(char c0) {
            return this.addHolder(String.valueOf(c0));
        }

        public Objects.ToStringHelper addValue(double d0) {
            return this.addHolder(String.valueOf(d0));
        }

        public Objects.ToStringHelper addValue(float f) {
            return this.addHolder(String.valueOf(f));
        }

        public Objects.ToStringHelper addValue(int i) {
            return this.addHolder(String.valueOf(i));
        }

        public Objects.ToStringHelper addValue(long i) {
            return this.addHolder(String.valueOf(i));
        }

        public String toString() {
            boolean flag = this.omitNullValues;
            String s = "";
            StringBuilder stringbuilder = (new StringBuilder(32)).append(this.className).append('{');

            for (Objects.ToStringHelper.ValueHolder objects_tostringhelper_valueholder = this.holderHead.next; objects_tostringhelper_valueholder != null; objects_tostringhelper_valueholder = objects_tostringhelper_valueholder.next) {
                if (!flag || objects_tostringhelper_valueholder.value != null) {
                    stringbuilder.append(s);
                    s = ", ";
                    if (objects_tostringhelper_valueholder.name != null) {
                        stringbuilder.append(objects_tostringhelper_valueholder.name).append('=');
                    }

                    stringbuilder.append(objects_tostringhelper_valueholder.value);
                }
            }

            return stringbuilder.append('}').toString();
        }

        private Objects.ToStringHelper.ValueHolder addHolder() {
            Objects.ToStringHelper.ValueHolder objects_tostringhelper_valueholder = new Objects.ToStringHelper.ValueHolder();

            this.holderTail = this.holderTail.next = objects_tostringhelper_valueholder;
            return objects_tostringhelper_valueholder;
        }

        private Objects.ToStringHelper addHolder(@Nullable Object object) {
            Objects.ToStringHelper.ValueHolder objects_tostringhelper_valueholder = this.addHolder();

            objects_tostringhelper_valueholder.value = object;
            return this;
        }

        private Objects.ToStringHelper addHolder(String s, @Nullable Object object) {
            Objects.ToStringHelper.ValueHolder objects_tostringhelper_valueholder = this.addHolder();

            objects_tostringhelper_valueholder.value = object;
            objects_tostringhelper_valueholder.name = (String) Preconditions.checkNotNull(s);
            return this;
        }

        private static final class ValueHolder {

            String name;
            Object value;
            Objects.ToStringHelper.ValueHolder next;

            private ValueHolder() {}
        }
    }
}
