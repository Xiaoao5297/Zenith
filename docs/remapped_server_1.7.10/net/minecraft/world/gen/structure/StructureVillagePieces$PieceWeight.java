package net.minecraft.world.gen.structure;

public class StructureVillagePieces$PieceWeight {

    public Class field_75090_a;
    public final int field_75088_b;
    public int field_75089_c;
    public int field_75087_d;

    public StructureVillagePieces$PieceWeight(Class oclass, int i, int j) {
        this.field_75090_a = oclass;
        this.field_75088_b = i;
        this.field_75087_d = j;
    }

    public boolean func_75085_a(int i) {
        return this.field_75087_d == 0 || this.field_75089_c < this.field_75087_d;
    }

    public boolean func_75086_a() {
        return this.field_75087_d == 0 || this.field_75089_c < this.field_75087_d;
    }
}
