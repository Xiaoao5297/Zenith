package net.minecraft.world.gen.structure;

class StructureStrongholdPieces$PieceWeight {

    public Class field_75194_a;
    public final int field_75192_b;
    public int field_75193_c;
    public int field_75191_d;

    public StructureStrongholdPieces$PieceWeight(Class oclass, int i, int j) {
        this.field_75194_a = oclass;
        this.field_75192_b = i;
        this.field_75191_d = j;
    }

    public boolean func_75189_a(int i) {
        return this.field_75191_d == 0 || this.field_75193_c < this.field_75191_d;
    }

    public boolean func_75190_a() {
        return this.field_75191_d == 0 || this.field_75193_c < this.field_75191_d;
    }
}
