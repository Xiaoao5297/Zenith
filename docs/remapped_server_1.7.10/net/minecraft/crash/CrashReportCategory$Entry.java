package net.minecraft.crash;

class CrashReportCategory$Entry {

    private final String field_85092_a;
    private final String field_85091_b;

    public CrashReportCategory$Entry(String s, Object object) {
        this.field_85092_a = s;
        if (object == null) {
            this.field_85091_b = "~~NULL~~";
        } else if (object instanceof Throwable) {
            Throwable throwable = (Throwable) object;

            this.field_85091_b = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
        } else {
            this.field_85091_b = object.toString();
        }

    }

    public String func_85089_a() {
        return this.field_85092_a;
    }

    public String func_85090_b() {
        return this.field_85091_b;
    }
}
