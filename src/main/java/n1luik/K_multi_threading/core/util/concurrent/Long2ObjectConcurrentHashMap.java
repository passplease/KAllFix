package n1luik.K_multi_threading.core.util.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class Long2ObjectConcurrentHashMap<V> implements Long2ObjectMap<V> {

	protected final ConcurrentHashMap<Long, V> backing;
	V defaultReturn = null;
	
	public Long2ObjectConcurrentHashMap() {
		backing = new ConcurrentHashMap<Long, V>();
	}
	
	@Override
	public V get(long key) {
		V out = backing.get(key);
		return (out == null && !backing.containsKey(key)) ? defaultReturn : out;
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
	public void putAll(Map<? extends Long, ? extends V> m) {
		backing.putAll(m);
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public void defaultReturnValue(V rv) {
		defaultReturn = rv;
	}

	@Override
	public V defaultReturnValue() {
		return defaultReturn;
	}

	@Override
	public ObjectSet<Entry<V>> long2ObjectEntrySet() {
		return FastUtilHackUtil.entrySetLongWrap(backing);
	}

		
	@Override
	public LongSet keySet() {
		return FastUtilHackUtil.wrapLongSet(backing.keySet());
	}

	@Override
	public ObjectCollection<V> values() {
		return FastUtilHackUtil.wrap(backing.values());
	}

	@Override
	public boolean containsKey(long key) {
		return backing.containsKey(key);
	}

	@Override
	public V put(long key, V value) {
		return put((Long)key, value);
	}
	
	@Override
	public V put(Long key, V value) {
		V out = backing.put(key, value);
		return (out == null && !backing.containsKey(key)) ? defaultReturn : backing.put(key, value);
	}
	
	@Override
	public V remove(long key) {
		V out = backing.remove(key);
		return (out == null && !backing.containsKey(key)) ? defaultReturn : out;
	}

	@Override
	public void clear() {
		backing.clear();
	}
	@Override
	public V getOrDefault(long k, V defaultValue) {
		return backing.getOrDefault(k, defaultValue);
	}
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return backing.getOrDefault(key, defaultValue);
	}

	@Override
	public @Nullable V putIfAbsent(Long key, V value) {
		return backing.putIfAbsent(key, value);
	}

	@Override
	public V putIfAbsent(long k, V v) {
		return backing.putIfAbsent(k, v);
	}

	@Override
	public V compute(Long key, @NotNull BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
		return backing.compute(key, remappingFunction);
	}

	@Override
	public V compute(long k, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
		return backing.compute(k, remappingFunction);
	}

	@Override
	public V computeIfPresent(Long key, @NotNull BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
		return backing.computeIfPresent(key, remappingFunction);
	}

	@Override
	public V computeIfPresent(long k, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
		return backing.computeIfPresent(k, remappingFunction);
	}

	@Override
	public @Nullable V replace(Long key, V value) {
		return backing.replace(key, value);
	}

	@Override
	public boolean replace(Long key, V oldValue, V newValue) {
		return backing.replace(key, oldValue, newValue);
	}

	@Override
	public boolean replace(long k, V oldValue, V v) {
		return backing.replace(k, oldValue, v);
	}

	@Override
	public V replace(long k, V v) {
		return backing.replace(k, v);
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
		return backing.hashCode();
	}

	@Override
	public boolean remove(Object key, Object value) {
		return backing.remove(key, value);
	}

	@Override
	public void replaceAll(BiFunction<? super Long, ? super V, ? extends V> function) {
		backing.replaceAll(function);
	}

	@Override
	public boolean remove(long k, Object v) {
		return backing.remove(k, v);
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
	public V merge(Long key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return backing.merge(key, value, remappingFunction);
	}

	@Override
	public V merge(long k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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

	@Override
	public void forEach(BiConsumer<? super Long, ? super V> consumer) {
		backing.forEach(consumer);
	}
}
