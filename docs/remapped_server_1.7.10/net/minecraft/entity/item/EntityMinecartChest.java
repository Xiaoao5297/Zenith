package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {

    public EntityMinecartChest(World world) {
        super(world);
    }

    public EntityMinecartChest(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public void func_94095_a(DamageSource damagesource) {
        super.func_94095_a(damagesource);
        this.func_145778_a(Item.func_150898_a(Blocks.field_150486_ae), 1, 0.0F);
    }

    @Override
    public int func_70302_i_() {
        return 27;
    }

    @Override
    public int func_94087_l() {
        return 1;
    }

    @Override
    public Block func_145817_o() {
        return Blocks.field_150486_ae;
    }

    @Override
    public int func_94085_r() {
        return 8;
    }
}
