package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemDoor extends Item {

    private Material field_77870_a;

    public ItemDoor(Material material) {
        this.field_77870_a = material;
        this.field_77777_bU = 1;
        this.func_77637_a(CreativeTabs.field_78028_d);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (l != 1) {
            return false;
        } else {
            ++j;
            Block block;

            if (this.field_77870_a == Material.field_151575_d) {
                block = Blocks.field_150466_ao;
            } else {
                block = Blocks.field_150454_av;
            }

            if (entityplayer.func_82247_a(i, j, k, l, itemstack) && entityplayer.func_82247_a(i, j + 1, k, l, itemstack)) {
                if (!block.func_149742_c(world, i, j, k)) {
                    return false;
                } else {
                    int i1 = MathHelper.func_76128_c((double) ((entityplayer.field_70177_z + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;

                    func_150924_a(world, i, j, k, i1, block);
                    --itemstack.field_77994_a;
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public static void func_150924_a(World world, int i, int j, int k, int l, Block block) {
        byte b0 = 0;
        byte b1 = 0;

        if (l == 0) {
            b1 = 1;
        }

        if (l == 1) {
            b0 = -1;
        }

        if (l == 2) {
            b1 = -1;
        }

        if (l == 3) {
            b0 = 1;
        }

        int i1 = (world.func_147439_a(i - b0, j, k - b1).func_149721_r() ? 1 : 0) + (world.func_147439_a(i - b0, j + 1, k - b1).func_149721_r() ? 1 : 0);
        int j1 = (world.func_147439_a(i + b0, j, k + b1).func_149721_r() ? 1 : 0) + (world.func_147439_a(i + b0, j + 1, k + b1).func_149721_r() ? 1 : 0);
        boolean flag = world.func_147439_a(i - b0, j, k - b1) == block || world.func_147439_a(i - b0, j + 1, k - b1) == block;
        boolean flag1 = world.func_147439_a(i + b0, j, k + b1) == block || world.func_147439_a(i + b0, j + 1, k + b1) == block;
        boolean flag2 = false;

        if (flag && !flag1) {
            flag2 = true;
        } else if (j1 > i1) {
            flag2 = true;
        }

        world.func_147465_d(i, j, k, block, l, 2);
        world.func_147465_d(i, j + 1, k, block, 8 | (flag2 ? 1 : 0), 2);
        world.func_147459_d(i, j, k, block);
        world.func_147459_d(i, j + 1, k, block);
    }
}
