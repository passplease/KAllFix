package asm.n1luik.KAllFix.asm;

import asm.n1luik.KAllFix.asm.mod.createdieselgenerators.EntityMixinAsm;
import asm.n1luik.KAllFix.asm.mod.fabric_object_builder_api.ReadClassAsm;
import asm.n1luik.KAllFix.asm.mod.jei.JEI_AddMapConcurrent_ASM;
import asm.n1luik.KAllFix.asm.mod.jei.JEI_NotErrorAddSynchronized_Asm;
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
            transformers.add(new ReadClassAsm("fabric-object-builder-api", "v1-11.1.3", "TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin", Set.of(
                    ITransformer.Target.targetClass("net.fabricmc.fabric.mixin.object.builder.TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin")
            )));
        }
        if (Boolean.getBoolean("KAF-UnsafeCinderscapesFix1")){
            transformers.add(new ReadClassAsm("cinderscapes", "all", "MixinServerWorld", Set.of(
                    ITransformer.Target.targetClass("com.terraformersmc.cinderscapes.mixin.MixinServerWorld")
            )));
        }
        if (Boolean.getBoolean("KAF-MultiThreadingJEICommon")){
            transformers.add(new JEI_NotErrorAddSynchronized_Asm());
            transformers.add(new JEI_AddMapConcurrent_ASM());
        }
        return transformers;
    }
}
