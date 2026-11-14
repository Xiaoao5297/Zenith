package net.minecraft.util;

class LongHashMap$Entry {

    final long field_76150_a;
    Object field_76148_b;
    LongHashMap$Entry field_76149_c;
    final int field_76147_d;

    LongHashMap$Entry(int i, long j, Object object, LongHashMap$Entry longhashmap$entry) {
        this.field_76148_b = object;
        this.field_76149_c = longhashmap$entry;
        this.field_76150_a = j;
        this.field_76147_d = i;
    }

    public final long func_76146_a() {
        return this.field_76150_a;
    }

    public final Object func_76145_b() {
        return this.field_76148_b;
    }

    public final boolean equals(Object object) {
        if (!(object instanceof LongHashMap$Entry)) {
            return false;
        } else {
            LongHashMap$Entry longhashmap$entry = (LongHashMap$Entry) object;
            Long olong = this.func_76146_a();
            Long olong1 = longhashmap$entry.func_76146_a();

            if (olong == olong1 || olong != null && olong.equals(olong1)) {
                Object object1 = this.func_76145_b();
                Object object2 = longhashmap$entry.func_76145_b();

                if (object1 == object2 || object1 != null && object1.equals(object2)) {
                    return true;
                }
            }

            return false;
        }
    }

    public final int hashCode() {
        return LongHashMap.access$000(this.field_76150_a);
    }

    public final String toString() {
        return this.func_76146_a() + "=" + this.func_76145_b();
    }
}
