package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent$Serializer;
import net.minecraft.world.World;

public class EntityMinecartCommandBlock extends EntityMinecart {

    private final CommandBlockLogic field_145824_a = new EntityMinecartCommandBlock$1(this);
    private int field_145823_b = 0;

    public EntityMinecartCommandBlock(World world) {
        super(world);
    }

    public EntityMinecartCommandBlock(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.func_70096_w().func_75682_a(23, "");
        this.func_70096_w().func_75682_a(24, "");
    }

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.field_145824_a.func_145759_b(nbttagcompound);
        this.func_70096_w().func_75692_b(23, this.func_145822_e().func_145753_i());
        this.func_70096_w().func_75692_b(24, IChatComponent$Serializer.func_150696_a(this.func_145822_e().func_145749_h()));
    }

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        this.field_145824_a.func_145758_a(nbttagcompound);
    }

    @Override
    public int func_94087_l() {
        return 6;
    }

    @Override
    public Block func_145817_o() {
        return Blocks.field_150483_bI;
    }

    public CommandBlockLogic func_145822_e() {
        return this.field_145824_a;
    }

    @Override
    public void func_96095_a(int i, int j, int k, boolean flag) {
        if (flag && this.field_70173_aa - this.field_145823_b >= 4) {
            this.func_145822_e().func_145755_a(this.field_70170_p);
            this.field_145823_b = this.field_70173_aa;
        }

    }

    @Override
    public boolean func_130002_c(EntityPlayer entityplayer) {
        if (this.field_70170_p.field_72995_K) {
            entityplayer.func_146095_a(this.func_145822_e());
        }

        return super.func_130002_c(entityplayer);
    }

    @Override
    public void func_145781_i(int i) {
        super.func_145781_i(i);
        if (i == 24) {
            try {
                this.field_145824_a.func_145750_b(IChatComponent$Serializer.func_150699_a(this.func_70096_w().func_75681_e(24)));
            } catch (Throwable throwable) {
                ;
            }
        } else if (i == 23) {
            this.field_145824_a.func_145752_a(this.func_70096_w().func_75681_e(23));
        }

    }
}
