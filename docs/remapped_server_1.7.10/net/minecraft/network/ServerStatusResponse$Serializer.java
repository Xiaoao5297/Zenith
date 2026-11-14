package net.minecraft.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse$Serializer implements JsonDeserializer, JsonSerializer {

    public ServerStatusResponse$Serializer() {}

    @Override
    public ServerStatusResponse deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) {
        JsonObject jsonobject = JsonUtils.func_151210_l(jsonelement, "status");
        ServerStatusResponse serverstatusresponse = new ServerStatusResponse();

        if (jsonobject.has("description")) {
            serverstatusresponse.func_151315_a((IChatComponent) jsondeserializationcontext.deserialize(jsonobject.get("description"), IChatComponent.class));
        }

        if (jsonobject.has("players")) {
            serverstatusresponse.func_151319_a((ServerStatusResponse$PlayerCountData) jsondeserializationcontext.deserialize(jsonobject.get("players"), ServerStatusResponse$PlayerCountData.class));
        }

        if (jsonobject.has("version")) {
            serverstatusresponse.func_151321_a((ServerStatusResponse$MinecraftProtocolVersionIdentifier) jsondeserializationcontext.deserialize(jsonobject.get("version"), ServerStatusResponse$MinecraftProtocolVersionIdentifier.class));
        }

        if (jsonobject.has("favicon")) {
            serverstatusresponse.func_151320_a(JsonUtils.func_151200_h(jsonobject, "favicon"));
        }

        return serverstatusresponse;
    }

    public JsonElement serialize(ServerStatusResponse serverstatusresponse, Type type, JsonSerializationContext jsonserializationcontext) {
        JsonObject jsonobject = new JsonObject();

        if (serverstatusresponse.func_151317_a() != null) {
            jsonobject.add("description", jsonserializationcontext.serialize(serverstatusresponse.func_151317_a()));
        }

        if (serverstatusresponse.func_151318_b() != null) {
            jsonobject.add("players", jsonserializationcontext.serialize(serverstatusresponse.func_151318_b()));
        }

        if (serverstatusresponse.func_151322_c() != null) {
            jsonobject.add("version", jsonserializationcontext.serialize(serverstatusresponse.func_151322_c()));
        }

        if (serverstatusresponse.func_151316_d() != null) {
            jsonobject.addProperty("favicon", serverstatusresponse.func_151316_d());
        }

        return jsonobject;
    }
}
