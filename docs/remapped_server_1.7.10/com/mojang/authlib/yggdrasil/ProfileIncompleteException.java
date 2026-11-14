package com.mojang.authlib.yggdrasil;

public class ProfileIncompleteException extends RuntimeException {

    public ProfileIncompleteException() {}

    public ProfileIncompleteException(String s) {
        super(s);
    }

    public ProfileIncompleteException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProfileIncompleteException(Throwable throwable) {
        super(throwable);
    }
}
