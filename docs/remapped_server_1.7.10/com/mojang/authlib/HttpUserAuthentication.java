package com.mojang.authlib;

public abstract class HttpUserAuthentication extends BaseUserAuthentication {

    protected HttpUserAuthentication(HttpAuthenticationService httpauthenticationservice) {
        super(httpauthenticationservice);
    }

    @Override
    public HttpAuthenticationService getAuthenticationService() {
        return (HttpAuthenticationService) super.getAuthenticationService();
    }
}
