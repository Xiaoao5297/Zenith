package net.minecraft.server.management;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

class PlayerProfileCache$Serializer implements JsonDeserializer, JsonSerializer {

    private PlayerProfileCache$Serializer(PlayerProfileCache playerprofilecache) {
        this.field_152677_a = playerprofilecache;
    }

    public JsonElement func_152676_a(PlayerProfileCache$ProfileEntry playerprofilecache$profileentry, Type type, JsonSerializationContext jsonserializationcontext) {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("name", playerprofilecache$profileentry.func_152668_a().getName());
        UUID uuid = playerprofilecache$profileentry.func_152668_a().getId();

        jsonobject.addProperty("uuid", uuid == null ? "" : uuid.toString());
        jsonobject.addProperty("expiresOn", PlayerProfileCache.field_152659_a.format(playerprofilecache$profileentry.func_152670_b()));
        return jsonobject;
    }

    public PlayerProfileCache$ProfileEntry func_152675_a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) {
        if (jsonelement.isJsonObject()) {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            JsonElement jsonelement1 = jsonobject.get("name");
            JsonElement jsonelement2 = jsonobject.get("uuid");
            JsonElement jsonelement3 = jsonobject.get("expiresOn");

            if (jsonelement1 != null && jsonelement2 != null) {
                String s = jsonelement2.getAsString();
                String s1 = jsonelement1.getAsString();
                Date date = null;

                if (jsonelement3 != null) {
                    try {
                        date = PlayerProfileCache.field_152659_a.parse(jsonelement3.getAsString());
                    } catch (ParseException parseexception) {
                        date = null;
                    }
                }

                if (s1 != null && s != null) {
                    UUID uuid;

                    try {
                        uuid = UUID.fromString(s);
                    } catch (Throwable throwable) {
                        return null;
                    }

                    PlayerProfileCache$ProfileEntry playerprofilecache$profileentry = new PlayerProfileCache$ProfileEntry(this.field_152677_a, new GameProfile(uuid, s1), date, (PlayerProfileCache$1) null);

                    return playerprofilecache$profileentry;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
