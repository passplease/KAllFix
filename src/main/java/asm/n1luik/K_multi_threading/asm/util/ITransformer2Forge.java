package asm.n1luik.K_multi_threading.asm.util;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ITransformer2Forge implements ITransformer<ClassNode> {
    public final ITransformer2 transformer;


    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        return transformer.transform(input);
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return transformer.targets().stream().map(Target::targetClass).collect(Collectors.toCollection(HashSet::new));
    }

}
