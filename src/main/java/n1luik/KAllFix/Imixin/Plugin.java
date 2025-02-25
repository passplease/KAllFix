package n1luik.KAllFix.Imixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class Plugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String s = "n1luik.KAllFix.mixin.unsafe.";
        if (mixinClassName.startsWith(s)){
            return Boolean.getBoolean("KAF-"+mixinClassName.substring(s.length()));
        }
        final boolean biolith = isModLoaded("biolith");
        boolean terrablender = isModLoaded("terrablender");
        //wc这玩意默认双开的
        //String s2 = "n1luik.KAllFix.mixin.mixinfix.biolith.test_else.";
        //String s3 = "n1luik.KAllFix.mixin.mixinfix.biolith.test_all.";
        String s4 = "n1luik.KAllFix.mixin.mixinfix.fancyenchantments.";
        //boolean fixBiolithBugMode2 = Boolean.getBoolean("FixBiolithBugMode2");
        //if (!biolith && mixinClassName.startsWith(s2) && fixBiolithBugMode2) {
        //    return false;
        //}
        //if (!biolith && mixinClassName.startsWith(s3) && !fixBiolithBugMode2) {
        //    return false;
        //}
        if (mixinClassName.startsWith(s4)) {
            return isModLoaded("fancyenchantments");
        }
        //KAF-NbtAZ
        return switch (mixinClassName) {
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MinecraftServerMixin" -> biolith;
            //case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin" -> biolith;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin" -> biolith;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.mod.TerramityModBiomesMixin" -> biolith;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin2" -> biolith;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.terrablender.InitializationHandlerMixin" ->
                    biolith && terrablender;
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        }
        return ModList.get().isLoaded(modId);
    }
}