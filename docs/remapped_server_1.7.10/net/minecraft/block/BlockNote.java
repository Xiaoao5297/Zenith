package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer {

    public BlockNote() {
        super(Material.field_151575_d);
        this.func_149647_a(CreativeTabs.field_78028_d);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        boolean flag = world.func_72864_z(i, j, k);
        TileEntityNote tileentitynote = (TileEntityNote) world.func_147438_o(i, j, k);

        if (tileentitynote != null && tileentitynote.field_145880_i != flag) {
            if (flag) {
                tileentitynote.func_145878_a(world, i, j, k);
            }

            tileentitynote.field_145880_i = flag;
        }

    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            TileEntityNote tileentitynote = (TileEntityNote) world.func_147438_o(i, j, k);

            if (tileentitynote != null) {
                tileentitynote.func_145877_a();
                tileentitynote.func_145878_a(world, i, j, k);
            }

            return true;
        }
    }

    @Override
    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {
        if (!world.field_72995_K) {
            TileEntityNote tileentitynote = (TileEntityNote) world.func_147438_o(i, j, k);

            if (tileentitynote != null) {
                tileentitynote.func_145878_a(world, i, j, k);
            }

        }
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityNote();
    }

    @Override
    public boolean func_149696_a(World world, int i, int j, int k, int l, int i1) {
        float f = (float) Math.pow(2.0D, (double) (i1 - 12) / 12.0D);
        String s = "harp";

        if (l == 1) {
            s = "bd";
        }

        if (l == 2) {
            s = "snare";
        }

        if (l == 3) {
            s = "hat";
        }

        if (l == 4) {
            s = "bassattack";
        }

        world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "note." + s, 3.0F, f);
        world.func_72869_a("note", (double) i + 0.5D, (double) j + 1.2D, (double) k + 0.5D, (double) i1 / 24.0D, 0.0D, 0.0D);
        return true;
    }
}
