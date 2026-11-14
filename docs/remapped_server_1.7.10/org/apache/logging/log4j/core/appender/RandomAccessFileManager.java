package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;

public class RandomAccessFileManager extends OutputStreamManager {

    static final int DEFAULT_BUFFER_SIZE = 262144;
    private static final RandomAccessFileManager.RandomAccessFileManagerFactory FACTORY = new RandomAccessFileManager.RandomAccessFileManagerFactory();
    private final boolean isImmediateFlush;
    private final String advertiseURI;
    private final RandomAccessFile randomAccessFile;
    private final ByteBuffer buffer;
    private final ThreadLocal<Boolean> isEndOfBatch = new ThreadLocal();

    protected RandomAccessFileManager(RandomAccessFile randomaccessfile, String s, OutputStream outputstream, boolean flag, String s1, Layout<? extends Serializable> layout) {
        super(outputstream, s, layout);
        this.isImmediateFlush = flag;
        this.randomAccessFile = randomaccessfile;
        this.advertiseURI = s1;
        this.isEndOfBatch.set(Boolean.FALSE);
        this.buffer = ByteBuffer.allocate(262144);
    }

    public static RandomAccessFileManager getFileManager(String s, boolean flag, boolean flag1, String s1, Layout<? extends Serializable> layout) {
        return (RandomAccessFileManager) getManager(s, (Object) (new RandomAccessFileManager.FactoryData(flag, flag1, s1, layout)), (ManagerFactory) RandomAccessFileManager.FACTORY);
    }

    public Boolean isEndOfBatch() {
        return (Boolean) this.isEndOfBatch.get();
    }

    public void setEndOfBatch(boolean flag) {
        this.isEndOfBatch.set(flag);
    }

    @Override
    protected synchronized void write(byte[] abyte, int i, int j) {
        super.write(abyte, i, j);
        boolean flag = false;

        do {
            if (j > this.buffer.remaining()) {
                this.flush();
            }

            int k = Math.min(j, this.buffer.remaining());

            this.buffer.put(abyte, i, k);
            i += k;
            j -= k;
        } while (j > 0);

        if (this.isImmediateFlush || this.isEndOfBatch.get() == Boolean.TRUE) {
            this.flush();
        }

    }

    @Override
    public synchronized void flush() {
        this.buffer.flip();

        try {
            this.randomAccessFile.write(this.buffer.array(), 0, this.buffer.limit());
        } catch (IOException ioexception) {
            String s = "Error writing to RandomAccessFile " + this.getName();

            throw new AppenderLoggingException(s, ioexception);
        }

        this.buffer.clear();
    }

    @Override
    public synchronized void close() {
        this.flush();

        try {
            this.randomAccessFile.close();
        } catch (IOException ioexception) {
            RandomAccessFileManager.LOGGER.error("Unable to close RandomAccessFile " + this.getName() + ". " + ioexception);
        }

    }

    public String getFileName() {
        return this.getName();
    }

    @Override
    public Map<String, String> getContentFormat() {
        HashMap hashmap = new HashMap(super.getContentFormat());

        hashmap.put("fileURI", this.advertiseURI);
        return hashmap;
    }

    private static class RandomAccessFileManagerFactory implements ManagerFactory<RandomAccessFileManager, RandomAccessFileManager.FactoryData> {

        private RandomAccessFileManagerFactory() {}

        public RandomAccessFileManager createManager(String s, RandomAccessFileManager.FactoryData randomaccessfilemanager_factorydata) {
            File file = new File(s);
            File file1 = file.getParentFile();

            if (null != file1 && !file1.exists()) {
                file1.mkdirs();
            }

            if (!randomaccessfilemanager_factorydata.append) {
                file.delete();
            }

            RandomAccessFileManager.DummyOutputStream randomaccessfilemanager_dummyoutputstream = new RandomAccessFileManager.DummyOutputStream();

            try {
                RandomAccessFile randomaccessfile = new RandomAccessFile(s, "rw");

                if (randomaccessfilemanager_factorydata.append) {
                    randomaccessfile.seek(randomaccessfile.length());
                } else {
                    randomaccessfile.setLength(0L);
                }

                return new RandomAccessFileManager(randomaccessfile, s, randomaccessfilemanager_dummyoutputstream, randomaccessfilemanager_factorydata.immediateFlush, randomaccessfilemanager_factorydata.advertiseURI, randomaccessfilemanager_factorydata.layout);
            } catch (Exception exception) {
                AbstractManager.LOGGER.error("RandomAccessFileManager (" + s + ") " + exception);
                return null;
            }
        }
    }

    private static class FactoryData {

        private final boolean append;
        private final boolean immediateFlush;
        private final String advertiseURI;
        private final Layout<? extends Serializable> layout;

        public FactoryData(boolean flag, boolean flag1, String s, Layout<? extends Serializable> layout) {
            this.append = flag;
            this.immediateFlush = flag1;
            this.advertiseURI = s;
            this.layout = layout;
        }
    }

    static class DummyOutputStream extends OutputStream {

        DummyOutputStream() {}

        public void write(int i) throws IOException {}

        public void write(byte[] abyte, int i, int j) throws IOException {}
    }
}
