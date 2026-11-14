package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class AuthenticationRequest {

    private Agent agent;
    private String username;
    private String password;
    private String clientToken;
    private boolean requestUser = true;

    public AuthenticationRequest(YggdrasilUserAuthentication yggdrasiluserauthentication, String s, String s1) {
        this.agent = yggdrasiluserauthentication.getAgent();
        this.username = s;
        this.clientToken = yggdrasiluserauthentication.getAuthenticationService().getClientToken();
        this.password = s1;
    }
}
