package net.minecraft.world.chunk.storage;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IChunkLoader {

    Chunk func_75815_a(World world, int i, int j);

    void func_75816_a(World world, Chunk chunk);

    void func_75819_b(World world, Chunk chunk);

    void func_75817_a();

    void func_75818_b();
}
