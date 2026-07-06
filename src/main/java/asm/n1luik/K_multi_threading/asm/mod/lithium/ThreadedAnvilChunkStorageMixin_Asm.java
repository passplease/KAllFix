package asm.n1luik.K_multi_threading.asm.mod.lithium;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

@Slf4j
public class ThreadedAnvilChunkStorageMixin_Asm extends ITransformer2 {
    //@NotNull
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {
        String[] strings = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ChunkMap.playerMap");
        boolean debug_add1 = false;

        for (MethodNode method : input.methods) {
            if (method.name.equals("m_140184_")){
                String name = strings[1];
                {
                    debug_add1 = true;
                    if ((input.access & Opcodes.ACC_STATIC) == 0) {
                        if ((input.access & Opcodes.ACC_INTERFACE) == 0) {
                            InsnList start;
                            InsnList end;

                            Label rstart = new Label();
                            Label rend = new Label();
                            method.visitTryCatchBlock(rstart, rend, rend, null);
                            start = new InsnList();
                            start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            start.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Lnet/minecraft/server/level/PlayerMap;"));
                            start.add(new InsnNode(Opcodes.MONITORENTER));
                            start.add((LabelNode) rstart.info);
                            end = new InsnList();
                            end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Lnet/minecraft/server/level/PlayerMap;"));
                            end.add(new InsnNode(Opcodes.MONITOREXIT));
                            InsnList il = method.instructions;
                            AbstractInsnNode ain = il.getFirst();
                            while (ain != null) {
                                if (ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                                        || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                                        || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                                    il.insertBefore(ain, end);
                                    end = new InsnList();
                                    end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Lnet/minecraft/server/level/PlayerMap;"));
                                    end.add(new InsnNode(Opcodes.MONITOREXIT));
                                }
                                ain = ain.getNext();
                            }
                            il.insertBefore(il.getFirst(), start);
                            method.visitLabel(rend);
                            method.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
                            method.instructions.add(end);
                            method.visitInsn(Opcodes.ATHROW);
                            method.maxStack++;
                        }

                    } else {
                        input.access &= Opcodes.ACC_SYNTHETIC;
                    }
                }
            }
        }

        if (!debug_add1){
            log.error("Not method error");//throw new RuntimeException("Not method");
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "me/jellysquid/mods/lithium/mixin/world/player_chunk_tick/ThreadedAnvilChunkStorageMixin");
    }
}
