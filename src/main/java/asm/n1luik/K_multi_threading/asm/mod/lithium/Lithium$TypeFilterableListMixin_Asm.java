package asm.n1luik.K_multi_threading.asm.mod.lithium;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Set;

@Slf4j
@Deprecated
public class Lithium$TypeFilterableListMixin_Asm extends ITransformer2 {
    //@NotNull
    //@Override
    //public ClassNode transform(ClassNode input) {
    //    String[] strings = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerChunkCache.mainThread");
    //    boolean debug_add1 = false;
//
    //    if (input.name.equals(strings[0])){
    //        for (MethodNode method : input.methods) {
    //            if (method.name.equals("handler$zdl000$bailIfServerDead")){
    //                InsnList instructions = method.instructions;
    //                InsnList instructions2 = method.instructions = new InsnList();
    //                for (AbstractInsnNode instruction : instructions) {
    //                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode
    //                            && methodInsnNode.owner.equals("java/lang/Thread") && methodInsnNode.name.equals("currentThread")) {
    //                        debug_add1 = true;
    //                        log.info("ModernfixGetChunkSynchronized Asm");
    //                        instructions2.add(new VarInsnNode(Opcodes.ALOAD, 0));//this
    //                        instructions2.add(new FieldInsnNode(Opcodes.GETFIELD, strings[0], strings[1], "Ljava/lang/Thread;"));//this
    //                    }
    //                    instructions2.add(instruction);
    //                }
    //            }
    //        }
    //    }
//
    //    if (!debug_add1){
    //        log.error("Not method error");
    //    }
//
//
    //    return input;
    //}
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {
        String[] strings = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerChunkCache.mainThread");
        log.info("LithiumGetChunkSynchronized Asm");

        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>")){
                input.methods = new ArrayList<>();
                input.methods.add(method);
                return input;
            }
        }

        log.error("Not method error");//throw new RuntimeException("Not method");


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                //"net/minecraft/server/level/ServerChunkCache");
                "me/jellysquid/mods/lithium/mixin/collections/entity_by_type/TypeFilterableListMixin");
    }
}
