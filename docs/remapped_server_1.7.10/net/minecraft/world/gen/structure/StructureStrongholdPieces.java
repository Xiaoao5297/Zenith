package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class StructureStrongholdPieces {

    private static final StructureStrongholdPieces$PieceWeight[] field_75205_b = new StructureStrongholdPieces$PieceWeight[]{new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$Straight.class, 40, 0), new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$Prison.class, 5, 5), new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$LeftTurn.class, 20, 0), new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$RightTurn.class, 20, 0), new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$RoomCrossing.class, 10, 6), new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$StairsStraight.class, 5, 5), new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$Stairs.class, 5, 5), new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$Crossing.class, 5, 4), new StructureStrongholdPieces$PieceWeight(StructureStrongholdPieces$ChestCorridor.class, 5, 4), new StructureStrongholdPieces$1(StructureStrongholdPieces$Library.class, 10, 2), new StructureStrongholdPieces$2(StructureStrongholdPieces$PortalRoom.class, 20, 1)};
    private static List field_75206_c;
    private static Class field_75203_d;
    static int field_75207_a;
    private static final StructureStrongholdPieces$Stones field_75204_e = new StructureStrongholdPieces$Stones((StructureStrongholdPieces$1) null);

    public static void func_143046_a() {
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$ChestCorridor.class, "SHCC");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Corridor.class, "SHFC");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Crossing.class, "SH5C");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$LeftTurn.class, "SHLT");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Library.class, "SHLi");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$PortalRoom.class, "SHPR");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Prison.class, "SHPH");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$RightTurn.class, "SHRT");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$RoomCrossing.class, "SHRC");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Stairs.class, "SHSD");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Stairs2.class, "SHStart");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$Straight.class, "SHS");
        MapGenStructureIO.func_143031_a(StructureStrongholdPieces$StairsStraight.class, "SHSSD");
    }

    public static void func_75198_a() {
        StructureStrongholdPieces.field_75206_c = new ArrayList();
        StructureStrongholdPieces$PieceWeight[] astructurestrongholdpieces$pieceweight = StructureStrongholdPieces.field_75205_b;
        int i = astructurestrongholdpieces$pieceweight.length;

        for (int j = 0; j < i; ++j) {
            StructureStrongholdPieces$PieceWeight structurestrongholdpieces$pieceweight = astructurestrongholdpieces$pieceweight[j];

            structurestrongholdpieces$pieceweight.field_75193_c = 0;
            StructureStrongholdPieces.field_75206_c.add(structurestrongholdpieces$pieceweight);
        }

        StructureStrongholdPieces.field_75203_d = null;
    }

    private static boolean func_75202_c() {
        boolean flag = false;

        StructureStrongholdPieces.field_75207_a = 0;

        StructureStrongholdPieces$PieceWeight structurestrongholdpieces$pieceweight;

        for (Iterator iterator = StructureStrongholdPieces.field_75206_c.iterator(); iterator.hasNext(); StructureStrongholdPieces.field_75207_a += structurestrongholdpieces$pieceweight.field_75192_b) {
            structurestrongholdpieces$pieceweight = (StructureStrongholdPieces$PieceWeight) iterator.next();
            if (structurestrongholdpieces$pieceweight.field_75191_d > 0 && structurestrongholdpieces$pieceweight.field_75193_c < structurestrongholdpieces$pieceweight.field_75191_d) {
                flag = true;
            }
        }

        return flag;
    }

    private static StructureStrongholdPieces$Stronghold func_75200_a(Class oclass, List list, Random random, int i, int j, int k, int l, int i1) {
        Object object = null;

        if (oclass == StructureStrongholdPieces$Straight.class) {
            object = StructureStrongholdPieces$Straight.func_75018_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$Prison.class) {
            object = StructureStrongholdPieces$Prison.func_75016_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$LeftTurn.class) {
            object = StructureStrongholdPieces$LeftTurn.func_75010_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$RightTurn.class) {
            object = StructureStrongholdPieces$RightTurn.func_75010_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$RoomCrossing.class) {
            object = StructureStrongholdPieces$RoomCrossing.func_75012_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$StairsStraight.class) {
            object = StructureStrongholdPieces$StairsStraight.func_75028_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$Stairs.class) {
            object = StructureStrongholdPieces$Stairs.func_75022_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$Crossing.class) {
            object = StructureStrongholdPieces$Crossing.func_74994_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$ChestCorridor.class) {
            object = StructureStrongholdPieces$ChestCorridor.func_75000_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$Library.class) {
            object = StructureStrongholdPieces$Library.func_75006_a(list, random, i, j, k, l, i1);
        } else if (oclass == StructureStrongholdPieces$PortalRoom.class) {
            object = StructureStrongholdPieces$PortalRoom.func_75004_a(list, random, i, j, k, l, i1);
        }

        return (StructureStrongholdPieces$Stronghold) object;
    }

    private static StructureStrongholdPieces$Stronghold func_75201_b(StructureStrongholdPieces$Stairs2 structurestrongholdpieces$stairs2, List list, Random random, int i, int j, int k, int l, int i1) {
        if (!func_75202_c()) {
            return null;
        } else {
            if (StructureStrongholdPieces.field_75203_d != null) {
                StructureStrongholdPieces$Stronghold structurestrongholdpieces$stronghold = func_75200_a(StructureStrongholdPieces.field_75203_d, list, random, i, j, k, l, i1);

                StructureStrongholdPieces.field_75203_d = null;
                if (structurestrongholdpieces$stronghold != null) {
                    return structurestrongholdpieces$stronghold;
                }
            }

            int j1 = 0;

            while (j1 < 5) {
                ++j1;
                int k1 = random.nextInt(StructureStrongholdPieces.field_75207_a);
                Iterator iterator = StructureStrongholdPieces.field_75206_c.iterator();

                while (iterator.hasNext()) {
                    StructureStrongholdPieces$PieceWeight structurestrongholdpieces$pieceweight = (StructureStrongholdPieces$PieceWeight) iterator.next();

                    k1 -= structurestrongholdpieces$pieceweight.field_75192_b;
                    if (k1 < 0) {
                        if (!structurestrongholdpieces$pieceweight.func_75189_a(i1) || structurestrongholdpieces$pieceweight == structurestrongholdpieces$stairs2.field_75027_a) {
                            break;
                        }

                        StructureStrongholdPieces$Stronghold structurestrongholdpieces$stronghold1 = func_75200_a(structurestrongholdpieces$pieceweight.field_75194_a, list, random, i, j, k, l, i1);

                        if (structurestrongholdpieces$stronghold1 != null) {
                            ++structurestrongholdpieces$pieceweight.field_75193_c;
                            structurestrongholdpieces$stairs2.field_75027_a = structurestrongholdpieces$pieceweight;
                            if (!structurestrongholdpieces$pieceweight.func_75190_a()) {
                                StructureStrongholdPieces.field_75206_c.remove(structurestrongholdpieces$pieceweight);
                            }

                            return structurestrongholdpieces$stronghold1;
                        }
                    }
                }
            }

            StructureBoundingBox structureboundingbox = StructureStrongholdPieces$Corridor.func_74992_a(list, random, i, j, k, l);

            if (structureboundingbox != null && structureboundingbox.field_78895_b > 1) {
                return new StructureStrongholdPieces$Corridor(i1, random, structureboundingbox, l);
            } else {
                return null;
            }
        }
    }

    private static StructureComponent func_75196_c(StructureStrongholdPieces$Stairs2 structurestrongholdpieces$stairs2, List list, Random random, int i, int j, int k, int l, int i1) {
        if (i1 > 50) {
            return null;
        } else if (Math.abs(i - structurestrongholdpieces$stairs2.func_74874_b().field_78897_a) <= 112 && Math.abs(k - structurestrongholdpieces$stairs2.func_74874_b().field_78896_c) <= 112) {
            StructureStrongholdPieces$Stronghold structurestrongholdpieces$stronghold = func_75201_b(structurestrongholdpieces$stairs2, list, random, i, j, k, l, i1 + 1);

            if (structurestrongholdpieces$stronghold != null) {
                list.add(structurestrongholdpieces$stronghold);
                structurestrongholdpieces$stairs2.field_75026_c.add(structurestrongholdpieces$stronghold);
            }

            return structurestrongholdpieces$stronghold;
        } else {
            return null;
        }
    }
}
