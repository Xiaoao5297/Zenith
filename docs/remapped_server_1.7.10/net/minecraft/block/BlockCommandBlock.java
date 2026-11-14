package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer {

    public BlockCommandBlock() {
        super(Material.field_151573_f);
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityCommandBlock();
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K) {
            boolean flag = world.func_72864_z(i, j, k);
            int l = world.func_72805_g(i, j, k);
            boolean flag1 = (l & 1) != 0;

            if (flag && !flag1) {
                world.func_72921_c(i, j, k, l | 1, 4);
                world.func_147464_a(i, j, k, this, this.func_149738_a(world));
            } else if (!flag && flag1) {
                world.func_72921_c(i, j, k, l & -2, 4);
            }
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        TileEntity tileentity = world.func_147438_o(i, j, k);

        if (tileentity != null && tileentity instanceof TileEntityCommandBlock) {
            CommandBlockLogic commandblocklogic = ((TileEntityCommandBlock) tileentity).func_145993_a();

            commandblocklogic.func_145755_a(world);
            world.func_147453_f(i, j, k, this);
        }

    }

    @Override
    public int func_149738_a(World world) {
        return 1;
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock) world.func_147438_o(i, j, k);

        if (tileentitycommandblock != null) {
            entityplayer.func_146100_a(tileentitycommandblock);
        }

        return true;
    }

    @Override
    public boolean func_149740_M() {
        return true;
    }

    @Override
    public int func_149736_g(World world, int i, int j, int k, int l) {
        TileEntity tileentity = world.func_147438_o(i, j, k);

        return tileentity != null && tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock) tileentity).func_145993_a().func_145760_g() : 0;
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock) world.func_147438_o(i, j, k);

        if (itemstack.func_82837_s()) {
            tileentitycommandblock.func_145993_a().func_145754_b(itemstack.func_82833_r());
        }

    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }
}
