package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase {

    protected ItemEmptyMap() {
        this.func_77637_a(CreativeTabs.field_78026_f);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        ItemStack itemstack1 = new ItemStack(Items.field_151098_aY, 1, world.func_72841_b("map"));
        String s = "map_" + itemstack1.func_77960_j();
        MapData mapdata = new MapData(s);

        world.func_72823_a(s, mapdata);
        mapdata.field_76197_d = 0;
        int i = 128 * (1 << mapdata.field_76197_d);

        mapdata.field_76201_a = (int) (Math.round(entityplayer.field_70165_t / (double) i) * (long) i);
        mapdata.field_76199_b = (int) (Math.round(entityplayer.field_70161_v / (double) i) * (long) i);
        mapdata.field_76200_c = (byte) world.field_73011_w.field_76574_g;
        mapdata.func_76185_a();
        --itemstack.field_77994_a;
        if (itemstack.field_77994_a <= 0) {
            return itemstack1;
        } else {
            if (!entityplayer.field_71071_by.func_70441_a(itemstack1.func_77946_l())) {
                entityplayer.func_71019_a(itemstack1, false);
            }

            return itemstack;
        }
    }
}
