package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S33PacketUpdateSign;

public class TileEntitySign extends TileEntity {

    public String[] field_145915_a = new String[]{"", "", "", ""};
    public int field_145918_i = -1;
    private boolean field_145916_j = true;
    private EntityPlayer field_145917_k;

    public TileEntitySign() {}

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        nbttagcompound.func_74778_a("Text1", this.field_145915_a[0]);
        nbttagcompound.func_74778_a("Text2", this.field_145915_a[1]);
        nbttagcompound.func_74778_a("Text3", this.field_145915_a[2]);
        nbttagcompound.func_74778_a("Text4", this.field_145915_a[3]);
    }

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        this.field_145916_j = false;
        super.func_145839_a(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            this.field_145915_a[i] = nbttagcompound.func_74779_i("Text" + (i + 1));
            if (this.field_145915_a[i].length() > 15) {
                this.field_145915_a[i] = this.field_145915_a[i].substring(0, 15);
            }
        }

    }

    @Override
    public Packet func_145844_m() {
        String[] astring = new String[4];

        System.arraycopy(this.field_145915_a, 0, astring, 0, 4);
        return new S33PacketUpdateSign(this.field_145851_c, this.field_145848_d, this.field_145849_e, astring);
    }

    public boolean func_145914_a() {
        return this.field_145916_j;
    }

    public void func_145912_a(EntityPlayer entityplayer) {
        this.field_145917_k = entityplayer;
    }

    public EntityPlayer func_145911_b() {
        return this.field_145917_k;
    }
}
