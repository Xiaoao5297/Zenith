package net.minecraft.command.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent$Serializer;
import net.minecraft.world.World;

public abstract class CommandBlockLogic implements ICommandSender {

    private static final SimpleDateFormat field_145766_a = new SimpleDateFormat("HH:mm:ss");
    private int field_145764_b;
    private boolean field_145765_c = true;
    private IChatComponent field_145762_d = null;
    private String field_145763_e = "";
    private String field_145761_f = "@";

    public CommandBlockLogic() {}

    public int func_145760_g() {
        return this.field_145764_b;
    }

    public IChatComponent func_145749_h() {
        return this.field_145762_d;
    }

    public void func_145758_a(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74778_a("Command", this.field_145763_e);
        nbttagcompound.func_74768_a("SuccessCount", this.field_145764_b);
        nbttagcompound.func_74778_a("CustomName", this.field_145761_f);
        if (this.field_145762_d != null) {
            nbttagcompound.func_74778_a("LastOutput", IChatComponent$Serializer.func_150696_a(this.field_145762_d));
        }

        nbttagcompound.func_74757_a("TrackOutput", this.field_145765_c);
    }

    public void func_145759_b(NBTTagCompound nbttagcompound) {
        this.field_145763_e = nbttagcompound.func_74779_i("Command");
        this.field_145764_b = nbttagcompound.func_74762_e("SuccessCount");
        if (nbttagcompound.func_150297_b("CustomName", 8)) {
            this.field_145761_f = nbttagcompound.func_74779_i("CustomName");
        }

        if (nbttagcompound.func_150297_b("LastOutput", 8)) {
            this.field_145762_d = IChatComponent$Serializer.func_150699_a(nbttagcompound.func_74779_i("LastOutput"));
        }

        if (nbttagcompound.func_150297_b("TrackOutput", 1)) {
            this.field_145765_c = nbttagcompound.func_74767_n("TrackOutput");
        }

    }

    @Override
    public boolean func_70003_b(int i, String s) {
        return i <= 2;
    }

    public void func_145752_a(String s) {
        this.field_145763_e = s;
    }

    public String func_145753_i() {
        return this.field_145763_e;
    }

    public void func_145755_a(World world) {
        if (world.field_72995_K) {
            this.field_145764_b = 0;
        }

        MinecraftServer minecraftserver = MinecraftServer.func_71276_C();

        if (minecraftserver != null && minecraftserver.func_82356_Z()) {
            ICommandManager icommandmanager = minecraftserver.func_71187_D();

            this.field_145764_b = icommandmanager.func_71556_a(this, this.field_145763_e);
        } else {
            this.field_145764_b = 0;
        }

    }

    @Override
    public String func_70005_c_() {
        return this.field_145761_f;
    }

    @Override
    public IChatComponent func_145748_c_() {
        return new ChatComponentText(this.func_70005_c_());
    }

    public void func_145754_b(String s) {
        this.field_145761_f = s;
    }

    @Override
    public void func_145747_a(IChatComponent ichatcomponent) {
        if (this.field_145765_c && this.func_130014_f_() != null && !this.func_130014_f_().field_72995_K) {
            this.field_145762_d = (new ChatComponentText("[" + CommandBlockLogic.field_145766_a.format(new Date()) + "] ")).func_150257_a(ichatcomponent);
            this.func_145756_e();
        }

    }

    public abstract void func_145756_e();

    public void func_145750_b(IChatComponent ichatcomponent) {
        this.field_145762_d = ichatcomponent;
    }
}
