package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.world.gen.layer.IntCache;

class CrashReport$8 implements Callable {

    CrashReport$8(CrashReport crashreport) {
        this.field_85086_a = crashreport;
    }

    public String call() {
        return IntCache.func_85144_b();
    }
}
