package asm.n1luik.KAllFix.asm;

import asm.n1luik.KAllFix.asm.mod.RemoveMixin_ASM;
import asm.n1luik.KAllFix.asm.mod.createdieselgenerators.EntityMixinAsm;
import asm.n1luik.KAllFix.asm.mod.gcyr.CanaryConfig;
import asm.n1luik.KAllFix.asm.mod.gcyr.GcyrCanaryMapping_Asm;
import asm.n1luik.KAllFix.asm.mod.petrolpark.ITeamBoundItemAsm;
import asm.n1luik.KAllFix.asm.mod.petrolpark.ShopMenuItemAsm;
import asm.n1luik.KAllFix.asm.mod.tfmg.DestroyFix_Asm;
import asm.n1luik.KAllFix.asm.util.ReadClassAsm;
import asm.n1luik.KAllFix.asm.mod.jei.JEI_AddMapConcurrent_ASM;
import asm.n1luik.KAllFix.asm.mod.jei.JEI_NotErrorAddSynchronized_Asm;
import asm.n1luik.K_multi_threading.asm.JavaAgent.AgentAPI;
import asm.n1luik.K_multi_threading.asm.JavaAgent.AsmUtil;
import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class KAllFixAsm extends AgentAPI {
    //public static BiFunction<TransformingClassLoader, String, byte[]> getclass;
    //static {
    //    try {
    //        Method buildTransformedClassNodeFor = TransformingClassLoader.class.getDeclaredMethod("buildTransformedClassNodeFor", String.class, String.class);
    //        buildTransformedClassNodeFor.setAccessible(true);
    //        getclass = (transformingClassLoader, s) ->
    //        {
    //            try {
    //                return (byte[]) buildTransformedClassNodeFor.invoke(transformingClassLoader, s, s);
    //            } catch (IllegalAccessException | InvocationTargetException e) {
    //                throw new RuntimeException(e);
    //            }
    //        };
    //    } catch (NoSuchMethodException e) {
    //        throw new RuntimeException(e);
    //    }
    //}

    public KAllFixAsm() {
        super("KAllFixAsm");
    }

    @Override
    public @NotNull List<ITransformer2> transformers() {
        List<ITransformer2> transformers = new ArrayList<>(List.of(
                new EntityMixinAsm()
        ));
        if (Boolean.getBoolean("KAF-Fix_fabric-object-builder-api.jar")){
            transformers.add(new ReadClassAsm("fabric-object-builder-api", "v1-11.1.3", "TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin", Set.of(
                    "net.fabricmc.fabric.mixin.object.builder.TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin"
            )));
        }
        if (Boolean.getBoolean("KAF-UnsafeCinderscapesFix1")){
            transformers.add(new ReadClassAsm("cinderscapes", "all", "MixinServerWorld", Set.of(
                    "com.terraformersmc.cinderscapes.mixin.MixinServerWorld"
            )));
        }
        if (Boolean.getBoolean("KAF-MultiThreadingJEICommon")){
            transformers.add(new JEI_NotErrorAddSynchronized_Asm());
            transformers.add(new JEI_AddMapConcurrent_ASM());
        }
        if (Boolean.getBoolean("KAF-PlainTextSearchTreeMultiThreading")) {
            transformers.add(new ReadClassAsm("quark", "1.20.1+all", "PotionUtilsMixin", Set.of(
                    "org.violetmoon.quark.mixin.mixins.PotionUtilsMixin"
            )));
        }
        try {
            ClassLoader.getPlatformClassLoader().loadClass("net.neoforged.fml.javafmlmod.FMLModContainer");//确定是neoforge
            transformers.add(AsmUtil.newForge2MCPMap());

        }catch (Exception e){
        }
        transformers.add(new RemoveMixin_ASM());
        if (Boolean.getBoolean("KAF-FixTFMGDestroy")){
            transformers.add(new DestroyFix_Asm());
        }

        if (!AsmApi.isClient && !Boolean.getBoolean("KAF-DisablePetrolpark")) {//用不了，直接不加载了
            //transformers.add(new PetrolparkAsm());
            transformers.add(new ShopMenuItemAsm());
            transformers.add(new ITeamBoundItemAsm());
        }
        if (CanaryConfig.ENABLED) {
            transformers.add(new GcyrCanaryMapping_Asm());
        }
        return transformers;
    }
}
