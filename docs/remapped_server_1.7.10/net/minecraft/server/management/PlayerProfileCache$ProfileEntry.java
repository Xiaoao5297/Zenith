package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import java.util.Date;

class PlayerProfileCache$ProfileEntry {

    private final GameProfile field_152672_b;
    private final Date field_152673_c;

    private PlayerProfileCache$ProfileEntry(PlayerProfileCache playerprofilecache, GameProfile gameprofile, Date date) {
        this.field_152671_a = playerprofilecache;
        this.field_152672_b = gameprofile;
        this.field_152673_c = date;
    }

    public GameProfile func_152668_a() {
        return this.field_152672_b;
    }

    public Date func_152670_b() {
        return this.field_152673_c;
    }
}
