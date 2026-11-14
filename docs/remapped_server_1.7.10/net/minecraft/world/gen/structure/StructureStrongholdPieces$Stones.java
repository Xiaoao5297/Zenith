package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.init.Blocks;

class StructureStrongholdPieces$Stones extends StructureComponent$BlockSelector {

    private StructureStrongholdPieces$Stones() {}

    @Override
    public void func_75062_a(Random random, int i, int j, int k, boolean flag) {
        if (flag) {
            this.field_151562_a = Blocks.field_150417_aV;
            float f = random.nextFloat();

            if (f < 0.2F) {
                this.field_75065_b = 2;
            } else if (f < 0.5F) {
                this.field_75065_b = 1;
            } else if (f < 0.55F) {
                this.field_151562_a = Blocks.field_150418_aU;
                this.field_75065_b = 2;
            } else {
                this.field_75065_b = 0;
            }
        } else {
            this.field_151562_a = Blocks.field_150350_a;
            this.field_75065_b = 0;
        }

    }
}
