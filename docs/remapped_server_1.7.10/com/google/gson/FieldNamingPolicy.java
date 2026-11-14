package com.google.gson;

import java.lang.reflect.Field;

public enum FieldNamingPolicy implements FieldNamingStrategy {

    IDENTITY {
        @Override
        public String translateName(Field field) {
            return field.getName();
        }
    },
    UPPER_CAMEL_CASE {
        @Override
        public String translateName(Field field) {
            return FieldNamingPolicy.upperCaseFirstLetter(field.getName());
        }
    },
    UPPER_CAMEL_CASE_WITH_SPACES {
        @Override
        public String translateName(Field field) {
            return FieldNamingPolicy.upperCaseFirstLetter(FieldNamingPolicy.separateCamelCase(field.getName(), " "));
        }
    },
    LOWER_CASE_WITH_UNDERSCORES {
        @Override
        public String translateName(Field field) {
            return FieldNamingPolicy.separateCamelCase(field.getName(), "_").toLowerCase();
        }
    },
    LOWER_CASE_WITH_DASHES {
        @Override
        public String translateName(Field field) {
            return FieldNamingPolicy.separateCamelCase(field.getName(), "-").toLowerCase();
        }
    };

    private FieldNamingPolicy() {}

    private static String separateCamelCase(String s, String s1) {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);

            if (Character.isUpperCase(c0) && stringbuilder.length() != 0) {
                stringbuilder.append(s1);
            }

            stringbuilder.append(c0);
        }

        return stringbuilder.toString();
    }

    private static String upperCaseFirstLetter(String s) {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;

        char c0;

        for (c0 = s.charAt(i); i < s.length() - 1 && !Character.isLetter(c0); c0 = s.charAt(i)) {
            stringbuilder.append(c0);
            ++i;
        }

        if (i == s.length()) {
            return stringbuilder.toString();
        } else if (!Character.isUpperCase(c0)) {
            char c1 = Character.toUpperCase(c0);

            ++i;
            String s1 = modifyString(c1, s, i);

            return stringbuilder.append(s1).toString();
        } else {
            return s;
        }
    }

    private static String modifyString(char c0, String s, int i) {
        return i < s.length() ? c0 + s.substring(i) : String.valueOf(c0);
    }
}
