package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.world.World;

public class ItemBucket extends Item {

    private Block field_77876_a;

    public ItemBucket(Block block) {
        this.field_77777_bU = 1;
        this.field_77876_a = block;
        this.func_77637_a(CreativeTabs.field_78026_f);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        boolean flag = this.field_77876_a == Blocks.field_150350_a;
        MovingObjectPosition movingobjectposition = this.func_77621_a(world, entityplayer, flag);

        if (movingobjectposition == null) {
            return itemstack;
        } else {
            if (movingobjectposition.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
                int i = movingobjectposition.field_72311_b;
                int j = movingobjectposition.field_72312_c;
                int k = movingobjectposition.field_72309_d;

                if (!world.func_72962_a(entityplayer, i, j, k)) {
                    return itemstack;
                }

                if (flag) {
                    if (!entityplayer.func_82247_a(i, j, k, movingobjectposition.field_72310_e, itemstack)) {
                        return itemstack;
                    }

                    Material material = world.func_147439_a(i, j, k).func_149688_o();
                    int l = world.func_72805_g(i, j, k);

                    if (material == Material.field_151586_h && l == 0) {
                        world.func_147468_f(i, j, k);
                        return this.func_150910_a(itemstack, entityplayer, Items.field_151131_as);
                    }

                    if (material == Material.field_151587_i && l == 0) {
                        world.func_147468_f(i, j, k);
                        return this.func_150910_a(itemstack, entityplayer, Items.field_151129_at);
                    }
                } else {
                    if (this.field_77876_a == Blocks.field_150350_a) {
                        return new ItemStack(Items.field_151133_ar);
                    }

                    if (movingobjectposition.field_72310_e == 0) {
                        --j;
                    }

                    if (movingobjectposition.field_72310_e == 1) {
                        ++j;
                    }

                    if (movingobjectposition.field_72310_e == 2) {
                        --k;
                    }

                    if (movingobjectposition.field_72310_e == 3) {
                        ++k;
                    }

                    if (movingobjectposition.field_72310_e == 4) {
                        --i;
                    }

                    if (movingobjectposition.field_72310_e == 5) {
                        ++i;
                    }

                    if (!entityplayer.func_82247_a(i, j, k, movingobjectposition.field_72310_e, itemstack)) {
                        return itemstack;
                    }

                    if (this.func_77875_a(world, i, j, k) && !entityplayer.field_71075_bZ.field_75098_d) {
                        return new ItemStack(Items.field_151133_ar);
                    }
                }
            }

            return itemstack;
        }
    }

    private ItemStack func_150910_a(ItemStack itemstack, EntityPlayer entityplayer, Item item) {
        if (entityplayer.field_71075_bZ.field_75098_d) {
            return itemstack;
        } else if (--itemstack.field_77994_a <= 0) {
            return new ItemStack(item);
        } else {
            if (!entityplayer.field_71071_by.func_70441_a(new ItemStack(item))) {
                entityplayer.func_71019_a(new ItemStack(item, 1, 0), false);
            }

            return itemstack;
        }
    }

    public boolean func_77875_a(World world, int i, int j, int k) {
        if (this.field_77876_a == Blocks.field_150350_a) {
            return false;
        } else {
            Material material = world.func_147439_a(i, j, k).func_149688_o();
            boolean flag = !material.func_76220_a();

            if (!world.func_147437_c(i, j, k) && !flag) {
                return false;
            } else {
                if (world.field_73011_w.field_76575_d && this.field_77876_a == Blocks.field_150358_i) {
                    world.func_72908_a((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), "random.fizz", 0.5F, 2.6F + (world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.8F);

                    for (int l = 0; l < 8; ++l) {
                        world.func_72869_a("largesmoke", (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                } else {
                    if (!world.field_72995_K && flag && !material.func_76224_d()) {
                        world.func_147480_a(i, j, k, true);
                    }

                    world.func_147465_d(i, j, k, this.field_77876_a, 0, 3);
                }

                return true;
            }
        }
    }
}
