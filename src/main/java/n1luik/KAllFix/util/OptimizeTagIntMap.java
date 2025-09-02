package n1luik.KAllFix.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;

public class OptimizeTagIntMap {
    private final Int2ObjectArrayMap<Data> map = new Int2ObjectArrayMap<>(2);
    public boolean contains(final int k) {
        Data integers = map.get(k);
        return integers != null && integers.v > 0;
    }
    public IntArraySet getKeys() {
        return new IntArraySet(map.keySet());
    }
    public synchronized void add(int k) {
        map.computeIfAbsent(k, k2 -> new Data()).v++;
    }
    public synchronized void remove(int k) {
        Data data = map.get(k);
        if (data == null)return;
        data.v--;
        if (data.v < 1){
            map.remove(k);
        }

    }
    private static class Data{
        private int v = 0;
    }
}
