package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemSeeds extends Item {

    private Block field_150925_a;
    private Block field_77838_b;

    public ItemSeeds(Block block, Block block1) {
        this.field_150925_a = block;
        this.field_77838_b = block1;
        this.func_77637_a(CreativeTabs.field_78035_l);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (l != 1) {
            return false;
        } else if (entityplayer.func_82247_a(i, j, k, l, itemstack) && entityplayer.func_82247_a(i, j + 1, k, l, itemstack)) {
            if (world.func_147439_a(i, j, k) == this.field_77838_b && world.func_147437_c(i, j + 1, k)) {
                world.func_147449_b(i, j + 1, k, this.field_150925_a);
                --itemstack.field_77994_a;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
