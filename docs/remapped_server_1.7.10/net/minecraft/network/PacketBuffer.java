package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

public class PacketBuffer extends ByteBuf {

    private final ByteBuf field_150794_a;

    public PacketBuffer(ByteBuf bytebuf) {
        this.field_150794_a = bytebuf;
    }

    public static int func_150790_a(int i) {
        return (i & Byte.MIN_VALUE) == 0 ? 1 : ((i & -16384) == 0 ? 2 : ((i & -2097152) == 0 ? 3 : ((i & -268435456) == 0 ? 4 : 5)));
    }

    public int func_150792_a() {
        int i = 0;
        int j = 0;

        byte b0;

        do {
            b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public void func_150787_b(int i) {
        while ((i & Byte.MIN_VALUE) != 0) {
            this.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        this.writeByte(i);
    }

    public void func_150786_a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            this.writeShort(-1);
        } else {
            byte[] abyte = CompressedStreamTools.func_74798_a(nbttagcompound);

            this.writeShort((short) abyte.length);
            this.writeBytes(abyte);
        }

    }

    public NBTTagCompound func_150793_b() {
        short short0 = this.readShort();

        if (short0 < 0) {
            return null;
        } else {
            byte[] abyte = new byte[short0];

            this.readBytes(abyte);
            return CompressedStreamTools.func_152457_a(abyte, new NBTSizeTracker(2097152L));
        }
    }

    public void func_150788_a(ItemStack itemstack) {
        if (itemstack == null) {
            this.writeShort(-1);
        } else {
            this.writeShort(Item.func_150891_b(itemstack.func_77973_b()));
            this.writeByte(itemstack.field_77994_a);
            this.writeShort(itemstack.func_77960_j());
            NBTTagCompound nbttagcompound = null;

            if (itemstack.func_77973_b().func_77645_m() || itemstack.func_77973_b().func_77651_p()) {
                nbttagcompound = itemstack.field_77990_d;
            }

            this.func_150786_a(nbttagcompound);
        }

    }

    public ItemStack func_150791_c() {
        ItemStack itemstack = null;
        short short0 = this.readShort();

        if (short0 >= 0) {
            byte b0 = this.readByte();
            short short1 = this.readShort();

            itemstack = new ItemStack(Item.func_150899_d(short0), b0, short1);
            itemstack.field_77990_d = this.func_150793_b();
        }

        return itemstack;
    }

    public String func_150789_c(int i) {
        int j = this.func_150792_a();

        if (j > i * 4) {
            throw new IOException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
        } else if (j < 0) {
            throw new IOException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String s = new String(this.readBytes(j).array(), Charsets.UTF_8);

            if (s.length() > i) {
                throw new IOException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
            } else {
                return s;
            }
        }
    }

    public void func_150785_a(String s) {
        byte[] abyte = s.getBytes(Charsets.UTF_8);

        if (abyte.length > 32767) {
            throw new IOException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            this.func_150787_b(abyte.length);
            this.writeBytes(abyte);
        }
    }

    @Override
    public int capacity() {
        return this.field_150794_a.capacity();
    }

    @Override
    public ByteBuf capacity(int i) {
        return this.field_150794_a.capacity(i);
    }

    @Override
    public int maxCapacity() {
        return this.field_150794_a.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.field_150794_a.alloc();
    }

    @Override
    public ByteOrder order() {
        return this.field_150794_a.order();
    }

    @Override
    public ByteBuf order(ByteOrder byteorder) {
        return this.field_150794_a.order(byteorder);
    }

    @Override
    public ByteBuf unwrap() {
        return this.field_150794_a.unwrap();
    }

    @Override
    public boolean isDirect() {
        return this.field_150794_a.isDirect();
    }

    @Override
    public int readerIndex() {
        return this.field_150794_a.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int i) {
        return this.field_150794_a.readerIndex(i);
    }

    @Override
    public int writerIndex() {
        return this.field_150794_a.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int i) {
        return this.field_150794_a.writerIndex(i);
    }

    @Override
    public ByteBuf setIndex(int i, int j) {
        return this.field_150794_a.setIndex(i, j);
    }

    @Override
    public int readableBytes() {
        return this.field_150794_a.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.field_150794_a.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.field_150794_a.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.field_150794_a.isReadable();
    }

    @Override
    public boolean isReadable(int i) {
        return this.field_150794_a.isReadable(i);
    }

    @Override
    public boolean isWritable() {
        return this.field_150794_a.isWritable();
    }

    @Override
    public boolean isWritable(int i) {
        return this.field_150794_a.isWritable(i);
    }

    @Override
    public ByteBuf clear() {
        return this.field_150794_a.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this.field_150794_a.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this.field_150794_a.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this.field_150794_a.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this.field_150794_a.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return this.field_150794_a.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return this.field_150794_a.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int i) {
        return this.field_150794_a.ensureWritable(i);
    }

    @Override
    public int ensureWritable(int i, boolean flag) {
        return this.field_150794_a.ensureWritable(i, flag);
    }

    @Override
    public boolean getBoolean(int i) {
        return this.field_150794_a.getBoolean(i);
    }

    @Override
    public byte getByte(int i) {
        return this.field_150794_a.getByte(i);
    }

    @Override
    public short getUnsignedByte(int i) {
        return this.field_150794_a.getUnsignedByte(i);
    }

    @Override
    public short getShort(int i) {
        return this.field_150794_a.getShort(i);
    }

    @Override
    public int getUnsignedShort(int i) {
        return this.field_150794_a.getUnsignedShort(i);
    }

    @Override
    public int getMedium(int i) {
        return this.field_150794_a.getMedium(i);
    }

    @Override
    public int getUnsignedMedium(int i) {
        return this.field_150794_a.getUnsignedMedium(i);
    }

    @Override
    public int getInt(int i) {
        return this.field_150794_a.getInt(i);
    }

    @Override
    public long getUnsignedInt(int i) {
        return this.field_150794_a.getUnsignedInt(i);
    }

    @Override
    public long getLong(int i) {
        return this.field_150794_a.getLong(i);
    }

    @Override
    public char getChar(int i) {
        return this.field_150794_a.getChar(i);
    }

    @Override
    public float getFloat(int i) {
        return this.field_150794_a.getFloat(i);
    }

    @Override
    public double getDouble(int i) {
        return this.field_150794_a.getDouble(i);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        return this.field_150794_a.getBytes(i, bytebuf);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        return this.field_150794_a.getBytes(i, bytebuf, j);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.field_150794_a.getBytes(i, bytebuf, j, k);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte) {
        return this.field_150794_a.getBytes(i, abyte);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        return this.field_150794_a.getBytes(i, abyte, j, k);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        return this.field_150794_a.getBytes(i, bytebuffer);
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) {
        return this.field_150794_a.getBytes(i, outputstream, j);
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) {
        return this.field_150794_a.getBytes(i, gatheringbytechannel, j);
    }

    @Override
    public ByteBuf setBoolean(int i, boolean flag) {
        return this.field_150794_a.setBoolean(i, flag);
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        return this.field_150794_a.setByte(i, j);
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        return this.field_150794_a.setShort(i, j);
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        return this.field_150794_a.setMedium(i, j);
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        return this.field_150794_a.setInt(i, j);
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        return this.field_150794_a.setLong(i, j);
    }

    @Override
    public ByteBuf setChar(int i, int j) {
        return this.field_150794_a.setChar(i, j);
    }

    @Override
    public ByteBuf setFloat(int i, float f) {
        return this.field_150794_a.setFloat(i, f);
    }

    @Override
    public ByteBuf setDouble(int i, double d0) {
        return this.field_150794_a.setDouble(i, d0);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        return this.field_150794_a.setBytes(i, bytebuf);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        return this.field_150794_a.setBytes(i, bytebuf, j);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.field_150794_a.setBytes(i, bytebuf, j, k);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte) {
        return this.field_150794_a.setBytes(i, abyte);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        return this.field_150794_a.setBytes(i, abyte, j, k);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        return this.field_150794_a.setBytes(i, bytebuffer);
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) {
        return this.field_150794_a.setBytes(i, inputstream, j);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) {
        return this.field_150794_a.setBytes(i, scatteringbytechannel, j);
    }

    @Override
    public ByteBuf setZero(int i, int j) {
        return this.field_150794_a.setZero(i, j);
    }

    @Override
    public boolean readBoolean() {
        return this.field_150794_a.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.field_150794_a.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.field_150794_a.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.field_150794_a.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.field_150794_a.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        return this.field_150794_a.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        return this.field_150794_a.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        return this.field_150794_a.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return this.field_150794_a.readUnsignedInt();
    }

    @Override
    public long readLong() {
        return this.field_150794_a.readLong();
    }

    @Override
    public char readChar() {
        return this.field_150794_a.readChar();
    }

    @Override
    public float readFloat() {
        return this.field_150794_a.readFloat();
    }

    @Override
    public double readDouble() {
        return this.field_150794_a.readDouble();
    }

    @Override
    public ByteBuf readBytes(int i) {
        return this.field_150794_a.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(int i) {
        return this.field_150794_a.readSlice(i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf) {
        return this.field_150794_a.readBytes(bytebuf);
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        return this.field_150794_a.readBytes(bytebuf, i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        return this.field_150794_a.readBytes(bytebuf, i, j);
    }

    @Override
    public ByteBuf readBytes(byte[] abyte) {
        return this.field_150794_a.readBytes(abyte);
    }

    @Override
    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        return this.field_150794_a.readBytes(abyte, i, j);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        return this.field_150794_a.readBytes(bytebuffer);
    }

    @Override
    public ByteBuf readBytes(OutputStream outputstream, int i) {
        return this.field_150794_a.readBytes(outputstream, i);
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) {
        return this.field_150794_a.readBytes(gatheringbytechannel, i);
    }

    @Override
    public ByteBuf skipBytes(int i) {
        return this.field_150794_a.skipBytes(i);
    }

    @Override
    public ByteBuf writeBoolean(boolean flag) {
        return this.field_150794_a.writeBoolean(flag);
    }

    @Override
    public ByteBuf writeByte(int i) {
        return this.field_150794_a.writeByte(i);
    }

    @Override
    public ByteBuf writeShort(int i) {
        return this.field_150794_a.writeShort(i);
    }

    @Override
    public ByteBuf writeMedium(int i) {
        return this.field_150794_a.writeMedium(i);
    }

    @Override
    public ByteBuf writeInt(int i) {
        return this.field_150794_a.writeInt(i);
    }

    @Override
    public ByteBuf writeLong(long i) {
        return this.field_150794_a.writeLong(i);
    }

    @Override
    public ByteBuf writeChar(int i) {
        return this.field_150794_a.writeChar(i);
    }

    @Override
    public ByteBuf writeFloat(float f) {
        return this.field_150794_a.writeFloat(f);
    }

    @Override
    public ByteBuf writeDouble(double d0) {
        return this.field_150794_a.writeDouble(d0);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf) {
        return this.field_150794_a.writeBytes(bytebuf);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        return this.field_150794_a.writeBytes(bytebuf, i);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        return this.field_150794_a.writeBytes(bytebuf, i, j);
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte) {
        return this.field_150794_a.writeBytes(abyte);
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        return this.field_150794_a.writeBytes(abyte, i, j);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        return this.field_150794_a.writeBytes(bytebuffer);
    }

    @Override
    public int writeBytes(InputStream inputstream, int i) {
        return this.field_150794_a.writeBytes(inputstream, i);
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) {
        return this.field_150794_a.writeBytes(scatteringbytechannel, i);
    }

    @Override
    public ByteBuf writeZero(int i) {
        return this.field_150794_a.writeZero(i);
    }

    @Override
    public int indexOf(int i, int j, byte b0) {
        return this.field_150794_a.indexOf(i, j, b0);
    }

    @Override
    public int bytesBefore(byte b0) {
        return this.field_150794_a.bytesBefore(b0);
    }

    @Override
    public int bytesBefore(int i, byte b0) {
        return this.field_150794_a.bytesBefore(i, b0);
    }

    @Override
    public int bytesBefore(int i, int j, byte b0) {
        return this.field_150794_a.bytesBefore(i, j, b0);
    }

    @Override
    public int forEachByte(ByteBufProcessor bytebufprocessor) {
        return this.field_150794_a.forEachByte(bytebufprocessor);
    }

    @Override
    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.field_150794_a.forEachByte(i, j, bytebufprocessor);
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
        return this.field_150794_a.forEachByteDesc(bytebufprocessor);
    }

    @Override
    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.field_150794_a.forEachByteDesc(i, j, bytebufprocessor);
    }

    @Override
    public ByteBuf copy() {
        return this.field_150794_a.copy();
    }

    @Override
    public ByteBuf copy(int i, int j) {
        return this.field_150794_a.copy(i, j);
    }

    @Override
    public ByteBuf slice() {
        return this.field_150794_a.slice();
    }

    @Override
    public ByteBuf slice(int i, int j) {
        return this.field_150794_a.slice(i, j);
    }

    @Override
    public ByteBuf duplicate() {
        return this.field_150794_a.duplicate();
    }

    @Override
    public int nioBufferCount() {
        return this.field_150794_a.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.field_150794_a.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        return this.field_150794_a.nioBuffer(i, j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.field_150794_a.internalNioBuffer(i, j);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.field_150794_a.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.field_150794_a.nioBuffers(i, j);
    }

    @Override
    public boolean hasArray() {
        return this.field_150794_a.hasArray();
    }

    @Override
    public byte[] array() {
        return this.field_150794_a.array();
    }

    @Override
    public int arrayOffset() {
        return this.field_150794_a.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.field_150794_a.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.field_150794_a.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return this.field_150794_a.toString(charset);
    }

    @Override
    public String toString(int i, int j, Charset charset) {
        return this.field_150794_a.toString(i, j, charset);
    }

    @Override
    public int hashCode() {
        return this.field_150794_a.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.field_150794_a.equals(object);
    }

    @Override
    public int compareTo(ByteBuf bytebuf) {
        return this.field_150794_a.compareTo(bytebuf);
    }

    @Override
    public String toString() {
        return this.field_150794_a.toString();
    }

    @Override
    public ByteBuf retain(int i) {
        return this.field_150794_a.retain(i);
    }

    @Override
    public ByteBuf retain() {
        return this.field_150794_a.retain();
    }

    @Override
    public int refCnt() {
        return this.field_150794_a.refCnt();
    }

    @Override
    public boolean release() {
        return this.field_150794_a.release();
    }

    @Override
    public boolean release(int i) {
        return this.field_150794_a.release(i);
    }
}
