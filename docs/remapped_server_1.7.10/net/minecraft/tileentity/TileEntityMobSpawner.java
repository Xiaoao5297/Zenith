package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityMobSpawner extends TileEntity {

    private final MobSpawnerBaseLogic field_145882_a = new TileEntityMobSpawner$1(this);

    public TileEntityMobSpawner() {}

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        super.func_145839_a(nbttagcompound);
        this.field_145882_a.func_98270_a(nbttagcompound);
    }

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        this.field_145882_a.func_98280_b(nbttagcompound);
    }

    @Override
    public void func_145845_h() {
        this.field_145882_a.func_98278_g();
        super.func_145845_h();
    }

    @Override
    public Packet func_145844_m() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.func_145841_b(nbttagcompound);
        nbttagcompound.func_82580_o("SpawnPotentials");
        return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 1, nbttagcompound);
    }

    @Override
    public boolean func_145842_c(int i, int j) {
        return this.field_145882_a.func_98268_b(i) ? true : super.func_145842_c(i, j);
    }

    public MobSpawnerBaseLogic func_145881_a() {
        return this.field_145882_a;
    }
}
