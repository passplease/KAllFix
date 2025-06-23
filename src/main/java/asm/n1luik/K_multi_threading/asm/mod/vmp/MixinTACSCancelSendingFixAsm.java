package asm.n1luik.K_multi_threading.asm.mod.vmp;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.Set;

public class MixinTACSCancelSendingFixAsm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean add2 = false;
        boolean add1 = false;
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.move(Lnet/minecraft/server/level/ServerPlayer;)V");
        String[] strings2 = ForgeAsm.minecraft_map.mapMethod("net/minecraft/core/SectionPos.x()I");
        for (MethodNode method : input.methods) {
            if (method.name.equals(strings[1]) && method.desc.equals(strings[2])) {
                add1 = true;
                AbstractInsnNode add = null;
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction instanceof MethodInsnNode methodInsnNode
                            && methodInsnNode.owner.equals(strings2[0])
                            && methodInsnNode.name.equals(strings2[1])
                            && methodInsnNode.desc.equals(strings2[2])){
                        add = instruction;
                        add2 = true;
                        break;
                    }

                }
                assert add != null;
                InsnList instructions = new InsnList();
                method.instructions.insertBefore(add, new InsnNode(Opcodes.RETURN));

            }
        }


        if (!(add1 && add2)){
            throw new RuntimeException("Not mapping error: net.minecraft.server.level.ChunkMap %s %s".formatted(add1, add2));
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
                Target.targetClass("net.minecraft.server.level.ChunkMap"));
    }
}
