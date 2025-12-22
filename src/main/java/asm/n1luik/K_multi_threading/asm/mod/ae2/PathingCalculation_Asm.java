package asm.n1luik.K_multi_threading.asm.mod.ae2;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class PathingCalculation_Asm extends ITransformer2 {
    @Override
    public @NotNull ClassNode transform(ClassNode input) {
        boolean stopAdd = false;
        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>") && method.desc.equals("(Lappeng/api/networking/IGrid;)V")){
                AbstractInsnNode[] abstractInsnNodes = method.instructions.toArray();
                method.instructions.clear();
                for (AbstractInsnNode abstractInsnNode : abstractInsnNodes) {
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKESPECIAL && !stopAdd) {
                        method.instructions.add(abstractInsnNode);
                        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        method.instructions.add(new InsnNode(Opcodes.MONITORENTER));
                        stopAdd = true;
                    } else if (abstractInsnNode.getOpcode() == Opcodes.RETURN || abstractInsnNode.getOpcode() == Opcodes.ARETURN
                            || abstractInsnNode.getOpcode() == Opcodes.DRETURN || abstractInsnNode.getOpcode() == Opcodes.FRETURN
                            || abstractInsnNode.getOpcode() == Opcodes.IRETURN || abstractInsnNode.getOpcode() == Opcodes.LRETURN) {
                        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        method.instructions.add(new InsnNode(Opcodes.MONITOREXIT));
                        method.instructions.add(abstractInsnNode);
                        stopAdd = true;
                    } else {
                        method.instructions.add(abstractInsnNode);
                    }
                }
                method.maxStack++;
            }
        }
        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of("appeng/me/pathfinding/PathingCalculation");
    }
}
