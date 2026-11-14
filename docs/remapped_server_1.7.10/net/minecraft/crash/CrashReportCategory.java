package net.minecraft.crash;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;

public class CrashReportCategory {

    private final CrashReport field_85078_a;
    private final String field_85076_b;
    private final List field_85077_c = new ArrayList();
    private StackTraceElement[] field_85075_d = new StackTraceElement[0];

    public CrashReportCategory(CrashReport crashreport, String s) {
        this.field_85078_a = crashreport;
        this.field_85076_b = s;
    }

    public static String func_85071_a(int i, int j, int k) {
        StringBuilder stringbuilder = new StringBuilder();

        try {
            stringbuilder.append(String.format("World: (%d,%d,%d)", i, j, k));
        } catch (Throwable throwable) {
            stringbuilder.append("(Error finding world loc)");
        }

        stringbuilder.append(", ");

        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        int k2;
        int l2;

        try {
            l = i >> 4;
            i1 = k >> 4;
            j1 = i & 15;
            k1 = j >> 4;
            l1 = k & 15;
            i2 = l << 4;
            j2 = i1 << 4;
            k2 = (l + 1 << 4) - 1;
            l2 = (i1 + 1 << 4) - 1;
            stringbuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", j1, k1, l1, l, i1, i2, j2, k2, l2));
        } catch (Throwable throwable1) {
            stringbuilder.append("(Error finding chunk loc)");
        }

        stringbuilder.append(", ");

        try {
            l = i >> 9;
            i1 = k >> 9;
            j1 = l << 5;
            k1 = i1 << 5;
            l1 = (l + 1 << 5) - 1;
            i2 = (i1 + 1 << 5) - 1;
            j2 = l << 9;
            k2 = i1 << 9;
            l2 = (l + 1 << 9) - 1;
            int i3 = (i1 + 1 << 9) - 1;

            stringbuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", l, i1, j1, k1, l1, i2, j2, k2, l2, i3));
        } catch (Throwable throwable2) {
            stringbuilder.append("(Error finding world loc)");
        }

        return stringbuilder.toString();
    }

    public void func_71500_a(String s, Callable callable) {
        try {
            this.func_71507_a(s, callable.call());
        } catch (Throwable throwable) {
            this.func_71499_a(s, throwable);
        }

    }

    public void func_71507_a(String s, Object object) {
        this.field_85077_c.add(new CrashReportCategory$Entry(s, object));
    }

    public void func_71499_a(String s, Throwable throwable) {
        this.func_71507_a(s, throwable);
    }

    public int func_85073_a(int i) {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();

        if (astacktraceelement.length <= 0) {
            return 0;
        } else {
            this.field_85075_d = new StackTraceElement[astacktraceelement.length - 3 - i];
            System.arraycopy(astacktraceelement, 3 + i, this.field_85075_d, 0, this.field_85075_d.length);
            return this.field_85075_d.length;
        }
    }

    public boolean func_85069_a(StackTraceElement stacktraceelement, StackTraceElement stacktraceelement1) {
        if (this.field_85075_d.length != 0 && stacktraceelement != null) {
            StackTraceElement stacktraceelement2 = this.field_85075_d[0];

            if (stacktraceelement2.isNativeMethod() == stacktraceelement.isNativeMethod() && stacktraceelement2.getClassName().equals(stacktraceelement.getClassName()) && stacktraceelement2.getFileName().equals(stacktraceelement.getFileName()) && stacktraceelement2.getMethodName().equals(stacktraceelement.getMethodName())) {
                if (stacktraceelement1 != null != this.field_85075_d.length > 1) {
                    return false;
                } else if (stacktraceelement1 != null && !this.field_85075_d[1].equals(stacktraceelement1)) {
                    return false;
                } else {
                    this.field_85075_d[0] = stacktraceelement;
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void func_85070_b(int i) {
        StackTraceElement[] astacktraceelement = new StackTraceElement[this.field_85075_d.length - i];

        System.arraycopy(this.field_85075_d, 0, astacktraceelement, 0, astacktraceelement.length);
        this.field_85075_d = astacktraceelement;
    }

    public void func_85072_a(StringBuilder stringbuilder) {
        stringbuilder.append("-- ").append(this.field_85076_b).append(" --\n");
        stringbuilder.append("Details:");
        Iterator iterator = this.field_85077_c.iterator();

        while (iterator.hasNext()) {
            CrashReportCategory$Entry crashreportcategory$entry = (CrashReportCategory$Entry) iterator.next();

            stringbuilder.append("\n\t");
            stringbuilder.append(crashreportcategory$entry.func_85089_a());
            stringbuilder.append(": ");
            stringbuilder.append(crashreportcategory$entry.func_85090_b());
        }

        if (this.field_85075_d != null && this.field_85075_d.length > 0) {
            stringbuilder.append("\nStacktrace:");
            StackTraceElement[] astacktraceelement = this.field_85075_d;
            int i = astacktraceelement.length;

            for (int j = 0; j < i; ++j) {
                StackTraceElement stacktraceelement = astacktraceelement[j];

                stringbuilder.append("\n\tat ");
                stringbuilder.append(stacktraceelement.toString());
            }
        }

    }

    public StackTraceElement[] func_147152_a() {
        return this.field_85075_d;
    }

    public static void func_147153_a(CrashReportCategory crashreportcategory, int i, int j, int k, Block block, int l) {
        int i1 = Block.func_149682_b(block);

        crashreportcategory.func_71500_a("Block type", new CrashReportCategory$1(i1, block));
        crashreportcategory.func_71500_a("Block data value", new CrashReportCategory$2(l));
        crashreportcategory.func_71500_a("Block location", new CrashReportCategory$3(i, j, k));
    }
}
