package n1luik.KAllFix.data.pmmo2;

import harmonised.pmmo.core.Core;
import harmonised.pmmo.events.impl.PlayerTickHandler;
import harmonised.pmmo.features.penalties.EffectManager;
import harmonised.pmmo.features.veinmining.VeinMiningLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;

public class EventHandler {
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        if (event.getServer().getTickCount() % 10 == 0) {
            event.getServer().getPlayerList().getPlayers().forEach(player -> {
                VeinMiningLogic.regenerateVein((ServerPlayer)player);
            });
            event.getServer().getPlayerList().getPlayers().forEach(player -> {
                Core core = Core.get(event.side);
                EffectManager.applyEffects(core, player);
            });
        }
    }
}
