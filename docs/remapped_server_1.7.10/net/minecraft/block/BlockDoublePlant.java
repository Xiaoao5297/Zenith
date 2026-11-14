package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoublePlant extends BlockBush implements IGrowable {

    public static final String[] field_149892_a = new String[]{"sunflower", "syringa", "grass", "fern", "rose", "paeonia"};

    public BlockDoublePlant() {
        super(Material.field_151585_k);
        this.func_149711_c(0.0F);
        this.func_149672_a(BlockDoublePlant.field_149779_h);
        this.func_149663_c("doublePlant");
    }

    @Override
    public int func_149645_b() {
        return 40;
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public int func_149885_e(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);

        return !func_149887_c(l) ? l & 7 : iblockaccess.func_72805_g(i, j - 1, k) & 7;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return super.func_149742_c(world, i, j, k) && world.func_147437_c(i, j + 1, k);
    }

    @Override
    protected void func_149855_e(World world, int i, int j, int k) {
        if (!this.func_149718_j(world, i, j, k)) {
            int l = world.func_72805_g(i, j, k);

            if (!func_149887_c(l)) {
                this.func_149697_b(world, i, j, k, l, 0);
                if (world.func_147439_a(i, j + 1, k) == this) {
                    world.func_147465_d(i, j + 1, k, Blocks.field_150350_a, 0, 2);
                }
            }

            world.func_147465_d(i, j, k, Blocks.field_150350_a, 0, 2);
        }

    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);

        return func_149887_c(l) ? world.func_147439_a(i, j - 1, k) == this : world.func_147439_a(i, j + 1, k) == this && super.func_149718_j(world, i, j, k);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        if (func_149887_c(i)) {
            return null;
        } else {
            int k = func_149890_d(i);

            return k != 3 && k != 2 ? Item.func_150898_a(this) : null;
        }
    }

    @Override
    public int func_149692_a(int i) {
        return func_149887_c(i) ? 0 : i & 7;
    }

    public static boolean func_149887_c(int i) {
        return (i & 8) != 0;
    }

    public static int func_149890_d(int i) {
        return i & 7;
    }

    public void func_149889_c(World world, int i, int j, int k, int l, int i1) {
        world.func_147465_d(i, j, k, this, l, i1);
        world.func_147465_d(i, j + 1, k, this, 8, i1);
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = ((MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;

        world.func_147465_d(i, j + 1, k, this, 8 | l, 2);
    }

    @Override
    public void func_149636_a(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
        if (world.field_72995_K || entityplayer.func_71045_bC() == null || entityplayer.func_71045_bC().func_77973_b() != Items.field_151097_aZ || func_149887_c(l) || !this.func_149886_b(world, i, j, k, l, entityplayer)) {
            super.func_149636_a(world, entityplayer, i, j, k, l);
        }
    }

    @Override
    public void func_149681_a(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {
        if (func_149887_c(l)) {
            if (world.func_147439_a(i, j - 1, k) == this) {
                if (!entityplayer.field_71075_bZ.field_75098_d) {
                    int i1 = world.func_72805_g(i, j - 1, k);
                    int j1 = func_149890_d(i1);

                    if (j1 != 3 && j1 != 2) {
                        world.func_147480_a(i, j - 1, k, true);
                    } else {
                        if (!world.field_72995_K && entityplayer.func_71045_bC() != null && entityplayer.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
                            this.func_149886_b(world, i, j, k, i1, entityplayer);
                        }

                        world.func_147468_f(i, j - 1, k);
                    }
                } else {
                    world.func_147468_f(i, j - 1, k);
                }
            }
        } else if (entityplayer.field_71075_bZ.field_75098_d && world.func_147439_a(i, j + 1, k) == this) {
            world.func_147465_d(i, j + 1, k, Blocks.field_150350_a, 0, 2);
        }

        super.func_149681_a(world, i, j, k, l, entityplayer);
    }

    private boolean func_149886_b(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {
        int i1 = func_149890_d(l);

        if (i1 != 3 && i1 != 2) {
            return false;
        } else {
            entityplayer.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
            byte b0 = 1;

            if (i1 == 3) {
                b0 = 2;
            }

            this.func_149642_a(world, i, j, k, new ItemStack(Blocks.field_150329_H, 2, b0));
            return true;
        }
    }

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);

        return func_149887_c(l) ? func_149890_d(world.func_72805_g(i, j - 1, k)) : func_149890_d(l);
    }

    @Override
    public boolean func_149851_a(World world, int i, int j, int k, boolean flag) {
        int l = this.func_149885_e(world, i, j, k);

        return l != 2 && l != 3;
    }

    @Override
    public boolean func_149852_a(World world, Random random, int i, int j, int k) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random random, int i, int j, int k) {
        int l = this.func_149885_e(world, i, j, k);

        this.func_149642_a(world, i, j, k, new ItemStack(this, 1, l));
    }
}
