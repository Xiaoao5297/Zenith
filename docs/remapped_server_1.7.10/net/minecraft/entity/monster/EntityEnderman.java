package net.minecraft.entity.monster;

import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob {

    private static final UUID field_110192_bp = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier field_110193_bq = (new AttributeModifier(EntityEnderman.field_110192_bp, "Attacking speed boost", 6.199999809265137D, 0)).func_111168_a(false);
    private static boolean[] field_70827_d = new boolean[256];
    private int field_70828_e;
    private int field_70826_g;
    private Entity field_110194_bu;
    private boolean field_104003_g;

    public EntityEnderman(World world) {
        super(world);
        this.func_70105_a(0.6F, 2.9F);
        this.field_70138_W = 1.0F;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(40.0D);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(7.0D);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, new Byte((byte) 0));
        this.field_70180_af.func_75682_a(17, new Byte((byte) 0));
        this.field_70180_af.func_75682_a(18, new Byte((byte) 0));
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74777_a("carried", (short) Block.func_149682_b(this.func_146080_bZ()));
        nbttagcompound.func_74777_a("carriedData", (short) this.func_70824_q());
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.func_146081_a(Block.func_149729_e(nbttagcompound.func_74765_d("carried")));
        this.func_70817_b(nbttagcompound.func_74765_d("carriedData"));
    }

    @Override
    protected Entity func_70782_k() {
        EntityPlayer entityplayer = this.field_70170_p.func_72856_b(this, 64.0D);

        if (entityplayer != null) {
            if (this.func_70821_d(entityplayer)) {
                this.field_104003_g = true;
                if (this.field_70826_g == 0) {
                    this.field_70170_p.func_72908_a(entityplayer.field_70165_t, entityplayer.field_70163_u, entityplayer.field_70161_v, "mob.endermen.stare", 1.0F, 1.0F);
                }

                if (this.field_70826_g++ == 5) {
                    this.field_70826_g = 0;
                    this.func_70819_e(true);
                    return entityplayer;
                }
            } else {
                this.field_70826_g = 0;
            }
        }

        return null;
    }

    private boolean func_70821_d(EntityPlayer entityplayer) {
        ItemStack itemstack = entityplayer.field_71071_by.field_70460_b[3];

        if (itemstack != null && itemstack.func_77973_b() == Item.func_150898_a(Blocks.field_150423_aK)) {
            return false;
        } else {
            Vec3 vec3 = entityplayer.func_70676_i(1.0F).func_72432_b();
            Vec3 vec31 = Vec3.func_72443_a(this.field_70165_t - entityplayer.field_70165_t, this.field_70121_D.field_72338_b + (double) (this.field_70131_O / 2.0F) - (entityplayer.field_70163_u + (double) entityplayer.func_70047_e()), this.field_70161_v - entityplayer.field_70161_v);
            double d0 = vec31.func_72433_c();

            vec31 = vec31.func_72432_b();
            double d1 = vec3.func_72430_b(vec31);

            return d1 > 1.0D - 0.025D / d0 && entityplayer.func_70685_l(this);
        }
    }

    @Override
    public void func_70636_d() {
        if (this.func_70026_G()) {
            this.func_70097_a(DamageSource.field_76369_e, 1.0F);
        }

        if (this.field_110194_bu != this.field_70789_a) {
            IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);

            iattributeinstance.func_111124_b(EntityEnderman.field_110193_bq);
            if (this.field_70789_a != null) {
                iattributeinstance.func_111121_a(EntityEnderman.field_110193_bq);
            }
        }

        this.field_110194_bu = this.field_70789_a;
        int i;

        if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            int j;
            int k;
            Block block;

            if (this.func_146080_bZ().func_149688_o() == Material.field_151579_a) {
                if (this.field_70146_Z.nextInt(20) == 0) {
                    i = MathHelper.func_76128_c(this.field_70165_t - 2.0D + this.field_70146_Z.nextDouble() * 4.0D);
                    j = MathHelper.func_76128_c(this.field_70163_u + this.field_70146_Z.nextDouble() * 3.0D);
                    k = MathHelper.func_76128_c(this.field_70161_v - 2.0D + this.field_70146_Z.nextDouble() * 4.0D);
                    block = this.field_70170_p.func_147439_a(i, j, k);
                    if (EntityEnderman.field_70827_d[Block.func_149682_b(block)]) {
                        this.func_146081_a(block);
                        this.func_70817_b(this.field_70170_p.func_72805_g(i, j, k));
                        this.field_70170_p.func_147449_b(i, j, k, Blocks.field_150350_a);
                    }
                }
            } else if (this.field_70146_Z.nextInt(2000) == 0) {
                i = MathHelper.func_76128_c(this.field_70165_t - 1.0D + this.field_70146_Z.nextDouble() * 2.0D);
                j = MathHelper.func_76128_c(this.field_70163_u + this.field_70146_Z.nextDouble() * 2.0D);
                k = MathHelper.func_76128_c(this.field_70161_v - 1.0D + this.field_70146_Z.nextDouble() * 2.0D);
                block = this.field_70170_p.func_147439_a(i, j, k);
                Block block1 = this.field_70170_p.func_147439_a(i, j - 1, k);

                if (block.func_149688_o() == Material.field_151579_a && block1.func_149688_o() != Material.field_151579_a && block1.func_149686_d()) {
                    this.field_70170_p.func_147465_d(i, j, k, this.func_146080_bZ(), this.func_70824_q(), 3);
                    this.func_146081_a(Blocks.field_150350_a);
                }
            }
        }

        for (i = 0; i < 2; ++i) {
            this.field_70170_p.func_72869_a("portal", this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double) this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double) this.field_70131_O - 0.25D, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double) this.field_70130_N, (this.field_70146_Z.nextDouble() - 0.5D) * 2.0D, -this.field_70146_Z.nextDouble(), (this.field_70146_Z.nextDouble() - 0.5D) * 2.0D);
        }

        if (this.field_70170_p.func_72935_r() && !this.field_70170_p.field_72995_K) {
            float f = this.func_70013_c(1.0F);

            if (f > 0.5F && this.field_70170_p.func_72937_j(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)) && this.field_70146_Z.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.field_70789_a = null;
                this.func_70819_e(false);
                this.field_104003_g = false;
                this.func_70820_n();
            }
        }

        if (this.func_70026_G() || this.func_70027_ad()) {
            this.field_70789_a = null;
            this.func_70819_e(false);
            this.field_104003_g = false;
            this.func_70820_n();
        }

        if (this.func_70823_r() && !this.field_104003_g && this.field_70146_Z.nextInt(100) == 0) {
            this.func_70819_e(false);
        }

        this.field_70703_bu = false;
        if (this.field_70789_a != null) {
            this.func_70625_a(this.field_70789_a, 100.0F, 100.0F);
        }

        if (!this.field_70170_p.field_72995_K && this.func_70089_S()) {
            if (this.field_70789_a != null) {
                if (this.field_70789_a instanceof EntityPlayer && this.func_70821_d((EntityPlayer) this.field_70789_a)) {
                    if (this.field_70789_a.func_70068_e(this) < 16.0D) {
                        this.func_70820_n();
                    }

                    this.field_70828_e = 0;
                } else if (this.field_70789_a.func_70068_e(this) > 256.0D && this.field_70828_e++ >= 30 && this.func_70816_c(this.field_70789_a)) {
                    this.field_70828_e = 0;
                }
            } else {
                this.func_70819_e(false);
                this.field_70828_e = 0;
            }
        }

        super.func_70636_d();
    }

    protected boolean func_70820_n() {
        double d0 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * 64.0D;
        double d1 = this.field_70163_u + (double) (this.field_70146_Z.nextInt(64) - 32);
        double d2 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * 64.0D;

        return this.func_70825_j(d0, d1, d2);
    }

    protected boolean func_70816_c(Entity entity) {
        Vec3 vec3 = Vec3.func_72443_a(this.field_70165_t - entity.field_70165_t, this.field_70121_D.field_72338_b + (double) (this.field_70131_O / 2.0F) - entity.field_70163_u + (double) entity.func_70047_e(), this.field_70161_v - entity.field_70161_v);

        vec3 = vec3.func_72432_b();
        double d0 = 16.0D;
        double d1 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * 8.0D - vec3.field_72450_a * d0;
        double d2 = this.field_70163_u + (double) (this.field_70146_Z.nextInt(16) - 8) - vec3.field_72448_b * d0;
        double d3 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * 8.0D - vec3.field_72449_c * d0;

        return this.func_70825_j(d1, d2, d3);
    }

    protected boolean func_70825_j(double d0, double d1, double d2) {
        double d3 = this.field_70165_t;
        double d4 = this.field_70163_u;
        double d5 = this.field_70161_v;

        this.field_70165_t = d0;
        this.field_70163_u = d1;
        this.field_70161_v = d2;
        boolean flag = false;
        int i = MathHelper.func_76128_c(this.field_70165_t);
        int j = MathHelper.func_76128_c(this.field_70163_u);
        int k = MathHelper.func_76128_c(this.field_70161_v);

        if (this.field_70170_p.func_72899_e(i, j, k)) {
            boolean flag1 = false;

            while (!flag1 && j > 0) {
                Block block = this.field_70170_p.func_147439_a(i, j - 1, k);

                if (block.func_149688_o().func_76230_c()) {
                    flag1 = true;
                } else {
                    --this.field_70163_u;
                    --j;
                }
            }

            if (flag1) {
                this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                if (this.field_70170_p.func_72945_a(this, this.field_70121_D).isEmpty() && !this.field_70170_p.func_72953_d(this.field_70121_D)) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            this.func_70107_b(d3, d4, d5);
            return false;
        } else {
            short short0 = 128;

            for (int l = 0; l < short0; ++l) {
                double d6 = (double) l / ((double) short0 - 1.0D);
                float f = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (this.field_70165_t - d3) * d6 + (this.field_70146_Z.nextDouble() - 0.5D) * (double) this.field_70130_N * 2.0D;
                double d8 = d4 + (this.field_70163_u - d4) * d6 + this.field_70146_Z.nextDouble() * (double) this.field_70131_O;
                double d9 = d5 + (this.field_70161_v - d5) * d6 + (this.field_70146_Z.nextDouble() - 0.5D) * (double) this.field_70130_N * 2.0D;

                this.field_70170_p.func_72869_a("portal", d7, d8, d9, (double) f, (double) f1, (double) f2);
            }

            this.field_70170_p.func_72908_a(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
            this.func_85030_a("mob.endermen.portal", 1.0F, 1.0F);
            return true;
        }
    }

    @Override
    protected String func_70639_aQ() {
        return this.func_70823_r() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    @Override
    protected String func_70621_aR() {
        return "mob.endermen.hit";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.endermen.death";
    }

    @Override
    protected Item func_146068_u() {
        return Items.field_151079_bi;
    }

    @Override
    protected void func_70628_a(boolean flag, int i) {
        Item item = this.func_146068_u();

        if (item != null) {
            int j = this.field_70146_Z.nextInt(2 + i);

            for (int k = 0; k < j; ++k) {
                this.func_145779_a(item, 1);
            }
        }

    }

    public void func_146081_a(Block block) {
        this.field_70180_af.func_75692_b(16, (byte) (Block.func_149682_b(block) & 255));
    }

    public Block func_146080_bZ() {
        return Block.func_149729_e(this.field_70180_af.func_75683_a(16));
    }

    public void func_70817_b(int i) {
        this.field_70180_af.func_75692_b(17, (byte) (i & 255));
    }

    public int func_70824_q() {
        return this.field_70180_af.func_75683_a(17);
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else {
            this.func_70819_e(true);
            if (damagesource instanceof EntityDamageSource && damagesource.func_76346_g() instanceof EntityPlayer) {
                this.field_104003_g = true;
            }

            if (damagesource instanceof EntityDamageSourceIndirect) {
                this.field_104003_g = false;

                for (int i = 0; i < 64; ++i) {
                    if (this.func_70820_n()) {
                        return true;
                    }
                }

                return false;
            } else {
                return super.func_70097_a(damagesource, f);
            }
        }
    }

    public boolean func_70823_r() {
        return this.field_70180_af.func_75683_a(18) > 0;
    }

    public void func_70819_e(boolean flag) {
        this.field_70180_af.func_75692_b(18, (byte) (flag ? 1 : 0));
    }

    static {
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150349_c)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150346_d)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150354_m)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150351_n)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150327_N)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150328_O)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150338_P)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150337_Q)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150335_W)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150434_aF)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150435_aG)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150423_aK)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150440_ba)] = true;
        EntityEnderman.field_70827_d[Block.func_149682_b(Blocks.field_150391_bh)] = true;
    }
}
