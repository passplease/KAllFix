package asm.n1luik.K_multi_threading.asm.mod.canary;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class ServerChunkCacheMixin_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;");
        String[] strings2 = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerChunkCache.mainThread");
        boolean debug_add1 = false;
        boolean debug_add2 = false;

        for (MethodNode method : input.methods) {
            if ((method.name.equals(strings[1]) && method.desc.equals(strings[2]))){
                InsnList instructions = method.instructions;
                InsnList instructions2 = method.instructions = new InsnList();
                debug_add1 = true;



                for (AbstractInsnNode instruction : instructions) {
                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode
                            && methodInsnNode.owner.equals("java/lang/Thread")
                            && methodInsnNode.name.equals("currentThread")){
                        debug_add2 = true;

                        instructions2.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        instructions2.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, strings2[1], "Ljava/lang/Thread;"));
                    }else {
                        instructions2.add(instruction);
                    }
                }
                method.maxStack++;
            }
        }

        if (!debug_add1 || !debug_add2){
            throw new RuntimeException("Not mapping error: com.abdelaziz.canary.mixin.world.chunk_access.ServerChunkCacheMixin %s %s".formatted(debug_add1, debug_add2));
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
                Target.targetClass("com.abdelaziz.canary.mixin.world.chunk_access.ServerChunkCacheMixin"));
    }
}
