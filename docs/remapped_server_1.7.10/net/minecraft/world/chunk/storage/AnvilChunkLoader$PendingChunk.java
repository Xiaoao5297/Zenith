package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;

class AnvilChunkLoader$PendingChunk {

    public final ChunkCoordIntPair field_76548_a;
    public final NBTTagCompound field_76547_b;

    public AnvilChunkLoader$PendingChunk(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        this.field_76548_a = chunkcoordintpair;
        this.field_76547_b = nbttagcompound;
    }
}
