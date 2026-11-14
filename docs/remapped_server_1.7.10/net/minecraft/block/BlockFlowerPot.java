package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer {

    public BlockFlowerPot() {
        super(Material.field_151594_q);
        this.func_149683_g();
    }

    @Override
    public void func_149683_g() {
        float f = 0.375F;
        float f1 = f / 2.0F;

        this.func_149676_a(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, f, 0.5F + f1);
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 33;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

        if (itemstack != null && itemstack.func_77973_b() instanceof ItemBlock) {
            TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(world, i, j, k);

            if (tileentityflowerpot != null) {
                if (tileentityflowerpot.func_145965_a() != null) {
                    return false;
                } else {
                    Block block = Block.func_149634_a(itemstack.func_77973_b());

                    if (!this.func_149928_a(block, itemstack.func_77960_j())) {
                        return false;
                    } else {
                        tileentityflowerpot.func_145964_a(itemstack.func_77973_b(), itemstack.func_77960_j());
                        tileentityflowerpot.func_70296_d();
                        if (!world.func_72921_c(i, j, k, itemstack.func_77960_j(), 2)) {
                            world.func_147471_g(i, j, k);
                        }

                        if (!entityplayer.field_71075_bZ.field_75098_d && --itemstack.field_77994_a <= 0) {
                            entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
                        }

                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean func_149928_a(Block block, int i) {
        return block != Blocks.field_150327_N && block != Blocks.field_150328_O && block != Blocks.field_150434_aF && block != Blocks.field_150338_P && block != Blocks.field_150337_Q && block != Blocks.field_150345_g && block != Blocks.field_150330_I ? block == Blocks.field_150329_H && i == 2 : true;
    }

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(world, i, j, k);

        return tileentityflowerpot != null && tileentityflowerpot.func_145965_a() != null ? tileentityflowerpot.func_145966_b() : 0;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return super.func_149742_c(world, i, j, k) && World.func_147466_a(world, i, j - 1, k);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!World.func_147466_a(world, i, j - 1, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
        }

    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(world, i, j, k);

        if (tileentityflowerpot != null && tileentityflowerpot.func_145965_a() != null) {
            this.func_149642_a(world, i, j, k, new ItemStack(tileentityflowerpot.func_145965_a(), 1, tileentityflowerpot.func_145966_b()));
        }

        super.func_149749_a(world, i, j, k, block, l);
    }

    @Override
    public void func_149681_a(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {
        super.func_149681_a(world, i, j, k, l, entityplayer);
        if (entityplayer.field_71075_bZ.field_75098_d) {
            TileEntityFlowerPot tileentityflowerpot = this.func_149929_e(world, i, j, k);

            if (tileentityflowerpot != null) {
                tileentityflowerpot.func_145964_a(Item.func_150899_d(0), 0);
            }
        }

    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151162_bE;
    }

    private TileEntityFlowerPot func_149929_e(World world, int i, int j, int k) {
        TileEntity tileentity = world.func_147438_o(i, j, k);

        return tileentity != null && tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot) tileentity : null;
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        Object object = null;
        byte b0 = 0;

        switch (i) {
            case 1:
                object = Blocks.field_150328_O;
                b0 = 0;
                break;
            case 2:
                object = Blocks.field_150327_N;
                break;
            case 3:
                object = Blocks.field_150345_g;
                b0 = 0;
                break;
            case 4:
                object = Blocks.field_150345_g;
                b0 = 1;
                break;
            case 5:
                object = Blocks.field_150345_g;
                b0 = 2;
                break;
            case 6:
                object = Blocks.field_150345_g;
                b0 = 3;
                break;
            case 7:
                object = Blocks.field_150337_Q;
                break;
            case 8:
                object = Blocks.field_150338_P;
                break;
            case 9:
                object = Blocks.field_150434_aF;
                break;
            case 10:
                object = Blocks.field_150330_I;
                break;
            case 11:
                object = Blocks.field_150329_H;
                b0 = 2;
                break;
            case 12:
                object = Blocks.field_150345_g;
                b0 = 4;
                break;
            case 13:
                object = Blocks.field_150345_g;
                b0 = 5;
        }

        return new TileEntityFlowerPot(Item.func_150898_a((Block) object), b0);
    }
}
