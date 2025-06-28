//摘自moonrise
package n1luik.KAllFix.mixin.unsafe.path.moonrise_fast_palette;

import n1luik.KAllFix.data.moonrise.patches.fast_palette.FastPalette;
import n1luik.KAllFix.data.moonrise.patches.fast_palette.FastPaletteData;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LinearPalette.class)
public abstract class LinearPaletteMixin<T> implements Palette<T>, FastPalette<T> {

    @Shadow
    @Final
    private T[] values;

    @Override
    public final T[] KAllFix$moonrise$getRawPalette(final FastPaletteData<T> container) {
        return this.values;
    }
}