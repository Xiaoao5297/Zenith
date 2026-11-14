package net.minecraft.world.gen;

import net.minecraft.block.Block;

public class FlatLayerInfo {

    private Block field_151537_a;
    private int field_82664_a;
    private int field_82663_c;
    private int field_82661_d;

    public FlatLayerInfo(int i, Block block) {
        this.field_82664_a = 1;
        this.field_82664_a = i;
        this.field_151537_a = block;
    }

    public FlatLayerInfo(int i, Block block, int j) {
        this(i, block);
        this.field_82663_c = j;
    }

    public int func_82657_a() {
        return this.field_82664_a;
    }

    public Block func_151536_b() {
        return this.field_151537_a;
    }

    public int func_82658_c() {
        return this.field_82663_c;
    }

    public int func_82656_d() {
        return this.field_82661_d;
    }

    public void func_82660_d(int i) {
        this.field_82661_d = i;
    }

    public String toString() {
        String s = Integer.toString(Block.func_149682_b(this.field_151537_a));

        if (this.field_82664_a > 1) {
            s = this.field_82664_a + "x" + s;
        }

        if (this.field_82663_c > 0) {
            s = s + ":" + this.field_82663_c;
        }

        return s;
    }
}
