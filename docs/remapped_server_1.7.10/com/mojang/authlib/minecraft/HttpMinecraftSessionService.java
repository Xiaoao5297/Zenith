package com.mojang.authlib.minecraft;

import com.mojang.authlib.HttpAuthenticationService;

public abstract class HttpMinecraftSessionService extends BaseMinecraftSessionService {

    protected HttpMinecraftSessionService(HttpAuthenticationService httpauthenticationservice) {
        super(httpauthenticationservice);
    }

    @Override
    public HttpAuthenticationService getAuthenticationService() {
        return (HttpAuthenticationService) super.getAuthenticationService();
    }
}
