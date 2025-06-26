package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.impl.shadow.V;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

public class Object2BooleanConcurrentHashMap<K> implements Object2BooleanMap<K> {

	protected final ConcurrentHashMap<K, Boolean> backing;

	public Object2BooleanConcurrentHashMap() {
		backing = new ConcurrentHashMap<K, Boolean>();
	}

	@Override
	public boolean getBoolean(Object key) {
		return backing.getOrDefault(key, false);
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
	public void putAll(@NotNull Map<? extends K, ? extends Boolean> m) {
		backing.putAll(m);
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public void defaultReturnValue(boolean rv) {
		throw new NotImplementedException("MCMT - Not implemented");
	}

	@Override
	public boolean defaultReturnValue() {
		return false;
	}

	@Override
	public ObjectSet<Entry<K>> object2BooleanEntrySet() {
		return FastUtilHackUtil.entrySetBooleanWrap(backing);
	}

	@Override
	public ObjectSet<K> keySet() {
		return new FastUtilHackUtil.ToObjectSet<>(backing.keySet());
	}

	@Override
	public BooleanCollection values() {
		return FastUtilHackUtil.wrapBoolean(backing.values());
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
	public boolean put(K key, boolean value) {
		Boolean put = backing.put(key, value);
		return put != null && put;
	}

	@Override
	public Boolean put(K key, Boolean value) {
		return backing.put(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return backing.remove(key, value);
	}

	@Override
	public boolean removeBoolean(Object key) {
		Boolean remove = backing.remove(key);
		return remove != null && remove;
	}

	@Override
	public boolean getOrDefault(Object key, boolean defaultValue) {
		return backing.getOrDefault(key, defaultValue);
	}

	@Override
	public @Nullable Boolean replace(K key, Boolean value) {
		return backing.replace(key, value);
	}

	@Override
	public Boolean getOrDefault(Object key, Boolean defaultValue) {
		return backing.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean replace(K key, boolean value) {
		Boolean replace = backing.replace(key, value);
		return replace != null && replace;
	}

	@Override
	public void clear() {
		backing.clear();
	}

	@Override
	public Boolean computeIfPresent(K key, @NotNull BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
		return backing.computeIfPresent(key, remappingFunction);
	}

	@Override
	public boolean computeIfAbsent(K key, Object2BooleanFunction<? super K> mappingFunction) {
		return backing.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public boolean computeIfAbsent(K key, Predicate<? super K> mappingFunction) {
		return backing.computeIfAbsent(key, mappingFunction::test);
	}

	@Override
	public Boolean computeIfAbsent(K key, @NotNull Function<? super K, ? extends Boolean> mappingFunction) {
		return backing.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public boolean remove(Object key, boolean value) {
		return backing.remove(key, value);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super Boolean> consumer) {
		backing.forEach(consumer);
	}

	@Override
	public @Nullable Boolean putIfAbsent(K key, Boolean value) {
		return backing.putIfAbsent(key, value);
	}

	@Override
	public boolean putIfAbsent(K key, boolean value) {
		return Boolean.TRUE.equals(backing.putIfAbsent(key, value));
	}

	@Override
	public Boolean compute(K key, @NotNull BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
		return backing.compute(key, remappingFunction);
	}

	@Override
	public boolean computeBoolean(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
		return backing.compute(key, remappingFunction);
	}

	@Override
	public boolean replace(K key, Boolean oldValue, Boolean newValue) {
		return backing.replace(key, oldValue, newValue);
	}

	@Override
	public boolean replace(K key, boolean oldValue, boolean newValue) {
		return backing.replace(key, oldValue, newValue);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super Boolean, ? extends Boolean> function) {
		backing.replaceAll(function);
	}

	@Override
	public int hashCode() {
		return  backing.hashCode();
	}

	@Override
	public Boolean get(Object key) {
		return backing.get(key);
	}

	@Override
	public String toString() {
		return backing.toString();
	}

	@Override
	public boolean merge(K key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
		return backing.merge(key, value, remappingFunction);
	}

	@Override
	public Boolean merge(K key, @NotNull Boolean value, @NotNull BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
		return backing.merge(key, value, remappingFunction);
	}
}
