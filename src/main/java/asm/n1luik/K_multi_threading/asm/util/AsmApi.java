package asm.n1luik.K_multi_threading.asm.util;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

/**
 * 他会动态变化，根据不同的环境而变化
 */
public class AsmApi {
    public static final boolean isClient = isClient_();
    public static final boolean isServer = !isClient;
    public static final String mcVersion = mcVersion_();

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
