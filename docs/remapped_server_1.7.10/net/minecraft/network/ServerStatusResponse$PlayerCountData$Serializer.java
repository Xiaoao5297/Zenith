package net.minecraft.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse$PlayerCountData$Serializer implements JsonDeserializer, JsonSerializer {

    public ServerStatusResponse$PlayerCountData$Serializer() {}

    @Override
    public ServerStatusResponse$PlayerCountData deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) {
        JsonObject jsonobject = JsonUtils.func_151210_l(jsonelement, "players");
        ServerStatusResponse$PlayerCountData serverstatusresponse$playercountdata = new ServerStatusResponse$PlayerCountData(JsonUtils.func_151203_m(jsonobject, "max"), JsonUtils.func_151203_m(jsonobject, "online"));

        if (JsonUtils.func_151202_d(jsonobject, "sample")) {
            JsonArray jsonarray = JsonUtils.func_151214_t(jsonobject, "sample");

            if (jsonarray.size() > 0) {
                GameProfile[] agameprofile = new GameProfile[jsonarray.size()];

                for (int i = 0; i < agameprofile.length; ++i) {
                    JsonObject jsonobject1 = JsonUtils.func_151210_l(jsonarray.get(i), "player[" + i + "]");
                    String s = JsonUtils.func_151200_h(jsonobject1, "id");

                    agameprofile[i] = new GameProfile(UUID.fromString(s), JsonUtils.func_151200_h(jsonobject1, "name"));
                }

                serverstatusresponse$playercountdata.func_151330_a(agameprofile);
            }
        }

        return serverstatusresponse$playercountdata;
    }

    public JsonElement serialize(ServerStatusResponse$PlayerCountData serverstatusresponse$playercountdata, Type type, JsonSerializationContext jsonserializationcontext) {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("max", (Number) serverstatusresponse$playercountdata.func_151332_a());
        jsonobject.addProperty("online", (Number) serverstatusresponse$playercountdata.func_151333_b());
        if (serverstatusresponse$playercountdata.func_151331_c() != null && serverstatusresponse$playercountdata.func_151331_c().length > 0) {
            JsonArray jsonarray = new JsonArray();

            for (int i = 0; i < serverstatusresponse$playercountdata.func_151331_c().length; ++i) {
                JsonObject jsonobject1 = new JsonObject();
                UUID uuid = serverstatusresponse$playercountdata.func_151331_c()[i].getId();

                jsonobject1.addProperty("id", uuid == null ? "" : uuid.toString());
                jsonobject1.addProperty("name", serverstatusresponse$playercountdata.func_151331_c()[i].getName());
                jsonarray.add(jsonobject1);
            }

            jsonobject.add("sample", jsonarray);
        }

        return jsonobject;
    }
}
