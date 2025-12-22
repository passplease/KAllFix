package asm.n1luik.K_multi_threading.asm.util;

import org.objectweb.asm.tree.ClassNode;

import java.util.Set;

/**
 * 在不存在forge插件接口的实现方案
 */
public abstract class ITransformer2 {
    public ClassNode transform(ClassNode input) {
        return input;
    }
    public abstract Set<String> targets();
}
