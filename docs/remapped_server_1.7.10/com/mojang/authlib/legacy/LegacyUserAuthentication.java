package com.mojang.authlib.legacy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.HttpUserAuthentication;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.util.UUIDTypeAdapter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class LegacyUserAuthentication extends HttpUserAuthentication {

    private static final URL AUTHENTICATION_URL = HttpAuthenticationService.constantURL("https://login.minecraft.net");
    private static final int AUTHENTICATION_VERSION = 14;
    private static final int RESPONSE_PART_PROFILE_NAME = 2;
    private static final int RESPONSE_PART_SESSION_TOKEN = 3;
    private static final int RESPONSE_PART_PROFILE_ID = 4;
    private String sessionToken;

    protected LegacyUserAuthentication(LegacyAuthenticationService legacyauthenticationservice) {
        super((HttpAuthenticationService) legacyauthenticationservice);
    }

    @Override
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(this.getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        } else if (StringUtils.isBlank(this.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        } else {
            HashMap hashmap = new HashMap();

            hashmap.put("user", this.getUsername());
            hashmap.put("password", this.getPassword());
            hashmap.put("version", 14);

            String s;

            try {
                s = this.getAuthenticationService().performPostRequest(LegacyUserAuthentication.AUTHENTICATION_URL, HttpAuthenticationService.buildQuery(hashmap), "application/x-www-form-urlencoded").trim();
            } catch (IOException ioexception) {
                throw new AuthenticationException("Authentication server is not responding", ioexception);
            }

            String[] astring = s.split(":");

            if (astring.length == 5) {
                String s1 = astring[4];
                String s2 = astring[2];
                String s3 = astring[3];

                if (!StringUtils.isBlank(s1) && !StringUtils.isBlank(s2) && !StringUtils.isBlank(s3)) {
                    this.setSelectedProfile(new GameProfile(UUIDTypeAdapter.fromString(s1), s2));
                    this.sessionToken = s3;
                    this.setUserType(UserType.LEGACY);
                } else {
                    throw new AuthenticationException("Unknown response from authentication server: " + s);
                }
            } else {
                throw new InvalidCredentialsException(s);
            }
        }
    }

    @Override
    public void logOut() {
        super.logOut();
        this.sessionToken = null;
    }

    @Override
    public boolean canPlayOnline() {
        return this.isLoggedIn() && this.getSelectedProfile() != null && this.getAuthenticatedToken() != null;
    }

    @Override
    public GameProfile[] getAvailableProfiles() {
        return this.getSelectedProfile() != null ? new GameProfile[]{this.getSelectedProfile()} : new GameProfile[0];
    }

    @Override
    public void selectGameProfile(GameProfile gameprofile) throws AuthenticationException {
        throw new UnsupportedOperationException("Game profiles cannot be changed in the legacy authentication service");
    }

    @Override
    public String getAuthenticatedToken() {
        return this.sessionToken;
    }

    @Override
    public String getUserID() {
        return this.getUsername();
    }

    @Override
    public LegacyAuthenticationService getAuthenticationService() {
        return (LegacyAuthenticationService) super.getAuthenticationService();
    }
}
