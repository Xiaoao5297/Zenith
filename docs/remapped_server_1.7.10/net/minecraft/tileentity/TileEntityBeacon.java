package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityBeacon extends TileEntity implements IInventory {

    public static final Potion[][] field_146009_a = new Potion[][]{{Potion.field_76424_c, Potion.field_76422_e}, {Potion.field_76429_m, Potion.field_76430_j}, {Potion.field_76420_g}, {Potion.field_76428_l}};
    private boolean field_146015_k;
    private int field_146012_l = -1;
    private int field_146013_m;
    private int field_146010_n;
    private ItemStack field_146011_o;
    private String field_146008_p;

    public TileEntityBeacon() {}

    @Override
    public void func_145845_h() {
        if (this.field_145850_b.func_82737_E() % 80L == 0L) {
            this.func_146003_y();
            this.func_146000_x();
        }

    }

    private void func_146000_x() {
        if (this.field_146015_k && this.field_146012_l > 0 && !this.field_145850_b.field_72995_K && this.field_146013_m > 0) {
            double d0 = (double) (this.field_146012_l * 10 + 10);
            byte b0 = 0;

            if (this.field_146012_l >= 4 && this.field_146013_m == this.field_146010_n) {
                b0 = 1;
            }

            AxisAlignedBB axisalignedbb = AxisAlignedBB.func_72330_a((double) this.field_145851_c, (double) this.field_145848_d, (double) this.field_145849_e, (double) (this.field_145851_c + 1), (double) (this.field_145848_d + 1), (double) (this.field_145849_e + 1)).func_72314_b(d0, d0, d0);

            axisalignedbb.field_72337_e = (double) this.field_145850_b.func_72800_K();
            List list = this.field_145850_b.func_72872_a(EntityPlayer.class, axisalignedbb);
            Iterator iterator = list.iterator();

            EntityPlayer entityplayer;

            while (iterator.hasNext()) {
                entityplayer = (EntityPlayer) iterator.next();
                entityplayer.func_70690_d(new PotionEffect(this.field_146013_m, 180, b0, true));
            }

            if (this.field_146012_l >= 4 && this.field_146013_m != this.field_146010_n && this.field_146010_n > 0) {
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityplayer = (EntityPlayer) iterator.next();
                    entityplayer.func_70690_d(new PotionEffect(this.field_146010_n, 180, 0, true));
                }
            }
        }

    }

    private void func_146003_y() {
        int i = this.field_146012_l;

        if (!this.field_145850_b.func_72937_j(this.field_145851_c, this.field_145848_d + 1, this.field_145849_e)) {
            this.field_146015_k = false;
            this.field_146012_l = 0;
        } else {
            this.field_146015_k = true;
            this.field_146012_l = 0;

            for (int j = 1; j <= 4; this.field_146012_l = j++) {
                int k = this.field_145848_d - j;

                if (k < 0) {
                    break;
                }

                boolean flag = true;

                for (int l = this.field_145851_c - j; l <= this.field_145851_c + j && flag; ++l) {
                    for (int i1 = this.field_145849_e - j; i1 <= this.field_145849_e + j; ++i1) {
                        Block block = this.field_145850_b.func_147439_a(l, k, i1);

                        if (block != Blocks.field_150475_bE && block != Blocks.field_150340_R && block != Blocks.field_150484_ah && block != Blocks.field_150339_S) {
                            flag = false;
                            break;
                        }
                    }
                }

                if (!flag) {
                    break;
                }
            }

            if (this.field_146012_l == 0) {
                this.field_146015_k = false;
            }
        }

        if (!this.field_145850_b.field_72995_K && this.field_146012_l == 4 && i < this.field_146012_l) {
            Iterator iterator = this.field_145850_b.func_72872_a(EntityPlayer.class, AxisAlignedBB.func_72330_a((double) this.field_145851_c, (double) this.field_145848_d, (double) this.field_145849_e, (double) this.field_145851_c, (double) (this.field_145848_d - 4), (double) this.field_145849_e).func_72314_b(10.0D, 5.0D, 10.0D)).iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.func_71029_a(AchievementList.field_150965_K);
            }
        }

    }

    public int func_146007_j() {
        return this.field_146013_m;
    }

    public int func_146006_k() {
        return this.field_146010_n;
    }

    public int func_145998_l() {
        return this.field_146012_l;
    }

    public void func_146001_d(int i) {
        this.field_146013_m = 0;

        for (int j = 0; j < this.field_146012_l && j < 3; ++j) {
            Potion[] apotion = TileEntityBeacon.field_146009_a[j];
            int k = apotion.length;

            for (int l = 0; l < k; ++l) {
                Potion potion = apotion[l];

                if (potion.field_76415_H == i) {
                    this.field_146013_m = i;
                    return;
                }
            }
        }

    }

    public void func_146004_e(int i) {
        this.field_146010_n = 0;
        if (this.field_146012_l >= 4) {
            for (int j = 0; j < 4; ++j) {
                Potion[] apotion = TileEntityBeacon.field_146009_a[j];
                int k = apotion.length;

                for (int l = 0; l < k; ++l) {
                    Potion potion = apotion[l];

                    if (potion.field_76415_H == i) {
                        this.field_146010_n = i;
                        return;
                    }
                }
            }
        }

    }

    @Override
    public Packet func_145844_m() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.func_145841_b(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 3, nbttagcompound);
    }

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        super.func_145839_a(nbttagcompound);
        this.field_146013_m = nbttagcompound.func_74762_e("Primary");
        this.field_146010_n = nbttagcompound.func_74762_e("Secondary");
        this.field_146012_l = nbttagcompound.func_74762_e("Levels");
    }

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        nbttagcompound.func_74768_a("Primary", this.field_146013_m);
        nbttagcompound.func_74768_a("Secondary", this.field_146010_n);
        nbttagcompound.func_74768_a("Levels", this.field_146012_l);
    }

    @Override
    public int func_70302_i_() {
        return 1;
    }

    @Override
    public ItemStack func_70301_a(int i) {
        return i == 0 ? this.field_146011_o : null;
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        if (i == 0 && this.field_146011_o != null) {
            if (j >= this.field_146011_o.field_77994_a) {
                ItemStack itemstack = this.field_146011_o;

                this.field_146011_o = null;
                return itemstack;
            } else {
                this.field_146011_o.field_77994_a -= j;
                return new ItemStack(this.field_146011_o.func_77973_b(), j, this.field_146011_o.func_77960_j());
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack func_70304_b(int i) {
        if (i == 0 && this.field_146011_o != null) {
            ItemStack itemstack = this.field_146011_o;

            this.field_146011_o = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        if (i == 0) {
            this.field_146011_o = itemstack;
        }

    }

    @Override
    public String func_145825_b() {
        return this.func_145818_k_() ? this.field_146008_p : "container.beacon";
    }

    @Override
    public boolean func_145818_k_() {
        return this.field_146008_p != null && this.field_146008_p.length() > 0;
    }

    public void func_145999_a(String s) {
        this.field_146008_p = s;
    }

    @Override
    public int func_70297_j_() {
        return 1;
    }

    @Override
    public boolean func_70300_a(EntityPlayer entityplayer) {
        return this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e) != this ? false : entityplayer.func_70092_e((double) this.field_145851_c + 0.5D, (double) this.field_145848_d + 0.5D, (double) this.field_145849_e + 0.5D) <= 64.0D;
    }

    @Override
    public void func_70295_k_() {}

    @Override
    public void func_70305_f() {}

    @Override
    public boolean func_94041_b(int i, ItemStack itemstack) {
        return itemstack.func_77973_b() == Items.field_151166_bC || itemstack.func_77973_b() == Items.field_151045_i || itemstack.func_77973_b() == Items.field_151043_k || itemstack.func_77973_b() == Items.field_151042_j;
    }
}
