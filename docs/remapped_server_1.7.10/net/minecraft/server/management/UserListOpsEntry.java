package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListOpsEntry extends UserListEntry {

    private final int field_152645_a;

    public UserListOpsEntry(GameProfile gameprofile, int i) {
        super(gameprofile);
        this.field_152645_a = i;
    }

    public UserListOpsEntry(JsonObject jsonobject) {
        super(func_152643_b(jsonobject), jsonobject);
        this.field_152645_a = jsonobject.has("level") ? jsonobject.get("level").getAsInt() : 0;
    }

    public int func_152644_a() {
        return this.field_152645_a;
    }

    @Override
    protected void func_152641_a(JsonObject jsonobject) {
        if (this.func_152640_f() != null) {
            jsonobject.addProperty("uuid", ((GameProfile) this.func_152640_f()).getId() == null ? "" : ((GameProfile) this.func_152640_f()).getId().toString());
            jsonobject.addProperty("name", ((GameProfile) this.func_152640_f()).getName());
            super.func_152641_a(jsonobject);
            jsonobject.addProperty("level", (Number) this.field_152645_a);
        }
    }

    private static GameProfile func_152643_b(JsonObject jsonobject) {
        if (jsonobject.has("uuid") && jsonobject.has("name")) {
            String s = jsonobject.get("uuid").getAsString();

            UUID uuid;

            try {
                uuid = UUID.fromString(s);
            } catch (Throwable throwable) {
                return null;
            }

            return new GameProfile(uuid, jsonobject.get("name").getAsString());
        } else {
            return null;
        }
    }
}
