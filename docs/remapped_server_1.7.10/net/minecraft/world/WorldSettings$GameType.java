package net.minecraft.world;

import net.minecraft.entity.player.PlayerCapabilities;

public enum WorldSettings$GameType {

    NOT_SET(-1, ""), SURVIVAL(0, "survival"), CREATIVE(1, "creative"), ADVENTURE(2, "adventure");

    int field_77154_e;
    String field_77151_f;

    private WorldSettings$GameType(int i, String s) {
        this.field_77154_e = i;
        this.field_77151_f = s;
    }

    public int func_77148_a() {
        return this.field_77154_e;
    }

    public String func_77149_b() {
        return this.field_77151_f;
    }

    public void func_77147_a(PlayerCapabilities playercapabilities) {
        if (this == WorldSettings$GameType.CREATIVE) {
            playercapabilities.field_75101_c = true;
            playercapabilities.field_75098_d = true;
            playercapabilities.field_75102_a = true;
        } else {
            playercapabilities.field_75101_c = false;
            playercapabilities.field_75098_d = false;
            playercapabilities.field_75102_a = false;
            playercapabilities.field_75100_b = false;
        }

        playercapabilities.field_75099_e = !this.func_82752_c();
    }

    public boolean func_82752_c() {
        return this == WorldSettings$GameType.ADVENTURE;
    }

    public boolean func_77145_d() {
        return this == WorldSettings$GameType.CREATIVE;
    }

    public static WorldSettings$GameType func_77146_a(int i) {
        WorldSettings$GameType[] aworldsettings$gametype = values();
        int j = aworldsettings$gametype.length;

        for (int k = 0; k < j; ++k) {
            WorldSettings$GameType worldsettings$gametype = aworldsettings$gametype[k];

            if (worldsettings$gametype.field_77154_e == i) {
                return worldsettings$gametype;
            }
        }

        return WorldSettings$GameType.SURVIVAL;
    }
}
