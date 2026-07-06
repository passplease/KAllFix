package asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace;

import net.minecraftforge.forgespi.locating.IModLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static cpw.mods.modlauncher.util.ServiceLoaderUtils.streamWithErrorHandling;

//@Slf4j
public class NeoForge21Asm {
    private static final Class<?> targetClass;

    static {
        try {
            targetClass = Thread.currentThread().getContextClassLoader().loadClass("cpw.mods.jarhandling.JarContentsBuilder");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static volatile Class<?> ThisClass = null;

    public synchronized static <T> Stream<T> streamServiceLoader(Supplier<ServiceLoader<T>> slSupplier, Consumer<ServiceConfigurationError> errorConsumer) {
        List<T> list = new ArrayList<>(streamWithErrorHandling(slSupplier.get(), errorConsumer).toList());
        Class<?> thisClass = ThisClass;
        if (thisClass != null) {
            System.out.println("Forge20Asn.streamServiceLoader: thisClass = "+ thisClass);
            //if(thisClass.isAssignableFrom(ITransformationService.class)) {
            //    list.add((T)(new K_multi_threadingForge()));
            //    list.add((T)(new KAllFixAsmForge()));
            //}else
                if(thisClass.isAssignableFrom(targetClass)) {
                try {
                    list.add((T)(Class.forName("n1luik.K_multi_threading.install.NeoForgeModLocator").getDeclaredConstructor().newInstance()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list.stream();
    }
    public static <S> ServiceLoader<S> load(ModuleLayer layer, Class<S> service) {
        ThisClass = service;
        return ServiceLoader.load(layer, service);
    }
}
