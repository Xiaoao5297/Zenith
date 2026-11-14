package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.BlockSkull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSkull extends Item {

    private static final String[] field_82807_a = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};
    public static final String[] field_94587_a = new String[]{"skeleton", "wither", "zombie", "steve", "creeper"};

    public ItemSkull() {
        this.func_77637_a(CreativeTabs.field_78031_c);
        this.func_77656_e(0);
        this.func_77627_a(true);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (l == 0) {
            return false;
        } else if (!world.func_147439_a(i, j, k).func_149688_o().func_76220_a()) {
            return false;
        } else {
            if (l == 1) {
                ++j;
            }

            if (l == 2) {
                --k;
            }

            if (l == 3) {
                ++k;
            }

            if (l == 4) {
                --i;
            }

            if (l == 5) {
                ++i;
            }

            if (!world.field_72995_K) {
                world.func_147465_d(i, j, k, Blocks.field_150465_bP, l, 2);
                int i1 = 0;

                if (l == 1) {
                    i1 = MathHelper.func_76128_c((double) (entityplayer.field_70177_z * 16.0F / 360.0F) + 0.5D) & 15;
                }

                TileEntity tileentity = world.func_147438_o(i, j, k);

                if (tileentity != null && tileentity instanceof TileEntitySkull) {
                    if (itemstack.func_77960_j() == 3) {
                        GameProfile gameprofile = null;

                        if (itemstack.func_77942_o()) {
                            NBTTagCompound nbttagcompound = itemstack.func_77978_p();

                            if (nbttagcompound.func_150297_b("SkullOwner", 10)) {
                                gameprofile = NBTUtil.func_152459_a(nbttagcompound.func_74775_l("SkullOwner"));
                            } else if (nbttagcompound.func_150297_b("SkullOwner", 8) && nbttagcompound.func_74779_i("SkullOwner").length() > 0) {
                                gameprofile = new GameProfile((UUID) null, nbttagcompound.func_74779_i("SkullOwner"));
                            }
                        }

                        ((TileEntitySkull) tileentity).func_152106_a(gameprofile);
                    } else {
                        ((TileEntitySkull) tileentity).func_152107_a(itemstack.func_77960_j());
                    }

                    ((TileEntitySkull) tileentity).func_145903_a(i1);
                    ((BlockSkull) Blocks.field_150465_bP).func_149965_a(world, i, j, k, (TileEntitySkull) tileentity);
                }

                --itemstack.field_77994_a;
            }

            return true;
        }
    }

    @Override
    public int func_77647_b(int i) {
        return i;
    }

    @Override
    public String func_77667_c(ItemStack itemstack) {
        int i = itemstack.func_77960_j();

        if (i < 0 || i >= ItemSkull.field_82807_a.length) {
            i = 0;
        }

        return super.func_77658_a() + "." + ItemSkull.field_82807_a[i];
    }

    @Override
    public String func_77653_i(ItemStack itemstack) {
        if (itemstack.func_77960_j() == 3 && itemstack.func_77942_o()) {
            if (itemstack.func_77978_p().func_150297_b("SkullOwner", 10)) {
                return StatCollector.func_74837_a("item.skull.player.name", NBTUtil.func_152459_a(itemstack.func_77978_p().func_74775_l("SkullOwner")).getName());
            }

            if (itemstack.func_77978_p().func_150297_b("SkullOwner", 8)) {
                return StatCollector.func_74837_a("item.skull.player.name", itemstack.func_77978_p().func_74779_i("SkullOwner"));
            }
        }

        return super.func_77653_i(itemstack);
    }
}
