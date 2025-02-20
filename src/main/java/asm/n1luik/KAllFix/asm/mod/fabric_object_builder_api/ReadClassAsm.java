package asm.n1luik.KAllFix.asm.mod.fabric_object_builder_api;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Set;

public record ReadClassAsm(String file, String version, String target) implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        try {
            ClassNode classNode = new ClassNode();
            byte[] bytes = ReadClassAsm.class.getResourceAsStream("/asm/KAllFix.fix/"+file+"/"+version+"/"+target+".fix").readAllBytes();
            bytes[0] = (byte)0xCA;
            bytes[1] = (byte)0xFE;
            bytes[2] = (byte)0xBA;
            bytes[3] = (byte)0xBE;
            new ClassReader(bytes).accept(classNode, 0);
            return classNode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return Set.of(
                Target.targetClass("net.fabricmc.fabric.mixin.object.builder.TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin")
        );
    }
}
