package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Throne extends StructureNetherBridgePieces$Piece {

    private boolean field_74976_a;

    public StructureNetherBridgePieces$Throne() {}

    public StructureNetherBridgePieces$Throne(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_74976_a = nbttagcompound.func_74767_n("Mob");
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Mob", this.field_74976_a);
    }

    public static StructureNetherBridgePieces$Throne func_74975_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -2, 0, 0, 7, 8, 9, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$Throne(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 6, 7, 7, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 0, 5, 1, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 2, 1, 5, 2, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 3, 2, 5, 3, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 4, 3, 5, 4, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 2, 0, 1, 4, 2, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 5, 2, 0, 5, 4, 2, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 5, 2, 1, 5, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 5, 5, 2, 5, 5, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 3, 0, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 5, 3, 6, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 5, 8, 5, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151550_a(world, Blocks.field_150386_bk, 0, 1, 6, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150386_bk, 0, 5, 6, 3, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 0, 6, 3, 0, 6, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 6, 6, 3, 6, 6, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 1, 6, 8, 5, 7, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 2, 8, 8, 4, 8, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        int i;
        int j;

        if (!this.field_74976_a) {
            i = this.func_74862_a(5);
            j = this.func_74865_a(3, 5);
            int k = this.func_74873_b(3, 5);

            if (structureboundingbox.func_78890_b(j, i, k)) {
                this.field_74976_a = true;
                world.func_147465_d(j, i, k, Blocks.field_150474_ac, 0, 2);
                TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) world.func_147438_o(j, i, k);

                if (tileentitymobspawner != null) {
                    tileentitymobspawner.func_145881_a().func_98272_a("Blaze");
                }
            }
        }

        for (i = 0; i <= 6; ++i) {
            for (j = 0; j <= 6; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, j, structureboundingbox);
            }
        }

        return true;
    }
}
