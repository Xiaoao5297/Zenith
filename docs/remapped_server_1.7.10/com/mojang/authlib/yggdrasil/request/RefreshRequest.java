package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class RefreshRequest {

    private String clientToken;
    private String accessToken;
    private GameProfile selectedProfile;
    private boolean requestUser;

    public RefreshRequest(YggdrasilUserAuthentication yggdrasiluserauthentication) {
        this(yggdrasiluserauthentication, (GameProfile) null);
    }

    public RefreshRequest(YggdrasilUserAuthentication yggdrasiluserauthentication, GameProfile gameprofile) {
        this.requestUser = true;
        this.clientToken = yggdrasiluserauthentication.getAuthenticationService().getClientToken();
        this.accessToken = yggdrasiluserauthentication.getAuthenticatedToken();
        this.selectedProfile = gameprofile;
    }
}
