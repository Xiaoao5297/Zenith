package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {

    public BlockRedstoneComparator(boolean flag) {
        super(flag);
        this.field_149758_A = true;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151132_bS;
    }

    @Override
    protected int func_149901_b(int i) {
        return 2;
    }

    @Override
    protected BlockRedstoneDiode func_149906_e() {
        return Blocks.field_150455_bV;
    }

    @Override
    protected BlockRedstoneDiode func_149898_i() {
        return Blocks.field_150441_bU;
    }

    @Override
    public int func_149645_b() {
        return 37;
    }

    @Override
    protected boolean func_149905_c(int i) {
        return this.field_149914_a || (i & 8) != 0;
    }

    @Override
    protected int func_149904_f(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return this.func_149971_e(iblockaccess, i, j, k).func_145996_a();
    }

    private int func_149970_j(World world, int i, int j, int k, int l) {
        return !this.func_149969_d(l) ? this.func_149903_h(world, i, j, k, l) : Math.max(this.func_149903_h(world, i, j, k, l) - this.func_149902_h(world, i, j, k, l), 0);
    }

    public boolean func_149969_d(int i) {
        return (i & 4) == 4;
    }

    @Override
    protected boolean func_149900_a(World world, int i, int j, int k, int l) {
        int i1 = this.func_149903_h(world, i, j, k, l);

        if (i1 >= 15) {
            return true;
        } else if (i1 == 0) {
            return false;
        } else {
            int j1 = this.func_149902_h(world, i, j, k, l);

            return j1 == 0 ? true : i1 >= j1;
        }
    }

    @Override
    protected int func_149903_h(World world, int i, int j, int k, int l) {
        int i1 = super.func_149903_h(world, i, j, k, l);
        int j1 = func_149895_l(l);
        int k1 = i + Direction.field_71583_a[j1];
        int l1 = k + Direction.field_71581_b[j1];
        Block block = world.func_147439_a(k1, j, l1);

        if (block.func_149740_M()) {
            i1 = block.func_149736_g(world, k1, j, l1, Direction.field_71580_e[j1]);
        } else if (i1 < 15 && block.func_149721_r()) {
            k1 += Direction.field_71583_a[j1];
            l1 += Direction.field_71581_b[j1];
            block = world.func_147439_a(k1, j, l1);
            if (block.func_149740_M()) {
                i1 = block.func_149736_g(world, k1, j, l1, Direction.field_71580_e[j1]);
            }
        }

        return i1;
    }

    public TileEntityComparator func_149971_e(IBlockAccess iblockaccess, int i, int j, int k) {
        return (TileEntityComparator) iblockaccess.func_147438_o(i, j, k);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        int i1 = world.func_72805_g(i, j, k);
        boolean flag = this.field_149914_a | (i1 & 8) != 0;
        boolean flag1 = !this.func_149969_d(i1);
        int j1 = flag1 ? 4 : 0;

        j1 |= flag ? 8 : 0;
        world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F, flag1 ? 0.55F : 0.5F);
        world.func_72921_c(i, j, k, j1 | i1 & 3, 2);
        this.func_149972_c(world, i, j, k, world.field_73012_v);
        return true;
    }

    @Override
    protected void func_149897_b(World world, int i, int j, int k, Block block) {
        if (!world.func_147477_a(i, j, k, this)) {
            int l = world.func_72805_g(i, j, k);
            int i1 = this.func_149970_j(world, i, j, k, l);
            int j1 = this.func_149971_e(world, i, j, k).func_145996_a();

            if (i1 != j1 || this.func_149905_c(l) != this.func_149900_a(world, i, j, k, l)) {
                if (this.func_149912_i(world, i, j, k, l)) {
                    world.func_147454_a(i, j, k, this, this.func_149901_b(0), -1);
                } else {
                    world.func_147454_a(i, j, k, this, this.func_149901_b(0), 0);
                }
            }
        }

    }

    private void func_149972_c(World world, int i, int j, int k, Random random) {
        int l = world.func_72805_g(i, j, k);
        int i1 = this.func_149970_j(world, i, j, k, l);
        int j1 = this.func_149971_e(world, i, j, k).func_145996_a();

        this.func_149971_e(world, i, j, k).func_145995_a(i1);
        if (j1 != i1 || !this.func_149969_d(l)) {
            boolean flag = this.func_149900_a(world, i, j, k, l);
            boolean flag1 = this.field_149914_a || (l & 8) != 0;

            if (flag1 && !flag) {
                world.func_72921_c(i, j, k, l & -9, 2);
            } else if (!flag1 && flag) {
                world.func_72921_c(i, j, k, l | 8, 2);
            }

            this.func_149911_e(world, i, j, k);
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (this.field_149914_a) {
            int l = world.func_72805_g(i, j, k);

            world.func_147465_d(i, j, k, this.func_149898_i(), l | 8, 4);
        }

        this.func_149972_c(world, i, j, k, random);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        world.func_147455_a(i, j, k, this.func_149915_a(world, 0));
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        super.func_149749_a(world, i, j, k, block, l);
        world.func_147475_p(i, j, k);
        this.func_149911_e(world, i, j, k);
    }

    @Override
    public boolean func_149696_a(World world, int i, int j, int k, int l, int i1) {
        super.func_149696_a(world, i, j, k, l, i1);
        TileEntity tileentity = world.func_147438_o(i, j, k);

        return tileentity != null ? tileentity.func_145842_c(l, i1) : false;
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityComparator();
    }
}
