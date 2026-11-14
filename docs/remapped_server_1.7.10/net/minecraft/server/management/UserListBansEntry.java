package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class UserListBansEntry extends BanEntry {

    public UserListBansEntry(GameProfile gameprofile) {
        this(gameprofile, (Date) null, (String) null, (Date) null, (String) null);
    }

    public UserListBansEntry(GameProfile gameprofile, Date date, String s, Date date1, String s1) {
        super(gameprofile, date1, s, date1, s1);
    }

    public UserListBansEntry(JsonObject jsonobject) {
        super(func_152648_b(jsonobject), jsonobject);
    }

    @Override
    protected void func_152641_a(JsonObject jsonobject) {
        if (this.func_152640_f() != null) {
            jsonobject.addProperty("uuid", ((GameProfile) this.func_152640_f()).getId() == null ? "" : ((GameProfile) this.func_152640_f()).getId().toString());
            jsonobject.addProperty("name", ((GameProfile) this.func_152640_f()).getName());
            super.func_152641_a(jsonobject);
        }
    }

    private static GameProfile func_152648_b(JsonObject jsonobject) {
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
