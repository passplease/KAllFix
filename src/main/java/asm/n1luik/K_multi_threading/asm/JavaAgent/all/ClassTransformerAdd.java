package asm.n1luik.K_multi_threading.asm.JavaAgent.all;

import asm.n1luik.K_multi_threading.asm.JavaAgent.JavaAgent;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Set;

@Slf4j
public class ClassTransformerAdd extends ITransformer2 {


    @Override
    public ClassNode transform(ClassNode input) {
        MethodVisitor m = input.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "createClassWriter_",
                "(ILcpw/mods/modlauncher/ClassTransformer;Lorg/objectweb/asm/tree/ClassNode;)Lorg/objectweb/asm/ClassWriter;"
                , null, null);

        ClassLoader platformClassLoader = Thread.currentThread().getContextClassLoader();
        log.info("ClassTransformerAdd.transform: platformClassLoader = {}", platformClassLoader);
        JavaAgent.loadClass(platformClassLoader, "asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace.ClassTransformerAsm");


        m.visitCode();
        m.visitVarInsn(Opcodes.ALOAD, 2);
        m.visitVarInsn(Opcodes.ILOAD, 0);
        m.visitMethodInsn(Opcodes.INVOKESTATIC, "asm/n1luik/K_multi_threading/asm/JavaAgent/all/replace/ClassTransformerAsm",
                "code1", "(Lorg/objectweb/asm/tree/ClassNode;I)I", false);
        m.visitVarInsn(Opcodes.ALOAD, 1);
        m.visitVarInsn(Opcodes.ALOAD, 2);
        m.visitMethodInsn(Opcodes.INVOKESTATIC, "cpw/mods/modlauncher/TransformerClassWriter",
                "createClassWriter", "(ILcpw/mods/modlauncher/ClassTransformer;Lorg/objectweb/asm/tree/ClassNode;)Lorg/objectweb/asm/ClassWriter;", false);
        m.visitInsn(Opcodes.ARETURN);
        m.visitMaxs(3, 3);
        m.visitEnd();

        for (MethodNode method : input.methods) {
            if (method.name.equals("transform")) {
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode m2){
                        if (m2.owner.equals("cpw/mods/modlauncher/TransformerClassWriter")
                                && m2.name.equals("createClassWriter")){
                            m2.owner = input.name;
                            m2.name = "createClassWriter_";
                        }
                    }
                }
            }
        }
        return input;
    }

    @Override
    public Set<String> targets() {
        return Set.of("cpw.mods.modlauncher.ClassTransformer");
    }
}
