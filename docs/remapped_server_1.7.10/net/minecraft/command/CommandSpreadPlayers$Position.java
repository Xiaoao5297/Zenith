package net.minecraft.command;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

class CommandSpreadPlayers$Position {

    double field_111101_a;
    double field_111100_b;

    CommandSpreadPlayers$Position() {}

    CommandSpreadPlayers$Position(double d0, double d1) {
        this.field_111101_a = d0;
        this.field_111100_b = d1;
    }

    double func_111099_a(CommandSpreadPlayers$Position commandspreadplayers$position) {
        double d0 = this.field_111101_a - commandspreadplayers$position.field_111101_a;
        double d1 = this.field_111100_b - commandspreadplayers$position.field_111100_b;

        return Math.sqrt(d0 * d0 + d1 * d1);
    }

    void func_111095_a() {
        double d0 = (double) this.func_111096_b();

        this.field_111101_a /= d0;
        this.field_111100_b /= d0;
    }

    float func_111096_b() {
        return MathHelper.func_76133_a(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
    }

    public void func_111094_b(CommandSpreadPlayers$Position commandspreadplayers$position) {
        this.field_111101_a -= commandspreadplayers$position.field_111101_a;
        this.field_111100_b -= commandspreadplayers$position.field_111100_b;
    }

    public boolean func_111093_a(double d0, double d1, double d2, double d3) {
        boolean flag = false;

        if (this.field_111101_a < d0) {
            this.field_111101_a = d0;
            flag = true;
        } else if (this.field_111101_a > d2) {
            this.field_111101_a = d2;
            flag = true;
        }

        if (this.field_111100_b < d1) {
            this.field_111100_b = d1;
            flag = true;
        } else if (this.field_111100_b > d3) {
            this.field_111100_b = d3;
            flag = true;
        }

        return flag;
    }

    public int func_111092_a(World world) {
        int i = MathHelper.func_76128_c(this.field_111101_a);
        int j = MathHelper.func_76128_c(this.field_111100_b);

        for (int k = 256; k > 0; --k) {
            if (world.func_147439_a(i, k, j).func_149688_o() != Material.field_151579_a) {
                return k + 1;
            }
        }

        return 257;
    }

    public boolean func_111098_b(World world) {
        int i = MathHelper.func_76128_c(this.field_111101_a);
        int j = MathHelper.func_76128_c(this.field_111100_b);
        short short0 = 256;

        if (short0 <= 0) {
            return false;
        } else {
            Material material = world.func_147439_a(i, short0, j).func_149688_o();

            return !material.func_76224_d() && material != Material.field_151581_o;
        }
    }

    public void func_111097_a(Random random, double d0, double d1, double d2, double d3) {
        this.field_111101_a = MathHelper.func_82716_a(random, d0, d2);
        this.field_111100_b = MathHelper.func_82716_a(random, d1, d3);
    }
}
