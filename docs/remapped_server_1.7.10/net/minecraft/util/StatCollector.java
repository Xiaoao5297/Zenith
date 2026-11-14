package net.minecraft.util;

public class StatCollector {

    private static StringTranslate field_74839_a = StringTranslate.func_74808_a();
    private static StringTranslate field_150828_b = new StringTranslate();

    public static String func_74838_a(String s) {
        return StatCollector.field_74839_a.func_74805_b(s);
    }

    public static String func_74837_a(String s, Object... aobject) {
        return StatCollector.field_74839_a.func_74803_a(s, aobject);
    }

    public static String func_150826_b(String s) {
        return StatCollector.field_150828_b.func_74805_b(s);
    }

    public static boolean func_94522_b(String s) {
        return StatCollector.field_74839_a.func_94520_b(s);
    }

    public static long func_150827_a() {
        return StatCollector.field_74839_a.func_150510_c();
    }
}
