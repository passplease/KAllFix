package n1luik.K_multi_threading.core.mixin.fix.create;

import com.simibubi.create.content.kinetics.TorquePropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

@Mixin(value = TorquePropagator.class)
public class TorquePropagatorFix1 {
    @Unique
    private final VarHandle networkVH;

    {
        try {
            networkVH = MethodHandles.lookup().findVarHandle(KineticBlockEntity.class, "network", Long.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}