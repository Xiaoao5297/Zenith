package net.minecraft.util;

public class ChunkCoordinates implements Comparable {

    public int field_71574_a;
    public int field_71572_b;
    public int field_71573_c;

    public ChunkCoordinates() {}

    public ChunkCoordinates(int i, int j, int k) {
        this.field_71574_a = i;
        this.field_71572_b = j;
        this.field_71573_c = k;
    }

    public ChunkCoordinates(ChunkCoordinates chunkcoordinates) {
        this.field_71574_a = chunkcoordinates.field_71574_a;
        this.field_71572_b = chunkcoordinates.field_71572_b;
        this.field_71573_c = chunkcoordinates.field_71573_c;
    }

    public boolean equals(Object object) {
        if (!(object instanceof ChunkCoordinates)) {
            return false;
        } else {
            ChunkCoordinates chunkcoordinates = (ChunkCoordinates) object;

            return this.field_71574_a == chunkcoordinates.field_71574_a && this.field_71572_b == chunkcoordinates.field_71572_b && this.field_71573_c == chunkcoordinates.field_71573_c;
        }
    }

    public int hashCode() {
        return this.field_71574_a + this.field_71573_c << 8 + this.field_71572_b << 16;
    }

    public int compareTo(ChunkCoordinates chunkcoordinates) {
        return this.field_71572_b == chunkcoordinates.field_71572_b ? (this.field_71573_c == chunkcoordinates.field_71573_c ? this.field_71574_a - chunkcoordinates.field_71574_a : this.field_71573_c - chunkcoordinates.field_71573_c) : this.field_71572_b - chunkcoordinates.field_71572_b;
    }

    public void func_71571_b(int i, int j, int k) {
        this.field_71574_a = i;
        this.field_71572_b = j;
        this.field_71573_c = k;
    }

    public float func_71569_e(int i, int j, int k) {
        float f = (float) (this.field_71574_a - i);
        float f1 = (float) (this.field_71572_b - j);
        float f2 = (float) (this.field_71573_c - k);

        return f * f + f1 * f1 + f2 * f2;
    }

    public float func_82371_e(ChunkCoordinates chunkcoordinates) {
        return this.func_71569_e(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c);
    }

    public String toString() {
        return "Pos{x=" + this.field_71574_a + ", y=" + this.field_71572_b + ", z=" + this.field_71573_c + '}';
    }
}
