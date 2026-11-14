package net.minecraft.world;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {

    private TreeMap field_82771_a = new TreeMap();

    public GameRules() {
        this.func_82769_a("doFireTick", "true");
        this.func_82769_a("mobGriefing", "true");
        this.func_82769_a("keepInventory", "false");
        this.func_82769_a("doMobSpawning", "true");
        this.func_82769_a("doMobLoot", "true");
        this.func_82769_a("doTileDrops", "true");
        this.func_82769_a("commandBlockOutput", "true");
        this.func_82769_a("naturalRegeneration", "true");
        this.func_82769_a("doDaylightCycle", "true");
    }

    public void func_82769_a(String s, String s1) {
        this.field_82771_a.put(s, new GameRules$Value(s1));
    }

    public void func_82764_b(String s, String s1) {
        GameRules$Value gamerules$value = (GameRules$Value) this.field_82771_a.get(s);

        if (gamerules$value != null) {
            gamerules$value.func_82757_a(s1);
        } else {
            this.func_82769_a(s, s1);
        }

    }

    public String func_82767_a(String s) {
        GameRules$Value gamerules$value = (GameRules$Value) this.field_82771_a.get(s);

        return gamerules$value != null ? gamerules$value.func_82756_a() : "";
    }

    public boolean func_82766_b(String s) {
        GameRules$Value gamerules$value = (GameRules$Value) this.field_82771_a.get(s);

        return gamerules$value != null ? gamerules$value.func_82758_b() : false;
    }

    public NBTTagCompound func_82770_a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.field_82771_a.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            GameRules$Value gamerules$value = (GameRules$Value) this.field_82771_a.get(s);

            nbttagcompound.func_74778_a(s, gamerules$value.func_82756_a());
        }

        return nbttagcompound;
    }

    public void func_82768_a(NBTTagCompound nbttagcompound) {
        Set set = nbttagcompound.func_150296_c();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            String s1 = nbttagcompound.func_74779_i(s);

            this.func_82764_b(s, s1);
        }

    }

    public String[] func_82763_b() {
        return (String[]) this.field_82771_a.keySet().toArray(new String[0]);
    }

    public boolean func_82765_e(String s) {
        return this.field_82771_a.containsKey(s);
    }
}
