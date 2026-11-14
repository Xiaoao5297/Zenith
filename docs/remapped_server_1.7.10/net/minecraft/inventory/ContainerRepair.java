package net.minecraft.inventory;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {

    private static final Logger field_148326_f = LogManager.getLogger();
    private IInventory field_82852_f = new InventoryCraftResult();
    private IInventory field_82853_g = new ContainerRepair$1(this, "Repair", true, 2);
    private World field_82860_h;
    private int field_82861_i;
    private int field_82858_j;
    private int field_82859_k;
    public int field_82854_e;
    private int field_82856_l;
    private String field_82857_m;
    private final EntityPlayer field_82855_n;

    public ContainerRepair(InventoryPlayer inventoryplayer, World world, int i, int j, int k, EntityPlayer entityplayer) {
        this.field_82860_h = world;
        this.field_82861_i = i;
        this.field_82858_j = j;
        this.field_82859_k = k;
        this.field_82855_n = entityplayer;
        this.func_75146_a(new Slot(this.field_82853_g, 0, 27, 47));
        this.func_75146_a(new Slot(this.field_82853_g, 1, 76, 47));
        this.func_75146_a(new ContainerRepair$2(this, this.field_82852_f, 2, 134, 47, world, i, j, k));

        int l;

        for (l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.func_75146_a(new Slot(inventoryplayer, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l) {
            this.func_75146_a(new Slot(inventoryplayer, l, 8 + l * 18, 142));
        }

    }

    @Override
    public void func_75130_a(IInventory iinventory) {
        super.func_75130_a(iinventory);
        if (iinventory == this.field_82853_g) {
            this.func_82848_d();
        }

    }

    public void func_82848_d() {
        ItemStack itemstack = this.field_82853_g.func_70301_a(0);

        this.field_82854_e = 0;
        int i = 0;
        int j = 0;
        int k = 0;

        if (itemstack == null) {
            this.field_82852_f.func_70299_a(0, (ItemStack) null);
            this.field_82854_e = 0;
        } else {
            ItemStack itemstack1 = itemstack.func_77946_l();
            ItemStack itemstack2 = this.field_82853_g.func_70301_a(1);
            Map map = EnchantmentHelper.func_82781_a(itemstack1);
            boolean flag = false;

            j += itemstack.func_82838_A() + (itemstack2 == null ? 0 : itemstack2.func_82838_A());
            this.field_82856_l = 0;
            int l;
            int i1;
            int j1;
            int k1;
            Iterator iterator;
            Enchantment enchantment;
            int l1;

            if (itemstack2 != null) {
                flag = itemstack2.func_77973_b() == Items.field_151134_bR && Items.field_151134_bR.func_92110_g(itemstack2).func_74745_c() > 0;
                if (itemstack1.func_77984_f() && itemstack1.func_77973_b().func_82789_a(itemstack, itemstack2)) {
                    l = Math.min(itemstack1.func_77952_i(), itemstack1.func_77958_k() / 4);
                    if (l <= 0) {
                        this.field_82852_f.func_70299_a(0, (ItemStack) null);
                        this.field_82854_e = 0;
                        return;
                    }

                    for (j1 = 0; l > 0 && j1 < itemstack2.field_77994_a; ++j1) {
                        i1 = itemstack1.func_77952_i() - l;
                        itemstack1.func_77964_b(i1);
                        i += Math.max(1, l / 100) + map.size();
                        l = Math.min(itemstack1.func_77952_i(), itemstack1.func_77958_k() / 4);
                    }

                    this.field_82856_l = j1;
                } else {
                    if (!flag && (itemstack1.func_77973_b() != itemstack2.func_77973_b() || !itemstack1.func_77984_f())) {
                        this.field_82852_f.func_70299_a(0, (ItemStack) null);
                        this.field_82854_e = 0;
                        return;
                    }

                    if (itemstack1.func_77984_f() && !flag) {
                        l = itemstack.func_77958_k() - itemstack.func_77952_i();
                        j1 = itemstack2.func_77958_k() - itemstack2.func_77952_i();
                        i1 = j1 + itemstack1.func_77958_k() * 12 / 100;
                        int i2 = l + i1;

                        k1 = itemstack1.func_77958_k() - i2;
                        if (k1 < 0) {
                            k1 = 0;
                        }

                        if (k1 < itemstack1.func_77960_j()) {
                            itemstack1.func_77964_b(k1);
                            i += Math.max(1, i1 / 100);
                        }
                    }

                    Map map1 = EnchantmentHelper.func_82781_a(itemstack2);

                    iterator = map1.keySet().iterator();

                    while (iterator.hasNext()) {
                        i1 = (Integer) iterator.next();
                        enchantment = Enchantment.field_77331_b[i1];
                        k1 = map.containsKey(i1) ? (Integer) map.get(i1) : 0;
                        l1 = (Integer) map1.get(i1);
                        int j2;

                        if (k1 == l1) {
                            ++l1;
                            j2 = l1;
                        } else {
                            j2 = Math.max(l1, k1);
                        }

                        l1 = j2;
                        int k2 = l1 - k1;
                        boolean flag1 = enchantment.func_92089_a(itemstack);

                        if (this.field_82855_n.field_71075_bZ.field_75098_d || itemstack.func_77973_b() == Items.field_151134_bR) {
                            flag1 = true;
                        }

                        Iterator iterator1 = map.keySet().iterator();

                        while (iterator1.hasNext()) {
                            int l2 = (Integer) iterator1.next();

                            if (l2 != i1 && !enchantment.func_77326_a(Enchantment.field_77331_b[l2])) {
                                flag1 = false;
                                i += k2;
                            }
                        }

                        if (flag1) {
                            if (l1 > enchantment.func_77325_b()) {
                                l1 = enchantment.func_77325_b();
                            }

                            map.put(i1, l1);
                            int i3 = 0;

                            switch (enchantment.func_77324_c()) {
                                case 1:
                                    i3 = 8;
                                    break;
                                case 2:
                                    i3 = 4;
                                case 3:
                                case 4:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                default:
                                    break;
                                case 5:
                                    i3 = 2;
                                    break;
                                case 10:
                                    i3 = 1;
                            }

                            if (flag) {
                                i3 = Math.max(1, i3 / 2);
                            }

                            i += i3 * k2;
                        }
                    }
                }
            }

            if (StringUtils.isBlank(this.field_82857_m)) {
                if (itemstack.func_82837_s()) {
                    k = itemstack.func_77984_f() ? 7 : itemstack.field_77994_a * 5;
                    i += k;
                    itemstack1.func_135074_t();
                }
            } else if (!this.field_82857_m.equals(itemstack.func_82833_r())) {
                k = itemstack.func_77984_f() ? 7 : itemstack.field_77994_a * 5;
                i += k;
                if (itemstack.func_82837_s()) {
                    j += k / 2;
                }

                itemstack1.func_151001_c(this.field_82857_m);
            }

            l = 0;

            for (iterator = map.keySet().iterator(); iterator.hasNext(); j += l + k1 * l1) {
                i1 = (Integer) iterator.next();
                enchantment = Enchantment.field_77331_b[i1];
                k1 = (Integer) map.get(i1);
                l1 = 0;
                ++l;
                switch (enchantment.func_77324_c()) {
                    case 1:
                        l1 = 8;
                        break;
                    case 2:
                        l1 = 4;
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    default:
                        break;
                    case 5:
                        l1 = 2;
                        break;
                    case 10:
                        l1 = 1;
                }

                if (flag) {
                    l1 = Math.max(1, l1 / 2);
                }
            }

            if (flag) {
                j = Math.max(1, j / 2);
            }

            this.field_82854_e = j + i;
            if (i <= 0) {
                itemstack1 = null;
            }

            if (k == i && k > 0 && this.field_82854_e >= 40) {
                this.field_82854_e = 39;
            }

            if (this.field_82854_e >= 40 && !this.field_82855_n.field_71075_bZ.field_75098_d) {
                itemstack1 = null;
            }

            if (itemstack1 != null) {
                j1 = itemstack1.func_82838_A();
                if (itemstack2 != null && j1 < itemstack2.func_82838_A()) {
                    j1 = itemstack2.func_82838_A();
                }

                if (itemstack1.func_82837_s()) {
                    j1 -= 9;
                }

                if (j1 < 0) {
                    j1 = 0;
                }

                j1 += 2;
                itemstack1.func_82841_c(j1);
                EnchantmentHelper.func_82782_a(map, itemstack1);
            }

            this.field_82852_f.func_70299_a(0, itemstack1);
            this.func_75142_b();
        }
    }

    @Override
    public void func_75132_a(ICrafting icrafting) {
        super.func_75132_a(icrafting);
        icrafting.func_71112_a(this, 0, this.field_82854_e);
    }

    @Override
    public void func_75134_a(EntityPlayer entityplayer) {
        super.func_75134_a(entityplayer);
        if (!this.field_82860_h.field_72995_K) {
            for (int i = 0; i < this.field_82853_g.func_70302_i_(); ++i) {
                ItemStack itemstack = this.field_82853_g.func_70304_b(i);

                if (itemstack != null) {
                    entityplayer.func_71019_a(itemstack, false);
                }
            }

        }
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_82860_h.func_147439_a(this.field_82861_i, this.field_82858_j, this.field_82859_k) != Blocks.field_150467_bQ ? false : entityplayer.func_70092_e((double) this.field_82861_i + 0.5D, (double) this.field_82858_j + 0.5D, (double) this.field_82859_k + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i == 2) {
                if (!this.func_75135_a(itemstack1, 3, 39, true)) {
                    return null;
                }

                slot.func_75220_a(itemstack1, itemstack);
            } else if (i != 0 && i != 1) {
                if (i >= 3 && i < 39 && !this.func_75135_a(itemstack1, 0, 2, false)) {
                    return null;
                }
            } else if (!this.func_75135_a(itemstack1, 3, 39, false)) {
                return null;
            }

            if (itemstack1.field_77994_a == 0) {
                slot.func_75215_d((ItemStack) null);
            } else {
                slot.func_75218_e();
            }

            if (itemstack1.field_77994_a == itemstack.field_77994_a) {
                return null;
            }

            slot.func_82870_a(entityplayer, itemstack1);
        }

        return itemstack;
    }

    public void func_82850_a(String s) {
        this.field_82857_m = s;
        if (this.func_75139_a(2).func_75216_d()) {
            ItemStack itemstack = this.func_75139_a(2).func_75211_c();

            if (StringUtils.isBlank(s)) {
                itemstack.func_135074_t();
            } else {
                itemstack.func_151001_c(this.field_82857_m);
            }
        }

        this.func_82848_d();
    }
}
