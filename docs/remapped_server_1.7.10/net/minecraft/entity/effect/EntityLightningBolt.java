package net.minecraft.entity.effect;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityLightningBolt extends EntityWeatherEffect {

    private int field_70262_b;
    public long field_70264_a;
    private int field_70263_c;

    public EntityLightningBolt(World world, double d0, double d1, double d2) {
        super(world);
        this.func_70012_b(d0, d1, d2, 0.0F, 0.0F);
        this.field_70262_b = 2;
        this.field_70264_a = this.field_70146_Z.nextLong();
        this.field_70263_c = this.field_70146_Z.nextInt(3) + 1;
        if (!world.field_72995_K && world.func_82736_K().func_82766_b("doFireTick") && (world.field_73013_u == EnumDifficulty.NORMAL || world.field_73013_u == EnumDifficulty.HARD) && world.func_72873_a(MathHelper.func_76128_c(d0), MathHelper.func_76128_c(d1), MathHelper.func_76128_c(d2), 10)) {
            int i = MathHelper.func_76128_c(d0);
            int j = MathHelper.func_76128_c(d1);
            int k = MathHelper.func_76128_c(d2);

            if (world.func_147439_a(i, j, k).func_149688_o() == Material.field_151579_a && Blocks.field_150480_ab.func_149742_c(world, i, j, k)) {
                world.func_147449_b(i, j, k, Blocks.field_150480_ab);
            }

            for (i = 0; i < 4; ++i) {
                j = MathHelper.func_76128_c(d0) + this.field_70146_Z.nextInt(3) - 1;
                k = MathHelper.func_76128_c(d1) + this.field_70146_Z.nextInt(3) - 1;
                int l = MathHelper.func_76128_c(d2) + this.field_70146_Z.nextInt(3) - 1;

                if (world.func_147439_a(j, k, l).func_149688_o() == Material.field_151579_a && Blocks.field_150480_ab.func_149742_c(world, j, k, l)) {
                    world.func_147449_b(j, k, l, Blocks.field_150480_ab);
                }
            }
        }

    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_70262_b == 2) {
            this.field_70170_p.func_72908_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, "ambient.weather.thunder", 10000.0F, 0.8F + this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70170_p.func_72908_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, "random.explode", 2.0F, 0.5F + this.field_70146_Z.nextFloat() * 0.2F);
        }

        --this.field_70262_b;
        if (this.field_70262_b < 0) {
            if (this.field_70263_c == 0) {
                this.func_70106_y();
            } else if (this.field_70262_b < -this.field_70146_Z.nextInt(10)) {
                --this.field_70263_c;
                this.field_70262_b = 1;
                this.field_70264_a = this.field_70146_Z.nextLong();
                if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_82736_K().func_82766_b("doFireTick") && this.field_70170_p.func_72873_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v), 10)) {
                    int i = MathHelper.func_76128_c(this.field_70165_t);
                    int j = MathHelper.func_76128_c(this.field_70163_u);
                    int k = MathHelper.func_76128_c(this.field_70161_v);

                    if (this.field_70170_p.func_147439_a(i, j, k).func_149688_o() == Material.field_151579_a && Blocks.field_150480_ab.func_149742_c(this.field_70170_p, i, j, k)) {
                        this.field_70170_p.func_147449_b(i, j, k, Blocks.field_150480_ab);
                    }
                }
            }
        }

        if (this.field_70262_b >= 0) {
            if (this.field_70170_p.field_72995_K) {
                this.field_70170_p.field_73016_r = 2;
            } else {
                double d0 = 3.0D;
                List list = this.field_70170_p.func_72839_b(this, AxisAlignedBB.func_72330_a(this.field_70165_t - d0, this.field_70163_u - d0, this.field_70161_v - d0, this.field_70165_t + d0, this.field_70163_u + 6.0D + d0, this.field_70161_v + d0));

                for (int l = 0; l < list.size(); ++l) {
                    Entity entity = (Entity) list.get(l);

                    entity.func_70077_a(this);
                }
            }
        }

    }

    @Override
    protected void func_70088_a() {}

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {}

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {}
}
