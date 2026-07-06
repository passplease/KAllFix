package asm.n1luik.K_multi_threading.asm.util;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TargetType;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@AllArgsConstructor
public class ITransformer2NeoForge implements ITransformer<ClassNode> {
    public final ITransformer2 transformer;


    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        try{
            return transformer.transform(input);
        }catch (Throwable e){
            log.error("transform error", e);
            throw e;
        }
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target<ClassNode>> targets() {
        return transformer.targets().stream().map(Target::targetClass).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public @NotNull TargetType<ClassNode> getTargetType() {
        return TargetType.CLASS;
    }

}
