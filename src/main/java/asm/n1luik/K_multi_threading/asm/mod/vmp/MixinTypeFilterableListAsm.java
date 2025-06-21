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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MixinTypeFilterableListAsm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean add1 = false;
        String[] strings = ForgeAsm.minecraft_map.mapMethod("com/ishland/vmp/mixins/general/collections/MixinTypeFilterableList.getBackingArray()[Ljava/lang/Object;");
        String[] strings2 = ForgeAsm.minecraft_map.mapField("net/minecraft/util/ClassInstanceMultiMap.allInstances");
        Iterator<MethodNode> iterator = input.methods.iterator();
        while (iterator.hasNext()){
            MethodNode method = iterator.next();
            if (method.name.equals(strings[1]) && method.desc.equals(strings[2])){
                iterator.remove();
                add1 = true;
                break;
            }
        }
        MethodVisitor methodVisitor = input.visitMethod(Opcodes.ACC_PUBLIC, strings[1], strings[2], null, null);
        Label label = new Label();
        Label label2 = new Label();
        methodVisitor.visitLocalVariable("this", "Lcom/ishland/vmp/mixins/general/collections/MixinTypeFilterableList;", null, label, label2, 0);
        methodVisitor.visitCode();
        methodVisitor.visitLabel(label);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, input.name, strings2[1], "Ljava/util/List;");
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Collection", "size", "()I", true);
        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Collection", "toArray", "([Ljava/lang/Object;)Ljava/lang/Object;", true);
        methodVisitor.visitInsn(Opcodes.ARETURN);

        methodVisitor.visitLabel(label2);
        methodVisitor.visitMaxs(4, 1);


        if (!add1){
            throw new RuntimeException("Not mapping error: com.ishland.vmp.mixins.general.collections.MixinTypeFilterableList");
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
                Target.targetClass("com.ishland.vmp.mixins.general.collections.MixinTypeFilterableList"));
    }
}
