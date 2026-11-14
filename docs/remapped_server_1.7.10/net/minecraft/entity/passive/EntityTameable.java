package net.minecraft.entity.passive;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {

    protected EntityAISit field_70911_d = new EntityAISit(this);

    public EntityTameable(World world) {
        super(world);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, (byte) 0);
        this.field_70180_af.func_75682_a(17, "");
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        if (this.func_152113_b() == null) {
            nbttagcompound.func_74778_a("OwnerUUID", "");
        } else {
            nbttagcompound.func_74778_a("OwnerUUID", this.func_152113_b());
        }

        nbttagcompound.func_74757_a("Sitting", this.func_70906_o());
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        String s = "";

        if (nbttagcompound.func_150297_b("OwnerUUID", 8)) {
            s = nbttagcompound.func_74779_i("OwnerUUID");
        } else {
            String s1 = nbttagcompound.func_74779_i("Owner");

            s = PreYggdrasilConverter.func_152719_a(s1);
        }

        if (s.length() > 0) {
            this.func_152115_b(s);
            this.func_70903_f(true);
        }

        this.field_70911_d.func_75270_a(nbttagcompound.func_74767_n("Sitting"));
        this.func_70904_g(nbttagcompound.func_74767_n("Sitting"));
    }

    protected void func_70908_e(boolean flag) {
        String s = "heart";

        if (!flag) {
            s = "smoke";
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.field_70146_Z.nextGaussian() * 0.02D;
            double d1 = this.field_70146_Z.nextGaussian() * 0.02D;
            double d2 = this.field_70146_Z.nextGaussian() * 0.02D;

            this.field_70170_p.func_72869_a(s, this.field_70165_t + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, this.field_70163_u + 0.5D + (double) (this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, d0, d1, d2);
        }

    }

    public boolean func_70909_n() {
        return (this.field_70180_af.func_75683_a(16) & 4) != 0;
    }

    public void func_70903_f(boolean flag) {
        byte b0 = this.field_70180_af.func_75683_a(16);

        if (flag) {
            this.field_70180_af.func_75692_b(16, (byte) (b0 | 4));
        } else {
            this.field_70180_af.func_75692_b(16, (byte) (b0 & -5));
        }

    }

    public boolean func_70906_o() {
        return (this.field_70180_af.func_75683_a(16) & 1) != 0;
    }

    public void func_70904_g(boolean flag) {
        byte b0 = this.field_70180_af.func_75683_a(16);

        if (flag) {
            this.field_70180_af.func_75692_b(16, (byte) (b0 | 1));
        } else {
            this.field_70180_af.func_75692_b(16, (byte) (b0 & -2));
        }

    }

    @Override
    public String func_152113_b() {
        return this.field_70180_af.func_75681_e(17);
    }

    public void func_152115_b(String s) {
        this.field_70180_af.func_75692_b(17, s);
    }

    @Override
    public EntityLivingBase func_70902_q() {
        try {
            UUID uuid = UUID.fromString(this.func_152113_b());

            return uuid == null ? null : this.field_70170_p.func_152378_a(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    public boolean func_152114_e(EntityLivingBase entitylivingbase) {
        return entitylivingbase == this.func_70902_q();
    }

    public EntityAISit func_70907_r() {
        return this.field_70911_d;
    }

    public boolean func_142018_a(EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1) {
        return true;
    }

    @Override
    public Team func_96124_cp() {
        if (this.func_70909_n()) {
            EntityLivingBase entitylivingbase = this.func_70902_q();

            if (entitylivingbase != null) {
                return entitylivingbase.func_96124_cp();
            }
        }

        return super.func_96124_cp();
    }

    @Override
    public boolean func_142014_c(EntityLivingBase entitylivingbase) {
        if (this.func_70909_n()) {
            EntityLivingBase entitylivingbase1 = this.func_70902_q();

            if (entitylivingbase == entitylivingbase1) {
                return true;
            }

            if (entitylivingbase1 != null) {
                return entitylivingbase1.func_142014_c(entitylivingbase);
            }
        }

        return super.func_142014_c(entitylivingbase);
    }
}
