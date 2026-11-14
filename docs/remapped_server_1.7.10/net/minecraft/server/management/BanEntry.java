package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BanEntry extends UserListEntry {

    public static final SimpleDateFormat field_73698_a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    protected final Date field_73694_d;
    protected final String field_73695_e;
    protected final Date field_73692_f;
    protected final String field_73693_g;

    public BanEntry(Object object, Date date, String s, Date date1, String s1) {
        super(object);
        this.field_73694_d = date == null ? new Date() : date;
        this.field_73695_e = s == null ? "(Unknown)" : s;
        this.field_73692_f = date1;
        this.field_73693_g = s1 == null ? "Banned by an operator." : s1;
    }

    protected BanEntry(Object object, JsonObject jsonobject) {
        super(object, jsonobject);

        Date date;

        try {
            date = jsonobject.has("created") ? BanEntry.field_73698_a.parse(jsonobject.get("created").getAsString()) : new Date();
        } catch (ParseException parseexception) {
            date = new Date();
        }

        this.field_73694_d = date;
        this.field_73695_e = jsonobject.has("source") ? jsonobject.get("source").getAsString() : "(Unknown)";

        Date date1;

        try {
            date1 = jsonobject.has("expires") ? BanEntry.field_73698_a.parse(jsonobject.get("expires").getAsString()) : null;
        } catch (ParseException parseexception1) {
            date1 = null;
        }

        this.field_73692_f = date1;
        this.field_73693_g = jsonobject.has("reason") ? jsonobject.get("reason").getAsString() : "Banned by an operator.";
    }

    public Date func_73680_d() {
        return this.field_73692_f;
    }

    public String func_73686_f() {
        return this.field_73693_g;
    }

    @Override
    boolean func_73682_e() {
        return this.field_73692_f == null ? false : this.field_73692_f.before(new Date());
    }

    @Override
    protected void func_152641_a(JsonObject jsonobject) {
        jsonobject.addProperty("created", BanEntry.field_73698_a.format(this.field_73694_d));
        jsonobject.addProperty("source", this.field_73695_e);
        jsonobject.addProperty("expires", this.field_73692_f == null ? "forever" : BanEntry.field_73698_a.format(this.field_73692_f));
        jsonobject.addProperty("reason", this.field_73693_g);
    }
}
