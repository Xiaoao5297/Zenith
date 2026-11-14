package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.world.World;

public class ItemSnowball extends Item {

    public ItemSnowball() {
        this.field_77777_bU = 16;
        this.func_77637_a(CreativeTabs.field_78026_f);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!entityplayer.field_71075_bZ.field_75098_d) {
            --itemstack.field_77994_a;
        }

        world.func_72956_a(entityplayer, "random.bow", 0.5F, 0.4F / (ItemSnowball.field_77697_d.nextFloat() * 0.4F + 0.8F));
        if (!world.field_72995_K) {
            world.func_72838_d(new EntitySnowball(world, entityplayer));
        }

        return itemstack;
    }
}
