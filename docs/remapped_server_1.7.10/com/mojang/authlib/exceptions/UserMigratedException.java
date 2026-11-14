package com.mojang.authlib.exceptions;

public class UserMigratedException extends InvalidCredentialsException {

    public UserMigratedException() {}

    public UserMigratedException(String s) {
        super(s);
    }

    public UserMigratedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UserMigratedException(Throwable throwable) {
        super(throwable);
    }
}
