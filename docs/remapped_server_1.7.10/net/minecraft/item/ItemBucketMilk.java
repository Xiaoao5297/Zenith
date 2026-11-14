package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemBucketMilk extends Item {

    public ItemBucketMilk() {
        this.func_77625_d(1);
        this.func_77637_a(CreativeTabs.field_78026_f);
    }

    @Override
    public ItemStack func_77654_b(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!entityplayer.field_71075_bZ.field_75098_d) {
            --itemstack.field_77994_a;
        }

        if (!world.field_72995_K) {
            entityplayer.func_70674_bp();
        }

        return itemstack.field_77994_a <= 0 ? new ItemStack(Items.field_151133_ar) : itemstack;
    }

    @Override
    public int func_77626_a(ItemStack itemstack) {
        return 32;
    }

    @Override
    public EnumAction func_77661_b(ItemStack itemstack) {
        return EnumAction.drink;
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        entityplayer.func_71008_a(itemstack, this.func_77626_a(itemstack));
        return itemstack;
    }
}
