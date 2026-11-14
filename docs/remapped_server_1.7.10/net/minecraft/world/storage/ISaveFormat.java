package net.minecraft.world.storage;

import net.minecraft.util.IProgressUpdate;

public interface ISaveFormat {

    ISaveHandler func_75804_a(String s, boolean flag);

    void func_75800_d();

    boolean func_75802_e(String s);

    boolean func_75801_b(String s);

    boolean func_75805_a(String s, IProgressUpdate iprogressupdate);
}
