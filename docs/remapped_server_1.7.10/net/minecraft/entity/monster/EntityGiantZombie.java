package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityGiantZombie extends EntityMob {

    public EntityGiantZombie(World world) {
        super(world);
        this.field_70129_M *= 6.0F;
        this.func_70105_a(this.field_70130_N * 6.0F, this.field_70131_O * 6.0F);
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(100.0D);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5D);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(50.0D);
    }

    @Override
    public float func_70783_a(int i, int j, int k) {
        return this.field_70170_p.func_72801_o(i, j, k) - 0.5F;
    }
}
