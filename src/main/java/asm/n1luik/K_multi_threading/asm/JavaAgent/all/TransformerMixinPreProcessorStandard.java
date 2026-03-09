//package asm.n1luik.K_multi_threading.asm.JavaAgent.all;
//
//import asm.n1luik.K_multi_threading.asm.JavaAgent.JavaAgent;
//import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
//import lombok.extern.slf4j.Slf4j;
//import org.objectweb.asm.tree.ClassNode;
//
//import java.util.Set;
//
//@Slf4j
//public class TransformerMixinPreProcessorStandard extends ITransformer2 {
//    @Override
//    public ClassNode transform(ClassNode input) {
//        try {
//            ClassLoader platformClassLoader = Thread.currentThread().getContextClassLoader();
//            log.info("TransformerMixinApplicatorStandard.transform: platformClassLoader = {}", platformClassLoader);
//            JavaAgent.loadClass(platformClassLoader, "asm.n1luik.K_multi_threading.asm.JavaAgent.all.code.MixinPreProcessorStandardAddCode");
//
//            //AsmUtil.head(input, MixinPreProcessorStandardAddCode.class.getMethod("of", SecureJar[].class)
//            //        , "applyMethods", null, Opcodes.INVOKESTATIC, );
//
//            //JavaAgent.saveClass(input);
//            //Class<?> aClass = platformClassLoader.loadClass("jdk.internal.module.Modules");
//            //Method addReadsAllUnnamed = aClass.getMethod("addReadsAllUnnamed", Module.class);
//            //((Consumer<Module>)Unsafe2.metafactory(aClass, Consumer.class.getMethod("accept", Object.class), Unsafe2.lookup.unreflect(addReadsAllUnnamed))
//            //        .dynamicInvoker().invoke()).accept(platformClassLoader.loadClass("cpw.mods.modlauncher.Launcher").getModule());
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//        return input;
//    }
//
//    @Override
//    public Set<String> targets() {
//        return Set.of("org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard");
//    }
//}
//