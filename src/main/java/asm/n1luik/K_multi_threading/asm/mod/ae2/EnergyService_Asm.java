package asm.n1luik.K_multi_threading.asm.mod.ae2;

import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Set;

@Deprecated
public class EnergyService_Asm extends ITransformer2 {
    @Override
    public @NotNull ClassNode transform(ClassNode input) {
        for (MethodNode method : input.methods) {
            if (method.name.equals("injectProviderPower")){

            }
        }
        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {
        return Set.of(
                "appeng/me/service/EnergyService");
    }
}
