package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenJungle extends BiomeGenBase {

    private boolean field_150614_aC;

    public BiomeGenJungle(int i, boolean flag) {
        super(i);
        this.field_150614_aC = flag;
        if (flag) {
            this.field_76760_I.field_76832_z = 2;
        } else {
            this.field_76760_I.field_76832_z = 50;
        }

        this.field_76760_I.field_76803_B = 25;
        this.field_76760_I.field_76802_A = 4;
        if (!flag) {
            this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntityOcelot.class, 2, 1, 1));
        }

        this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityChicken.class, 10, 4, 4));
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return (WorldGenAbstractTree) (random.nextInt(10) == 0 ? this.field_76758_O : (random.nextInt(2) == 0 ? new WorldGenShrub(3, 0) : (!this.field_150614_aC && random.nextInt(3) == 0 ? new WorldGenMegaJungle(false, 10, 20, 3, 3) : new WorldGenTrees(false, 4 + random.nextInt(7), 3, 3, true))));
    }

    @Override
    public WorldGenerator func_76730_b(Random random) {
        return random.nextInt(4) == 0 ? new WorldGenTallGrass(Blocks.field_150329_H, 2) : new WorldGenTallGrass(Blocks.field_150329_H, 1);
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        super.func_76728_a(world, random, i, j);
        int k = i + random.nextInt(16) + 8;
        int l = j + random.nextInt(16) + 8;
        int i1 = random.nextInt(world.func_72976_f(k, l) * 2);

        (new WorldGenMelon()).func_76484_a(world, random, k, i1, l);
        WorldGenVines worldgenvines = new WorldGenVines();

        for (l = 0; l < 50; ++l) {
            i1 = i + random.nextInt(16) + 8;
            short short0 = 128;
            int j1 = j + random.nextInt(16) + 8;

            worldgenvines.func_76484_a(world, random, i1, short0, j1);
        }

    }
}
