package com.mojang.authlib.exceptions;

public class AuthenticationUnavailableException extends AuthenticationException {

    public AuthenticationUnavailableException() {}

    public AuthenticationUnavailableException(String s) {
        super(s);
    }

    public AuthenticationUnavailableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AuthenticationUnavailableException(Throwable throwable) {
        super(throwable);
    }
}
