package net.minecraft.entity.item;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPainting extends EntityHanging {

    public EntityPainting$EnumArt field_70522_e;

    public EntityPainting(World world) {
        super(world);
    }

    public EntityPainting(World world, int i, int j, int k, int l) {
        super(world, i, j, k, l);
        ArrayList arraylist = new ArrayList();
        EntityPainting$EnumArt[] aentitypainting$enumart = EntityPainting$EnumArt.values();
        int i1 = aentitypainting$enumart.length;

        for (int j1 = 0; j1 < i1; ++j1) {
            EntityPainting$EnumArt entitypainting$enumart = aentitypainting$enumart[j1];

            this.field_70522_e = entitypainting$enumart;
            this.func_82328_a(l);
            if (this.func_70518_d()) {
                arraylist.add(entitypainting$enumart);
            }
        }

        if (!arraylist.isEmpty()) {
            this.field_70522_e = (EntityPainting$EnumArt) arraylist.get(this.field_70146_Z.nextInt(arraylist.size()));
        }

        this.func_82328_a(l);
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74778_a("Motive", this.field_70522_e.field_75702_A);
        super.func_70014_b(nbttagcompound);
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        String s = nbttagcompound.func_74779_i("Motive");
        EntityPainting$EnumArt[] aentitypainting$enumart = EntityPainting$EnumArt.values();
        int i = aentitypainting$enumart.length;

        for (int j = 0; j < i; ++j) {
            EntityPainting$EnumArt entitypainting$enumart = aentitypainting$enumart[j];

            if (entitypainting$enumart.field_75702_A.equals(s)) {
                this.field_70522_e = entitypainting$enumart;
            }
        }

        if (this.field_70522_e == null) {
            this.field_70522_e = EntityPainting$EnumArt.Kebab;
        }

        super.func_70037_a(nbttagcompound);
    }

    @Override
    public int func_82329_d() {
        return this.field_70522_e.field_75703_B;
    }

    @Override
    public int func_82330_g() {
        return this.field_70522_e.field_75704_C;
    }

    @Override
    public void func_110128_b(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            if (entityplayer.field_71075_bZ.field_75098_d) {
                return;
            }
        }

        this.func_70099_a(new ItemStack(Items.field_151159_an), 0.0F);
    }
}
