package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntity implements IHopper {

    private ItemStack[] field_145900_a = new ItemStack[5];
    private String field_145902_i;
    private int field_145901_j = -1;

    public TileEntityHopper() {}

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        super.func_145839_a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Items", 10);

        this.field_145900_a = new ItemStack[this.func_70302_i_()];
        if (nbttagcompound.func_150297_b("CustomName", 8)) {
            this.field_145902_i = nbttagcompound.func_74779_i("CustomName");
        }

        this.field_145901_j = nbttagcompound.func_74762_e("TransferCooldown");

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            byte b0 = nbttagcompound1.func_74771_c("Slot");

            if (b0 >= 0 && b0 < this.field_145900_a.length) {
                this.field_145900_a[b0] = ItemStack.func_77949_a(nbttagcompound1);
            }
        }

    }

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.field_145900_a.length; ++i) {
            if (this.field_145900_a[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.func_74774_a("Slot", (byte) i);
                this.field_145900_a[i].func_77955_b(nbttagcompound1);
                nbttaglist.func_74742_a(nbttagcompound1);
            }
        }

        nbttagcompound.func_74782_a("Items", nbttaglist);
        nbttagcompound.func_74768_a("TransferCooldown", this.field_145901_j);
        if (this.func_145818_k_()) {
            nbttagcompound.func_74778_a("CustomName", this.field_145902_i);
        }

    }

    @Override
    public void func_70296_d() {
        super.func_70296_d();
    }

    @Override
    public int func_70302_i_() {
        return this.field_145900_a.length;
    }

    @Override
    public ItemStack func_70301_a(int i) {
        return this.field_145900_a[i];
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        if (this.field_145900_a[i] != null) {
            ItemStack itemstack;

            if (this.field_145900_a[i].field_77994_a <= j) {
                itemstack = this.field_145900_a[i];
                this.field_145900_a[i] = null;
                return itemstack;
            } else {
                itemstack = this.field_145900_a[i].func_77979_a(j);
                if (this.field_145900_a[i].field_77994_a == 0) {
                    this.field_145900_a[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack func_70304_b(int i) {
        if (this.field_145900_a[i] != null) {
            ItemStack itemstack = this.field_145900_a[i];

            this.field_145900_a[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        this.field_145900_a[i] = itemstack;
        if (itemstack != null && itemstack.field_77994_a > this.func_70297_j_()) {
            itemstack.field_77994_a = this.func_70297_j_();
        }

    }

    @Override
    public String func_145825_b() {
        return this.func_145818_k_() ? this.field_145902_i : "container.hopper";
    }

    @Override
    public boolean func_145818_k_() {
        return this.field_145902_i != null && this.field_145902_i.length() > 0;
    }

    public void func_145886_a(String s) {
        this.field_145902_i = s;
    }

    @Override
    public int func_70297_j_() {
        return 64;
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
        return true;
    }

    @Override
    public void func_145845_h() {
        if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
            --this.field_145901_j;
            if (!this.func_145888_j()) {
                this.func_145896_c(0);
                this.func_145887_i();
            }

        }
    }

    public boolean func_145887_i() {
        if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
            if (!this.func_145888_j() && BlockHopper.func_149917_c(this.func_145832_p())) {
                boolean flag = false;

                if (!this.func_152104_k()) {
                    flag = this.func_145883_k();
                }

                if (!this.func_152105_l()) {
                    flag = func_145891_a(this) || flag;
                }

                if (flag) {
                    this.func_145896_c(8);
                    this.func_70296_d();
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private boolean func_152104_k() {
        ItemStack[] aitemstack = this.field_145900_a;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            if (itemstack != null) {
                return false;
            }
        }

        return true;
    }

    private boolean func_152105_l() {
        ItemStack[] aitemstack = this.field_145900_a;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            if (itemstack == null || itemstack.field_77994_a != itemstack.func_77976_d()) {
                return false;
            }
        }

        return true;
    }

    private boolean func_145883_k() {
        IInventory iinventory = this.func_145895_l();

        if (iinventory == null) {
            return false;
        } else {
            int i = Facing.field_71588_a[BlockHopper.func_149918_b(this.func_145832_p())];

            if (this.func_152102_a(iinventory, i)) {
                return false;
            } else {
                for (int j = 0; j < this.func_70302_i_(); ++j) {
                    if (this.func_70301_a(j) != null) {
                        ItemStack itemstack = this.func_70301_a(j).func_77946_l();
                        ItemStack itemstack1 = func_145889_a(iinventory, this.func_70298_a(j, 1), i);

                        if (itemstack1 == null || itemstack1.field_77994_a == 0) {
                            iinventory.func_70296_d();
                            return true;
                        }

                        this.func_70299_a(j, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private boolean func_152102_a(IInventory iinventory, int i) {
        if (iinventory instanceof ISidedInventory && i > -1) {
            ISidedInventory isidedinventory = (ISidedInventory) iinventory;
            int[] aint = isidedinventory.func_94128_d(i);

            for (int j = 0; j < aint.length; ++j) {
                ItemStack itemstack = isidedinventory.func_70301_a(aint[j]);

                if (itemstack == null || itemstack.field_77994_a != itemstack.func_77976_d()) {
                    return false;
                }
            }
        } else {
            int k = iinventory.func_70302_i_();

            for (int l = 0; l < k; ++l) {
                ItemStack itemstack1 = iinventory.func_70301_a(l);

                if (itemstack1 == null || itemstack1.field_77994_a != itemstack1.func_77976_d()) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean func_152103_b(IInventory iinventory, int i) {
        if (iinventory instanceof ISidedInventory && i > -1) {
            ISidedInventory isidedinventory = (ISidedInventory) iinventory;
            int[] aint = isidedinventory.func_94128_d(i);

            for (int j = 0; j < aint.length; ++j) {
                if (isidedinventory.func_70301_a(aint[j]) != null) {
                    return false;
                }
            }
        } else {
            int k = iinventory.func_70302_i_();

            for (int l = 0; l < k; ++l) {
                if (iinventory.func_70301_a(l) != null) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean func_145891_a(IHopper ihopper) {
        IInventory iinventory = func_145884_b(ihopper);

        if (iinventory != null) {
            byte b0 = 0;

            if (func_152103_b(iinventory, b0)) {
                return false;
            }

            if (iinventory instanceof ISidedInventory && b0 > -1) {
                ISidedInventory isidedinventory = (ISidedInventory) iinventory;
                int[] aint = isidedinventory.func_94128_d(b0);

                for (int i = 0; i < aint.length; ++i) {
                    if (func_145892_a(ihopper, iinventory, aint[i], b0)) {
                        return true;
                    }
                }
            } else {
                int j = iinventory.func_70302_i_();

                for (int k = 0; k < j; ++k) {
                    if (func_145892_a(ihopper, iinventory, k, b0)) {
                        return true;
                    }
                }
            }
        } else {
            EntityItem entityitem = func_145897_a(ihopper.func_145831_w(), ihopper.func_96107_aA(), ihopper.func_96109_aB() + 1.0D, ihopper.func_96108_aC());

            if (entityitem != null) {
                return func_145898_a(ihopper, entityitem);
            }
        }

        return false;
    }

    private static boolean func_145892_a(IHopper ihopper, IInventory iinventory, int i, int j) {
        ItemStack itemstack = iinventory.func_70301_a(i);

        if (itemstack != null && func_145890_b(iinventory, itemstack, i, j)) {
            ItemStack itemstack1 = itemstack.func_77946_l();
            ItemStack itemstack2 = func_145889_a(ihopper, iinventory.func_70298_a(i, 1), -1);

            if (itemstack2 == null || itemstack2.field_77994_a == 0) {
                iinventory.func_70296_d();
                return true;
            }

            iinventory.func_70299_a(i, itemstack1);
        }

        return false;
    }

    public static boolean func_145898_a(IInventory iinventory, EntityItem entityitem) {
        boolean flag = false;

        if (entityitem == null) {
            return false;
        } else {
            ItemStack itemstack = entityitem.func_92059_d().func_77946_l();
            ItemStack itemstack1 = func_145889_a(iinventory, itemstack, -1);

            if (itemstack1 != null && itemstack1.field_77994_a != 0) {
                entityitem.func_92058_a(itemstack1);
            } else {
                flag = true;
                entityitem.func_70106_y();
            }

            return flag;
        }
    }

    public static ItemStack func_145889_a(IInventory iinventory, ItemStack itemstack, int i) {
        if (iinventory instanceof ISidedInventory && i > -1) {
            ISidedInventory isidedinventory = (ISidedInventory) iinventory;
            int[] aint = isidedinventory.func_94128_d(i);

            for (int j = 0; j < aint.length && itemstack != null && itemstack.field_77994_a > 0; ++j) {
                itemstack = func_145899_c(iinventory, itemstack, aint[j], i);
            }
        } else {
            int k = iinventory.func_70302_i_();

            for (int l = 0; l < k && itemstack != null && itemstack.field_77994_a > 0; ++l) {
                itemstack = func_145899_c(iinventory, itemstack, l, i);
            }
        }

        if (itemstack != null && itemstack.field_77994_a == 0) {
            itemstack = null;
        }

        return itemstack;
    }

    private static boolean func_145885_a(IInventory iinventory, ItemStack itemstack, int i, int j) {
        return !iinventory.func_94041_b(i, itemstack) ? false : !(iinventory instanceof ISidedInventory) || ((ISidedInventory) iinventory).func_102007_a(i, itemstack, j);
    }

    private static boolean func_145890_b(IInventory iinventory, ItemStack itemstack, int i, int j) {
        return !(iinventory instanceof ISidedInventory) || ((ISidedInventory) iinventory).func_102008_b(i, itemstack, j);
    }

    private static ItemStack func_145899_c(IInventory iinventory, ItemStack itemstack, int i, int j) {
        ItemStack itemstack1 = iinventory.func_70301_a(i);

        if (func_145885_a(iinventory, itemstack, i, j)) {
            boolean flag = false;

            if (itemstack1 == null) {
                iinventory.func_70299_a(i, itemstack);
                itemstack = null;
                flag = true;
            } else if (func_145894_a(itemstack1, itemstack)) {
                int k = itemstack.func_77976_d() - itemstack1.field_77994_a;
                int l = Math.min(itemstack.field_77994_a, k);

                itemstack.field_77994_a -= l;
                itemstack1.field_77994_a += l;
                flag = l > 0;
            }

            if (flag) {
                if (iinventory instanceof TileEntityHopper) {
                    ((TileEntityHopper) iinventory).func_145896_c(8);
                    iinventory.func_70296_d();
                }

                iinventory.func_70296_d();
            }
        }

        return itemstack;
    }

    private IInventory func_145895_l() {
        int i = BlockHopper.func_149918_b(this.func_145832_p());

        return func_145893_b(this.func_145831_w(), (double) (this.field_145851_c + Facing.field_71586_b[i]), (double) (this.field_145848_d + Facing.field_71587_c[i]), (double) (this.field_145849_e + Facing.field_71585_d[i]));
    }

    public static IInventory func_145884_b(IHopper ihopper) {
        return func_145893_b(ihopper.func_145831_w(), ihopper.func_96107_aA(), ihopper.func_96109_aB() + 1.0D, ihopper.func_96108_aC());
    }

    public static EntityItem func_145897_a(World world, double d0, double d1, double d2) {
        List list = world.func_82733_a(EntityItem.class, AxisAlignedBB.func_72330_a(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D), IEntitySelector.field_94557_a);

        return list.size() > 0 ? (EntityItem) list.get(0) : null;
    }

    public static IInventory func_145893_b(World world, double d0, double d1, double d2) {
        IInventory iinventory = null;
        int i = MathHelper.func_76128_c(d0);
        int j = MathHelper.func_76128_c(d1);
        int k = MathHelper.func_76128_c(d2);
        TileEntity tileentity = world.func_147438_o(i, j, k);

        if (tileentity != null && tileentity instanceof IInventory) {
            iinventory = (IInventory) tileentity;
            if (iinventory instanceof TileEntityChest) {
                Block block = world.func_147439_a(i, j, k);

                if (block instanceof BlockChest) {
                    iinventory = ((BlockChest) block).func_149951_m(world, i, j, k);
                }
            }
        }

        if (iinventory == null) {
            List list = world.func_94576_a((Entity) null, AxisAlignedBB.func_72330_a(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D), IEntitySelector.field_96566_b);

            if (list != null && list.size() > 0) {
                iinventory = (IInventory) list.get(world.field_73012_v.nextInt(list.size()));
            }
        }

        return iinventory;
    }

    private static boolean func_145894_a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.func_77973_b() != itemstack1.func_77973_b() ? false : (itemstack.func_77960_j() != itemstack1.func_77960_j() ? false : (itemstack.field_77994_a > itemstack.func_77976_d() ? false : ItemStack.func_77970_a(itemstack, itemstack1)));
    }

    @Override
    public double func_96107_aA() {
        return (double) this.field_145851_c;
    }

    @Override
    public double func_96109_aB() {
        return (double) this.field_145848_d;
    }

    @Override
    public double func_96108_aC() {
        return (double) this.field_145849_e;
    }

    public void func_145896_c(int i) {
        this.field_145901_j = i;
    }

    public boolean func_145888_j() {
        return this.field_145901_j > 0;
    }
}
