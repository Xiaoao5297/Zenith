package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;

abstract class StructureNetherBridgePieces$Piece extends StructureComponent {

    protected static final WeightedRandomChestContent[] field_111019_a = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 5), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151010_B, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151171_ah, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151033_d, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151075_bm, 0, 3, 7, 5), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 8), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 3)};

    public StructureNetherBridgePieces$Piece() {}

    protected StructureNetherBridgePieces$Piece(int i) {
        super(i);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {}

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {}

    private int func_74960_a(List list) {
        boolean flag = false;
        int i = 0;

        StructureNetherBridgePieces$PieceWeight structurenetherbridgepieces$pieceweight;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i += structurenetherbridgepieces$pieceweight.field_78826_b) {
            structurenetherbridgepieces$pieceweight = (StructureNetherBridgePieces$PieceWeight) iterator.next();
            if (structurenetherbridgepieces$pieceweight.field_78824_d > 0 && structurenetherbridgepieces$pieceweight.field_78827_c < structurenetherbridgepieces$pieceweight.field_78824_d) {
                flag = true;
            }
        }

        return flag ? i : -1;
    }

    private StructureNetherBridgePieces$Piece func_74959_a(StructureNetherBridgePieces$Start structurenetherbridgepieces$start, List list, List list1, Random random, int i, int j, int k, int l, int i1) {
        int j1 = this.func_74960_a(list);
        boolean flag = j1 > 0 && i1 <= 30;
        int k1 = 0;

        while (k1 < 5 && flag) {
            ++k1;
            int l1 = random.nextInt(j1);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                StructureNetherBridgePieces$PieceWeight structurenetherbridgepieces$pieceweight = (StructureNetherBridgePieces$PieceWeight) iterator.next();

                l1 -= structurenetherbridgepieces$pieceweight.field_78826_b;
                if (l1 < 0) {
                    if (!structurenetherbridgepieces$pieceweight.func_78822_a(i1) || structurenetherbridgepieces$pieceweight == structurenetherbridgepieces$start.field_74970_a && !structurenetherbridgepieces$pieceweight.field_78825_e) {
                        break;
                    }

                    StructureNetherBridgePieces$Piece structurenetherbridgepieces$piece = StructureNetherBridgePieces.access$000(structurenetherbridgepieces$pieceweight, list1, random, i, j, k, l, i1);

                    if (structurenetherbridgepieces$piece != null) {
                        ++structurenetherbridgepieces$pieceweight.field_78827_c;
                        structurenetherbridgepieces$start.field_74970_a = structurenetherbridgepieces$pieceweight;
                        if (!structurenetherbridgepieces$pieceweight.func_78823_a()) {
                            list.remove(structurenetherbridgepieces$pieceweight);
                        }

                        return structurenetherbridgepieces$piece;
                    }
                }
            }
        }

        return StructureNetherBridgePieces$End.func_74971_a(list1, random, i, j, k, l, i1);
    }

    private StructureComponent func_74962_a(StructureNetherBridgePieces$Start structurenetherbridgepieces$start, List list, Random random, int i, int j, int k, int l, int i1, boolean flag) {
        if (Math.abs(i - structurenetherbridgepieces$start.func_74874_b().field_78897_a) <= 112 && Math.abs(k - structurenetherbridgepieces$start.func_74874_b().field_78896_c) <= 112) {
            List list1 = structurenetherbridgepieces$start.field_74968_b;

            if (flag) {
                list1 = structurenetherbridgepieces$start.field_74969_c;
            }

            StructureNetherBridgePieces$Piece structurenetherbridgepieces$piece = this.func_74959_a(structurenetherbridgepieces$start, list1, list, random, i, j, k, l, i1 + 1);

            if (structurenetherbridgepieces$piece != null) {
                list.add(structurenetherbridgepieces$piece);
                structurenetherbridgepieces$start.field_74967_d.add(structurenetherbridgepieces$piece);
            }

            return structurenetherbridgepieces$piece;
        } else {
            return StructureNetherBridgePieces$End.func_74971_a(list, random, i, j, k, l, i1);
        }
    }

    protected StructureComponent func_74963_a(StructureNetherBridgePieces$Start structurenetherbridgepieces$start, List list, Random random, int i, int j, boolean flag) {
        switch (this.field_74885_f) {
            case 0:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a + i, this.field_74887_e.field_78895_b + j, this.field_74887_e.field_78892_f + 1, this.field_74885_f, this.func_74877_c(), flag);
            case 1:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + j, this.field_74887_e.field_78896_c + i, this.field_74885_f, this.func_74877_c(), flag);
            case 2:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a + i, this.field_74887_e.field_78895_b + j, this.field_74887_e.field_78896_c - 1, this.field_74885_f, this.func_74877_c(), flag);
            case 3:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + j, this.field_74887_e.field_78896_c + i, this.field_74885_f, this.func_74877_c(), flag);
            default:
                return null;
        }
    }

    protected StructureComponent func_74961_b(StructureNetherBridgePieces$Start structurenetherbridgepieces$start, List list, Random random, int i, int j, boolean flag) {
        switch (this.field_74885_f) {
            case 0:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 1, this.func_74877_c(), flag);
            case 1:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c - 1, 2, this.func_74877_c(), flag);
            case 2:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 1, this.func_74877_c(), flag);
            case 3:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c - 1, 2, this.func_74877_c(), flag);
            default:
                return null;
        }
    }

    protected StructureComponent func_74965_c(StructureNetherBridgePieces$Start structurenetherbridgepieces$start, List list, Random random, int i, int j, boolean flag) {
        switch (this.field_74885_f) {
            case 0:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 3, this.func_74877_c(), flag);
            case 1:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78892_f + 1, 0, this.func_74877_c(), flag);
            case 2:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 3, this.func_74877_c(), flag);
            case 3:
                return this.func_74962_a(structurenetherbridgepieces$start, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78892_f + 1, 0, this.func_74877_c(), flag);
            default:
                return null;
        }
    }

    protected static boolean func_74964_a(StructureBoundingBox structureboundingbox) {
        return structureboundingbox != null && structureboundingbox.field_78895_b > 10;
    }
}
