package com.mojang.authlib.yggdrasil;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.authlib.yggdrasil.response.HasJoinedMinecraftServerResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilMinecraftSessionService extends HttpMinecraftSessionService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/";
    private static final URL JOIN_URL = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/join");
    private static final URL CHECK_URL = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/hasJoined");
    private final PublicKey publicKey;
    private final Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    protected YggdrasilMinecraftSessionService(YggdrasilAuthenticationService yggdrasilauthenticationservice) {
        super((HttpAuthenticationService) yggdrasilauthenticationservice);

        try {
            X509EncodedKeySpec x509encodedkeyspec = new X509EncodedKeySpec(IOUtils.toByteArray(YggdrasilMinecraftSessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der")));
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");

            this.publicKey = keyfactory.generatePublic(x509encodedkeyspec);
        } catch (Exception exception) {
            throw new Error("Missing/invalid yggdrasil public key!");
        }
    }

    @Override
    public void joinServer(GameProfile gameprofile, String s, String s1) throws AuthenticationException {
        JoinMinecraftServerRequest joinminecraftserverrequest = new JoinMinecraftServerRequest();

        joinminecraftserverrequest.accessToken = s;
        joinminecraftserverrequest.selectedProfile = gameprofile.getId();
        joinminecraftserverrequest.serverId = s1;
        this.getAuthenticationService().makeRequest(YggdrasilMinecraftSessionService.JOIN_URL, joinminecraftserverrequest, Response.class);
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile gameprofile, String s) throws AuthenticationUnavailableException {
        HashMap hashmap = new HashMap();

        hashmap.put("username", gameprofile.getName());
        hashmap.put("serverId", s);
        URL url = HttpAuthenticationService.concatenateURL(YggdrasilMinecraftSessionService.CHECK_URL, HttpAuthenticationService.buildQuery(hashmap));

        try {
            HasJoinedMinecraftServerResponse hasjoinedminecraftserverresponse = (HasJoinedMinecraftServerResponse) this.getAuthenticationService().makeRequest(url, (Object) null, HasJoinedMinecraftServerResponse.class);

            if (hasjoinedminecraftserverresponse != null && hasjoinedminecraftserverresponse.getId() != null) {
                GameProfile gameprofile1 = new GameProfile(hasjoinedminecraftserverresponse.getId(), gameprofile.getName());

                if (hasjoinedminecraftserverresponse.getProperties() != null) {
                    gameprofile1.getProperties().putAll(hasjoinedminecraftserverresponse.getProperties());
                }

                return gameprofile1;
            } else {
                return null;
            }
        } catch (AuthenticationUnavailableException authenticationunavailableexception) {
            throw authenticationunavailableexception;
        } catch (AuthenticationException authenticationexception) {
            return null;
        }
    }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameprofile, boolean flag) {
        Property property = (Property) Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object) null);

        if (property == null) {
            return new HashMap();
        } else {
            if (flag) {
                if (!property.hasSignature()) {
                    YggdrasilMinecraftSessionService.LOGGER.error("Signature is missing from textures payload");
                    throw new InsecureTextureException("Signature is missing from textures payload");
                }

                if (!property.isSignatureValid(this.publicKey)) {
                    YggdrasilMinecraftSessionService.LOGGER.error("Textures payload has been tampered with (signature invalid)");
                    throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
                }
            }

            MinecraftTexturesPayload minecrafttexturespayload;

            try {
                String s = new String(Base64.decodeBase64(property.getValue()), Charsets.UTF_8);

                minecrafttexturespayload = (MinecraftTexturesPayload) this.gson.fromJson(s, MinecraftTexturesPayload.class);
            } catch (JsonParseException jsonparseexception) {
                YggdrasilMinecraftSessionService.LOGGER.error("Could not decode textures payload", (Throwable) jsonparseexception);
                return new HashMap();
            }

            return (Map) (minecrafttexturespayload.getTextures() == null ? new HashMap() : minecrafttexturespayload.getTextures());
        }
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile gameprofile, boolean flag) {
        if (gameprofile.getId() == null) {
            return gameprofile;
        } else {
            try {
                URL url = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(gameprofile.getId()));

                url = HttpAuthenticationService.concatenateURL(url, "unsigned=" + !flag);
                MinecraftProfilePropertiesResponse minecraftprofilepropertiesresponse = (MinecraftProfilePropertiesResponse) this.getAuthenticationService().makeRequest(url, (Object) null, MinecraftProfilePropertiesResponse.class);

                if (minecraftprofilepropertiesresponse == null) {
                    YggdrasilMinecraftSessionService.LOGGER.debug("Couldn't fetch profile properties for " + gameprofile + " as the profile does not exist");
                    return gameprofile;
                } else {
                    GameProfile gameprofile1 = new GameProfile(minecraftprofilepropertiesresponse.getId(), minecraftprofilepropertiesresponse.getName());

                    gameprofile1.getProperties().putAll(minecraftprofilepropertiesresponse.getProperties());
                    gameprofile.getProperties().putAll(minecraftprofilepropertiesresponse.getProperties());
                    YggdrasilMinecraftSessionService.LOGGER.debug("Successfully fetched profile properties for " + gameprofile);
                    return gameprofile1;
                }
            } catch (AuthenticationException authenticationexception) {
                YggdrasilMinecraftSessionService.LOGGER.warn("Couldn't look up profile properties for " + gameprofile, (Throwable) authenticationexception);
                return gameprofile;
            }
        }
    }

    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService) super.getAuthenticationService();
    }
}
