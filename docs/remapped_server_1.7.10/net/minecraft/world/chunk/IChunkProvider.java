package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public interface IChunkProvider {

    boolean func_73149_a(int i, int j);

    Chunk func_73154_d(int i, int j);

    Chunk func_73158_c(int i, int j);

    void func_73153_a(IChunkProvider ichunkprovider, int i, int j);

    boolean func_73151_a(boolean flag, IProgressUpdate iprogressupdate);

    boolean func_73156_b();

    boolean func_73157_c();

    String func_73148_d();

    List func_73155_a(EnumCreatureType enumcreaturetype, int i, int j, int k);

    ChunkPosition func_147416_a(World world, String s, int i, int j, int k);

    int func_73152_e();

    void func_82695_e(int i, int j);

    void func_104112_b();
}
