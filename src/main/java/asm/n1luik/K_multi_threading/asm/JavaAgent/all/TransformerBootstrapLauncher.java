package asm.n1luik.K_multi_threading.asm.JavaAgent.all;

import asm.n1luik.K_multi_threading.asm.JavaAgent.AsmUtil;
import asm.n1luik.K_multi_threading.asm.JavaAgent.JavaAgent;
import asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.BootstrapLauncherAsm;
import asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.Forge20Asn;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import cpw.mods.jarhandling.SecureJar;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class TransformerBootstrapLauncher extends ITransformer2 {
    @Override
    public ClassNode transform(ClassNode input) {
        try {
            System.setProperty("K_multi_threading.TransformerBootstrapLauncher1", new File(TransformerBootstrapLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath());

            ClassLoader platformClassLoader = Thread.currentThread().getContextClassLoader();
            log.info("TransformerBootstrapLauncher.transform: platformClassLoader = {}", platformClassLoader);
            JavaAgent.loadClass(platformClassLoader, "asm.n1luik.K_multi_threading.asm.JavaAgent.ArgsUtil");
            JavaAgent.loadClass(platformClassLoader, "asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.BootstrapLauncherAsm");

            AsmUtil.replaceMethodName(input, BootstrapLauncherAsm.class.getMethod("of", SecureJar[].class)
                    , "of", "cpw/mods/cl/JarModuleFinder");

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
        return Set.of("cpw.mods.bootstraplauncher.BootstrapLauncher");
    }
}
