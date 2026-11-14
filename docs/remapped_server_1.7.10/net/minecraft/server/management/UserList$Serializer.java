package net.minecraft.server.management;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

class UserList$Serializer implements JsonDeserializer, JsonSerializer {

    private UserList$Serializer(UserList userlist) {
        this.field_152752_a = userlist;
    }

    public JsonElement func_152751_a(UserListEntry userlistentry, Type type, JsonSerializationContext jsonserializationcontext) {
        JsonObject jsonobject = new JsonObject();

        userlistentry.func_152641_a(jsonobject);
        return jsonobject;
    }

    public UserListEntry func_152750_a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) {
        if (jsonelement.isJsonObject()) {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            UserListEntry userlistentry = this.field_152752_a.func_152682_a(jsonobject);

            return userlistentry;
        } else {
            return null;
        }
    }
}
