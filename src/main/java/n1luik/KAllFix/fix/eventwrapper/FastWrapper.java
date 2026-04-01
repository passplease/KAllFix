package n1luik.KAllFix.fix.eventwrapper;

import io.github.lounode.eventwrapper.eventbus.api.EventConverter;
import io.github.lounode.eventwrapper.eventbus.api.EventWrapper;
import io.github.lounode.eventwrapper.eventbus.api.IPlatformEventHelper;
import n1luik.KAllFix.mixin.mixinfix.eventwrapper.ForgeEventHelperAccessor;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class FastWrapper {
    private final static Map<Event, EventWrapper> FORGE_EVENT_TRACKER_MAP = ForgeEventHelperAccessor.FORGE_EVENT_TRACKER_MAP();

    public static <T extends Event, T2> void wrapper(BiConsumer<T2, EventWrapper> call, EventConverter<T, ?> converter, T2 o, T event) {

        {
            try {
                EventWrapper wrapper = converter.toWrapper(event);
                if (FORGE_EVENT_TRACKER_MAP.containsKey(event)) {
                    wrapper = (EventWrapper)FORGE_EVENT_TRACKER_MAP.get(event);
                    IPlatformEventHelper.syncEventData(event, wrapper);
                }

                call.accept(o, wrapper);
                IPlatformEventHelper.syncEventData(wrapper, event);
            } catch (Exception e) {
                throw new RuntimeException("Event call Error!", e);
            }
        }
    }
}
