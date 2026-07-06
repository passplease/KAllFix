package n1luik.KAllFix;

import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import n1luik.KAllFix.forge.InitDataCollectorsEvent;
import n1luik.K_multi_threading.core.dataCollectors.ValkyrienSkies;
import n1luik.K_multi_threading.fix.canary.CanaryConfigAuto;
import n1luik.K_multi_threading.fix.lithium.LithiumConfigAuto;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.IOException;

public class DataCollectorsRun {
    private static boolean INIT_DATA_COLLECTORS_EVENT = false;

    public synchronized static void initDataCollectors(){
        if (INIT_DATA_COLLECTORS_EVENT) return;
        INIT_DATA_COLLECTORS_EVENT = true;
        //InitDataCollectorsEvent event = new InitDataCollectorsEvent();
        //MinecraftForge.EVENT_BUS.post(event);
        DataCollectors dataCollectors = new DataCollectors();
        if ((AsmApi.isServer && System.getProperty("KMT_D") == null) || Boolean.getBoolean("KMT_Client")) {
            dataCollectors.addTools(new ValkyrienSkies());
            dataCollectors.addTools(new CanaryConfigAuto());
            dataCollectors.addTools(new LithiumConfigAuto());
        }

        try {
            dataCollectors.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
