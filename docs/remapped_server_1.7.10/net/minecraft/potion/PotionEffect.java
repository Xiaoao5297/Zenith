package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class PotionEffect {

    private int field_76462_a;
    private int field_76460_b;
    private int field_76461_c;
    private boolean field_82723_d;
    private boolean field_82724_e;

    public PotionEffect(int i, int j) {
        this(i, j, 0);
    }

    public PotionEffect(int i, int j, int k) {
        this(i, j, k, false);
    }

    public PotionEffect(int i, int j, int k, boolean flag) {
        this.field_76462_a = i;
        this.field_76460_b = j;
        this.field_76461_c = k;
        this.field_82724_e = flag;
    }

    public PotionEffect(PotionEffect potioneffect) {
        this.field_76462_a = potioneffect.field_76462_a;
        this.field_76460_b = potioneffect.field_76460_b;
        this.field_76461_c = potioneffect.field_76461_c;
    }

    public void func_76452_a(PotionEffect potioneffect) {
        if (this.field_76462_a != potioneffect.field_76462_a) {
            System.err.println("This method should only be called for matching effects!");
        }

        if (potioneffect.field_76461_c > this.field_76461_c) {
            this.field_76461_c = potioneffect.field_76461_c;
            this.field_76460_b = potioneffect.field_76460_b;
        } else if (potioneffect.field_76461_c == this.field_76461_c && this.field_76460_b < potioneffect.field_76460_b) {
            this.field_76460_b = potioneffect.field_76460_b;
        } else if (!potioneffect.field_82724_e && this.field_82724_e) {
            this.field_82724_e = potioneffect.field_82724_e;
        }

    }

    public int func_76456_a() {
        return this.field_76462_a;
    }

    public int func_76459_b() {
        return this.field_76460_b;
    }

    public int func_76458_c() {
        return this.field_76461_c;
    }

    public void func_82721_a(boolean flag) {
        this.field_82723_d = flag;
    }

    public boolean func_82720_e() {
        return this.field_82724_e;
    }

    public boolean func_76455_a(EntityLivingBase entitylivingbase) {
        if (this.field_76460_b > 0) {
            if (Potion.field_76425_a[this.field_76462_a].func_76397_a(this.field_76460_b, this.field_76461_c)) {
                this.func_76457_b(entitylivingbase);
            }

            this.func_76454_e();
        }

        return this.field_76460_b > 0;
    }

    private int func_76454_e() {
        return --this.field_76460_b;
    }

    public void func_76457_b(EntityLivingBase entitylivingbase) {
        if (this.field_76460_b > 0) {
            Potion.field_76425_a[this.field_76462_a].func_76394_a(entitylivingbase, this.field_76461_c);
        }

    }

    public String func_76453_d() {
        return Potion.field_76425_a[this.field_76462_a].func_76393_a();
    }

    public int hashCode() {
        return this.field_76462_a;
    }

    public String toString() {
        String s = "";

        if (this.func_76458_c() > 0) {
            s = this.func_76453_d() + " x " + (this.func_76458_c() + 1) + ", Duration: " + this.func_76459_b();
        } else {
            s = this.func_76453_d() + ", Duration: " + this.func_76459_b();
        }

        if (this.field_82723_d) {
            s = s + ", Splash: true";
        }

        return Potion.field_76425_a[this.field_76462_a].func_76395_i() ? "(" + s + ")" : s;
    }

    public boolean equals(Object object) {
        if (!(object instanceof PotionEffect)) {
            return false;
        } else {
            PotionEffect potioneffect = (PotionEffect) object;

            return this.field_76462_a == potioneffect.field_76462_a && this.field_76461_c == potioneffect.field_76461_c && this.field_76460_b == potioneffect.field_76460_b && this.field_82723_d == potioneffect.field_82723_d && this.field_82724_e == potioneffect.field_82724_e;
        }
    }

    public NBTTagCompound func_82719_a(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74774_a("Id", (byte) this.func_76456_a());
        nbttagcompound.func_74774_a("Amplifier", (byte) this.func_76458_c());
        nbttagcompound.func_74768_a("Duration", this.func_76459_b());
        nbttagcompound.func_74757_a("Ambient", this.func_82720_e());
        return nbttagcompound;
    }

    public static PotionEffect func_82722_b(NBTTagCompound nbttagcompound) {
        byte b0 = nbttagcompound.func_74771_c("Id");

        if (b0 >= 0 && b0 < Potion.field_76425_a.length && Potion.field_76425_a[b0] != null) {
            byte b1 = nbttagcompound.func_74771_c("Amplifier");
            int i = nbttagcompound.func_74762_e("Duration");
            boolean flag = nbttagcompound.func_74767_n("Ambient");

            return new PotionEffect(b0, i, b1, flag);
        } else {
            return null;
        }
    }
}
