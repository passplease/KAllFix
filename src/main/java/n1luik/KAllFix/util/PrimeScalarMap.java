package n1luik.KAllFix.util;

import java.util.List;

public class PrimeScalarMap<V> {

    public static class AABB {
        public double minX, minY, minZ;
        public double maxX, maxY, maxZ;
        public AABB(double minX, double minY, double minZ,
                    double maxX, double maxY, double maxZ) {
            this.minX = minX; this.minY = minY; this.minZ = minZ;
            this.maxX = maxX; this.maxY = maxY; this.maxZ = maxZ;
        }
    }

    public static class Vec3i {
        public int x, y, z;
        public Vec3i(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }
    }

    private static final long A = 1000003L;
    private static final long B = 1000007L;
    private static final long C = 1000009L;

    private static final class Entry<V> {
        long key_min;
        long key_max;
        int[] offset;
        short[] aabb;
        V data;
        Entry(long key_min, long key_max, int[] offset, short[] aabb, V data) {
            this.key_min = key_min; this.key_max = key_max;
            this.offset = offset; this.aabb = aabb; this.data = data;
        }
    }

    private volatile Entry<V>[] array;
    private volatile long global_min_key;
    private volatile long global_max_key;
    private final Object lock = new Object();

    @SuppressWarnings("unchecked")
    public PrimeScalarMap() {
        array = (Entry<V>[]) new Entry<?>[0];
        global_min_key = Long.MAX_VALUE;
        global_max_key = Long.MIN_VALUE;
    }

    private static long computeKeyMin(double minX, double minY, double minZ) {
        return (long) minX * A + (long) minY * B + (long) minZ * C;
    }

    private static long computeKeyMax(double maxX, double maxY, double maxZ) {
        return (long) maxX * A + (long) maxY * B + (long) maxZ * C;
    }

    public static boolean intersects(double minX, double minY, double minZ,
                                     double maxX, double maxY, double maxZ,
                                     double qminX, double qminY, double qminZ,
                                     double qmaxX, double qmaxY, double qmaxZ) {
        return minX <= qmaxX && maxX >= qminX &&
               minY <= qmaxY && maxY >= qminY &&
               minZ <= qmaxZ && maxZ >= qminZ;
    }

    public void put(AABB key, Vec3i offset, V data) {
        synchronized (lock) {
            long km = computeKeyMin(key.minX, key.minY, key.minZ);
            long kM = computeKeyMax(key.maxX, key.maxY, key.maxZ);
            short[] aabbStorage = new short[6];
            aabbStorage[0] = (short) (key.minX - offset.x);
            aabbStorage[1] = (short) (key.minY - offset.y);
            aabbStorage[2] = (short) (key.minZ - offset.z);
            aabbStorage[3] = (short) (key.maxX - offset.x);
            aabbStorage[4] = (short) (key.maxY - offset.y);
            aabbStorage[5] = (short) (key.maxZ - offset.z);
            int[] offsetArr = new int[]{offset.x, offset.y, offset.z};
            Entry<V> newEntry = new Entry<>(km, kM, offsetArr, aabbStorage, data);
            int insertPos = findInsertPos(array, km);
            @SuppressWarnings("unchecked")
            Entry<V>[] newArray = (Entry<V>[]) new Entry<?>[array.length + 1];
            if (insertPos > 0) System.arraycopy(array, 0, newArray, 0, insertPos);
            newArray[insertPos] = newEntry;
            if (insertPos < array.length) System.arraycopy(array, insertPos, newArray, insertPos + 1, array.length - insertPos);
            updateGlobalKeys(newArray);
            array = newArray;
        }
    }

