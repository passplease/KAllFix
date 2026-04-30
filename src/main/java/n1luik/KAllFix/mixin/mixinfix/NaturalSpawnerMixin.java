package n1luik.KAllFix.mixin.mixinfix;

import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Mixin(value = NaturalSpawner.class)
public class NaturalSpawnerMixin {
    private final AtomicIntegerArray ints = new AtomicIntegerArray(1);
}
