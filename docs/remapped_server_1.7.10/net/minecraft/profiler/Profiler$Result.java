package net.minecraft.profiler;

public final class Profiler$Result implements Comparable {

    public double field_76332_a;
    public double field_76330_b;
    public String field_76331_c;

    public Profiler$Result(String s, double d0, double d1) {
        this.field_76331_c = s;
        this.field_76332_a = d0;
        this.field_76330_b = d1;
    }

    public int compareTo(Profiler$Result profiler$result) {
        return profiler$result.field_76332_a < this.field_76332_a ? -1 : (profiler$result.field_76332_a > this.field_76332_a ? 1 : profiler$result.field_76331_c.compareTo(this.field_76331_c));
    }
}
