package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {

    private BlockPressurePlate$Sensitivity field_150069_a;

    protected BlockPressurePlate(String s, Material material, BlockPressurePlate$Sensitivity blockpressureplate$sensitivity) {
        super(s, material);
        this.field_150069_a = blockpressureplate$sensitivity;
    }

    @Override
    protected int func_150066_d(int i) {
        return i > 0 ? 1 : 0;
    }

    @Override
    protected int func_150060_c(int i) {
        return i == 1 ? 15 : 0;
    }

    @Override
    protected int func_150065_e(World world, int i, int j, int k) {
        List list = null;

        if (this.field_150069_a == BlockPressurePlate$Sensitivity.everything) {
            list = world.func_72839_b((Entity) null, this.func_150061_a(i, j, k));
        }

        if (this.field_150069_a == BlockPressurePlate$Sensitivity.mobs) {
            list = world.func_72872_a(EntityLivingBase.class, this.func_150061_a(i, j, k));
        }

        if (this.field_150069_a == BlockPressurePlate$Sensitivity.players) {
            list = world.func_72872_a(EntityPlayer.class, this.func_150061_a(i, j, k));
        }

        if (list != null && !list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (!entity.func_145773_az()) {
                    return 15;
                }
            }
        }

        return 0;
    }
}
