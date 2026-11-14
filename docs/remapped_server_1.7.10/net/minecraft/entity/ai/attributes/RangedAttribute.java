package net.minecraft.entity.ai.attributes;

public class RangedAttribute extends BaseAttribute {

    private final double field_111120_a;
    private final double field_111118_b;
    private String field_111119_c;

    public RangedAttribute(String s, double d0, double d1, double d2) {
        super(s, d0);
        this.field_111120_a = d1;
        this.field_111118_b = d2;
        if (d1 > d2) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        } else if (d0 < d1) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        } else if (d0 > d2) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    public RangedAttribute func_111117_a(String s) {
        this.field_111119_c = s;
        return this;
    }

    public String func_111116_f() {
        return this.field_111119_c;
    }

    @Override
    public double func_111109_a(double d0) {
        if (d0 < this.field_111120_a) {
            d0 = this.field_111120_a;
        }

        if (d0 > this.field_111118_b) {
            d0 = this.field_111118_b;
        }

        return d0;
    }
}
