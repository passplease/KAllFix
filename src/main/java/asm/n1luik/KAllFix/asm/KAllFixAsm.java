package asm.n1luik.KAllFix.asm;

import asm.n1luik.KAllFix.asm.mod.createdieselgenerators.EntityMixinAsm;
import asm.n1luik.KAllFix.asm.mod.fabric_object_builder_api.ReadClassAsm;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KAllFixAsm implements ITransformationService {
    @Override
    public @NotNull String name() {
        return "KAllFixAsm";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        List<ITransformer> transformers = new ArrayList<>(List.of(
                new EntityMixinAsm()
        ));
        if (Boolean.getBoolean("KAF-Fix_fabric-object-builder-api.jar")){
            transformers.add(new ReadClassAsm("fabric-object-builder-api", "v1-11.1.3", "TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin"));
        }
        return transformers;
    }
}
