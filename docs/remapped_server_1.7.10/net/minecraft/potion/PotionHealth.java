package net.minecraft.potion;

public class PotionHealth extends Potion {

    public PotionHealth(int i, boolean flag, int j) {
        super(i, flag, j);
    }

    @Override
    public boolean func_76403_b() {
        return true;
    }

    @Override
    public boolean func_76397_a(int i, int j) {
        return i >= 1;
    }
}
