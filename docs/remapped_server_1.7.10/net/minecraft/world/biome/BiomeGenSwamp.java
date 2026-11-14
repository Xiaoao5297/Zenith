package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenSwamp extends BiomeGenBase {

    protected BiomeGenSwamp(int i) {
        super(i);
        this.field_76760_I.field_76832_z = 2;
        this.field_76760_I.field_76802_A = 1;
        this.field_76760_I.field_76804_C = 1;
        this.field_76760_I.field_76798_D = 8;
        this.field_76760_I.field_76799_E = 10;
        this.field_76760_I.field_76806_I = 1;
        this.field_76760_I.field_76833_y = 4;
        this.field_76760_I.field_76805_H = 0;
        this.field_76760_I.field_76801_G = 0;
        this.field_76760_I.field_76803_B = 5;
        this.field_76759_H = 14745518;
        this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntitySlime.class, 1, 1, 1));
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return this.field_76763_Q;
    }

    @Override
    public String func_150572_a(Random random, int i, int j, int k) {
        return BlockFlower.field_149859_a[1];
    }

    @Override
    public void func_150573_a(World world, Random random, Block[] ablock, byte[] abyte, int i, int j, double d0) {
        double d1 = BiomeGenSwamp.field_150606_ad.func_151601_a((double) i * 0.25D, (double) j * 0.25D);

        if (d1 > 0.0D) {
            int k = i & 15;
            int l = j & 15;
            int i1 = ablock.length / 256;

            for (int j1 = 255; j1 >= 0; --j1) {
                int k1 = (l * 16 + k) * i1 + j1;

                if (ablock[k1] == null || ablock[k1].func_149688_o() != Material.field_151579_a) {
                    if (j1 == 62 && ablock[k1] != Blocks.field_150355_j) {
                        ablock[k1] = Blocks.field_150355_j;
                        if (d1 < 0.12D) {
                            ablock[k1 + 1] = Blocks.field_150392_bi;
                        }
                    }
                    break;
                }
            }
        }

        this.func_150560_b(world, random, ablock, abyte, i, j, d0);
    }
}
