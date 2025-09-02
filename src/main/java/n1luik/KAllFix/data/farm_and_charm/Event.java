package n1luik.KAllFix.data.farm_and_charm;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;

import java.util.concurrent.atomic.AtomicInteger;

public class Event {
    public static final AtomicInteger removeO1 = new AtomicInteger();
    public static void sendData( Iterable<Entity> entities){
        removeO1.incrementAndGet();
        for (Entity entity : entities) {
            EntityType<?> type = entity.getType();
            if (!(type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN)) return;
            if (entity instanceof Animal animal) {

                SaturationTracker tracker = ((SaturationTracker.SaturatedAnimal)animal).farm_and_charm$getSaturationTracker();

                SyncSaturationPacket packet = new SyncSaturationPacket(entity.getId(), tracker.level(), tracker.foodCounter());
                PacketHandler.sendSaturationSync(packet, entity);
            }
        }
        removeO1.decrementAndGet();
    }
    @SubscribeEvent
    public static void onPlayerLonin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            sendData(serverPlayer.serverLevel().getAllEntities());
        }
    }
    @SubscribeEvent
    public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            sendData(serverPlayer.serverLevel().getAllEntities());
        }
    }
}
