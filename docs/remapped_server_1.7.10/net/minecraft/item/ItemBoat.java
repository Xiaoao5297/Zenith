package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat extends Item {

    public ItemBoat() {
        this.field_77777_bU = 1;
        this.func_77637_a(CreativeTabs.field_78029_e);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        float f = 1.0F;
        float f1 = entityplayer.field_70127_C + (entityplayer.field_70125_A - entityplayer.field_70127_C) * f;
        float f2 = entityplayer.field_70126_B + (entityplayer.field_70177_z - entityplayer.field_70126_B) * f;
        double d0 = entityplayer.field_70169_q + (entityplayer.field_70165_t - entityplayer.field_70169_q) * (double) f;
        double d1 = entityplayer.field_70167_r + (entityplayer.field_70163_u - entityplayer.field_70167_r) * (double) f + 1.62D - (double) entityplayer.field_70129_M;
        double d2 = entityplayer.field_70166_s + (entityplayer.field_70161_v - entityplayer.field_70166_s) * (double) f;
        Vec3 vec3 = Vec3.func_72443_a(d0, d1, d2);
        float f3 = MathHelper.func_76134_b(-f2 * 0.017453292F - 3.1415927F);
        float f4 = MathHelper.func_76126_a(-f2 * 0.017453292F - 3.1415927F);
        float f5 = -MathHelper.func_76134_b(-f1 * 0.017453292F);
        float f6 = MathHelper.func_76126_a(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        Vec3 vec31 = vec3.func_72441_c((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        MovingObjectPosition movingobjectposition = world.func_72901_a(vec3, vec31, true);

        if (movingobjectposition == null) {
            return itemstack;
        } else {
            Vec3 vec32 = entityplayer.func_70676_i(f);
            boolean flag = false;
            float f9 = 1.0F;
            List list = world.func_72839_b(entityplayer, entityplayer.field_70121_D.func_72321_a(vec32.field_72450_a * d3, vec32.field_72448_b * d3, vec32.field_72449_c * d3).func_72314_b((double) f9, (double) f9, (double) f9));

            int i;

            for (i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity.func_70067_L()) {
                    float f10 = entity.func_70111_Y();
                    AxisAlignedBB axisalignedbb = entity.field_70121_D.func_72314_b((double) f10, (double) f10, (double) f10);

                    if (axisalignedbb.func_72318_a(vec3)) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                return itemstack;
            } else {
                if (movingobjectposition.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
                    i = movingobjectposition.field_72311_b;
                    int j = movingobjectposition.field_72312_c;
                    int k = movingobjectposition.field_72309_d;

                    if (world.func_147439_a(i, j, k) == Blocks.field_150431_aC) {
                        --j;
                    }

                    EntityBoat entityboat = new EntityBoat(world, (double) ((float) i + 0.5F), (double) ((float) j + 1.0F), (double) ((float) k + 0.5F));

                    entityboat.field_70177_z = (float) (((MathHelper.func_76128_c((double) (entityplayer.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);
                    if (!world.func_72945_a(entityboat, entityboat.field_70121_D.func_72314_b(-0.1D, -0.1D, -0.1D)).isEmpty()) {
                        return itemstack;
                    }

                    if (!world.field_72995_K) {
                        world.func_72838_d(entityboat);
                    }

                    if (!entityplayer.field_71075_bZ.field_75098_d) {
                        --itemstack.field_77994_a;
                    }
                }

                return itemstack;
            }
        }
    }
}
