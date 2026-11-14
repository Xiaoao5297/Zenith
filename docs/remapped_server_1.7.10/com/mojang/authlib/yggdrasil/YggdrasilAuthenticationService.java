package com.mojang.authlib.yggdrasil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserMigratedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class YggdrasilAuthenticationService extends HttpAuthenticationService {

    private final String clientToken;
    private final Gson gson;

    public YggdrasilAuthenticationService(Proxy proxy, String s) {
        super(proxy);
        this.clientToken = s;
        GsonBuilder gsonbuilder = new GsonBuilder();

        gsonbuilder.registerTypeAdapter(GameProfile.class, new YggdrasilAuthenticationService.GameProfileSerializer());
        gsonbuilder.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
        gsonbuilder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
        gsonbuilder.registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer());
        this.gson = gsonbuilder.create();
    }

    @Override
    public UserAuthentication createUserAuthentication(Agent agent) {
        return new YggdrasilUserAuthentication(this, agent);
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return new YggdrasilMinecraftSessionService(this);
    }

    @Override
    public GameProfileRepository createProfileRepository() {
        return new YggdrasilGameProfileRepository(this);
    }

    protected <T extends Response> T makeRequest(URL url, Object object, Class<T> oclass) throws AuthenticationException {
        try {
            String s = object == null ? this.performGetRequest(url) : this.performPostRequest(url, this.gson.toJson(object), "application/json");
            Response response = (Response) this.gson.fromJson(s, oclass);

            if (response == null) {
                return null;
            } else if (StringUtils.isNotBlank(response.getError())) {
                if ("UserMigratedException".equals(response.getCause())) {
                    throw new UserMigratedException(response.getErrorMessage());
                } else if (response.getError().equals("ForbiddenOperationException")) {
                    throw new InvalidCredentialsException(response.getErrorMessage());
                } else {
                    throw new AuthenticationException(response.getErrorMessage());
                }
            } else {
                return response;
            }
        } catch (IOException ioexception) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server", ioexception);
        } catch (IllegalStateException illegalstateexception) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server", illegalstateexception);
        } catch (JsonParseException jsonparseexception) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server", jsonparseexception);
        }
    }

    public String getClientToken() {
        return this.clientToken;
    }

    private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

        private GameProfileSerializer() {}

        @Override
        public GameProfile deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = (JsonObject) jsonelement;
            UUID uuid = jsonobject.has("id") ? (UUID) jsondeserializationcontext.deserialize(jsonobject.get("id"), UUID.class) : null;
            String s = jsonobject.has("name") ? jsonobject.getAsJsonPrimitive("name").getAsString() : null;

            return new GameProfile(uuid, s);
        }

        public JsonElement serialize(GameProfile gameprofile, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            if (gameprofile.getId() != null) {
                jsonobject.add("id", jsonserializationcontext.serialize(gameprofile.getId()));
            }

            if (gameprofile.getName() != null) {
                jsonobject.addProperty("name", gameprofile.getName());
            }

            return jsonobject;
        }
    }
}
