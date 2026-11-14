package net.minecraft.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase$SpawnListEntry;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerAnimals {

    private HashMap field_77193_b = new HashMap();

    public SpawnerAnimals() {}

    protected static ChunkPosition func_151350_a(World world, int i, int j) {
        Chunk chunk = world.func_72964_e(i, j);
        int k = i * 16 + world.field_73012_v.nextInt(16);
        int l = j * 16 + world.field_73012_v.nextInt(16);
        int i1 = world.field_73012_v.nextInt(chunk == null ? world.func_72940_L() : chunk.func_76625_h() + 16 - 1);

        return new ChunkPosition(k, i1, l);
    }

    public int func_77192_a(WorldServer worldserver, boolean flag, boolean flag1, boolean flag2) {
        if (!flag && !flag1) {
            return 0;
        } else {
            this.field_77193_b.clear();

            int i;
            int j;
            int k;

            for (i = 0; i < worldserver.field_73010_i.size(); ++i) {
                EntityPlayer entityplayer = (EntityPlayer) worldserver.field_73010_i.get(i);
                int l = MathHelper.func_76128_c(entityplayer.field_70165_t / 16.0D);

                j = MathHelper.func_76128_c(entityplayer.field_70161_v / 16.0D);
                k = 8;

                for (int i1 = -k; i1 <= k; ++i1) {
                    for (int j1 = -k; j1 <= k; ++j1) {
                        boolean flag3 = i1 == -k || i1 == k || j1 == -k || j1 == k;
                        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i1 + l, j1 + j);

                        if (!flag3) {
                            this.field_77193_b.put(chunkcoordintpair, false);
                        } else if (!this.field_77193_b.containsKey(chunkcoordintpair)) {
                            this.field_77193_b.put(chunkcoordintpair, true);
                        }
                    }
                }
            }

            i = 0;
            ChunkCoordinates chunkcoordinates = worldserver.func_72861_E();
            EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();

            j = aenumcreaturetype.length;

            for (k = 0; k < j; ++k) {
                EnumCreatureType enumcreaturetype = aenumcreaturetype[k];

                if ((!enumcreaturetype.func_75599_d() || flag1) && (enumcreaturetype.func_75599_d() || flag) && (!enumcreaturetype.func_82705_e() || flag2) && worldserver.func_72907_a(enumcreaturetype.func_75598_a()) <= enumcreaturetype.func_75601_b() * this.field_77193_b.size() / 256) {
                    Iterator iterator = this.field_77193_b.keySet().iterator();

                    label110:
                    while (iterator.hasNext()) {
                        ChunkCoordIntPair chunkcoordintpair1 = (ChunkCoordIntPair) iterator.next();

                        if (!(Boolean) this.field_77193_b.get(chunkcoordintpair1)) {
                            ChunkPosition chunkposition = func_151350_a(worldserver, chunkcoordintpair1.field_77276_a, chunkcoordintpair1.field_77275_b);
                            int k1 = chunkposition.field_151329_a;
                            int l1 = chunkposition.field_151327_b;
                            int i2 = chunkposition.field_151328_c;

                            if (!worldserver.func_147439_a(k1, l1, i2).func_149721_r() && worldserver.func_147439_a(k1, l1, i2).func_149688_o() == enumcreaturetype.func_75600_c()) {
                                int j2 = 0;
                                int k2 = 0;

                                while (k2 < 3) {
                                    int l2 = k1;
                                    int i3 = l1;
                                    int j3 = i2;
                                    byte b0 = 6;
                                    BiomeGenBase$SpawnListEntry biomegenbase$spawnlistentry = null;
                                    IEntityLivingData ientitylivingdata = null;
                                    int k3 = 0;

                                    while (true) {
                                        if (k3 < 4) {
                                            label103:
                                            {
                                                l2 += worldserver.field_73012_v.nextInt(b0) - worldserver.field_73012_v.nextInt(b0);
                                                i3 += worldserver.field_73012_v.nextInt(1) - worldserver.field_73012_v.nextInt(1);
                                                j3 += worldserver.field_73012_v.nextInt(b0) - worldserver.field_73012_v.nextInt(b0);
                                                if (func_77190_a(enumcreaturetype, worldserver, l2, i3, j3)) {
                                                    float f = (float) l2 + 0.5F;
                                                    float f1 = (float) i3;
                                                    float f2 = (float) j3 + 0.5F;

                                                    if (worldserver.func_72977_a((double) f, (double) f1, (double) f2, 24.0D) == null) {
                                                        float f3 = f - (float) chunkcoordinates.field_71574_a;
                                                        float f4 = f1 - (float) chunkcoordinates.field_71572_b;
                                                        float f5 = f2 - (float) chunkcoordinates.field_71573_c;
                                                        float f6 = f3 * f3 + f4 * f4 + f5 * f5;

                                                        if (f6 >= 576.0F) {
                                                            if (biomegenbase$spawnlistentry == null) {
                                                                biomegenbase$spawnlistentry = worldserver.func_73057_a(enumcreaturetype, l2, i3, j3);
                                                                if (biomegenbase$spawnlistentry == null) {
                                                                    break label103;
                                                                }
                                                            }

                                                            EntityLiving entityliving;

                                                            try {
                                                                entityliving = (EntityLiving) biomegenbase$spawnlistentry.field_76300_b.getConstructor(World.class).newInstance(worldserver);
                                                            } catch (Exception exception) {
                                                                exception.printStackTrace();
                                                                return i;
                                                            }

                                                            entityliving.func_70012_b((double) f, (double) f1, (double) f2, worldserver.field_73012_v.nextFloat() * 360.0F, 0.0F);
                                                            if (entityliving.func_70601_bi()) {
                                                                ++j2;
                                                                worldserver.func_72838_d(entityliving);
                                                                ientitylivingdata = entityliving.func_110161_a(ientitylivingdata);
                                                                if (j2 >= entityliving.func_70641_bl()) {
                                                                    continue label110;
                                                                }
                                                            }

                                                            i += j2;
                                                        }
                                                    }
                                                }

                                                ++k3;
                                                continue;
                                            }
                                        }

                                        ++k2;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return i;
        }
    }

    public static boolean func_77190_a(EnumCreatureType enumcreaturetype, World world, int i, int j, int k) {
        if (enumcreaturetype.func_75600_c() == Material.field_151586_h) {
            return world.func_147439_a(i, j, k).func_149688_o().func_76224_d() && world.func_147439_a(i, j - 1, k).func_149688_o().func_76224_d() && !world.func_147439_a(i, j + 1, k).func_149721_r();
        } else if (!World.func_147466_a(world, i, j - 1, k)) {
            return false;
        } else {
            Block block = world.func_147439_a(i, j - 1, k);

            return block != Blocks.field_150357_h && !world.func_147439_a(i, j, k).func_149721_r() && !world.func_147439_a(i, j, k).func_149688_o().func_76224_d() && !world.func_147439_a(i, j + 1, k).func_149721_r();
        }
    }

    public static void func_77191_a(World world, BiomeGenBase biomegenbase, int i, int j, int k, int l, Random random) {
        List list = biomegenbase.func_76747_a(EnumCreatureType.creature);

        if (!list.isEmpty()) {
            while (random.nextFloat() < biomegenbase.func_76741_f()) {
                BiomeGenBase$SpawnListEntry biomegenbase$spawnlistentry = (BiomeGenBase$SpawnListEntry) WeightedRandom.func_76271_a(world.field_73012_v, list);
                IEntityLivingData ientitylivingdata = null;
                int i1 = biomegenbase$spawnlistentry.field_76301_c + random.nextInt(1 + biomegenbase$spawnlistentry.field_76299_d - biomegenbase$spawnlistentry.field_76301_c);
                int j1 = i + random.nextInt(k);
                int k1 = j + random.nextInt(l);
                int l1 = j1;
                int i2 = k1;

                for (int j2 = 0; j2 < i1; ++j2) {
                    boolean flag = false;

                    for (int k2 = 0; !flag && k2 < 4; ++k2) {
                        int l2 = world.func_72825_h(j1, k1);

                        if (func_77190_a(EnumCreatureType.creature, world, j1, l2, k1)) {
                            float f = (float) j1 + 0.5F;
                            float f1 = (float) l2;
                            float f2 = (float) k1 + 0.5F;

                            EntityLiving entityliving;

                            try {
                                entityliving = (EntityLiving) biomegenbase$spawnlistentry.field_76300_b.getConstructor(World.class).newInstance(world);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                continue;
                            }

                            entityliving.func_70012_b((double) f, (double) f1, (double) f2, random.nextFloat() * 360.0F, 0.0F);
                            world.func_72838_d(entityliving);
                            ientitylivingdata = entityliving.func_110161_a(ientitylivingdata);
                            flag = true;
                        }

                        j1 += random.nextInt(5) - random.nextInt(5);

                        for (k1 += random.nextInt(5) - random.nextInt(5); j1 < i || j1 >= i + k || k1 < j || k1 >= j + k; k1 = i2 + random.nextInt(5) - random.nextInt(5)) {
                            j1 = l1 + random.nextInt(5) - random.nextInt(5);
                        }
                    }
                }
            }

        }
    }
}
