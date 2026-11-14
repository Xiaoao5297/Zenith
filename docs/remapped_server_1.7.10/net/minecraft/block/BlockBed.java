package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer$EnumStatus;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional {

    public static final int[][] field_149981_a = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};

    public BlockBed() {
        super(Material.field_151580_n);
        this.func_149978_e();
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            int i1 = world.func_72805_g(i, j, k);

            if (!func_149975_b(i1)) {
                int j1 = func_149895_l(i1);

                i += BlockBed.field_149981_a[j1][0];
                k += BlockBed.field_149981_a[j1][1];
                if (world.func_147439_a(i, j, k) != this) {
                    return true;
                }

                i1 = world.func_72805_g(i, j, k);
            }

            if (world.field_73011_w.func_76567_e() && world.func_72807_a(i, k) != BiomeGenBase.field_76778_j) {
                if (func_149976_c(i1)) {
                    EntityPlayer entityplayer1 = null;
                    Iterator iterator = world.field_73010_i.iterator();

                    while (iterator.hasNext()) {
                        EntityPlayer entityplayer2 = (EntityPlayer) iterator.next();

                        if (entityplayer2.func_70608_bn()) {
                            ChunkCoordinates chunkcoordinates = entityplayer2.field_71081_bT;

                            if (chunkcoordinates.field_71574_a == i && chunkcoordinates.field_71572_b == j && chunkcoordinates.field_71573_c == k) {
                                entityplayer1 = entityplayer2;
                            }
                        }
                    }

                    if (entityplayer1 != null) {
                        entityplayer.func_146105_b(new ChatComponentTranslation("tile.bed.occupied", new Object[0]));
                        return true;
                    }

                    func_149979_a(world, i, j, k, false);
                }

                EntityPlayer$EnumStatus entityplayer$enumstatus = entityplayer.func_71018_a(i, j, k);

                if (entityplayer$enumstatus == EntityPlayer$EnumStatus.OK) {
                    func_149979_a(world, i, j, k, true);
                    return true;
                } else {
                    if (entityplayer$enumstatus == EntityPlayer$EnumStatus.NOT_POSSIBLE_NOW) {
                        entityplayer.func_146105_b(new ChatComponentTranslation("tile.bed.noSleep", new Object[0]));
                    } else if (entityplayer$enumstatus == EntityPlayer$EnumStatus.NOT_SAFE) {
                        entityplayer.func_146105_b(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]));
                    }

                    return true;
                }
            } else {
                double d0 = (double) i + 0.5D;
                double d1 = (double) j + 0.5D;
                double d2 = (double) k + 0.5D;

                world.func_147468_f(i, j, k);
                int k1 = func_149895_l(i1);

                i += BlockBed.field_149981_a[k1][0];
                k += BlockBed.field_149981_a[k1][1];
                if (world.func_147439_a(i, j, k) == this) {
                    world.func_147468_f(i, j, k);
                    d0 = (d0 + (double) i + 0.5D) / 2.0D;
                    d1 = (d1 + (double) j + 0.5D) / 2.0D;
                    d2 = (d2 + (double) k + 0.5D) / 2.0D;
                }

                world.func_72885_a((Entity) null, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), 5.0F, true, true);
                return true;
            }
        }
    }

    @Override
    public int func_149645_b() {
        return 14;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_149978_e();
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        int l = world.func_72805_g(i, j, k);
        int i1 = func_149895_l(l);

        if (func_149975_b(l)) {
            if (world.func_147439_a(i - BlockBed.field_149981_a[i1][0], j, k - BlockBed.field_149981_a[i1][1]) != this) {
                world.func_147468_f(i, j, k);
            }
        } else if (world.func_147439_a(i + BlockBed.field_149981_a[i1][0], j, k + BlockBed.field_149981_a[i1][1]) != this) {
            world.func_147468_f(i, j, k);
            if (!world.field_72995_K) {
                this.func_149697_b(world, i, j, k, l, 0);
            }
        }

    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return func_149975_b(i) ? Item.func_150899_d(0) : Items.field_151104_aV;
    }

    private void func_149978_e() {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    public static boolean func_149975_b(int i) {
        return (i & 8) != 0;
    }

    public static boolean func_149976_c(int i) {
        return (i & 4) != 0;
    }

    public static void func_149979_a(World world, int i, int j, int k, boolean flag) {
        int l = world.func_72805_g(i, j, k);

        if (flag) {
            l |= 4;
        } else {
            l &= -5;
        }

        world.func_72921_c(i, j, k, l, 4);
    }

    public static ChunkCoordinates func_149977_a(World world, int i, int j, int k, int l) {
        int i1 = world.func_72805_g(i, j, k);
        int j1 = BlockDirectional.func_149895_l(i1);

        for (int k1 = 0; k1 <= 1; ++k1) {
            int l1 = i - BlockBed.field_149981_a[j1][0] * k1 - 1;
            int i2 = k - BlockBed.field_149981_a[j1][1] * k1 - 1;
            int j2 = l1 + 2;
            int k2 = i2 + 2;

            for (int l2 = l1; l2 <= j2; ++l2) {
                for (int i3 = i2; i3 <= k2; ++i3) {
                    if (World.func_147466_a(world, l2, j - 1, i3) && !world.func_147439_a(l2, j, i3).func_149688_o().func_76218_k() && !world.func_147439_a(l2, j + 1, i3).func_149688_o().func_76218_k()) {
                        if (l <= 0) {
                            return new ChunkCoordinates(l2, j, i3);
                        }

                        --l;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        if (!func_149975_b(l)) {
            super.func_149690_a(world, i, j, k, l, f, 0);
        }

    }

    @Override
    public int func_149656_h() {
        return 1;
    }

    @Override
    public void func_149681_a(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {
        if (entityplayer.field_71075_bZ.field_75098_d && func_149975_b(l)) {
            int i1 = func_149895_l(l);

            i -= BlockBed.field_149981_a[i1][0];
            k -= BlockBed.field_149981_a[i1][1];
            if (world.func_147439_a(i, j, k) == this) {
                world.func_147468_f(i, j, k);
            }
        }

    }
}
