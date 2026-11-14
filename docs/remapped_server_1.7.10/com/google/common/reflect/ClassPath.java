package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@Beta
public final class ClassPath {

    private static final Logger logger = Logger.getLogger(ClassPath.class.getName());
    private static final Predicate<ClassPath.ClassInfo> IS_TOP_LEVEL = new Predicate<ClassPath.ClassInfo>() {
        public boolean apply(ClassPath.ClassInfo classpath_classinfo) {
            return classpath_classinfo.className.indexOf(36) == -1;
        }
    };
    private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
    private static final String CLASS_FILE_NAME_EXTENSION = ".class";
    private final ImmutableSet<ClassPath.ResourceInfo> resources;

    private ClassPath(ImmutableSet<ClassPath.ResourceInfo> immutableset) {
        this.resources = immutableset;
    }

    public static ClassPath from(ClassLoader classloader) throws IOException {
        ClassPath.Scanner classpath_scanner = new ClassPath.Scanner();
        Iterator iterator = getClassPathEntries(classloader).entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            classpath_scanner.scan((URI) entry.getKey(), (ClassLoader) entry.getValue());
        }

        return new ClassPath(classpath_scanner.getResources());
    }

    public ImmutableSet<ClassPath.ResourceInfo> getResources() {
        return this.resources;
    }

    public ImmutableSet<ClassPath.ClassInfo> getAllClasses() {
        return FluentIterable.from((Iterable) this.resources).filter(ClassPath.ClassInfo.class).toSet();
    }

    public ImmutableSet<ClassPath.ClassInfo> getTopLevelClasses() {
        return FluentIterable.from((Iterable) this.resources).filter(ClassPath.ClassInfo.class).filter(ClassPath.IS_TOP_LEVEL).toSet();
    }

    public ImmutableSet<ClassPath.ClassInfo> getTopLevelClasses(String s) {
        Preconditions.checkNotNull(s);
        ImmutableSet.Builder immutableset_builder = ImmutableSet.builder();
        Iterator iterator = this.getTopLevelClasses().iterator();

        while (iterator.hasNext()) {
            ClassPath.ClassInfo classpath_classinfo = (ClassPath.ClassInfo) iterator.next();

            if (classpath_classinfo.getPackageName().equals(s)) {
                immutableset_builder.add((Object) classpath_classinfo);
            }
        }

        return immutableset_builder.build();
    }

    public ImmutableSet<ClassPath.ClassInfo> getTopLevelClassesRecursive(String s) {
        Preconditions.checkNotNull(s);
        String s1 = s + '.';
        ImmutableSet.Builder immutableset_builder = ImmutableSet.builder();
        Iterator iterator = this.getTopLevelClasses().iterator();

        while (iterator.hasNext()) {
            ClassPath.ClassInfo classpath_classinfo = (ClassPath.ClassInfo) iterator.next();

            if (classpath_classinfo.getName().startsWith(s1)) {
                immutableset_builder.add((Object) classpath_classinfo);
            }
        }

        return immutableset_builder.build();
    }

    @VisibleForTesting
    static ImmutableMap<URI, ClassLoader> getClassPathEntries(ClassLoader classloader) {
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        ClassLoader classloader1 = classloader.getParent();

        if (classloader1 != null) {
            linkedhashmap.putAll(getClassPathEntries(classloader1));
        }

        if (classloader instanceof URLClassLoader) {
            URLClassLoader urlclassloader = (URLClassLoader) classloader;
            URL[] aurl = urlclassloader.getURLs();
            int i = aurl.length;

            for (int j = 0; j < i; ++j) {
                URL url = aurl[j];

                URI uri;

                try {
                    uri = url.toURI();
                } catch (URISyntaxException urisyntaxexception) {
                    throw new IllegalArgumentException(urisyntaxexception);
                }

                if (!linkedhashmap.containsKey(uri)) {
                    linkedhashmap.put(uri, classloader);
                }
            }
        }

        return ImmutableMap.copyOf(linkedhashmap);
    }

    @VisibleForTesting
    static String getClassName(String s) {
        int i = s.length() - ".class".length();

        return s.substring(0, i).replace('/', '.');
    }

    @VisibleForTesting
    static final class Scanner {

        private final ImmutableSortedSet.Builder<ClassPath.ResourceInfo> resources = new ImmutableSortedSet.Builder<>(Ordering.usingToString());
        private final Set<URI> scannedUris = Sets.newHashSet();

        Scanner() {}

        ImmutableSortedSet<ClassPath.ResourceInfo> getResources() {
            return this.resources.build();
        }

        void scan(URI uri, ClassLoader classloader) throws IOException {
            if (uri.getScheme().equals("file") && this.scannedUris.add(uri)) {
                this.scanFrom(new File(uri), classloader);
            }

        }

        @VisibleForTesting
        void scanFrom(File file, ClassLoader classloader) throws IOException {
            if (file.exists()) {
                if (file.isDirectory()) {
                    this.scanDirectory(file, classloader);
                } else {
                    this.scanJar(file, classloader);
                }

            }
        }

        private void scanDirectory(File file, ClassLoader classloader) throws IOException {
            this.scanDirectory(file, classloader, "", ImmutableSet.of());
        }

        private void scanDirectory(File file, ClassLoader classloader, String s, ImmutableSet<File> immutableset) throws IOException {
            File file1 = file.getCanonicalFile();

            if (!immutableset.contains(file1)) {
                File[] afile = file.listFiles();

                if (afile == null) {
                    ClassPath.logger.warning("Cannot read directory " + file);
                } else {
                    ImmutableSet immutableset1 = ImmutableSet.builder().addAll((Iterable) immutableset).add((Object) file1).build();
                    File[] afile1 = afile;
                    int i = afile.length;

                    for (int j = 0; j < i; ++j) {
                        File file2 = afile1[j];
                        String s1 = file2.getName();

                        if (file2.isDirectory()) {
                            this.scanDirectory(file2, classloader, s + s1 + "/", immutableset1);
                        } else {
                            String s2 = s + s1;

                            if (!s2.equals("META-INF/MANIFEST.MF")) {
                                this.resources.add((Object) ClassPath.ResourceInfo.of(s2, classloader));
                            }
                        }
                    }

                }
            }
        }

        private void scanJar(File file, ClassLoader classloader) throws IOException {
            JarFile jarfile;

            try {
                jarfile = new JarFile(file);
            } catch (IOException ioexception) {
                return;
            }

            try {
                Iterator iterator = getClassPathFromManifest(file, jarfile.getManifest()).iterator();

                while (iterator.hasNext()) {
                    URI uri = (URI) iterator.next();

                    this.scan(uri, classloader);
                }

                Enumeration enumeration = jarfile.entries();

                while (enumeration.hasMoreElements()) {
                    JarEntry jarentry = (JarEntry) enumeration.nextElement();

                    if (!jarentry.isDirectory() && !jarentry.getName().equals("META-INF/MANIFEST.MF")) {
                        this.resources.add((Object) ClassPath.ResourceInfo.of(jarentry.getName(), classloader));
                    }
                }
            } finally {
                try {
                    jarfile.close();
                } catch (IOException ioexception1) {
                    ;
                }

            }

        }

        @VisibleForTesting
        static ImmutableSet<URI> getClassPathFromManifest(File file, @Nullable Manifest manifest) {
            if (manifest == null) {
                return ImmutableSet.of();
            } else {
                ImmutableSet.Builder immutableset_builder = ImmutableSet.builder();
                String s = manifest.getMainAttributes().getValue(Name.CLASS_PATH.toString());

                if (s != null) {
                    Iterator iterator = ClassPath.CLASS_PATH_ATTRIBUTE_SEPARATOR.split(s).iterator();

                    while (iterator.hasNext()) {
                        String s1 = (String) iterator.next();

                        URI uri;

                        try {
                            uri = getClassPathEntry(file, s1);
                        } catch (URISyntaxException urisyntaxexception) {
                            ClassPath.logger.warning("Invalid Class-Path entry: " + s1);
                            continue;
                        }

                        immutableset_builder.add((Object) uri);
                    }
                }

                return immutableset_builder.build();
            }
        }

        @VisibleForTesting
        static URI getClassPathEntry(File file, String s) throws URISyntaxException {
            URI uri = new URI(s);

            return uri.isAbsolute() ? uri : (new File(file.getParentFile(), s.replace('/', File.separatorChar))).toURI();
        }
    }

    @Beta
    public static final class ClassInfo extends ClassPath.ResourceInfo {

        private final String className;

        ClassInfo(String s, ClassLoader classloader) {
            super(s, classloader);
            this.className = ClassPath.getClassName(s);
        }

        public String getPackageName() {
            return Reflection.getPackageName(this.className);
        }

        public String getSimpleName() {
            int i = this.className.lastIndexOf(36);
            String s;

            if (i != -1) {
                s = this.className.substring(i + 1);
                return CharMatcher.DIGIT.trimLeadingFrom(s);
            } else {
                s = this.getPackageName();
                return s.isEmpty() ? this.className : this.className.substring(s.length() + 1);
            }
        }

        public String getName() {
            return this.className;
        }

        public Class<?> load() {
            try {
                return this.loader.loadClass(this.className);
            } catch (ClassNotFoundException classnotfoundexception) {
                throw new IllegalStateException(classnotfoundexception);
            }
        }

        @Override
        public String toString() {
            return this.className;
        }
    }

    @Beta
    public static class ResourceInfo {

        private final String resourceName;
        final ClassLoader loader;

        static ClassPath.ResourceInfo of(String s, ClassLoader classloader) {
            return (ClassPath.ResourceInfo) (s.endsWith(".class") ? new ClassPath.ClassInfo(s, classloader) : new ClassPath.ResourceInfo(s, classloader));
        }

        ResourceInfo(String s, ClassLoader classloader) {
            this.resourceName = (String) Preconditions.checkNotNull(s);
            this.loader = (ClassLoader) Preconditions.checkNotNull(classloader);
        }

        public final URL url() {
            return (URL) Preconditions.checkNotNull(this.loader.getResource(this.resourceName), "Failed to load resource: %s", this.resourceName);
        }

        public final String getResourceName() {
            return this.resourceName;
        }

        public int hashCode() {
            return this.resourceName.hashCode();
        }

        public boolean equals(Object object) {
            if (!(object instanceof ClassPath.ResourceInfo)) {
                return false;
            } else {
                ClassPath.ResourceInfo classpath_resourceinfo = (ClassPath.ResourceInfo) object;

                return this.resourceName.equals(classpath_resourceinfo.resourceName) && this.loader == classpath_resourceinfo.loader;
            }
        }

        public String toString() {
            return this.resourceName;
        }
    }
}
