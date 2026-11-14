package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlockWithMetadata {

    public ItemSnow(Block block, Block block1) {
        super(block, block1);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (itemstack.field_77994_a == 0) {
            return false;
        } else if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
            return false;
        } else {
            Block block = world.func_147439_a(i, j, k);

            if (block == Blocks.field_150431_aC) {
                int i1 = world.func_72805_g(i, j, k);
                int j1 = i1 & 7;

                if (j1 <= 6 && world.func_72855_b(this.field_150939_a.func_149668_a(world, i, j, k)) && world.func_72921_c(i, j, k, j1 + 1 | i1 & -8, 2)) {
                    world.func_72908_a((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.field_150939_a.field_149762_H.func_150496_b(), (this.field_150939_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150939_a.field_149762_H.func_150494_d() * 0.8F);
                    --itemstack.field_77994_a;
                    return true;
                }
            }

            return super.func_77648_a(itemstack, entityplayer, world, i, j, k, l, f, f1, f2);
        }
    }
}
