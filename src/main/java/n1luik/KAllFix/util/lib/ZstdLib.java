package n1luik.KAllFix.util.lib;

import lombok.Getter;
import n1luik.KAllFix.util.AutoLib;
import n1luik.K_multi_threading.core.util.ConstOB2;
import n1luik.K_multi_threading.core.util.OB2;
import n1luik.K_multi_threading.core.util.Unsafe;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.function.Function;

@Getter
public class ZstdLib extends AutoLib<ConstOB2<Function<InputStream, InputStream>, Function<OutputStream, OutputStream>>> {
    @Nullable
    public static final ZstdLib INSTANCE;
    public static final Function<InputStream, InputStream> in;
    public static final Function<OutputStream, OutputStream> out;
    static {


        try {
            INSTANCE = initInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (INSTANCE == null) {
            try {
                ConstOB2<Function<InputStream, InputStream>, Function<OutputStream, OutputStream>> instance1 = getInstance(ZstdLib.class.getClassLoader());

                in = instance1.t1;
                out = instance1.t2;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }else {

            in = INSTANCE.instance.t1;
            out = INSTANCE.instance.t2;
        }

    }
    public static void cinit(){

    }
    public final ConstOB2<Function<InputStream, InputStream>, Function<OutputStream, OutputStream>> instance;

    public static ZstdLib initInstance() throws Throwable {
        try {
            ZstdLib.class.getClassLoader().loadClass("com.github.luben.zstd.Zstd");
            return null;
        } catch (ClassNotFoundException e) {
            return new ZstdLib();
        }
    }
    public static ConstOB2<Function<InputStream, InputStream>, Function<OutputStream, OutputStream>> getInstance(ClassLoader classLoader) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Class.forName("com.github.luben.zstd.Zstd", true, classLoader), Unsafe.lookup);
        MethodType interfaceMethodType = MethodType.methodType(InputStream.class, InputStream.class);
        MethodType interfaceMethodType2 = MethodType.methodType(OutputStream.class, OutputStream.class);
        MethodType interfaceMethodType3 = MethodType.methodType(Object.class, Object.class);
        MethodType factoryType = MethodType.methodType(Function.class);
        return new ConstOB2<>(
                (Function<InputStream, InputStream>)LambdaMetafactory.metafactory(
                        lookup,
                        "apply",
                        factoryType,
                        interfaceMethodType3,
                        lookup.findConstructor(classLoader.loadClass("com.github.luben.zstd.ZstdInputStream"), MethodType.methodType(void.class, InputStream.class)),
                        interfaceMethodType
                ).dynamicInvoker().invoke(),
                (Function<OutputStream, OutputStream>)LambdaMetafactory.metafactory(
                        lookup,
                        "apply",
                        factoryType,
                        interfaceMethodType3,
                        lookup.findConstructor(classLoader.loadClass("com.github.luben.zstd.ZstdOutputStream"), MethodType.methodType(void.class, OutputStream.class)),
                        interfaceMethodType2
                ).dynamicInvoker().invoke()
        );
    }
    @SuppressWarnings("all")
    public ZstdLib() throws Throwable {
        super("https://repo1.maven.org/maven2", "1.5.7-2", "zstd-jni", "com.github.luben");
        instance = getInstance(this);
    }
}   
