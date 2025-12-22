package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

@Deprecated
public class ChunkMap_Asm extends ITransformer2{
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
                    if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode) {
                        if (methodInsnNode.owner.equals("it/unimi/dsi/fastutil/longs/LongOpenHashSet")) {
                            if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/LongConcurrentHashSet", "<init>", "()V", false));
                            } else {
                                method.instructions.add(instruction);
                            }
                        } else {
                            method.instructions.add(instruction);
                        }
                        debug_add2 = true;
                    } else if (instruction.getOpcode() == Opcodes.NEW && instruction instanceof TypeInsnNode typeInsnNode) {
                        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        //        "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                        //        "concurrentLongSet",
                        //        "()Lit/unimi/dsi/fastutil/longs/LongSortedSet;"));
                        if (typeInsnNode.desc.equals("it/unimi/dsi/fastutil/longs/LongOpenHashSet")) {
                            method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/LongConcurrentHashSet"));
                        } else {
                            method.instructions.add(instruction);
                        }
                    } else {
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
            throw new RuntimeException("Not mapping error: net.minecraft.server.level.ChunkMap: %s %s ".formatted(debug_add1, debug_add2));
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "net.minecraft.server.level.ChunkMap");
    }
}
