package net.minecraft.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings$GameType;

public class PlayerSelector {

    private static final Pattern field_82389_a = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");
    private static final Pattern field_82387_b = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
    private static final Pattern field_82388_c = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");

    public static EntityPlayerMP func_82386_a(ICommandSender icommandsender, String s) {
        EntityPlayerMP[] aentityplayermp = func_82380_c(icommandsender, s);

        return aentityplayermp != null && aentityplayermp.length == 1 ? aentityplayermp[0] : null;
    }

    public static IChatComponent func_150869_b(ICommandSender icommandsender, String s) {
        EntityPlayerMP[] aentityplayermp = func_82380_c(icommandsender, s);

        if (aentityplayermp != null && aentityplayermp.length != 0) {
            IChatComponent[] aichatcomponent = new IChatComponent[aentityplayermp.length];

            for (int i = 0; i < aichatcomponent.length; ++i) {
                aichatcomponent[i] = aentityplayermp[i].func_145748_c_();
            }

            return CommandBase.func_147177_a(aichatcomponent);
        } else {
            return null;
        }
    }

    public static EntityPlayerMP[] func_82380_c(ICommandSender icommandsender, String s) {
        Matcher matcher = PlayerSelector.field_82389_a.matcher(s);

        if (matcher.matches()) {
            Map map = func_82381_h(matcher.group(2));
            String s1 = matcher.group(1);
            int i = func_82384_c(s1);
            int j = func_82379_d(s1);
            int k = func_82375_f(s1);
            int l = func_82376_e(s1);
            int i1 = func_82382_g(s1);
            int j1 = WorldSettings$GameType.NOT_SET.func_77148_a();
            ChunkCoordinates chunkcoordinates = icommandsender.func_82114_b();
            Map map1 = func_96560_a(map);
            String s2 = null;
            String s3 = null;
            boolean flag = false;

            if (map.containsKey("rm")) {
                i = MathHelper.func_82715_a((String) map.get("rm"), i);
                flag = true;
            }

            if (map.containsKey("r")) {
                j = MathHelper.func_82715_a((String) map.get("r"), j);
                flag = true;
            }

            if (map.containsKey("lm")) {
                k = MathHelper.func_82715_a((String) map.get("lm"), k);
            }

            if (map.containsKey("l")) {
                l = MathHelper.func_82715_a((String) map.get("l"), l);
            }

            if (map.containsKey("x")) {
                chunkcoordinates.field_71574_a = MathHelper.func_82715_a((String) map.get("x"), chunkcoordinates.field_71574_a);
                flag = true;
            }

            if (map.containsKey("y")) {
                chunkcoordinates.field_71572_b = MathHelper.func_82715_a((String) map.get("y"), chunkcoordinates.field_71572_b);
                flag = true;
            }

            if (map.containsKey("z")) {
                chunkcoordinates.field_71573_c = MathHelper.func_82715_a((String) map.get("z"), chunkcoordinates.field_71573_c);
                flag = true;
            }

            if (map.containsKey("m")) {
                j1 = MathHelper.func_82715_a((String) map.get("m"), j1);
            }

            if (map.containsKey("c")) {
                i1 = MathHelper.func_82715_a((String) map.get("c"), i1);
            }

            if (map.containsKey("team")) {
                s3 = (String) map.get("team");
            }

            if (map.containsKey("name")) {
                s2 = (String) map.get("name");
            }

            World world = flag ? icommandsender.func_130014_f_() : null;
            List list;

            if (!s1.equals("p") && !s1.equals("a")) {
                if (s1.equals("r")) {
                    list = MinecraftServer.func_71276_C().func_71203_ab().func_82449_a(chunkcoordinates, i, j, 0, j1, k, l, map1, s2, s3, world);
                    Collections.shuffle(list);
                    list = list.subList(0, Math.min(i1, list.size()));
                    return list.isEmpty() ? new EntityPlayerMP[0] : (EntityPlayerMP[]) list.toArray(new EntityPlayerMP[list.size()]);
                } else {
                    return null;
                }
            } else {
                list = MinecraftServer.func_71276_C().func_71203_ab().func_82449_a(chunkcoordinates, i, j, i1, j1, k, l, map1, s2, s3, world);
                return list.isEmpty() ? new EntityPlayerMP[0] : (EntityPlayerMP[]) list.toArray(new EntityPlayerMP[list.size()]);
            }
        } else {
            return null;
        }
    }

    public static Map func_96560_a(Map map) {
        HashMap hashmap = new HashMap();
        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (s.startsWith("score_") && s.length() > "score_".length()) {
                String s1 = s.substring("score_".length());

                hashmap.put(s1, MathHelper.func_82715_a((String) map.get(s), 1));
            }
        }

        return hashmap;
    }

    public static boolean func_82377_a(String s) {
        Matcher matcher = PlayerSelector.field_82389_a.matcher(s);

        if (matcher.matches()) {
            Map map = func_82381_h(matcher.group(2));
            String s1 = matcher.group(1);
            int i = func_82382_g(s1);

            if (map.containsKey("c")) {
                i = MathHelper.func_82715_a((String) map.get("c"), i);
            }

            return i != 1;
        } else {
            return false;
        }
    }

    public static boolean func_82383_a(String s, String s1) {
        Matcher matcher = PlayerSelector.field_82389_a.matcher(s);

        if (matcher.matches()) {
            String s2 = matcher.group(1);

            return s1 == null || s1.equals(s2);
        } else {
            return false;
        }
    }

    public static boolean func_82378_b(String s) {
        return func_82383_a(s, (String) null);
    }

    private static final int func_82384_c(String s) {
        return 0;
    }

    private static final int func_82379_d(String s) {
        return 0;
    }

    private static final int func_82376_e(String s) {
        return Integer.MAX_VALUE;
    }

    private static final int func_82375_f(String s) {
        return 0;
    }

    private static final int func_82382_g(String s) {
        return s.equals("a") ? 0 : 1;
    }

    private static Map func_82381_h(String s) {
        HashMap hashmap = new HashMap();

        if (s == null) {
            return hashmap;
        } else {
            Matcher matcher = PlayerSelector.field_82387_b.matcher(s);
            int i = 0;

            int j;

            for (j = -1; matcher.find(); j = matcher.end()) {
                String s1 = null;

                switch (i++) {
                    case 0:
                        s1 = "x";
                        break;
                    case 1:
                        s1 = "y";
                        break;
                    case 2:
                        s1 = "z";
                        break;
                    case 3:
                        s1 = "r";
                }

                if (s1 != null && matcher.group(1).length() > 0) {
                    hashmap.put(s1, matcher.group(1));
                }
            }

            if (j < s.length()) {
                matcher = PlayerSelector.field_82388_c.matcher(j == -1 ? s : s.substring(j));

                while (matcher.find()) {
                    hashmap.put(matcher.group(1), matcher.group(2));
                }
            }

            return hashmap;
        }
    }
}
