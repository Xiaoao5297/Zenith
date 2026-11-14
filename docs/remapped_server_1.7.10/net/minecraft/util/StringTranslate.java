package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate {

    private static final Pattern field_111053_a = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Splitter field_135065_b = Splitter.on('=').limit(2);
    private static StringTranslate field_74817_a = new StringTranslate();
    private final Map field_74816_c = Maps.newHashMap();
    private long field_150511_e;

    public StringTranslate() {
        try {
            InputStream inputstream = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");
            Iterator iterator = IOUtils.readLines(inputstream, Charsets.UTF_8).iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                if (!s.isEmpty() && s.charAt(0) != '#') {
                    String[] astring = (String[]) Iterables.toArray(StringTranslate.field_135065_b.split(s), String.class);

                    if (astring != null && astring.length == 2) {
                        String s1 = astring[0];
                        String s2 = StringTranslate.field_111053_a.matcher(astring[1]).replaceAll("%$1s");

                        this.field_74816_c.put(s1, s2);
                    }
                }
            }

            this.field_150511_e = System.currentTimeMillis();
        } catch (IOException ioexception) {
            ;
        }

    }

    static StringTranslate func_74808_a() {
        return StringTranslate.field_74817_a;
    }

    public synchronized String func_74805_b(String s) {
        return this.func_135064_c(s);
    }

    public synchronized String func_74803_a(String s, Object... aobject) {
        String s1 = this.func_135064_c(s);

        try {
            return String.format(s1, aobject);
        } catch (IllegalFormatException illegalformatexception) {
            return "Format error: " + s1;
        }
    }

    private String func_135064_c(String s) {
        String s1 = (String) this.field_74816_c.get(s);

        return s1 == null ? s : s1;
    }

    public synchronized boolean func_94520_b(String s) {
        return this.field_74816_c.containsKey(s);
    }

    public long func_150510_c() {
        return this.field_150511_e;
    }
}
