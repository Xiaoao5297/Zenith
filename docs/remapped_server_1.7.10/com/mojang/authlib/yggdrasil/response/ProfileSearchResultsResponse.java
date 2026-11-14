package com.mojang.authlib.yggdrasil.response;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;

public class ProfileSearchResultsResponse extends Response {

    private GameProfile[] profiles;

    public ProfileSearchResultsResponse() {}

    public GameProfile[] getProfiles() {
        return this.profiles;
    }

    public static class Serializer implements JsonDeserializer<ProfileSearchResultsResponse> {

        public Serializer() {}

        @Override
        public ProfileSearchResultsResponse deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            ProfileSearchResultsResponse profilesearchresultsresponse = new ProfileSearchResultsResponse();

            if (jsonelement instanceof JsonObject) {
                JsonObject jsonobject = (JsonObject) jsonelement;

                if (jsonobject.has("error")) {
                    profilesearchresultsresponse.setError(jsonobject.getAsJsonPrimitive("error").getAsString());
                }

                if (jsonobject.has("errorMessage")) {
                    profilesearchresultsresponse.setError(jsonobject.getAsJsonPrimitive("errorMessage").getAsString());
                }

                if (jsonobject.has("cause")) {
                    profilesearchresultsresponse.setError(jsonobject.getAsJsonPrimitive("cause").getAsString());
                }
            } else {
                profilesearchresultsresponse.profiles = (GameProfile[]) jsondeserializationcontext.deserialize(jsonelement, GameProfile[].class);
            }

            return profilesearchresultsresponse;
        }
    }
}
