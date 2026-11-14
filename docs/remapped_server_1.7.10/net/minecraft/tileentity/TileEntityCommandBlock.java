package net.minecraft.tileentity;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityCommandBlock extends TileEntity {

    private final CommandBlockLogic field_145994_a = new TileEntityCommandBlock$1(this);

    public TileEntityCommandBlock() {}

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        this.field_145994_a.func_145758_a(nbttagcompound);
    }

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        super.func_145839_a(nbttagcompound);
        this.field_145994_a.func_145759_b(nbttagcompound);
    }

    @Override
    public Packet func_145844_m() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.func_145841_b(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 2, nbttagcompound);
    }

    public CommandBlockLogic func_145993_a() {
        return this.field_145994_a;
    }
}
