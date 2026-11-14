package net.minecraft.entity;

public class DataWatcher$WatchableObject {

    private final int field_75678_a;
    private final int field_75676_b;
    private Object field_75677_c;
    private boolean field_75675_d;

    public DataWatcher$WatchableObject(int i, int j, Object object) {
        this.field_75676_b = j;
        this.field_75677_c = object;
        this.field_75678_a = i;
        this.field_75675_d = true;
    }

    public int func_75672_a() {
        return this.field_75676_b;
    }

    public void func_75673_a(Object object) {
        this.field_75677_c = object;
    }

    public Object func_75669_b() {
        return this.field_75677_c;
    }

    public int func_75674_c() {
        return this.field_75678_a;
    }

    public boolean func_75670_d() {
        return this.field_75675_d;
    }

    public void func_75671_a(boolean flag) {
        this.field_75675_d = flag;
    }
}
