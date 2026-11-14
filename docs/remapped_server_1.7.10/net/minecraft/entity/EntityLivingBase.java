package net.minecraft.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Block$SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity {

    private static final UUID field_110156_b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier field_110157_c = (new AttributeModifier(EntityLivingBase.field_110156_b, "Sprinting speed boost", 0.30000001192092896D, 2)).func_111168_a(false);
    private BaseAttributeMap field_110155_d;
    private final CombatTracker field_94063_bt = new CombatTracker(this);
    private final HashMap field_70713_bf = new HashMap();
    private final ItemStack[] field_82180_bT = new ItemStack[5];
    public boolean field_82175_bq;
    public int field_110158_av;
    public int field_70720_be;
    public float field_70735_aL;
    public int field_70737_aN;
    public int field_70738_aO;
    public float field_70739_aP;
    public int field_70725_aQ;
    public int field_70724_aR;
    public float field_70732_aI;
    public float field_70733_aJ;
    public float field_70722_aY;
    public float field_70721_aZ;
    public float field_70754_ba;
    public int field_70771_an = 20;
    public float field_70727_aS;
    public float field_70726_aT;
    public float field_70769_ao;
    public float field_70770_ap;
    public float field_70761_aq;
    public float field_70760_ar;
    public float field_70759_as;
    public float field_70758_at;
    public float field_70747_aH = 0.02F;
    protected EntityPlayer field_70717_bb;
    protected int field_70718_bc;
    protected boolean field_70729_aU;
    protected int field_70708_bq;
    protected float field_70768_au;
    protected float field_110154_aX;
    protected float field_70764_aw;
    protected float field_70763_ax;
    protected float field_70741_aB;
    protected int field_70744_aE;
    protected float field_110153_bc;
    protected boolean field_70703_bu;
    public float field_70702_br;
    public float field_70701_bs;
    protected float field_70704_bt;
    protected int field_70716_bi;
    protected double field_70709_bj;
    protected double field_70710_bk;
    protected double field_110152_bk;
    protected double field_70712_bm;
    protected double field_70705_bn;
    private boolean field_70752_e = true;
    private EntityLivingBase field_70755_b;
    private int field_70756_c;
    private EntityLivingBase field_110150_bn;
    private int field_142016_bo;
    private float field_70746_aG;
    private int field_70773_bE;
    private float field_110151_bq;

    public EntityLivingBase(World world) {
        super(world);
        this.func_110147_ax();
        this.func_70606_j(this.func_110138_aP());
        this.field_70156_m = true;
        this.field_70770_ap = (float) (Math.random() + 1.0D) * 0.01F;
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70769_ao = (float) Math.random() * 12398.0F;
        this.field_70177_z = (float) (Math.random() * 3.1415927410125732D * 2.0D);
        this.field_70759_as = this.field_70177_z;
        this.field_70138_W = 0.5F;
    }

    @Override
    protected void func_70088_a() {
        this.field_70180_af.func_75682_a(7, 0);
        this.field_70180_af.func_75682_a(8, (byte) 0);
        this.field_70180_af.func_75682_a(9, (byte) 0);
        this.field_70180_af.func_75682_a(6, 1.0F);
    }

    protected void func_110147_ax() {
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111267_a);
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111266_c);
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111263_d);
        if (!this.func_70650_aV()) {
            this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.10000000149011612D);
        }

    }

    @Override
    protected void func_70064_a(double d0, boolean flag) {
        if (!this.func_70090_H()) {
            this.func_70072_I();
        }

        if (flag && this.field_70143_R > 0.0F) {
            int i = MathHelper.func_76128_c(this.field_70165_t);
            int j = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D - (double) this.field_70129_M);
            int k = MathHelper.func_76128_c(this.field_70161_v);
            Block block = this.field_70170_p.func_147439_a(i, j, k);

            if (block.func_149688_o() == Material.field_151579_a) {
                int l = this.field_70170_p.func_147439_a(i, j - 1, k).func_149645_b();

                if (l == 11 || l == 32 || l == 21) {
                    block = this.field_70170_p.func_147439_a(i, j - 1, k);
                }
            } else if (!this.field_70170_p.field_72995_K && this.field_70143_R > 3.0F) {
                this.field_70170_p.func_72926_e(2006, i, j, k, MathHelper.func_76123_f(this.field_70143_R - 3.0F));
            }

            block.func_149746_a(this.field_70170_p, i, j, k, this, this.field_70143_R);
        }

        super.func_70064_a(d0, flag);
    }

    public boolean func_70648_aU() {
        return false;
    }

    @Override
    public void func_70030_z() {
        this.field_70732_aI = this.field_70733_aJ;
        super.func_70030_z();
        this.field_70170_p.field_72984_F.func_76320_a("livingEntityBaseTick");
        if (this.func_70089_S() && this.func_70094_T()) {
            this.func_70097_a(DamageSource.field_76368_d, 1.0F);
        }

        if (this.func_70045_F() || this.field_70170_p.field_72995_K) {
            this.func_70066_B();
        }

        boolean flag = this instanceof EntityPlayer && ((EntityPlayer) this).field_71075_bZ.field_75102_a;

        if (this.func_70089_S() && this.func_70055_a(Material.field_151586_h)) {
            if (!this.func_70648_aU() && !this.func_82165_m(Potion.field_76427_o.field_76415_H) && !flag) {
                this.func_70050_g(this.func_70682_h(this.func_70086_ai()));
                if (this.func_70086_ai() == -20) {
                    this.func_70050_g(0);

                    for (int i = 0; i < 8; ++i) {
                        float f = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                        float f1 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                        float f2 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();

                        this.field_70170_p.func_72869_a("bubble", this.field_70165_t + (double) f, this.field_70163_u + (double) f1, this.field_70161_v + (double) f2, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                    }

                    this.func_70097_a(DamageSource.field_76369_e, 2.0F);
                }
            }

            if (!this.field_70170_p.field_72995_K && this.func_70115_ae() && this.field_70154_o instanceof EntityLivingBase) {
                this.func_70078_a((Entity) null);
            }
        } else {
            this.func_70050_g(300);
        }

        if (this.func_70089_S() && this.func_70026_G()) {
            this.func_70066_B();
        }

        this.field_70727_aS = this.field_70726_aT;
        if (this.field_70724_aR > 0) {
            --this.field_70724_aR;
        }

        if (this.field_70737_aN > 0) {
            --this.field_70737_aN;
        }

        if (this.field_70172_ad > 0 && !(this instanceof EntityPlayerMP)) {
            --this.field_70172_ad;
        }

        if (this.func_110143_aJ() <= 0.0F) {
            this.func_70609_aI();
        }

        if (this.field_70718_bc > 0) {
            --this.field_70718_bc;
        } else {
            this.field_70717_bb = null;
        }

        if (this.field_110150_bn != null && !this.field_110150_bn.func_70089_S()) {
            this.field_110150_bn = null;
        }

        if (this.field_70755_b != null) {
            if (!this.field_70755_b.func_70089_S()) {
                this.func_70604_c((EntityLivingBase) null);
            } else if (this.field_70173_aa - this.field_70756_c > 100) {
                this.func_70604_c((EntityLivingBase) null);
            }
        }

        this.func_70679_bo();
        this.field_70763_ax = this.field_70764_aw;
        this.field_70760_ar = this.field_70761_aq;
        this.field_70758_at = this.field_70759_as;
        this.field_70126_B = this.field_70177_z;
        this.field_70127_C = this.field_70125_A;
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    public boolean func_70631_g_() {
        return false;
    }

    protected void func_70609_aI() {
        ++this.field_70725_aQ;
        if (this.field_70725_aQ == 20) {
            int i;

            if (!this.field_70170_p.field_72995_K && (this.field_70718_bc > 0 || this.func_70684_aJ()) && this.func_146066_aG() && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot")) {
                i = this.func_70693_a(this.field_70717_bb);

                while (i > 0) {
                    int j = EntityXPOrb.func_70527_a(i);

                    i -= j;
                    this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, j));
                }
            }

            this.func_70106_y();

            for (i = 0; i < 20; ++i) {
                double d0 = this.field_70146_Z.nextGaussian() * 0.02D;
                double d1 = this.field_70146_Z.nextGaussian() * 0.02D;
                double d2 = this.field_70146_Z.nextGaussian() * 0.02D;

                this.field_70170_p.func_72869_a("explode", this.field_70165_t + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, this.field_70163_u + (double) (this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, d0, d1, d2);
            }
        }

    }

    protected boolean func_146066_aG() {
        return !this.func_70631_g_();
    }

    protected int func_70682_h(int i) {
        int j = EnchantmentHelper.func_77501_a(this);

        return j > 0 && this.field_70146_Z.nextInt(j + 1) > 0 ? i : i - 1;
    }

    protected int func_70693_a(EntityPlayer entityplayer) {
        return 0;
    }

    protected boolean func_70684_aJ() {
        return false;
    }

    public Random func_70681_au() {
        return this.field_70146_Z;
    }

    public EntityLivingBase func_70643_av() {
        return this.field_70755_b;
    }

    public int func_142015_aE() {
        return this.field_70756_c;
    }

    public void func_70604_c(EntityLivingBase entitylivingbase) {
        this.field_70755_b = entitylivingbase;
        this.field_70756_c = this.field_70173_aa;
    }

    public EntityLivingBase func_110144_aD() {
        return this.field_110150_bn;
    }

    public int func_142013_aG() {
        return this.field_142016_bo;
    }

    public void func_130011_c(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            this.field_110150_bn = (EntityLivingBase) entity;
        } else {
            this.field_110150_bn = null;
        }

        this.field_142016_bo = this.field_70173_aa;
    }

    public int func_70654_ax() {
        return this.field_70708_bq;
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74776_a("HealF", this.func_110143_aJ());
        nbttagcompound.func_74777_a("Health", (short) ((int) Math.ceil((double) this.func_110143_aJ())));
        nbttagcompound.func_74777_a("HurtTime", (short) this.field_70737_aN);
        nbttagcompound.func_74777_a("DeathTime", (short) this.field_70725_aQ);
        nbttagcompound.func_74777_a("AttackTime", (short) this.field_70724_aR);
        nbttagcompound.func_74776_a("AbsorptionAmount", this.func_110139_bj());
        ItemStack[] aitemstack = this.func_70035_c();
        int i = aitemstack.length;

        ItemStack itemstack;
        int j;

        for (j = 0; j < i; ++j) {
            itemstack = aitemstack[j];
            if (itemstack != null) {
                this.field_110155_d.func_111148_a(itemstack.func_111283_C());
            }
        }

        nbttagcompound.func_74782_a("Attributes", SharedMonsterAttributes.func_111257_a(this.func_110140_aT()));
        aitemstack = this.func_70035_c();
        i = aitemstack.length;

        for (j = 0; j < i; ++j) {
            itemstack = aitemstack[j];
            if (itemstack != null) {
                this.field_110155_d.func_111147_b(itemstack.func_111283_C());
            }
        }

        if (!this.field_70713_bf.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.field_70713_bf.values().iterator();

            while (iterator.hasNext()) {
                PotionEffect potioneffect = (PotionEffect) iterator.next();

                nbttaglist.func_74742_a(potioneffect.func_82719_a(new NBTTagCompound()));
            }

            nbttagcompound.func_74782_a("ActiveEffects", nbttaglist);
        }

    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        this.func_110149_m(nbttagcompound.func_74760_g("AbsorptionAmount"));
        if (nbttagcompound.func_150297_b("Attributes", 9) && this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
            SharedMonsterAttributes.func_151475_a(this.func_110140_aT(), nbttagcompound.func_150295_c("Attributes", 10));
        }

        if (nbttagcompound.func_150297_b("ActiveEffects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.func_150295_c("ActiveEffects", 10);

            for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
                PotionEffect potioneffect = PotionEffect.func_82722_b(nbttagcompound1);

                if (potioneffect != null) {
                    this.field_70713_bf.put(potioneffect.func_76456_a(), potioneffect);
                }
            }
        }

        if (nbttagcompound.func_150297_b("HealF", 99)) {
            this.func_70606_j(nbttagcompound.func_74760_g("HealF"));
        } else {
            NBTBase nbtbase = nbttagcompound.func_74781_a("Health");

            if (nbtbase == null) {
                this.func_70606_j(this.func_110138_aP());
            } else if (nbtbase.func_74732_a() == 5) {
                this.func_70606_j(((NBTTagFloat) nbtbase).func_150288_h());
            } else if (nbtbase.func_74732_a() == 2) {
                this.func_70606_j((float) ((NBTTagShort) nbtbase).func_150289_e());
            }
        }

        this.field_70737_aN = nbttagcompound.func_74765_d("HurtTime");
        this.field_70725_aQ = nbttagcompound.func_74765_d("DeathTime");
        this.field_70724_aR = nbttagcompound.func_74765_d("AttackTime");
    }

    protected void func_70679_bo() {
        Iterator iterator = this.field_70713_bf.keySet().iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            PotionEffect potioneffect = (PotionEffect) this.field_70713_bf.get(integer);

            if (!potioneffect.func_76455_a(this)) {
                if (!this.field_70170_p.field_72995_K) {
                    iterator.remove();
                    this.func_70688_c(potioneffect);
                }
            } else if (potioneffect.func_76459_b() % 600 == 0) {
                this.func_70695_b(potioneffect, false);
            }
        }

        int i;

        if (this.field_70752_e) {
            if (!this.field_70170_p.field_72995_K) {
                if (this.field_70713_bf.isEmpty()) {
                    this.field_70180_af.func_75692_b(8, (byte) 0);
                    this.field_70180_af.func_75692_b(7, 0);
                    this.func_82142_c(false);
                } else {
                    i = PotionHelper.func_77911_a(this.field_70713_bf.values());
                    this.field_70180_af.func_75692_b(8, Byte.valueOf((byte) (PotionHelper.func_82817_b(this.field_70713_bf.values()) ? 1 : 0)));
                    this.field_70180_af.func_75692_b(7, i);
                    this.func_82142_c(this.func_82165_m(Potion.field_76441_p.field_76415_H));
                }
            }

            this.field_70752_e = false;
        }

        i = this.field_70180_af.func_75679_c(7);
        boolean flag = this.field_70180_af.func_75683_a(8) > 0;

        if (i > 0) {
            boolean flag1 = false;

            if (!this.func_82150_aj()) {
                flag1 = this.field_70146_Z.nextBoolean();
            } else {
                flag1 = this.field_70146_Z.nextInt(15) == 0;
            }

            if (flag) {
                flag1 &= this.field_70146_Z.nextInt(5) == 0;
            }

            if (flag1 && i > 0) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;

                this.field_70170_p.func_72869_a(flag ? "mobSpellAmbient" : "mobSpell", this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double) this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double) this.field_70131_O - (double) this.field_70129_M, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double) this.field_70130_N, d0, d1, d2);
            }
        }

    }

    public void func_70674_bp() {
        Iterator iterator = this.field_70713_bf.keySet().iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            PotionEffect potioneffect = (PotionEffect) this.field_70713_bf.get(integer);

            if (!this.field_70170_p.field_72995_K) {
                iterator.remove();
                this.func_70688_c(potioneffect);
            }
        }

    }

    public Collection func_70651_bq() {
        return this.field_70713_bf.values();
    }

    public boolean func_82165_m(int i) {
        return this.field_70713_bf.containsKey(i);
    }

    public boolean func_70644_a(Potion potion) {
        return this.field_70713_bf.containsKey(potion.field_76415_H);
    }

    public PotionEffect func_70660_b(Potion potion) {
        return (PotionEffect) this.field_70713_bf.get(potion.field_76415_H);
    }

    public void func_70690_d(PotionEffect potioneffect) {
        if (this.func_70687_e(potioneffect)) {
            if (this.field_70713_bf.containsKey(potioneffect.func_76456_a())) {
                ((PotionEffect) this.field_70713_bf.get(potioneffect.func_76456_a())).func_76452_a(potioneffect);
                this.func_70695_b((PotionEffect) this.field_70713_bf.get(potioneffect.func_76456_a()), true);
            } else {
                this.field_70713_bf.put(potioneffect.func_76456_a(), potioneffect);
                this.func_70670_a(potioneffect);
            }

        }
    }

    public boolean func_70687_e(PotionEffect potioneffect) {
        if (this.func_70668_bt() == EnumCreatureAttribute.UNDEAD) {
            int i = potioneffect.func_76456_a();

            if (i == Potion.field_76428_l.field_76415_H || i == Potion.field_76436_u.field_76415_H) {
                return false;
            }
        }

        return true;
    }

    public boolean func_70662_br() {
        return this.func_70668_bt() == EnumCreatureAttribute.UNDEAD;
    }

    public void func_82170_o(int i) {
        PotionEffect potioneffect = (PotionEffect) this.field_70713_bf.remove(i);

        if (potioneffect != null) {
            this.func_70688_c(potioneffect);
        }

    }

    protected void func_70670_a(PotionEffect potioneffect) {
        this.field_70752_e = true;
        if (!this.field_70170_p.field_72995_K) {
            Potion.field_76425_a[potioneffect.func_76456_a()].func_111185_a(this, this.func_110140_aT(), potioneffect.func_76458_c());
        }

    }

    protected void func_70695_b(PotionEffect potioneffect, boolean flag) {
        this.field_70752_e = true;
        if (flag && !this.field_70170_p.field_72995_K) {
            Potion.field_76425_a[potioneffect.func_76456_a()].func_111187_a(this, this.func_110140_aT(), potioneffect.func_76458_c());
            Potion.field_76425_a[potioneffect.func_76456_a()].func_111185_a(this, this.func_110140_aT(), potioneffect.func_76458_c());
        }

    }

    protected void func_70688_c(PotionEffect potioneffect) {
        this.field_70752_e = true;
        if (!this.field_70170_p.field_72995_K) {
            Potion.field_76425_a[potioneffect.func_76456_a()].func_111187_a(this, this.func_110140_aT(), potioneffect.func_76458_c());
        }

    }

    public void func_70691_i(float f) {
        float f1 = this.func_110143_aJ();

        if (f1 > 0.0F) {
            this.func_70606_j(f1 + f);
        }

    }

    public final float func_110143_aJ() {
        return this.field_70180_af.func_111145_d(6);
    }

    public void func_70606_j(float f) {
        this.field_70180_af.func_75692_b(6, MathHelper.func_76131_a(f, 0.0F, this.func_110138_aP()));
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else if (this.field_70170_p.field_72995_K) {
            return false;
        } else {
            this.field_70708_bq = 0;
            if (this.func_110143_aJ() <= 0.0F) {
                return false;
            } else if (damagesource.func_76347_k() && this.func_70644_a(Potion.field_76426_n)) {
                return false;
            } else {
                if ((damagesource == DamageSource.field_82728_o || damagesource == DamageSource.field_82729_p) && this.func_71124_b(4) != null) {
                    this.func_71124_b(4).func_77972_a((int) (f * 4.0F + this.field_70146_Z.nextFloat() * f * 2.0F), this);
                    f *= 0.75F;
                }

                this.field_70721_aZ = 1.5F;
                boolean flag = true;

                if ((float) this.field_70172_ad > (float) this.field_70771_an / 2.0F) {
                    if (f <= this.field_110153_bc) {
                        return false;
                    }

                    this.func_70665_d(damagesource, f - this.field_110153_bc);
                    this.field_110153_bc = f;
                    flag = false;
                } else {
                    this.field_110153_bc = f;
                    this.field_70735_aL = this.func_110143_aJ();
                    this.field_70172_ad = this.field_70771_an;
                    this.func_70665_d(damagesource, f);
                    this.field_70737_aN = this.field_70738_aO = 10;
                }

                this.field_70739_aP = 0.0F;
                Entity entity = damagesource.func_76346_g();

                if (entity != null) {
                    if (entity instanceof EntityLivingBase) {
                        this.func_70604_c((EntityLivingBase) entity);
                    }

                    if (entity instanceof EntityPlayer) {
                        this.field_70718_bc = 100;
                        this.field_70717_bb = (EntityPlayer) entity;
                    } else if (entity instanceof EntityWolf) {
                        EntityWolf entitywolf = (EntityWolf) entity;

                        if (entitywolf.func_70909_n()) {
                            this.field_70718_bc = 100;
                            this.field_70717_bb = null;
                        }
                    }
                }

                if (flag) {
                    this.field_70170_p.func_72960_a(this, (byte) 2);
                    if (damagesource != DamageSource.field_76369_e) {
                        this.func_70018_K();
                    }

                    if (entity != null) {
                        double d0 = entity.field_70165_t - this.field_70165_t;

                        double d1;

                        for (d1 = entity.field_70161_v - this.field_70161_v; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                            d0 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.field_70739_aP = (float) (Math.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - this.field_70177_z;
                        this.func_70653_a(entity, f, d0, d1);
                    } else {
                        this.field_70739_aP = (float) ((int) (Math.random() * 2.0D) * 180);
                    }
                }

                String s;

                if (this.func_110143_aJ() <= 0.0F) {
                    s = this.func_70673_aS();
                    if (flag && s != null) {
                        this.func_85030_a(s, this.func_70599_aP(), this.func_70647_i());
                    }

                    this.func_70645_a(damagesource);
                } else {
                    s = this.func_70621_aR();
                    if (flag && s != null) {
                        this.func_85030_a(s, this.func_70599_aP(), this.func_70647_i());
                    }
                }

                return true;
            }
        }
    }

    public void func_70669_a(ItemStack itemstack) {
        this.func_85030_a("random.break", 0.8F, 0.8F + this.field_70170_p.field_73012_v.nextFloat() * 0.4F);

        for (int i = 0; i < 5; ++i) {
            Vec3 vec3 = Vec3.func_72443_a(((double) this.field_70146_Z.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

            vec3.func_72440_a(-this.field_70125_A * 3.1415927F / 180.0F);
            vec3.func_72442_b(-this.field_70177_z * 3.1415927F / 180.0F);
            Vec3 vec31 = Vec3.func_72443_a(((double) this.field_70146_Z.nextFloat() - 0.5D) * 0.3D, (double) (-this.field_70146_Z.nextFloat()) * 0.6D - 0.3D, 0.6D);

            vec31.func_72440_a(-this.field_70125_A * 3.1415927F / 180.0F);
            vec31.func_72442_b(-this.field_70177_z * 3.1415927F / 180.0F);
            vec31 = vec31.func_72441_c(this.field_70165_t, this.field_70163_u + (double) this.func_70047_e(), this.field_70161_v);
            this.field_70170_p.func_72869_a("iconcrack_" + Item.func_150891_b(itemstack.func_77973_b()), vec31.field_72450_a, vec31.field_72448_b, vec31.field_72449_c, vec3.field_72450_a, vec3.field_72448_b + 0.05D, vec3.field_72449_c);
        }

    }

    public void func_70645_a(DamageSource damagesource) {
        Entity entity = damagesource.func_76346_g();
        EntityLivingBase entitylivingbase = this.func_94060_bK();

        if (this.field_70744_aE >= 0 && entitylivingbase != null) {
            entitylivingbase.func_70084_c(this, this.field_70744_aE);
        }

        if (entity != null) {
            entity.func_70074_a(this);
        }

        this.field_70729_aU = true;
        this.func_110142_aN().func_94549_h();
        if (!this.field_70170_p.field_72995_K) {
            int i = 0;

            if (entity instanceof EntityPlayer) {
                i = EnchantmentHelper.func_77519_f((EntityLivingBase) entity);
            }

            if (this.func_146066_aG() && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot")) {
                this.func_70628_a(this.field_70718_bc > 0, i);
                this.func_82160_b(this.field_70718_bc > 0, i);
                if (this.field_70718_bc > 0) {
                    int j = this.field_70146_Z.nextInt(200) - i;

                    if (j < 5) {
                        this.func_70600_l(j <= 0 ? 1 : 0);
                    }
                }
            }
        }

        this.field_70170_p.func_72960_a(this, (byte) 3);
    }

    protected void func_82160_b(boolean flag, int i) {}

    public void func_70653_a(Entity entity, float f, double d0, double d1) {
        if (this.field_70146_Z.nextDouble() >= this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111126_e()) {
            this.field_70160_al = true;
            float f1 = MathHelper.func_76133_a(d0 * d0 + d1 * d1);
            float f2 = 0.4F;

            this.field_70159_w /= 2.0D;
            this.field_70181_x /= 2.0D;
            this.field_70179_y /= 2.0D;
            this.field_70159_w -= d0 / (double) f1 * (double) f2;
            this.field_70181_x += (double) f2;
            this.field_70179_y -= d1 / (double) f1 * (double) f2;
            if (this.field_70181_x > 0.4000000059604645D) {
                this.field_70181_x = 0.4000000059604645D;
            }

        }
    }

    protected String func_70621_aR() {
        return "game.neutral.hurt";
    }

    protected String func_70673_aS() {
        return "game.neutral.die";
    }

    protected void func_70600_l(int i) {}

    protected void func_70628_a(boolean flag, int i) {}

    public boolean func_70617_f_() {
        int i = MathHelper.func_76128_c(this.field_70165_t);
        int j = MathHelper.func_76128_c(this.field_70121_D.field_72338_b);
        int k = MathHelper.func_76128_c(this.field_70161_v);
        Block block = this.field_70170_p.func_147439_a(i, j, k);

        return block == Blocks.field_150468_ap || block == Blocks.field_150395_bd;
    }

    @Override
    public boolean func_70089_S() {
        return !this.field_70128_L && this.func_110143_aJ() > 0.0F;
    }

    @Override
    protected void func_70069_a(float f) {
        super.func_70069_a(f);
        PotionEffect potioneffect = this.func_70660_b(Potion.field_76430_j);
        float f1 = potioneffect != null ? (float) (potioneffect.func_76458_c() + 1) : 0.0F;
        int i = MathHelper.func_76123_f(f - 3.0F - f1);

        if (i > 0) {
            this.func_85030_a(this.func_146067_o(i), 1.0F, 1.0F);
            this.func_70097_a(DamageSource.field_76379_h, (float) i);
            int j = MathHelper.func_76128_c(this.field_70165_t);
            int k = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D - (double) this.field_70129_M);
            int l = MathHelper.func_76128_c(this.field_70161_v);
            Block block = this.field_70170_p.func_147439_a(j, k, l);

            if (block.func_149688_o() != Material.field_151579_a) {
                Block$SoundType block$soundtype = block.field_149762_H;

                this.func_85030_a(block$soundtype.func_150498_e(), block$soundtype.func_150497_c() * 0.5F, block$soundtype.func_150494_d() * 0.75F);
            }
        }

    }

    protected String func_146067_o(int i) {
        return i > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    public int func_70658_aO() {
        int i = 0;
        ItemStack[] aitemstack = this.func_70035_c();
        int j = aitemstack.length;

        for (int k = 0; k < j; ++k) {
            ItemStack itemstack = aitemstack[k];

            if (itemstack != null && itemstack.func_77973_b() instanceof ItemArmor) {
                int l = ((ItemArmor) itemstack.func_77973_b()).field_77879_b;

                i += l;
            }
        }

        return i;
    }

    protected void func_70675_k(float f) {}

    protected float func_70655_b(DamageSource damagesource, float f) {
        if (!damagesource.func_76363_c()) {
            int i = 25 - this.func_70658_aO();
            float f1 = f * (float) i;

            this.func_70675_k(f);
            f = f1 / 25.0F;
        }

        return f;
    }

    protected float func_70672_c(DamageSource damagesource, float f) {
        if (damagesource.func_151517_h()) {
            return f;
        } else {
            if (this instanceof EntityZombie) {
                f = f;
            }

            int i;
            int j;
            float f1;

            if (this.func_70644_a(Potion.field_76429_m) && damagesource != DamageSource.field_76380_i) {
                i = (this.func_70660_b(Potion.field_76429_m).func_76458_c() + 1) * 5;
                j = 25 - i;
                f1 = f * (float) j;
                f = f1 / 25.0F;
            }

            if (f <= 0.0F) {
                return 0.0F;
            } else {
                i = EnchantmentHelper.func_77508_a(this.func_70035_c(), damagesource);
                if (i > 20) {
                    i = 20;
                }

                if (i > 0 && i <= 20) {
                    j = 25 - i;
                    f1 = f * (float) j;
                    f = f1 / 25.0F;
                }

                return f;
            }
        }
    }

    protected void func_70665_d(DamageSource damagesource, float f) {
        if (!this.func_85032_ar()) {
            f = this.func_70655_b(damagesource, f);
            f = this.func_70672_c(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.func_110139_bj(), 0.0F);
            this.func_110149_m(this.func_110139_bj() - (f1 - f));
            if (f != 0.0F) {
                float f2 = this.func_110143_aJ();

                this.func_70606_j(f2 - f);
                this.func_110142_aN().func_94547_a(damagesource, f2, f);
                this.func_110149_m(this.func_110139_bj() - f);
            }
        }
    }

    public CombatTracker func_110142_aN() {
        return this.field_94063_bt;
    }

    public EntityLivingBase func_94060_bK() {
        return (EntityLivingBase) (this.field_94063_bt.func_94550_c() != null ? this.field_94063_bt.func_94550_c() : (this.field_70717_bb != null ? this.field_70717_bb : (this.field_70755_b != null ? this.field_70755_b : null)));
    }

    public final float func_110138_aP() {
        return (float) this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111126_e();
    }

    public final int func_85035_bI() {
        return this.field_70180_af.func_75683_a(9);
    }

    public final void func_85034_r(int i) {
        this.field_70180_af.func_75692_b(9, (byte) i);
    }

    private int func_82166_i() {
        return this.func_70644_a(Potion.field_76422_e) ? 6 - (1 + this.func_70660_b(Potion.field_76422_e).func_76458_c()) * 1 : (this.func_70644_a(Potion.field_76419_f) ? 6 + (1 + this.func_70660_b(Potion.field_76419_f).func_76458_c()) * 2 : 6);
    }

    public void func_71038_i() {
        if (!this.field_82175_bq || this.field_110158_av >= this.func_82166_i() / 2 || this.field_110158_av < 0) {
            this.field_110158_av = -1;
            this.field_82175_bq = true;
            if (this.field_70170_p instanceof WorldServer) {
                ((WorldServer) this.field_70170_p).func_73039_n().func_151247_a(this, new S0BPacketAnimation(this, 0));
            }
        }

    }

    @Override
    protected void func_70076_C() {
        this.func_70097_a(DamageSource.field_76380_i, 4.0F);
    }

    protected void func_82168_bl() {
        int i = this.func_82166_i();

        if (this.field_82175_bq) {
            ++this.field_110158_av;
            if (this.field_110158_av >= i) {
                this.field_110158_av = 0;
                this.field_82175_bq = false;
            }
        } else {
            this.field_110158_av = 0;
        }

        this.field_70733_aJ = (float) this.field_110158_av / (float) i;
    }

    public IAttributeInstance func_110148_a(IAttribute iattribute) {
        return this.func_110140_aT().func_111151_a(iattribute);
    }

    public BaseAttributeMap func_110140_aT() {
        if (this.field_110155_d == null) {
            this.field_110155_d = new ServersideAttributeMap();
        }

        return this.field_110155_d;
    }

    public EnumCreatureAttribute func_70668_bt() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public abstract ItemStack func_70694_bm();

    public abstract ItemStack func_71124_b(int i);

    @Override
    public abstract void func_70062_b(int i, ItemStack itemstack);

    @Override
    public void func_70031_b(boolean flag) {
        super.func_70031_b(flag);
        IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);

        if (iattributeinstance.func_111127_a(EntityLivingBase.field_110156_b) != null) {
            iattributeinstance.func_111124_b(EntityLivingBase.field_110157_c);
        }

        if (flag) {
            iattributeinstance.func_111121_a(EntityLivingBase.field_110157_c);
        }

    }

    @Override
    public abstract ItemStack[] func_70035_c();

    protected float func_70599_aP() {
        return 1.0F;
    }

    protected float func_70647_i() {
        return this.func_70631_g_() ? (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.5F : (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean func_70610_aX() {
        return this.func_110143_aJ() <= 0.0F;
    }

    public void func_70634_a(double d0, double d1, double d2) {
        this.func_70012_b(d0, d1, d2, this.field_70177_z, this.field_70125_A);
    }

    public void func_110145_l(Entity entity) {
        double d0 = entity.field_70165_t;
        double d1 = entity.field_70121_D.field_72338_b + (double) entity.field_70131_O;
        double d2 = entity.field_70161_v;
        byte b0 = 1;

        for (int i = -b0; i <= b0; ++i) {
            for (int j = -b0; j < b0; ++j) {
                if (i != 0 || j != 0) {
                    int k = (int) (this.field_70165_t + (double) i);
                    int l = (int) (this.field_70161_v + (double) j);
                    AxisAlignedBB axisalignedbb = this.field_70121_D.func_72325_c((double) i, 1.0D, (double) j);

                    if (this.field_70170_p.func_147461_a(axisalignedbb).isEmpty()) {
                        if (World.func_147466_a(this.field_70170_p, k, (int) this.field_70163_u, l)) {
                            this.func_70634_a(this.field_70165_t + (double) i, this.field_70163_u + 1.0D, this.field_70161_v + (double) j);
                            return;
                        }

                        if (World.func_147466_a(this.field_70170_p, k, (int) this.field_70163_u - 1, l) || this.field_70170_p.func_147439_a(k, (int) this.field_70163_u - 1, l).func_149688_o() == Material.field_151586_h) {
                            d0 = this.field_70165_t + (double) i;
                            d1 = this.field_70163_u + 1.0D;
                            d2 = this.field_70161_v + (double) j;
                        }
                    }
                }
            }
        }

        this.func_70634_a(d0, d1, d2);
    }

    protected void func_70664_aZ() {
        this.field_70181_x = 0.41999998688697815D;
        if (this.func_70644_a(Potion.field_76430_j)) {
            this.field_70181_x += (double) ((float) (this.func_70660_b(Potion.field_76430_j).func_76458_c() + 1) * 0.1F);
        }

        if (this.func_70051_ag()) {
            float f = this.field_70177_z * 0.017453292F;

            this.field_70159_w -= (double) (MathHelper.func_76126_a(f) * 0.2F);
            this.field_70179_y += (double) (MathHelper.func_76134_b(f) * 0.2F);
        }

        this.field_70160_al = true;
    }

    public void func_70612_e(float f, float f1) {
        double d0;

        if (this.func_70090_H() && (!(this instanceof EntityPlayer) || !((EntityPlayer) this).field_71075_bZ.field_75100_b)) {
            d0 = this.field_70163_u;
            this.func_70060_a(f, f1, this.func_70650_aV() ? 0.04F : 0.02F);
            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            this.field_70159_w *= 0.800000011920929D;
            this.field_70181_x *= 0.800000011920929D;
            this.field_70179_y *= 0.800000011920929D;
            this.field_70181_x -= 0.02D;
            if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + 0.6000000238418579D - this.field_70163_u + d0, this.field_70179_y)) {
                this.field_70181_x = 0.30000001192092896D;
            }
        } else if (this.func_70058_J() && (!(this instanceof EntityPlayer) || !((EntityPlayer) this).field_71075_bZ.field_75100_b)) {
            d0 = this.field_70163_u;
            this.func_70060_a(f, f1, 0.02F);
            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            this.field_70159_w *= 0.5D;
            this.field_70181_x *= 0.5D;
            this.field_70179_y *= 0.5D;
            this.field_70181_x -= 0.02D;
            if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + 0.6000000238418579D - this.field_70163_u + d0, this.field_70179_y)) {
                this.field_70181_x = 0.30000001192092896D;
            }
        } else {
            float f2 = 0.91F;

            if (this.field_70122_E) {
                f2 = this.field_70170_p.func_147439_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70121_D.field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v)).field_149765_K * 0.91F;
            }

            float f3 = 0.16277136F / (f2 * f2 * f2);
            float f4;

            if (this.field_70122_E) {
                f4 = this.func_70689_ay() * f3;
            } else {
                f4 = this.field_70747_aH;
            }

            this.func_70060_a(f, f1, f4);
            f2 = 0.91F;
            if (this.field_70122_E) {
                f2 = this.field_70170_p.func_147439_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70121_D.field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v)).field_149765_K * 0.91F;
            }

            if (this.func_70617_f_()) {
                float f5 = 0.15F;

                if (this.field_70159_w < (double) (-f5)) {
                    this.field_70159_w = (double) (-f5);
                }

                if (this.field_70159_w > (double) f5) {
                    this.field_70159_w = (double) f5;
                }

                if (this.field_70179_y < (double) (-f5)) {
                    this.field_70179_y = (double) (-f5);
                }

                if (this.field_70179_y > (double) f5) {
                    this.field_70179_y = (double) f5;
                }

                this.field_70143_R = 0.0F;
                if (this.field_70181_x < -0.15D) {
                    this.field_70181_x = -0.15D;
                }

                boolean flag = this.func_70093_af() && this instanceof EntityPlayer;

                if (flag && this.field_70181_x < 0.0D) {
                    this.field_70181_x = 0.0D;
                }
            }

            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            if (this.field_70123_F && this.func_70617_f_()) {
                this.field_70181_x = 0.2D;
            }

            if (this.field_70170_p.field_72995_K && (!this.field_70170_p.func_72899_e((int) this.field_70165_t, 0, (int) this.field_70161_v) || !this.field_70170_p.func_72938_d((int) this.field_70165_t, (int) this.field_70161_v).field_76636_d)) {
                if (this.field_70163_u > 0.0D) {
                    this.field_70181_x = -0.1D;
                } else {
                    this.field_70181_x = 0.0D;
                }
            } else {
                this.field_70181_x -= 0.08D;
            }

            this.field_70181_x *= 0.9800000190734863D;
            this.field_70159_w *= (double) f2;
            this.field_70179_y *= (double) f2;
        }

        this.field_70722_aY = this.field_70721_aZ;
        d0 = this.field_70165_t - this.field_70169_q;
        double d1 = this.field_70161_v - this.field_70166_s;
        float f6 = MathHelper.func_76133_a(d0 * d0 + d1 * d1) * 4.0F;

        if (f6 > 1.0F) {
            f6 = 1.0F;
        }

        this.field_70721_aZ += (f6 - this.field_70721_aZ) * 0.4F;
        this.field_70754_ba += this.field_70721_aZ;
    }

    protected boolean func_70650_aV() {
        return false;
    }

    public float func_70689_ay() {
        return this.func_70650_aV() ? this.field_70746_aG : 0.1F;
    }

    public void func_70659_e(float f) {
        this.field_70746_aG = f;
    }

    public boolean func_70652_k(Entity entity) {
        this.func_130011_c(entity);
        return false;
    }

    public boolean func_70608_bn() {
        return false;
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K) {
            int i = this.func_85035_bI();

            if (i > 0) {
                if (this.field_70720_be <= 0) {
                    this.field_70720_be = 20 * (30 - i);
                }

                --this.field_70720_be;
                if (this.field_70720_be <= 0) {
                    this.func_85034_r(i - 1);
                }
            }

            for (int j = 0; j < 5; ++j) {
                ItemStack itemstack = this.field_82180_bT[j];
                ItemStack itemstack1 = this.func_71124_b(j);

                if (!ItemStack.func_77989_b(itemstack1, itemstack)) {
                    ((WorldServer) this.field_70170_p).func_73039_n().func_151247_a(this, new S04PacketEntityEquipment(this.func_145782_y(), j, itemstack1));
                    if (itemstack != null) {
                        this.field_110155_d.func_111148_a(itemstack.func_111283_C());
                    }

                    if (itemstack1 != null) {
                        this.field_110155_d.func_111147_b(itemstack1.func_111283_C());
                    }

                    this.field_82180_bT[j] = itemstack1 == null ? null : itemstack1.func_77946_l();
                }
            }

            if (this.field_70173_aa % 20 == 0) {
                this.func_110142_aN().func_94549_h();
            }
        }

        this.func_70636_d();
        double d0 = this.field_70165_t - this.field_70169_q;
        double d1 = this.field_70161_v - this.field_70166_s;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.field_70761_aq;
        float f2 = 0.0F;

        this.field_70768_au = this.field_110154_aX;
        float f3 = 0.0F;

        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            f1 = (float) Math.atan2(d1, d0) * 180.0F / 3.1415927F - 90.0F;
        }

        if (this.field_70733_aJ > 0.0F) {
            f1 = this.field_70177_z;
        }

        if (!this.field_70122_E) {
            f3 = 0.0F;
        }

        this.field_110154_aX += (f3 - this.field_110154_aX) * 0.3F;
        this.field_70170_p.field_72984_F.func_76320_a("headTurn");
        f2 = this.func_110146_f(f1, f2);
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("rangeChecks");

        while (this.field_70177_z - this.field_70126_B < -180.0F) {
            this.field_70126_B -= 360.0F;
        }

        while (this.field_70177_z - this.field_70126_B >= 180.0F) {
            this.field_70126_B += 360.0F;
        }

        while (this.field_70761_aq - this.field_70760_ar < -180.0F) {
            this.field_70760_ar -= 360.0F;
        }

        while (this.field_70761_aq - this.field_70760_ar >= 180.0F) {
            this.field_70760_ar += 360.0F;
        }

        while (this.field_70125_A - this.field_70127_C < -180.0F) {
            this.field_70127_C -= 360.0F;
        }

        while (this.field_70125_A - this.field_70127_C >= 180.0F) {
            this.field_70127_C += 360.0F;
        }

        while (this.field_70759_as - this.field_70758_at < -180.0F) {
            this.field_70758_at -= 360.0F;
        }

        while (this.field_70759_as - this.field_70758_at >= 180.0F) {
            this.field_70758_at += 360.0F;
        }

        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70764_aw += f2;
    }

    protected float func_110146_f(float f, float f1) {
        float f2 = MathHelper.func_76142_g(f - this.field_70761_aq);

        this.field_70761_aq += f2 * 0.3F;
        float f3 = MathHelper.func_76142_g(this.field_70177_z - this.field_70761_aq);
        boolean flag = f3 < -90.0F || f3 >= 90.0F;

        if (f3 < -75.0F) {
            f3 = -75.0F;
        }

        if (f3 >= 75.0F) {
            f3 = 75.0F;
        }

        this.field_70761_aq = this.field_70177_z - f3;
        if (f3 * f3 > 2500.0F) {
            this.field_70761_aq += f3 * 0.2F;
        }

        if (flag) {
            f1 *= -1.0F;
        }

        return f1;
    }

    public void func_70636_d() {
        if (this.field_70773_bE > 0) {
            --this.field_70773_bE;
        }

        if (this.field_70716_bi > 0) {
            double d0 = this.field_70165_t + (this.field_70709_bj - this.field_70165_t) / (double) this.field_70716_bi;
            double d1 = this.field_70163_u + (this.field_70710_bk - this.field_70163_u) / (double) this.field_70716_bi;
            double d2 = this.field_70161_v + (this.field_110152_bk - this.field_70161_v) / (double) this.field_70716_bi;
            double d3 = MathHelper.func_76138_g(this.field_70712_bm - (double) this.field_70177_z);

            this.field_70177_z = (float) ((double) this.field_70177_z + d3 / (double) this.field_70716_bi);
            this.field_70125_A = (float) ((double) this.field_70125_A + (this.field_70705_bn - (double) this.field_70125_A) / (double) this.field_70716_bi);
            --this.field_70716_bi;
            this.func_70107_b(d0, d1, d2);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
        } else if (!this.func_70613_aW()) {
            this.field_70159_w *= 0.98D;
            this.field_70181_x *= 0.98D;
            this.field_70179_y *= 0.98D;
        }

        if (Math.abs(this.field_70159_w) < 0.005D) {
            this.field_70159_w = 0.0D;
        }

        if (Math.abs(this.field_70181_x) < 0.005D) {
            this.field_70181_x = 0.0D;
        }

        if (Math.abs(this.field_70179_y) < 0.005D) {
            this.field_70179_y = 0.0D;
        }

        this.field_70170_p.field_72984_F.func_76320_a("ai");
        if (this.func_70610_aX()) {
            this.field_70703_bu = false;
            this.field_70702_br = 0.0F;
            this.field_70701_bs = 0.0F;
            this.field_70704_bt = 0.0F;
        } else if (this.func_70613_aW()) {
            if (this.func_70650_aV()) {
                this.field_70170_p.field_72984_F.func_76320_a("newAi");
                this.func_70619_bc();
                this.field_70170_p.field_72984_F.func_76319_b();
            } else {
                this.field_70170_p.field_72984_F.func_76320_a("oldAi");
                this.func_70626_be();
                this.field_70170_p.field_72984_F.func_76319_b();
                this.field_70759_as = this.field_70177_z;
            }
        }

        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("jump");
        if (this.field_70703_bu) {
            if (!this.func_70090_H() && !this.func_70058_J()) {
                if (this.field_70122_E && this.field_70773_bE == 0) {
                    this.func_70664_aZ();
                    this.field_70773_bE = 10;
                }
            } else {
                this.field_70181_x += 0.03999999910593033D;
            }
        } else {
            this.field_70773_bE = 0;
        }

        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("travel");
        this.field_70702_br *= 0.98F;
        this.field_70701_bs *= 0.98F;
        this.field_70704_bt *= 0.9F;
        this.func_70612_e(this.field_70702_br, this.field_70701_bs);
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("push");
        if (!this.field_70170_p.field_72995_K) {
            this.func_85033_bc();
        }

        this.field_70170_p.field_72984_F.func_76319_b();
    }

    protected void func_70619_bc() {}

    protected void func_85033_bc() {
        List list = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72314_b(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity.func_70104_M()) {
                    this.func_82167_n(entity);
                }
            }
        }

    }

    protected void func_82167_n(Entity entity) {
        entity.func_70108_f(this);
    }

    @Override
    public void func_70098_U() {
        super.func_70098_U();
        this.field_70768_au = this.field_110154_aX;
        this.field_110154_aX = 0.0F;
        this.field_70143_R = 0.0F;
    }

    protected void func_70629_bd() {}

    protected void func_70626_be() {
        ++this.field_70708_bq;
    }

    public void func_70637_d(boolean flag) {
        this.field_70703_bu = flag;
    }

    public void func_71001_a(Entity entity, int i) {
        if (!entity.field_70128_L && !this.field_70170_p.field_72995_K) {
            EntityTracker entitytracker = ((WorldServer) this.field_70170_p).func_73039_n();

            if (entity instanceof EntityItem) {
                entitytracker.func_151247_a(entity, new S0DPacketCollectItem(entity.func_145782_y(), this.func_145782_y()));
            }

            if (entity instanceof EntityArrow) {
                entitytracker.func_151247_a(entity, new S0DPacketCollectItem(entity.func_145782_y(), this.func_145782_y()));
            }

            if (entity instanceof EntityXPOrb) {
                entitytracker.func_151247_a(entity, new S0DPacketCollectItem(entity.func_145782_y(), this.func_145782_y()));
            }
        }

    }

    public boolean func_70685_l(Entity entity) {
        return this.field_70170_p.func_72933_a(Vec3.func_72443_a(this.field_70165_t, this.field_70163_u + (double) this.func_70047_e(), this.field_70161_v), Vec3.func_72443_a(entity.field_70165_t, entity.field_70163_u + (double) entity.func_70047_e(), entity.field_70161_v)) == null;
    }

    @Override
    public Vec3 func_70040_Z() {
        return this.func_70676_i(1.0F);
    }

    public Vec3 func_70676_i(float f) {
        float f1;
        float f2;
        float f3;
        float f4;

        if (f == 1.0F) {
            f1 = MathHelper.func_76134_b(-this.field_70177_z * 0.017453292F - 3.1415927F);
            f2 = MathHelper.func_76126_a(-this.field_70177_z * 0.017453292F - 3.1415927F);
            f3 = -MathHelper.func_76134_b(-this.field_70125_A * 0.017453292F);
            f4 = MathHelper.func_76126_a(-this.field_70125_A * 0.017453292F);
            return Vec3.func_72443_a((double) (f2 * f3), (double) f4, (double) (f1 * f3));
        } else {
            f1 = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * f;
            f2 = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * f;
            f3 = MathHelper.func_76134_b(-f2 * 0.017453292F - 3.1415927F);
            f4 = MathHelper.func_76126_a(-f2 * 0.017453292F - 3.1415927F);
            float f5 = -MathHelper.func_76134_b(-f1 * 0.017453292F);
            float f6 = MathHelper.func_76126_a(-f1 * 0.017453292F);

            return Vec3.func_72443_a((double) (f4 * f5), (double) f6, (double) (f3 * f5));
        }
    }

    public boolean func_70613_aW() {
        return !this.field_70170_p.field_72995_K;
    }

    @Override
    public boolean func_70067_L() {
        return !this.field_70128_L;
    }

    @Override
    public boolean func_70104_M() {
        return !this.field_70128_L;
    }

    @Override
    public float func_70047_e() {
        return this.field_70131_O * 0.85F;
    }

    @Override
    protected void func_70018_K() {
        this.field_70133_I = this.field_70146_Z.nextDouble() >= this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111126_e();
    }

    @Override
    public float func_70079_am() {
        return this.field_70759_as;
    }

    public float func_110139_bj() {
        return this.field_110151_bq;
    }

    public void func_110149_m(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.field_110151_bq = f;
    }

    public Team func_96124_cp() {
        return null;
    }

    public boolean func_142014_c(EntityLivingBase entitylivingbase) {
        return this.func_142012_a(entitylivingbase.func_96124_cp());
    }

    public boolean func_142012_a(Team team) {
        return this.func_96124_cp() != null ? this.func_96124_cp().func_142054_a(team) : false;
    }

    public void func_152111_bt() {}

    public void func_152112_bu() {}
}
