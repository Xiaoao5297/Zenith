package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemBlock extends Item {

    protected final Block field_150939_a;

    public ItemBlock(Block block) {
        this.field_150939_a = block;
    }

    @Override
    public ItemBlock func_77655_b(String s) {
        super.func_77655_b(s);
        return this;
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        Block block = world.func_147439_a(i, j, k);

        if (block == Blocks.field_150431_aC && (world.func_72805_g(i, j, k) & 7) < 1) {
            l = 1;
        } else if (block != Blocks.field_150395_bd && block != Blocks.field_150329_H && block != Blocks.field_150330_I) {
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
        }

        if (itemstack.field_77994_a == 0) {
            return false;
        } else if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
            return false;
        } else if (j == 255 && this.field_150939_a.func_149688_o().func_76220_a()) {
            return false;
        } else if (world.func_147472_a(this.field_150939_a, i, j, k, false, l, entityplayer, itemstack)) {
            int i1 = this.func_77647_b(itemstack.func_77960_j());
            int j1 = this.field_150939_a.func_149660_a(world, i, j, k, l, f, f1, f2, i1);

            if (world.func_147465_d(i, j, k, this.field_150939_a, j1, 3)) {
                if (world.func_147439_a(i, j, k) == this.field_150939_a) {
                    this.field_150939_a.func_149689_a(world, i, j, k, entityplayer, itemstack);
                    this.field_150939_a.func_149714_e(world, i, j, k, j1);
                }

                world.func_72908_a((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.field_150939_a.field_149762_H.func_150496_b(), (this.field_150939_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150939_a.field_149762_H.func_150494_d() * 0.8F);
                --itemstack.field_77994_a;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public String func_77667_c(ItemStack itemstack) {
        return this.field_150939_a.func_149739_a();
    }

    @Override
    public String func_77658_a() {
        return this.field_150939_a.func_149739_a();
    }
}
