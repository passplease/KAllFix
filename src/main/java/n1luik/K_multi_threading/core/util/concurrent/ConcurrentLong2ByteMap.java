package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteBinaryOperator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.impl.shadow.V;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

public class ConcurrentLong2ByteMap implements Long2ByteMap {
    protected final ConcurrentHashMap<Long, Byte> backing = new ConcurrentHashMap<>();


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
    public void putAll(@NotNull Map<? extends Long, ? extends Byte> m) {
        backing.putAll(m);
    }

    @Override
    public void defaultReturnValue(byte rv) {
        throw new UnsupportedOperationException("MCMT - Not implemented");
    }

    @Override
    public byte defaultReturnValue() {
        return 0;
    }

    @Override
    public ObjectSet<Entry> long2ByteEntrySet() {
        return FastUtilHackUtil.entrySetLongByteWrap(backing);
    }

    @Override
    public ObjectSet<Map.Entry<Long, Byte>> entrySet() {
        return new FastUtilHackUtil.WrappingObjectSortedSet(backing.entrySet());
    }

    @Override
    public Byte put(Long key, Byte value) {
        return backing.put(key, value);
    }

    @Override
    public Byte get(Object key) {
        return backing.get(key);
    }

    @Override
    public Byte remove(Object key) {
        return backing.remove(key);
    }

    @Override
    public LongSet keySet() {
        return new FastUtilHackUtil.WrappingLongSet(backing.keySet());
    }

    @Override
    public ByteCollection values() {
        return new FastUtilHackUtil.WrappingByteCollection(backing.values());
    }

    @Override
    public byte put(long key, byte value) {
        Byte put = backing.put(key, value);
        return put == null ? 0 : put;
    }

    @Override
    public byte get(long key) {
        Byte get = backing.get(key);
        return get == null ? 0 : get;
    }

    @Override
    public boolean containsKey(long key) {
        return backing.containsKey(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return backing.containsKey(key);
    }

    @Override
    public boolean containsValue(byte value) {
        return backing.containsValue(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return backing.containsValue(value);
    }

    @Override
    public void forEach(BiConsumer<? super Long, ? super Byte> consumer) {
        backing.forEach(consumer);
    }

    @Override
    public void replaceAll(BiFunction<? super Long, ? super Byte, ? extends Byte> function) {
        backing.replaceAll(function);
    }

    @Override
    public byte getOrDefault(long key, byte defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public byte remove(long key) {
        Byte remove = backing.remove(key);
        return remove == null ? 0 : remove;
    }

    @Override
    public Byte getOrDefault(Object key, Byte defaultValue) {
        return backing.getOrDefault(key, defaultValue);
    }

    @Override
    public byte putIfAbsent(long key, byte value) {
        Byte b = backing.putIfAbsent(key, value);
        return b == null ? 0 : b;
    }

    @Override
    public boolean remove(long key, byte value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(long key, byte oldValue, byte newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public byte replace(long key, byte value) {
        return backing.replace(key, value);
    }

    @Override
    public byte computeIfAbsent(long key, LongToIntFunction mappingFunction) {
        Byte b = backing.computeIfAbsent(key, value -> SafeMath.safeIntToByte(mappingFunction.applyAsInt(value)));
        return b == null ? 0 : b;
    }

    @Override
    public byte computeIfAbsentNullable(long key, LongFunction<? extends Byte> mappingFunction) {
        Byte b = backing.computeIfAbsent(key, mappingFunction::apply);
        return b == null ? 0 : b;
    }

    @Override
    public byte computeIfAbsent(long key, Long2ByteFunction mappingFunction) {
        Byte b = backing.computeIfAbsent(key, mappingFunction);
        return b == null ? 0 : b;
    }

    @Override
    public byte computeIfAbsentPartial(long key, Long2ByteFunction mappingFunction) {
        return computeIfAbsent(key, mappingFunction);
    }

    @Override
    public byte computeIfPresent(long key, BiFunction<? super Long, ? super Byte, ? extends Byte> remappingFunction) {
        Byte b = backing.computeIfPresent(key, remappingFunction);
        return b == null ? 0 : b;
    }

    @Override
    public byte compute(long key, BiFunction<? super Long, ? super Byte, ? extends Byte> remappingFunction) {
        Byte b = backing.compute(key, remappingFunction);
        return b == null ? 0 : b;
    }

    @Override
    public byte merge(long key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
        Byte b = backing.merge(key, value, remappingFunction);
        return b == null ? 0 : b;
    }

    @Override
    public byte mergeByte(long key, byte value, ByteBinaryOperator remappingFunction) {
        Byte b = backing.merge(key, value, remappingFunction);
        return b == null ? 0 : b;
    }

    @Override
    public byte mergeByte(long key, byte value, IntBinaryOperator remappingFunction) {
        return mergeByte(key, value, (a, b) -> (byte) remappingFunction.applyAsInt(a, b));
    }

    @Override
    public Byte putIfAbsent(Long key, Byte value) {
        return backing.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return backing.remove(key, value);
    }

    @Override
    public boolean replace(Long key, Byte oldValue, Byte newValue) {
        return backing.replace(key, oldValue, newValue);
    }

    @Override
    public Byte replace(Long key, Byte value) {
        return backing.replace(key, value);
    }

    @Override
    public Byte computeIfAbsent(Long key, Function<? super Long, ? extends Byte> mappingFunction) {
    return backing.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Byte computeIfPresent(Long key, BiFunction<? super Long, ? super Byte, ? extends Byte> remappingFunction) {
        return backing.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Byte compute(Long key, BiFunction<? super Long, ? super Byte, ? extends Byte> remappingFunction) {
        return backing.compute(key, remappingFunction);
    }

    @Override
    public Byte merge(Long key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
        return backing.merge(key, value, remappingFunction);
    }
}
