package n1luik.K_multi_threading.neoforge.util;

import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.base.CalculateTask;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.ListenerList;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventListener;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;

public class NeoEventUtil {
    private static final Method getListeners;
    static {
        try {
            getListeners = EventBus.class.getDeclaredMethod("getListenerList", Class.class);
            getListeners.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    private static ListenerList getListeners(EventBus eventBus, Class<? extends Event> eventType){
        try {
            return (ListenerList) getListeners.invoke(eventBus, eventType);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    public static void runEvent(ForkJoinPool pool, IEventBus eventBus, Event event){
        runEvent(pool, eventBus, event, true);
    }

    public static void runEvent(ForkJoinPool pool, IEventBus eventBus, Event event, boolean wait){
        runEvent(pool, eventBus, event, wait, null);
    }
    public static void runEvent(ForkJoinPool pool, IEventBus eventBus, Event event, boolean wait, EventPriority phase){
        EventListener[] listeners;
        if (phase != null) {
            listeners = getListeners((EventBus) eventBus, event.getClass()).getPhaseListeners(phase);
        }else {
            listeners = getListeners((EventBus) eventBus, event.getClass()).getListeners();
        }

        //Base.WaitInt waitInt = new Base.WaitInt();
        //Base.ForkJoinPool_ ex = Base.getEx();

        //Base.getEx().getDataMap().put(Base.thisRunTaskName,name);
        //waitInt.size = listeners.length;

        int length = listeners.length;
        CalculateTask submit = (new CalculateTask(()->"Event["+event.getClass().getName()+"]", 0, length, (i) -> {
            if (i < length && !Objects.equals(listeners[i].getClass(), EventPriority.class)) listeners[i].invoke(event);
            //synchronized (waitInt) {
            //    waitInt.size--;
            //}
        }));

        if (wait) {
            submit.call(pool);
        }else {
            pool.submit(submit);//网上没有查到资料
        }


        //if (wait) {
        //    Base.getEx().getDataMap().put(Base.thisRunTaskName, null);
        //}
    }
}