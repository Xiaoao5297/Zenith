package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityIronGolem extends EntityGolem {

    private int field_70858_e;
    Village field_70857_d;
    private int field_70855_f;
    private int field_70856_g;

    public EntityIronGolem(World world) {
        super(world);
        this.func_70105_a(1.4F, 2.9F);
        this.func_70661_as().func_75491_a(true);
        this.field_70714_bg.func_75776_a(1, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.field_70714_bg.func_75776_a(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
        this.field_70714_bg.func_75776_a(3, new EntityAIMoveThroughVillage(this, 0.6D, true));
        this.field_70714_bg.func_75776_a(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.field_70714_bg.func_75776_a(5, new EntityAILookAtVillager(this));
        this.field_70714_bg.func_75776_a(6, new EntityAIWander(this, 0.6D));
        this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
        this.field_70715_bh.func_75776_a(1, new EntityAIDefendVillage(this));
        this.field_70715_bh.func_75776_a(2, new EntityAIHurtByTarget(this, false));
        this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, IMob.field_82192_a));
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, (byte) 0);
    }

    @Override
    public boolean func_70650_aV() {
        return true;
    }

    @Override
    protected void func_70629_bd() {
        if (--this.field_70858_e <= 0) {
            this.field_70858_e = 70 + this.field_70146_Z.nextInt(50);
            this.field_70857_d = this.field_70170_p.field_72982_D.func_75550_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v), 32);
            if (this.field_70857_d == null) {
                this.func_110177_bN();
            } else {
                ChunkCoordinates chunkcoordinates = this.field_70857_d.func_75577_a();

                this.func_110171_b(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c, (int) ((float) this.field_70857_d.func_75568_b() * 0.6F));
            }
        }

        super.func_70629_bd();
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(100.0D);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
    }

    @Override
    protected int func_70682_h(int i) {
        return i;
    }

    @Override
    protected void func_82167_n(Entity entity) {
        if (entity instanceof IMob && this.func_70681_au().nextInt(20) == 0) {
            this.func_70624_b((EntityLivingBase) entity);
        }

        super.func_82167_n(entity);
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (this.field_70855_f > 0) {
            --this.field_70855_f;
        }

        if (this.field_70856_g > 0) {
            --this.field_70856_g;
        }

        if (this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 2.500000277905201E-7D && this.field_70146_Z.nextInt(5) == 0) {
            int i = MathHelper.func_76128_c(this.field_70165_t);
            int j = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D - (double) this.field_70129_M);
            int k = MathHelper.func_76128_c(this.field_70161_v);
            Block block = this.field_70170_p.func_147439_a(i, j, k);

            if (block.func_149688_o() != Material.field_151579_a) {
                this.field_70170_p.func_72869_a("blockcrack_" + Block.func_149682_b(block) + "_" + this.field_70170_p.func_72805_g(i, j, k), this.field_70165_t + ((double) this.field_70146_Z.nextFloat() - 0.5D) * (double) this.field_70130_N, this.field_70121_D.field_72338_b + 0.1D, this.field_70161_v + ((double) this.field_70146_Z.nextFloat() - 0.5D) * (double) this.field_70130_N, 4.0D * ((double) this.field_70146_Z.nextFloat() - 0.5D), 0.5D, ((double) this.field_70146_Z.nextFloat() - 0.5D) * 4.0D);
            }
        }

    }

    @Override
    public boolean func_70686_a(Class oclass) {
        return this.func_70850_q() && EntityPlayer.class.isAssignableFrom(oclass) ? false : super.func_70686_a(oclass);
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74757_a("PlayerCreated", this.func_70850_q());
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.func_70849_f(nbttagcompound.func_74767_n("PlayerCreated"));
    }

    @Override
    public boolean func_70652_k(Entity entity) {
        this.field_70855_f = 10;
        this.field_70170_p.func_72960_a(this, (byte) 4);
        boolean flag = entity.func_70097_a(DamageSource.func_76358_a(this), (float) (7 + this.field_70146_Z.nextInt(15)));

        if (flag) {
            entity.field_70181_x += 0.4000000059604645D;
        }

        this.func_85030_a("mob.irongolem.throw", 1.0F, 1.0F);
        return flag;
    }

    public Village func_70852_n() {
        return this.field_70857_d;
    }

    public void func_70851_e(boolean flag) {
        this.field_70856_g = flag ? 400 : 0;
        this.field_70170_p.func_72960_a(this, (byte) 11);
    }

    @Override
    protected String func_70621_aR() {
        return "mob.irongolem.hit";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.irongolem.death";
    }

    @Override
    protected void func_145780_a(int i, int j, int k, Block block) {
        this.func_85030_a("mob.irongolem.walk", 1.0F, 1.0F);
    }

    @Override
    protected void func_70628_a(boolean flag, int i) {
        int j = this.field_70146_Z.nextInt(3);

        int k;

        for (k = 0; k < j; ++k) {
            this.func_145778_a(Item.func_150898_a(Blocks.field_150328_O), 1, 0.0F);
        }

        k = 3 + this.field_70146_Z.nextInt(3);

        for (int l = 0; l < k; ++l) {
            this.func_145779_a(Items.field_151042_j, 1);
        }

    }

    public int func_70853_p() {
        return this.field_70856_g;
    }

    public boolean func_70850_q() {
        return (this.field_70180_af.func_75683_a(16) & 1) != 0;
    }

    public void func_70849_f(boolean flag) {
        byte b0 = this.field_70180_af.func_75683_a(16);

        if (flag) {
            this.field_70180_af.func_75692_b(16, (byte) (b0 | 1));
        } else {
            this.field_70180_af.func_75692_b(16, (byte) (b0 & -2));
        }

    }

    @Override
    public void func_70645_a(DamageSource damagesource) {
        if (!this.func_70850_q() && this.field_70717_bb != null && this.field_70857_d != null) {
            this.field_70857_d.func_82688_a(this.field_70717_bb.func_70005_c_(), -5);
        }

        super.func_70645_a(damagesource);
    }
}
