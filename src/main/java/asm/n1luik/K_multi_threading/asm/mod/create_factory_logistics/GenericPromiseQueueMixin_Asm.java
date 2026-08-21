package asm.n1luik.K_multi_threading.asm.mod.create_factory_logistics;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class GenericPromiseQueueMixin_Asm extends ITransformer2 {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {
        boolean debug_add1 = false;
        boolean debug_add2 = false;

        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>")){
                debug_add1 = true;
                InsnList instructions = method.instructions;
                InsnList instructions2 = method.instructions = new InsnList();



                for (AbstractInsnNode instruction : instructions) {
                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode
                            && methodInsnNode.owner.equals("com/google/common/collect/HashMultimap")
                            && methodInsnNode.name.equals("create")){
                        instructions2.add(instruction);
                        instructions2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/google/common/collect/Multimaps", "synchronizedMultimap", "(Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/Multimap;"));
                        debug_add2 = true;
                    }else
                        instructions2.add(instruction);
                }

                method.maxStack++;
            }
        }

        if (!debug_add1 || !debug_add2){
            throw new RuntimeException("Not mapping error: ru.zznty.create_factory_logistics.mixin.logistics.packager.GenericPromiseQueueMixin %s %s".formatted(debug_add1, debug_add2));
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "ru.zznty.create_factory_logistics.mixin.logistics.packager.GenericPromiseQueueMixin");
    }
}
