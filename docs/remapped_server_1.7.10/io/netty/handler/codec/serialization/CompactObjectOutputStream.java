package io.netty.handler.codec.serialization;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

class CompactObjectOutputStream extends ObjectOutputStream {

    static final int TYPE_FAT_DESCRIPTOR = 0;
    static final int TYPE_THIN_DESCRIPTOR = 1;

    CompactObjectOutputStream(OutputStream outputstream) throws IOException {
        super(outputstream);
    }

    protected void writeStreamHeader() throws IOException {
        this.writeByte(5);
    }

    protected void writeClassDescriptor(ObjectStreamClass objectstreamclass) throws IOException {
        Class oclass = objectstreamclass.forClass();

        if (!oclass.isPrimitive() && !oclass.isArray() && !oclass.isInterface() && objectstreamclass.getSerialVersionUID() != 0L) {
            this.write(1);
            this.writeUTF(objectstreamclass.getName());
        } else {
            this.write(0);
            super.writeClassDescriptor(objectstreamclass);
        }

    }
}