    // remove by exact AABB+offset+data match. returns true if removed.
    public boolean remove(AABB key, Vec3i offset, V data) {
        synchronized (lock) {
            if (array.length == 0) return false;
            long km = computeKeyMin(key.minX, key.minY, key.minZ);
            long kM = computeKeyMax(key.maxX, key.maxY, key.maxZ);
            short[] ta = new short[6];
            ta[0] = (short) (key.minX - offset.x);
            ta[1] = (short) (key.minY - offset.y);
            ta[2] = (short) (key.minZ - offset.z);
            ta[3] = (short) (key.maxX - offset.x);
            ta[4] = (short) (key.maxY - offset.y);
            ta[5] = (short) (key.maxZ - offset.z);
            int matchIdx = -1;
            for (int i = 0; i < array.length; i++) {
                Entry<V> e = array[i];
                if (e.key_min != km || e.key_max != kM) continue;
                if (e.offset[0] != offset.x || e.offset[1] != offset.y || e.offset[2] != offset.z) continue;
                short[] a = e.aabb;
                if (a[0] != ta[0] || a[1] != ta[1] || a[2] != ta[2] || a[3] != ta[3] || a[4] != ta[4] || a[5] != ta[5]) continue;
                if (e.data != data) continue;
                matchIdx = i;
                break;
            }
            if (matchIdx < 0) return false;
            @SuppressWarnings("unchecked")
            Entry<V>[] newArray = (Entry<V>[]) new Entry<?>[array.length - 1];
            if (matchIdx > 0) System.arraycopy(array, 0, newArray, 0, matchIdx);
            if (matchIdx < array.length - 1) System.arraycopy(array, matchIdx + 1, newArray, matchIdx, array.length - matchIdx - 1);
            updateGlobalKeys(newArray);
            array = newArray;
            return true;
        }
    }

    // remove all entries matching data reference. returns count removed.
    public int remove(V data) {
        synchronized (lock) {
            if (array.length == 0) return 0;
            int removed = 0;
            for (Entry<V> e : array) {
                if (e.data == data) removed++;
            }
            if (removed == 0) return 0;
            int newLen = array.length - removed;
            @SuppressWarnings("unchecked")
            Entry<V>[] newArray = (Entry<V>[]) new Entry<?>[newLen];
            int wi = 0;
            for (Entry<V> e : array) {
                if (e.data != data) newArray[wi++] = e;
            }
            updateGlobalKeys(newArray);
            array = newArray;
            return removed;
        }
    }

    // clear all entries
    public void clear() {
        synchronized (lock) {
            @SuppressWarnings("unchecked")
            Entry<V>[] newArray = (Entry<V>[]) new Entry<?>[0];
            global_min_key = Long.MAX_VALUE;
            global_max_key = Long.MIN_VALUE;
            array = newArray;
        }
    }

    private void updateGlobalKeys(Entry<V>[] newArray) {
        long new_min = Long.MAX_VALUE;
        long new_max = Long.MIN_VALUE;
        for (Entry<V> e : newArray) {
            if (e.key_min < new_min) new_min = e.key_min;
            if (e.key_max > new_max) new_max = e.key_max;
        }
        global_min_key = new_min;
        global_max_key = new_max;
    }

    private static <V> int findInsertPos(Entry<V>[] arr, long key_min) {
        int lo = 0, hi = arr.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (arr[mid].key_min < key_min) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }

    public void get(AABB query, List<V> out) {
        Entry<V>[] snapshot = array;
        long g_min = global_min_key;
        long g_max = global_max_key;
        if (snapshot.length == 0) return;
        long q_min = computeKeyMin(query.minX, query.minY, query.minZ);
        long q_max = computeKeyMax(query.maxX, query.maxY, query.maxZ);
        if (q_min <= g_min && q_max >= g_max) {
            for (Entry<V> e : snapshot) out.add(e.data);
            return;
        }
        int right = findRightBound(snapshot, q_max);
        if (right < 0) return;
        for (int i = 0; i <= right; i++) {
            Entry<V> e = snapshot[i];
            if (e.key_max < q_min) continue;
            double ax_min = (double) e.aabb[0] + e.offset[0];
            double ay_min = (double) e.aabb[1] + e.offset[1];
            double az_min = (double) e.aabb[2] + e.offset[2];
            double ax_max = (double) e.aabb[3] + e.offset[0];
            double ay_max = (double) e.aabb[4] + e.offset[1];
            double az_max = (double) e.aabb[5] + e.offset[2];
            if (intersects(ax_min, ay_min, az_min,
                            ax_max, ay_max, az_max,
                            query.minX, query.minY, query.minZ,
                            query.maxX, query.maxY, query.maxZ)) {
                out.add(e.data);
            }
        }
    }

    private static <V> int findRightBound(Entry<V>[] snapshot, long q_max) {
        int lo = 0, hi = snapshot.length - 1, result = -1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            if (snapshot[mid].key_min <= q_max) {
                result = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return result;
    }

    public int size() { return array.length; }
    public boolean isEmpty() { return array.length == 0; }
}