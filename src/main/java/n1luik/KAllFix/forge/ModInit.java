package n1luik.KAllFix.forge;

import n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent;
import n1luik.K_multi_threading.core.Base;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Base.MOD_ID2)
public class ModInit {
    public ModInit(){
        if (Boolean.getBoolean("KAF-LoginProtectionMod")) {
            try {
                MinecraftForge.EVENT_BUS.register(Class.forName("n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent", true, ModInit.class.getClassLoader()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
