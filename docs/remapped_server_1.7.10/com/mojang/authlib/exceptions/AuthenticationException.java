package com.mojang.authlib.exceptions;

public class AuthenticationException extends Exception {

    public AuthenticationException() {}

    public AuthenticationException(String s) {
        super(s);
    }

    public AuthenticationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AuthenticationException(Throwable throwable) {
        super(throwable);
    }
}
