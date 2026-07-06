package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.mapping.MappingImpl;
import asm.n1luik.K_multi_threading.asm.mapping.MappingTransformer;
import asm.n1luik.K_multi_threading.asm.util.AsmApi2;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class PreMixin_ASM extends ITransformer2 {
    public final Map<String, List<String>> preOverwrites = new HashMap<>();

    {

        preOverwrites.put(ForgeAsm.minecraft_map.mapClass("net.minecraft.world.item.crafting.Ingredient".replace(".", "/"))
                , new ArrayList<>(List.of(
                        "asm.n1luik.K_multi_threading.asm.falseMixin.PreIngredientFix1"
                )));
        if (AsmApi2.bootType == AsmApi2.BootType.NEO_FORGE){

            preOverwrites.put(ForgeAsm.minecraft_map.mapClass("net.minecraft.world.level.block.Block".replace(".", "/"))
                    , new ArrayList<>(List.of(
                            "asm.n1luik.K_multi_threading.asm.falseMixin.PreBlockFix1"
                    )));
        }
    }
    @Override
    public ClassNode transform(ClassNode classNode) {
        {
            var list = preOverwrites.get(classNode.name);
            if (list == null) return classNode;
            List<ClassNode> mixins = new ArrayList<>();

            for (var mixinClass : list) {
                //读取他的类
                InputStream resourceAsStream = PreMixin_ASM.class.getResourceAsStream("/" + mixinClass.replace('.', '/') + ".class");
                if (resourceAsStream == null) {
                    log.error("MixinConnector: 无法找到mixin类{}", mixinClass);
                    continue;
                }
                ClassNode mixinClassNode = new ClassNode();
                try {
                    new ClassReader(resourceAsStream).accept(mixinClassNode, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String name1 = mixinClassNode.name;
                new MappingTransformer(new MappingImpl(){
                    @Override
                    public String mapClass(String name) {
                        if (name.equals(name1)) {
                            return classNode.name;
                        }
                        return name;
                    }
                }).transform(mixinClassNode);
                mixins.add(mixinClassNode);
            }
            //已经替换的函数，进行安全检查用
            Set<String> replacedMethHashSet = new HashSet<>();
            List<MethodNode> overwriteMethods = new ArrayList<>();
            for (var mixinClassNode : mixins) {
                for (MethodNode method : mixinClassNode.methods) {
                    if (!findAnnotation(method, "Lorg/spongepowered/asm/mixin/Overwrite;")) {
                        if ((method.access & Opcodes.ACC_SYNTHETIC) != 0){
                            if (!replacedMethHashSet.add(method.name + method.desc)) {
                                log.error("MixinConnector: Mixin {} 中存在重复的Overwrite函数 {} {}", mixinClassNode.name, method.name, method.desc);
                                continue;
                            }
                            overwriteMethods.add(method);
                        }
                        continue;
                    }
                    if (!replacedMethHashSet.add(method.name + method.desc)) {
                        log.error("MixinConnector: Mixin {} 中存在重复的Overwrite函数 {} {}", mixinClassNode.name, method.name, method.desc);
                        continue;
                    }
                    boolean found = false;
                    Iterator<MethodNode> iterator = classNode.methods.iterator();
                    while (iterator.hasNext()) {
                        MethodNode next = iterator.next();
                        if (next.name.equals(method.name) && next.desc.equals(method.desc)) {
                            method.access = next.access;
                            overwriteMethods.add(method);
                            iterator.remove();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        log.error("MixinConnector: Mixin {} 中存在Overwrite函数 {} {} 但是目标类 {} 中不存在", mixinClassNode.name, method.name, method.desc, classNode.name);
                    }
                }
            }
            classNode.methods.addAll(overwriteMethods);
        }
        return classNode;
    }

    public static boolean findAnnotation(MethodNode met, String annotation) {
        if (met.visibleAnnotations != null && met.visibleAnnotations.stream().anyMatch(a -> a.desc.equals(annotation)))
            return true;
        return met.invisibleAnnotations != null && met.invisibleAnnotations.stream().anyMatch(a -> a.desc.equals(annotation));
    }

    @Override
    public Set<String> targets() {
        return preOverwrites.keySet();
    }
}
