package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;

public enum ItemFishFood$FishType {

    COD(0, "cod", 2, 0.1F, 5, 0.6F), SALMON(1, "salmon", 2, 0.1F, 6, 0.8F), CLOWNFISH(2, "clownfish", 1, 0.1F), PUFFERFISH(3, "pufferfish", 1, 0.1F);

    private static final Map field_150983_e = Maps.newHashMap();
    private final int field_150980_f;
    private final String field_150981_g;
    private final int field_150991_j;
    private final float field_150992_k;
    private final int field_150989_l;
    private final float field_150990_m;
    private boolean field_150987_n = false;

    private ItemFishFood$FishType(int i, String s, int j, float f, int k, float f1) {
        this.field_150980_f = i;
        this.field_150981_g = s;
        this.field_150991_j = j;
        this.field_150992_k = f;
        this.field_150989_l = k;
        this.field_150990_m = f1;
        this.field_150987_n = true;
    }

    private ItemFishFood$FishType(int i, String s, int j, float f) {
        this.field_150980_f = i;
        this.field_150981_g = s;
        this.field_150991_j = j;
        this.field_150992_k = f;
        this.field_150989_l = 0;
        this.field_150990_m = 0.0F;
        this.field_150987_n = false;
    }

    public int func_150976_a() {
        return this.field_150980_f;
    }

    public String func_150972_b() {
        return this.field_150981_g;
    }

    public int func_150975_c() {
        return this.field_150991_j;
    }

    public float func_150967_d() {
        return this.field_150992_k;
    }

    public int func_150970_e() {
        return this.field_150989_l;
    }

    public float func_150977_f() {
        return this.field_150990_m;
    }

    public boolean func_150973_i() {
        return this.field_150987_n;
    }

    public static ItemFishFood$FishType func_150974_a(int i) {
        ItemFishFood$FishType itemfishfood$fishtype = (ItemFishFood$FishType) ItemFishFood$FishType.field_150983_e.get(i);

        return itemfishfood$fishtype == null ? ItemFishFood$FishType.COD : itemfishfood$fishtype;
    }

    public static ItemFishFood$FishType func_150978_a(ItemStack itemstack) {
        return itemstack.func_77973_b() instanceof ItemFishFood ? func_150974_a(itemstack.func_77960_j()) : ItemFishFood$FishType.COD;
    }

    static {
        ItemFishFood$FishType[] aitemfishfood$fishtype = values();
        int i = aitemfishfood$fishtype.length;

        for (int j = 0; j < i; ++j) {
            ItemFishFood$FishType itemfishfood$fishtype = aitemfishfood$fishtype[j];

            ItemFishFood$FishType.field_150983_e.put(itemfishfood$fishtype.func_150976_a(), itemfishfood$fishtype);
        }

    }
}
