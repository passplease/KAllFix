package asm.n1luik.K_multi_threading.asm.JavaAgent;

import asm.n1luik.K_multi_threading.asm.mapping.MappingImpl;
import asm.n1luik.K_multi_threading.asm.mapping.MappingSrgImplForge;
import asm.n1luik.K_multi_threading.asm.mapping.MappingTransformerForge;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

@Slf4j
public class AsmUtil {
    public static <T extends MappingImpl>  T loadMap(String path, Function<String, T> mapper) {
        try (var is = JavaAgent.class.getResourceAsStream("/" + path)) {
            if (is == null) {
                throw new RuntimeException("无法找到类文件: " + path);
            }
            return mapper.apply(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String[] loadMapFile(String path) {
        try (var is = JavaAgent.class.getResourceAsStream("/" + path)) {
            if (is == null) {
                throw new RuntimeException("无法找到类文件: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8).split("[\n\r]+");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ITransformer2 newForge2MCPMap() {
        Set<String> strings = Set.of(loadMapFile("K_multi_threading.mapping/map_class.txt"));
        return new MappingTransformerForge(loadMap("K_multi_threading.mapping/map_srg.srg", MappingSrgImplForge::new)){
            @Override
            public @NotNull Set<String> targets() {
                return strings;
            }
        };
    }

    public static void replaceMethodName(ClassNode node, Method method, String name, String owner) {
        node.methods.forEach(methodNode -> replaceMethodName(methodNode, method, name, owner));
    }

    public static void replaceMethodName(MethodNode mn, Method method, String name, String owner) {
        replaceMethodName(mn, method, name, owner, null);
    }
    public static void replaceMethodName(ClassNode node, Method method, String name, String owner, String desc) {
        node.methods.forEach(methodNode -> replaceMethodName(methodNode, method, name, owner, desc));
    }

    public static void replaceMethodName(MethodNode mn, Method method, String name, String owner, String desc) {
        for (AbstractInsnNode instruction : mn.instructions) {
            if (instruction instanceof MethodInsnNode methodInsnNode) {
                if (methodInsnNode.name.equals(name) && (owner == null || owner.equals(methodInsnNode.owner))) {
                    log.info("Replace method name {} to {} in class {}", methodInsnNode.name, method.getName(), methodInsnNode.owner);
                    methodInsnNode.owner = method.getDeclaringClass().getName().replace(".", "/");
                    methodInsnNode.name = method.getName();
                    if (desc != null) {
                        methodInsnNode.desc = desc;
                    }
                }
            }
        }
    }
}
