package com.mojang.authlib.yggdrasil;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException() {}

    public ProfileNotFoundException(String s) {
        super(s);
    }

    public ProfileNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProfileNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
