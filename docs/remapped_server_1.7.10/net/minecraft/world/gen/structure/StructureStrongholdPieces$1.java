package net.minecraft.world.gen.structure;

final class StructureStrongholdPieces$1 extends StructureStrongholdPieces$PieceWeight {

    StructureStrongholdPieces$1(Class oclass, int i, int j) {
        super(oclass, i, j);
    }

    @Override
    public boolean func_75189_a(int i) {
        return super.func_75189_a(i) && i > 4;
    }
}
