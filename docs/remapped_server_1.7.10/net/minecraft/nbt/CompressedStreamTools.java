package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools {

    public static NBTTagCompound func_74796_a(InputStream inputstream) {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputstream)));

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = func_152456_a(datainputstream, NBTSizeTracker.field_152451_a);
        } finally {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    public static void func_74799_a(NBTTagCompound nbttagcompound, OutputStream outputstream) {
        DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputstream)));

        try {
            func_74800_a(nbttagcompound, dataoutputstream);
        } finally {
            dataoutputstream.close();
        }

    }

    public static NBTTagCompound func_152457_a(byte[] abyte, NBTSizeTracker nbtsizetracker) {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte))));

        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = func_152456_a(datainputstream, nbtsizetracker);
        } finally {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    public static byte[] func_74798_a(NBTTagCompound nbttagcompound) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

        try {
            func_74800_a(nbttagcompound, dataoutputstream);
        } finally {
            dataoutputstream.close();
        }

        return bytearrayoutputstream.toByteArray();
    }

    public static NBTTagCompound func_74794_a(DataInputStream datainputstream) {
        return func_152456_a(datainputstream, NBTSizeTracker.field_152451_a);
    }

    public static NBTTagCompound func_152456_a(DataInput datainput, NBTSizeTracker nbtsizetracker) {
        NBTBase nbtbase = func_152455_a(datainput, 0, nbtsizetracker);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void func_74800_a(NBTTagCompound nbttagcompound, DataOutput dataoutput) {
        func_150663_a(nbttagcompound, dataoutput);
    }

    private static void func_150663_a(NBTBase nbtbase, DataOutput dataoutput) {
        dataoutput.writeByte(nbtbase.func_74732_a());
        if (nbtbase.func_74732_a() != 0) {
            dataoutput.writeUTF("");
            nbtbase.func_74734_a(dataoutput);
        }
    }

    private static NBTBase func_152455_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        byte b0 = datainput.readByte();

        if (b0 == 0) {
            return new NBTTagEnd();
        } else {
            datainput.readUTF();
            NBTBase nbtbase = NBTBase.func_150284_a(b0);

            try {
                nbtbase.func_152446_a(datainput, i, nbtsizetracker);
                return nbtbase;
            } catch (IOException ioexception) {
                CrashReport crashreport = CrashReport.func_85055_a(ioexception, "Loading NBT data");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("NBT Tag");

                crashreportcategory.func_71507_a("Tag name", "[UNNAMED TAG]");
                crashreportcategory.func_71507_a("Tag type", b0);
                throw new ReportedException(crashreport);
            }
        }
    }
}
