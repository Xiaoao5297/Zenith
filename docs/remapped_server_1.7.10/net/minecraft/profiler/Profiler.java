package net.minecraft.profiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {

    private static final Logger field_151234_b = LogManager.getLogger();
    private final List field_76325_b = new ArrayList();
    private final List field_76326_c = new ArrayList();
    public boolean field_76327_a;
    private String field_76323_d = "";
    private final Map field_76324_e = new HashMap();

    public Profiler() {}

    public void func_76317_a() {
        this.field_76324_e.clear();
        this.field_76323_d = "";
        this.field_76325_b.clear();
    }

    public void func_76320_a(String s) {
        if (this.field_76327_a) {
            if (this.field_76323_d.length() > 0) {
                this.field_76323_d = this.field_76323_d + ".";
            }

            this.field_76323_d = this.field_76323_d + s;
            this.field_76325_b.add(this.field_76323_d);
            this.field_76326_c.add(System.nanoTime());
        }
    }

    public void func_76319_b() {
        if (this.field_76327_a) {
            long i = System.nanoTime();
            long j = (Long) this.field_76326_c.remove(this.field_76326_c.size() - 1);

            this.field_76325_b.remove(this.field_76325_b.size() - 1);
            long k = i - j;

            if (this.field_76324_e.containsKey(this.field_76323_d)) {
                this.field_76324_e.put(this.field_76323_d, (Long) this.field_76324_e.get(this.field_76323_d) + k);
            } else {
                this.field_76324_e.put(this.field_76323_d, k);
            }

            if (k > 100000000L) {
                Profiler.field_151234_b.warn("Something's taking too long! '" + this.field_76323_d + "' took aprox " + (double) k / 1000000.0D + " ms");
            }

            this.field_76323_d = !this.field_76325_b.isEmpty() ? (String) this.field_76325_b.get(this.field_76325_b.size() - 1) : "";
        }
    }

    public List func_76321_b(String s) {
        if (!this.field_76327_a) {
            return null;
        } else {
            long i = this.field_76324_e.containsKey("root") ? (Long) this.field_76324_e.get("root") : 0L;
            long j = this.field_76324_e.containsKey(s) ? (Long) this.field_76324_e.get(s) : -1L;
            ArrayList arraylist = new ArrayList();

            if (s.length() > 0) {
                s = s + ".";
            }

            long k = 0L;
            Iterator iterator = this.field_76324_e.keySet().iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();

                if (s1.length() > s.length() && s1.startsWith(s) && s1.indexOf(".", s.length() + 1) < 0) {
                    k += (Long) this.field_76324_e.get(s1);
                }
            }

            float f = (float) k;

            if (k < j) {
                k = j;
            }

            if (i < k) {
                i = k;
            }

            Iterator iterator1 = this.field_76324_e.keySet().iterator();

            String s2;

            while (iterator1.hasNext()) {
                s2 = (String) iterator1.next();
                if (s2.length() > s.length() && s2.startsWith(s) && s2.indexOf(".", s.length() + 1) < 0) {
                    long l = (Long) this.field_76324_e.get(s2);
                    double d0 = (double) l * 100.0D / (double) k;
                    double d1 = (double) l * 100.0D / (double) i;
                    String s3 = s2.substring(s.length());

                    arraylist.add(new Profiler$Result(s3, d0, d1));
                }
            }

            iterator1 = this.field_76324_e.keySet().iterator();

            while (iterator1.hasNext()) {
                s2 = (String) iterator1.next();
                this.field_76324_e.put(s2, (Long) this.field_76324_e.get(s2) * 999L / 1000L);
            }

            if ((float) k > f) {
                arraylist.add(new Profiler$Result("unspecified", (double) ((float) k - f) * 100.0D / (double) k, (double) ((float) k - f) * 100.0D / (double) i));
            }

            Collections.sort(arraylist);
            arraylist.add(0, new Profiler$Result(s, 100.0D, (double) k * 100.0D / (double) i));
            return arraylist;
        }
    }

    public void func_76318_c(String s) {
        this.func_76319_b();
        this.func_76320_a(s);
    }

    public String func_76322_c() {
        return this.field_76325_b.size() == 0 ? "[UNKNOWN]" : (String) this.field_76325_b.get(this.field_76325_b.size() - 1);
    }
}
