package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
public final class Funnels {

    private Funnels() {}

    public static Funnel<byte[]> byteArrayFunnel() {
        return Funnels.ByteArrayFunnel.INSTANCE;
    }

    public static Funnel<CharSequence> unencodedCharsFunnel() {
        return Funnels.UnencodedCharsFunnel.INSTANCE;
    }

    public static Funnel<CharSequence> stringFunnel(Charset charset) {
        return new Funnels.StringCharsetFunnel(charset);
    }

    public static Funnel<Integer> integerFunnel() {
        return Funnels.IntegerFunnel.INSTANCE;
    }

    public static <E> Funnel<Iterable<? extends E>> sequentialFunnel(Funnel<E> funnel) {
        return new Funnels.SequentialFunnel<>(funnel);
    }

    public static Funnel<Long> longFunnel() {
        return Funnels.LongFunnel.INSTANCE;
    }

    public static OutputStream asOutputStream(PrimitiveSink primitivesink) {
        return new Funnels.SinkAsStream(primitivesink);
    }

    private static class SinkAsStream extends OutputStream {

        final PrimitiveSink sink;

        SinkAsStream(PrimitiveSink primitivesink) {
            this.sink = (PrimitiveSink) Preconditions.checkNotNull(primitivesink);
        }

        public void write(int i) {
            this.sink.putByte((byte) i);
        }

        public void write(byte[] abyte) {
            this.sink.putBytes(abyte);
        }

        public void write(byte[] abyte, int i, int j) {
            this.sink.putBytes(abyte, i, j);
        }

        public String toString() {
            return "Funnels.asOutputStream(" + this.sink + ")";
        }
    }

    private static enum LongFunnel implements Funnel<Long> {

        INSTANCE;

        private LongFunnel() {}

        public void funnel(Long olong, PrimitiveSink primitivesink) {
            primitivesink.putLong(olong);
        }

        public String toString() {
            return "Funnels.longFunnel()";
        }
    }

    private static class SequentialFunnel<E> implements Funnel<Iterable<? extends E>>, Serializable {

        private final Funnel<E> elementFunnel;

        SequentialFunnel(Funnel<E> funnel) {
            this.elementFunnel = (Funnel) Preconditions.checkNotNull(funnel);
        }

        public void funnel(Iterable<? extends E> iterable, PrimitiveSink primitivesink) {
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                this.elementFunnel.funnel(object, primitivesink);
            }

        }

        public String toString() {
            return "Funnels.sequentialFunnel(" + this.elementFunnel + ")";
        }

        public boolean equals(@Nullable Object object) {
            if (object instanceof Funnels.SequentialFunnel) {
                Funnels.SequentialFunnel funnels_sequentialfunnel = (Funnels.SequentialFunnel) object;

                return this.elementFunnel.equals(funnels_sequentialfunnel.elementFunnel);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Funnels.SequentialFunnel.class.hashCode() ^ this.elementFunnel.hashCode();
        }
    }

    private static enum IntegerFunnel implements Funnel<Integer> {

        INSTANCE;

        private IntegerFunnel() {}

        public void funnel(Integer integer, PrimitiveSink primitivesink) {
            primitivesink.putInt(integer);
        }

        public String toString() {
            return "Funnels.integerFunnel()";
        }
    }

    private static class StringCharsetFunnel implements Funnel<CharSequence>, Serializable {

        private final Charset charset;

        StringCharsetFunnel(Charset charset) {
            this.charset = (Charset) Preconditions.checkNotNull(charset);
        }

        public void funnel(CharSequence charsequence, PrimitiveSink primitivesink) {
            primitivesink.putString(charsequence, this.charset);
        }

        public String toString() {
            return "Funnels.stringFunnel(" + this.charset.name() + ")";
        }

        public boolean equals(@Nullable Object object) {
            if (object instanceof Funnels.StringCharsetFunnel) {
                Funnels.StringCharsetFunnel funnels_stringcharsetfunnel = (Funnels.StringCharsetFunnel) object;

                return this.charset.equals(funnels_stringcharsetfunnel.charset);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Funnels.StringCharsetFunnel.class.hashCode() ^ this.charset.hashCode();
        }

        Object writeReplace() {
            return new Funnels.StringCharsetFunnel.SerializedForm(this.charset);
        }

        private static class SerializedForm implements Serializable {

            private final String charsetCanonicalName;
            private static final long serialVersionUID = 0L;

            SerializedForm(Charset charset) {
                this.charsetCanonicalName = charset.name();
            }

            private Object readResolve() {
                return Funnels.stringFunnel(Charset.forName(this.charsetCanonicalName));
            }
        }
    }

    private static enum UnencodedCharsFunnel implements Funnel<CharSequence> {

        INSTANCE;

        private UnencodedCharsFunnel() {}

        public void funnel(CharSequence charsequence, PrimitiveSink primitivesink) {
            primitivesink.putUnencodedChars(charsequence);
        }

        public String toString() {
            return "Funnels.unencodedCharsFunnel()";
        }
    }

    private static enum ByteArrayFunnel implements Funnel<byte[]> {

        INSTANCE;

        private ByteArrayFunnel() {}

        public void funnel(byte[] abyte, PrimitiveSink primitivesink) {
            primitivesink.putBytes(abyte);
        }

        public String toString() {
            return "Funnels.byteArrayFunnel()";
        }
    }
}
