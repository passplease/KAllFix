package n1luik.KAllFix.mixin.mixinfix.eventwrapper;

import io.github.lounode.eventwrapper.eventbus.api.EventConverter;
import io.github.lounode.eventwrapper.eventbus.api.EventWrapper;
import io.github.lounode.eventwrapper.eventbus.api.IPlatformEventHelper;
import io.github.lounode.eventwrapper.eventbus.api.SubscribeEventWrapper;
import io.github.lounode.eventwrapper.forge.ForgeEventHelper;
import io.github.lounode.eventwrapper.forge.event.ForgeEventMappings;
import n1luik.KAllFix.fix.eventwrapper.FastWrapper;
import n1luik.K_multi_threading.core.util.Unsafe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Deprecated
@Mixin(value = ForgeEventHelper.class, remap = false)
public class ForgeEventHelperMixin {

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private static Map<Event, EventWrapper> FORGE_EVENT_TRACKER_MAP;
    @Unique
    private static final Method accept;
    @Unique
    private static final Method accept2;
    @Unique
    private static final MethodHandle wrapper;

    static  {
        try {
            accept = BiConsumer.class.getMethod("accept", Object.class, Object.class);
            accept2 = Consumer.class.getMethod("accept", Object.class);
            wrapper = MethodHandles.publicLookup().findStatic(FastWrapper.class, "wrapper", MethodType.methodType(void.class, BiConsumer.class, EventConverter.class, Object.class, Event.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void registerListener(Object target, Method method) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            throw new IllegalArgumentException("Event subscriber can only subscribe one event!");
        } else if (!EventWrapper.class.isAssignableFrom(params[0])) {
            throw new IllegalArgumentException("EventType must extend EventWrapper");
        } else {
            var wrapperClass = params[0];
            EventConverter converter = ForgeEventMappings.getConverter(wrapperClass);
            if (converter == null) {
                LOGGER.error("Can't find the converter: {}", wrapperClass);
            } else {
                Class<? extends Event> forgeEventClass = ForgeEventMappings.getForgeEventClass((Class<? extends EventWrapper>) wrapperClass);
                if (forgeEventClass == null) {
                    LOGGER.error("Wrapper was not found forge event class: {}", wrapperClass);
                } else {
                    SubscribeEventWrapper annotation = (SubscribeEventWrapper)method.getAnnotation(SubscribeEventWrapper.class);
                    EventPriority priority = annotation != null ? EventPriority.valueOf(annotation.priority().name()) : EventPriority.NORMAL;
                    boolean receiveCanceled = annotation != null && annotation.receiveCanceled();

                    try {
                        Unsafe.metafactory(FastWrapper.class, accept2, MethodHandles.insertArguments(
                                wrapper,
                                0, Unsafe.metafactory(method.getClass(), accept, Unsafe.lookup.unreflect(method)).dynamicInvoker().invoke(),
                                1, converter,
                                2, target
                        )).dynamicInvoker().invoke();


                        Consumer invoke = (Consumer) Unsafe.metafactory(FastWrapper.class, accept2, MethodHandles.insertArguments(
                                wrapper,
                                0, Unsafe.metafactory(method.getClass(), accept, Unsafe.lookup.unreflect(method)).dynamicInvoker().invoke(),
                                1, converter,
                                2, target
                        )).dynamicInvoker().invoke();
                        MinecraftForge.EVENT_BUS.addListener(priority, receiveCanceled, forgeEventClass,
                                invoke
                            );
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (LambdaConversionException e) {
                        throw new RuntimeException(e);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}