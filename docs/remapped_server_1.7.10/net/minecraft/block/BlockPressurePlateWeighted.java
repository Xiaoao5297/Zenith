package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {

    private final int field_150068_a;

    protected BlockPressurePlateWeighted(String s, Material material, int i) {
        super(s, material);
        this.field_150068_a = i;
    }

    @Override
    protected int func_150065_e(World world, int i, int j, int k) {
        int l = Math.min(world.func_72872_a(Entity.class, this.func_150061_a(i, j, k)).size(), this.field_150068_a);

        if (l <= 0) {
            return 0;
        } else {
            float f = (float) Math.min(this.field_150068_a, l) / (float) this.field_150068_a;

            return MathHelper.func_76123_f(f * 15.0F);
        }
    }

    @Override
    protected int func_150060_c(int i) {
        return i;
    }

    @Override
    protected int func_150066_d(int i) {
        return i;
    }

    @Override
    public int func_149738_a(World world) {
        return 10;
    }
}
