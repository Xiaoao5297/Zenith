package net.minecraft.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public abstract class Container {

    public List field_75153_a = new ArrayList();
    public List field_75151_b = new ArrayList();
    public int field_75152_c;
    private int field_94535_f = -1;
    private int field_94536_g;
    private final Set field_94537_h = new HashSet();
    protected List field_75149_d = new ArrayList();
    private Set field_75148_f = new HashSet();

    public Container() {}

    protected Slot func_75146_a(Slot slot) {
        slot.field_75222_d = this.field_75151_b.size();
        this.field_75151_b.add(slot);
        this.field_75153_a.add((Object) null);
        return slot;
    }

    public void func_75132_a(ICrafting icrafting) {
        if (this.field_75149_d.contains(icrafting)) {
            throw new IllegalArgumentException("Listener already listening");
        } else {
            this.field_75149_d.add(icrafting);
            icrafting.func_71110_a(this, this.func_75138_a());
            this.func_75142_b();
        }
    }

    public List func_75138_a() {
        ArrayList arraylist = new ArrayList();

        for (int i = 0; i < this.field_75151_b.size(); ++i) {
            arraylist.add(((Slot) this.field_75151_b.get(i)).func_75211_c());
        }

        return arraylist;
    }

    public void func_75142_b() {
        for (int i = 0; i < this.field_75151_b.size(); ++i) {
            ItemStack itemstack = ((Slot) this.field_75151_b.get(i)).func_75211_c();
            ItemStack itemstack1 = (ItemStack) this.field_75153_a.get(i);

            if (!ItemStack.func_77989_b(itemstack1, itemstack)) {
                itemstack1 = itemstack == null ? null : itemstack.func_77946_l();
                this.field_75153_a.set(i, itemstack1);

                for (int j = 0; j < this.field_75149_d.size(); ++j) {
                    ((ICrafting) this.field_75149_d.get(j)).func_71111_a(this, i, itemstack1);
                }
            }
        }

    }

    public boolean func_75140_a(EntityPlayer entityplayer, int i) {
        return false;
    }

    public Slot func_75147_a(IInventory iinventory, int i) {
        for (int j = 0; j < this.field_75151_b.size(); ++j) {
            Slot slot = (Slot) this.field_75151_b.get(j);

            if (slot.func_75217_a(iinventory, i)) {
                return slot;
            }
        }

        return null;
    }

    public Slot func_75139_a(int i) {
        return (Slot) this.field_75151_b.get(i);
    }

    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        Slot slot = (Slot) this.field_75151_b.get(i);

        return slot != null ? slot.func_75211_c() : null;
    }

    public ItemStack func_75144_a(int i, int j, int k, EntityPlayer entityplayer) {
        ItemStack itemstack = null;
        InventoryPlayer inventoryplayer = entityplayer.field_71071_by;
        ItemStack itemstack1;
        int l;

        if (k == 5) {
            int i1 = this.field_94536_g;

            this.field_94536_g = func_94532_c(j);
            if ((i1 != 1 || this.field_94536_g != 2) && i1 != this.field_94536_g) {
                this.func_94533_d();
            } else if (inventoryplayer.func_70445_o() == null) {
                this.func_94533_d();
            } else if (this.field_94536_g == 0) {
                this.field_94535_f = func_94529_b(j);
                if (func_94528_d(this.field_94535_f)) {
                    this.field_94536_g = 1;
                    this.field_94537_h.clear();
                } else {
                    this.func_94533_d();
                }
            } else if (this.field_94536_g == 1) {
                Slot slot = (Slot) this.field_75151_b.get(i);

                if (slot != null && func_94527_a(slot, inventoryplayer.func_70445_o(), true) && slot.func_75214_a(inventoryplayer.func_70445_o()) && inventoryplayer.func_70445_o().field_77994_a > this.field_94537_h.size() && this.func_94531_b(slot)) {
                    this.field_94537_h.add(slot);
                }
            } else if (this.field_94536_g == 2) {
                if (!this.field_94537_h.isEmpty()) {
                    itemstack1 = inventoryplayer.func_70445_o().func_77946_l();
                    l = inventoryplayer.func_70445_o().field_77994_a;
                    Iterator iterator = this.field_94537_h.iterator();

                    while (iterator.hasNext()) {
                        Slot slot1 = (Slot) iterator.next();

                        if (slot1 != null && func_94527_a(slot1, inventoryplayer.func_70445_o(), true) && slot1.func_75214_a(inventoryplayer.func_70445_o()) && inventoryplayer.func_70445_o().field_77994_a >= this.field_94537_h.size() && this.func_94531_b(slot1)) {
                            ItemStack itemstack2 = itemstack1.func_77946_l();
                            int j1 = slot1.func_75216_d() ? slot1.func_75211_c().field_77994_a : 0;

                            func_94525_a(this.field_94537_h, this.field_94535_f, itemstack2, j1);
                            if (itemstack2.field_77994_a > itemstack2.func_77976_d()) {
                                itemstack2.field_77994_a = itemstack2.func_77976_d();
                            }

                            if (itemstack2.field_77994_a > slot1.func_75219_a()) {
                                itemstack2.field_77994_a = slot1.func_75219_a();
                            }

                            l -= itemstack2.field_77994_a - j1;
                            slot1.func_75215_d(itemstack2);
                        }
                    }

                    itemstack1.field_77994_a = l;
                    if (itemstack1.field_77994_a <= 0) {
                        itemstack1 = null;
                    }

                    inventoryplayer.func_70437_b(itemstack1);
                }

                this.func_94533_d();
            } else {
                this.func_94533_d();
            }
        } else if (this.field_94536_g != 0) {
            this.func_94533_d();
        } else {
            Slot slot2;
            int k1;
            ItemStack itemstack3;

            if ((k == 0 || k == 1) && (j == 0 || j == 1)) {
                if (i == -999) {
                    if (inventoryplayer.func_70445_o() != null && i == -999) {
                        if (j == 0) {
                            entityplayer.func_71019_a(inventoryplayer.func_70445_o(), true);
                            inventoryplayer.func_70437_b((ItemStack) null);
                        }

                        if (j == 1) {
                            entityplayer.func_71019_a(inventoryplayer.func_70445_o().func_77979_a(1), true);
                            if (inventoryplayer.func_70445_o().field_77994_a == 0) {
                                inventoryplayer.func_70437_b((ItemStack) null);
                            }
                        }
                    }
                } else if (k == 1) {
                    if (i < 0) {
                        return null;
                    }

                    slot2 = (Slot) this.field_75151_b.get(i);
                    if (slot2 != null && slot2.func_82869_a(entityplayer)) {
                        itemstack1 = this.func_82846_b(entityplayer, i);
                        if (itemstack1 != null) {
                            Item item = itemstack1.func_77973_b();

                            itemstack = itemstack1.func_77946_l();
                            if (slot2.func_75211_c() != null && slot2.func_75211_c().func_77973_b() == item) {
                                this.func_75133_b(i, j, true, entityplayer);
                            }
                        }
                    }
                } else {
                    if (i < 0) {
                        return null;
                    }

                    slot2 = (Slot) this.field_75151_b.get(i);
                    if (slot2 != null) {
                        itemstack1 = slot2.func_75211_c();
                        ItemStack itemstack4 = inventoryplayer.func_70445_o();

                        if (itemstack1 != null) {
                            itemstack = itemstack1.func_77946_l();
                        }

                        if (itemstack1 == null) {
                            if (itemstack4 != null && slot2.func_75214_a(itemstack4)) {
                                k1 = j == 0 ? itemstack4.field_77994_a : 1;
                                if (k1 > slot2.func_75219_a()) {
                                    k1 = slot2.func_75219_a();
                                }

                                if (itemstack4.field_77994_a >= k1) {
                                    slot2.func_75215_d(itemstack4.func_77979_a(k1));
                                }

                                if (itemstack4.field_77994_a == 0) {
                                    inventoryplayer.func_70437_b((ItemStack) null);
                                }
                            }
                        } else if (slot2.func_82869_a(entityplayer)) {
                            if (itemstack4 == null) {
                                k1 = j == 0 ? itemstack1.field_77994_a : (itemstack1.field_77994_a + 1) / 2;
                                itemstack3 = slot2.func_75209_a(k1);
                                inventoryplayer.func_70437_b(itemstack3);
                                if (itemstack1.field_77994_a == 0) {
                                    slot2.func_75215_d((ItemStack) null);
                                }

                                slot2.func_82870_a(entityplayer, inventoryplayer.func_70445_o());
                            } else if (slot2.func_75214_a(itemstack4)) {
                                if (itemstack1.func_77973_b() == itemstack4.func_77973_b() && itemstack1.func_77960_j() == itemstack4.func_77960_j() && ItemStack.func_77970_a(itemstack1, itemstack4)) {
                                    k1 = j == 0 ? itemstack4.field_77994_a : 1;
                                    if (k1 > slot2.func_75219_a() - itemstack1.field_77994_a) {
                                        k1 = slot2.func_75219_a() - itemstack1.field_77994_a;
                                    }

                                    if (k1 > itemstack4.func_77976_d() - itemstack1.field_77994_a) {
                                        k1 = itemstack4.func_77976_d() - itemstack1.field_77994_a;
                                    }

                                    itemstack4.func_77979_a(k1);
                                    if (itemstack4.field_77994_a == 0) {
                                        inventoryplayer.func_70437_b((ItemStack) null);
                                    }

                                    itemstack1.field_77994_a += k1;
                                } else if (itemstack4.field_77994_a <= slot2.func_75219_a()) {
                                    slot2.func_75215_d(itemstack4);
                                    inventoryplayer.func_70437_b(itemstack1);
                                }
                            } else if (itemstack1.func_77973_b() == itemstack4.func_77973_b() && itemstack4.func_77976_d() > 1 && (!itemstack1.func_77981_g() || itemstack1.func_77960_j() == itemstack4.func_77960_j()) && ItemStack.func_77970_a(itemstack1, itemstack4)) {
                                k1 = itemstack1.field_77994_a;
                                if (k1 > 0 && k1 + itemstack4.field_77994_a <= itemstack4.func_77976_d()) {
                                    itemstack4.field_77994_a += k1;
                                    itemstack1 = slot2.func_75209_a(k1);
                                    if (itemstack1.field_77994_a == 0) {
                                        slot2.func_75215_d((ItemStack) null);
                                    }

                                    slot2.func_82870_a(entityplayer, inventoryplayer.func_70445_o());
                                }
                            }
                        }

                        slot2.func_75218_e();
                    }
                }
            } else if (k == 2 && j >= 0 && j < 9) {
                slot2 = (Slot) this.field_75151_b.get(i);
                if (slot2.func_82869_a(entityplayer)) {
                    itemstack1 = inventoryplayer.func_70301_a(j);
                    boolean flag = itemstack1 == null || slot2.field_75224_c == inventoryplayer && slot2.func_75214_a(itemstack1);

                    k1 = -1;
                    if (!flag) {
                        k1 = inventoryplayer.func_70447_i();
                        flag |= k1 > -1;
                    }

                    if (slot2.func_75216_d() && flag) {
                        itemstack3 = slot2.func_75211_c();
                        inventoryplayer.func_70299_a(j, itemstack3.func_77946_l());
                        if ((slot2.field_75224_c != inventoryplayer || !slot2.func_75214_a(itemstack1)) && itemstack1 != null) {
                            if (k1 > -1) {
                                inventoryplayer.func_70441_a(itemstack1);
                                slot2.func_75209_a(itemstack3.field_77994_a);
                                slot2.func_75215_d((ItemStack) null);
                                slot2.func_82870_a(entityplayer, itemstack3);
                            }
                        } else {
                            slot2.func_75209_a(itemstack3.field_77994_a);
                            slot2.func_75215_d(itemstack1);
                            slot2.func_82870_a(entityplayer, itemstack3);
                        }
                    } else if (!slot2.func_75216_d() && itemstack1 != null && slot2.func_75214_a(itemstack1)) {
                        inventoryplayer.func_70299_a(j, (ItemStack) null);
                        slot2.func_75215_d(itemstack1);
                    }
                }
            } else if (k == 3 && entityplayer.field_71075_bZ.field_75098_d && inventoryplayer.func_70445_o() == null && i >= 0) {
                slot2 = (Slot) this.field_75151_b.get(i);
                if (slot2 != null && slot2.func_75216_d()) {
                    itemstack1 = slot2.func_75211_c().func_77946_l();
                    itemstack1.field_77994_a = itemstack1.func_77976_d();
                    inventoryplayer.func_70437_b(itemstack1);
                }
            } else if (k == 4 && inventoryplayer.func_70445_o() == null && i >= 0) {
                slot2 = (Slot) this.field_75151_b.get(i);
                if (slot2 != null && slot2.func_75216_d() && slot2.func_82869_a(entityplayer)) {
                    itemstack1 = slot2.func_75209_a(j == 0 ? 1 : slot2.func_75211_c().field_77994_a);
                    slot2.func_82870_a(entityplayer, itemstack1);
                    entityplayer.func_71019_a(itemstack1, true);
                }
            } else if (k == 6 && i >= 0) {
                slot2 = (Slot) this.field_75151_b.get(i);
                itemstack1 = inventoryplayer.func_70445_o();
                if (itemstack1 != null && (slot2 == null || !slot2.func_75216_d() || !slot2.func_82869_a(entityplayer))) {
                    l = j == 0 ? 0 : this.field_75151_b.size() - 1;
                    k1 = j == 0 ? 1 : -1;

                    for (int l1 = 0; l1 < 2; ++l1) {
                        for (int i2 = l; i2 >= 0 && i2 < this.field_75151_b.size() && itemstack1.field_77994_a < itemstack1.func_77976_d(); i2 += k1) {
                            Slot slot3 = (Slot) this.field_75151_b.get(i2);

                            if (slot3.func_75216_d() && func_94527_a(slot3, itemstack1, true) && slot3.func_82869_a(entityplayer) && this.func_94530_a(itemstack1, slot3) && (l1 != 0 || slot3.func_75211_c().field_77994_a != slot3.func_75211_c().func_77976_d())) {
                                int j2 = Math.min(itemstack1.func_77976_d() - itemstack1.field_77994_a, slot3.func_75211_c().field_77994_a);
                                ItemStack itemstack5 = slot3.func_75209_a(j2);

                                itemstack1.field_77994_a += j2;
                                if (itemstack5.field_77994_a <= 0) {
                                    slot3.func_75215_d((ItemStack) null);
                                }

                                slot3.func_82870_a(entityplayer, itemstack5);
                            }
                        }
                    }
                }

                this.func_75142_b();
            }
        }

        return itemstack;
    }

    public boolean func_94530_a(ItemStack itemstack, Slot slot) {
        return true;
    }

    protected void func_75133_b(int i, int j, boolean flag, EntityPlayer entityplayer) {
        this.func_75144_a(i, j, 1, entityplayer);
    }

    public void func_75134_a(EntityPlayer entityplayer) {
        InventoryPlayer inventoryplayer = entityplayer.field_71071_by;

        if (inventoryplayer.func_70445_o() != null) {
            entityplayer.func_71019_a(inventoryplayer.func_70445_o(), false);
            inventoryplayer.func_70437_b((ItemStack) null);
        }

    }

    public void func_75130_a(IInventory iinventory) {
        this.func_75142_b();
    }

    public void func_75141_a(int i, ItemStack itemstack) {
        this.func_75139_a(i).func_75215_d(itemstack);
    }

    public boolean func_75129_b(EntityPlayer entityplayer) {
        return !this.field_75148_f.contains(entityplayer);
    }

    public void func_75128_a(EntityPlayer entityplayer, boolean flag) {
        if (flag) {
            this.field_75148_f.remove(entityplayer);
        } else {
            this.field_75148_f.add(entityplayer);
        }

    }

    public abstract boolean func_75145_c(EntityPlayer entityplayer);

    protected boolean func_75135_a(ItemStack itemstack, int i, int j, boolean flag) {
        boolean flag1 = false;
        int k = i;

        if (flag) {
            k = j - 1;
        }

        Slot slot;
        ItemStack itemstack1;

        if (itemstack.func_77985_e()) {
            while (itemstack.field_77994_a > 0 && (!flag && k < j || flag && k >= i)) {
                slot = (Slot) this.field_75151_b.get(k);
                itemstack1 = slot.func_75211_c();
                if (itemstack1 != null && itemstack1.func_77973_b() == itemstack.func_77973_b() && (!itemstack.func_77981_g() || itemstack.func_77960_j() == itemstack1.func_77960_j()) && ItemStack.func_77970_a(itemstack, itemstack1)) {
                    int l = itemstack1.field_77994_a + itemstack.field_77994_a;

                    if (l <= itemstack.func_77976_d()) {
                        itemstack.field_77994_a = 0;
                        itemstack1.field_77994_a = l;
                        slot.func_75218_e();
                        flag1 = true;
                    } else if (itemstack1.field_77994_a < itemstack.func_77976_d()) {
                        itemstack.field_77994_a -= itemstack.func_77976_d() - itemstack1.field_77994_a;
                        itemstack1.field_77994_a = itemstack.func_77976_d();
                        slot.func_75218_e();
                        flag1 = true;
                    }
                }

                if (flag) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        if (itemstack.field_77994_a > 0) {
            if (flag) {
                k = j - 1;
            } else {
                k = i;
            }

            while (!flag && k < j || flag && k >= i) {
                slot = (Slot) this.field_75151_b.get(k);
                itemstack1 = slot.func_75211_c();
                if (itemstack1 == null) {
                    slot.func_75215_d(itemstack.func_77946_l());
                    slot.func_75218_e();
                    itemstack.field_77994_a = 0;
                    flag1 = true;
                    break;
                }

                if (flag) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        return flag1;
    }

    public static int func_94529_b(int i) {
        return i >> 2 & 3;
    }

    public static int func_94532_c(int i) {
        return i & 3;
    }

    public static boolean func_94528_d(int i) {
        return i == 0 || i == 1;
    }

    protected void func_94533_d() {
        this.field_94536_g = 0;
        this.field_94537_h.clear();
    }

    public static boolean func_94527_a(Slot slot, ItemStack itemstack, boolean flag) {
        boolean flag1 = slot == null || !slot.func_75216_d();

        if (slot != null && slot.func_75216_d() && itemstack != null && itemstack.func_77969_a(slot.func_75211_c()) && ItemStack.func_77970_a(slot.func_75211_c(), itemstack)) {
            flag1 |= slot.func_75211_c().field_77994_a + (flag ? 0 : itemstack.field_77994_a) <= itemstack.func_77976_d();
        }

        return flag1;
    }

    public static void func_94525_a(Set set, int i, ItemStack itemstack, int j) {
        switch (i) {
            case 0:
                itemstack.field_77994_a = MathHelper.func_76141_d((float) itemstack.field_77994_a / (float) set.size());
                break;
            case 1:
                itemstack.field_77994_a = 1;
        }

        itemstack.field_77994_a += j;
    }

    public boolean func_94531_b(Slot slot) {
        return true;
    }

    public static int func_94526_b(IInventory iinventory) {
        if (iinventory == null) {
            return 0;
        } else {
            int i = 0;
            float f = 0.0F;

            for (int j = 0; j < iinventory.func_70302_i_(); ++j) {
                ItemStack itemstack = iinventory.func_70301_a(j);

                if (itemstack != null) {
                    f += (float) itemstack.field_77994_a / (float) Math.min(iinventory.func_70297_j_(), itemstack.func_77976_d());
                    ++i;
                }
            }

            f /= (float) iinventory.func_70302_i_();
            return MathHelper.func_76141_d(f * 14.0F) + (i > 0 ? 1 : 0);
        }
    }
}
