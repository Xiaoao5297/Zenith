package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {

    protected BlockSkull() {
        super(Material.field_151594_q);
        this.func_149676_a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
    }

    @Override
    public int func_149645_b() {
        return -1;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k) & 7;

        switch (l) {
            case 1:
            default:
                this.func_149676_a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;
            case 2:
                this.func_149676_a(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;
            case 3:
                this.func_149676_a(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;
            case 4:
                this.func_149676_a(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case 5:
                this.func_149676_a(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }

    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        this.func_149719_a(world, i, j, k);
        return super.func_149668_a(world, i, j, k);
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 2.5D) & 3;

        world.func_72921_c(i, j, k, l, 2);
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntitySkull();
    }

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        TileEntity tileentity = world.func_147438_o(i, j, k);

        return tileentity != null && tileentity instanceof TileEntitySkull ? ((TileEntitySkull) tileentity).func_145904_a() : super.func_149643_k(world, i, j, k);
    }

    @Override
    public int func_149692_a(int i) {
        return i;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {}

    @Override
    public void func_149681_a(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {
        if (entityplayer.field_71075_bZ.field_75098_d) {
            l |= 8;
            world.func_72921_c(i, j, k, l, 4);
        }

        super.func_149681_a(world, i, j, k, l, entityplayer);
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        if (!world.field_72995_K) {
            if ((l & 8) == 0) {
                ItemStack itemstack = new ItemStack(Items.field_151144_bL, 1, this.func_149643_k(world, i, j, k));
                TileEntitySkull tileentityskull = (TileEntitySkull) world.func_147438_o(i, j, k);

                if (tileentityskull.func_145904_a() == 3 && tileentityskull.func_152108_a() != null) {
                    itemstack.func_77982_d(new NBTTagCompound());
                    NBTTagCompound nbttagcompound = new NBTTagCompound();

                    NBTUtil.func_152460_a(nbttagcompound, tileentityskull.func_152108_a());
                    itemstack.func_77978_p().func_74782_a("SkullOwner", nbttagcompound);
                }

                this.func_149642_a(world, i, j, k, itemstack);
            }

            super.func_149749_a(world, i, j, k, block, l);
        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151144_bL;
    }

    public void func_149965_a(World world, int i, int j, int k, TileEntitySkull tileentityskull) {
        if (tileentityskull.func_145904_a() == 1 && j >= 2 && world.field_73013_u != EnumDifficulty.PEACEFUL && !world.field_72995_K) {
            int l;
            EntityWither entitywither;
            Iterator iterator;
            EntityPlayer entityplayer;
            int i1;

            for (l = -2; l <= 0; ++l) {
                if (world.func_147439_a(i, j - 1, k + l) == Blocks.field_150425_aM && world.func_147439_a(i, j - 1, k + l + 1) == Blocks.field_150425_aM && world.func_147439_a(i, j - 2, k + l + 1) == Blocks.field_150425_aM && world.func_147439_a(i, j - 1, k + l + 2) == Blocks.field_150425_aM && this.func_149966_a(world, i, j, k + l, 1) && this.func_149966_a(world, i, j, k + l + 1, 1) && this.func_149966_a(world, i, j, k + l + 2, 1)) {
                    world.func_72921_c(i, j, k + l, 8, 2);
                    world.func_72921_c(i, j, k + l + 1, 8, 2);
                    world.func_72921_c(i, j, k + l + 2, 8, 2);
                    world.func_147465_d(i, j, k + l, func_149729_e(0), 0, 2);
                    world.func_147465_d(i, j, k + l + 1, func_149729_e(0), 0, 2);
                    world.func_147465_d(i, j, k + l + 2, func_149729_e(0), 0, 2);
                    world.func_147465_d(i, j - 1, k + l, func_149729_e(0), 0, 2);
                    world.func_147465_d(i, j - 1, k + l + 1, func_149729_e(0), 0, 2);
                    world.func_147465_d(i, j - 1, k + l + 2, func_149729_e(0), 0, 2);
                    world.func_147465_d(i, j - 2, k + l + 1, func_149729_e(0), 0, 2);
                    if (!world.field_72995_K) {
                        entitywither = new EntityWither(world);
                        entitywither.func_70012_b((double) i + 0.5D, (double) j - 1.45D, (double) (k + l) + 1.5D, 90.0F, 0.0F);
                        entitywither.field_70761_aq = 90.0F;
                        entitywither.func_82206_m();
                        if (!world.field_72995_K) {
                            iterator = world.func_72872_a(EntityPlayer.class, entitywither.field_70121_D.func_72314_b(50.0D, 50.0D, 50.0D)).iterator();

                            while (iterator.hasNext()) {
                                entityplayer = (EntityPlayer) iterator.next();
                                entityplayer.func_71029_a(AchievementList.field_150963_I);
                            }
                        }

                        world.func_72838_d(entitywither);
                    }

                    for (i1 = 0; i1 < 120; ++i1) {
                        world.func_72869_a("snowballpoof", (double) i + world.field_73012_v.nextDouble(), (double) (j - 2) + world.field_73012_v.nextDouble() * 3.9D, (double) (k + l + 1) + world.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }

                    world.func_147444_c(i, j, k + l, func_149729_e(0));
                    world.func_147444_c(i, j, k + l + 1, func_149729_e(0));
                    world.func_147444_c(i, j, k + l + 2, func_149729_e(0));
                    world.func_147444_c(i, j - 1, k + l, func_149729_e(0));
                    world.func_147444_c(i, j - 1, k + l + 1, func_149729_e(0));
                    world.func_147444_c(i, j - 1, k + l + 2, func_149729_e(0));
                    world.func_147444_c(i, j - 2, k + l + 1, func_149729_e(0));
                    return;
                }
            }

            for (l = -2; l <= 0; ++l) {
                if (world.func_147439_a(i + l, j - 1, k) == Blocks.field_150425_aM && world.func_147439_a(i + l + 1, j - 1, k) == Blocks.field_150425_aM && world.func_147439_a(i + l + 1, j - 2, k) == Blocks.field_150425_aM && world.func_147439_a(i + l + 2, j - 1, k) == Blocks.field_150425_aM && this.func_149966_a(world, i + l, j, k, 1) && this.func_149966_a(world, i + l + 1, j, k, 1) && this.func_149966_a(world, i + l + 2, j, k, 1)) {
                    world.func_72921_c(i + l, j, k, 8, 2);
                    world.func_72921_c(i + l + 1, j, k, 8, 2);
                    world.func_72921_c(i + l + 2, j, k, 8, 2);
                    world.func_147465_d(i + l, j, k, func_149729_e(0), 0, 2);
                    world.func_147465_d(i + l + 1, j, k, func_149729_e(0), 0, 2);
                    world.func_147465_d(i + l + 2, j, k, func_149729_e(0), 0, 2);
                    world.func_147465_d(i + l, j - 1, k, func_149729_e(0), 0, 2);
                    world.func_147465_d(i + l + 1, j - 1, k, func_149729_e(0), 0, 2);
                    world.func_147465_d(i + l + 2, j - 1, k, func_149729_e(0), 0, 2);
                    world.func_147465_d(i + l + 1, j - 2, k, func_149729_e(0), 0, 2);
                    if (!world.field_72995_K) {
                        entitywither = new EntityWither(world);
                        entitywither.func_70012_b((double) (i + l) + 1.5D, (double) j - 1.45D, (double) k + 0.5D, 0.0F, 0.0F);
                        entitywither.func_82206_m();
                        if (!world.field_72995_K) {
                            iterator = world.func_72872_a(EntityPlayer.class, entitywither.field_70121_D.func_72314_b(50.0D, 50.0D, 50.0D)).iterator();

                            while (iterator.hasNext()) {
                                entityplayer = (EntityPlayer) iterator.next();
                                entityplayer.func_71029_a(AchievementList.field_150963_I);
                            }
                        }

                        world.func_72838_d(entitywither);
                    }

                    for (i1 = 0; i1 < 120; ++i1) {
                        world.func_72869_a("snowballpoof", (double) (i + l + 1) + world.field_73012_v.nextDouble(), (double) (j - 2) + world.field_73012_v.nextDouble() * 3.9D, (double) k + world.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }

                    world.func_147444_c(i + l, j, k, func_149729_e(0));
                    world.func_147444_c(i + l + 1, j, k, func_149729_e(0));
                    world.func_147444_c(i + l + 2, j, k, func_149729_e(0));
                    world.func_147444_c(i + l, j - 1, k, func_149729_e(0));
                    world.func_147444_c(i + l + 1, j - 1, k, func_149729_e(0));
                    world.func_147444_c(i + l + 2, j - 1, k, func_149729_e(0));
                    world.func_147444_c(i + l + 1, j - 2, k, func_149729_e(0));
                    return;
                }
            }
        }

    }

    private boolean func_149966_a(World world, int i, int j, int k, int l) {
        if (world.func_147439_a(i, j, k) != this) {
            return false;
        } else {
            TileEntity tileentity = world.func_147438_o(i, j, k);

            return tileentity != null && tileentity instanceof TileEntitySkull ? ((TileEntitySkull) tileentity).func_145904_a() == l : false;
        }
    }
}
