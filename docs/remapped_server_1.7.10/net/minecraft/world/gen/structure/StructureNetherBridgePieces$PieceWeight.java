package net.minecraft.world.gen.structure;

class StructureNetherBridgePieces$PieceWeight {

    public Class field_78828_a;
    public final int field_78826_b;
    public int field_78827_c;
    public int field_78824_d;
    public boolean field_78825_e;

    public StructureNetherBridgePieces$PieceWeight(Class oclass, int i, int j, boolean flag) {
        this.field_78828_a = oclass;
        this.field_78826_b = i;
        this.field_78824_d = j;
        this.field_78825_e = flag;
    }

    public StructureNetherBridgePieces$PieceWeight(Class oclass, int i, int j) {
        this(oclass, i, j, false);
    }

    public boolean func_78822_a(int i) {
        return this.field_78824_d == 0 || this.field_78827_c < this.field_78824_d;
    }

    public boolean func_78823_a() {
        return this.field_78824_d == 0 || this.field_78827_c < this.field_78824_d;
    }
}
