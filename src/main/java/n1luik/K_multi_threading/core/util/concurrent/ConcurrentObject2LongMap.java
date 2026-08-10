package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.longs.LongBinaryOperator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;

public class ConcurrentObject2LongMap<K> implements Object2LongMap<K> {

    protected final ConcurrentHashMap<K,Long> backing;
    public ConcurrentObject2LongMap() {
        backing = new ConcurrentHashMap<>();
    }

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public void clear() {
        backing.clear();
    }

    @Override
    public boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public long getLong(Object key) {
        return backing.getOrDefault(key, 0L);
    }

    @Override
    public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long defaultReturnValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectSet<Entry<K>> object2LongEntrySet() {
        return FastUtilHackUtil.entrySetLongWrap2(backing);
    }

    @Override
    public ObjectSet<Map.Entry<K, Long>> entrySet() {
        return new FastUtilHackUtil.ToObjectSet(backing.entrySet());
    }

    @Override
    public long put(K key, long value) {
        Long put = backing.put(key, value);
        return put == null ? 0 : put;
    }
    @Override
    public Long put(K key, Long value) {
        return backing.put(key, value);
    }

    @Override
    public Long get(Object key) {
        return backing.get(key);
    }

    @Override
    public Long remove(Object key) {
        return backing.remove(key);
    }

    @Override
    public ObjectSet<K> keySet() {
        return new FastUtilHackUtil.WrappingObjectSortedSet<>(backing.keySet());
    }

    @Override
    public LongCollection values() {
        return new FastUtilHackUtil.WrappingLongCollection(backing.values());
    }

    @Override
    public boolean containsKey(Object key) {
        return backing.containsKey(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends Long> m) {
        backing.putAll(m);
    }

    @Override
    public boolean containsValue(long value) {
        return backing.containsValue(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return backing.containsValue(value);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super Long> consumer) {
        backing.forEach(consumer);
    }

    @Override
    public long getOrDefault(Object key, long defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public Long getOrDefault(Object key, Long defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public long putIfAbsent(K key, long value) {
        Long l = backing.putIfAbsent(key, value);
        return l == null ? 0 : l;
    }

    @Override
    public boolean remove(Object key, long value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(K key, long oldValue, long newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public long replace(K key, long value) {
        Long replace = backing.replace(key, value);
        return replace == null ? 0 : replace;
    }

    @Override
    public long computeIfAbsent(K key, ToLongFunction<? super K> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction::applyAsLong);
    }

    @Override
    public long computeLongIfAbsent(K key, ToLongFunction<? super K> mappingFunction) {
        Long l = backing.computeIfAbsent(key, mappingFunction::applyAsLong);
        return l == null ? 0 : l;
    }

    @Override
    public long computeIfAbsent(K key, Object2LongFunction<? super K> mappingFunction) {
        Long l = backing.computeIfAbsent(key, mappingFunction);
        return l == null ? 0 : l;
    }

    @Override
    public long computeLongIfAbsentPartial(K key, Object2LongFunction<? super K> mappingFunction) {
        Long l = backing.computeIfAbsent(key, mappingFunction);
        return l == null ? 0 : l;
    }

    @Override
    public long computeLongIfPresent(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
        Long l = backing.computeIfPresent(key, remappingFunction);
        return l == null ? 0 : l;
    }

    @Override
    public long computeLong(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
        return backing.compute(key, remappingFunction);
    }

    @Override
    public long merge(K key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }

    @Override
    public long mergeLong(K key, long value, java.util.function.LongBinaryOperator remappingFunction) {
        return backing.merge(key, value, remappingFunction::applyAsLong);
    }

    @Override
    public long mergeLong(K key, long value, LongBinaryOperator remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }

    @Override
    public long mergeLong(K key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }

    @Override
    public Long putIfAbsent(K key, Long value) {
        return backing.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(K key, Long oldValue, Long newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public Long replace(K key, Long value) {
        return backing.replace(key, value);
    }

    @Override
    public Long merge(K key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }


}
