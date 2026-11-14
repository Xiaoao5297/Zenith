package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGenDungeons extends WorldGenerator {

    private static final WeightedRandomChestContent[] field_111189_a = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 4, 10), new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151015_O, 0, 1, 4, 10), new WeightedRandomChestContent(Items.field_151016_H, 0, 1, 4, 10), new WeightedRandomChestContent(Items.field_151007_F, 0, 1, 4, 10), new WeightedRandomChestContent(Items.field_151133_ar, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151153_ao, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151137_ax, 0, 1, 4, 10), new WeightedRandomChestContent(Items.field_151096_cd, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151093_ce, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151057_cb, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 2), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)};

    public WorldGenDungeons() {}

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        byte b0 = 3;
        int l = random.nextInt(2) + 2;
        int i1 = random.nextInt(2) + 2;
        int j1 = 0;

        int k1;
        int l1;
        int i2;

        for (k1 = i - l - 1; k1 <= i + l + 1; ++k1) {
            for (l1 = j - 1; l1 <= j + b0 + 1; ++l1) {
                for (i2 = k - i1 - 1; i2 <= k + i1 + 1; ++i2) {
                    Material material = world.func_147439_a(k1, l1, i2).func_149688_o();

                    if (l1 == j - 1 && !material.func_76220_a()) {
                        return false;
                    }

                    if (l1 == j + b0 + 1 && !material.func_76220_a()) {
                        return false;
                    }

                    if ((k1 == i - l - 1 || k1 == i + l + 1 || i2 == k - i1 - 1 || i2 == k + i1 + 1) && l1 == j && world.func_147437_c(k1, l1, i2) && world.func_147437_c(k1, l1 + 1, i2)) {
                        ++j1;
                    }
                }
            }
        }

        if (j1 >= 1 && j1 <= 5) {
            for (k1 = i - l - 1; k1 <= i + l + 1; ++k1) {
                for (l1 = j + b0; l1 >= j - 1; --l1) {
                    for (i2 = k - i1 - 1; i2 <= k + i1 + 1; ++i2) {
                        if (k1 != i - l - 1 && l1 != j - 1 && i2 != k - i1 - 1 && k1 != i + l + 1 && l1 != j + b0 + 1 && i2 != k + i1 + 1) {
                            world.func_147468_f(k1, l1, i2);
                        } else if (l1 >= 0 && !world.func_147439_a(k1, l1 - 1, i2).func_149688_o().func_76220_a()) {
                            world.func_147468_f(k1, l1, i2);
                        } else if (world.func_147439_a(k1, l1, i2).func_149688_o().func_76220_a()) {
                            if (l1 == j - 1 && random.nextInt(4) != 0) {
                                world.func_147465_d(k1, l1, i2, Blocks.field_150341_Y, 0, 2);
                            } else {
                                world.func_147465_d(k1, l1, i2, Blocks.field_150347_e, 0, 2);
                            }
                        }
                    }
                }
            }

            k1 = 0;

            while (k1 < 2) {
                l1 = 0;

                while (true) {
                    if (l1 < 3) {
                        label101:
                        {
                            i2 = i + random.nextInt(l * 2 + 1) - l;
                            int j2 = k + random.nextInt(i1 * 2 + 1) - i1;

                            if (world.func_147437_c(i2, j, j2)) {
                                int k2 = 0;

                                if (world.func_147439_a(i2 - 1, j, j2).func_149688_o().func_76220_a()) {
                                    ++k2;
                                }

                                if (world.func_147439_a(i2 + 1, j, j2).func_149688_o().func_76220_a()) {
                                    ++k2;
                                }

                                if (world.func_147439_a(i2, j, j2 - 1).func_149688_o().func_76220_a()) {
                                    ++k2;
                                }

                                if (world.func_147439_a(i2, j, j2 + 1).func_149688_o().func_76220_a()) {
                                    ++k2;
                                }

                                if (k2 == 1) {
                                    world.func_147465_d(i2, j, j2, Blocks.field_150486_ae, 0, 2);
                                    WeightedRandomChestContent[] aweightedrandomchestcontent = WeightedRandomChestContent.func_92080_a(WorldGenDungeons.field_111189_a, Items.field_151134_bR.func_92114_b(random));
                                    TileEntityChest tileentitychest = (TileEntityChest) world.func_147438_o(i2, j, j2);

                                    if (tileentitychest != null) {
                                        WeightedRandomChestContent.func_76293_a(random, aweightedrandomchestcontent, tileentitychest, 8);
                                    }
                                    break label101;
                                }
                            }

                            ++l1;
                            continue;
                        }
                    }

                    ++k1;
                    break;
                }
            }

            world.func_147465_d(i, j, k, Blocks.field_150474_ac, 0, 2);
            TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) world.func_147438_o(i, j, k);

            if (tileentitymobspawner != null) {
                tileentitymobspawner.func_145881_a().func_98272_a(this.func_76543_b(random));
            } else {
                System.err.println("Failed to fetch mob spawner entity at (" + i + ", " + j + ", " + k + ")");
            }

            return true;
        } else {
            return false;
        }
    }

    private String func_76543_b(Random random) {
        int i = random.nextInt(4);

        return i == 0 ? "Skeleton" : (i == 1 ? "Zombie" : (i == 2 ? "Zombie" : (i == 3 ? "Spider" : "")));
    }
}
