package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class Potion {

    public static final Potion[] field_76425_a = new Potion[32];
    public static final Potion field_76423_b = null;
    public static final Potion field_76424_c = (new Potion(1, false, 8171462)).func_76390_b("potion.moveSpeed").func_76399_b(0, 0).func_111184_a(SharedMonsterAttributes.field_111263_d, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2);
    public static final Potion field_76421_d = (new Potion(2, true, 5926017)).func_76390_b("potion.moveSlowdown").func_76399_b(1, 0).func_111184_a(SharedMonsterAttributes.field_111263_d, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, 2);
    public static final Potion field_76422_e = (new Potion(3, false, 14270531)).func_76390_b("potion.digSpeed").func_76399_b(2, 0).func_76404_a(1.5D);
    public static final Potion field_76419_f = (new Potion(4, true, 4866583)).func_76390_b("potion.digSlowDown").func_76399_b(3, 0);
    public static final Potion field_76420_g = (new PotionAttackDamage(5, false, 9643043)).func_76390_b("potion.damageBoost").func_76399_b(4, 0).func_111184_a(SharedMonsterAttributes.field_111264_e, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 3.0D, 2);
    public static final Potion field_76432_h = (new PotionHealth(6, false, 16262179)).func_76390_b("potion.heal");
    public static final Potion field_76433_i = (new PotionHealth(7, true, 4393481)).func_76390_b("potion.harm");
    public static final Potion field_76430_j = (new Potion(8, false, 7889559)).func_76390_b("potion.jump").func_76399_b(2, 1);
    public static final Potion field_76431_k = (new Potion(9, true, 5578058)).func_76390_b("potion.confusion").func_76399_b(3, 1).func_76404_a(0.25D);
    public static final Potion field_76428_l = (new Potion(10, false, 13458603)).func_76390_b("potion.regeneration").func_76399_b(7, 0).func_76404_a(0.25D);
    public static final Potion field_76429_m = (new Potion(11, false, 10044730)).func_76390_b("potion.resistance").func_76399_b(6, 1);
    public static final Potion field_76426_n = (new Potion(12, false, 14981690)).func_76390_b("potion.fireResistance").func_76399_b(7, 1);
    public static final Potion field_76427_o = (new Potion(13, false, 3035801)).func_76390_b("potion.waterBreathing").func_76399_b(0, 2);
    public static final Potion field_76441_p = (new Potion(14, false, 8356754)).func_76390_b("potion.invisibility").func_76399_b(0, 1);
    public static final Potion field_76440_q = (new Potion(15, true, 2039587)).func_76390_b("potion.blindness").func_76399_b(5, 1).func_76404_a(0.25D);
    public static final Potion field_76439_r = (new Potion(16, false, 2039713)).func_76390_b("potion.nightVision").func_76399_b(4, 1);
    public static final Potion field_76438_s = (new Potion(17, true, 5797459)).func_76390_b("potion.hunger").func_76399_b(1, 1);
    public static final Potion field_76437_t = (new PotionAttackDamage(18, true, 4738376)).func_76390_b("potion.weakness").func_76399_b(5, 0).func_111184_a(SharedMonsterAttributes.field_111264_e, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);
    public static final Potion field_76436_u = (new Potion(19, true, 5149489)).func_76390_b("potion.poison").func_76399_b(6, 0).func_76404_a(0.25D);
    public static final Potion field_82731_v = (new Potion(20, true, 3484199)).func_76390_b("potion.wither").func_76399_b(1, 2).func_76404_a(0.25D);
    public static final Potion field_76434_w = (new PotionHealthBoost(21, false, 16284963)).func_76390_b("potion.healthBoost").func_76399_b(2, 2).func_111184_a(SharedMonsterAttributes.field_111267_a, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0);
    public static final Potion field_76444_x = (new PotionAbsoption(22, false, 2445989)).func_76390_b("potion.absorption").func_76399_b(2, 2);
    public static final Potion field_76443_y = (new PotionHealth(23, false, 16262179)).func_76390_b("potion.saturation");
    public static final Potion field_76442_z = null;
    public static final Potion field_76409_A = null;
    public static final Potion field_76410_B = null;
    public static final Potion field_76411_C = null;
    public static final Potion field_76405_D = null;
    public static final Potion field_76406_E = null;
    public static final Potion field_76407_F = null;
    public static final Potion field_76408_G = null;
    public final int field_76415_H;
    private final Map field_111188_I = Maps.newHashMap();
    private final boolean field_76418_K;
    private final int field_76414_N;
    private String field_76416_I = "";
    private int field_76417_J = -1;
    private double field_76412_L;
    private boolean field_76413_M;

    protected Potion(int i, boolean flag, int j) {
        this.field_76415_H = i;
        Potion.field_76425_a[i] = this;
        this.field_76418_K = flag;
        if (flag) {
            this.field_76412_L = 0.5D;
        } else {
            this.field_76412_L = 1.0D;
        }

        this.field_76414_N = j;
    }

    protected Potion func_76399_b(int i, int j) {
        this.field_76417_J = i + j * 8;
        return this;
    }

    public int func_76396_c() {
        return this.field_76415_H;
    }

    public void func_76394_a(EntityLivingBase entitylivingbase, int i) {
        if (this.field_76415_H == Potion.field_76428_l.field_76415_H) {
            if (entitylivingbase.func_110143_aJ() < entitylivingbase.func_110138_aP()) {
                entitylivingbase.func_70691_i(1.0F);
            }
        } else if (this.field_76415_H == Potion.field_76436_u.field_76415_H) {
            if (entitylivingbase.func_110143_aJ() > 1.0F) {
                entitylivingbase.func_70097_a(DamageSource.field_76376_m, 1.0F);
            }
        } else if (this.field_76415_H == Potion.field_82731_v.field_76415_H) {
            entitylivingbase.func_70097_a(DamageSource.field_82727_n, 1.0F);
        } else if (this.field_76415_H == Potion.field_76438_s.field_76415_H && entitylivingbase instanceof EntityPlayer) {
            ((EntityPlayer) entitylivingbase).func_71020_j(0.025F * (float) (i + 1));
        } else if (this.field_76415_H == Potion.field_76443_y.field_76415_H && entitylivingbase instanceof EntityPlayer) {
            if (!entitylivingbase.field_70170_p.field_72995_K) {
                ((EntityPlayer) entitylivingbase).func_71024_bL().func_75122_a(i + 1, 1.0F);
            }
        } else if ((this.field_76415_H != Potion.field_76432_h.field_76415_H || entitylivingbase.func_70662_br()) && (this.field_76415_H != Potion.field_76433_i.field_76415_H || !entitylivingbase.func_70662_br())) {
            if (this.field_76415_H == Potion.field_76433_i.field_76415_H && !entitylivingbase.func_70662_br() || this.field_76415_H == Potion.field_76432_h.field_76415_H && entitylivingbase.func_70662_br()) {
                entitylivingbase.func_70097_a(DamageSource.field_76376_m, (float) (6 << i));
            }
        } else {
            entitylivingbase.func_70691_i((float) Math.max(4 << i, 0));
        }

    }

    public void func_76402_a(EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1, int i, double d0) {
        int j;

        if ((this.field_76415_H != Potion.field_76432_h.field_76415_H || entitylivingbase1.func_70662_br()) && (this.field_76415_H != Potion.field_76433_i.field_76415_H || !entitylivingbase1.func_70662_br())) {
            if (this.field_76415_H == Potion.field_76433_i.field_76415_H && !entitylivingbase1.func_70662_br() || this.field_76415_H == Potion.field_76432_h.field_76415_H && entitylivingbase1.func_70662_br()) {
                j = (int) (d0 * (double) (6 << i) + 0.5D);
                if (entitylivingbase == null) {
                    entitylivingbase1.func_70097_a(DamageSource.field_76376_m, (float) j);
                } else {
                    entitylivingbase1.func_70097_a(DamageSource.func_76354_b(entitylivingbase1, entitylivingbase), (float) j);
                }
            }
        } else {
            j = (int) (d0 * (double) (4 << i) + 0.5D);
            entitylivingbase1.func_70691_i((float) j);
        }

    }

    public boolean func_76403_b() {
        return false;
    }

    public boolean func_76397_a(int i, int j) {
        int k;

        if (this.field_76415_H == Potion.field_76428_l.field_76415_H) {
            k = 50 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this.field_76415_H == Potion.field_76436_u.field_76415_H) {
            k = 25 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this.field_76415_H == Potion.field_82731_v.field_76415_H) {
            k = 40 >> j;
            return k > 0 ? i % k == 0 : true;
        } else {
            return this.field_76415_H == Potion.field_76438_s.field_76415_H;
        }
    }

    public Potion func_76390_b(String s) {
        this.field_76416_I = s;
        return this;
    }

    public String func_76393_a() {
        return this.field_76416_I;
    }

    protected Potion func_76404_a(double d0) {
        this.field_76412_L = d0;
        return this;
    }

    public double func_76388_g() {
        return this.field_76412_L;
    }

    public boolean func_76395_i() {
        return this.field_76413_M;
    }

    public int func_76401_j() {
        return this.field_76414_N;
    }

    public Potion func_111184_a(IAttribute iattribute, String s, double d0, int i) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(s), this.func_76393_a(), d0, i);

        this.field_111188_I.put(iattribute, attributemodifier);
        return this;
    }

    public void func_111187_a(EntityLivingBase entitylivingbase, BaseAttributeMap baseattributemap, int i) {
        Iterator iterator = this.field_111188_I.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            IAttributeInstance iattributeinstance = baseattributemap.func_111151_a((IAttribute) entry.getKey());

            if (iattributeinstance != null) {
                iattributeinstance.func_111124_b((AttributeModifier) entry.getValue());
            }
        }

    }

    public void func_111185_a(EntityLivingBase entitylivingbase, BaseAttributeMap baseattributemap, int i) {
        Iterator iterator = this.field_111188_I.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            IAttributeInstance iattributeinstance = baseattributemap.func_111151_a((IAttribute) entry.getKey());

            if (iattributeinstance != null) {
                AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();

                iattributeinstance.func_111124_b(attributemodifier);
                iattributeinstance.func_111121_a(new AttributeModifier(attributemodifier.func_111167_a(), this.func_76393_a() + " " + i, this.func_111183_a(i, attributemodifier), attributemodifier.func_111169_c()));
            }
        }

    }

    public double func_111183_a(int i, AttributeModifier attributemodifier) {
        return attributemodifier.func_111164_d() * (double) (i + 1);
    }
}
