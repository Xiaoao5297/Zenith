package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Beta
public final class FileBackedOutputStream extends OutputStream {

    private final int fileThreshold;
    private final boolean resetOnFinalize;
    private final ByteSource source;
    private OutputStream out;
    private FileBackedOutputStream.MemoryOutput memory;
    private File file;

    @VisibleForTesting
    synchronized File getFile() {
        return this.file;
    }

    public FileBackedOutputStream(int i) {
        this(i, false);
    }

    public FileBackedOutputStream(int i, boolean flag) {
        this.fileThreshold = i;
        this.resetOnFinalize = flag;
        this.memory = new FileBackedOutputStream.MemoryOutput();
        this.out = this.memory;
        if (flag) {
            this.source = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return FileBackedOutputStream.this.openInputStream();
                }

                protected void finalize() {
                    try {
                        FileBackedOutputStream.this.reset();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace(System.err);
                    }

                }
            };
        } else {
            this.source = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return FileBackedOutputStream.this.openInputStream();
                }
            };
        }

    }

    public ByteSource asByteSource() {
        return this.source;
    }

    private synchronized InputStream openInputStream() throws IOException {
        return (InputStream) (this.file != null ? new FileInputStream(this.file) : new ByteArrayInputStream(this.memory.getBuffer(), 0, this.memory.getCount()));
    }

    public synchronized void reset() throws IOException {
        boolean flag = false;

        try {
            flag = true;
            this.close();
            flag = false;
        } finally {
            if (flag) {
                if (this.memory == null) {
                    this.memory = new FileBackedOutputStream.MemoryOutput();
                } else {
                    this.memory.reset();
                }

                this.out = this.memory;
                if (this.file != null) {
                    File file = this.file;

                    this.file = null;
                    if (!file.delete()) {
                        throw new IOException("Could not delete: " + file);
                    }
                }

            }
        }

        if (this.memory == null) {
            this.memory = new FileBackedOutputStream.MemoryOutput();
        } else {
            this.memory.reset();
        }

        this.out = this.memory;
        if (this.file != null) {
            File file1 = this.file;

            this.file = null;
            if (!file1.delete()) {
                throw new IOException("Could not delete: " + file1);
            }
        }

    }

    public synchronized void write(int i) throws IOException {
        this.update(1);
        this.out.write(i);
    }

    public synchronized void write(byte[] abyte) throws IOException {
        this.write(abyte, 0, abyte.length);
    }

    public synchronized void write(byte[] abyte, int i, int j) throws IOException {
        this.update(j);
        this.out.write(abyte, i, j);
    }

    public synchronized void close() throws IOException {
        this.out.close();
    }

    public synchronized void flush() throws IOException {
        this.out.flush();
    }

    private void update(int i) throws IOException {
        if (this.file == null && this.memory.getCount() + i > this.fileThreshold) {
            File file = File.createTempFile("FileBackedOutputStream", (String) null);

            if (this.resetOnFinalize) {
                file.deleteOnExit();
            }

            FileOutputStream fileoutputstream = new FileOutputStream(file);

            fileoutputstream.write(this.memory.getBuffer(), 0, this.memory.getCount());
            fileoutputstream.flush();
            this.out = fileoutputstream;
            this.file = file;
            this.memory = null;
        }

    }

    private static class MemoryOutput extends ByteArrayOutputStream {

        private MemoryOutput() {}

        byte[] getBuffer() {
            return this.buf;
        }

        int getCount() {
            return this.count;
        }
    }
}
