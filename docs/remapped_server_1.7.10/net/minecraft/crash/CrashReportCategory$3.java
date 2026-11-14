package net.minecraft.crash;

import java.util.concurrent.Callable;

final class CrashReportCategory$3 implements Callable {

    CrashReportCategory$3(int i, int j, int k) {
        this.field_85067_a = i;
        this.field_85065_b = j;
        this.field_85066_c = k;
    }

    public String call() {
        return CrashReportCategory.func_85071_a(this.field_85067_a, this.field_85065_b, this.field_85066_c);
    }
}
