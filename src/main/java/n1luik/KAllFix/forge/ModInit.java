package n1luik.KAllFix.forge;

import com.abdelaziz.canary.common.Canary;
import cpw.mods.modlauncher.TransformingClassLoader;
import lombok.extern.slf4j.Slf4j;
import n1luik.KAllFix.DataCollectors;
import n1luik.KAllFix.api.OptimizeTagManager;
import n1luik.KAllFix.debug.KAFGetterChunkTagCommand;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.dataCollectors.ValkyrienSkies;
import n1luik.K_multi_threading.debug.GetterClassFileCommand;
import n1luik.K_multi_threading.fix.canary.CanaryConfigAuto;
import n1luik.K_multi_threading.fix.lithium.LithiumConfigAuto;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


@Slf4j
@Mod(Base.MOD_ID2)
public class ModInit {
    public static final OptimizeTagManager OPTIMIZE_TAG_MANAGER = new OptimizeTagManager();

    //懒得折腾了，拉一点吧
    public static synchronized void registerKeyBinding(KeyMapping key)
    {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, key);
    }

    public ModInit(){
        try {
            if (Boolean.getBoolean("KAF-LoginProtectionMod")) {// TODO neoforge
                MinecraftForge.EVENT_BUS.register(Class.forName("n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent", true, ModInit.class.getClassLoader()));
            }
            if (Boolean.getBoolean("KAF-packetOptimize")) {
                Class.forName("n1luik.KAllFix.forge.PacketOptimizeAll", true, ModInit.class.getClassLoader());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        MinecraftForge.EVENT_BUS.register(new EventRun());

    }

    public static class EventRun{
        @SubscribeEvent
        public static void onRegisterCommandsEvent(final RegisterCommandsEvent registerCommandsEvent) {
            KAFGetterChunkTagCommand.register(registerCommandsEvent.getDispatcher());
        }
        //@SubscribeEvent
        //public void onInitDataCollectorsEvent(InitDataCollectorsEvent event){
        //    log.debug("K_multi_threading onInitDataCollectorsEvent");
        //    if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
        //        event.dataCollectors.addTools(new ValkyrienSkies());
        //    }
        //}
    }
}
