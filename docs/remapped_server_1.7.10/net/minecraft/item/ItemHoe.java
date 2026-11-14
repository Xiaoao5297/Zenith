package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemHoe extends Item {

    protected Item$ToolMaterial field_77843_a;

    public ItemHoe(Item$ToolMaterial item$toolmaterial) {
        this.field_77843_a = item$toolmaterial;
        this.field_77777_bU = 1;
        this.func_77656_e(item$toolmaterial.func_77997_a());
        this.func_77637_a(CreativeTabs.field_78040_i);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
            return false;
        } else {
            Block block = world.func_147439_a(i, j, k);

            if (l != 0 && world.func_147439_a(i, j + 1, k).func_149688_o() == Material.field_151579_a && (block == Blocks.field_150349_c || block == Blocks.field_150346_d)) {
                Block block1 = Blocks.field_150458_ak;

                world.func_72908_a((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), block1.field_149762_H.func_150498_e(), (block1.field_149762_H.func_150497_c() + 1.0F) / 2.0F, block1.field_149762_H.func_150494_d() * 0.8F);
                if (world.field_72995_K) {
                    return true;
                } else {
                    world.func_147449_b(i, j, k, block1);
                    itemstack.func_77972_a(1, entityplayer);
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public String func_77842_f() {
        return this.field_77843_a.toString();
    }
}
