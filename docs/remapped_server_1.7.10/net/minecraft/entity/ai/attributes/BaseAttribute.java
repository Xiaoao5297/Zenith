package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute {

    private final String field_111115_a;
    private final double field_111113_b;
    private boolean field_111114_c;

    protected BaseAttribute(String s, double d0) {
        this.field_111115_a = s;
        this.field_111113_b = d0;
        if (s == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
    }

    @Override
    public String func_111108_a() {
        return this.field_111115_a;
    }

    @Override
    public double func_111110_b() {
        return this.field_111113_b;
    }

    @Override
    public boolean func_111111_c() {
        return this.field_111114_c;
    }

    public BaseAttribute func_111112_a(boolean flag) {
        this.field_111114_c = flag;
        return this;
    }

    public int hashCode() {
        return this.field_111115_a.hashCode();
    }
}
