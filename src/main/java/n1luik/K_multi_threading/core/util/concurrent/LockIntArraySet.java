package n1luik.K_multi_threading.core.util.concurrent;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;

import java.util.Collection;

public class LockIntArraySet extends IntArraySet {
    public LockIntArraySet() {
        super();
    }
    public LockIntArraySet(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public synchronized boolean remove(int k) {
        return super.remove(k);
    }

    @Override
    public synchronized boolean add(int k) {
        return super.add(k);
    }

    @Override
    public synchronized boolean add(Integer key) {
        return super.add(key);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends Integer> c) {
        return super.addAll(c);
    }

    @Override
    public synchronized boolean addAll(IntCollection c) {
        return super.addAll(c);
    }

    @Override
    public synchronized boolean remove(Object key) {
        return super.remove(key);
    }

    @Override
    public synchronized boolean removeAll(IntCollection c) {
        return super.removeAll(c);
    }
}
