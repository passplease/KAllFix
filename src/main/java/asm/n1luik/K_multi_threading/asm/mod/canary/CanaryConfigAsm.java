package asm.n1luik.K_multi_threading.asm.mod.canary;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.Set;

public class CanaryConfigAsm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapMethod("com/abdelaziz/canary/common/config/CanaryConfig.getEffectiveOptionForMixin(Ljava/lang/String;)Lcom/abdelaziz/canary/common/config/Option;");
        boolean debug_add1 = false;
        boolean debug_add2 = false;
        String kMultiThreading$FixTest = "K_multi_threading$FixTest";

        for (MethodNode method : input.methods) {
            if ((method.name.equals(strings[1]) && method.desc.equals(strings[2]))){
                debug_add1 = true;



                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction.getOpcode() != -1){
                        debug_add2 = true;
                        LabelNode labelNode = new LabelNode();
                        labelNode.getLabel().info = labelNode;
                        InsnList abstractInsnNodes = new InsnList();

                        abstractInsnNodes.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        abstractInsnNodes.add(new MethodInsnNode(Opcodes.INVOKESTATIC, input.name, kMultiThreading$FixTest, "(Ljava/lang/String;)Z"));
                        abstractInsnNodes.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));

                        //abstractInsnNodes.add(new InsnNode(Opcodes.ICONST_0));
                        //abstractInsnNodes.add(new InsnNode(Opcodes.IRETURN));

                        //abstractInsnNodes.add(new TypeInsnNode(Opcodes.NEW, "com/abdelaziz/canary/common/config/Option"));
                        //abstractInsnNodes.add(new InsnNode(Opcodes.DUP));
                        //abstractInsnNodes.add(new LdcInsnNode("K_multi_threading$Fix"));
                        //abstractInsnNodes.add(new InsnNode(Opcodes.ICONST_0));
                        //abstractInsnNodes.add(new InsnNode(Opcodes.ICONST_0));
                        //abstractInsnNodes.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/abdelaziz/canary/common/config/Option", "<init>", "(Ljava/lang/String;ZZ)V", false));
                        abstractInsnNodes.add(new InsnNode(Opcodes.ACONST_NULL));
                        abstractInsnNodes.add(new InsnNode(Opcodes.ARETURN));

                        abstractInsnNodes.add(labelNode);

                        method.instructions.insertBefore(instruction, abstractInsnNodes);
                        break;
                    }
                }
                method.maxStack += 5;
            }
        }

        {
            MethodVisitor methodVisitor = input.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, kMultiThreading$FixTest, "(Ljava/lang/String;)Z", null, null);
            Label l1 = new Label();
            Label l2 = new Label();
            Label l3 = new Label();
            methodVisitor.visitLocalVariable("className", "Ljava/lang/String;", null, l1, l2, 0);
            methodVisitor.visitCode();

            methodVisitor.visitLabel(l1);
            addRemove(methodVisitor, l3, "collections.entity_by_type.");
            addRemove(methodVisitor, l3, "world.tick_scheduler.");
            //addRemove(methodVisitor, l3, "com.abdelaziz.canary.mixin.collections.entity_by_type.");
            //addRemove(methodVisitor, l3, "com.abdelaziz.canary.mixin.world.tick_scheduler.");
            //addRemove(methodVisitor, l3, "com.abdelaziz.canary.mixin.collections.entity_filtering.");
            //addRemove(methodVisitor, l3, "com.abdelaziz.canary.mixin.chunk.entity_class_groups.");
            //addRemove(methodVisitor, l3, "com.abdelaziz.canary.mixin.entity.collisions.unpushable_cramming.");
            methodVisitor.visitInsn(Opcodes.ICONST_1);
            methodVisitor.visitInsn(Opcodes.IRETURN);


            methodVisitor.visitLabel(l3);
            methodVisitor.visitInsn(Opcodes.ICONST_0);
            methodVisitor.visitInsn(Opcodes.IRETURN);
            methodVisitor.visitLabel(l2);

            methodVisitor.visitMaxs(0, 0);
        }

        if (!debug_add1 || !debug_add2){
            throw new RuntimeException("Not mapping error: com.abdelaziz.canary.common.config.CanaryConfig %s %s".formatted(debug_add1, debug_add2));
        }
        if (Boolean.getBoolean("KAF-CanaryConfigAsmDebug")){
            //吧input写入debug.class用于调试
            try {
                ClassWriter classWriter = new ClassWriter(0);
                input.accept(classWriter);
                java.nio.file.Files.write(java.nio.file.Paths.get("./CanaryConfigAsmDebug.class"), classWriter.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        return input;
    }

    public static void addRemove(MethodVisitor methodVisitor, Label stop, String name) {
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitLdcInsn(name);
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
        methodVisitor.visitJumpInsn(Opcodes.IFEQ, stop);
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return Set.of(
                Target.targetClass("com.abdelaziz.canary.common.config.CanaryConfig"));
    }
}
