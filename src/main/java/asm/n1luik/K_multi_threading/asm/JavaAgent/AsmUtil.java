package asm.n1luik.K_multi_threading.asm.JavaAgent;

import asm.n1luik.K_multi_threading.asm.OB2_ASM;
import asm.n1luik.K_multi_threading.asm.mapping.*;
import asm.n1luik.K_multi_threading.asm.util.AsmApi2;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import asm.n1luik.K_multi_threading.asm.util.Neo21MapppingMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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
            return mapper.apply(new String(is.readAllBytes(), StandardCharsets.UTF_8));
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
        return new MappingTransformerForge(loadMap("K_multi_threading.mapping/map_srg.srg", AsmApi2.bootType == AsmApi2.BootType.NEO_FORGE ? m -> new MappingMCP(new MapMappingSrgImplForge(m, new Neo21MapppingMap())) : MappingSrgImplForge::new)){
            @Override
            public @NotNull Set<String> targets() {
                return strings;
            }
        };
    }

    public static void head(ClassNode node, Method method, String name, String desc, int op, OB2_ASM<String, Integer>...local) {
        for (MethodNode methodNode : node.methods) {
            if (methodNode.name.equals(name) && (desc == null || methodNode.desc.equals(desc))) {
                log.info("Replace method name {} to {} in class {}", methodNode.name, method.getName(), node.name);
                methodNode.name = method.getName();
                InsnList instructions = methodNode.instructions;
                for (OB2_ASM<String, Integer> v : local) {
                    switch (v.getT1()) {
                        case "I", "S", "B", "C", "Z" -> instructions.add(new VarInsnNode(Opcodes.ILOAD, v.getT2()));
                        case "J" -> instructions.add(new VarInsnNode(Opcodes.LLOAD, v.getT2()));
                        case "F" -> instructions.add(new VarInsnNode(Opcodes.FLOAD, v.getT2()));
                        case "D" -> instructions.add(new VarInsnNode(Opcodes.DLOAD, v.getT2()));
                        default -> instructions.add(new VarInsnNode(Opcodes.ALOAD, v.getT2()));
                    }

                }

                instructions.add(new MethodInsnNode(op, method.getDeclaringClass().getName().replace(".", "/"), method.getName(), toJVMName(method.getReturnType(), method.getParameterTypes()), false));
            }
        }
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
                        methodInsnNode.desc = desc.isEmpty() ? toJVMName(method.getReturnType(), method.getParameterTypes()) : desc;
                    }
                }
            }
        }
    }
    public static String toJVMName(Class<?> returnType, Class<?>... parameterTypes){
        StringBuilder sb = new StringBuilder("(");
        toJVMName(sb, parameterTypes);
        sb.append(")");
        while (returnType.isArray()) {
            sb.append("[");
            returnType = returnType.getComponentType();
        }
        if (returnType.isPrimitive()) {
            if (returnType == int.class) {
                sb.append("I");
            } else if (returnType == long.class) {
                sb.append("J");
            } else if (returnType == float.class) {
                sb.append("F");
            } else if (returnType == double.class) {
                sb.append("D");
            } else if (returnType == boolean.class) {
                sb.append("Z");
            } else if (returnType == byte.class) {
                sb.append("B");
            } else if (returnType == char.class) {
                sb.append("C");
            } else if (returnType == short.class) {
                sb.append("S");
            } else if (returnType == void.class) {
                sb.append("V");
            }else {
                throw new IllegalArgumentException("Primitive type " + returnType + " is not supported");
            }
        } else {
            sb.append('L');
            String name = returnType.getName();
            sb.append(name.replace('.', '/'));
            sb.append(';');
        }
        return sb.toString();
    }
    public static void toJVMName(StringBuilder sb, Class<?>... parameterTypes){
        for (Class<?> parameterType : parameterTypes) {
            while (parameterType.isArray()) {
                sb.append("[");
                parameterType = parameterType.getComponentType();
            }
            if (parameterType.isPrimitive()) {
                if (parameterType == int.class) {
                    sb.append("I");
                } else if (parameterType == long.class) {
                    sb.append("J");
                } else if (parameterType == float.class) {
                    sb.append("F");
                } else if (parameterType == double.class) {
                    sb.append("D");
                } else if (parameterType == boolean.class) {
                    sb.append("Z");
                } else if (parameterType == byte.class) {
                    sb.append("B");
                } else if (parameterType == char.class) {
                    sb.append("C");
                } else if (parameterType == short.class) {
                    sb.append("S");
                } else if (parameterType == void.class) {
                    sb.append("V");
                }else {
                    throw new IllegalArgumentException("Primitive type " + parameterType + " is not supported");
                }
            } else {
                sb.append('L');
                String name = parameterType.getName();
                sb.append(name.replace('.', '/'));
                sb.append(';');
            }
        }
    }

    public static ClassNode readClass(Class<?> c) {
        return readClass(c.getClassLoader(), c.getName().replace('.', '/') + ".class");
    }
    public static ClassNode readClass(ClassLoader loader, String name) {
        try (var is = loader.getResourceAsStream(name)) {
            if (is == null) {
                throw new RuntimeException("无法找到类文件: " + name);
            }
            ClassNode classNode = new ClassNode();
            new ClassReader(is).accept(classNode, 0);
            return classNode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static MethodNode findMethod(ClassNode node, String name) {
        return findMethod(node, name, null);
    }
    public static MethodNode findMethod(ClassNode node, String name, String desc) {
        for (MethodNode methodNode : node.methods) {
            if (methodNode.name.equals(name) && (desc == null || methodNode.desc.equals(desc))) {
                return methodNode;
            }
        }
        return null;
    }
    public static void replaceMethodImpl(ClassNode node, MethodNode method, String name) {
        replaceMethodImpl(node, method, name, null);
    }

    public static void replaceMethodImpl(ClassNode node, MethodNode method, String name, String desc) {
        if (desc != null && desc.isEmpty())desc = method.desc;
        Iterator<MethodNode> iterator = node.methods.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            MethodNode methodNode = iterator.next();
            if (methodNode.name.equals(name) && (desc == null || methodNode.desc.equals(desc))) {
                found = true;
                iterator.remove();
            }
        }
        if (!found){
            log.error("[]无法找到方法: {} {}", method.name, method.desc);
            return;
        }
        node.methods.add(method);
    }
}
