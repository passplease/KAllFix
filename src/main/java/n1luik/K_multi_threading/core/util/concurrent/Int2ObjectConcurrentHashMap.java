package n1luik.K_multi_threading.core.util.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import org.apache.commons.lang3.NotImplementedException;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class Int2ObjectConcurrentHashMap<V> implements Int2ObjectMap<V> {

	protected final ConcurrentHashMap<Integer, V> backing;
	
	public Int2ObjectConcurrentHashMap() {
		backing = new ConcurrentHashMap<Integer, V>();
	}

	public Int2ObjectConcurrentHashMap(Map<Integer, ? extends V> v) {
		backing = new ConcurrentHashMap<>(v);
	}

	@Override
	public V get(int key) {
		return backing.get(key);
	}

	@Override
	public boolean isEmpty() {
		return backing.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		return backing.containsValue(value);
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends V> m) {
		backing.putAll(m);
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public void defaultReturnValue(V rv) {
		throw new NotImplementedException("MCMT - Not implemented");
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
	public IntSet keySet() {
		return FastUtilHackUtil.wrapIntSet(backing.keySet());
	}

	@Override
	public ObjectCollection<V> values() {
		return FastUtilHackUtil.wrap(backing.values());
	}

	@Override
	public boolean containsKey(int key) {
		return backing.containsKey(key);
	}

	@Override
	public V put(int key, V value) {
		return backing.put(key, value);
	}
	
	@Override
	public V put(Integer key, V value) {
		return backing.put(key, value);
	}
	
	@Override
	public V remove(int key) {
		return backing.remove(key);
	}

	@Override
	public void clear() {
		backing.clear();
	}

	@Override
	public V computeIfAbsent(Integer key, @NotNull Function<? super Integer, ? extends V> mappingFunction) {
		return backing.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public V computeIfAbsent(int k, IntFunction<? extends V> mappingFunction) {
		return backing.computeIfAbsent(k, mappingFunction::apply);
	}

	@Override
	public V computeIfAbsent(int key, Int2ObjectFunction<? extends V> mappingFunction) {
		return backing.computeIfAbsent(key, mappingFunction);
	}

	//@Override
	//public ConcurrentInt2ObjectOpenHashMap<V> clone() {
	//    ConcurrentInt2ObjectOpenHashMap<V> clone = (ConcurrentInt2ObjectOpenHashMap<V>)super.clone();
	//    clone.backing.clear();
	//    clone.backing.putAll(backing);
	//    return clone;
	//}

	@Override
	public void forEach(BiConsumer<? super Integer, ? super V> consumer) {
		backing.forEach(consumer);
	}

	@Override
	public V getOrDefault(int k, V defaultValue) {
		return backing.getOrDefault(k, defaultValue);
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return backing.getOrDefault(key, defaultValue);
	}

	@Override
	public V putIfAbsent(int k, V v) {
		return backing.putIfAbsent(k, v);
	}

	@Override
	public V compute(Integer key, @NotNull BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		return backing.compute(key, remappingFunction);
	}

	@Override
	public V compute(int key, @NotNull BiFunction<? super Integer,? super V,? extends V> remappingFunction) {
		return backing.compute(key, remappingFunction);
	}

	@Override
	public V computeIfPresent(int k, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		return backing.computeIfPresent(k, remappingFunction);
	}

	@Override
	public V computeIfPresent(Integer key, @NotNull BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		return backing.computeIfPresent(key, remappingFunction);
	}

	@Override
	public @Nullable V putIfAbsent(Integer key, V value) {
		return backing.putIfAbsent(key, value);
	}

	@Override
	public @Nullable V replace(Integer key, V value) {
		return backing.replace(key, value);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Map)) return false;
		final Map<?, ?> m = (Map<?, ?>)o;
		return backing.entrySet().containsAll(m.entrySet());
	}

	@Override
	public int hashCode() {
		return  backing.hashCode();
	}

	@Override
	public boolean replace(int k, V oldValue, V v) {
		return backing.replace(k, oldValue, v);
	}

	@Override
	public boolean replace(Integer key, V oldValue, V v) {
		return backing.replace(key, oldValue, v);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return backing.remove(key, value);
	}

	@Override
	public void replaceAll(BiFunction<? super Integer, ? super V, ? extends V> function) {
		backing.replaceAll(function);
	}

	@Override
	public boolean remove(int k, Object v) {
		return backing.remove(k, v);
	}

	@Override
	public V replace(int k, V v) {
		return backing.replace(k, v);
	}

	@Override
	public V get(Object key) {
		return backing.get(key);
	}

	@Override
	public String toString() {
		return backing.toString();
	}

	@Override
	public V merge(Integer key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return backing.merge(key, value, remappingFunction);
	}

	@Override
	public V merge(int k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return backing.merge(k, v, remappingFunction);
	}

	@Override
	public V remove(Object key) {
		return backing.remove(key);
	}

	@Override
	public boolean containsKey(Object key) {
		return backing.containsKey(key);
	}
}
