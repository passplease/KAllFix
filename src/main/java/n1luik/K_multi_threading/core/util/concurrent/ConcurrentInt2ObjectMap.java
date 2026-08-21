package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

public class ConcurrentInt2ObjectMap<V> implements Int2ObjectMap<V> {
    protected final ConcurrentHashMap<Integer, V> backing = new ConcurrentHashMap<>();


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
    public void putAll(@NotNull Map<? extends Integer, ? extends V> m) {
        backing.putAll(m);
    }

    @Override
    public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException("MCMT - Not implemented");
    }

    @Override
    public V defaultReturnValue() {
        return null;
    }

    @Override
    public ObjectSet<Entry<V>> int2ObjectEntrySet() {
        return FastUtilHackUtil.entrySetIntWrap(backing);
    }

    @Override
    public ObjectSet<Map.Entry<Integer, V>> entrySet() {
        return new FastUtilHackUtil.WrappingObjectSortedSet<Map.Entry<Integer, V>>(backing.entrySet());
    }

    @Override
    public V put(Integer key, V value) {
        return backing.put(key, value);
    }

    @Override
    public V get(Object key) {
        return backing.get(key);
    }

    @Override
    public V remove(Object key) {
        return backing.remove(key);
    }

    @Override
    public IntSet keySet() {
        return new FastUtilHackUtil.WrappingIntSet(backing.keySet());
    }

    @Override
    public ObjectCollection<V> values() {
        return new FastUtilHackUtil.WrappingObjectCollection<>(backing.values());
    }

    @Override
    public V put(int key, V value) {
        return backing.put(key, value);
    }

    @Override
    public V get(int key) {
        return backing.get(key);
    }

    @Override
    public boolean containsKey(int key) {
        return backing.containsKey(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return backing.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return backing.containsValue(value);
    }

    @Override
    public void forEach(BiConsumer<? super Integer, ? super V> consumer) {
        backing.forEach(consumer);
    }

    @Override
    public void replaceAll(BiFunction<? super Integer, ? super V, ? extends V> function) {
        backing.replaceAll(function);
    }

    @Override
    public V getOrDefault(int key, V defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public V remove(int key) {
        return backing.remove(key);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public V putIfAbsent(int key, V value) {
        return backing.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(int key, Object value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(int key, V oldValue, V newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(int key, V value) {
        return backing.replace(key, value);
    }

    @Override
    public V computeIfAbsent(int key, IntFunction<? extends V> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction::apply);
    }

    @Override
    public V computeIfAbsent(int key, Int2ObjectFunction<? extends V> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfAbsentPartial(int key, Int2ObjectFunction<? extends V> mappingFunction) {
        return computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(int key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        return backing.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(int key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        return backing.compute(key, remappingFunction);
    }

    @Override
    public V merge(int key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }

    @Override
    public V putIfAbsent(Integer key, V value) {
        return backing.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(Integer key, V oldValue, V newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(Integer key, V value) {
        return backing.replace(key, value);
    }

    @Override
    public V computeIfAbsent(Integer key, Function<? super Integer, ? extends V> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(Integer key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        return backing.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(Integer key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        return backing.compute(key, remappingFunction);
    }

    @Override
    public V merge(Integer key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }
}
