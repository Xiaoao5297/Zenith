package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@Beta
public final class Resources {

    private Resources() {}

    /** @deprecated */
    @Deprecated
    public static InputSupplier<InputStream> newInputStreamSupplier(URL url) {
        return ByteStreams.asInputSupplier(asByteSource(url));
    }

    public static ByteSource asByteSource(URL url) {
        return new Resources.UrlByteSource(url);
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<InputStreamReader> newReaderSupplier(URL url, Charset charset) {
        return CharStreams.asInputSupplier(asCharSource(url, charset));
    }

    public static CharSource asCharSource(URL url, Charset charset) {
        return asByteSource(url).asCharSource(charset);
    }

    public static byte[] toByteArray(URL url) throws IOException {
        return asByteSource(url).read();
    }

    public static String toString(URL url, Charset charset) throws IOException {
        return asCharSource(url, charset).read();
    }

    public static <T> T readLines(URL url, Charset charset, LineProcessor<T> lineprocessor) throws IOException {
        return CharStreams.readLines(newReaderSupplier(url, charset), lineprocessor);
    }

    public static List<String> readLines(URL url, Charset charset) throws IOException {
        return (List) readLines(url, charset, new LineProcessor<List<String>>() {
            final List<String> result = Lists.newArrayList();

            @Override
            public boolean processLine(String s) {
                this.result.add(s);
                return true;
            }

            @Override
            public List<String> getResult() {
                return this.result;
            }
        });
    }

    public static void copy(URL url, OutputStream outputstream) throws IOException {
        asByteSource(url).copyTo(outputstream);
    }

    public static URL getResource(String s) {
        ClassLoader classloader = (ClassLoader) Objects.firstNonNull(Thread.currentThread().getContextClassLoader(), Resources.class.getClassLoader());
        URL url = classloader.getResource(s);

        Preconditions.checkArgument(url != null, "resource %s not found.", s);
        return url;
    }

    public static URL getResource(Class<?> oclass, String s) {
        URL url = oclass.getResource(s);

        Preconditions.checkArgument(url != null, "resource %s relative to %s not found.", s, oclass.getName());
        return url;
    }

    private static final class UrlByteSource extends ByteSource {

        private final URL url;

        private UrlByteSource(URL url) {
            this.url = (URL) Preconditions.checkNotNull(url);
        }

        @Override
        public InputStream openStream() throws IOException {
            return this.url.openStream();
        }

        public String toString() {
            return "Resources.asByteSource(" + this.url + ")";
        }
    }
}
