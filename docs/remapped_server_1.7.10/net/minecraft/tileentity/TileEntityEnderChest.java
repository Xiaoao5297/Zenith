package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

public class TileEntityEnderChest extends TileEntity {

    public float field_145972_a;
    public float field_145975_i;
    public int field_145973_j;
    private int field_145974_k;

    public TileEntityEnderChest() {}

    @Override
    public void func_145845_h() {
        super.func_145845_h();
        if (++this.field_145974_k % 20 * 4 == 0) {
            this.field_145850_b.func_147452_c(this.field_145851_c, this.field_145848_d, this.field_145849_e, Blocks.field_150477_bB, 1, this.field_145973_j);
        }

        this.field_145975_i = this.field_145972_a;
        float f = 0.1F;
        double d0;

        if (this.field_145973_j > 0 && this.field_145972_a == 0.0F) {
            double d1 = (double) this.field_145851_c + 0.5D;

            d0 = (double) this.field_145849_e + 0.5D;
            this.field_145850_b.func_72908_a(d1, (double) this.field_145848_d + 0.5D, d0, "random.chestopen", 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
        }

        if (this.field_145973_j == 0 && this.field_145972_a > 0.0F || this.field_145973_j > 0 && this.field_145972_a < 1.0F) {
            float f1 = this.field_145972_a;

            if (this.field_145973_j > 0) {
                this.field_145972_a += f;
            } else {
                this.field_145972_a -= f;
            }

            if (this.field_145972_a > 1.0F) {
                this.field_145972_a = 1.0F;
            }

            float f2 = 0.5F;

            if (this.field_145972_a < f2 && f1 >= f2) {
                d0 = (double) this.field_145851_c + 0.5D;
                double d2 = (double) this.field_145849_e + 0.5D;

                this.field_145850_b.func_72908_a(d0, (double) this.field_145848_d + 0.5D, d2, "random.chestclosed", 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
            }

            if (this.field_145972_a < 0.0F) {
                this.field_145972_a = 0.0F;
            }
        }

    }

    @Override
    public boolean func_145842_c(int i, int j) {
        if (i == 1) {
            this.field_145973_j = j;
            return true;
        } else {
            return super.func_145842_c(i, j);
        }
    }

    @Override
    public void func_145843_s() {
        this.func_145836_u();
        super.func_145843_s();
    }

    public void func_145969_a() {
        ++this.field_145973_j;
        this.field_145850_b.func_147452_c(this.field_145851_c, this.field_145848_d, this.field_145849_e, Blocks.field_150477_bB, 1, this.field_145973_j);
    }

    public void func_145970_b() {
        --this.field_145973_j;
        this.field_145850_b.func_147452_c(this.field_145851_c, this.field_145848_d, this.field_145849_e, Blocks.field_150477_bB, 1, this.field_145973_j);
    }

    public boolean func_145971_a(EntityPlayer entityplayer) {
        return this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e) != this ? false : entityplayer.func_70092_e((double) this.field_145851_c + 0.5D, (double) this.field_145848_d + 0.5D, (double) this.field_145849_e + 0.5D) <= 64.0D;
    }
}
