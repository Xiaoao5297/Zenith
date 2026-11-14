package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;

public class StructureNetherBridgePieces {

    private static final StructureNetherBridgePieces$PieceWeight[] field_78742_a = new StructureNetherBridgePieces$PieceWeight[]{new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Straight.class, 30, 0, true), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Crossing3.class, 10, 4), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Crossing.class, 10, 4), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Stairs.class, 10, 3), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Throne.class, 5, 2), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Entrance.class, 5, 1)};
    private static final StructureNetherBridgePieces$PieceWeight[] field_78741_b = new StructureNetherBridgePieces$PieceWeight[]{new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor5.class, 25, 0, true), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Crossing2.class, 15, 5), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor2.class, 5, 10), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor.class, 5, 10), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor3.class, 10, 3, true), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$Corridor4.class, 7, 2), new StructureNetherBridgePieces$PieceWeight(StructureNetherBridgePieces$NetherStalkRoom.class, 5, 2)};

    public static void func_143049_a() {
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Crossing3.class, "NeBCr");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$End.class, "NeBEF");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Straight.class, "NeBS");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor3.class, "NeCCS");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor4.class, "NeCTB");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Entrance.class, "NeCE");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Crossing2.class, "NeSCSC");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor.class, "NeSCLT");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor5.class, "NeSC");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Corridor2.class, "NeSCRT");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$NetherStalkRoom.class, "NeCSR");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Throne.class, "NeMT");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Crossing.class, "NeRC");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Stairs.class, "NeSR");
        MapGenStructureIO.func_143031_a(StructureNetherBridgePieces$Start.class, "NeStart");
    }

    private static StructureNetherBridgePieces$Piece func_78738_b(StructureNetherBridgePieces$PieceWeight structurenetherbridgepieces$pieceweight, List list, Random random, int i, int j, int k, int l, int i1) {
        Class oclass = structurenetherbridgepieces$pieceweight.field_78828_a;
        Object object = null;

        if (oclass == StructureNetherBridgePieces$Straight.class) {
            object = StructureNetherBridgePieces$Straight.func_74983_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Crossing3.class) {
            object = StructureNetherBridgePieces$Crossing3.func_74966_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Crossing.class) {
            object = StructureNetherBridgePieces$Crossing.func_74974_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Stairs.class) {
            object = StructureNetherBridgePieces$Stairs.func_74973_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Throne.class) {
            object = StructureNetherBridgePieces$Throne.func_74975_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Entrance.class) {
            object = StructureNetherBridgePieces$Entrance.func_74984_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Corridor5.class) {
            object = StructureNetherBridgePieces$Corridor5.func_74981_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Corridor2.class) {
            object = StructureNetherBridgePieces$Corridor2.func_74980_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Corridor.class) {
            object = StructureNetherBridgePieces$Corridor.func_74978_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Corridor3.class) {
            object = StructureNetherBridgePieces$Corridor3.func_74982_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Corridor4.class) {
            object = StructureNetherBridgePieces$Corridor4.func_74985_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$Crossing2.class) {
            object = StructureNetherBridgePieces$Crossing2.func_74979_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureNetherBridgePieces$NetherStalkRoom.class) {
            object = StructureNetherBridgePieces$NetherStalkRoom.func_74977_a(list, random, i, j, k, l, i1);
        }

        return (StructureNetherBridgePieces$Piece) object;
    }
}
