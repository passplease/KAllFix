package n1luik.KAllFix.mixin.mixinfix.atmospheric;

import com.teamabnormals.atmospheric.core.other.AtmosphericEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Calendar;

@Mixin(value = AtmosphericEvents.class, remap = false)
public class AtmosphericEventsMixin {
    @Unique
    private static final int KAllFix$max = 10000;
    @Unique
    private static int KAllFix$size = 0;
    @Unique
    private static boolean KAllFix$isAprilFools = false;
    @Unique
    private static boolean KAllFix$_isAprilFools() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(2) + 1 == 4 && calendar.get(5) == 1;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static boolean isAprilFools() {
        if (KAllFix$size-- < 1){
            KAllFix$isAprilFools = KAllFix$_isAprilFools();
            KAllFix$size = KAllFix$max;
        }
        return KAllFix$isAprilFools;
    }
}
