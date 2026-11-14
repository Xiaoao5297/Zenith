package io.netty.buffer;

import io.netty.util.ResourceLeak;
import io.netty.util.internal.EmptyArrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CompositeByteBuf extends AbstractReferenceCountedByteBuf {

    private final ResourceLeak leak;
    private final ByteBufAllocator alloc;
    private final boolean direct;
    private final List<CompositeByteBuf.Component> components = new ArrayList();
    private final int maxNumComponents;
    private static final ByteBuffer FULL_BYTEBUFFER = (ByteBuffer) ByteBuffer.allocate(1).position(1);
    private boolean freed;

    public CompositeByteBuf(ByteBufAllocator bytebufallocator, boolean flag, int i) {
        super(Integer.MAX_VALUE);
        if (bytebufallocator == null) {
            throw new NullPointerException("alloc");
        } else {
            this.alloc = bytebufallocator;
            this.direct = flag;
            this.maxNumComponents = i;
            this.leak = CompositeByteBuf.leakDetector.open(this);
        }
    }

    public CompositeByteBuf(ByteBufAllocator bytebufallocator, boolean flag, int i, ByteBuf... abytebuf) {
        super(Integer.MAX_VALUE);
        if (bytebufallocator == null) {
            throw new NullPointerException("alloc");
        } else if (i < 2) {
            throw new IllegalArgumentException("maxNumComponents: " + i + " (expected: >= 2)");
        } else {
            this.alloc = bytebufallocator;
            this.direct = flag;
            this.maxNumComponents = i;
            this.addComponents0(0, abytebuf);
            this.consolidateIfNeeded();
            this.setIndex(0, this.capacity());
            this.leak = CompositeByteBuf.leakDetector.open(this);
        }
    }

    public CompositeByteBuf(ByteBufAllocator bytebufallocator, boolean flag, int i, Iterable<ByteBuf> iterable) {
        super(Integer.MAX_VALUE);
        if (bytebufallocator == null) {
            throw new NullPointerException("alloc");
        } else if (i < 2) {
            throw new IllegalArgumentException("maxNumComponents: " + i + " (expected: >= 2)");
        } else {
            this.alloc = bytebufallocator;
            this.direct = flag;
            this.maxNumComponents = i;
            this.addComponents0(0, iterable);
            this.consolidateIfNeeded();
            this.setIndex(0, this.capacity());
            this.leak = CompositeByteBuf.leakDetector.open(this);
        }
    }

    public CompositeByteBuf addComponent(ByteBuf bytebuf) {
        this.addComponent0(this.components.size(), bytebuf);
        this.consolidateIfNeeded();
        return this;
    }

    public CompositeByteBuf addComponents(ByteBuf... abytebuf) {
        this.addComponents0(this.components.size(), abytebuf);
        this.consolidateIfNeeded();
        return this;
    }

    public CompositeByteBuf addComponents(Iterable<ByteBuf> iterable) {
        this.addComponents0(this.components.size(), iterable);
        this.consolidateIfNeeded();
        return this;
    }

    public CompositeByteBuf addComponent(int i, ByteBuf bytebuf) {
        this.addComponent0(i, bytebuf);
        this.consolidateIfNeeded();
        return this;
    }

    private int addComponent0(int i, ByteBuf bytebuf) {
        this.checkComponentIndex(i);
        if (bytebuf == null) {
            throw new NullPointerException("buffer");
        } else {
            int j = bytebuf.readableBytes();

            if (j == 0) {
                return i;
            } else {
                CompositeByteBuf.Component compositebytebuf_component = new CompositeByteBuf.Component(bytebuf.order(ByteOrder.BIG_ENDIAN).slice());

                if (i == this.components.size()) {
                    this.components.add(compositebytebuf_component);
                    if (i == 0) {
                        compositebytebuf_component.endOffset = j;
                    } else {
                        CompositeByteBuf.Component compositebytebuf_component1 = (CompositeByteBuf.Component) this.components.get(i - 1);

                        compositebytebuf_component.offset = compositebytebuf_component1.endOffset;
                        compositebytebuf_component.endOffset = compositebytebuf_component.offset + j;
                    }
                } else {
                    this.components.add(i, compositebytebuf_component);
                    this.updateComponentOffsets(i);
                }

                return i;
            }
        }
    }

    public CompositeByteBuf addComponents(int i, ByteBuf... abytebuf) {
        this.addComponents0(i, abytebuf);
        this.consolidateIfNeeded();
        return this;
    }

    private int addComponents0(int i, ByteBuf... abytebuf) {
        this.checkComponentIndex(i);
        if (abytebuf == null) {
            throw new NullPointerException("buffers");
        } else {
            int j = 0;
            ByteBuf[] abytebuf1 = abytebuf;
            int k = abytebuf.length;

            ByteBuf bytebuf;
            int l;

            for (l = 0; l < k; ++l) {
                bytebuf = abytebuf1[l];
                if (bytebuf == null) {
                    break;
                }

                j += bytebuf.readableBytes();
            }

            if (j == 0) {
                return i;
            } else {
                abytebuf1 = abytebuf;
                k = abytebuf.length;

                for (l = 0; l < k; ++l) {
                    bytebuf = abytebuf1[l];
                    if (bytebuf == null) {
                        break;
                    }

                    if (bytebuf.isReadable()) {
                        i = this.addComponent0(i, bytebuf) + 1;
                        int i1 = this.components.size();

                        if (i > i1) {
                            i = i1;
                        }
                    } else {
                        bytebuf.release();
                    }
                }

                return i;
            }
        }
    }

    public CompositeByteBuf addComponents(int i, Iterable<ByteBuf> iterable) {
        this.addComponents0(i, iterable);
        this.consolidateIfNeeded();
        return this;
    }

    private int addComponents0(int i, Iterable<ByteBuf> iterable) {
        if (iterable == null) {
            throw new NullPointerException("buffers");
        } else if (iterable instanceof ByteBuf) {
            return this.addComponent0(i, (ByteBuf) iterable);
        } else {
            if (!(iterable instanceof Collection)) {
                ArrayList arraylist = new ArrayList();
                Iterator iterator = ((Iterable) iterable).iterator();

                while (iterator.hasNext()) {
                    ByteBuf bytebuf = (ByteBuf) iterator.next();

                    arraylist.add(bytebuf);
                }

                iterable = arraylist;
            }

            Collection collection = (Collection) iterable;

            return this.addComponents0(i, (ByteBuf[]) collection.toArray(new ByteBuf[collection.size()]));
        }
    }

    private void consolidateIfNeeded() {
        int i = this.components.size();

        if (i > this.maxNumComponents) {
            int j = ((CompositeByteBuf.Component) this.components.get(i - 1)).endOffset;
            ByteBuf bytebuf = this.allocBuffer(j);

            for (int k = 0; k < i; ++k) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(k);
                ByteBuf bytebuf1 = compositebytebuf_component.buf;

                bytebuf.writeBytes(bytebuf1);
                compositebytebuf_component.freeIfNecessary();
            }

            CompositeByteBuf.Component compositebytebuf_component1 = new CompositeByteBuf.Component(bytebuf);

            compositebytebuf_component1.endOffset = compositebytebuf_component1.length;
            this.components.clear();
            this.components.add(compositebytebuf_component1);
        }

    }

    private void checkComponentIndex(int i) {
        assert !this.freed;

        if (i < 0 || i > this.components.size()) {
            throw new IndexOutOfBoundsException(String.format("cIndex: %d (expected: >= 0 && <= numComponents(%d))", i, this.components.size()));
        }
    }

    private void checkComponentIndex(int i, int j) {
        assert !this.freed;

        if (i < 0 || i + j > this.components.size()) {
            throw new IndexOutOfBoundsException(String.format("cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", i, j, this.components.size()));
        }
    }

    private void updateComponentOffsets(int i) {
        int j = this.components.size();

        if (j > i) {
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(i);

            if (i == 0) {
                compositebytebuf_component.offset = 0;
                compositebytebuf_component.endOffset = compositebytebuf_component.length;
                ++i;
            }

            for (int k = i; k < j; ++k) {
                CompositeByteBuf.Component compositebytebuf_component1 = (CompositeByteBuf.Component) this.components.get(k - 1);
                CompositeByteBuf.Component compositebytebuf_component2 = (CompositeByteBuf.Component) this.components.get(k);

                compositebytebuf_component2.offset = compositebytebuf_component1.endOffset;
                compositebytebuf_component2.endOffset = compositebytebuf_component2.offset + compositebytebuf_component2.length;
            }

        }
    }

    public CompositeByteBuf removeComponent(int i) {
        this.checkComponentIndex(i);
        ((CompositeByteBuf.Component) this.components.remove(i)).freeIfNecessary();
        this.updateComponentOffsets(i);
        return this;
    }

    public CompositeByteBuf removeComponents(int i, int j) {
        this.checkComponentIndex(i, j);
        List list = this.components.subList(i, i + j);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) iterator.next();

            compositebytebuf_component.freeIfNecessary();
        }

        list.clear();
        this.updateComponentOffsets(i);
        return this;
    }

    public Iterator<ByteBuf> iterator() {
        assert !this.freed;

        ArrayList arraylist = new ArrayList(this.components.size());
        Iterator iterator = this.components.iterator();

        while (iterator.hasNext()) {
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) iterator.next();

            arraylist.add(compositebytebuf_component.buf);
        }

        return arraylist.iterator();
    }

    public List<ByteBuf> decompose(int i, int j) {
        this.checkIndex(i, j);
        if (j == 0) {
            return Collections.emptyList();
        } else {
            int k = this.toComponentIndex(i);
            ArrayList arraylist = new ArrayList(this.components.size());
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(k);
            ByteBuf bytebuf = compositebytebuf_component.buf.duplicate();

            bytebuf.readerIndex(i - compositebytebuf_component.offset);
            ByteBuf bytebuf1 = bytebuf;
            int l = j;

            int i1;

            do {
                i1 = bytebuf1.readableBytes();
                if (l <= i1) {
                    bytebuf1.writerIndex(bytebuf1.readerIndex() + l);
                    arraylist.add(bytebuf1);
                    break;
                }

                arraylist.add(bytebuf1);
                l -= i1;
                ++k;
                bytebuf1 = ((CompositeByteBuf.Component) this.components.get(k)).buf.duplicate();
            } while (l > 0);

            for (i1 = 0; i1 < arraylist.size(); ++i1) {
                arraylist.set(i1, ((ByteBuf) arraylist.get(i1)).slice());
            }

            return arraylist;
        }
    }

    @Override
    public boolean isDirect() {
        int i = this.components.size();

        if (i == 0) {
            return false;
        } else {
            for (int j = 0; j < i; ++j) {
                if (!((CompositeByteBuf.Component) this.components.get(j)).buf.isDirect()) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean hasArray() {
        return this.components.size() == 1 ? ((CompositeByteBuf.Component) this.components.get(0)).buf.hasArray() : false;
    }

    @Override
    public byte[] array() {
        if (this.components.size() == 1) {
            return ((CompositeByteBuf.Component) this.components.get(0)).buf.array();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int arrayOffset() {
        if (this.components.size() == 1) {
            return ((CompositeByteBuf.Component) this.components.get(0)).buf.arrayOffset();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.components.size() == 1 ? ((CompositeByteBuf.Component) this.components.get(0)).buf.hasMemoryAddress() : false;
    }

    @Override
    public long memoryAddress() {
        if (this.components.size() == 1) {
            return ((CompositeByteBuf.Component) this.components.get(0)).buf.memoryAddress();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int capacity() {
        return this.components.isEmpty() ? 0 : ((CompositeByteBuf.Component) this.components.get(this.components.size() - 1)).endOffset;
    }

    @Override
    public CompositeByteBuf capacity(int i) {
        assert !this.freed;

        if (i >= 0 && i <= this.maxCapacity()) {
            int j = this.capacity();
            int k;

            if (i > j) {
                k = i - j;
                int l = this.components.size();
                ByteBuf bytebuf;

                if (l < this.maxNumComponents) {
                    bytebuf = this.allocBuffer(k);
                    bytebuf.setIndex(0, k);
                    this.addComponent0(this.components.size(), bytebuf);
                } else {
                    bytebuf = this.allocBuffer(k);
                    bytebuf.setIndex(0, k);
                    this.addComponent0(this.components.size(), bytebuf);
                    this.consolidateIfNeeded();
                }
            } else if (i < j) {
                k = j - i;
                ListIterator listiterator = this.components.listIterator(this.components.size());

                while (listiterator.hasPrevious()) {
                    CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) listiterator.previous();

                    if (k < compositebytebuf_component.length) {
                        CompositeByteBuf.Component compositebytebuf_component1 = new CompositeByteBuf.Component(compositebytebuf_component.buf.slice(0, compositebytebuf_component.length - k));

                        compositebytebuf_component1.offset = compositebytebuf_component.offset;
                        compositebytebuf_component1.endOffset = compositebytebuf_component1.offset + compositebytebuf_component1.length;
                        listiterator.set(compositebytebuf_component1);
                        break;
                    }

                    k -= compositebytebuf_component.length;
                    listiterator.remove();
                }

                if (this.readerIndex() > i) {
                    this.setIndex(i, i);
                } else if (this.writerIndex() > i) {
                    this.writerIndex(i);
                }
            }

            return this;
        } else {
            throw new IllegalArgumentException("newCapacity: " + i);
        }
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    public int numComponents() {
        return this.components.size();
    }

    public int maxNumComponents() {
        return this.maxNumComponents;
    }

    public int toComponentIndex(int i) {
        assert !this.freed;

        this.checkIndex(i);
        int j = 0;
        int k = this.components.size();

        while (j <= k) {
            int l = j + k >>> 1;
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(l);

            if (i >= compositebytebuf_component.endOffset) {
                j = l + 1;
            } else {
                if (i >= compositebytebuf_component.offset) {
                    return l;
                }

                k = l - 1;
            }
        }

        throw new Error("should not reach here");
    }

    public int toByteIndex(int i) {
        this.checkComponentIndex(i);
        return ((CompositeByteBuf.Component) this.components.get(i)).offset;
    }

    @Override
    public byte getByte(int i) {
        return this._getByte(i);
    }

    @Override
    protected byte _getByte(int i) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        return compositebytebuf_component.buf.getByte(i - compositebytebuf_component.offset);
    }

    @Override
    protected short _getShort(int i) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        return i + 2 <= compositebytebuf_component.endOffset ? compositebytebuf_component.buf.getShort(i - compositebytebuf_component.offset) : (this.order() == ByteOrder.BIG_ENDIAN ? (short) ((this._getByte(i) & 255) << 8 | this._getByte(i + 1) & 255) : (short) (this._getByte(i) & 255 | (this._getByte(i + 1) & 255) << 8));
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        return i + 3 <= compositebytebuf_component.endOffset ? compositebytebuf_component.buf.getUnsignedMedium(i - compositebytebuf_component.offset) : (this.order() == ByteOrder.BIG_ENDIAN ? (this._getShort(i) & '\uffff') << 8 | this._getByte(i + 2) & 255 : this._getShort(i) & '\uffff' | (this._getByte(i + 2) & 255) << 16);
    }

    @Override
    protected int _getInt(int i) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        return i + 4 <= compositebytebuf_component.endOffset ? compositebytebuf_component.buf.getInt(i - compositebytebuf_component.offset) : (this.order() == ByteOrder.BIG_ENDIAN ? (this._getShort(i) & '\uffff') << 16 | this._getShort(i + 2) & '\uffff' : this._getShort(i) & '\uffff' | (this._getShort(i + 2) & '\uffff') << 16);
    }

    @Override
    protected long _getLong(int i) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        return i + 8 <= compositebytebuf_component.endOffset ? compositebytebuf_component.buf.getLong(i - compositebytebuf_component.offset) : (this.order() == ByteOrder.BIG_ENDIAN ? ((long) this._getInt(i) & 4294967295L) << 32 | (long) this._getInt(i + 4) & 4294967295L : (long) this._getInt(i) & 4294967295L | ((long) this._getInt(i + 4) & 4294967295L) << 32);
    }

    @Override
    public CompositeByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.checkDstIndex(i, k, j, abyte.length);
        if (k == 0) {
            return this;
        } else {
            for (int l = this.toComponentIndex(i); k > 0; ++l) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(l);
                ByteBuf bytebuf = compositebytebuf_component.buf;
                int i1 = compositebytebuf_component.offset;
                int j1 = Math.min(k, bytebuf.capacity() - (i - i1));

                bytebuf.getBytes(i - i1, abyte, j, j1);
                i += j1;
                j += j1;
                k -= j1;
            }

            return this;
        }
    }

    @Override
    public CompositeByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        int j = bytebuffer.limit();
        int k = bytebuffer.remaining();

        this.checkIndex(i, k);
        if (k == 0) {
            return this;
        } else {
            int l = this.toComponentIndex(i);

            try {
                while (k > 0) {
                    CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(l);
                    ByteBuf bytebuf = compositebytebuf_component.buf;
                    int i1 = compositebytebuf_component.offset;
                    int j1 = Math.min(k, bytebuf.capacity() - (i - i1));

                    bytebuffer.limit(bytebuffer.position() + j1);
                    bytebuf.getBytes(i - i1, bytebuffer);
                    i += j1;
                    k -= j1;
                    ++l;
                }
            } finally {
                bytebuffer.limit(j);
            }

            return this;
        }
    }

    @Override
    public CompositeByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkDstIndex(i, k, j, bytebuf.capacity());
        if (k == 0) {
            return this;
        } else {
            for (int l = this.toComponentIndex(i); k > 0; ++l) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(l);
                ByteBuf bytebuf1 = compositebytebuf_component.buf;
                int i1 = compositebytebuf_component.offset;
                int j1 = Math.min(k, bytebuf1.capacity() - (i - i1));

                bytebuf1.getBytes(i - i1, bytebuf, j, j1);
                i += j1;
                j += j1;
                k -= j1;
            }

            return this;
        }
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        int k = this.nioBufferCount();

        if (k == 1) {
            return gatheringbytechannel.write(this.internalNioBuffer(i, j));
        } else {
            long l = gatheringbytechannel.write(this.nioBuffers(i, j));

            return l > 2147483647L ? Integer.MAX_VALUE : (int) l;
        }
    }

    @Override
    public CompositeByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.checkIndex(i, j);
        if (j == 0) {
            return this;
        } else {
            for (int k = this.toComponentIndex(i); j > 0; ++k) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(k);
                ByteBuf bytebuf = compositebytebuf_component.buf;
                int l = compositebytebuf_component.offset;
                int i1 = Math.min(j, bytebuf.capacity() - (i - l));

                bytebuf.getBytes(i - l, outputstream, i1);
                i += i1;
                j -= i1;
            }

            return this;
        }
    }

    @Override
    public CompositeByteBuf setByte(int i, int j) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        compositebytebuf_component.buf.setByte(i - compositebytebuf_component.offset, j);
        return this;
    }

    @Override
    protected void _setByte(int i, int j) {
        this.setByte(i, j);
    }

    @Override
    public CompositeByteBuf setShort(int i, int j) {
        return (CompositeByteBuf) super.setShort(i, j);
    }

    @Override
    protected void _setShort(int i, int j) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        if (i + 2 <= compositebytebuf_component.endOffset) {
            compositebytebuf_component.buf.setShort(i - compositebytebuf_component.offset, j);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setByte(i, (byte) (j >>> 8));
            this._setByte(i + 1, (byte) j);
        } else {
            this._setByte(i, (byte) j);
            this._setByte(i + 1, (byte) (j >>> 8));
        }

    }

    @Override
    public CompositeByteBuf setMedium(int i, int j) {
        return (CompositeByteBuf) super.setMedium(i, j);
    }

    @Override
    protected void _setMedium(int i, int j) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        if (i + 3 <= compositebytebuf_component.endOffset) {
            compositebytebuf_component.buf.setMedium(i - compositebytebuf_component.offset, j);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShort(i, (short) (j >> 8));
            this._setByte(i + 2, (byte) j);
        } else {
            this._setShort(i, (short) j);
            this._setByte(i + 2, (byte) (j >>> 16));
        }

    }

    @Override
    public CompositeByteBuf setInt(int i, int j) {
        return (CompositeByteBuf) super.setInt(i, j);
    }

    @Override
    protected void _setInt(int i, int j) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        if (i + 4 <= compositebytebuf_component.endOffset) {
            compositebytebuf_component.buf.setInt(i - compositebytebuf_component.offset, j);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShort(i, (short) (j >>> 16));
            this._setShort(i + 2, (short) j);
        } else {
            this._setShort(i, (short) j);
            this._setShort(i + 2, (short) (j >>> 16));
        }

    }

    @Override
    public CompositeByteBuf setLong(int i, long j) {
        return (CompositeByteBuf) super.setLong(i, j);
    }

    @Override
    protected void _setLong(int i, long j) {
        CompositeByteBuf.Component compositebytebuf_component = this.findComponent(i);

        if (i + 8 <= compositebytebuf_component.endOffset) {
            compositebytebuf_component.buf.setLong(i - compositebytebuf_component.offset, j);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setInt(i, (int) (j >>> 32));
            this._setInt(i + 4, (int) j);
        } else {
            this._setInt(i, (int) j);
            this._setInt(i + 4, (int) (j >>> 32));
        }

    }

    @Override
    public CompositeByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        this.checkSrcIndex(i, k, j, abyte.length);
        if (k == 0) {
            return this;
        } else {
            for (int l = this.toComponentIndex(i); k > 0; ++l) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(l);
                ByteBuf bytebuf = compositebytebuf_component.buf;
                int i1 = compositebytebuf_component.offset;
                int j1 = Math.min(k, bytebuf.capacity() - (i - i1));

                bytebuf.setBytes(i - i1, abyte, j, j1);
                i += j1;
                j += j1;
                k -= j1;
            }

            return this;
        }
    }

    @Override
    public CompositeByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        int j = bytebuffer.limit();
        int k = bytebuffer.remaining();

        this.checkIndex(i, k);
        if (k == 0) {
            return this;
        } else {
            int l = this.toComponentIndex(i);

            try {
                while (k > 0) {
                    CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(l);
                    ByteBuf bytebuf = compositebytebuf_component.buf;
                    int i1 = compositebytebuf_component.offset;
                    int j1 = Math.min(k, bytebuf.capacity() - (i - i1));

                    bytebuffer.limit(bytebuffer.position() + j1);
                    bytebuf.setBytes(i - i1, bytebuffer);
                    i += j1;
                    k -= j1;
                    ++l;
                }
            } finally {
                bytebuffer.limit(j);
            }

            return this;
        }
    }

    @Override
    public CompositeByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkSrcIndex(i, k, j, bytebuf.capacity());
        if (k == 0) {
            return this;
        } else {
            for (int l = this.toComponentIndex(i); k > 0; ++l) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(l);
                ByteBuf bytebuf1 = compositebytebuf_component.buf;
                int i1 = compositebytebuf_component.offset;
                int j1 = Math.min(k, bytebuf1.capacity() - (i - i1));

                bytebuf1.setBytes(i - i1, bytebuf, j, j1);
                i += j1;
                j += j1;
                k -= j1;
            }

            return this;
        }
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        this.checkIndex(i, j);
        if (j == 0) {
            return inputstream.read(EmptyArrays.EMPTY_BYTES);
        } else {
            int k = this.toComponentIndex(i);
            int l = 0;

            do {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(k);
                ByteBuf bytebuf = compositebytebuf_component.buf;
                int i1 = compositebytebuf_component.offset;
                int j1 = Math.min(j, bytebuf.capacity() - (i - i1));
                int k1 = bytebuf.setBytes(i - i1, inputstream, j1);

                if (k1 < 0) {
                    if (l == 0) {
                        return -1;
                    }
                    break;
                }

                if (k1 == j1) {
                    i += j1;
                    j -= j1;
                    l += j1;
                    ++k;
                } else {
                    i += k1;
                    j -= k1;
                    l += k1;
                }
            } while (j > 0);

            return l;
        }
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        this.checkIndex(i, j);
        if (j == 0) {
            return scatteringbytechannel.read(CompositeByteBuf.FULL_BYTEBUFFER);
        } else {
            int k = this.toComponentIndex(i);
            int l = 0;

            do {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(k);
                ByteBuf bytebuf = compositebytebuf_component.buf;
                int i1 = compositebytebuf_component.offset;
                int j1 = Math.min(j, bytebuf.capacity() - (i - i1));
                int k1 = bytebuf.setBytes(i - i1, scatteringbytechannel, j1);

                if (k1 == 0) {
                    break;
                }

                if (k1 < 0) {
                    if (l == 0) {
                        return -1;
                    }
                    break;
                }

                if (k1 == j1) {
                    i += j1;
                    j -= j1;
                    l += j1;
                    ++k;
                } else {
                    i += k1;
                    j -= k1;
                    l += k1;
                }
            } while (j > 0);

            return l;
        }
    }

    @Override
    public ByteBuf copy(int i, int j) {
        this.checkIndex(i, j);
        ByteBuf bytebuf = Unpooled.buffer(j);

        if (j != 0) {
            this.copyTo(i, j, this.toComponentIndex(i), bytebuf);
        }

        return bytebuf;
    }

    private void copyTo(int i, int j, int k, ByteBuf bytebuf) {
        int l = 0;

        for (int i1 = k; j > 0; ++i1) {
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(i1);
            ByteBuf bytebuf1 = compositebytebuf_component.buf;
            int j1 = compositebytebuf_component.offset;
            int k1 = Math.min(j, bytebuf1.capacity() - (i - j1));

            bytebuf1.getBytes(i - j1, bytebuf, l, k1);
            i += k1;
            l += k1;
            j -= k1;
        }

        bytebuf.writerIndex(bytebuf.capacity());
    }

    public ByteBuf component(int i) {
        return this.internalComponent(i).duplicate();
    }

    public ByteBuf componentAtOffset(int i) {
        return this.internalComponentAtOffset(i).duplicate();
    }

    public ByteBuf internalComponent(int i) {
        this.checkComponentIndex(i);
        return ((CompositeByteBuf.Component) this.components.get(i)).buf;
    }

    public ByteBuf internalComponentAtOffset(int i) {
        return this.findComponent(i).buf;
    }

    private CompositeByteBuf.Component findComponent(int i) {
        assert !this.freed;

        this.checkIndex(i);

        assert !this.freed;

        this.checkIndex(i);
        int j = 0;
        int k = this.components.size();

        while (j <= k) {
            int l = j + k >>> 1;
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(l);

            if (i >= compositebytebuf_component.endOffset) {
                j = l + 1;
            } else {
                if (i >= compositebytebuf_component.offset) {
                    return compositebytebuf_component;
                }

                k = l - 1;
            }
        }

        throw new Error("should not reach here");
    }

    @Override
    public int nioBufferCount() {
        if (this.components.size() == 1) {
            return ((CompositeByteBuf.Component) this.components.get(0)).buf.nioBufferCount();
        } else {
            int i = 0;
            int j = this.components.size();

            for (int k = 0; k < j; ++k) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(k);

                i += compositebytebuf_component.buf.nioBufferCount();
            }

            return i;
        }
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        if (this.components.size() == 1) {
            return ((CompositeByteBuf.Component) this.components.get(0)).buf.internalNioBuffer(i, j);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        if (this.components.size() == 1) {
            ByteBuf bytebuf = ((CompositeByteBuf.Component) this.components.get(0)).buf;

            if (bytebuf.nioBufferCount() == 1) {
                return ((CompositeByteBuf.Component) this.components.get(0)).buf.nioBuffer(i, j);
            }
        }

        ByteBuffer bytebuffer = ByteBuffer.allocate(j).order(this.order());
        ByteBuffer[] abytebuffer = this.nioBuffers(i, j);

        for (int k = 0; k < abytebuffer.length; ++k) {
            bytebuffer.put(abytebuffer[k]);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        this.checkIndex(i, j);
        if (j == 0) {
            return EmptyArrays.EMPTY_BYTE_BUFFERS;
        } else {
            ArrayList arraylist = new ArrayList(this.components.size());

            for (int k = this.toComponentIndex(i); j > 0; ++k) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(k);
                ByteBuf bytebuf = compositebytebuf_component.buf;
                int l = compositebytebuf_component.offset;
                int i1 = Math.min(j, bytebuf.capacity() - (i - l));

                switch (bytebuf.nioBufferCount()) {
                    case 0:
                        throw new UnsupportedOperationException();
                    case 1:
                        arraylist.add(bytebuf.nioBuffer(i - l, i1));
                        break;
                    default:
                        Collections.addAll(arraylist, bytebuf.nioBuffers(i - l, i1));
                }

                i += i1;
                j -= i1;
            }

            return (ByteBuffer[]) arraylist.toArray(new ByteBuffer[arraylist.size()]);
        }
    }

    public CompositeByteBuf consolidate() {
        assert !this.freed;

        int i = this.numComponents();

        if (i <= 1) {
            return this;
        } else {
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(i - 1);
            int j = compositebytebuf_component.endOffset;
            ByteBuf bytebuf = this.allocBuffer(j);

            for (int k = 0; k < i; ++k) {
                CompositeByteBuf.Component compositebytebuf_component1 = (CompositeByteBuf.Component) this.components.get(k);
                ByteBuf bytebuf1 = compositebytebuf_component1.buf;

                bytebuf.writeBytes(bytebuf1);
                compositebytebuf_component1.freeIfNecessary();
            }

            this.components.clear();
            this.components.add(new CompositeByteBuf.Component(bytebuf));
            this.updateComponentOffsets(0);
            return this;
        }
    }

    public CompositeByteBuf consolidate(int i, int j) {
        this.checkComponentIndex(i, j);
        if (j <= 1) {
            return this;
        } else {
            int k = i + j;
            CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(k - 1);
            int l = compositebytebuf_component.endOffset - ((CompositeByteBuf.Component) this.components.get(i)).offset;
            ByteBuf bytebuf = this.allocBuffer(l);

            for (int i1 = i; i1 < k; ++i1) {
                CompositeByteBuf.Component compositebytebuf_component1 = (CompositeByteBuf.Component) this.components.get(i1);
                ByteBuf bytebuf1 = compositebytebuf_component1.buf;

                bytebuf.writeBytes(bytebuf1);
                compositebytebuf_component1.freeIfNecessary();
            }

            this.components.subList(i + 1, k).clear();
            this.components.set(i, new CompositeByteBuf.Component(bytebuf));
            this.updateComponentOffsets(i);
            return this;
        }
    }

    public CompositeByteBuf discardReadComponents() {
        assert !this.freed;

        int i = this.readerIndex();

        if (i == 0) {
            return this;
        } else {
            int j = this.writerIndex();
            CompositeByteBuf.Component compositebytebuf_component;

            if (i == j && j == this.capacity()) {
                Iterator iterator = this.components.iterator();

                while (iterator.hasNext()) {
                    compositebytebuf_component = (CompositeByteBuf.Component) iterator.next();
                    compositebytebuf_component.freeIfNecessary();
                }

                this.components.clear();
                this.setIndex(0, 0);
                this.adjustMarkers(i);
                return this;
            } else {
                int k = this.toComponentIndex(i);

                for (int l = 0; l < k; ++l) {
                    ((CompositeByteBuf.Component) this.components.get(l)).freeIfNecessary();
                }

                this.components.subList(0, k).clear();
                compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(0);
                this.updateComponentOffsets(0);
                this.setIndex(i - compositebytebuf_component.offset, j - compositebytebuf_component.offset);
                this.adjustMarkers(compositebytebuf_component.offset);
                return this;
            }
        }
    }

    @Override
    public CompositeByteBuf discardReadBytes() {
        assert !this.freed;

        int i = this.readerIndex();

        if (i == 0) {
            return this;
        } else {
            int j = this.writerIndex();
            CompositeByteBuf.Component compositebytebuf_component;

            if (i == j && j == this.capacity()) {
                Iterator iterator = this.components.iterator();

                while (iterator.hasNext()) {
                    compositebytebuf_component = (CompositeByteBuf.Component) iterator.next();
                    compositebytebuf_component.freeIfNecessary();
                }

                this.components.clear();
                this.setIndex(0, 0);
                this.adjustMarkers(i);
                return this;
            } else {
                int k = this.toComponentIndex(i);

                for (int l = 0; l < k; ++l) {
                    ((CompositeByteBuf.Component) this.components.get(l)).freeIfNecessary();
                }

                this.components.subList(0, k).clear();
                compositebytebuf_component = (CompositeByteBuf.Component) this.components.get(0);
                int i1 = i - compositebytebuf_component.offset;

                if (i1 == compositebytebuf_component.length) {
                    this.components.remove(0);
                } else {
                    CompositeByteBuf.Component compositebytebuf_component1 = new CompositeByteBuf.Component(compositebytebuf_component.buf.slice(i1, compositebytebuf_component.length - i1));

                    this.components.set(0, compositebytebuf_component1);
                }

                this.updateComponentOffsets(0);
                this.setIndex(0, j - i);
                this.adjustMarkers(i);
                return this;
            }
        }
    }

    private ByteBuf allocBuffer(int i) {
        return this.direct ? this.alloc().directBuffer(i) : this.alloc().heapBuffer(i);
    }

    @Override
    public String toString() {
        String s = super.toString();

        s = s.substring(0, s.length() - 1);
        return s + ", components=" + this.components.size() + ')';
    }

    @Override
    public CompositeByteBuf readerIndex(int i) {
        return (CompositeByteBuf) super.readerIndex(i);
    }

    @Override
    public CompositeByteBuf writerIndex(int i) {
        return (CompositeByteBuf) super.writerIndex(i);
    }

    @Override
    public CompositeByteBuf setIndex(int i, int j) {
        return (CompositeByteBuf) super.setIndex(i, j);
    }

    @Override
    public CompositeByteBuf clear() {
        return (CompositeByteBuf) super.clear();
    }

    @Override
    public CompositeByteBuf markReaderIndex() {
        return (CompositeByteBuf) super.markReaderIndex();
    }

    @Override
    public CompositeByteBuf resetReaderIndex() {
        return (CompositeByteBuf) super.resetReaderIndex();
    }

    @Override
    public CompositeByteBuf markWriterIndex() {
        return (CompositeByteBuf) super.markWriterIndex();
    }

    @Override
    public CompositeByteBuf resetWriterIndex() {
        return (CompositeByteBuf) super.resetWriterIndex();
    }

    @Override
    public CompositeByteBuf ensureWritable(int i) {
        return (CompositeByteBuf) super.ensureWritable(i);
    }

    @Override
    public CompositeByteBuf getBytes(int i, ByteBuf bytebuf) {
        return (CompositeByteBuf) super.getBytes(i, bytebuf);
    }

    @Override
    public CompositeByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        return (CompositeByteBuf) super.getBytes(i, bytebuf, j);
    }

    @Override
    public CompositeByteBuf getBytes(int i, byte[] abyte) {
        return (CompositeByteBuf) super.getBytes(i, abyte);
    }

    @Override
    public CompositeByteBuf setBoolean(int i, boolean flag) {
        return (CompositeByteBuf) super.setBoolean(i, flag);
    }

    @Override
    public CompositeByteBuf setChar(int i, int j) {
        return (CompositeByteBuf) super.setChar(i, j);
    }

    @Override
    public CompositeByteBuf setFloat(int i, float f) {
        return (CompositeByteBuf) super.setFloat(i, f);
    }

    @Override
    public CompositeByteBuf setDouble(int i, double d0) {
        return (CompositeByteBuf) super.setDouble(i, d0);
    }

    @Override
    public CompositeByteBuf setBytes(int i, ByteBuf bytebuf) {
        return (CompositeByteBuf) super.setBytes(i, bytebuf);
    }

    @Override
    public CompositeByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        return (CompositeByteBuf) super.setBytes(i, bytebuf, j);
    }

    @Override
    public CompositeByteBuf setBytes(int i, byte[] abyte) {
        return (CompositeByteBuf) super.setBytes(i, abyte);
    }

    @Override
    public CompositeByteBuf setZero(int i, int j) {
        return (CompositeByteBuf) super.setZero(i, j);
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf bytebuf) {
        return (CompositeByteBuf) super.readBytes(bytebuf);
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf bytebuf, int i) {
        return (CompositeByteBuf) super.readBytes(bytebuf, i);
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        return (CompositeByteBuf) super.readBytes(bytebuf, i, j);
    }

    @Override
    public CompositeByteBuf readBytes(byte[] abyte) {
        return (CompositeByteBuf) super.readBytes(abyte);
    }

    @Override
    public CompositeByteBuf readBytes(byte[] abyte, int i, int j) {
        return (CompositeByteBuf) super.readBytes(abyte, i, j);
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuffer bytebuffer) {
        return (CompositeByteBuf) super.readBytes(bytebuffer);
    }

    @Override
    public CompositeByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        return (CompositeByteBuf) super.readBytes(outputstream, i);
    }

    @Override
    public CompositeByteBuf skipBytes(int i) {
        return (CompositeByteBuf) super.skipBytes(i);
    }

    @Override
    public CompositeByteBuf writeBoolean(boolean flag) {
        return (CompositeByteBuf) super.writeBoolean(flag);
    }

    @Override
    public CompositeByteBuf writeByte(int i) {
        return (CompositeByteBuf) super.writeByte(i);
    }

    @Override
    public CompositeByteBuf writeShort(int i) {
        return (CompositeByteBuf) super.writeShort(i);
    }

    @Override
    public CompositeByteBuf writeMedium(int i) {
        return (CompositeByteBuf) super.writeMedium(i);
    }

    @Override
    public CompositeByteBuf writeInt(int i) {
        return (CompositeByteBuf) super.writeInt(i);
    }

    @Override
    public CompositeByteBuf writeLong(long i) {
        return (CompositeByteBuf) super.writeLong(i);
    }

    @Override
    public CompositeByteBuf writeChar(int i) {
        return (CompositeByteBuf) super.writeChar(i);
    }

    @Override
    public CompositeByteBuf writeFloat(float f) {
        return (CompositeByteBuf) super.writeFloat(f);
    }

    @Override
    public CompositeByteBuf writeDouble(double d0) {
        return (CompositeByteBuf) super.writeDouble(d0);
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf bytebuf) {
        return (CompositeByteBuf) super.writeBytes(bytebuf);
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf bytebuf, int i) {
        return (CompositeByteBuf) super.writeBytes(bytebuf, i);
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        return (CompositeByteBuf) super.writeBytes(bytebuf, i, j);
    }

    @Override
    public CompositeByteBuf writeBytes(byte[] abyte) {
        return (CompositeByteBuf) super.writeBytes(abyte);
    }

    @Override
    public CompositeByteBuf writeBytes(byte[] abyte, int i, int j) {
        return (CompositeByteBuf) super.writeBytes(abyte, i, j);
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuffer bytebuffer) {
        return (CompositeByteBuf) super.writeBytes(bytebuffer);
    }

    @Override
    public CompositeByteBuf writeZero(int i) {
        return (CompositeByteBuf) super.writeZero(i);
    }

    @Override
    public CompositeByteBuf retain(int i) {
        return (CompositeByteBuf) super.retain(i);
    }

    @Override
    public CompositeByteBuf retain() {
        return (CompositeByteBuf) super.retain();
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers(this.readerIndex(), this.readableBytes());
    }

    @Override
    public CompositeByteBuf discardSomeReadBytes() {
        return this.discardReadComponents();
    }

    @Override
    protected void deallocate() {
        if (!this.freed) {
            this.freed = true;
            Iterator iterator = this.components.iterator();

            while (iterator.hasNext()) {
                CompositeByteBuf.Component compositebytebuf_component = (CompositeByteBuf.Component) iterator.next();

                compositebytebuf_component.freeIfNecessary();
            }

            if (this.leak != null) {
                this.leak.close();
            }

        }
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    private final class Component {

        final ByteBuf buf;
        final int length;
        int offset;
        int endOffset;

        Component(ByteBuf bytebuf) {
            this.buf = bytebuf;
            this.length = bytebuf.readableBytes();
        }

        void freeIfNecessary() {
            this.buf.release();
        }
    }
}
