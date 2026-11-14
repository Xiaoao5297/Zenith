package net.minecraft.block;

public class Block$SoundType {

    public final String field_150501_a;
    public final float field_150499_b;
    public final float field_150500_c;

    public Block$SoundType(String s, float f, float f1) {
        this.field_150501_a = s;
        this.field_150499_b = f;
        this.field_150500_c = f1;
    }

    public float func_150497_c() {
        return this.field_150499_b;
    }

    public float func_150494_d() {
        return this.field_150500_c;
    }

    public String func_150495_a() {
        return "dig." + this.field_150501_a;
    }

    public String func_150498_e() {
        return "step." + this.field_150501_a;
    }

    public String func_150496_b() {
        return this.func_150495_a();
    }
}
