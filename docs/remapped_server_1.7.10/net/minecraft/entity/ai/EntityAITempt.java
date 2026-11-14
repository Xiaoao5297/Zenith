package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EntityAITempt extends EntityAIBase {

    private EntityCreature field_75284_a;
    private double field_75282_b;
    private double field_75283_c;
    private double field_75280_d;
    private double field_75281_e;
    private double field_75278_f;
    private double field_75279_g;
    private EntityPlayer field_75289_h;
    private int field_75290_i;
    private boolean field_75287_j;
    private Item field_151484_k;
    private boolean field_75285_l;
    private boolean field_75286_m;

    public EntityAITempt(EntityCreature entitycreature, double d0, Item item, boolean flag) {
        this.field_75284_a = entitycreature;
        this.field_75282_b = d0;
        this.field_151484_k = item;
        this.field_75285_l = flag;
        this.func_75248_a(3);
    }

    @Override
    public boolean func_75250_a() {
        if (this.field_75290_i > 0) {
            --this.field_75290_i;
            return false;
        } else {
            this.field_75289_h = this.field_75284_a.field_70170_p.func_72890_a(this.field_75284_a, 10.0D);
            if (this.field_75289_h == null) {
                return false;
            } else {
                ItemStack itemstack = this.field_75289_h.func_71045_bC();

                return itemstack == null ? false : itemstack.func_77973_b() == this.field_151484_k;
            }
        }
    }

    @Override
    public boolean func_75253_b() {
        if (this.field_75285_l) {
            if (this.field_75284_a.func_70068_e(this.field_75289_h) < 36.0D) {
                if (this.field_75289_h.func_70092_e(this.field_75283_c, this.field_75280_d, this.field_75281_e) > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs((double) this.field_75289_h.field_70125_A - this.field_75278_f) > 5.0D || Math.abs((double) this.field_75289_h.field_70177_z - this.field_75279_g) > 5.0D) {
                    return false;
                }
            } else {
                this.field_75283_c = this.field_75289_h.field_70165_t;
                this.field_75280_d = this.field_75289_h.field_70163_u;
                this.field_75281_e = this.field_75289_h.field_70161_v;
            }

            this.field_75278_f = (double) this.field_75289_h.field_70125_A;
            this.field_75279_g = (double) this.field_75289_h.field_70177_z;
        }

        return this.func_75250_a();
    }

    @Override
    public void func_75249_e() {
        this.field_75283_c = this.field_75289_h.field_70165_t;
        this.field_75280_d = this.field_75289_h.field_70163_u;
        this.field_75281_e = this.field_75289_h.field_70161_v;
        this.field_75287_j = true;
        this.field_75286_m = this.field_75284_a.func_70661_as().func_75486_a();
        this.field_75284_a.func_70661_as().func_75491_a(false);
    }

    @Override
    public void func_75251_c() {
        this.field_75289_h = null;
        this.field_75284_a.func_70661_as().func_75499_g();
        this.field_75290_i = 100;
        this.field_75287_j = false;
        this.field_75284_a.func_70661_as().func_75491_a(this.field_75286_m);
    }

    @Override
    public void func_75246_d() {
        this.field_75284_a.func_70671_ap().func_75651_a(this.field_75289_h, 30.0F, (float) this.field_75284_a.func_70646_bf());
        if (this.field_75284_a.func_70068_e(this.field_75289_h) < 6.25D) {
            this.field_75284_a.func_70661_as().func_75499_g();
        } else {
            this.field_75284_a.func_70661_as().func_75497_a(this.field_75289_h, this.field_75282_b);
        }

    }

    public boolean func_75277_f() {
        return this.field_75287_j;
    }
}
