package asm.n1luik.KAllFix.asm.mod.tfmg;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class DestroyFix_Asm implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;
        boolean debug_add2 = false;
        String[] strings = ForgeAsm.minecraft_map.mapMethod("com/drmangotea/tfmg/mixins/FluidPropagatorMixin.propagateChangedPipe(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V");
        String[] strings2 = ForgeAsm.minecraft_map.mapMethod("com/tterrag/registrate/util/entry/BlockEntry.has(Lnet/minecraft/world/level/block/state/BlockState;)Z");

        for (MethodNode method : input.methods) {
            if (method.name.equals(strings[1]) && method.desc.equals(strings[2])) {
                debug_add2 = true;
                InsnList instructions = method.instructions;
                method.instructions = new InsnList();
                //method.maxStack += 2;
                LabelNode label = new LabelNode();
                label.getLabel().info = label;
                LabelNode label2 = new LabelNode();
                label2.getLabel().info = label2;
                //AbstractInsnNode il = null;
                //AbstractInsnNode il2 = null;
                method.visitLocalVariable("DestroyFix_Asm", "Z", null, label.getLabel(), label2.getLabel(), method.maxLocals);

                //for (AbstractInsnNode instruction : instructions) {
                //    if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode) {
                //        if (methodInsnNode.owner.equals(strings2[0]) && methodInsnNode.name.equals(strings2[1]) && methodInsnNode.desc.equals(strings2[2])) {
                //            il = instruction.getNext();
                //            while (!(il instanceof JumpInsnNode))
                //                il = il.getNext();
                //            //il2 = instruction.getPrevious();
                //            //while (!(il2 instanceof VarInsnNode))
                //            //    il2 = il2.getPrevious();
                //            //il2 = il2.clone(null);
                //        }
                //    }
                //}
                //assert il != null;

                for (AbstractInsnNode instruction : instructions) {
                    if (!debug_add1 && instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode) {
                        if (methodInsnNode.owner.equals(strings2[0]) && methodInsnNode.name.equals(strings2[1]) && methodInsnNode.desc.equals(strings2[2])) {
                            debug_add1 = true;

                            method.instructions.add(new InsnNode(Opcodes.DUP));
                            method.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/petrolpark/destroy/DestroyBlocks", "CREATIVE_PUMP", "Lcom/tterrag/registrate/util/entry/BlockEntry;"));
                            method.instructions.add(new InsnNode(Opcodes.SWAP));
                            method.instructions.add(label);
                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, strings2[0], strings2[1], strings2[2]));
                            //method.instructions.add(new JumpInsnNode(Opcodes.IFNE, label));
                            method.instructions.add(new VarInsnNode(Opcodes.ISTORE, method.maxLocals));
                            method.instructions.add(instruction);
                            method.instructions.add(new VarInsnNode(Opcodes.ILOAD, method.maxLocals));
                            method.instructions.add(label2);
                            //method.instructions.add(new InsnNode(Opcodes.IOR));
                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "logicalOr", "(ZZ)Z"));


                            //method.instructions.add(il2);
                        }else {
                            method.instructions.add(instruction);
                        }
                    } else {
                        method.instructions.add(instruction);
                    }
                }
                //method.instructions.insertBefore(il, label);
                method.visitMaxs(0, 0);
            }
        }
        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        //                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
        //                                    "concurrentMap",
        //                                    "(Ljava/util/Map;)Ljava/util/concurrent/ConcurrentHashMap;"))
        if (!(debug_add1)){
            throw new RuntimeException("Not mapping error: com.drmangotea.tfmg.mixins.FluidPropagatorMixin: %s %s ".formatted(debug_add1, debug_add2));
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
                Target.targetClass("com.drmangotea.tfmg.mixins.FluidPropagatorMixin"));
    }
}
