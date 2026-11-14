package com.mojang.authlib;

import java.util.HashMap;
import java.util.Map;

public enum UserType {

    LEGACY("legacy"), MOJANG("mojang");

    private static final Map<String, UserType> BY_NAME = new HashMap();
    private final String name;

    private UserType(String s) {
        this.name = s;
    }

    public static UserType byName(String s) {
        return (UserType) UserType.BY_NAME.get(s.toLowerCase());
    }

    public String getName() {
        return this.name;
    }

    static {
        UserType[] ausertype = values();
        int i = ausertype.length;

        for (int j = 0; j < i; ++j) {
            UserType usertype = ausertype[j];

            UserType.BY_NAME.put(usertype.name, usertype);
        }

    }
}
