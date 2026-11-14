package com.mojang.authlib;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Map;

public interface UserAuthentication {

    boolean canLogIn();

    void logIn() throws AuthenticationException;

    void logOut();

    boolean isLoggedIn();

    boolean canPlayOnline();

    GameProfile[] getAvailableProfiles();

    GameProfile getSelectedProfile();

    void selectGameProfile(GameProfile gameprofile) throws AuthenticationException;

    void loadFromStorage(Map<String, Object> map);

    Map<String, Object> saveForStorage();

    void setUsername(String s);

    void setPassword(String s);

    String getAuthenticatedToken();

    String getUserID();

    PropertyMap getUserProperties();

    UserType getUserType();
}
