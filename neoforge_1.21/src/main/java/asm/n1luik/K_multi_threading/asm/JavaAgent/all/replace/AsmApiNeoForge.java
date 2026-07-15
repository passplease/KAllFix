package asm.n1luik.K_multi_threading.asm.JavaAgent.all.replace;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforgespi.language.IModInfo;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    public static Optional<InputStream> getJarFile(String modId, String file) {
        List<ModFileInfo> list = LoadingModList.get().getModFiles().stream().filter(new ModFileInfoPredicate(modId)).toList();
        if (list.isEmpty()) {
            throw new RuntimeException("Mod not found: " + modId);
        }
        ModFileInfo modInfo = list.get(0);
        return modInfo.getFile().getSecureJar().moduleDataProvider().open(file);
    }

    public static class ModFileInfoPredicate implements Predicate<ModFileInfo> {
        private final String modId;

        public ModFileInfoPredicate(String modId) {
            this.modId = modId;
        }

        @Override
        public boolean test(ModFileInfo anObject) {
            for (IModInfo modInfo2 : anObject.getMods()) {
                if (modId.equals(modInfo2.getModId())) {
                    return true;
                }
            }
            return false;
        }
    }
}
