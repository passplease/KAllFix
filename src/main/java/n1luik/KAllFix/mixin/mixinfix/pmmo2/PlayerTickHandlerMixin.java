package n1luik.KAllFix.mixin.mixinfix.pmmo2;

import harmonised.pmmo.core.Core;
import harmonised.pmmo.events.impl.PlayerTickHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PlayerTickHandler.class, remap = false)
public class PlayerTickHandlerMixin {
    @Redirect(method = "handle", at = @At(value = "INVOKE",target = "Lharmonised/pmmo/features/veinmining/VeinMiningLogic;regenerateVein(Lnet/minecraft/server/level/ServerPlayer;)V", remap = false), remap = false)
    private static void redirectRegenerateVein(ServerPlayer player) {

    }
    @Redirect(method = "handle", at = @At(value = "INVOKE",target = "Lharmonised/pmmo/features/penalties/EffectManager;applyEffects(Lharmonised/pmmo/core/Core;Lnet/minecraft/world/entity/player/Player;)V", remap = false), remap = false)
    private static void redirectApplyEffects(Core core, Player player) {

    }
}