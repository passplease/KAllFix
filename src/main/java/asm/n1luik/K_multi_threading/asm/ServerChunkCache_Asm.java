package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Set;

@Deprecated
@Slf4j
public class ServerChunkCache_Asm extends ITransformer2 {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {
        boolean debug_add1 = false;

        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>")){
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction.getOpcode() == Opcodes.NEW && instruction instanceof TypeInsnNode typeInsnNode
                            && typeInsnNode.desc.equals("net/minecraft/server/level/ServerChunkCache")){
                        log.info("set ServerLevel ParaServerChunkProvider");
                        debug_add1 = true;
                        typeInsnNode.desc = "n1luik/K_multi_threading/core/base/ParaServerChunkProvider";
                    }else if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode
                            && methodInsnNode.owner.equals("net/minecraft/server/level/ServerChunkCache")){
                        log.info("set ServerLevel ParaServerChunkProvider2");
                        if (!debug_add1){
                            throw new RuntimeException("Not method error: <init>");
                        }
                        methodInsnNode.owner = "n1luik/K_multi_threading/core/base/ParaServerChunkProvider";
                    }
                }
            }
        }

        if (!debug_add1){
            throw new RuntimeException("Not method error: <init>");
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "net/minecraft/server/level/ServerLevel");
    }
}
