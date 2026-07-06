package n1luik.KAllFix.neoforge;

import n1luik.KAllFix.debug.KAFGetterChunkTagCommand;
import n1luik.KAllFix.forge.ModInit;
import n1luik.K_multi_threading.core.Base;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Base.MOD_ID)
public class NeoKAF {
    public NeoKAF(){
        NeoForge.EVENT_BUS.register(EventRun.class);

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
