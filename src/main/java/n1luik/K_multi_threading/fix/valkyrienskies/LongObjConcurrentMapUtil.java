package n1luik.K_multi_threading.fix.valkyrienskies;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import n1luik.K_multi_threading.core.util.concurrent.Long2ObjectConcurrentHashMap;

import java.util.Map;

public class LongObjConcurrentMapUtil {
    public static <V> Long2ObjectMap<V> create(kotlin.Pair<Long, ? extends V>... var0) {
        Intrinsics.checkNotNullParameter(var0, "");
        Long2ObjectConcurrentHashMap<V> var1 = new Long2ObjectConcurrentHashMap<>();
        MapsKt.putAll(var1, var0);
        return var1;
    }

    public static <V> Long2ObjectMap<V> create() {
        return new Long2ObjectConcurrentHashMap<>();
    }
}
