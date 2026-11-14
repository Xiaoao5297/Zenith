package net.minecraft.world;

import net.minecraft.block.Block;

public class NextTickListEntry implements Comparable {

    private static long field_77177_f;
    private final Block field_151352_g;
    public int field_77183_a;
    public int field_77181_b;
    public int field_77182_c;
    public long field_77180_e;
    public int field_82754_f;
    private long field_77178_g;

    public NextTickListEntry(int i, int j, int k, Block block) {
        this.field_77178_g = (long) (NextTickListEntry.field_77177_f++);
        this.field_77183_a = i;
        this.field_77181_b = j;
        this.field_77182_c = k;
        this.field_151352_g = block;
    }

    public boolean equals(Object object) {
        if (!(object instanceof NextTickListEntry)) {
            return false;
        } else {
            NextTickListEntry nextticklistentry = (NextTickListEntry) object;

            return this.field_77183_a == nextticklistentry.field_77183_a && this.field_77181_b == nextticklistentry.field_77181_b && this.field_77182_c == nextticklistentry.field_77182_c && Block.func_149680_a(this.field_151352_g, nextticklistentry.field_151352_g);
        }
    }

    public int hashCode() {
        return (this.field_77183_a * 1024 * 1024 + this.field_77182_c * 1024 + this.field_77181_b) * 256;
    }

    public NextTickListEntry func_77176_a(long i) {
        this.field_77180_e = i;
        return this;
    }

    public void func_82753_a(int i) {
        this.field_82754_f = i;
    }

    public int compareTo(NextTickListEntry nextticklistentry) {
        return this.field_77180_e < nextticklistentry.field_77180_e ? -1 : (this.field_77180_e > nextticklistentry.field_77180_e ? 1 : (this.field_82754_f != nextticklistentry.field_82754_f ? this.field_82754_f - nextticklistentry.field_82754_f : (this.field_77178_g < nextticklistentry.field_77178_g ? -1 : (this.field_77178_g > nextticklistentry.field_77178_g ? 1 : 0))));
    }

    public String toString() {
        return Block.func_149682_b(this.field_151352_g) + ": (" + this.field_77183_a + ", " + this.field_77181_b + ", " + this.field_77182_c + "), " + this.field_77180_e + ", " + this.field_82754_f + ", " + this.field_77178_g;
    }

    public Block func_151351_a() {
        return this.field_151352_g;
    }
}
