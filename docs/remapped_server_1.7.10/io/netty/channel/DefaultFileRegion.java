package io.netty.channel;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class DefaultFileRegion extends AbstractReferenceCounted implements FileRegion {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
    private final FileChannel file;
    private final long position;
    private final long count;
    private long transfered;

    public DefaultFileRegion(FileChannel filechannel, long i, long j) {
        if (filechannel == null) {
            throw new NullPointerException("file");
        } else if (i < 0L) {
            throw new IllegalArgumentException("position must be >= 0 but was " + i);
        } else if (j < 0L) {
            throw new IllegalArgumentException("count must be >= 0 but was " + j);
        } else {
            this.file = filechannel;
            this.position = i;
            this.count = j;
        }
    }

    @Override
    public long position() {
        return this.position;
    }

    @Override
    public long count() {
        return this.count;
    }

    @Override
    public long transfered() {
        return this.transfered;
    }

    @Override
    public long transferTo(WritableByteChannel writablebytechannel, long i) throws IOException {
        long j = this.count - i;

        if (j >= 0L && i >= 0L) {
            if (j == 0L) {
                return 0L;
            } else {
                long k = this.file.transferTo(this.position + i, j, writablebytechannel);

                if (k > 0L) {
                    this.transfered += k;
                }

                return k;
            }
        } else {
            throw new IllegalArgumentException("position out of range: " + i + " (expected: 0 - " + (this.count - 1L) + ')');
        }
    }

    @Override
    protected void deallocate() {
        try {
            this.file.close();
        } catch (IOException ioexception) {
            if (DefaultFileRegion.logger.isWarnEnabled()) {
                DefaultFileRegion.logger.warn("Failed to close a file.", (Throwable) ioexception);
            }
        }

    }
}
