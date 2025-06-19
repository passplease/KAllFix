package n1luik.KAllFix.forge;

import lombok.extern.slf4j.Slf4j;
import n1luik.KAllFix.DataCollectors;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.dataCollectors.ValkyrienSkies;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

@Slf4j
@Mod(Base.MOD_ID2)
public class ModInit {

    //懒得折腾了，拉一点吧
    public static synchronized void registerKeyBinding(KeyMapping key)
    {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, key);
    }

    private static boolean INIT_DATA_COLLECTORS_EVENT = false;
    public ModInit(){
        try {
            if (Boolean.getBoolean("KAF-LoginProtectionMod")) {
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

    public synchronized static void initDataCollectors(){
        if (INIT_DATA_COLLECTORS_EVENT) return;
        INIT_DATA_COLLECTORS_EVENT = true;
        InitDataCollectorsEvent event = new InitDataCollectorsEvent();
        MinecraftForge.EVENT_BUS.post(event);
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            event.dataCollectors.addTools(new ValkyrienSkies());
        }

        try {
            event.dataCollectors.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class EventRun{
        //@SubscribeEvent
        //public void onInitDataCollectorsEvent(InitDataCollectorsEvent event){
        //    log.debug("K_multi_threading onInitDataCollectorsEvent");
        //    if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
        //        event.dataCollectors.addTools(new ValkyrienSkies());
        //    }
        //}
    }
}
