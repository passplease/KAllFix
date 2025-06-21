package asm.n1luik.K_multi_threading.asm.mc1_19;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class TruePacketThreadTestAsm implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/network/PacketListener.isAcceptingMessages()Z");
        String[] strings2 = ForgeAsm.minecraft_map.mapMethod("net/minecraft/network/PacketListener.getConnection()Lnet/minecraft/network/Connection;");
        String[] strings3 = ForgeAsm.minecraft_map.mapMethod("net/minecraft/network/Connection.isConnected()Z");
        for (MethodNode method : input.methods) {
            InsnList instructions = method.instructions;
            InsnList instructions1 = method.instructions = new InsnList();
            boolean add = false;
            for (AbstractInsnNode instruction : instructions) {
                if (instruction.getOpcode() == Opcodes.INVOKEINTERFACE && instruction instanceof MethodInsnNode methodInsnNode) {
                    if (methodInsnNode.owner.equals(strings[0]) && methodInsnNode.name.equals(strings[1]) && methodInsnNode.desc.equals(strings[2])) {
                        add = true;
                        instructions1.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, strings2[0], strings2[1], strings2[2], true));
                        instructions1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, strings3[0], strings3[1], strings3[2]));
                    }else {
                        instructions1.add(instruction);
                    }
                }else {
                    instructions1.add(instruction);
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
        return Set.of(Target.targetClass("n1luik.K_multi_threading.core.util.TruePacketThreadTest"));
    }
}
