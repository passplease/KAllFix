package n1luik.KAllFix.util;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class PrimeScalarEntitySectionMapMap<V extends EntityAccess> {
    private static final int BLOCK_SIZE = 32;
    private static final long A = 1000003L;
    private static final long B = 1000007L;
    private static final long C = 1000009L;

    private final Object lock = new Object();
    private Block<V> head;
    private Block<V> tail;
    {
        clear();
    }

    private static class Block<V extends EntityAccess> {
        Entry<V>[] entries;
        int size;
        Block<V> prev;
        Block<V> next;
        long[] keyMins;  // 单独的连续数组存储key_min
        long[] keyMaxs;  // 单独的连续数组存储key_max
        // 直接存储6个double代替AABB对象，减少对象开销和缓存不友好
        double minX, minY, minZ, maxX, maxY, maxZ;
        public Block(int capacity) {
            entries = new Entry[capacity];
            size = 0;
            prev = null;
            next = null;
            keyMins = new long[capacity];  // 独立连续数组
            keyMaxs = new long[capacity];  // 独立连续数组
        }
        public void recalcBlockAABB() {
            if (size == 0) {
                minX = minY = minZ = maxX = maxY = maxZ = 0;
                return;
            }
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            double maxZ = Double.MIN_VALUE;
            for (int i = 0; i < size; i++) {
                AABB box = entries[i].data.getBoundingBox();
                minX = Math.min(minX, box.minX);
                minY = Math.min(minY, box.minY);
                minZ = Math.min(minZ, box.minZ);
                maxX = Math.max(maxX, box.maxX);
                maxY = Math.max(maxY, box.maxY);
                maxZ = Math.max(maxZ, box.maxZ);
            }
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }
    }

    private static class Entry<V extends EntityAccess> {
        /// 哈希值用于排序（一维顺序）
        //long key_min;
        //long key_max;
        // 真实坐标用于精确碰撞检测
        double minX, minY, minZ;
        double maxX, maxY, maxZ;
        V data;
        //int offsetX;
        //int offsetY;
        //int offsetZ;

        Entry(double minX, double minY, double minZ,
              double maxX, double maxY, double maxZ,
              V data) {//, int offsetX, int offsetY, int offsetZ) {
            assert data != null;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            //this.key_min = (long) minX * A + (long) minY * B + (long) minZ * C;
            //this.key_max = (long) maxX * A + (long) maxY * B + (long) maxZ * C;
            this.data = data;
            //this.offsetX = offsetX;
            //this.offsetY = offsetY;
            //this.offsetZ = offsetZ;
        }
    }

    // 使用真实坐标计算包围盒（用于块过滤）
    private Block<V> findInsertBlock(double minX, double maxX) {
        Block<V> block = head;
        while (block != null) {
            if (block.size == 0) return block;
            // 按实体X轴最大值排序（真实空间顺序）
            if (maxX >= block.entries[block.size - 1].maxX) {
                if (block.next == null) return block;
                block = block.next;
            } else {
                return block;
            }
        }
        return head;
    }

    public void put(V data) {
        AABB boundingBox = data.getBoundingBox();
        put(boundingBox, data);
    }
    public void put(AABB key//, Vec3i offset
            , V data) {
        synchronized (lock) {
            Block<V> block = findInsertBlock(key.minX, key.maxX);

            if (block.size < BLOCK_SIZE) {
                int pos = block.size;
                block.entries[pos] = new Entry<>(key.minX, key.minY, key.minZ,
                                              key.maxX, key.maxY, key.maxZ,
                                              data);//, offset.getX(), offset.getY(), offset.getZ());
                // 直接计算并存入连续数组
                block.keyMins[pos] = (long)key.minX * A + (long)key.minY * B + (long)key.minZ * C;
                block.keyMaxs[pos] = (long)key.maxX * A + (long)key.maxY * B + (long)key.maxZ * C;
                block.size++;
                block.recalcBlockAABB();
            } else {
                // 分裂块逻辑（简化版）
                Block<V> newBlock = new Block<>(BLOCK_SIZE);
                int splitIndex = BLOCK_SIZE / 2;
                // 同时移动连续数组
                System.arraycopy(block.entries, splitIndex, newBlock.entries, 0, BLOCK_SIZE - splitIndex);
                System.arraycopy(block.keyMins, splitIndex, newBlock.keyMins, 0, BLOCK_SIZE - splitIndex);
                System.arraycopy(block.keyMaxs, splitIndex, newBlock.keyMaxs, 0, BLOCK_SIZE - splitIndex);
                newBlock.size = BLOCK_SIZE - splitIndex;
                block.size = splitIndex;

                block.recalcBlockAABB();
                newBlock.recalcBlockAABB();

                // 插入新块到链表
                newBlock.prev = block;
                newBlock.next = block.next;
                if (block.next != null) block.next.prev = newBlock;
                else tail = newBlock;
                block.next = newBlock;

                // 重新插入到正确块
                if (key.maxX < newBlock.entries[0].minX) {
                    int pos = block.size;
                    block.entries[pos] = new Entry<>(key.minX, key.minY, key.minZ,
                                                  key.maxX, key.maxY, key.maxZ,
                                                  data);//, offset.getX(), offset.getY(), offset.getZ());
                    block.keyMins[pos] = (long)key.minX * A + (long)key.minY * B + (long)key.minZ * C;
                    block.keyMaxs[pos] = (long)key.maxX * A + (long)key.maxY * B + (long)key.maxZ * C;
                    block.size++;
                    block.recalcBlockAABB();
                } else {
                    int pos = newBlock.size;
                    newBlock.entries[pos] = new Entry<>(key.minX, key.minY, key.minZ,
                                                     key.maxX, key.maxY, key.maxZ,
                                                     data);//, offset.getX(), offset.getY(), offset.getZ());
                    newBlock.keyMins[pos] = (long)key.minX * A + (long)key.minY * B + (long)key.minZ * C;
                    newBlock.keyMaxs[pos] = (long)key.maxX * A + (long)key.maxY * B + (long)key.maxZ * C;
                    newBlock.size++;
                    newBlock.recalcBlockAABB();
                }
            }
        }
    }

    public boolean remove(V data) {
        synchronized (lock) {
            Block<V> block = head;
            while (block != null) {
                for (int i = 0; i < block.size; i++) {
                    if (block.entries[i].data == data) {
                        // 移动entries数组
                        for (int j = i; j < block.size - 1; j++) {
                            block.entries[j] = block.entries[j + 1];
                        }
                        // 同时移动连续数组
                        for (int j = i; j < block.size - 1; j++) {
                            block.keyMins[j] = block.keyMins[j + 1];
                            block.keyMaxs[j] = block.keyMaxs[j + 1];
                        }
                        block.size--;
                        block.recalcBlockAABB();

                        if (block.size == 0) {
                            if (block == head) head = block.next;
                            if (block == tail) tail = block.prev;
                            if (block.prev != null) block.prev.next = block.next;
                            if (block.next != null) block.next.prev = block.prev;
                        }
                        return true;
                    }
                }
                block = block.next;
            }
            return false;
        }
    }
    // 批量插入优化 - 用于reset操作
    public void clearAndAddAll(List<V> entities) {
        synchronized (lock) {
            clear();
            if (entities.isEmpty()) return;

            // 按maxX排序，减少Block分裂
            entities.sort(Comparator.comparingDouble(e -> e.getBoundingBox().maxX));

            Block<V> currentBlock = head;
            for (V entity : entities) {
                AABB box = entity.getBoundingBox();
                if (currentBlock.size == BLOCK_SIZE) {
                    currentBlock = new Block<>(BLOCK_SIZE);
                    if (tail == null) {
                        head = currentBlock;
                        tail = currentBlock;
                    } else {
                        currentBlock.prev = tail;
                        tail.next = currentBlock;
                        tail = currentBlock;
                    }
                }

                int pos = currentBlock.size;
                currentBlock.entries[pos] = new Entry<>(box.minX, box.minY, box.minZ,
                        box.maxX, box.maxY, box.maxZ,
                        entity);
                currentBlock.keyMins[pos] = (long)box.minX * A + (long)box.minY * B + (long)box.minZ * C;
                currentBlock.keyMaxs[pos] = (long)box.maxX * A + (long)box.maxY * B + (long)box.maxZ * C;
                currentBlock.size++;
            }

            // 重置所有Block的AABB
            Block<V> block = head;
            while (block != null) {
                block.recalcBlockAABB();
                block = block.next;
            }
        }
    }
    // 所有get方法统一优化 - 使用连续数组加速
    public void get(AABB query, List<V> out) {
        Consumer<V> out2 = out::add;
        get(query, out2);
    }
    public void get(AABB query, Consumer<V> out) {
        synchronized (lock) {
            Block<V> block = head;
            while (block != null) {
                // 用6个double直接判断块是否相交
                if (block.size > 0 &&
                        (block.maxX < query.minX || block.minX > query.maxX ||
                                block.maxY < query.minY || block.minY > query.maxY ||
                                block.maxZ < query.minZ || block.minZ > query.maxZ)) {
                    block = block.next;
                    continue;
                }

                // 计算查询的key范围
                long queryKeyMin = (long)query.minX * A + (long)query.minY * B + (long)query.minZ * C;
                long queryKeyMax = (long)query.maxX * A + (long)query.maxY * B + (long)query.maxZ * C;

                // 二分查找起点：第一个keyMaxs >= queryKeyMin
                int low = 0, high = block.size;
                while (low < high) {
                    int mid = (low + high) >>> 1;
                    if (block.keyMaxs[mid] < queryKeyMin) {
                        low = mid + 1;
                    } else {
                        high = mid;
                    }
                }

                // 快速过滤并处理可能相交的条目
                for (int i = low; i < block.size; i++) {
                    // 使用连续数组快速跳过不相交项
                    if (block.keyMins[i] > queryKeyMax || block.keyMaxs[i] < queryKeyMin) {
                        continue;
                    }

                    // 精确检查
                    Entry<V> e = block.entries[i];
                    if (e.data.getBoundingBox().intersects(query)) {
                        out.accept(e.data);
                    }
                }
                block = block.next;
            }
        }
    }
    public void get(AABB query, Predicate<V> out) {
        synchronized (lock) {
            Block<V> block = head;
            while (block != null) {
                if (block.size > 0 &&
                        (block.maxX < query.minX || block.minX > query.maxX ||
                                block.maxY < query.minY || block.minY > query.maxY ||
                                block.maxZ < query.minZ || block.minZ > query.maxZ)) {
                    block = block.next;
                    continue;
                }

                long queryKeyMin = (long)query.minX * A + (long)query.minY * B + (long)query.minZ * C;
                long queryKeyMax = (long)query.maxX * A + (long)query.maxY * B + (long)query.maxZ * C;

                int low = 0, high = block.size;
                while (low < high) {
                    int mid = (low + high) >>> 1;
                    if (block.keyMaxs[mid] < queryKeyMin) {
                        low = mid + 1;
                    } else {
                        high = mid;
                    }
                }

                for (int i = low; i < block.size; i++) {
                    if (block.keyMins[i] > queryKeyMax || block.keyMaxs[i] < queryKeyMin) {
                        continue;
                    }

                    Entry<V> e = block.entries[i];
                    if (e.data.getBoundingBox().intersects(query) && out.test(e.data)) {
                        return;
                    }
                }
                block = block.next;
            }
        }
    }

    public void reset(V v){
        remove(v);
        put(v);
    }

    public void clear() {
        synchronized (lock) {
            head = new Block<>(BLOCK_SIZE);
            tail = head;
        }
    }
    public int size() {
        synchronized (lock) {
            int total = 0;
            Block<V> block = head;
            while (block != null) {
                total += block.size;
                block = block.next;
            }
            return total;
        }
    }
    public boolean isEmpty() {
        return size() == 0;
    }
}