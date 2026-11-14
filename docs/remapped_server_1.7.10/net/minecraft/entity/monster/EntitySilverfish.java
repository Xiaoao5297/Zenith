package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class EntitySilverfish extends EntityMob {

    private int field_70843_d;

    public EntitySilverfish(World world) {
        super(world);
        this.func_70105_a(0.3F, 0.7F);
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0D);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.6000000238418579D);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0D);
    }

    @Override
    protected boolean func_70041_e_() {
        return false;
    }

    @Override
    protected Entity func_70782_k() {
        double d0 = 8.0D;

        return this.field_70170_p.func_72856_b(this, d0);
    }

    @Override
    protected String func_70639_aQ() {
        return "mob.silverfish.say";
    }

    @Override
    protected String func_70621_aR() {
        return "mob.silverfish.hit";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.silverfish.kill";
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else {
            if (this.field_70843_d <= 0 && (damagesource instanceof EntityDamageSource || damagesource == DamageSource.field_76376_m)) {
                this.field_70843_d = 20;
            }

            return super.func_70097_a(damagesource, f);
        }
    }

    @Override
    protected void func_70785_a(Entity entity, float f) {
        if (this.field_70724_aR <= 0 && f < 1.2F && entity.field_70121_D.field_72337_e > this.field_70121_D.field_72338_b && entity.field_70121_D.field_72338_b < this.field_70121_D.field_72337_e) {
            this.field_70724_aR = 20;
            this.func_70652_k(entity);
        }

    }

    @Override
    protected void func_145780_a(int i, int j, int k, Block block) {
        this.func_85030_a("mob.silverfish.step", 0.15F, 1.0F);
    }

    @Override
    protected Item func_146068_u() {
        return Item.func_150899_d(0);
    }

    @Override
    public void func_70071_h_() {
        this.field_70761_aq = this.field_70177_z;
        super.func_70071_h_();
    }

    @Override
    protected void func_70626_be() {
        super.func_70626_be();
        if (!this.field_70170_p.field_72995_K) {
            int i;
            int j;
            int k;
            int l;

            if (this.field_70843_d > 0) {
                --this.field_70843_d;
                if (this.field_70843_d == 0) {
                    i = MathHelper.func_76128_c(this.field_70165_t);
                    j = MathHelper.func_76128_c(this.field_70163_u);
                    k = MathHelper.func_76128_c(this.field_70161_v);
                    boolean flag = false;

                    for (int i1 = 0; !flag && i1 <= 5 && i1 >= -5; i1 = i1 <= 0 ? 1 - i1 : 0 - i1) {
                        for (l = 0; !flag && l <= 10 && l >= -10; l = l <= 0 ? 1 - l : 0 - l) {
                            for (int j1 = 0; !flag && j1 <= 10 && j1 >= -10; j1 = j1 <= 0 ? 1 - j1 : 0 - j1) {
                                if (this.field_70170_p.func_147439_a(i + l, j + i1, k + j1) == Blocks.field_150418_aU) {
                                    if (!this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
                                        int k1 = this.field_70170_p.func_72805_g(i + l, j + i1, k + j1);
                                        ImmutablePair immutablepair = BlockSilverfish.func_150197_b(k1);

                                        this.field_70170_p.func_147465_d(i + l, j + i1, k + j1, (Block) immutablepair.getLeft(), (Integer) immutablepair.getRight(), 3);
                                    } else {
                                        this.field_70170_p.func_147480_a(i + l, j + i1, k + j1, false);
                                    }

                                    Blocks.field_150418_aU.func_149664_b(this.field_70170_p, i + l, j + i1, k + j1, 0);
                                    if (this.field_70146_Z.nextBoolean()) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (this.field_70789_a == null && !this.func_70781_l()) {
                i = MathHelper.func_76128_c(this.field_70165_t);
                j = MathHelper.func_76128_c(this.field_70163_u + 0.5D);
                k = MathHelper.func_76128_c(this.field_70161_v);
                int l1 = this.field_70146_Z.nextInt(6);
                Block block = this.field_70170_p.func_147439_a(i + Facing.field_71586_b[l1], j + Facing.field_71587_c[l1], k + Facing.field_71585_d[l1]);

                l = this.field_70170_p.func_72805_g(i + Facing.field_71586_b[l1], j + Facing.field_71587_c[l1], k + Facing.field_71585_d[l1]);
                if (BlockSilverfish.func_150196_a(block)) {
                    this.field_70170_p.func_147465_d(i + Facing.field_71586_b[l1], j + Facing.field_71587_c[l1], k + Facing.field_71585_d[l1], Blocks.field_150418_aU, BlockSilverfish.func_150195_a(block, l), 3);
                    this.func_70656_aK();
                    this.func_70106_y();
                } else {
                    this.func_70779_j();
                }
            } else if (this.field_70789_a != null && !this.func_70781_l()) {
                this.field_70789_a = null;
            }

        }
    }

    @Override
    public float func_70783_a(int i, int j, int k) {
        return this.field_70170_p.func_147439_a(i, j - 1, k) == Blocks.field_150348_b ? 10.0F : super.func_70783_a(i, j, k);
    }

    @Override
    protected boolean func_70814_o() {
        return true;
    }

    @Override
    public boolean func_70601_bi() {
        if (super.func_70601_bi()) {
            EntityPlayer entityplayer = this.field_70170_p.func_72890_a(this, 5.0D);

            return entityplayer == null;
        } else {
            return false;
        }
    }

    @Override
    public EnumCreatureAttribute func_70668_bt() {
        return EnumCreatureAttribute.ARTHROPOD;
    }
}
