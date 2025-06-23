package n1luik.KAllFix.forge.RenderOptimizer.mixin;

import n1luik.KAllFix.forge.RenderOptimizer.Config;
import n1luik.KAllFix.forge.RenderOptimizer.Imixin.IEntityOptimizerData;
import n1luik.KAllFix.util.UtilKAF;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    public void tickEntities(EntityTickList instance, Consumer<Entity> entityConsumer) {

        Vec3 projectedView = minecraft.gameRenderer.getMainCamera().getPosition();
        if (Config.Enable){
            if (instance.iterated != null) {
                throw new UnsupportedOperationException("Only one concurrent iteration supported");
            } else {
                instance.iterated = instance.active;

                try {
                    for(Entity entity : instance.active.values()) {
                        if (entity.isControlledByLocalInstance()){
                            ((IEntityOptimizerData) entity).KAllFix$setIsOptimizer(false);
                            entityConsumer.accept(entity);
                            continue;
                        }
                        double abs = Math.abs(UtilKAF.calculateDistance(projectedView.x, projectedView.y, projectedView.z, entity.getX(), entity.getY(), entity.getZ()));
                        if (abs < Config.EntityStopTickDistance) {
                            if (abs < Config.EntityStartStopTickDistance) {
                                entityConsumer.accept(entity);
                                ((IEntityOptimizerData) entity).KAllFix$setIsOptimizer(false);
                            }else{
                                ((IEntityOptimizerData) entity).KAllFix$setIsOptimizer(true);
                                if (Config.FixLivingEntity){
                                    if (entity instanceof LivingEntity livingEntity){
                                        if (livingEntity.lerpSteps > 0) {
                                            livingEntity.setPos(livingEntity.lerpX, livingEntity.lerpY, livingEntity.lerpZ);
                                            livingEntity.setRot((float) livingEntity.lerpXRot, (float) livingEntity.lerpYRot);
                                        }
                                    }

                                }
                                double v = (abs - Config.EntityStartStopTickDistance) / (Config.EntityStopTickDistance - Config.EntityStartStopTickDistance);
                                //错开tick减少卡顿
                                double i = Math.abs(entity.getX() * entity.getY()) % Config.EntityTickScaling;
                                int i1 = ((IEntityOptimizerData) entity).KAllFix$getTick();


                                double v1 = (i + i1) % ((v * Config.EntityTickScaling)+1);
                                if (((int)v1) == 0) {
                                    entityConsumer.accept(entity);
                                }
                            }
                        }
                    }
                } finally {
                    instance.iterated = null;
                }

            }
        }else {
            instance.forEach(entityConsumer);
        }
    }
}
