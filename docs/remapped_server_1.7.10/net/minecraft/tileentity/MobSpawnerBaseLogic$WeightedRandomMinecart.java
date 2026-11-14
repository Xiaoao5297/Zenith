package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom$Item;

public class MobSpawnerBaseLogic$WeightedRandomMinecart extends WeightedRandom$Item {

    public final NBTTagCompound field_98222_b;
    public final String field_98223_c;

    public MobSpawnerBaseLogic$WeightedRandomMinecart(MobSpawnerBaseLogic mobspawnerbaselogic, NBTTagCompound nbttagcompound) {
        super(nbttagcompound.func_74762_e("Weight"));
        this.field_98221_d = mobspawnerbaselogic;
        NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("Properties");
        String s = nbttagcompound.func_74779_i("Type");

        if (s.equals("Minecart")) {
            if (nbttagcompound1 != null) {
                switch (nbttagcompound1.func_74762_e("Type")) {
                    case 0:
                        s = "MinecartRideable";
                        break;
                    case 1:
                        s = "MinecartChest";
                        break;
                    case 2:
                        s = "MinecartFurnace";
                }
            } else {
                s = "MinecartRideable";
            }
        }

        this.field_98222_b = nbttagcompound1;
        this.field_98223_c = s;
    }

    public MobSpawnerBaseLogic$WeightedRandomMinecart(MobSpawnerBaseLogic mobspawnerbaselogic, NBTTagCompound nbttagcompound, String s) {
        super(1);
        this.field_98221_d = mobspawnerbaselogic;
        if (s.equals("Minecart")) {
            if (nbttagcompound != null) {
                switch (nbttagcompound.func_74762_e("Type")) {
                    case 0:
                        s = "MinecartRideable";
                        break;
                    case 1:
                        s = "MinecartChest";
                        break;
                    case 2:
                        s = "MinecartFurnace";
                }
            } else {
                s = "MinecartRideable";
            }
        }

        this.field_98222_b = nbttagcompound;
        this.field_98223_c = s;
    }

    public NBTTagCompound func_98220_a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.func_74782_a("Properties", this.field_98222_b);
        nbttagcompound.func_74778_a("Type", this.field_98223_c);
        nbttagcompound.func_74768_a("Weight", this.field_76292_a);
        return nbttagcompound;
    }
}
