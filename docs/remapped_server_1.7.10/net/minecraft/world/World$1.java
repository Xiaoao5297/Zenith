package net.minecraft.world;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;

class World$1 implements Callable {

    World$1(World world, int i, int j) {
        this.field_77485_a = world;
        this.field_151302_a = i;
        this.field_151301_b = j;
    }

    public String call() {
        return CrashReportCategory.func_85071_a(this.field_151302_a, 0, this.field_151301_b);
    }
}
