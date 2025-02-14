package asm.n1luik.KAllFix.asm.mod.fabric_object_builder_api;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Set;

public class TradeOffersTypeAwareBuyForOneEmeraldFactoryMixinAsm implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        try {
            ClassNode classNode = new ClassNode();
            byte[] bytes = TradeOffersTypeAwareBuyForOneEmeraldFactoryMixinAsm.class.getResourceAsStream("/fix/KAllFix/fabric-object-builder-api-v1-11.1.3/TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin.class").readAllBytes();
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
