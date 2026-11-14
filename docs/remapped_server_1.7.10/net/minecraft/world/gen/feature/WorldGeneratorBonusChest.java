package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator {

    private final WeightedRandomChestContent[] field_76546_a;
    private final int field_76545_b;

    public WorldGeneratorBonusChest(WeightedRandomChestContent[] aweightedrandomchestcontent, int i) {
        this.field_76546_a = aweightedrandomchestcontent;
        this.field_76545_b = i;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        Block block;

        while (((block = world.func_147439_a(i, j, k)).func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) && j > 1) {
            --j;
        }

        if (j < 1) {
            return false;
        } else {
            ++j;

            for (int l = 0; l < 4; ++l) {
                int i1 = i + random.nextInt(4) - random.nextInt(4);
                int j1 = j + random.nextInt(3) - random.nextInt(3);
                int k1 = k + random.nextInt(4) - random.nextInt(4);

                if (world.func_147437_c(i1, j1, k1) && World.func_147466_a(world, i1, j1 - 1, k1)) {
                    world.func_147465_d(i1, j1, k1, Blocks.field_150486_ae, 0, 2);
                    TileEntityChest tileentitychest = (TileEntityChest) world.func_147438_o(i1, j1, k1);

                    if (tileentitychest != null && tileentitychest != null) {
                        WeightedRandomChestContent.func_76293_a(random, this.field_76546_a, tileentitychest, this.field_76545_b);
                    }

                    if (world.func_147437_c(i1 - 1, j1, k1) && World.func_147466_a(world, i1 - 1, j1 - 1, k1)) {
                        world.func_147465_d(i1 - 1, j1, k1, Blocks.field_150478_aa, 0, 2);
                    }

                    if (world.func_147437_c(i1 + 1, j1, k1) && World.func_147466_a(world, i1 - 1, j1 - 1, k1)) {
                        world.func_147465_d(i1 + 1, j1, k1, Blocks.field_150478_aa, 0, 2);
                    }

                    if (world.func_147437_c(i1, j1, k1 - 1) && World.func_147466_a(world, i1 - 1, j1 - 1, k1)) {
                        world.func_147465_d(i1, j1, k1 - 1, Blocks.field_150478_aa, 0, 2);
                    }

                    if (world.func_147437_c(i1, j1, k1 + 1) && World.func_147466_a(world, i1 - 1, j1 - 1, k1)) {
                        world.func_147465_d(i1, j1, k1 + 1, Blocks.field_150478_aa, 0, 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}
