package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class StructureStrongholdPieces$PortalRoom extends StructureStrongholdPieces$Stronghold {

    private boolean field_75005_a;

    public StructureStrongholdPieces$PortalRoom() {}

    public StructureStrongholdPieces$PortalRoom(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Mob", this.field_75005_a);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_75005_a = nbttagcompound.func_74767_n("Mob");
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        if (structurecomponent != null) {
            ((StructureStrongholdPieces$Stairs2) structurecomponent).field_75025_b = this;
        }

    }

    public static StructureStrongholdPieces$PortalRoom func_75004_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -4, -1, 0, 11, 8, 16, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$PortalRoom(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_74882_a(world, structureboundingbox, 0, 0, 0, 10, 7, 15, false, random, StructureStrongholdPieces.access$200());
        this.func_74990_a(world, random, structureboundingbox, StructureStrongholdPieces$Stronghold$Door.GRATES, 4, 1, 0);
        int i = 6;

        this.func_74882_a(world, structureboundingbox, 1, i, 1, 1, i, 14, false, random, StructureStrongholdPieces.access$200());
        this.func_74882_a(world, structureboundingbox, 9, i, 1, 9, i, 14, false, random, StructureStrongholdPieces.access$200());
        this.func_74882_a(world, structureboundingbox, 2, i, 1, 8, i, 2, false, random, StructureStrongholdPieces.access$200());
        this.func_74882_a(world, structureboundingbox, 2, i, 14, 8, i, 14, false, random, StructureStrongholdPieces.access$200());
        this.func_74882_a(world, structureboundingbox, 1, 1, 1, 2, 1, 4, false, random, StructureStrongholdPieces.access$200());
        this.func_74882_a(world, structureboundingbox, 8, 1, 1, 9, 1, 4, false, random, StructureStrongholdPieces.access$200());
        this.func_151549_a(world, structureboundingbox, 1, 1, 1, 1, 1, 3, Blocks.field_150356_k, Blocks.field_150356_k, false);
        this.func_151549_a(world, structureboundingbox, 9, 1, 1, 9, 1, 3, Blocks.field_150356_k, Blocks.field_150356_k, false);
        this.func_74882_a(world, structureboundingbox, 3, 1, 8, 7, 1, 12, false, random, StructureStrongholdPieces.access$200());
        this.func_151549_a(world, structureboundingbox, 4, 1, 9, 6, 1, 11, Blocks.field_150356_k, Blocks.field_150356_k, false);

        int j;

        for (j = 3; j < 14; j += 2) {
            this.func_151549_a(world, structureboundingbox, 0, 3, j, 0, 4, j, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
            this.func_151549_a(world, structureboundingbox, 10, 3, j, 10, 4, j, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
        }

        for (j = 2; j < 9; j += 2) {
            this.func_151549_a(world, structureboundingbox, j, 3, 15, j, 4, 15, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
        }

        j = this.func_151555_a(Blocks.field_150390_bg, 3);
        this.func_74882_a(world, structureboundingbox, 4, 1, 5, 6, 1, 7, false, random, StructureStrongholdPieces.access$200());
        this.func_74882_a(world, structureboundingbox, 4, 2, 6, 6, 2, 7, false, random, StructureStrongholdPieces.access$200());
        this.func_74882_a(world, structureboundingbox, 4, 3, 7, 6, 3, 7, false, random, StructureStrongholdPieces.access$200());

        for (int k = 4; k <= 6; ++k) {
            this.func_151550_a(world, Blocks.field_150390_bg, j, k, 1, 4, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150390_bg, j, k, 2, 5, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150390_bg, j, k, 3, 6, structureboundingbox);
        }

        byte b0 = 2;
        byte b1 = 0;
        byte b2 = 3;
        byte b3 = 1;

        switch (this.field_74885_f) {
            case 0:
                b0 = 0;
                b1 = 2;
                break;
            case 1:
                b0 = 1;
                b1 = 3;
                b2 = 0;
                b3 = 2;
            case 2:
            default:
                break;
            case 3:
                b0 = 3;
                b1 = 1;
                b2 = 0;
                b3 = 2;
        }

        this.func_151550_a(world, Blocks.field_150378_br, b0 + (random.nextFloat() > 0.9F ? 4 : 0), 4, 3, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b0 + (random.nextFloat() > 0.9F ? 4 : 0), 5, 3, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b0 + (random.nextFloat() > 0.9F ? 4 : 0), 6, 3, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b1 + (random.nextFloat() > 0.9F ? 4 : 0), 4, 3, 12, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b1 + (random.nextFloat() > 0.9F ? 4 : 0), 5, 3, 12, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b1 + (random.nextFloat() > 0.9F ? 4 : 0), 6, 3, 12, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b2 + (random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 9, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b2 + (random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b2 + (random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 11, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b3 + (random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 9, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b3 + (random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150378_br, b3 + (random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 11, structureboundingbox);
        if (!this.field_75005_a) {
            i = this.func_74862_a(3);
            int l = this.func_74865_a(5, 6);
            int i1 = this.func_74873_b(5, 6);

            if (structureboundingbox.func_78890_b(l, i, i1)) {
                this.field_75005_a = true;
                world.func_147465_d(l, i, i1, Blocks.field_150474_ac, 0, 2);
                TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) world.func_147438_o(l, i, i1);

                if (tileentitymobspawner != null) {
                    tileentitymobspawner.func_145881_a().func_98272_a("Silverfish");
                }
            }
        }

        return true;
    }
}
