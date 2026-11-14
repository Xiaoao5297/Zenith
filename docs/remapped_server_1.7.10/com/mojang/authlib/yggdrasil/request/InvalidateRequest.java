package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class InvalidateRequest {

    private String accessToken;
    private String clientToken;

    public InvalidateRequest(YggdrasilUserAuthentication yggdrasiluserauthentication) {
        this.accessToken = yggdrasiluserauthentication.getAuthenticatedToken();
        this.clientToken = yggdrasiluserauthentication.getAuthenticationService().getClientToken();
    }
}
