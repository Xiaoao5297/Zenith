package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.ChunkPosition;

public class StructureStrongholdPieces$Stairs2 extends StructureStrongholdPieces$Stairs {

    public StructureStrongholdPieces$PieceWeight field_75027_a;
    public StructureStrongholdPieces$PortalRoom field_75025_b;
    public List field_75026_c = new ArrayList();

    public StructureStrongholdPieces$Stairs2() {}

    public StructureStrongholdPieces$Stairs2(int i, Random random, int j, int k) {
        super(0, random, j, k);
    }

    @Override
    public ChunkPosition func_151553_a() {
        return this.field_75025_b != null ? this.field_75025_b.func_151553_a() : super.func_151553_a();
    }
}
