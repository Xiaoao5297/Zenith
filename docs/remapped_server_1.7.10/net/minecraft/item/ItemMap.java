package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData$MapInfo;

public class ItemMap extends ItemMapBase {

    protected ItemMap() {
        this.func_77627_a(true);
    }

    public MapData func_77873_a(ItemStack itemstack, World world) {
        String s = "map_" + itemstack.func_77960_j();
        MapData mapdata = (MapData) world.func_72943_a(MapData.class, s);

        if (mapdata == null && !world.field_72995_K) {
            itemstack.func_77964_b(world.func_72841_b("map"));
            s = "map_" + itemstack.func_77960_j();
            mapdata = new MapData(s);
            mapdata.field_76197_d = 3;
            int i = 128 * (1 << mapdata.field_76197_d);

            mapdata.field_76201_a = Math.round((float) world.func_72912_H().func_76079_c() / (float) i) * i;
            mapdata.field_76199_b = Math.round((float) (world.func_72912_H().func_76074_e() / i)) * i;
            mapdata.field_76200_c = (byte) world.field_73011_w.field_76574_g;
            mapdata.func_76185_a();
            world.func_72823_a(s, mapdata);
        }

        return mapdata;
    }

    public void func_77872_a(World world, Entity entity, MapData mapdata) {
        if (world.field_73011_w.field_76574_g == mapdata.field_76200_c && entity instanceof EntityPlayer) {
            int i = 1 << mapdata.field_76197_d;
            int j = mapdata.field_76201_a;
            int k = mapdata.field_76199_b;
            int l = MathHelper.func_76128_c(entity.field_70165_t - (double) j) / i + 64;
            int i1 = MathHelper.func_76128_c(entity.field_70161_v - (double) k) / i + 64;
            int j1 = 128 / i;

            if (world.field_73011_w.field_76576_e) {
                j1 /= 2;
            }

            MapData$MapInfo mapdata$mapinfo = mapdata.func_82568_a((EntityPlayer) entity);

            ++mapdata$mapinfo.field_82569_d;

            for (int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
                if ((k1 & 15) == (mapdata$mapinfo.field_82569_d & 15)) {
                    int l1 = 255;
                    int i2 = 0;
                    double d0 = 0.0D;

                    for (int j2 = i1 - j1 - 1; j2 < i1 + j1; ++j2) {
                        if (k1 >= 0 && j2 >= -1 && k1 < 128 && j2 < 128) {
                            int k2 = k1 - l;
                            int l2 = j2 - i1;
                            boolean flag = k2 * k2 + l2 * l2 > (j1 - 2) * (j1 - 2);
                            int i3 = (j / i + k1 - 64) * i;
                            int j3 = (k / i + j2 - 64) * i;
                            HashMultiset hashmultiset = HashMultiset.create();
                            Chunk chunk = world.func_72938_d(i3, j3);

                            if (!chunk.func_76621_g()) {
                                int k3 = i3 & 15;
                                int l3 = j3 & 15;
                                int i4 = 0;
                                double d1 = 0.0D;
                                int j4;

                                if (world.field_73011_w.field_76576_e) {
                                    j4 = i3 + j3 * 231871;
                                    j4 = j4 * j4 * 31287121 + j4 * 11;
                                    if ((j4 >> 20 & 1) == 0) {
                                        hashmultiset.add(Blocks.field_150346_d.func_149728_f(0), 10);
                                    } else {
                                        hashmultiset.add(Blocks.field_150348_b.func_149728_f(0), 100);
                                    }

                                    d1 = 100.0D;
                                } else {
                                    for (j4 = 0; j4 < i; ++j4) {
                                        for (int k4 = 0; k4 < i; ++k4) {
                                            int l4 = chunk.func_76611_b(j4 + k3, k4 + l3) + 1;
                                            Block block = Blocks.field_150350_a;
                                            int i5 = 0;

                                            if (l4 > 1) {
                                                do {
                                                    --l4;
                                                    block = chunk.func_150810_a(j4 + k3, l4, k4 + l3);
                                                    i5 = chunk.func_76628_c(j4 + k3, l4, k4 + l3);
                                                } while (block.func_149728_f(i5) == MapColor.field_151660_b && l4 > 0);

                                                if (l4 > 0 && block.func_149688_o().func_76224_d()) {
                                                    int j5 = l4 - 1;

                                                    Block block1;

                                                    do {
                                                        block1 = chunk.func_150810_a(j4 + k3, j5--, k4 + l3);
                                                        ++i4;
                                                    } while (j5 > 0 && block1.func_149688_o().func_76224_d());
                                                }
                                            }

                                            d1 += (double) l4 / (double) (i * i);
                                            hashmultiset.add(block.func_149728_f(i5));
                                        }
                                    }
                                }

                                i4 /= i * i;
                                double d2 = (d1 - d0) * 4.0D / (double) (i + 4) + ((double) (k1 + j2 & 1) - 0.5D) * 0.4D;
                                byte b0 = 1;

                                if (d2 > 0.6D) {
                                    b0 = 2;
                                }

                                if (d2 < -0.6D) {
                                    b0 = 0;
                                }

                                MapColor mapcolor = (MapColor) Iterables.getFirst(Multisets.copyHighestCountFirst(hashmultiset), MapColor.field_151660_b);

                                if (mapcolor == MapColor.field_151662_n) {
                                    d2 = (double) i4 * 0.1D + (double) (k1 + j2 & 1) * 0.2D;
                                    b0 = 1;
                                    if (d2 < 0.5D) {
                                        b0 = 2;
                                    }

                                    if (d2 > 0.9D) {
                                        b0 = 0;
                                    }
                                }

                                d0 = d1;
                                if (j2 >= 0 && k2 * k2 + l2 * l2 < j1 * j1 && (!flag || (k1 + j2 & 1) != 0)) {
                                    byte b1 = mapdata.field_76198_e[k1 + j2 * 128];
                                    byte b2 = (byte) (mapcolor.field_76290_q * 4 + b0);

                                    if (b1 != b2) {
                                        if (l1 > j2) {
                                            l1 = j2;
                                        }

                                        if (i2 < j2) {
                                            i2 = j2;
                                        }

                                        mapdata.field_76198_e[k1 + j2 * 128] = b2;
                                    }
                                }
                            }
                        }
                    }

                    if (l1 <= i2) {
                        mapdata.func_76194_a(k1, l1, i2);
                    }
                }
            }

        }
    }

