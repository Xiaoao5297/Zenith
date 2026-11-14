package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional {

    private boolean field_149985_a;

    protected BlockPumpkin(boolean flag) {
        super(Material.field_151572_C);
        this.func_149675_a(true);
        this.field_149985_a = flag;
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        if (world.func_147439_a(i, j - 1, k) == Blocks.field_150433_aE && world.func_147439_a(i, j - 2, k) == Blocks.field_150433_aE) {
            if (!world.field_72995_K) {
                world.func_147465_d(i, j, k, func_149729_e(0), 0, 2);
                world.func_147465_d(i, j - 1, k, func_149729_e(0), 0, 2);
                world.func_147465_d(i, j - 2, k, func_149729_e(0), 0, 2);
                EntitySnowman entitysnowman = new EntitySnowman(world);

                entitysnowman.func_70012_b((double) i + 0.5D, (double) j - 1.95D, (double) k + 0.5D, 0.0F, 0.0F);
                world.func_72838_d(entitysnowman);
                world.func_147444_c(i, j, k, func_149729_e(0));
                world.func_147444_c(i, j - 1, k, func_149729_e(0));
                world.func_147444_c(i, j - 2, k, func_149729_e(0));
            }

            for (int l = 0; l < 120; ++l) {
                world.func_72869_a("snowshovel", (double) i + world.field_73012_v.nextDouble(), (double) (j - 2) + world.field_73012_v.nextDouble() * 2.5D, (double) k + world.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        } else if (world.func_147439_a(i, j - 1, k) == Blocks.field_150339_S && world.func_147439_a(i, j - 2, k) == Blocks.field_150339_S) {
            boolean flag = world.func_147439_a(i - 1, j - 1, k) == Blocks.field_150339_S && world.func_147439_a(i + 1, j - 1, k) == Blocks.field_150339_S;
            boolean flag1 = world.func_147439_a(i, j - 1, k - 1) == Blocks.field_150339_S && world.func_147439_a(i, j - 1, k + 1) == Blocks.field_150339_S;

            if (flag || flag1) {
                world.func_147465_d(i, j, k, func_149729_e(0), 0, 2);
                world.func_147465_d(i, j - 1, k, func_149729_e(0), 0, 2);
                world.func_147465_d(i, j - 2, k, func_149729_e(0), 0, 2);
                if (flag) {
                    world.func_147465_d(i - 1, j - 1, k, func_149729_e(0), 0, 2);
                    world.func_147465_d(i + 1, j - 1, k, func_149729_e(0), 0, 2);
                } else {
                    world.func_147465_d(i, j - 1, k - 1, func_149729_e(0), 0, 2);
                    world.func_147465_d(i, j - 1, k + 1, func_149729_e(0), 0, 2);
                }

                EntityIronGolem entityirongolem = new EntityIronGolem(world);

                entityirongolem.func_70849_f(true);
                entityirongolem.func_70012_b((double) i + 0.5D, (double) j - 1.95D, (double) k + 0.5D, 0.0F, 0.0F);
                world.func_72838_d(entityirongolem);

                for (int i1 = 0; i1 < 120; ++i1) {
                    world.func_72869_a("snowballpoof", (double) i + world.field_73012_v.nextDouble(), (double) (j - 2) + world.field_73012_v.nextDouble() * 3.9D, (double) k + world.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
                }

                world.func_147444_c(i, j, k, func_149729_e(0));
                world.func_147444_c(i, j - 1, k, func_149729_e(0));
                world.func_147444_c(i, j - 2, k, func_149729_e(0));
                if (flag) {
                    world.func_147444_c(i - 1, j - 1, k, func_149729_e(0));
                    world.func_147444_c(i + 1, j - 1, k, func_149729_e(0));
                } else {
                    world.func_147444_c(i, j - 1, k - 1, func_149729_e(0));
                    world.func_147444_c(i, j - 1, k + 1, func_149729_e(0));
                }
            }
        }

    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return world.func_147439_a(i, j, k).field_149764_J.func_76222_j() && World.func_147466_a(world, i, j - 1, k);
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 2.5D) & 3;

        world.func_72921_c(i, j, k, l, 2);
    }
}
