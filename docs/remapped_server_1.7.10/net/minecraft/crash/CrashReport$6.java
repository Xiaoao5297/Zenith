package net.minecraft.crash;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

class CrashReport$6 implements Callable {

    CrashReport$6(CrashReport crashreport) {
        this.field_71488_a = crashreport;
    }

    public String call() {
        RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
        List list = runtimemxbean.getInputArguments();
        int i = 0;
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (s.startsWith("-X")) {
                if (i++ > 0) {
                    stringbuilder.append(" ");
                }

                stringbuilder.append(s);
            }
        }

        return String.format("%d total; %s", i, stringbuilder.toString());
    }
}
