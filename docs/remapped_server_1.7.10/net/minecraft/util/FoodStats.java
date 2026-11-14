package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;

public class FoodStats {

    private int field_75127_a = 20;
    private float field_75125_b = 5.0F;
    private float field_75126_c;
    private int field_75123_d;
    private int field_75124_e = 20;

    public FoodStats() {}

    public void func_75122_a(int i, float f) {
        this.field_75127_a = Math.min(i + this.field_75127_a, 20);
        this.field_75125_b = Math.min(this.field_75125_b + (float) i * f * 2.0F, (float) this.field_75127_a);
    }

    public void func_151686_a(ItemFood itemfood, ItemStack itemstack) {
        this.func_75122_a(itemfood.func_150905_g(itemstack), itemfood.func_150906_h(itemstack));
    }

    public void func_75118_a(EntityPlayer entityplayer) {
        EnumDifficulty enumdifficulty = entityplayer.field_70170_p.field_73013_u;

        this.field_75124_e = this.field_75127_a;
        if (this.field_75126_c > 4.0F) {
            this.field_75126_c -= 4.0F;
            if (this.field_75125_b > 0.0F) {
                this.field_75125_b = Math.max(this.field_75125_b - 1.0F, 0.0F);
            } else if (enumdifficulty != EnumDifficulty.PEACEFUL) {
                this.field_75127_a = Math.max(this.field_75127_a - 1, 0);
            }
        }

        if (entityplayer.field_70170_p.func_82736_K().func_82766_b("naturalRegeneration") && this.field_75127_a >= 18 && entityplayer.func_70996_bM()) {
            ++this.field_75123_d;
            if (this.field_75123_d >= 80) {
                entityplayer.func_70691_i(1.0F);
                this.func_75113_a(3.0F);
                this.field_75123_d = 0;
            }
        } else if (this.field_75127_a <= 0) {
            ++this.field_75123_d;
            if (this.field_75123_d >= 80) {
                if (entityplayer.func_110143_aJ() > 10.0F || enumdifficulty == EnumDifficulty.HARD || entityplayer.func_110143_aJ() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL) {
                    entityplayer.func_70097_a(DamageSource.field_76366_f, 1.0F);
                }

                this.field_75123_d = 0;
            }
        } else {
            this.field_75123_d = 0;
        }

    }

    public void func_75112_a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.func_150297_b("foodLevel", 99)) {
            this.field_75127_a = nbttagcompound.func_74762_e("foodLevel");
            this.field_75123_d = nbttagcompound.func_74762_e("foodTickTimer");
            this.field_75125_b = nbttagcompound.func_74760_g("foodSaturationLevel");
            this.field_75126_c = nbttagcompound.func_74760_g("foodExhaustionLevel");
        }

    }

    public void func_75117_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74768_a("foodLevel", this.field_75127_a);
        nbttagcompound.func_74768_a("foodTickTimer", this.field_75123_d);
        nbttagcompound.func_74776_a("foodSaturationLevel", this.field_75125_b);
        nbttagcompound.func_74776_a("foodExhaustionLevel", this.field_75126_c);
    }

    public int func_75116_a() {
        return this.field_75127_a;
    }

    public boolean func_75121_c() {
        return this.field_75127_a < 20;
    }

    public void func_75113_a(float f) {
        this.field_75126_c = Math.min(this.field_75126_c + f, 40.0F);
    }

    public float func_75115_e() {
        return this.field_75125_b;
    }
}
