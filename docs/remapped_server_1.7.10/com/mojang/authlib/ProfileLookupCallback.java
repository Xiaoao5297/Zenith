package com.mojang.authlib;

public interface ProfileLookupCallback {

    void onProfileLookupSucceeded(GameProfile gameprofile);

    void onProfileLookupFailed(GameProfile gameprofile, Exception exception);
}
