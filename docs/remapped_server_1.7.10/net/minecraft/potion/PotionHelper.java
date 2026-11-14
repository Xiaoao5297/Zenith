package net.minecraft.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PotionHelper {

    public static final String field_77924_a = null;
    public static final String field_77922_b;
    public static final String field_77923_c = "+0-1-2-3&4-4+13";
    public static final String field_77920_d;
    public static final String field_77921_e;
    public static final String field_77918_f;
    public static final String field_77919_g;
    public static final String field_77931_h;
    public static final String field_77932_i;
    public static final String field_77929_j;
    public static final String field_77930_k;
    public static final String field_82818_l;
    public static final String field_151423_m;
    private static final HashMap field_77927_l = new HashMap();
    private static final HashMap field_77928_m = new HashMap();
    private static final HashMap field_77925_n;
    private static final String[] field_77926_o;

    public static boolean func_77914_a(int i, int j) {
        return (i & 1 << j) != 0;
    }

    private static int func_77910_c(int i, int j) {
        return func_77914_a(i, j) ? 1 : 0;
    }

    private static int func_77916_d(int i, int j) {
        return func_77914_a(i, j) ? 0 : 1;
    }

    public static int func_77909_a(int i) {
        return func_77908_a(i, 5, 4, 3, 2, 1);
    }

    public static int func_77911_a(Collection collection) {
        int i = 3694022;

        if (collection != null && !collection.isEmpty()) {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                PotionEffect potioneffect = (PotionEffect) iterator.next();
                int j = Potion.field_76425_a[potioneffect.func_76456_a()].func_76401_j();

                for (int k = 0; k <= potioneffect.func_76458_c(); ++k) {
                    f += (float) (j >> 16 & 255) / 255.0F;
                    f1 += (float) (j >> 8 & 255) / 255.0F;
                    f2 += (float) (j >> 0 & 255) / 255.0F;
                    ++f3;
                }
            }

            f = f / f3 * 255.0F;
            f1 = f1 / f3 * 255.0F;
            f2 = f2 / f3 * 255.0F;
            return (int) f << 16 | (int) f1 << 8 | (int) f2;
        } else {
            return i;
        }
    }

    public static boolean func_82817_b(Collection collection) {
        Iterator iterator = collection.iterator();

        PotionEffect potioneffect;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            potioneffect = (PotionEffect) iterator.next();
        } while (potioneffect.func_82720_e());

        return false;
    }

    public static String func_77905_c(int i) {
        int j = func_77909_a(i);

        return PotionHelper.field_77926_o[j];
    }

    private static int func_77904_a(boolean flag, boolean flag1, boolean flag2, int i, int j, int k, int l) {
        int i1 = 0;

        if (flag) {
            i1 = func_77916_d(l, j);
        } else if (i != -1) {
            if (i == 0 && func_77907_h(l) == j) {
                i1 = 1;
            } else if (i == 1 && func_77907_h(l) > j) {
                i1 = 1;
            } else if (i == 2 && func_77907_h(l) < j) {
                i1 = 1;
            }
        } else {
            i1 = func_77910_c(l, j);
        }

        if (flag1) {
            i1 *= k;
        }

        if (flag2) {
            i1 *= -1;
        }

        return i1;
    }

    private static int func_77907_h(int i) {
        int j;

        for (j = 0; i > 0; ++j) {
            i &= i - 1;
        }

        return j;
    }

    private static int func_77912_a(String s, int i, int j, int k) {
        if (i < s.length() && j >= 0 && i < j) {
            int l = s.indexOf(124, i);
            int i1;
            int j1;

            if (l >= 0 && l < j) {
                i1 = func_77912_a(s, i, l - 1, k);
                if (i1 > 0) {
                    return i1;
                } else {
                    j1 = func_77912_a(s, l + 1, j, k);
                    return j1 > 0 ? j1 : 0;
                }
            } else {
                i1 = s.indexOf(38, i);
                if (i1 >= 0 && i1 < j) {
                    j1 = func_77912_a(s, i, i1 - 1, k);
                    if (j1 <= 0) {
                        return 0;
                    } else {
                        int k1 = func_77912_a(s, i1 + 1, j, k);

                        return k1 <= 0 ? 0 : (j1 > k1 ? j1 : k1);
                    }
                } else {
                    boolean flag = false;
                    boolean flag1 = false;
                    boolean flag2 = false;
                    boolean flag3 = false;
                    boolean flag4 = false;
                    byte b0 = -1;
                    int l1 = 0;
                    int i2 = 0;
                    int j2 = 0;

                    for (int k2 = i; k2 < j; ++k2) {
                        char c0 = s.charAt(k2);

                        if (c0 >= '0' && c0 <= '9') {
                            if (flag) {
                                i2 = c0 - 48;
                                flag1 = true;
                            } else {
                                l1 *= 10;
                                l1 += c0 - 48;
                                flag2 = true;
                            }
                        } else if (c0 == '*') {
                            flag = true;
                        } else if (c0 == '!') {
                            if (flag2) {
                                j2 += func_77904_a(flag3, flag1, flag4, b0, l1, i2, k);
                                flag3 = false;
                                flag4 = false;
                                flag = false;
                                flag1 = false;
                                flag2 = false;
                                i2 = 0;
                                l1 = 0;
                                b0 = -1;
                            }

                            flag3 = true;
                        } else if (c0 == '-') {
                            if (flag2) {
                                j2 += func_77904_a(flag3, flag1, flag4, b0, l1, i2, k);
                                flag3 = false;
                                flag4 = false;
                                flag = false;
                                flag1 = false;
                                flag2 = false;
                                i2 = 0;
                                l1 = 0;
                                b0 = -1;
                            }

                            flag4 = true;
                        } else if (c0 != '=' && c0 != '<' && c0 != '>') {
                            if (c0 == '+' && flag2) {
                                j2 += func_77904_a(flag3, flag1, flag4, b0, l1, i2, k);
                                flag3 = false;
                                flag4 = false;
                                flag = false;
                                flag1 = false;
                                flag2 = false;
                                i2 = 0;
                                l1 = 0;
                                b0 = -1;
                            }
                        } else {
                            if (flag2) {
                                j2 += func_77904_a(flag3, flag1, flag4, b0, l1, i2, k);
                                flag3 = false;
                                flag4 = false;
                                flag = false;
                                flag1 = false;
                                flag2 = false;
                                i2 = 0;
                                l1 = 0;
                                b0 = -1;
                            }

                            if (c0 == '=') {
                                b0 = 0;
                            } else if (c0 == '<') {
                                b0 = 2;
                            } else if (c0 == '>') {
                                b0 = 1;
                            }
                        }
                    }

                    if (flag2) {
                        j2 += func_77904_a(flag3, flag1, flag4, b0, l1, i2, k);
                    }

                    return j2;
                }
            }
        } else {
            return 0;
        }
    }

    public static List func_77917_b(int i, boolean flag) {
        ArrayList arraylist = null;
        Potion[] apotion = Potion.field_76425_a;
        int j = apotion.length;

        for (int k = 0; k < j; ++k) {
            Potion potion = apotion[k];

            if (potion != null && (!potion.func_76395_i() || flag)) {
                String s = (String) PotionHelper.field_77927_l.get(potion.func_76396_c());

                if (s != null) {
                    int l = func_77912_a(s, 0, s.length(), i);

                    if (l > 0) {
                        int i1 = 0;
                        String s1 = (String) PotionHelper.field_77928_m.get(potion.func_76396_c());

                        if (s1 != null) {
                            i1 = func_77912_a(s1, 0, s1.length(), i);
                            if (i1 < 0) {
                                i1 = 0;
                            }
                        }

                        if (potion.func_76403_b()) {
                            l = 1;
                        } else {
                            l = 1200 * (l * 3 + (l - 1) * 2);
                            l >>= i1;
                            l = (int) Math.round((double) l * potion.func_76388_g());
                            if ((i & 16384) != 0) {
                                l = (int) Math.round((double) l * 0.75D + 0.5D);
                            }
                        }

                        if (arraylist == null) {
                            arraylist = new ArrayList();
                        }

                        PotionEffect potioneffect = new PotionEffect(potion.func_76396_c(), l, i1);

                        if ((i & 16384) != 0) {
                            potioneffect.func_82721_a(true);
                        }

                        arraylist.add(potioneffect);
                    }
                }
            }
        }

        return arraylist;
    }

    private static int func_77906_a(int i, int j, boolean flag, boolean flag1, boolean flag2) {
        if (flag2) {
            if (!func_77914_a(i, j)) {
                return 0;
            }
        } else if (flag) {
            i &= ~(1 << j);
        } else if (flag1) {
            if ((i & 1 << j) == 0) {
                i |= 1 << j;
            } else {
                i &= ~(1 << j);
            }
        } else {
            i |= 1 << j;
        }

        return i;
    }

    public static int func_77913_a(int i, String s) {
        byte b0 = 0;
        int j = s.length();
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        int k = 0;

        for (int l = b0; l < j; ++l) {
            char c0 = s.charAt(l);

            if (c0 >= '0' && c0 <= '9') {
                k *= 10;
                k += c0 - 48;
                flag = true;
            } else if (c0 == '!') {
                if (flag) {
                    i = func_77906_a(i, k, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    k = 0;
                }

                flag1 = true;
            } else if (c0 == '-') {
                if (flag) {
                    i = func_77906_a(i, k, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    k = 0;
                }

                flag2 = true;
            } else if (c0 == '+') {
                if (flag) {
                    i = func_77906_a(i, k, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    k = 0;
                }
            } else if (c0 == '&') {
                if (flag) {
                    i = func_77906_a(i, k, flag2, flag1, flag3);
                    flag3 = false;
                    flag1 = false;
                    flag2 = false;
                    flag = false;
                    k = 0;
                }

                flag3 = true;
            }
        }

        if (flag) {
            i = func_77906_a(i, k, flag2, flag1, flag3);
        }

        return i & 32767;
    }

    public static int func_77908_a(int i, int j, int k, int l, int i1, int j1) {
        return (func_77914_a(i, j) ? 16 : 0) | (func_77914_a(i, k) ? 8 : 0) | (func_77914_a(i, l) ? 4 : 0) | (func_77914_a(i, i1) ? 2 : 0) | (func_77914_a(i, j1) ? 1 : 0);
    }

    static {
        PotionHelper.field_77927_l.put(Potion.field_76428_l.func_76396_c(), "0 & !1 & !2 & !3 & 0+6");
        field_77922_b = "-0+1-2-3&4-4+13";
        PotionHelper.field_77927_l.put(Potion.field_76424_c.func_76396_c(), "!0 & 1 & !2 & !3 & 1+6");
        field_77931_h = "+0+1-2-3&4-4+13";
        PotionHelper.field_77927_l.put(Potion.field_76426_n.func_76396_c(), "0 & 1 & !2 & !3 & 0+6");
        field_77918_f = "+0-1+2-3&4-4+13";
        PotionHelper.field_77927_l.put(Potion.field_76432_h.func_76396_c(), "0 & !1 & 2 & !3");
        field_77920_d = "-0-1+2-3&4-4+13";
        PotionHelper.field_77927_l.put(Potion.field_76436_u.func_76396_c(), "!0 & !1 & 2 & !3 & 2+6");
        field_77921_e = "-0+3-4+13";
        PotionHelper.field_77927_l.put(Potion.field_76437_t.func_76396_c(), "!0 & !1 & !2 & 3 & 3+6");
        PotionHelper.field_77927_l.put(Potion.field_76433_i.func_76396_c(), "!0 & !1 & 2 & 3");
        PotionHelper.field_77927_l.put(Potion.field_76421_d.func_76396_c(), "!0 & 1 & !2 & 3 & 3+6");
        field_77919_g = "+0-1-2+3&4-4+13";
        PotionHelper.field_77927_l.put(Potion.field_76420_g.func_76396_c(), "0 & !1 & !2 & 3 & 3+6");
        field_82818_l = "-0+1+2-3+13&4-4";
        PotionHelper.field_77927_l.put(Potion.field_76439_r.func_76396_c(), "!0 & 1 & 2 & !3 & 2+6");
        PotionHelper.field_77927_l.put(Potion.field_76441_p.func_76396_c(), "!0 & 1 & 2 & 3 & 2+6");
        field_151423_m = "+0-1+2+3+13&4-4";
        PotionHelper.field_77927_l.put(Potion.field_76427_o.func_76396_c(), "0 & !1 & 2 & 3 & 2+6");
        field_77929_j = "+5-6-7";
        PotionHelper.field_77928_m.put(Potion.field_76424_c.func_76396_c(), "5");
        PotionHelper.field_77928_m.put(Potion.field_76422_e.func_76396_c(), "5");
        PotionHelper.field_77928_m.put(Potion.field_76420_g.func_76396_c(), "5");
        PotionHelper.field_77928_m.put(Potion.field_76428_l.func_76396_c(), "5");
        PotionHelper.field_77928_m.put(Potion.field_76433_i.func_76396_c(), "5");
        PotionHelper.field_77928_m.put(Potion.field_76432_h.func_76396_c(), "5");
        PotionHelper.field_77928_m.put(Potion.field_76429_m.func_76396_c(), "5");
        PotionHelper.field_77928_m.put(Potion.field_76436_u.func_76396_c(), "5");
        field_77932_i = "-5+6-7";
        field_77930_k = "+14&13-13";
        field_77925_n = new HashMap();
        field_77926_o = new String[]{"potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat", "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky"};
    }
}
