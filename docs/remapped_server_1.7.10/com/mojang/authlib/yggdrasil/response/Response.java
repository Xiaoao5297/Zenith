package com.mojang.authlib.yggdrasil.response;

public class Response {

    private String error;
    private String errorMessage;
    private String cause;

    public Response() {}

    public String getError() {
        return this.error;
    }

    public String getCause() {
        return this.cause;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    protected void setError(String s) {
        this.error = s;
    }

    protected void setErrorMessage(String s) {
        this.errorMessage = s;
    }

    protected void setCause(String s) {
        this.cause = s;
    }
}
