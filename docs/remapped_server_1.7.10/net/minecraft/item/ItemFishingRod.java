package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {

    public ItemFishingRod() {
        this.func_77656_e(64);
        this.func_77625_d(1);
        this.func_77637_a(CreativeTabs.field_78040_i);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (entityplayer.field_71104_cf != null) {
            int i = entityplayer.field_71104_cf.func_146034_e();

            itemstack.func_77972_a(i, entityplayer);
            entityplayer.func_71038_i();
        } else {
            world.func_72956_a(entityplayer, "random.bow", 0.5F, 0.4F / (ItemFishingRod.field_77697_d.nextFloat() * 0.4F + 0.8F));
            if (!world.field_72995_K) {
                world.func_72838_d(new EntityFishHook(world, entityplayer));
            }

            entityplayer.func_71038_i();
        }

        return itemstack;
    }

    @Override
    public boolean func_77616_k(ItemStack itemstack) {
        return super.func_77616_k(itemstack);
    }

    @Override
    public int func_77619_b() {
        return 1;
    }
}
