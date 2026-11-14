package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMagmaCube extends EntitySlime {

    public EntityMagmaCube(World world) {
        super(world);
        this.field_70178_ae = true;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.20000000298023224D);
    }

    @Override
    public boolean func_70601_bi() {
        return this.field_70170_p.field_73013_u != EnumDifficulty.PEACEFUL && this.field_70170_p.func_72855_b(this.field_70121_D) && this.field_70170_p.func_72945_a(this, this.field_70121_D).isEmpty() && !this.field_70170_p.func_72953_d(this.field_70121_D);
    }

    @Override
    public int func_70658_aO() {
        return this.func_70809_q() * 3;
    }

    @Override
    public float func_70013_c(float f) {
        return 1.0F;
    }

    @Override
    protected String func_70801_i() {
        return "flame";
    }

    @Override
    protected EntitySlime func_70802_j() {
        return new EntityMagmaCube(this.field_70170_p);
    }

    @Override
    protected Item func_146068_u() {
        return Items.field_151064_bs;
    }

    @Override
    protected void func_70628_a(boolean flag, int i) {
        Item item = this.func_146068_u();

        if (item != null && this.func_70809_q() > 1) {
            int j = this.field_70146_Z.nextInt(4) - 2;

            if (i > 0) {
                j += this.field_70146_Z.nextInt(i + 1);
            }

            for (int k = 0; k < j; ++k) {
                this.func_145779_a(item, 1);
            }
        }

    }

    @Override
    public boolean func_70027_ad() {
        return false;
    }

    @Override
    protected int func_70806_k() {
        return super.func_70806_k() * 4;
    }

    @Override
    protected void func_70808_l() {
        this.field_70813_a *= 0.9F;
    }

    @Override
    protected void func_70664_aZ() {
        this.field_70181_x = (double) (0.42F + (float) this.func_70809_q() * 0.1F);
        this.field_70160_al = true;
    }

    @Override
    protected void func_70069_a(float f) {}

    @Override
    protected boolean func_70800_m() {
        return true;
    }

    @Override
    protected int func_70805_n() {
        return super.func_70805_n() + 2;
    }

    @Override
    protected String func_70803_o() {
        return this.func_70809_q() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
    }

    @Override
    public boolean func_70058_J() {
        return false;
    }

    @Override
    protected boolean func_70804_p() {
        return true;
    }
}
