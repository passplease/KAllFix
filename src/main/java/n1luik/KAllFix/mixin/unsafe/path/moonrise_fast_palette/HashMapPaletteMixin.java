//摘自moonrise
package n1luik.KAllFix.mixin.unsafe.path.moonrise_fast_palette;

import n1luik.KAllFix.data.moonrise.patches.fast_palette.FastPalette;
import n1luik.KAllFix.data.moonrise.patches.fast_palette.FastPaletteData;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.chunk.Palette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HashMapPalette.class)
public abstract class HashMapPaletteMixin<T> implements Palette<T>, FastPalette<T> {

    @Shadow
    @Final
    private CrudeIncrementalIntIdentityHashBiMap<T> values;

    @Override
    public final T[] KAllFix$moonrise$getRawPalette(final FastPaletteData<T> container) {
        return ((FastPalette<T>)this.values).KAllFix$moonrise$getRawPalette(container);
    }
}