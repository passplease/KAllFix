package asm.n1luik.K_multi_threading.asm.JavaAgent;

import asm.n1luik.K_multi_threading.asm.JavaAgent.all.*;
import asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.ClassTransformerAsm;
import asm.n1luik.K_multi_threading.asm.mapping.MappingImpl;
import asm.n1luik.K_multi_threading.asm.mapping.MappingSrgImplForge;
import asm.n1luik.K_multi_threading.asm.mapping.MappingTransformerForge;
import asm.n1luik.K_multi_threading.asm.util.AsmApi2;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import asm.n1luik.K_multi_threading.asm.util.Unsafe2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Slf4j
public class JavaAgent {
    static final Method addClassMethod;
    static{
        try {
            // 确保Unsafe类已初始化
            Class.forName("asm.n1luik.K_multi_threading.asm.util.Unsafe2");

            // 为agent自身添加必要的opens参数
            Unsafe2.Jadd("--add-opens=java.base/java.lang=ALL-UNNAMED\n" +
                    "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED\n" +
                    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED\n" +
                    "--add-opens=java.base/java.util=ALL-UNNAMED\n" +
                    "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED\n" +
                    "--add-exports=java.base/jdk.internal.loader=ALL-UNNAMED\n" +
                    "--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED\n");

        } catch (Exception e) {
            System.err.println("Failed to initialize agent with opens parameters: " + e.getMessage());
            e.printStackTrace();
        }


        try {
            addClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        addClassMethod.setAccessible(true);
    }

    public static void saveClass(ClassNode classNode) {
        log.info("saveClass: {}", classNode.name);
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        byte[] bytes = classWriter.toByteArray();
        File outputFile = new File("target/classes/" + classNode.name.replace('/', '.') + ".class");
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            Files.write(outputFile.toPath(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean loadClass(ClassLoader loader, String name) {
        try (var is = JavaAgent.class.getResourceAsStream("/" + name.replace('.', '/') + ".class")) {
            if (is == null) {
                log.error("无法找到类文件: {}", name);
                return false;
            }
            byte[] bytes = is.readAllBytes();
            Class<?> aClass = (Class<?>) addClassMethod.invoke(loader, bytes, 0, bytes.length);
            log.info("成功加载类: {}", aClass.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    // JVM 启动时加载（-javaagent）
    public static void premain(String agentArgs, Instrumentation inst) {
        init(agentArgs, inst);
    }


    // 运行时 attach 加载（如 jcmd、Arthas）
    public static void agentmain(String agentArgs, Instrumentation inst) {
        //用中文，英文，俄语，日语输出不支持运行时attach加载
        log.info("""
        ---- KAllFix------
        不支持运行时attach加载
        Cannot support runtime attach loading
        Не поддерживается при загрузке attach
        ランタイムattachロードはサポートされていません
        """);
        System.exit(1);

    }

    private static void init(String agentArgs, Instrumentation inst) {
        log.info("KAllFix 智能体开始加载");
        log.info("agentArgs: {}", agentArgs);
        if (!(agentArgs == null || agentArgs.isEmpty())) {
            System.setProperty("K_multi_threading.agent.args", agentArgs);
        }
        //String property = System.getProperty("legacyClassPath");
        //try {
        //    if (property != null) {
        //        property += ";"+(new File(JavaAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath());
        //        log.info("legacyClassPath: {}", property);
        //        System.setProperty("legacyClassPath", property);
        //    }
        //} catch (URISyntaxException e) {
        //    throw new RuntimeException(e);
        //}


        AllTransformer transformer = new AllTransformer();
        transformer.addTransformer(new TransformerBootstrapLauncher());
        boolean isNeo = false;
        try {
            if(AsmApi2.bootType == AsmApi2.BootType.NEO_FORGE) {
                Thread.currentThread().getContextClassLoader().loadClass("cpw.mods.jarhandling.JarContentsBuilder");//确定是neoforge
                transformer.addTransformer(new AsmApiReplace(JavaAgent.class.getClassLoader().loadClass("asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.AsmApiNeoForge")));
                transformer.addTransformer(AsmUtil.newForge2MCPMap());
                transformer.addTransformer(new ClassTransformerAdd());
                isNeo = true;
            //}catch (Exception e){
            }
            if (isNeo){
                transformer.addTransformer((ITransformer2) JavaAgent.class.getClassLoader().loadClass("asm.n1luik.K_multi_threading.asm.JavaAgent.all.TransformerNeoForge21").newInstance());
            }else {
                transformer.addTransformer((ITransformer2) JavaAgent.class.getClassLoader().loadClass("asm.n1luik.K_multi_threading.asm.JavaAgent.all.TransformerForge20").newInstance());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        inst.addTransformer(transformer, false);
        File zipFile = new File("classes.zip");
        try {
            ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()));
            inst.addTransformer(new ClassFileTransformer() {
                int size = 0;
                @Override
                public synchronized byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                    try {
                        zos.putNextEntry(new ZipEntry(className + ".class"));
                        zos.write(classfileBuffer);
                        if (size++ % 1000 == 0) {
                            zos.closeEntry();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            }, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("KAllFix 智能体加载成功");


    }
    public static class AllTransformer implements ClassFileTransformer{
        public final Map<String, ITransformer2[]> classTransformer = new HashMap<>();
        public AgentAPI[] transformerAll = new AgentAPI[0];
        public void addTransformer(ITransformer2 iTransformer2) {
            iTransformer2.targets().forEach(target -> {
                log.info("添加Transformer2 {} 到 {}", iTransformer2, target);
                target = target.replace(".", "/");
                ITransformer2[] iTransformer2s = classTransformer.get(target);
                if (iTransformer2s == null) {
                    iTransformer2s = new ITransformer2[1];
                }else {
                    iTransformer2s = Arrays.copyOf(iTransformer2s, iTransformer2s.length + 1);
                }
                iTransformer2s[iTransformer2s.length - 1] = iTransformer2;
                classTransformer.put(target, iTransformer2s);
            });
        }
        public void addTransformerAll(AgentAPI agentAPI) {
            transformerAll = Arrays.copyOf(transformerAll, transformerAll.length + 1);
            transformerAll[transformerAll.length - 1] = agentAPI;
        }
        public void addAgentAPI(AgentAPI agentAPI) {
            if (agentAPI.isTransformerAll()) {
                transformerAll = Arrays.copyOf(transformerAll, transformerAll.length + 1);
                transformerAll[transformerAll.length - 1] = agentAPI;
            }
            agentAPI.transformers().forEach(this::addTransformer);
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

            ITransformer2[] iTransformer2s = classTransformer.get(className);
            if (iTransformer2s == null) {
                return null;
            }
            log.info("开始处理类 {} ，Transformer2 数量 {}", className, iTransformer2s.length);
            ClassNode classNode = new ClassNode();
            new ClassReader(classfileBuffer).accept(classNode, 0);
            for (ITransformer2 iTransformer2 : iTransformer2s) {
                try{
                    classNode = iTransformer2.transform(classNode);
                }catch (Throwable e) {
                    log.error("TransformerForge20.transform: ", e);
                    throw new RuntimeException(e);
                }
            }
            for (AgentAPI agentAPI : transformerAll) {
                try{
                    classNode = agentAPI.transformerAll(classNode);
                }catch (Throwable e) {
                    log.error("AgentAPI.transformerAll: ", e);
                    throw new RuntimeException(e);
                }
            }

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
    }
}
