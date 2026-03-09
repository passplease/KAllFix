package n1luik.KAllFix.mixin.unsafe.debug;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FireworkParticles.Starter.class)
public class StarterDebug1 {
    @Redirect(method = "createParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"))
    private Particle debug1(ParticleEngine instance, ParticleOptions p_107371_, double p_107372_, double p_107373_, double p_107374_, double p_107375_, double p_107376_, double p_107377_){

        Particle particle = instance.createParticle(p_107371_, p_107372_, p_107373_, p_107374_, p_107375_, p_107376_, p_107377_);
        if (particle == null) {
            Base.LOGGER.error("{} is not", BuiltInRegistries.PARTICLE_TYPE.getKey(p_107371_.getType()));
        }
        return particle;
    }
}