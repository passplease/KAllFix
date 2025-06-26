package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;
import java.util.function.LongPredicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class LongConcurrentHashSet implements LongSet {

	protected final ConcurrentHashMap.KeySetView<Long,Boolean> backing;

    public LongConcurrentHashSet() {
        backing = ConcurrentHashMap.newKeySet();
    }

    @Override
	public boolean add(long key) {
		return backing.add(key);
	}

	@Override
	public boolean contains(long key) {
		return backing.contains(key);
	}

	@Override
	public long[] toLongArray() {
		return backing.stream().mapToLong(i -> i).toArray();
	}

	@Override
	public long[] toLongArray(long[] a) {
		if (a.length >= size()) {
			throw new UnsupportedOperationException();
		} else {
			return toLongArray();
		}
	}

	@Override
	public long[] toArray(long[] a) {
		return toLongArray(a);
	}

	@Override
	public boolean addAll(LongCollection c) {
		return backing.addAll(c);
	}

	@Override
	public boolean containsAll(LongCollection c) {
		return backing.containsAll(c);
	}

	@Override
	public boolean removeAll(LongCollection c) {
		return backing.removeAll(c);
	}

	@Override
	public boolean retainAll(LongCollection c) {
		return backing.retainAll(c);
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public boolean isEmpty() {
		return backing.isEmpty();
	}

	@Override
	public Object[] toArray() {
		return backing.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return backing.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backing.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Long> c) {
		return backing.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return backing.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return backing.retainAll(c);
	}

	@Override
	public void clear() {
		backing.clear();
	}

	@Override
	public LongIterator iterator() {
		return new FastUtilHackUtil.WrappingLongIterator(backing.iterator());
	}

	@Override
	public boolean remove(long k) {
		return backing.remove(k);
	}

	@Override
	public LongStream longStream() {
		return backing.stream().mapToLong(i -> i);
	}

	@Override
	public @NotNull Stream<Long> stream() {
		return backing.stream();
	}

	@Override
	public int hashCode() {
		return backing.hashCode();
	}

	@Override
	public String toString() {
		return backing.toString();
	}

	@Override
	public <T1> T1[] toArray(@NotNull IntFunction<T1[]> generator) {
		return backing.toArray(generator);
	}

	@Override
	public void forEach(LongConsumer action) {
		backing.forEach(action);
	}
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Set)) return false;
		Set<?> s = (Set<?>)o;
		if (s.size() != size()) return false;
		return backing.containsAll(s);
	}

	@Override
	public boolean removeIf(LongPredicate filter) {
		return backing.removeIf(filter::test);
	}
}