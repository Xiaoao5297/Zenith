package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.command.IEntitySelector$ArmoredMob;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

final class ItemArmor$1 extends BehaviorDefaultDispenseItem {

    ItemArmor$1() {}

    @Override
    protected ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        int i = iblocksource.func_82623_d() + enumfacing.func_82601_c();
        int j = iblocksource.func_82622_e() + enumfacing.func_96559_d();
        int k = iblocksource.func_82621_f() + enumfacing.func_82599_e();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.func_72330_a((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1));
        List list = iblocksource.func_82618_k().func_82733_a(EntityLivingBase.class, axisalignedbb, new IEntitySelector$ArmoredMob(itemstack));

        if (list.size() > 0) {
            EntityLivingBase entitylivingbase = (EntityLivingBase) list.get(0);
            int l = entitylivingbase instanceof EntityPlayer ? 1 : 0;
            int i1 = EntityLiving.func_82159_b(itemstack);
            ItemStack itemstack1 = itemstack.func_77946_l();

            itemstack1.field_77994_a = 1;
            entitylivingbase.func_70062_b(i1 - l, itemstack1);
            if (entitylivingbase instanceof EntityLiving) {
                ((EntityLiving) entitylivingbase).func_96120_a(i1, 2.0F);
            }

            --itemstack.field_77994_a;
            return itemstack;
        } else {
            return super.func_82487_b(iblocksource, itemstack);
        }
    }
}
