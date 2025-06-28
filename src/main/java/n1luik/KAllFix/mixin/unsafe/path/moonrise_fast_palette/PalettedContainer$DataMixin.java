//摘自moonrise
package n1luik.KAllFix.mixin.unsafe.path.moonrise_fast_palette;

import n1luik.KAllFix.data.moonrise.patches.fast_palette.FastPaletteData;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PalettedContainer.Data.class)
public abstract class PalettedContainer$DataMixin<T> implements FastPaletteData<T> {

    @Unique
    private T[] palette;

    public final T[] KAllFix$moonrise$getPalette() {
        return this.palette;
    }

    @Override
    public final void KAllFix$moonrise$setPalette(final T[] palette) {
        this.palette = palette;
    }
}