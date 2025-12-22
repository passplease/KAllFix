package asm.n1luik.K_multi_threading.asm.JavaAgent;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;

@Slf4j
public class AsmUtil {

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
