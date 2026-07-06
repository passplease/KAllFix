package asm.n1luik.K_multi_threading.asm.mod.create;

import asm.n1luik.K_multi_threading.asm.mod.valkyrienskies.AddMapConcurrent;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;

@Slf4j
public class BeltInventory_ASM extends ITransformer2 {

    @Override
    public @NotNull ClassNode transform(ClassNode input) {
        log.info("[{}]", input.name);
        for (MethodNode method : input.methods) {
            AbstractInsnNode[] abstractInsnNodes = method.instructions.toArray();
            method.instructions.clear();
            for (AbstractInsnNode instruction : abstractInsnNodes) {
                if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode) {
                    switch (methodInsnNode.owner) {
                        case "java/util/LinkedList":
                            if (methodInsnNode.name.equals("<init>")) {
                                if (methodInsnNode.desc.equals("()V")) {
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
                                } else if (methodInsnNode.desc.equals("(I)V")) {
                                    method.instructions.add(new InsnNode(Opcodes.POP));
                                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
                                } else {
                                    method.instructions.add(instruction);
                                }
                            } else {
                                method.instructions.add(instruction);
                            }
                            break;
                        default:
                            method.instructions.add(instruction);

                            break;
                    }
                } else if (instruction.getOpcode() == Opcodes.NEW && instruction instanceof TypeInsnNode typeInsnNode) {
                    switch (typeInsnNode.desc) {
                        case "java/util/LinkedList" ->
                                method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArrayList"));
                        default -> method.instructions.add(instruction);
                    }
                } else {
                    method.instructions.add(instruction);
                }
            }
        }
        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "com.simibubi.create.content.kinetics.belt.transport.BeltInventory",
                "com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity"
        );
    }
}
