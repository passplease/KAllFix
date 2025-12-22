package asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace;

import asm.n1luik.K_multi_threading.asm.JavaAgent.all.TransformerBootstrapLauncher;
import cpw.mods.cl.JarModuleFinder;
import cpw.mods.jarhandling.SecureJar;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BootstrapLauncherAsm {
    //public Configuration resolveAndBind(Configuration c,
    //        ModuleFinder before,
    //                                    ModuleFinder after,
    //                                    Collection<String> roots)
    //{
    //    return Configuration.resolveAndBind(before, List.of(c), after, roots);
    //}//(Ljava.lang.module.Configuration;Ljava.lang.module.ModuleFinder;Ljava.lang.module.ModuleFinder;Ljava.util.Collection;)Ljava.lang.module.Configuration;

    public static JarModuleFinder of(SecureJar... jars) {
        jars = Arrays.copyOf(jars, jars.length+1);
        jars[jars.length-1] = SecureJar.from(new File(System.getProperty("K_multi_threading.TransformerBootstrapLauncher1")).toPath());
        return JarModuleFinder.of(jars);
    }
}
