package net.minecraft.world;

class GameRules$Value {

    private String field_82762_a;
    private boolean field_82760_b;
    private int field_82761_c;
    private double field_82759_d;

    public GameRules$Value(String s) {
        this.func_82757_a(s);
    }

    public void func_82757_a(String s) {
        this.field_82762_a = s;
        this.field_82760_b = Boolean.parseBoolean(s);

        try {
            this.field_82761_c = Integer.parseInt(s);
        } catch (NumberFormatException numberformatexception) {
            ;
        }

        try {
            this.field_82759_d = Double.parseDouble(s);
        } catch (NumberFormatException numberformatexception1) {
            ;
        }

    }

    public String func_82756_a() {
        return this.field_82762_a;
    }

    public boolean func_82758_b() {
        return this.field_82760_b;
    }
}
