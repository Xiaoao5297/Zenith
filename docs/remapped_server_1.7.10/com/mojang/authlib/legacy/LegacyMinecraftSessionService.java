package com.mojang.authlib.legacy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LegacyMinecraftSessionService extends HttpMinecraftSessionService {

    private static final String BASE_URL = "http://session.minecraft.net/game/";
    private static final URL JOIN_URL = HttpAuthenticationService.constantURL("http://session.minecraft.net/game/joinserver.jsp");
    private static final URL CHECK_URL = HttpAuthenticationService.constantURL("http://session.minecraft.net/game/checkserver.jsp");

    protected LegacyMinecraftSessionService(LegacyAuthenticationService legacyauthenticationservice) {
        super((HttpAuthenticationService) legacyauthenticationservice);
    }

    @Override
    public void joinServer(GameProfile gameprofile, String s, String s1) throws AuthenticationException {
        HashMap hashmap = new HashMap();

        hashmap.put("user", gameprofile.getName());
        hashmap.put("sessionId", s);
        hashmap.put("serverId", s1);
        URL url = HttpAuthenticationService.concatenateURL(LegacyMinecraftSessionService.JOIN_URL, HttpAuthenticationService.buildQuery(hashmap));

        try {
            String s2 = this.getAuthenticationService().performGetRequest(url);

            if (!s2.equals("OK")) {
                throw new AuthenticationException(s2);
            }
        } catch (IOException ioexception) {
            throw new AuthenticationUnavailableException(ioexception);
        }
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile gameprofile, String s) throws AuthenticationUnavailableException {
        HashMap hashmap = new HashMap();

        hashmap.put("user", gameprofile.getName());
        hashmap.put("serverId", s);
        URL url = HttpAuthenticationService.concatenateURL(LegacyMinecraftSessionService.CHECK_URL, HttpAuthenticationService.buildQuery(hashmap));

        try {
            String s1 = this.getAuthenticationService().performGetRequest(url);

            return s1.equals("YES") ? gameprofile : null;
        } catch (IOException ioexception) {
            throw new AuthenticationUnavailableException(ioexception);
        }
    }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameprofile, boolean flag) {
        return new HashMap();
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile gameprofile, boolean flag) {
        return gameprofile;
    }

    @Override
    public LegacyAuthenticationService getAuthenticationService() {
        return (LegacyAuthenticationService) super.getAuthenticationService();
    }
}
