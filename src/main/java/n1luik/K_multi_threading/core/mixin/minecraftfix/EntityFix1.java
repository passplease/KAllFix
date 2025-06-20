package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({Entity.class})
public class EntityFix1 {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public Entity getRootVehicle() {
        Entity entity = (Entity)(Object)this;
        Entity entity1 = null;
        while(entity.isPassenger() && (entity1 = entity.getVehicle()) != null) {
            entity = entity1;
        }

        return entity;
    }
}
