package net.minecraft.block;

import java.util.Random;
import net.minecraft.world.World;

public interface IGrowable {

    boolean func_149851_a(World world, int i, int j, int k, boolean flag);

    boolean func_149852_a(World world, Random random, int i, int j, int k);

    void func_149853_b(World world, Random random, int i, int j, int k);
}
