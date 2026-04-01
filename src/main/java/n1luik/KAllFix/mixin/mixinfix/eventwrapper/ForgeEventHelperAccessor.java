package n1luik.KAllFix.mixin.mixinfix.eventwrapper;

import io.github.lounode.eventwrapper.eventbus.api.EventWrapper;
import io.github.lounode.eventwrapper.forge.ForgeEventHelper;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ForgeEventHelper.class, remap = false)
public interface ForgeEventHelperAccessor {
    @Accessor("FORGE_EVENT_TRACKER_MAP")
    static Map<Event, EventWrapper> FORGE_EVENT_TRACKER_MAP() {
        throw new UnsupportedOperationException();
    }
}