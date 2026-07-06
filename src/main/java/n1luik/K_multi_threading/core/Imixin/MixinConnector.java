package n1luik.K_multi_threading.core.Imixin;

import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import asm.n1luik.K_multi_threading.asm.util.AsmApi2;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
    @Override
    public void connect() {
        if (System.getProperty("KMT_D") == null) {
            if (AsmApi.isServer || Boolean.getBoolean("KMT_Client")){
                Mixins.addConfigurations("mixins.K_multi_threading.json");
                if(AsmApi2.bootType == AsmApi2.BootType.NEO_FORGE) Mixins.addConfigurations("mixins.K_multi_threading_neo.json");
                String s = AsmApi.mcVersion;
                if (s.startsWith("1.19")){
                    Mixins.addConfigurations("mixins.K_multi_threading-1.19.2.json");
                }
            }
            Mixins.addConfigurations("mixins.K_multi_threadingAll.json");

        }
        if (System.getProperty("KAllFix_D") == null)
            Mixins.addConfigurations("mixins.KAllFix.json");
        if (Boolean.getBoolean("KAF-LoginProtectionMod"))
            Mixins.addConfigurations("mixins.KAllFix$KAF-LoginProtectionMod.json");
        if (Boolean.getBoolean("KAF-RenderOptimizer"))
            Mixins.addConfigurations("mixins.KAllFix$KAF-RenderOptimizer.json");
        if (Boolean.getBoolean("KMT-Debug"))
            Mixins.addConfigurations("mixins.K_multi_threading$debug.json");
    }
}

