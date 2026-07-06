package asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public class AsmApiNeoForge {
    public static boolean isModLoaded(String modId) {
        //if (ModList.get() == null) {
        return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        //}
        //return ModList.get().isLoaded(modId);
    }
    public static boolean isClient_() {
        return FMLLoader.getDist().isClient();
    }
    public static String mcVersion_() {
        return FMLLoader.versionInfo().mcVersion();
    }
}
