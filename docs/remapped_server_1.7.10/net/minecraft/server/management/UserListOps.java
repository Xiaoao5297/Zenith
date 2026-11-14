package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

public class UserListOps extends UserList {

    public UserListOps(File file) {
        super(file);
    }

    @Override
    protected UserListEntry func_152682_a(JsonObject jsonobject) {
        return new UserListOpsEntry(jsonobject);
    }

    @Override
    public String[] func_152685_a() {
        String[] astring = new String[this.func_152688_e().size()];
        int i = 0;

        UserListOpsEntry userlistopsentry;

        for (Iterator iterator = this.func_152688_e().values().iterator(); iterator.hasNext(); astring[i++] = ((GameProfile) userlistopsentry.func_152640_f()).getName()) {
            userlistopsentry = (UserListOpsEntry) iterator.next();
        }

        return astring;
    }

    protected String func_152699_b(GameProfile gameprofile) {
        return gameprofile.getId().toString();
    }

    public GameProfile func_152700_a(String s) {
        Iterator iterator = this.func_152688_e().values().iterator();

        UserListOpsEntry userlistopsentry;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            userlistopsentry = (UserListOpsEntry) iterator.next();
        } while (!s.equalsIgnoreCase(((GameProfile) userlistopsentry.func_152640_f()).getName()));

        return (GameProfile) userlistopsentry.func_152640_f();
    }
}
