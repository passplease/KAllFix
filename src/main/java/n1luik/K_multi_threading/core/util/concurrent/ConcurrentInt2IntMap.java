package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntBinaryOperator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

public class ConcurrentInt2IntMap implements Int2IntMap {
    protected final ConcurrentHashMap<Integer, Integer> backing = new ConcurrentHashMap<>();


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
    public void putAll(@NotNull Map<? extends Integer, ? extends Integer> m) {
        backing.putAll(m);
    }

    @Override
    public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException("MCMT - Not implemented");
    }

    @Override
    public int defaultReturnValue() {
        return 0;
    }

    @Override
    public ObjectSet<Entry> int2IntEntrySet() {
        return FastUtilHackUtil.entrySetIntIntWrap(backing);
    }

    @Override
    public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
        return new FastUtilHackUtil.WrappingObjectSortedSet(backing.entrySet());
    }

    @Override
    public Integer put(Integer key, Integer value) {
        return backing.put(key, value);
    }

    @Override
    public Integer get(Object key) {
        return backing.get(key);
    }

    @Override
    public Integer remove(Object key) {
        return backing.remove(key);
    }

    @Override
    public IntSet keySet() {
        return new FastUtilHackUtil.WrappingIntSet(backing.keySet());
    }

    @Override
    public IntCollection values() {
        return new FastUtilHackUtil.WrappingIntCollection(backing.values());
    }

    @Override
    public int put(int key, int value) {
        Integer put = backing.put(key, value);
        return put == null ? 0 : put;
    }

    @Override
    public int get(int key) {
        Integer get = backing.get(key);
        return get == null ? 0 : get;
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
    public boolean containsValue(int value) {
        return backing.containsValue(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return backing.containsValue(value);
    }

    @Override
    public void forEach(BiConsumer<? super Integer, ? super Integer> consumer) {
        backing.forEach(consumer);
    }

    @Override
    public void replaceAll(BiFunction<? super Integer, ? super Integer, ? extends Integer> function) {
        backing.replaceAll(function);
    }

    @Override
    public int getOrDefault(int key, int defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public int remove(int key) {
        Integer remove = backing.remove(key);
        return remove == null ? 0 : remove;
    }

    @Override
    public Integer getOrDefault(Object key, Integer defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public int putIfAbsent(int key, int value) {
        Integer i = backing.putIfAbsent(key, value);
        return i == null ? 0 : i;
    }

    @Override
    public boolean remove(int key, int value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(int key, int oldValue, int newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public int replace(int key, int value) {
        return backing.replace(key, value);
    }

    @Override
    public int computeIfAbsent(int key, IntUnaryOperator mappingFunction) {
        Integer i = backing.computeIfAbsent(key, mappingFunction::applyAsInt);
        return i == null ? 0 : i;
    }

    @Override
    public int computeIfAbsentNullable(int key, IntFunction<? extends Integer> mappingFunction) {
        Integer i = backing.computeIfAbsent(key, mappingFunction::apply);
        return i == null ? 0 : i;
    }

    @Override
    public int computeIfAbsent(int key, Int2IntFunction mappingFunction) {
        Integer i = backing.computeIfAbsent(key, mappingFunction);
        return i == null ? 0 : i;
    }

    @Override
    public int computeIfAbsentPartial(int key, Int2IntFunction mappingFunction) {
        return computeIfAbsent(key, mappingFunction);
    }

    @Override
    public int computeIfPresent(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        Integer i = backing.computeIfPresent(key, remappingFunction);
        return i == null ? 0 : i;
    }

    @Override
    public int compute(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        Integer i = backing.compute(key, remappingFunction);
        return i == null ? 0 : i;
    }

    @Override
    public int merge(int key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        Integer i = backing.merge(key, value, remappingFunction);
        return i == null ? 0 : i;
    }

    @Override
    public int mergeInt(int key, int value, java.util.function.IntBinaryOperator remappingFunction) {
        return Int2IntMap.super.mergeInt(key, value, remappingFunction);
    }

    @Override
    public int mergeInt(int key, int value, IntBinaryOperator remappingFunction) {
        Integer i = backing.merge(key, value, remappingFunction);
        return i == null ? 0 : i;
    }

    @Override
    public Integer putIfAbsent(Integer key, Integer value) {
        return backing.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(Integer key, Integer oldValue, Integer newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public Integer replace(Integer key, Integer value) {
        return backing.replace(key, value);
    }

    @Override
    public Integer computeIfAbsent(Integer key, Function<? super Integer, ? extends Integer> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Integer computeIfPresent(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return backing.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Integer compute(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return backing.compute(key, remappingFunction);
    }

    @Override
    public Integer merge(Integer key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }
}
