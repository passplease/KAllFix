package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FalseReference2ReferenceConcurrentHashMap<K, V> extends AbstractReference2ReferenceMap<K, V> {
    private final ConcurrentHashMap<K, V> backing;
    private FastUtilHackUtil.ConvertingObjectSortedSet<Map.Entry<K, V>, Entry<K, V>> reference2ReferenceEntrySet = null;

    public FalseReference2ReferenceConcurrentHashMap() {
        backing = new ConcurrentHashMap<K, V>();
    }
    public FalseReference2ReferenceConcurrentHashMap(ConcurrentHashMap<K, V> backing) {
        this.backing = backing;
    }

    @Override
    public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfAbsent(K key, Reference2ReferenceFunction<? super K, ? extends V> mappingFunction) {
        return backing.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public boolean containsValue(Object v) {
        return backing.containsValue(v);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        backing.putAll(m);
    }

    @Override
    public ReferenceSet<K> keySet() {
        return new OpenFalseAbstractReferenceSortedSet(backing.keySet());
    }

    @Override
    public ReferenceCollection<V> values() {
        return FastUtilHackUtil.wrapReference(backing.values());
    }

    @Override
    public boolean containsKey(Object k) {
        return backing.containsKey(k);
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
    public ObjectSet<Entry<K, V>> reference2ReferenceEntrySet() {
        ObjectSet<Entry<K, V>> es;
        if ((es = reference2ReferenceEntrySet) != null) return es;
        return reference2ReferenceEntrySet = new FastUtilHackUtil.ConvertingObjectSortedSet<>(backing.entrySet(), FalseEntry::new, v->((FalseEntry)v).entry);
    }

    @Override
    public ObjectSet<Map.Entry<K, V>> entrySet() {
        return new FastUtilHackUtil.WrappingObjectSortedSet<>(backing.entrySet());
    }


    @Override
    public V put(K key, V value) {
        return backing.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return backing.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return backing.remove(key, value);
    }

    @Override
    public V get(Object key) {
        return backing.get(key);
    }
    public static class FixNull<K, V> extends FalseReference2ReferenceConcurrentHashMap<K, V>{
        public FixNull(){
            super(new FixNullConcurrentHashMap<>());
        }
    }

    public record FalseEntry<K, V>(Map.Entry<K, V> entry) implements Entry<K, V> {
        public final K getKey() {
            return entry.getKey();
        }

        public final V getValue() {
            return entry.getValue();
        }

        public final String toString() {
                return entry.toString();
        }

        public final V setValue(V value) {
                throw new UnsupportedOperationException();
            }

        public final boolean equals(Object o) {
            return entry.equals(o);
        }
    }


    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public V putIfAbsent(K k, V v) {
        return backing.putIfAbsent(k, v);
    }

    @Override
    public V compute(K k, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return backing.compute(k, remappingFunction);
    }

    @Override
    public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return backing.computeIfPresent(k, remappingFunction);
    }

    @Override
    public boolean replace(K k, V oldValue, V v) {
        return backing.replace(k, oldValue, v);
    }

    @Override
    public V replace(K k, V v) {
        return backing.replace(k, v);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        backing.replaceAll(function);
    }

    @Override
    public int hashCode() {
        return backing.hashCode();
    }

    @Override
    public V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return backing.merge(k, v, remappingFunction);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> consumer) {
        backing.forEach(consumer);
    }
}
