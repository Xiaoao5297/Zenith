package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item {

    public ItemMonsterPlacer() {
        this.func_77627_a(true);
        this.func_77637_a(CreativeTabs.field_78026_f);
    }

    @Override
    public String func_77653_i(ItemStack itemstack) {
        String s = ("" + StatCollector.func_74838_a(this.func_77658_a() + ".name")).trim();
        String s1 = EntityList.func_75617_a(itemstack.func_77960_j());

        if (s1 != null) {
            s = s + " " + StatCollector.func_74838_a("entity." + s1 + ".name");
        }

        return s;
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            Block block = world.func_147439_a(i, j, k);

            i += Facing.field_71586_b[l];
            j += Facing.field_71587_c[l];
            k += Facing.field_71585_d[l];
            double d0 = 0.0D;

            if (l == 1 && block.func_149645_b() == 11) {
                d0 = 0.5D;
            }

            Entity entity = func_77840_a(world, itemstack.func_77960_j(), (double) i + 0.5D, (double) j + d0, (double) k + 0.5D);

            if (entity != null) {
                if (entity instanceof EntityLivingBase && itemstack.func_82837_s()) {
                    ((EntityLiving) entity).func_94058_c(itemstack.func_82833_r());
                }

                if (!entityplayer.field_71075_bZ.field_75098_d) {
                    --itemstack.field_77994_a;
                }
            }

            return true;
        }
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (world.field_72995_K) {
            return itemstack;
        } else {
            MovingObjectPosition movingobjectposition = this.func_77621_a(world, entityplayer, true);

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

                    if (!entityplayer.func_82247_a(i, j, k, movingobjectposition.field_72310_e, itemstack)) {
                        return itemstack;
                    }

                    if (world.func_147439_a(i, j, k) instanceof BlockLiquid) {
                        Entity entity = func_77840_a(world, itemstack.func_77960_j(), (double) i, (double) j, (double) k);

                        if (entity != null) {
                            if (entity instanceof EntityLivingBase && itemstack.func_82837_s()) {
                                ((EntityLiving) entity).func_94058_c(itemstack.func_82833_r());
                            }

                            if (!entityplayer.field_71075_bZ.field_75098_d) {
                                --itemstack.field_77994_a;
                            }
                        }
                    }
                }

                return itemstack;
            }
        }
    }

    public static Entity func_77840_a(World world, int i, double d0, double d1, double d2) {
        if (!EntityList.field_75627_a.containsKey(i)) {
            return null;
        } else {
            Entity entity = null;

            for (int j = 0; j < 1; ++j) {
                entity = EntityList.func_75616_a(i, world);
                if (entity != null && entity instanceof EntityLivingBase) {
                    EntityLiving entityliving = (EntityLiving) entity;

                    entity.func_70012_b(d0, d1, d2, MathHelper.func_76142_g(world.field_73012_v.nextFloat() * 360.0F), 0.0F);
                    entityliving.field_70759_as = entityliving.field_70177_z;
                    entityliving.field_70761_aq = entityliving.field_70177_z;
                    entityliving.func_110161_a((IEntityLivingData) null);
                    world.func_72838_d(entity);
                    entityliving.func_70642_aH();
                }
            }

            return entity;
        }
    }
}
