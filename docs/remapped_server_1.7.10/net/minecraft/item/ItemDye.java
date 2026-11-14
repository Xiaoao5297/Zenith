package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLog;
import net.minecraft.block.IGrowable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemDye extends Item {

    public static final String[] field_150923_a = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
    public static final String[] field_150921_b = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
    public static final int[] field_150922_c = new int[]{1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

    public ItemDye() {
        this.func_77627_a(true);
        this.func_77656_e(0);
        this.func_77637_a(CreativeTabs.field_78035_l);
    }

    @Override
    public String func_77667_c(ItemStack itemstack) {
        int i = MathHelper.func_76125_a(itemstack.func_77960_j(), 0, 15);

        return super.func_77658_a() + "." + ItemDye.field_150923_a[i];
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
            return false;
        } else {
            if (itemstack.func_77960_j() == 15) {
                if (func_150919_a(itemstack, world, i, j, k)) {
                    if (!world.field_72995_K) {
                        world.func_72926_e(2005, i, j, k, 0);
                    }

                    return true;
                }
            } else if (itemstack.func_77960_j() == 3) {
                Block block = world.func_147439_a(i, j, k);
                int i1 = world.func_72805_g(i, j, k);

                if (block == Blocks.field_150364_r && BlockLog.func_150165_c(i1) == 3) {
                    if (l == 0) {
                        return false;
                    }

                    if (l == 1) {
                        return false;
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

                    if (world.func_147437_c(i, j, k)) {
                        int j1 = Blocks.field_150375_by.func_149660_a(world, i, j, k, l, f, f1, f2, 0);

                        world.func_147465_d(i, j, k, Blocks.field_150375_by, j1, 2);
                        if (!entityplayer.field_71075_bZ.field_75098_d) {
                            --itemstack.field_77994_a;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public static boolean func_150919_a(ItemStack itemstack, World world, int i, int j, int k) {
        Block block = world.func_147439_a(i, j, k);

        if (block instanceof IGrowable) {
            IGrowable igrowable = (IGrowable) block;

            if (igrowable.func_149851_a(world, i, j, k, world.field_72995_K)) {
                if (!world.field_72995_K) {
                    if (igrowable.func_149852_a(world, world.field_73012_v, i, j, k)) {
                        igrowable.func_149853_b(world, world.field_73012_v, i, j, k);
                    }

                    --itemstack.field_77994_a;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean func_111207_a(ItemStack itemstack, EntityPlayer entityplayer, EntityLivingBase entitylivingbase) {
        if (entitylivingbase instanceof EntitySheep) {
            EntitySheep entitysheep = (EntitySheep) entitylivingbase;
            int i = BlockColored.func_150032_b(itemstack.func_77960_j());

            if (!entitysheep.func_70892_o() && entitysheep.func_70896_n() != i) {
                entitysheep.func_70891_b(i);
                --itemstack.field_77994_a;
            }

            return true;
        } else {
            return false;
        }
    }
}
