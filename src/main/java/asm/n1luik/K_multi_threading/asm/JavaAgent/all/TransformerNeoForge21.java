package asm.n1luik.K_multi_threading.asm.JavaAgent.all;

import asm.n1luik.K_multi_threading.asm.JavaAgent.AsmUtil;
import asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.Forge20Asm;
import asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.NeoForge21Asm;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class TransformerNeoForge21 extends ITransformer2 {
    @Override
    public ClassNode transform(ClassNode input) {
        try {
            log.info("TransformerForge20.transform: input = {}", input);

            //ClassLoader platformClassLoader = ClassLoader.getPlatformClassLoader();
            //log.info("TransformerForge20.transform: platformClassLoader = {}", platformClassLoader);
            //JavaAgent.loadClass(platformClassLoader, "asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.Forge20Asn");

            AsmUtil.replaceMethodName(input, NeoForge21Asm.class.getMethod("streamServiceLoader", Supplier.class, Consumer.class), "streamServiceLoader", null);
            AsmUtil.replaceMethodName(input, NeoForge21Asm.class.getMethod("load", ModuleLayer.class, Class.class), "load", "java/util/ServiceLoader");

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
