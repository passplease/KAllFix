package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.objects.AbstractReference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FalseReference2ReferenceConcurrentHashMap2<K, V> extends Reference2ReferenceArrayMap<K, V> {
    private final ConcurrentHashMap<K, V> map;
    private FastUtilHackUtil.ConvertingObjectSetFast_Reference2Reference<Map.Entry<K, V>, K, V> reference2ReferenceEntrySet = null;

    public FalseReference2ReferenceConcurrentHashMap2() {
        map = new ConcurrentHashMap<K, V>();
    }
    public FalseReference2ReferenceConcurrentHashMap2(ConcurrentHashMap<K, V> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public FastEntrySet<K, V> reference2ReferenceEntrySet() {
        FastEntrySet<K, V> es;
        if ((es = reference2ReferenceEntrySet) != null) return es;
        return reference2ReferenceEntrySet = new FastUtilHackUtil.ConvertingObjectSetFast_Reference2Reference<>(map.entrySet(), FalseEntry::new, v->((FalseEntry)v).entry);
    }

    @Override
    public ObjectSet<Map.Entry<K, V>> entrySet() {
        return new FastUtilHackUtil.WrappingObjectSortedSet<>(map.entrySet());
    }


    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }
    public static class FixNull<K, V> extends FalseReference2ReferenceConcurrentHashMap2<K, V> {
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
}
