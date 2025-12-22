package asm.n1luik.K_multi_threading.asm.JavaAgent.all;

import asm.n1luik.KAllFix.asm.util.StackUtil;
import asm.n1luik.K_multi_threading.asm.JavaAgent.AsmUtil;
import asm.n1luik.K_multi_threading.asm.JavaAgent.JavaAgent;
import asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.Forge20Asn;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import asm.n1luik.K_multi_threading.asm.util.Unsafe2;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class TransformerForge20 extends ITransformer2 {
    @Override
    public ClassNode transform(ClassNode input) {
        try {
            log.info("TransformerForge20.transform: input = {}", input);

            //ClassLoader platformClassLoader = ClassLoader.getPlatformClassLoader();
            //log.info("TransformerForge20.transform: platformClassLoader = {}", platformClassLoader);
            //JavaAgent.loadClass(platformClassLoader, "asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.Forge20Asn");

            AsmUtil.replaceMethodName(input, Forge20Asn.class.getMethod("streamServiceLoader", Supplier.class, Consumer.class), "streamServiceLoader", null);
            AsmUtil.replaceMethodName(input, Forge20Asn.class.getMethod("load", ModuleLayer.class, Class.class), "load", "java/util/ServiceLoader");

            //JavaAgent.saveClass(input);
            //Class<?> aClass = platformClassLoader.loadClass("jdk.internal.module.Modules");
            //Method addReadsAllUnnamed = aClass.getMethod("addReadsAllUnnamed", Module.class);
            //((Consumer<Module>)Unsafe2.metafactory(aClass, Consumer.class.getMethod("accept", Object.class), Unsafe2.lookup.unreflect(addReadsAllUnnamed))
            //        .dynamicInvoker().invoke()).accept(platformClassLoader.loadClass("cpw.mods.modlauncher.Launcher").getModule());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return input;
    }

    @Override
    public Set<String> targets() {
        return Set.of("cpw.mods.modlauncher.TransformationServicesHandler", "net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer");
    }
}
