package n1luik.KAllFix.Imixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import asm.n1luik.K_multi_threading.asm.mapping.MappingImpl;
import asm.n1luik.K_multi_threading.asm.mapping.MappingTransformer;
import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import asm.n1luik.K_multi_threading.asm.util.AsmApi2;
import cpw.mods.modlauncher.TransformingClassLoader;
import lombok.extern.slf4j.Slf4j;
import n1luik.K_multi_threading.debug.GetterClassFileCommand;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

@Slf4j
public class Plugin implements IMixinConfigPlugin {
    static {
        System.setProperty("KAF-ChunkBreedingControlSizeEnable", getInt("KAF-ChunkBreedingControlSize") != null ? "true" : "false");
    }
    public static Integer getInt(String key) {
        try {
            return Integer.getInteger(key);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private volatile Integer biolithFixVersion = null;
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return AsmApi2.bootType != AsmApi2.BootType.NEO_FORGE ? "mixins.K_multi_threading.refmap.json" : null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return test(targetClassName, mixinClassName) && !Boolean.getBoolean("KAF-RemoveMixin:"+mixinClassName);
    }
    public boolean test(String targetClassName, String mixinClassName) {
        if (biolithFixVersion == null) {
            synchronized (this) {
                if (biolithFixVersion == null) {
                    if (!isModLoaded("biolith")) {
                        biolithFixVersion = 0;
                    } else {
                        InputStream biolith = getJarFile("biolith", "/com/terraformersmc/biolith/impl/biome/InterfaceBiomeSource.class").get();
                        try {
                            byte[] bytes = biolith.readAllBytes();
                            ClassNode classNode = new ClassNode();
                            new ClassReader(bytes).accept(classNode, 0);
                            boolean debug_1 = false;
                            for (var method : classNode.methods) {

                                if (method.name.equals("biolith$getDimensionType")) {
                                    debug_1 = true;
                                    if (method.desc.equals("()Lnet/minecraft/resources/ResourceKey;")) {
                                        biolithFixVersion = 1;
                                    }else {
                                        biolithFixVersion = 2;
                                    }

                                    break;
                                }
                            }
                            if(!debug_1){
                                throw new RuntimeException("biolith 当前版本未适配");
                            }
                            biolith.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (AsmApi.mcVersion.startsWith("1.21")) {
                        biolithFixVersion += 2;
                    }
                    log.info("biolithFix: {}", biolithFixVersion);
                }
            }
        }
        String s = "n1luik.KAllFix.mixin.unsafe.";
        String s8 = "n1luik.KAllFix.mixin.unsafe.path.";
        if (mixinClassName.startsWith(s8)){
            if(Boolean.getBoolean("KAF-"+mixinClassName.substring(s8.length()).split("\\.", 2)[0])){
                return switch (mixinClassName) {
                    case "n1luik.KAllFix.mixin.unsafe.path.packetOptimize.blockEentity.ServerChunkCacheMixin2" -> Boolean.getBoolean("KMT_D");
                    case "n1luik.KAllFix.mixin.unsafe.path.packetOptimize.blockEentity.mt.ServerChunkCacheMixin1" -> !Boolean.getBoolean("KMT_D");
                    default -> true;
                };
            }
        }
        if (mixinClassName.startsWith(s)){
            return Boolean.getBoolean("KAF-"+mixinClassName.substring(s.length()));
        }
        //final boolean biolith = isModLoaded("biolith");
        //boolean terrablender = isModLoaded("terrablender");
        //wc这玩意默认双开的
        //String s2 = "n1luik.KAllFix.mixin.mixinfix.biolith.test_else.";
        //String s3 = "n1luik.KAllFix.mixin.mixinfix.biolith.test_all.";
        String s4 = "n1luik.KAllFix.mixin.mixinfix.fancyenchantments.";
        String s5 = "n1luik.KAllFix.mixin.FixConfigAll.";
        //String s6 = "n1luik.KAllFix.mixin.ex.CancelNio";
        String s7 = "n1luik.KAllFix.mixin.ex.FixAllPacket.";
        String s9 = "n1luik.KAllFix.mixin.mixinfix.path.";
        //boolean fixBiolithBugMode2 = Boolean.getBoolean("FixBiolithBugMode2");
        //if (!biolith && mixinClassName.startsWith(s2) && fixBiolithBugMode2) {
        //    return false;
        //}
        //if (!biolith && mixinClassName.startsWith(s3) && !fixBiolithBugMode2) {
        //    return false;
        //}
        if (mixinClassName.startsWith(s4)) {
            return isModLoaded("fancyenchantments");
        }
        if (!Boolean.getBoolean("KAF-FixConfigAuto") && mixinClassName.startsWith(s5)) {
            return false;
        }
        //if (mixinClassName.startsWith(s6)) {
        //    return Boolean.getBoolean("KAF-CancelNio");
        //}
        if (mixinClassName.startsWith(s7)) {
            if (Boolean.getBoolean("KAF-FixAllPacket")) {
                 return isModLoaded(mixinClassName.substring(s7.length()).split("\\.", 2)[0]);
            }
            return false;
        }
        if (mixinClassName.startsWith(s9)) {
             return isModLoaded(mixinClassName.substring(s9.length()).split("\\.", 2)[0]);
        }
        //KAF-NbtAZ
        return switch (mixinClassName) {
            case "n1luik.KAllFix.mixin.mixinfix.farm_and_charm.This" -> {
                try{
                    GetterClassFileCommand.getclass.apply((TransformingClassLoader)Plugin.class.getClassLoader(), "net.satisfy.farm_and_charm.core.util.SaturationTracker$SaturatedAnimal");
                    yield isModLoaded("farm_and_charm");
                }catch (Exception e){
                    log.error("KAllFix: farm_and_charm 加载失败", e);
                    yield false;
                }
            }
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MinecraftServerMixin" -> biolithFixVersion != 0;
            //case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin" -> isModLoaded("biolith");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin" -> biolithFixVersion != 0 ;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.mod.TerramityModBiomesMixin" -> biolithFixVersion != 0;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSource2" -> biolithFixVersion == 2;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSource2Forge" -> biolithFixVersion == 1;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin2" -> biolithFixVersion != 0;
            case "n1luik.KAllFix.mixin.mixinfix.lithium.LithiumEntityCollisionsMixin" -> Boolean.getBoolean("KAF-ChunkAwareBlockCollisionSweeperFast");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.terrablender.InitializationHandlerMixin" ->
                    biolithFixVersion != 0 && isModLoaded("terrablender");
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

        for (MethodNode methodNode : targetClass.methods) {
            if (findAnnotation(methodNode, "Ln1luik/KAllFix/api/SetPublic;")) {
                methodNode.access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED | Opcodes.ACC_PUBLIC);
                methodNode.access |= Opcodes.ACC_PUBLIC;
            }
        }
        for (FieldNode field : targetClass.fields) {
            if (findAnnotation(field, "Ln1luik/KAllFix/api/AddFinal;")) {
                field.access |= Opcodes.ACC_FINAL;
            }
        }
        // 检查是否有 CatInit 注解
        boolean isCatInit = false;
        List<MethodNode> allRedirectMethods = new ArrayList<>((int) (targetClass.methods.size() * 0.1) + 1);
        List<MethodNode> catInitMethods = new ArrayList<>(allRedirectMethods.size());
        for (MethodNode method : targetClass.methods) {
            if (findAnnotation(method, "Ln1luik/KAllFix/api/CatInit;")) {
                isCatInit = true;
                catInitMethods.add(method);
                break;
            }
        }
        if (isCatInit) {
            List<MethodNode> initMethods = targetClass.methods.stream().filter(method -> method.name.equals("<init>")).toList();

            int b = catInitMethods.stream().mapToInt(method -> method.maxStack).max().orElse(0);
            for (MethodNode initMethod : initMethods) {
                initMethod.maxStack = Math.max(initMethod.maxStack, b);
                // 在所有return之前添加
                // 找到所有return指令
                List<Integer> returnOffsets = Arrays.stream(initMethod.instructions.toArray())
                        .filter(instr -> instr.getOpcode() == Opcodes.RETURN)
                        .map(instr -> initMethod.instructions.indexOf(instr))
                        .toList();
                // 在每个return指令之前插入
                for (int offset : returnOffsets) {
                    AbstractInsnNode abstractInsnNode = initMethod.instructions.get(offset);
                    for (MethodNode catInitMethod : catInitMethods) {
                        initMethod.instructions.insertBefore(abstractInsnNode, catInitMethod.instructions);
                    }
                }
            }
        }
        if (!isCatInit) return;
        //移除所有CatInit方法
        targetClass.methods.removeIf(methodNode ->
                {
                    AnnotationNode annotationData = findAnnotationData(methodNode, "Ln1luik/KAllFix/api/AllRedirect;");
                    String redirectClass = (String) findAnnotationArg(annotationData, "value");
                    return findAnnotation(methodNode, "Ln1luik/KAllFix/api/CatInit;") ||
                            (redirectClass != null && !redirectClass.isEmpty());
                }
        );
    }

    private static boolean isModLoaded(String modId) {
        return AsmApi.isModLoaded(modId);
        //if (ModList.get() == null) {
        //    return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        //}
        //return ModList.get().isLoaded(modId);
    }
    private static Optional<InputStream> getJarFile(String modId, String file) {
        return AsmApi.getJarFile(modId, file);
    }

    public static boolean findAnnotation(FieldNode field, String annotation) {
        if (field.visibleAnnotations != null && field.visibleAnnotations.stream().anyMatch(a -> a.desc.equals(annotation)))
            return true;
        return field.invisibleAnnotations != null && field.invisibleAnnotations.stream().anyMatch(a -> a.desc.equals(annotation));
    }
    public static boolean findAnnotation(MethodNode met, String annotation) {
        if (met.visibleAnnotations != null && met.visibleAnnotations.stream().anyMatch(a -> a.desc.equals(annotation)))
            return true;
        return met.invisibleAnnotations != null && met.invisibleAnnotations.stream().anyMatch(a -> a.desc.equals(annotation));
    }
    public static Object findAnnotationArg(AnnotationNode annotationNode, String name) {
        if (annotationNode != null) {
            for (int i = 0; i < annotationNode.values.size(); i += 2) {
                if (annotationNode.values.get(i).equals(name)) {
                    return annotationNode.values.get(i + 1);
                }
            }
        }
        return null;
    }
    public static LocalVariableNode findLocal(MethodNode met, int annotation) {
        if (met.localVariables != null) {
            for (LocalVariableNode localVariableNode : met.localVariables) {
                if (localVariableNode.index == annotation) {
                    return localVariableNode;
                }
            }
        }
        return null;
    }
    public static AnnotationNode findAnnotationData(MethodNode met, String annotation) {
        if (met.visibleAnnotations != null) {
            AnnotationNode annotationNode = met.visibleAnnotations.stream().filter(a -> a.desc.equals(annotation)).findFirst().orElse(null);
            if (annotationNode != null) return annotationNode;
        }
        return met.invisibleAnnotations != null ? met.invisibleAnnotations.stream().filter(a -> a.desc.equals(annotation)).findFirst().orElse(null) : null;
    }
    public static AnnotationNode findAnnotationDataAndRemove(MethodNode met, String annotation) {
        if (met.visibleAnnotations != null) {
            Iterator<AnnotationNode> iterator = met.visibleAnnotations.iterator();
            while (iterator.hasNext()) {
                AnnotationNode annotationNode = iterator.next();
                if (annotationNode.desc.equals(annotation)) {
                    iterator.remove();
                    return annotationNode;
                }
            }
        }
        List<AnnotationNode> invisibleAnnotations = met.invisibleAnnotations;
        if (invisibleAnnotations != null) {
            var iterator = invisibleAnnotations.iterator();
            while (iterator.hasNext()) {
                AnnotationNode annotationNode = iterator.next();
                if (annotationNode.desc.equals(annotation)) {
                    iterator.remove();
                    return annotationNode;
                }
            }
        }
        return null;
    }
}