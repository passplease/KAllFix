package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.world.level.levelgen.BitRandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.atomic.AtomicLong;

@Mixin(LegacyRandomSource.class)
public abstract class LegacyRandomSourceFix2 implements BitRandomSource {
    @Shadow
    @Final
    private AtomicLong seed;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int next(int p_188581_) {
        long i;
        long j;
        do {
            i = this.seed.get();
            j = i * 25214903917L + 11L & 281474976710655L;
        } while(!this.seed.compareAndSet(i, j));

        return (int)(j >>> 48 - p_188581_);
    }
}
