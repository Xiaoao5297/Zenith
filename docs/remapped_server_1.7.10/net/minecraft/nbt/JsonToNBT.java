package net.minecraft.nbt;

import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT {

    private static final Logger field_150317_a = LogManager.getLogger();

    public static NBTBase func_150315_a(String s) {
        s = s.trim();
        int i = func_150310_b(s);

        if (i != 1) {
            throw new NBTException("Encountered multiple top tags, only one expected");
        } else {
            JsonToNBT$Any jsontonbt$any = null;

            if (s.startsWith("{")) {
                jsontonbt$any = func_150316_a("tag", s);
            } else {
                jsontonbt$any = func_150316_a(func_150313_b(s, false), func_150311_c(s, false));
            }

            return jsontonbt$any.func_150489_a();
        }
    }

    static int func_150310_b(String s) {
        int i = 0;
        boolean flag = false;
        Stack stack = new Stack();

        for (int j = 0; j < s.length(); ++j) {
            char c0 = s.charAt(j);

            if (c0 == '"') {
                if (j > 0 && s.charAt(j - 1) == '\\') {
                    if (!flag) {
                        throw new NBTException("Illegal use of \\\": " + s);
                    }
                } else {
                    flag = !flag;
                }
            } else if (!flag) {
                if (c0 != '{' && c0 != '[') {
                    if (c0 == '}' && (stack.isEmpty() || (Character) stack.pop() != '{')) {
                        throw new NBTException("Unbalanced curly brackets {}: " + s);
                    }

                    if (c0 == ']' && (stack.isEmpty() || (Character) stack.pop() != '[')) {
                        throw new NBTException("Unbalanced square brackets []: " + s);
                    }
                } else {
                    if (stack.isEmpty()) {
                        ++i;
                    }

                    stack.push(c0);
                }
            }
        }

        if (flag) {
            throw new NBTException("Unbalanced quotation: " + s);
        } else if (!stack.isEmpty()) {
            throw new NBTException("Unbalanced brackets: " + s);
        } else if (i == 0 && !s.isEmpty()) {
            return 1;
        } else {
            return i;
        }
    }

    static JsonToNBT$Any func_150316_a(String s, String s1) {
        s1 = s1.trim();
        func_150310_b(s1);
        String s2;
        String s3;
        String s4;
        char c0;

        if (s1.startsWith("{")) {
            if (!s1.endsWith("}")) {
                throw new NBTException("Unable to locate ending bracket for: " + s1);
            } else {
                s1 = s1.substring(1, s1.length() - 1);
                JsonToNBT$Compound jsontonbt$compound = new JsonToNBT$Compound(s);

                while (s1.length() > 0) {
                    s2 = func_150314_a(s1, false);
                    if (s2.length() > 0) {
                        s3 = func_150313_b(s2, false);
                        s4 = func_150311_c(s2, false);
                        jsontonbt$compound.field_150491_b.add(func_150316_a(s3, s4));
                        if (s1.length() < s2.length() + 1) {
                            break;
                        }

                        c0 = s1.charAt(s2.length());
                        if (c0 != ',' && c0 != '{' && c0 != '}' && c0 != '[' && c0 != ']') {
                            throw new NBTException("Unexpected token '" + c0 + "' at: " + s1.substring(s2.length()));
                        }

                        s1 = s1.substring(s2.length() + 1);
                    }
                }

                return jsontonbt$compound;
            }
        } else if (s1.startsWith("[") && !s1.matches("\\[[-\\d|,\\s]+\\]")) {
            if (!s1.endsWith("]")) {
                throw new NBTException("Unable to locate ending bracket for: " + s1);
            } else {
                s1 = s1.substring(1, s1.length() - 1);
                JsonToNBT$List jsontonbt$list = new JsonToNBT$List(s);

                while (s1.length() > 0) {
                    s2 = func_150314_a(s1, true);
                    if (s2.length() > 0) {
                        s3 = func_150313_b(s2, true);
                        s4 = func_150311_c(s2, true);
                        jsontonbt$list.field_150492_b.add(func_150316_a(s3, s4));
                        if (s1.length() < s2.length() + 1) {
                            break;
                        }

                        c0 = s1.charAt(s2.length());
                        if (c0 != ',' && c0 != '{' && c0 != '}' && c0 != '[' && c0 != ']') {
                            throw new NBTException("Unexpected token '" + c0 + "' at: " + s1.substring(s2.length()));
                        }

                        s1 = s1.substring(s2.length() + 1);
                    } else {
                        JsonToNBT.field_150317_a.debug(s1);
                    }
                }

                return jsontonbt$list;
            }
        } else {
            return new JsonToNBT$Primitive(s, s1);
        }
    }

    private static String func_150314_a(String s, boolean flag) {
        int i = func_150312_a(s, ':');

        if (i < 0 && !flag) {
            throw new NBTException("Unable to locate name/value separator for string: " + s);
        } else {
            int j = func_150312_a(s, ',');

            if (j >= 0 && j < i && !flag) {
                throw new NBTException("Name error at: " + s);
            } else {
                if (flag && (i < 0 || i > j)) {
                    i = -1;
                }

                Stack stack = new Stack();
                int k = i + 1;
                boolean flag1 = false;
                boolean flag2 = false;
                boolean flag3 = false;

                for (int l = 0; k < s.length(); ++k) {
                    char c0 = s.charAt(k);

                    if (c0 == '"') {
                        if (k > 0 && s.charAt(k - 1) == '\\') {
                            if (!flag1) {
                                throw new NBTException("Illegal use of \\\": " + s);
                            }
                        } else {
                            flag1 = !flag1;
                            if (flag1 && !flag3) {
                                flag2 = true;
                            }

                            if (!flag1) {
                                l = k;
                            }
                        }
                    } else if (!flag1) {
                        if (c0 != '{' && c0 != '[') {
                            if (c0 == '}' && (stack.isEmpty() || (Character) stack.pop() != '{')) {
                                throw new NBTException("Unbalanced curly brackets {}: " + s);
                            }

                            if (c0 == ']' && (stack.isEmpty() || (Character) stack.pop() != '[')) {
                                throw new NBTException("Unbalanced square brackets []: " + s);
                            }

                            if (c0 == ',' && stack.isEmpty()) {
                                return s.substring(0, k);
                            }
                        } else {
                            stack.push(c0);
                        }
                    }

                    if (!Character.isWhitespace(c0)) {
                        if (!flag1 && flag2 && l != k) {
                            return s.substring(0, l + 1);
                        }

                        flag3 = true;
                    }
                }

                return s.substring(0, k);
            }
        }
    }

    private static String func_150313_b(String s, boolean flag) {
        if (flag) {
            s = s.trim();
            if (s.startsWith("{") || s.startsWith("[")) {
                return "";
            }
        }

        int i = s.indexOf(58);

        if (i < 0) {
            if (flag) {
                return "";
            } else {
                throw new NBTException("Unable to locate name/value separator for string: " + s);
            }
        } else {
            return s.substring(0, i).trim();
        }
    }

    private static String func_150311_c(String s, boolean flag) {
        if (flag) {
            s = s.trim();
            if (s.startsWith("{") || s.startsWith("[")) {
                return s;
            }
        }

        int i = s.indexOf(58);

        if (i < 0) {
            if (flag) {
                return s;
            } else {
                throw new NBTException("Unable to locate name/value separator for string: " + s);
            }
        } else {
            return s.substring(i + 1).trim();
        }
    }

    private static int func_150312_a(String s, char c0) {
        int i = 0;

        for (boolean flag = false; i < s.length(); ++i) {
            char c1 = s.charAt(i);

            if (c1 == '"') {
                if (i <= 0 || s.charAt(i - 1) != '\\') {
                    flag = !flag;
                }
            } else if (!flag) {
                if (c1 == c0) {
                    return i;
                }

                if (c1 == '{' || c1 == '[') {
                    return -1;
                }
            }
        }

        return -1;
    }
}
