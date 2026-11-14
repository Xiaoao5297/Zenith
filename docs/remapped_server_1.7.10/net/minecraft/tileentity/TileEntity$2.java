package net.minecraft.tileentity;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;

class TileEntity$2 implements Callable {

    TileEntity$2(TileEntity tileentity) {
        this.field_150832_a = tileentity;
    }

    public String call() {
        int i = Block.func_149682_b(this.field_150832_a.field_145850_b.func_147439_a(this.field_150832_a.field_145851_c, this.field_150832_a.field_145848_d, this.field_150832_a.field_145849_e));

        try {
            return String.format("ID #%d (%s // %s)", i, Block.func_149729_e(i).func_149739_a(), Block.func_149729_e(i).getClass().getCanonicalName());
        } catch (Throwable throwable) {
            return "ID #" + i;
        }
    }
}
