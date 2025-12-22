package asm.n1luik.KAllFix.asm.mod.gcyr;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class GcyrCanaryMapping_Asm extends ITransformer2 {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {

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
    public @NotNull Set<String> targets() {
        return Set.of(
                "com.abdelaziz.canary.mixin.entity.collisions.intersection.LevelMixin",
                "com.abdelaziz.canary.mixin.entity.collisions.movement.EntityMixin"
        );
    }
}
