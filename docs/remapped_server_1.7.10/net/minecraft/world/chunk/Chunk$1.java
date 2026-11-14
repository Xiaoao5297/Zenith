package net.minecraft.world.chunk;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;

class Chunk$1 implements Callable {

    Chunk$1(Chunk chunk, int i, int j, int k) {
        this.field_150821_d = chunk;
        this.field_150824_a = i;
        this.field_150822_b = j;
        this.field_150823_c = k;
    }

    public String call() {
        return CrashReportCategory.func_85071_a(this.field_150824_a, this.field_150822_b, this.field_150823_c);
    }
}
