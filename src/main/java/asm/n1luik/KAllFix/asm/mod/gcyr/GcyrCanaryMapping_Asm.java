package asm.n1luik.KAllFix.asm.mod.gcyr;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class GcyrCanaryMapping_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {

        for (MethodNode method : input.methods) {


            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode
                        && methodInsnNode.owner.equals("com/abdelaziz/canary/common/entity/CanaryEntityCollisions")){
                    methodInsnNode.owner = "n1luik/KAllFix/fix/canary/GcyrCanaryEntityCollisions";
                }
            }
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
                Target.targetClass("com.abdelaziz.canary.mixin.entity.collisions.intersection.LevelMixin"),
                Target.targetClass("com.abdelaziz.canary.mixin.entity.collisions.movement.EntityMixin")
        );
    }
}
