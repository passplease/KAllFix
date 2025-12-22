package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

@Slf4j
public class ImplDistanceManager1_Asm extends ITransformer2 {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {
        boolean debug_add1 = false;
        boolean debug_add2 = false;

        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>")) {
                //debug_add2 = true;
                InsnList instructions = method.instructions;
                method.instructions = new InsnList();
                debug_add1 = true;

                for (AbstractInsnNode instruction : instructions) {

                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode) {
                        if (methodInsnNode.owner.equals("com/google/common/collect/Sets")) {
                            if (methodInsnNode.name.equals("newHashSet")) {
                                debug_add2 = true;
                                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/concurrent/ConcurrentHashMap", "newKeySet", "()Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", false));
                            } else {
                                method.instructions.add(instruction);
                            }
                        } else {
                            method.instructions.add(instruction);
                        }
                    }else {
                        method.instructions.add(instruction);
                    }
                }
            }
        }
        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        //                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
        //                                    "concurrentMap",
        //                                    "(Ljava/util/Map;)Ljava/util/concurrent/ConcurrentHashMap;"))
        if (!(debug_add1 && debug_add2)){
            throw new RuntimeException("Not mapping error: net.minecraft.server.level.DistanceManager: %s %s ".formatted(debug_add1, debug_add2));
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "net.minecraft.server.level.DistanceManager");
    }
}
