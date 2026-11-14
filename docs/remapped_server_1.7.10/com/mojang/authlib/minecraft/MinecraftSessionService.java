package com.mojang.authlib.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.util.Map;

public interface MinecraftSessionService {

    void joinServer(GameProfile gameprofile, String s, String s1) throws AuthenticationException;

    GameProfile hasJoinedServer(GameProfile gameprofile, String s) throws AuthenticationUnavailableException;

    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameprofile, boolean flag);

    GameProfile fillProfileProperties(GameProfile gameprofile, boolean flag);
}
