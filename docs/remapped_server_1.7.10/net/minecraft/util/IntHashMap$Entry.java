package net.minecraft.util;

class IntHashMap$Entry {

    final int field_76035_a;
    Object field_76033_b;
    IntHashMap$Entry field_76034_c;
    final int field_76032_d;

    IntHashMap$Entry(int i, int j, Object object, IntHashMap$Entry inthashmap$entry) {
        this.field_76033_b = object;
        this.field_76034_c = inthashmap$entry;
        this.field_76035_a = j;
        this.field_76032_d = i;
    }

    public final int func_76031_a() {
        return this.field_76035_a;
    }

    public final Object func_76030_b() {
        return this.field_76033_b;
    }

    public final boolean equals(Object object) {
        if (!(object instanceof IntHashMap$Entry)) {
            return false;
        } else {
            IntHashMap$Entry inthashmap$entry = (IntHashMap$Entry) object;
            Integer integer = this.func_76031_a();
            Integer integer1 = inthashmap$entry.func_76031_a();

            if (integer == integer1 || integer != null && integer.equals(integer1)) {
                Object object1 = this.func_76030_b();
                Object object2 = inthashmap$entry.func_76030_b();

                if (object1 == object2 || object1 != null && object1.equals(object2)) {
                    return true;
                }
            }

            return false;
        }
    }

    public final int hashCode() {
        return IntHashMap.access$000(this.field_76035_a);
    }

    public final String toString() {
        return this.func_76031_a() + "=" + this.func_76030_b();
    }
}
