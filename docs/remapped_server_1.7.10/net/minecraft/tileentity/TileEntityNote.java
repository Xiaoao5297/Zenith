package net.minecraft.tileentity;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityNote extends TileEntity {

    public byte field_145879_a;
    public boolean field_145880_i;

    public TileEntityNote() {}

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound) {
        super.func_145841_b(nbttagcompound);
        nbttagcompound.func_74774_a("note", this.field_145879_a);
    }

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound) {
        super.func_145839_a(nbttagcompound);
        this.field_145879_a = nbttagcompound.func_74771_c("note");
        if (this.field_145879_a < 0) {
            this.field_145879_a = 0;
        }

        if (this.field_145879_a > 24) {
            this.field_145879_a = 24;
        }

    }

    public void func_145877_a() {
        this.field_145879_a = (byte) ((this.field_145879_a + 1) % 25);
        this.func_70296_d();
    }

    public void func_145878_a(World world, int i, int j, int k) {
        if (world.func_147439_a(i, j + 1, k).func_149688_o() == Material.field_151579_a) {
            Material material = world.func_147439_a(i, j - 1, k).func_149688_o();
            byte b0 = 0;

            if (material == Material.field_151576_e) {
                b0 = 1;
            }

            if (material == Material.field_151595_p) {
                b0 = 2;
            }

            if (material == Material.field_151592_s) {
                b0 = 3;
            }

            if (material == Material.field_151575_d) {
                b0 = 4;
            }

            world.func_147452_c(i, j, k, Blocks.field_150323_B, b0, this.field_145879_a);
        }
    }
}
