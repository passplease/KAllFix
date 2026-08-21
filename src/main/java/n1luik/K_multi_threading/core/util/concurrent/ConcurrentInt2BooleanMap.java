package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.booleans.BooleanBinaryOperator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

public class ConcurrentInt2BooleanMap implements Int2BooleanMap {
    protected final ConcurrentHashMap<Integer, Boolean> backing = new ConcurrentHashMap<>();


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
    public void putAll(@NotNull Map<? extends Integer, ? extends Boolean> m) {
        backing.putAll(m);
    }

    @Override
    public void defaultReturnValue(boolean rv) {
        throw new UnsupportedOperationException("MCMT - Not implemented");
    }

    @Override
    public boolean defaultReturnValue() {
        return false;
    }

    @Override
    public ObjectSet<Entry> int2BooleanEntrySet() {
        return FastUtilHackUtil.entrySetIntBooleanWrap(backing);
    }

    @Override
    public ObjectSet<Map.Entry<Integer, Boolean>> entrySet() {
        return new FastUtilHackUtil.WrappingObjectSortedSet(backing.entrySet());
    }

    @Override
    public Boolean put(Integer key, Boolean value) {
        return backing.put(key, value);
    }

    @Override
    public Boolean get(Object key) {
        return backing.get(key);
    }

    @Override
    public Boolean remove(Object key) {
        return backing.remove(key);
    }

    @Override
    public IntSet keySet() {
        return new FastUtilHackUtil.WrappingIntSet(backing.keySet());
    }

    @Override
    public BooleanCollection values() {
        return new FastUtilHackUtil.WrappingBooleanCollection(backing.values());
    }

    @Override
    public boolean put(int key, boolean value) {
        Boolean put = backing.put(key, value);
        return put == null ? false : put;
    }

    @Override
    public boolean get(int key) {
        Boolean get = backing.get(key);
        return get == null ? false : get;
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
    public boolean containsValue(boolean value) {
        return backing.containsValue(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return backing.containsValue(value);
    }

    @Override
    public void forEach(BiConsumer<? super Integer, ? super Boolean> consumer) {
        backing.forEach(consumer);
    }

    @Override
    public void replaceAll(BiFunction<? super Integer, ? super Boolean, ? extends Boolean> function) {
        backing.replaceAll(function);
    }

    @Override
    public boolean getOrDefault(int key, boolean defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean remove(int key) {
        Boolean remove = backing.remove(key);
        return remove == null ? false : remove;
    }

    @Override
    public Boolean getOrDefault(Object key, Boolean defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean putIfAbsent(int key, boolean value) {
        Boolean b = backing.putIfAbsent(key, value);
        return b == null ? false : b;
    }

    @Override
    public boolean remove(int key, boolean value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(int key, boolean oldValue, boolean newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public boolean replace(int key, boolean value) {
        return backing.replace(key, value);
    }

    @Override
    public boolean computeIfAbsent(int key, IntPredicate mappingFunction) {
        Boolean b = backing.computeIfAbsent(key, value -> mappingFunction.test(value));
        return b == null ? false : b;
    }

    @Override
    public boolean computeIfAbsentNullable(int key, IntFunction<? extends Boolean> mappingFunction) {
        Boolean b = backing.computeIfAbsent(key, mappingFunction::apply);
        return b == null ? false : b;
    }

    @Override
    public boolean computeIfAbsent(int key, Int2BooleanFunction mappingFunction) {
        Boolean b = backing.computeIfAbsent(key, mappingFunction);
        return b == null ? false : b;
    }

    @Override
    public boolean computeIfAbsentPartial(int key, Int2BooleanFunction mappingFunction) {
        return computeIfAbsent(key, mappingFunction);
    }

    @Override
    public boolean computeIfPresent(int key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
        Boolean b = backing.computeIfPresent(key, remappingFunction);
        return b == null ? false : b;
    }

    @Override
    public boolean compute(int key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
        Boolean b = backing.compute(key, remappingFunction);
        return b == null ? false : b;
    }

    @Override
    public boolean merge(int key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        Boolean b = backing.merge(key, value, remappingFunction);
        return b == null ? false : b;
    }

    @Override
    public Boolean putIfAbsent(Integer key, Boolean value) {
        return backing.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(Integer key, Boolean oldValue, Boolean newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public Boolean replace(Integer key, Boolean value) {
        return backing.replace(key, value);
    }

    @Override
    public Boolean computeIfAbsent(Integer key, Function<? super Integer, ? extends Boolean> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Boolean computeIfPresent(Integer key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
        return backing.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Boolean compute(Integer key, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
        return backing.compute(key, remappingFunction);
    }

    @Override
    public Boolean merge(Integer key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }
}
