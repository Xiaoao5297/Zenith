package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {

    private final boolean field_150948_b;
    private final BlockSlab field_150949_c;
    private final BlockSlab field_150947_d;

    public ItemSlab(Block block, BlockSlab blockslab, BlockSlab blockslab1, boolean flag) {
        super(block);
        this.field_150949_c = blockslab;
        this.field_150947_d = blockslab1;
        this.field_150948_b = flag;
        this.func_77656_e(0);
        this.func_77627_a(true);
    }

    @Override
    public int func_77647_b(int i) {
        return i;
    }

    @Override
    public String func_77667_c(ItemStack itemstack) {
        return this.field_150949_c.func_150002_b(itemstack.func_77960_j());
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (this.field_150948_b) {
            return super.func_77648_a(itemstack, entityplayer, world, i, j, k, l, f, f1, f2);
        } else if (itemstack.field_77994_a == 0) {
            return false;
        } else if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
            return false;
        } else {
            Block block = world.func_147439_a(i, j, k);
            int i1 = world.func_72805_g(i, j, k);
            int j1 = i1 & 7;
            boolean flag = (i1 & 8) != 0;

            if ((l == 1 && !flag || l == 0 && flag) && block == this.field_150949_c && j1 == itemstack.func_77960_j()) {
                if (world.func_72855_b(this.field_150947_d.func_149668_a(world, i, j, k)) && world.func_147465_d(i, j, k, this.field_150947_d, j1, 3)) {
                    world.func_72908_a((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.field_150947_d.field_149762_H.func_150496_b(), (this.field_150947_d.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150947_d.field_149762_H.func_150494_d() * 0.8F);
                    --itemstack.field_77994_a;
                }

                return true;
            } else {
                return this.func_150946_a(itemstack, entityplayer, world, i, j, k, l) ? true : super.func_77648_a(itemstack, entityplayer, world, i, j, k, l, f, f1, f2);
            }
        }
    }

    private boolean func_150946_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l) {
        if (l == 0) {
            --j;
        }

        if (l == 1) {
            ++j;
        }

        if (l == 2) {
            --k;
        }

        if (l == 3) {
            ++k;
        }

        if (l == 4) {
            --i;
        }

        if (l == 5) {
            ++i;
        }

        Block block = world.func_147439_a(i, j, k);
        int i1 = world.func_72805_g(i, j, k);
        int j1 = i1 & 7;

        if (block == this.field_150949_c && j1 == itemstack.func_77960_j()) {
            if (world.func_72855_b(this.field_150947_d.func_149668_a(world, i, j, k)) && world.func_147465_d(i, j, k, this.field_150947_d, j1, 3)) {
                world.func_72908_a((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.field_150947_d.field_149762_H.func_150496_b(), (this.field_150947_d.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150947_d.field_149762_H.func_150494_d() * 0.8F);
                --itemstack.field_77994_a;
            }

            return true;
        } else {
            return false;
        }
    }
}
