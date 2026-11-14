package net.minecraft.world;

public class ChunkCoordIntPair {

    public final int field_77276_a;
    public final int field_77275_b;

    public ChunkCoordIntPair(int i, int j) {
        this.field_77276_a = i;
        this.field_77275_b = j;
    }

    public static long func_77272_a(int i, int j) {
        return (long) i & 4294967295L | ((long) j & 4294967295L) << 32;
    }

    public int hashCode() {
        int i = 1664525 * this.field_77276_a + 1013904223;
        int j = 1664525 * (this.field_77275_b ^ -559038737) + 1013904223;

        return i ^ j;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChunkCoordIntPair)) {
            return false;
        } else {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) object;

            return this.field_77276_a == chunkcoordintpair.field_77276_a && this.field_77275_b == chunkcoordintpair.field_77275_b;
        }
    }

    public int func_77273_a() {
        return (this.field_77276_a << 4) + 8;
    }

    public int func_77274_b() {
        return (this.field_77275_b << 4) + 8;
    }

    public ChunkPosition func_151349_a(int i) {
        return new ChunkPosition(this.func_77273_a(), i, this.func_77274_b());
    }

    public String toString() {
        return "[" + this.field_77276_a + ", " + this.field_77275_b + "]";
    }
}
