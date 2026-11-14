package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public final class JavassistTypeParameterMatcherGenerator {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(JavassistTypeParameterMatcherGenerator.class);
    private static final ClassPool classPool = new ClassPool(true);

    public void appendClassPath(ClassPath classpath) {
        JavassistTypeParameterMatcherGenerator.classPool.appendClassPath(classpath);
    }

    public void appendClassPath(String s) throws NotFoundException {
        JavassistTypeParameterMatcherGenerator.classPool.appendClassPath(s);
    }

    public static TypeParameterMatcher generate(Class<?> oclass) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        if (classloader == null) {
            classloader = ClassLoader.getSystemClassLoader();
        }

        return generate(oclass, classloader);
    }

    public static TypeParameterMatcher generate(Class<?> oclass, ClassLoader classloader) {
        String s = typeName(oclass);
        String s1 = "io.netty.util.internal.__matchers__." + s + "Matcher";

        try {
            try {
                return (TypeParameterMatcher) Class.forName(s1, true, classloader).newInstance();
            } catch (Exception exception) {
                CtClass ctclass = JavassistTypeParameterMatcherGenerator.classPool.getAndRename(NoOpTypeParameterMatcher.class.getName(), s1);

                ctclass.setModifiers(ctclass.getModifiers() | 16);
                ctclass.getDeclaredMethod("match").setBody("{ return $1 instanceof " + s + "; }");
                byte[] abyte = ctclass.toBytecode();

                ctclass.detach();
                Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);

                method.setAccessible(true);
                Class oclass1 = (Class) method.invoke(classloader, s1, abyte, 0, abyte.length);

                if (oclass != Object.class) {
                    JavassistTypeParameterMatcherGenerator.logger.debug("Generated: {}", (Object) oclass1.getName());
                }

                return (TypeParameterMatcher) oclass1.newInstance();
            }
        } catch (RuntimeException runtimeexception) {
            throw runtimeexception;
        } catch (Exception exception1) {
            throw new RuntimeException(exception1);
        }
    }

    private static String typeName(Class<?> oclass) {
        return oclass.isArray() ? typeName(oclass.getComponentType()) + "[]" : oclass.getName();
    }

    private JavassistTypeParameterMatcherGenerator() {}

    static {
        JavassistTypeParameterMatcherGenerator.classPool.appendClassPath(new ClassClassPath(NoOpTypeParameterMatcher.class));
    }
}
