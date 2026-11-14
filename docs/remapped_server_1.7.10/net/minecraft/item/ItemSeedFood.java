package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemSeedFood extends ItemFood {

    private Block field_150908_b;
    private Block field_82809_c;

    public ItemSeedFood(int i, float f, Block block, Block block1) {
        super(i, f, false);
        this.field_150908_b = block;
        this.field_82809_c = block1;
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (l != 1) {
            return false;
        } else if (entityplayer.func_82247_a(i, j, k, l, itemstack) && entityplayer.func_82247_a(i, j + 1, k, l, itemstack)) {
            if (world.func_147439_a(i, j, k) == this.field_82809_c && world.func_147437_c(i, j + 1, k)) {
                world.func_147449_b(i, j + 1, k, this.field_150908_b);
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
