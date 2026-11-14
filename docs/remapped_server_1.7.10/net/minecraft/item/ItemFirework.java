package net.minecraft.item;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemFirework extends Item {

    public ItemFirework() {}

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (!world.field_72995_K) {
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(world, (double) ((float) i + f), (double) ((float) j + f1), (double) ((float) k + f2), itemstack);

            world.func_72838_d(entityfireworkrocket);
            if (!entityplayer.field_71075_bZ.field_75098_d) {
                --itemstack.field_77994_a;
            }

            return true;
        } else {
            return false;
        }
    }
}
