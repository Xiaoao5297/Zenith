package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;

public class StructureNetherBridgePieces$Start extends StructureNetherBridgePieces$Crossing3 {

    public StructureNetherBridgePieces$PieceWeight field_74970_a;
    public List field_74968_b;
    public List field_74969_c;
    public ArrayList field_74967_d = new ArrayList();

    public StructureNetherBridgePieces$Start() {}

    public StructureNetherBridgePieces$Start(Random random, int i, int j) {
        super(random, i, j);
        this.field_74968_b = new ArrayList();
        StructureNetherBridgePieces$PieceWeight[] astructurenetherbridgepieces$pieceweight = StructureNetherBridgePieces.access$100();
        int k = astructurenetherbridgepieces$pieceweight.length;

        StructureNetherBridgePieces$PieceWeight structurenetherbridgepieces$pieceweight;
        int l;

        for (l = 0; l < k; ++l) {
            structurenetherbridgepieces$pieceweight = astructurenetherbridgepieces$pieceweight[l];
            structurenetherbridgepieces$pieceweight.field_78827_c = 0;
            this.field_74968_b.add(structurenetherbridgepieces$pieceweight);
        }

        this.field_74969_c = new ArrayList();
        astructurenetherbridgepieces$pieceweight = StructureNetherBridgePieces.access$200();
        k = astructurenetherbridgepieces$pieceweight.length;

        for (l = 0; l < k; ++l) {
            structurenetherbridgepieces$pieceweight = astructurenetherbridgepieces$pieceweight[l];
            structurenetherbridgepieces$pieceweight.field_78827_c = 0;
            this.field_74969_c.add(structurenetherbridgepieces$pieceweight);
        }

    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
    }
}
