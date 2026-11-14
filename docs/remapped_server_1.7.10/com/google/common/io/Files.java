package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Beta
public final class Files {

    private static final int TEMP_DIR_ATTEMPTS = 10000;
    private static final TreeTraverser<File> FILE_TREE_TRAVERSER = new TreeTraverser<File>() {
        public Iterable<File> children(File file) {
            if (file.isDirectory()) {
                File[] afile = file.listFiles();

                if (afile != null) {
                    return Collections.unmodifiableList(Arrays.asList(afile));
                }
            }

            return Collections.emptyList();
        }

        public String toString() {
            return "Files.fileTreeTraverser()";
        }
    };

    private Files() {}

    public static BufferedReader newReader(File file, Charset charset) throws FileNotFoundException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(charset);
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    }

    public static BufferedWriter newWriter(File file, Charset charset) throws FileNotFoundException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(charset);
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
    }

    public static ByteSource asByteSource(File file) {
        return new Files.FileByteSource(file);
    }

    static byte[] readFile(InputStream inputstream, long i) throws IOException {
        if (i > 2147483647L) {
            throw new OutOfMemoryError("file is too large to fit in a byte array: " + i + " bytes");
        } else {
            return i == 0L ? ByteStreams.toByteArray(inputstream) : ByteStreams.toByteArray(inputstream, (int) i);
        }
    }

    public static ByteSink asByteSink(File file, FileWriteMode... afilewritemode) {
        return new Files.FileByteSink(file, afilewritemode);
    }

    public static CharSource asCharSource(File file, Charset charset) {
        return asByteSource(file).asCharSource(charset);
    }

    public static CharSink asCharSink(File file, Charset charset, FileWriteMode... afilewritemode) {
        return asByteSink(file, afilewritemode).asCharSink(charset);
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<FileInputStream> newInputStreamSupplier(File file) {
        return ByteStreams.asInputSupplier(asByteSource(file));
    }

    /** @deprecated */
    @Deprecated
    public static OutputSupplier<FileOutputStream> newOutputStreamSupplier(File file) {
        return newOutputStreamSupplier(file, false);
    }

    /** @deprecated */
    @Deprecated
    public static OutputSupplier<FileOutputStream> newOutputStreamSupplier(File file, boolean flag) {
        return ByteStreams.asOutputSupplier(asByteSink(file, modes(flag)));
    }

    private static FileWriteMode[] modes(boolean flag) {
        return flag ? new FileWriteMode[]{FileWriteMode.APPEND} : new FileWriteMode[0];
    }

    /** @deprecated */
    @Deprecated
    public static InputSupplier<InputStreamReader> newReaderSupplier(File file, Charset charset) {
        return CharStreams.asInputSupplier(asCharSource(file, charset));
    }

    /** @deprecated */
    @Deprecated
    public static OutputSupplier<OutputStreamWriter> newWriterSupplier(File file, Charset charset) {
        return newWriterSupplier(file, charset, false);
    }

    /** @deprecated */
    @Deprecated
    public static OutputSupplier<OutputStreamWriter> newWriterSupplier(File file, Charset charset, boolean flag) {
        return CharStreams.asOutputSupplier(asCharSink(file, charset, modes(flag)));
    }

    public static byte[] toByteArray(File file) throws IOException {
        return asByteSource(file).read();
    }

    public static String toString(File file, Charset charset) throws IOException {
        return asCharSource(file, charset).read();
    }

    /** @deprecated */
    @Deprecated
    public static void copy(InputSupplier<? extends InputStream> inputsupplier, File file) throws IOException {
        ByteStreams.asByteSource(inputsupplier).copyTo(asByteSink(file));
    }

    public static void write(byte[] abyte, File file) throws IOException {
        asByteSink(file).write(abyte);
    }

    /** @deprecated */
    @Deprecated
    public static void copy(File file, OutputSupplier<? extends OutputStream> outputsupplier) throws IOException {
        asByteSource(file).copyTo(ByteStreams.asByteSink(outputsupplier));
    }

    public static void copy(File file, OutputStream outputstream) throws IOException {
        asByteSource(file).copyTo(outputstream);
    }

    public static void copy(File file, File file1) throws IOException {
        Preconditions.checkArgument(!file.equals(file1), "Source %s and destination %s must be different", file, file1);
        asByteSource(file).copyTo(asByteSink(file1));
    }

    /** @deprecated */
    @Deprecated
    public static <R extends Readable & Closeable> void copy(InputSupplier<R> inputsupplier, File file, Charset charset) throws IOException {
        CharStreams.asCharSource(inputsupplier).copyTo(asCharSink(file, charset));
    }

    public static void write(CharSequence charsequence, File file, Charset charset) throws IOException {
        asCharSink(file, charset).write(charsequence);
    }

    public static void append(CharSequence charsequence, File file, Charset charset) throws IOException {
        write(charsequence, file, charset, true);
    }

    private static void write(CharSequence charsequence, File file, Charset charset, boolean flag) throws IOException {
        asCharSink(file, charset, modes(flag)).write(charsequence);
    }

    /** @deprecated */
    @Deprecated
    public static <W extends Appendable & Closeable> void copy(File file, Charset charset, OutputSupplier<W> outputsupplier) throws IOException {
        asCharSource(file, charset).copyTo(CharStreams.asCharSink(outputsupplier));
    }

    public static void copy(File file, Charset charset, Appendable appendable) throws IOException {
        asCharSource(file, charset).copyTo(appendable);
    }

    public static boolean equal(File file, File file1) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(file1);
        if (file != file1 && !file.equals(file1)) {
            long i = file.length();
            long j = file1.length();

            return i != 0L && j != 0L && i != j ? false : asByteSource(file).contentEquals(asByteSource(file1));
        } else {
            return true;
        }
    }

    public static File createTempDir() {
        File file = new File(System.getProperty("java.io.tmpdir"));
        String s = System.currentTimeMillis() + "-";

        for (int i = 0; i < 10000; ++i) {
            File file1 = new File(file, s + i);

            if (file1.mkdir()) {
                return file1;
            }
        }

        throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + s + "0 to " + s + 9999 + ')');
    }

    public static void touch(File file) throws IOException {
        Preconditions.checkNotNull(file);
        if (!file.createNewFile() && !file.setLastModified(System.currentTimeMillis())) {
            throw new IOException("Unable to update modification time of " + file);
        }
    }

    public static void createParentDirs(File file) throws IOException {
        Preconditions.checkNotNull(file);
        File file1 = file.getCanonicalFile().getParentFile();

        if (file1 != null) {
            file1.mkdirs();
            if (!file1.isDirectory()) {
                throw new IOException("Unable to create parent directories of " + file);
            }
        }
    }

    public static void move(File file, File file1) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(file1);
        Preconditions.checkArgument(!file.equals(file1), "Source %s and destination %s must be different", file, file1);
        if (!file.renameTo(file1)) {
            copy(file, file1);
            if (!file.delete()) {
                if (!file1.delete()) {
                    throw new IOException("Unable to delete " + file1);
                }

                throw new IOException("Unable to delete " + file);
            }
        }

    }

    public static String readFirstLine(File file, Charset charset) throws IOException {
        return asCharSource(file, charset).readFirstLine();
    }

    public static List<String> readLines(File file, Charset charset) throws IOException {
        return (List) readLines(file, charset, new LineProcessor<List<String>>() {
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

    public static <T> T readLines(File file, Charset charset, LineProcessor<T> lineprocessor) throws IOException {
        return CharStreams.readLines(newReaderSupplier(file, charset), lineprocessor);
    }

    public static <T> T readBytes(File file, ByteProcessor<T> byteprocessor) throws IOException {
        return ByteStreams.readBytes(newInputStreamSupplier(file), byteprocessor);
    }

    public static HashCode hash(File file, HashFunction hashfunction) throws IOException {
        return asByteSource(file).hash(hashfunction);
    }

    public static MappedByteBuffer map(File file) throws IOException {
        Preconditions.checkNotNull(file);
        return map(file, MapMode.READ_ONLY);
    }

    public static MappedByteBuffer map(File file, MapMode mapmode) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(mapmode);
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        } else {
            return map(file, mapmode, file.length());
        }
    }

    public static MappedByteBuffer map(File file, MapMode mapmode, long i) throws FileNotFoundException, IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(mapmode);
        Closer closer = Closer.create();

        MappedByteBuffer mappedbytebuffer;

        try {
            RandomAccessFile randomaccessfile = (RandomAccessFile) closer.register(new RandomAccessFile(file, mapmode == MapMode.READ_ONLY ? "r" : "rw"));

            mappedbytebuffer = map(randomaccessfile, mapmode, i);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return mappedbytebuffer;
    }

    private static MappedByteBuffer map(RandomAccessFile randomaccessfile, MapMode mapmode, long i) throws IOException {
        Closer closer = Closer.create();

        MappedByteBuffer mappedbytebuffer;

        try {
            FileChannel filechannel = (FileChannel) closer.register(randomaccessfile.getChannel());

            mappedbytebuffer = filechannel.map(mapmode, 0L, i);
        } catch (Throwable throwable) {
            throw closer.rethrow(throwable);
        } finally {
            closer.close();
        }

        return mappedbytebuffer;
    }

    public static String simplifyPath(String s) {
        Preconditions.checkNotNull(s);
        if (s.length() == 0) {
            return ".";
        } else {
            Iterable iterable = Splitter.on('/').omitEmptyStrings().split(s);
            ArrayList arraylist = new ArrayList();
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();

                if (!s1.equals(".")) {
                    if (s1.equals("..")) {
                        if (arraylist.size() > 0 && !((String) arraylist.get(arraylist.size() - 1)).equals("..")) {
                            arraylist.remove(arraylist.size() - 1);
                        } else {
                            arraylist.add("..");
                        }
                    } else {
                        arraylist.add(s1);
                    }
                }
            }

            String s2 = Joiner.on('/').join((Iterable) arraylist);

            if (s.charAt(0) == '/') {
                s2 = "/" + s2;
            }

            while (s2.startsWith("/../")) {
                s2 = s2.substring(3);
            }

            if (s2.equals("/..")) {
                s2 = "/";
            } else if ("".equals(s2)) {
                s2 = ".";
            }

            return s2;
        }
    }

    public static String getFileExtension(String s) {
        Preconditions.checkNotNull(s);
        String s1 = (new File(s)).getName();
        int i = s1.lastIndexOf(46);

        return i == -1 ? "" : s1.substring(i + 1);
    }

    public static String getNameWithoutExtension(String s) {
        Preconditions.checkNotNull(s);
        String s1 = (new File(s)).getName();
        int i = s1.lastIndexOf(46);

        return i == -1 ? s1 : s1.substring(0, i);
    }

    public static TreeTraverser<File> fileTreeTraverser() {
        return Files.FILE_TREE_TRAVERSER;
    }

    public static Predicate<File> isDirectory() {
        return Files.FilePredicate.IS_DIRECTORY;
    }

    public static Predicate<File> isFile() {
        return Files.FilePredicate.IS_FILE;
    }

    private static enum FilePredicate implements Predicate<File> {

        IS_DIRECTORY {
            public boolean apply(File file) {
                return file.isDirectory();
            }

            public String toString() {
                return "Files.isDirectory()";
            }
        },
        IS_FILE {
            public boolean apply(File file) {
                return file.isFile();
            }

            public String toString() {
                return "Files.isFile()";
            }
        };

        private FilePredicate() {}
    }

    private static final class FileByteSink extends ByteSink {

        private final File file;
        private final ImmutableSet<FileWriteMode> modes;

        private FileByteSink(File file, FileWriteMode... afilewritemode) {
            this.file = (File) Preconditions.checkNotNull(file);
            this.modes = ImmutableSet.copyOf((Object[]) afilewritemode);
        }

        @Override
        public FileOutputStream openStream() throws IOException {
            return new FileOutputStream(this.file, this.modes.contains(FileWriteMode.APPEND));
        }

        public String toString() {
            return "Files.asByteSink(" + this.file + ", " + this.modes + ")";
        }
    }

    private static final class FileByteSource extends ByteSource {

        private final File file;

        private FileByteSource(File file) {
            this.file = (File) Preconditions.checkNotNull(file);
        }

        @Override
        public FileInputStream openStream() throws IOException {
            return new FileInputStream(this.file);
        }

        @Override
        public long size() throws IOException {
            if (!this.file.isFile()) {
                throw new FileNotFoundException(this.file.toString());
            } else {
                return this.file.length();
            }
        }

        @Override
        public byte[] read() throws IOException {
            Closer closer = Closer.create();

            byte[] abyte;

            try {
                FileInputStream fileinputstream = (FileInputStream) closer.register(this.openStream());

                abyte = Files.readFile(fileinputstream, fileinputstream.getChannel().size());
            } catch (Throwable throwable) {
                throw closer.rethrow(throwable);
            } finally {
                closer.close();
            }

            return abyte;
        }

        public String toString() {
            return "Files.asByteSource(" + this.file + ")";
        }
    }
}
