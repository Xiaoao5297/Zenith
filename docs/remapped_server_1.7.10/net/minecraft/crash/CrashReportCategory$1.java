package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;

final class CrashReportCategory$1 implements Callable {

    CrashReportCategory$1(int i, Block block) {
        this.field_85080_a = i;
        this.field_147151_b = block;
    }

    public String call() {
        try {
            return String.format("ID #%d (%s // %s)", this.field_85080_a, this.field_147151_b.func_149739_a(), this.field_147151_b.getClass().getCanonicalName());
        } catch (Throwable throwable) {
            return "ID #" + this.field_85080_a;
        }
    }
}
