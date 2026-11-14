package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.util.MathHelper;

public class StructureVillagePieces {

    public static void func_143016_a() {
        MapGenStructureIO.func_143031_a(StructureVillagePieces$House1.class, "ViBH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$Field1.class, "ViDF");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$Field2.class, "ViF");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$Torch.class, "ViL");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$Hall.class, "ViPH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$House4Garden.class, "ViSH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$WoodHut.class, "ViSmH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$Church.class, "ViST");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$House2.class, "ViS");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$Start.class, "ViStart");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$Path.class, "ViSR");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$House3.class, "ViTRH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces$Well.class, "ViW");
    }

    public static List func_75084_a(Random random, int i) {
        ArrayList arraylist = new ArrayList();

        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$House4Garden.class, 4, MathHelper.func_76136_a(random, 2 + i, 4 + i * 2)));
        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$Church.class, 20, MathHelper.func_76136_a(random, 0 + i, 1 + i)));
        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$House1.class, 20, MathHelper.func_76136_a(random, 0 + i, 2 + i)));
        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$WoodHut.class, 3, MathHelper.func_76136_a(random, 2 + i, 5 + i * 3)));
        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$Hall.class, 15, MathHelper.func_76136_a(random, 0 + i, 2 + i)));
        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$Field1.class, 3, MathHelper.func_76136_a(random, 1 + i, 4 + i)));
        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$Field2.class, 3, MathHelper.func_76136_a(random, 2 + i, 4 + i * 2)));
        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$House2.class, 15, MathHelper.func_76136_a(random, 0, 1 + i)));
        arraylist.add(new StructureVillagePieces$PieceWeight(StructureVillagePieces$House3.class, 8, MathHelper.func_76136_a(random, 0 + i, 3 + i * 2)));
        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            if (((StructureVillagePieces$PieceWeight) iterator.next()).field_75087_d == 0) {
                iterator.remove();
            }
        }

        return arraylist;
    }

    private static int func_75079_a(List list) {
        boolean flag = false;
        int i = 0;

        StructureVillagePieces$PieceWeight structurevillagepieces$pieceweight;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i += structurevillagepieces$pieceweight.field_75088_b) {
            structurevillagepieces$pieceweight = (StructureVillagePieces$PieceWeight) iterator.next();
            if (structurevillagepieces$pieceweight.field_75087_d > 0 && structurevillagepieces$pieceweight.field_75089_c < structurevillagepieces$pieceweight.field_75087_d) {
                flag = true;
            }
        }

        return flag ? i : -1;
    }

    private static StructureVillagePieces$Village func_75083_a(StructureVillagePieces$Start structurevillagepieces$start, StructureVillagePieces$PieceWeight structurevillagepieces$pieceweight, List list, Random random, int i, int j, int k, int l, int i1) {
        Class oclass = structurevillagepieces$pieceweight.field_75090_a;
        Object object = null;

        if (oclass == StructureVillagePieces$House4Garden.class) {
            object = StructureVillagePieces$House4Garden.func_74912_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        } else if (oclass == StructureVillagePieces$Church.class) {
            object = StructureVillagePieces$Church.func_74919_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        } else if (oclass == StructureVillagePieces$House1.class) {
            object = StructureVillagePieces$House1.func_74898_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        } else if (oclass == StructureVillagePieces$WoodHut.class) {
            object = StructureVillagePieces$WoodHut.func_74908_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        } else if (oclass == StructureVillagePieces$Hall.class) {
            object = StructureVillagePieces$Hall.func_74906_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        } else if (oclass == StructureVillagePieces$Field1.class) {
            object = StructureVillagePieces$Field1.func_74900_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        } else if (oclass == StructureVillagePieces$Field2.class) {
            object = StructureVillagePieces$Field2.func_74902_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        } else if (oclass == StructureVillagePieces$House2.class) {
            object = StructureVillagePieces$House2.func_74915_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        } else if (oclass == StructureVillagePieces$House3.class) {
            object = StructureVillagePieces$House3.func_74921_a(structurevillagepieces$start, list, random, i, j, k, l, i1);
        }

        return (StructureVillagePieces$Village) object;
    }

    private static StructureVillagePieces$Village func_75081_c(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        int j1 = func_75079_a(structurevillagepieces$start.field_74931_h);

        if (j1 <= 0) {
            return null;
        } else {
            int k1 = 0;

            while (k1 < 5) {
                ++k1;
                int l1 = random.nextInt(j1);
                Iterator iterator = structurevillagepieces$start.field_74931_h.iterator();

                while (iterator.hasNext()) {
                    StructureVillagePieces$PieceWeight structurevillagepieces$pieceweight = (StructureVillagePieces$PieceWeight) iterator.next();

                    l1 -= structurevillagepieces$pieceweight.field_75088_b;
                    if (l1 < 0) {
                        if (!structurevillagepieces$pieceweight.func_75085_a(i1) || structurevillagepieces$pieceweight == structurevillagepieces$start.field_74926_d && structurevillagepieces$start.field_74931_h.size() > 1) {
                            break;
                        }

                        StructureVillagePieces$Village structurevillagepieces$village = func_75083_a(structurevillagepieces$start, structurevillagepieces$pieceweight, list, random, i, j, k, l, i1);

                        if (structurevillagepieces$village != null) {
                            ++structurevillagepieces$pieceweight.field_75089_c;
                            structurevillagepieces$start.field_74926_d = structurevillagepieces$pieceweight;
                            if (!structurevillagepieces$pieceweight.func_75086_a()) {
                                structurevillagepieces$start.field_74931_h.remove(structurevillagepieces$pieceweight);
                            }

                            return structurevillagepieces$village;
                        }
                    }
                }
            }

            StructureBoundingBox structureboundingbox = StructureVillagePieces$Torch.func_74904_a(structurevillagepieces$start, list, random, i, j, k, l);

            if (structureboundingbox != null) {
                return new StructureVillagePieces$Torch(structurevillagepieces$start, i1, random, structureboundingbox, l);
            } else {
                return null;
            }
        }
    }

    private static StructureComponent func_75077_d(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        if (i1 > 50) {
            return null;
        } else if (Math.abs(i - structurevillagepieces$start.func_74874_b().field_78897_a) <= 112 && Math.abs(k - structurevillagepieces$start.func_74874_b().field_78896_c) <= 112) {
            StructureVillagePieces$Village structurevillagepieces$village = func_75081_c(structurevillagepieces$start, list, random, i, j, k, l, i1 + 1);

            if (structurevillagepieces$village != null) {
                int j1 = (structurevillagepieces$village.field_74887_e.field_78897_a + structurevillagepieces$village.field_74887_e.field_78893_d) / 2;
                int k1 = (structurevillagepieces$village.field_74887_e.field_78896_c + structurevillagepieces$village.field_74887_e.field_78892_f) / 2;
                int l1 = structurevillagepieces$village.field_74887_e.field_78893_d - structurevillagepieces$village.field_74887_e.field_78897_a;
                int i2 = structurevillagepieces$village.field_74887_e.field_78892_f - structurevillagepieces$village.field_74887_e.field_78896_c;
                int j2 = l1 > i2 ? l1 : i2;

                if (structurevillagepieces$start.func_74925_d().func_76940_a(j1, k1, j2 / 2 + 4, MapGenVillage.field_75055_e)) {
                    list.add(structurevillagepieces$village);
                    structurevillagepieces$start.field_74932_i.add(structurevillagepieces$village);
                    return structurevillagepieces$village;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    private static StructureComponent func_75080_e(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        if (i1 > 3 + structurevillagepieces$start.field_74928_c) {
            return null;
        } else if (Math.abs(i - structurevillagepieces$start.func_74874_b().field_78897_a) <= 112 && Math.abs(k - structurevillagepieces$start.func_74874_b().field_78896_c) <= 112) {
            StructureBoundingBox structureboundingbox = StructureVillagePieces$Path.func_74933_a(structurevillagepieces$start, list, random, i, j, k, l);

            if (structureboundingbox != null && structureboundingbox.field_78895_b > 10) {
                StructureVillagePieces$Path structurevillagepieces$path = new StructureVillagePieces$Path(structurevillagepieces$start, i1, random, structureboundingbox, l);
                int j1 = (structurevillagepieces$path.field_74887_e.field_78897_a + structurevillagepieces$path.field_74887_e.field_78893_d) / 2;
                int k1 = (structurevillagepieces$path.field_74887_e.field_78896_c + structurevillagepieces$path.field_74887_e.field_78892_f) / 2;
                int l1 = structurevillagepieces$path.field_74887_e.field_78893_d - structurevillagepieces$path.field_74887_e.field_78897_a;
                int i2 = structurevillagepieces$path.field_74887_e.field_78892_f - structurevillagepieces$path.field_74887_e.field_78896_c;
                int j2 = l1 > i2 ? l1 : i2;

                if (structurevillagepieces$start.func_74925_d().func_76940_a(j1, k1, j2 / 2 + 4, MapGenVillage.field_75055_e)) {
                    list.add(structurevillagepieces$path);
                    structurevillagepieces$start.field_74930_j.add(structurevillagepieces$path);
                    return structurevillagepieces$path;
                }
            }

            return null;
        } else {
            return null;
        }
    }
}
