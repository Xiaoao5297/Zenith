package net.minecraft.village;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList {

    public MerchantRecipeList() {}

    public MerchantRecipeList(NBTTagCompound nbttagcompound) {
        this.func_77201_a(nbttagcompound);
    }

    public MerchantRecipe func_77203_a(ItemStack itemstack, ItemStack itemstack1, int i) {
        if (i > 0 && i < this.size()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) this.get(i);

            return itemstack.func_77973_b() == merchantrecipe.func_77394_a().func_77973_b() && (itemstack1 == null && !merchantrecipe.func_77398_c() || merchantrecipe.func_77398_c() && itemstack1 != null && merchantrecipe.func_77396_b().func_77973_b() == itemstack1.func_77973_b()) && itemstack.field_77994_a >= merchantrecipe.func_77394_a().field_77994_a && (!merchantrecipe.func_77398_c() || itemstack1.field_77994_a >= merchantrecipe.func_77396_b().field_77994_a) ? merchantrecipe : null;
        } else {
            for (int j = 0; j < this.size(); ++j) {
                MerchantRecipe merchantrecipe1 = (MerchantRecipe) this.get(j);

                if (itemstack.func_77973_b() == merchantrecipe1.func_77394_a().func_77973_b() && itemstack.field_77994_a >= merchantrecipe1.func_77394_a().field_77994_a && (!merchantrecipe1.func_77398_c() && itemstack1 == null || merchantrecipe1.func_77398_c() && itemstack1 != null && merchantrecipe1.func_77396_b().func_77973_b() == itemstack1.func_77973_b() && itemstack1.field_77994_a >= merchantrecipe1.func_77396_b().field_77994_a)) {
                    return merchantrecipe1;
                }
            }

            return null;
        }
    }

    public void func_77205_a(MerchantRecipe merchantrecipe) {
        for (int i = 0; i < this.size(); ++i) {
            MerchantRecipe merchantrecipe1 = (MerchantRecipe) this.get(i);

            if (merchantrecipe.func_77393_a(merchantrecipe1)) {
                if (merchantrecipe.func_77391_b(merchantrecipe1)) {
                    this.set(i, merchantrecipe);
                }

                return;
            }
        }

        this.add(merchantrecipe);
    }

    public void func_151391_a(PacketBuffer packetbuffer) {
        packetbuffer.writeByte((byte) (this.size() & 255));

        for (int i = 0; i < this.size(); ++i) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) this.get(i);

            packetbuffer.func_150788_a(merchantrecipe.func_77394_a());
            packetbuffer.func_150788_a(merchantrecipe.func_77397_d());
            ItemStack itemstack = merchantrecipe.func_77396_b();

            packetbuffer.writeBoolean(itemstack != null);
            if (itemstack != null) {
                packetbuffer.func_150788_a(itemstack);
            }

            packetbuffer.writeBoolean(merchantrecipe.func_82784_g());
        }

    }

    public void func_77201_a(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Recipes", 10);

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);

            this.add(new MerchantRecipe(nbttagcompound1));
        }

    }

    public NBTTagCompound func_77202_a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.size(); ++i) {
            MerchantRecipe merchantrecipe = (MerchantRecipe) this.get(i);

            nbttaglist.func_74742_a(merchantrecipe.func_77395_g());
        }

        nbttagcompound.func_74782_a("Recipes", nbttaglist);
        return nbttagcompound;
    }
}
