package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.Profiler$Result;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase {

    private static final Logger field_147208_a = LogManager.getLogger();
    private long field_147206_b;
    private int field_147207_c;

    public CommandDebug() {}

    @Override
    public String func_71517_b() {
        return "debug";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.debug.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1) {
            if (astring[0].equals("start")) {
                func_152373_a(icommandsender, this, "commands.debug.start", new Object[0]);
                MinecraftServer.func_71276_C().func_71223_ag();
                this.field_147206_b = MinecraftServer.func_130071_aq();
                this.field_147207_c = MinecraftServer.func_71276_C().func_71259_af();
                return;
            }

            if (astring[0].equals("stop")) {
                if (!MinecraftServer.func_71276_C().field_71304_b.field_76327_a) {
                    throw new CommandException("commands.debug.notStarted", new Object[0]);
                }

                long i = MinecraftServer.func_130071_aq();
                int j = MinecraftServer.func_71276_C().func_71259_af();
                long k = i - this.field_147206_b;
                int l = j - this.field_147207_c;

                this.func_147205_a(k, l);
                MinecraftServer.func_71276_C().field_71304_b.field_76327_a = false;
                func_152373_a(icommandsender, this, "commands.debug.stop", new Object[]{(float) k / 1000.0F, l});
                return;
            }
        }

        throw new WrongUsageException("commands.debug.usage", new Object[0]);
    }

    private void func_147205_a(long i, int j) {
        File file = new File(MinecraftServer.func_71276_C().func_71209_f("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");

        file.getParentFile().mkdirs();

        try {
            FileWriter filewriter = new FileWriter(file);

            filewriter.write(this.func_147204_b(i, j));
            filewriter.close();
        } catch (Throwable throwable) {
            CommandDebug.field_147208_a.error("Could not save profiler results to " + file, throwable);
        }

    }

    private String func_147204_b(long i, int j) {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("---- Minecraft Profiler Results ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(func_147203_d());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time span: ").append(i).append(" ms\n");
        stringbuilder.append("Tick span: ").append(j).append(" ticks\n");
        stringbuilder.append("// This is approximately ").append(String.format("%.2f", (float) j / ((float) i / 1000.0F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.func_147202_a(0, "root", stringbuilder);
        stringbuilder.append("--- END PROFILE DUMP ---\n\n");
        return stringbuilder.toString();
    }

    private void func_147202_a(int i, String s, StringBuilder stringbuilder) {
        List list = MinecraftServer.func_71276_C().field_71304_b.func_76321_b(s);

        if (list != null && list.size() >= 3) {
            for (int j = 1; j < list.size(); ++j) {
                Profiler$Result profiler$result = (Profiler$Result) list.get(j);

                stringbuilder.append(String.format("[%02d] ", i));

                for (int k = 0; k < i; ++k) {
                    stringbuilder.append(" ");
                }

                stringbuilder.append(profiler$result.field_76331_c);
                stringbuilder.append(" - ");
                stringbuilder.append(String.format("%.2f", profiler$result.field_76332_a));
                stringbuilder.append("%/");
                stringbuilder.append(String.format("%.2f", profiler$result.field_76330_b));
                stringbuilder.append("%\n");
                if (!profiler$result.field_76331_c.equals("unspecified")) {
                    try {
                        this.func_147202_a(i + 1, s + "." + profiler$result.field_76331_c, stringbuilder);
                    } catch (Exception exception) {
                        stringbuilder.append("[[ EXCEPTION " + exception + " ]]");
                    }
                }
            }

        }
    }

    private static String func_147203_d() {
        String[] astring = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

        try {
            return astring[(int) (System.nanoTime() % (long) astring.length)];
        } catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, new String[]{"start", "stop"}) : null;
    }
}
