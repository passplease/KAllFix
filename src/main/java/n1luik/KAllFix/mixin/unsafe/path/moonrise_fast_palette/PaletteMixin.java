//摘自moonrise
package n1luik.KAllFix.mixin.unsafe.path.moonrise_fast_palette;

import n1luik.KAllFix.data.moonrise.patches.fast_palette.FastPalette;
import net.minecraft.world.level.chunk.Palette;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Palette.class)
public interface PaletteMixin<T> extends FastPalette<T> {

}