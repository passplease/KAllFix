package asm.n1luik.K_multi_threading.asm.mod.valkyrienskies;

import asm.n1luik.KAllFix.asm.util.StackUtil;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.objectweb.asm.tree.*;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

import java.util.Set;

public class ShipObjectServerWorld_Asm  implements ITransformer<ClassNode>{
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;
        boolean debug_add2 = false;

        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>")) {
                debug_add1 = true;
                InsnList instructions = method.instructions;
                InsnList al2 = method.instructions = new InsnList();
                AbstractInsnNode abstractInsnNode = StackUtil.previousStack(1, StackUtil.findField("shipToVoxelUpdates", instructions), true);
                for (AbstractInsnNode instruction : instructions) {
                    if (abstractInsnNode == instruction){
                        if (instruction instanceof MethodInsnNode methodInsnNode) {
                            debug_add2 = true;
                            al2.add(new MethodInsnNode(methodInsnNode.getOpcode(),
                                    "n1luik/K_multi_threading/fix/valkyrienskies/LongObjConcurrentMapUtil",
                                    "create",
                                    methodInsnNode.desc));
                        }else {
                            al2.add(instruction);
                        }
                    }else {
                        al2.add(instruction);
                    }
                }

            }
        }
        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        //                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
        //                                    "concurrentMap",
        //                                    "(Ljava/util/Map;)Ljava/util/concurrent/ConcurrentHashMap;"))
        if (!(debug_add1 && debug_add2)){
            throw new RuntimeException("Not mapping error: org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld: %s %s ".formatted(debug_add1, debug_add2));
        }


        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return Set.of(
                ITransformer.Target.targetClass("org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld"));
    }
}
