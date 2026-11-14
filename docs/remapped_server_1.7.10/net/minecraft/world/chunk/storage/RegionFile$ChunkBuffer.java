package net.minecraft.world.chunk.storage;

import java.io.ByteArrayOutputStream;

class RegionFile$ChunkBuffer extends ByteArrayOutputStream {

    private int field_76722_b;
    private int field_76723_c;

    public RegionFile$ChunkBuffer(RegionFile regionfile, int i, int j) {
        super(8096);
        this.field_76724_a = regionfile;
        this.field_76722_b = i;
        this.field_76723_c = j;
    }

    public void close() {
        this.field_76724_a.func_76706_a(this.field_76722_b, this.field_76723_c, this.buf, this.count);
    }
}
