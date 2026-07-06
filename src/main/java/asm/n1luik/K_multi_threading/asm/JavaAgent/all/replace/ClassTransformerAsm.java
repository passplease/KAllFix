package asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace;

import org.objectweb.asm.tree.ClassNode;

public class ClassTransformerAsm {
    public static int code1(ClassNode input, int i) {
        if (input.name.startsWith("asm/n1luik/K_multi_threading") || input.name.startsWith("n1luik/K_multi_threading")
        || input.name.startsWith("n1luik/KAllFix") || input.name.startsWith("asm/n1luik/KAllFix")) {
            return 0;
        }
        return i;
    }
}