    @Override
    public void func_77663_a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        if (!world.field_72995_K) {
            MapData mapdata = this.func_77873_a(itemstack, world);

            if (entity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entity;

                mapdata.func_76191_a(entityplayer, itemstack);
            }

            if (flag) {
                this.func_77872_a(world, entity, mapdata);
            }

        }
    }

    @Override
    public Packet func_150911_c(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        byte[] abyte = this.func_77873_a(itemstack, world).func_76193_a(itemstack, world, entityplayer);

        return abyte == null ? null : new S34PacketMaps(itemstack.func_77960_j(), abyte);
    }

    @Override
    public void func_77622_d(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (itemstack.func_77942_o() && itemstack.func_77978_p().func_74767_n("map_is_scaling")) {
            MapData mapdata = Items.field_151098_aY.func_77873_a(itemstack, world);

            itemstack.func_77964_b(world.func_72841_b("map"));
            MapData mapdata1 = new MapData("map_" + itemstack.func_77960_j());

            mapdata1.field_76197_d = (byte) (mapdata.field_76197_d + 1);
            if (mapdata1.field_76197_d > 4) {
                mapdata1.field_76197_d = 4;
            }

            mapdata1.field_76201_a = mapdata.field_76201_a;
            mapdata1.field_76199_b = mapdata.field_76199_b;
            mapdata1.field_76200_c = mapdata.field_76200_c;
            mapdata1.func_76185_a();
            world.func_72823_a("map_" + itemstack.func_77960_j(), mapdata1);
        }

    }
}
