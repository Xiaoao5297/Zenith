package net.minecraft.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse$MinecraftProtocolVersionIdentifier$Serializer implements JsonDeserializer, JsonSerializer {

    public ServerStatusResponse$MinecraftProtocolVersionIdentifier$Serializer() {}

    @Override
    public ServerStatusResponse$MinecraftProtocolVersionIdentifier deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) {
        JsonObject jsonobject = JsonUtils.func_151210_l(jsonelement, "version");

        return new ServerStatusResponse$MinecraftProtocolVersionIdentifier(JsonUtils.func_151200_h(jsonobject, "name"), JsonUtils.func_151203_m(jsonobject, "protocol"));
    }

    public JsonElement serialize(ServerStatusResponse$MinecraftProtocolVersionIdentifier serverstatusresponse$minecraftprotocolversionidentifier, Type type, JsonSerializationContext jsonserializationcontext) {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("name", serverstatusresponse$minecraftprotocolversionidentifier.func_151303_a());
        jsonobject.addProperty("protocol", (Number) serverstatusresponse$minecraftprotocolversionidentifier.func_151304_b());
        return jsonobject;
    }
}
