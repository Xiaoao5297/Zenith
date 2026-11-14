package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TileEntitySkull extends TileEntity {

    private int field_145908_a;
    private int field_145910_i;
    private GameProfile field_152110_j = null;

    public TileEntitySkull() {}

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        nbttagcompound.func_74774_a("SkullType", (byte) (this.field_145908_a & 255));
        nbttagcompound.func_74774_a("Rot", (byte) (this.field_145910_i & 255));
        if (this.field_152110_j != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            NBTUtil.func_152460_a(nbttagcompound1, this.field_152110_j);
            nbttagcompound.func_74782_a("Owner", nbttagcompound1);
        }

    }

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        super.func_145839_a(nbttagcompound);
        this.field_145908_a = nbttagcompound.func_74771_c("SkullType");
        this.field_145910_i = nbttagcompound.func_74771_c("Rot");
        if (this.field_145908_a == 3) {
            if (nbttagcompound.func_150297_b("Owner", 10)) {
                this.field_152110_j = NBTUtil.func_152459_a(nbttagcompound.func_74775_l("Owner"));
            } else if (nbttagcompound.func_150297_b("ExtraType", 8) && !StringUtils.func_151246_b(nbttagcompound.func_74779_i("ExtraType"))) {
                this.field_152110_j = new GameProfile((UUID) null, nbttagcompound.func_74779_i("ExtraType"));
                this.func_152109_d();
            }
        }

    }

    public GameProfile func_152108_a() {
        return this.field_152110_j;
    }

    @Override
    public Packet func_145844_m() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.func_145841_b(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 4, nbttagcompound);
    }

    public void func_152107_a(int i) {
        this.field_145908_a = i;
        this.field_152110_j = null;
    }

    public void func_152106_a(GameProfile gameprofile) {
        this.field_145908_a = 3;
        this.field_152110_j = gameprofile;
        this.func_152109_d();
    }

    private void func_152109_d() {
        if (this.field_152110_j != null && !StringUtils.func_151246_b(this.field_152110_j.getName())) {
            if (!this.field_152110_j.isComplete() || !this.field_152110_j.getProperties().containsKey("textures")) {
                GameProfile gameprofile = MinecraftServer.func_71276_C().func_152358_ax().func_152655_a(this.field_152110_j.getName());

                if (gameprofile != null) {
                    Property property = (Property) Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object) null);

                    if (property == null) {
                        gameprofile = MinecraftServer.func_71276_C().func_147130_as().fillProfileProperties(gameprofile, true);
                    }

                    this.field_152110_j = gameprofile;
                    this.func_70296_d();
                }
            }
        }
    }

    public int func_145904_a() {
        return this.field_145908_a;
    }

    public void func_145903_a(int i) {
        this.field_145910_i = i;
    }
}
