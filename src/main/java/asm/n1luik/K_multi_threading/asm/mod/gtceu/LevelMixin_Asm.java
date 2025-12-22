package asm.n1luik.K_multi_threading.asm.mod.gtceu;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class LevelMixin_Asm extends ITransformer2 {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input) {
        for (MethodNode method : input.methods) {
            if (/*method.name.equals("getBlockState") || */method.name.equals("getTileEntity")) {
                if(method.visibleAnnotations != null)method.visibleAnnotations.clear();
                if(method.invisibleAnnotations != null)method.invisibleAnnotations.clear();
            }
        }
        return input;
    }

    @Override
    public @NotNull Set<String> targets() {
        return Set.of("com.gregtechceu.gtceu.core.mixins.LevelMixin");
    }
}
