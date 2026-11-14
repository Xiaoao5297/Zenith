package com.mojang.authlib.minecraft;

import com.mojang.authlib.AuthenticationService;

public abstract class BaseMinecraftSessionService implements MinecraftSessionService {

    private final AuthenticationService authenticationService;

    protected BaseMinecraftSessionService(AuthenticationService authenticationservice) {
        this.authenticationService = authenticationservice;
    }

    public AuthenticationService getAuthenticationService() {
        return this.authenticationService;
    }
}
