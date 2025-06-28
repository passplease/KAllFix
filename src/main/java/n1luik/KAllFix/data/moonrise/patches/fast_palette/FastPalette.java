//摘自moonrise

package n1luik.KAllFix.data.moonrise.patches.fast_palette;

public interface FastPalette<T> {

    public default T[] KAllFix$moonrise$getRawPalette(final FastPaletteData<T> src) {
        return null;
    }

}