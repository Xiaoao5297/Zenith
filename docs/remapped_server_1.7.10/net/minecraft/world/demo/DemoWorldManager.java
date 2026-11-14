package net.minecraft.world.demo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class DemoWorldManager extends ItemInWorldManager {

    private boolean field_73105_c;
    private boolean field_73103_d;
    private int field_73104_e;
    private int field_73102_f;

    public DemoWorldManager(World world) {
        super(world);
    }

    @Override
    public void func_73075_a() {
        super.func_73075_a();
        ++this.field_73102_f;
        long i = this.field_73092_a.func_82737_E();
        long j = i / 24000L + 1L;

        if (!this.field_73105_c && this.field_73102_f > 20) {
            this.field_73105_c = true;
            this.field_73090_b.field_71135_a.func_147359_a(new S2BPacketChangeGameState(5, 0.0F));
        }

        this.field_73103_d = i > 120500L;
        if (this.field_73103_d) {
            ++this.field_73104_e;
        }

        if (i % 24000L == 500L) {
            if (j <= 6L) {
                this.field_73090_b.func_145747_a(new ChatComponentTranslation("demo.day." + j, new Object[0]));
            }
        } else if (j == 1L) {
            if (i == 100L) {
                this.field_73090_b.field_71135_a.func_147359_a(new S2BPacketChangeGameState(5, 101.0F));
            } else if (i == 175L) {
                this.field_73090_b.field_71135_a.func_147359_a(new S2BPacketChangeGameState(5, 102.0F));
            } else if (i == 250L) {
                this.field_73090_b.field_71135_a.func_147359_a(new S2BPacketChangeGameState(5, 103.0F));
            }
        } else if (j == 5L && i % 24000L == 22000L) {
            this.field_73090_b.func_145747_a(new ChatComponentTranslation("demo.day.warning", new Object[0]));
        }

    }

    private void func_73101_e() {
        if (this.field_73104_e > 100) {
            this.field_73090_b.func_145747_a(new ChatComponentTranslation("demo.reminder", new Object[0]));
            this.field_73104_e = 0;
        }

    }

    @Override
    public void func_73074_a(int i, int j, int k, int l) {
        if (this.field_73103_d) {
            this.func_73101_e();
        } else {
            super.func_73074_a(i, j, k, l);
        }
    }

    @Override
    public void func_73082_a(int i, int j, int k) {
        if (!this.field_73103_d) {
            super.func_73082_a(i, j, k);
        }
    }

    @Override
    public boolean func_73084_b(int i, int j, int k) {
        return this.field_73103_d ? false : super.func_73084_b(i, j, k);
    }

    @Override
    public boolean func_73085_a(EntityPlayer entityplayer, World world, ItemStack itemstack) {
        if (this.field_73103_d) {
            this.func_73101_e();
            return false;
        } else {
            return super.func_73085_a(entityplayer, world, itemstack);
        }
    }

    @Override
    public boolean func_73078_a(EntityPlayer entityplayer, World world, ItemStack itemstack, int i, int j, int k, int l, float f, float f1, float f2) {
        if (this.field_73103_d) {
            this.func_73101_e();
            return false;
        } else {
            return super.func_73078_a(entityplayer, world, itemstack, i, j, k, l, f, f1, f2);
        }
    }
}
