package n1luik.K_multi_threading.core.base;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Getter;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.util.Unsafe;
import n1luik.K_multi_threading.core.util.UnsafeClone;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.datafixers.DataFixer;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
/* */

/* 1.15.2 code; AKA the only thing that changed
import java.io.File;
/* */
//从mcmt复制并修改 1.18.2原码
public class ParaServerChunkProvider extends ServerChunkCache implements IWorldChunkLockedConfig {
    public static final Field currentlyLoading;

    static {
        try {
            currentlyLoading = ChunkHolder.class.getDeclaredField("currentlyLoading");
            currentlyLoading.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Object2LongMap<String> initId = new Object2LongOpenHashMap<>();
    public static final UnsafeClone<ServerChunkCache, ParaServerChunkProvider> UnsafeClone = new UnsafeClone<>(ServerChunkCache.class, ParaServerChunkProvider.class);
    protected static final int CACHE_SIZE = 4096;
    protected static final boolean level_lock_mode = false;
    //protected Thread cacheThread;
    protected Map<ChunkCacheAddress, ChunkCacheLine> chunkCache = new ConcurrentHashMap<ChunkCacheAddress, ChunkCacheLine>();
    protected AtomicInteger access = new AtomicInteger(Integer.MIN_VALUE);
    protected long clearTime = 0;
    protected final Object lock = new Object();
    protected final Object lock2 = new Object();
    protected final Object tasksRunLock = new Object();
    protected volatile boolean isCallTick = false;
    protected volatile boolean isCallGeneratorTick = false;
    protected final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    protected final List<Runnable> generatorTasks = new CopyOnWriteArrayList<>();
    protected final List<Object> locks = new CopyOnWriteArrayList<>();
    protected final Map<Long, Thread> threadBlacklist = new ConcurrentHashMap<>();

    //protected final IMainThreadExecutor iMainThreadExecutor;
    //protected ChunkLock loadingChunkLock = new ChunkLock();
    @Getter
    protected final List<Thread> generatorThread1 = new CopyOnWriteArrayList<>();
    @Getter
    protected Thread generatorThread2;
    static Logger log = LogManager.getLogger();
    Marker chunkCleaner = MarkerManager.getMarker("ChunkCleaner");

    static {
        for (Field declaredField : ParaServerChunkProvider.class.getDeclaredFields()) {
            if (!Modifier.isStatic(declaredField.getModifiers())) {
                initId.put(declaredField.getName(), Unsafe.unsafe.objectFieldOffset(declaredField));
            }

        }
    }

    public ParaServerChunkProvider(ServerLevel worldIn, LevelStorageSource.LevelStorageAccess worldDirectory, DataFixer dataFixer, StructureTemplateManager templateManagerIn, Executor executorIn, ChunkGenerator chunkGeneratorIn, int viewDistance, int simDistance, boolean spawnHostiles, ChunkProgressListener p_143236_, ChunkStatusUpdateListener p_143237_, Supplier<DimensionDataStorage> p_143238_) {
        super(worldIn, worldDirectory, dataFixer, templateManagerIn, executorIn, chunkGeneratorIn, viewDistance,
                simDistance, spawnHostiles, p_143236_, p_143237_, p_143238_);

        //iMainThreadExecutor = (IMainThreadExecutor) mainThreadProcessor;
        //cacheThread = new Thread(this::chunkCacheCleanup, "Chunk Cache Cleaner " + worldIn.dimension().location().getPath());
        //cacheThread.start();

        //这里出现过locks没有数据
        for (int i = 0; i < BuiltInRegistries.CHUNK_STATUS.size(); i++) {
            locks.add(new Object());
        }
    }
    public void UnsafeInit() {
        chunkCache = new ConcurrentHashMap<ChunkCacheAddress, ChunkCacheLine>();
        access = new AtomicInteger(Integer.MIN_VALUE);
        clearTime = 0;

        Unsafe.unsafe.putObject(this, initId.getLong("lock"), new Object());
        Unsafe.unsafe.putObject(this, initId.getLong("lock2"), new Object());
        Unsafe.unsafe.putObject(this, initId.getLong("tasksRunLock"), new Object());
        isCallTick = false;
        isCallGeneratorTick = false;
        Unsafe.unsafe.putObject(this, initId.getLong("tasks"), new CopyOnWriteArrayList<>());
        Unsafe.unsafe.putObject(this, initId.getLong("generatorTasks"), new CopyOnWriteArrayList<>());
        Unsafe.unsafe.putObject(this, initId.getLong("locks"), new ArrayList<>(256));
        Unsafe.unsafe.putObject(this, initId.getLong("threadBlacklist"), new ConcurrentHashMap<>());
        Unsafe.unsafe.putObject(this, initId.getLong("generatorThread1"), new CopyOnWriteArrayList<>());
        chunkCleaner = MarkerManager.getMarker("ChunkCleaner");

        for (int i = 0; i < BuiltInRegistries.CHUNK_STATUS.size(); i++) {
            locks.add(new Object());
        }
    }

    /*@Override
    public int getTickingGenerated() {
        return Math.min(super.getTickingGenerated(), 441);
    }*/

    @Nullable
    public ChunkAccess waitGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
        synchronized (locks.get(requiredStatus.getIndex())) {
            CompletableFuture<ChunkAccess> chunkAccessCompletableFuture = CompletableFuture.supplyAsync(() -> {
                return this.getChunk(chunkX, chunkZ, requiredStatus, load);
            }, tasks::add);
            while (isCallTick) Thread.onSpinWait();
            return chunkAccessCompletableFuture.join();
        }
    }
    /*@Nullable
    public ChunkAccess LevelGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {

        long i = ChunkPos.asLong(chunkX, chunkZ);

        //log.info("Missed chunk " + i + " on status "  + requiredStatus.toString());
        //log.info("Thread: {}", Thread.currentThread().getName());


        ChunkAccess c = lookupChunk(i, requiredStatus, false);
        if (c != null) {
            return c;
        }

        boolean isBlacklistThread = threadBlacklist.containsValue(Thread.currentThread());
        if (!Base.isThreadPooled() && !isBlacklistThread){
            return waitGetChunk(chunkX, chunkZ, requiredStatus, load);
        }


        ChunkAccess cl;
        //if (ASMHookTerminator.shouldThreadChunks()) {
        //    // Multithread but still limit to 1 load op per chunk
        //    long[] locks = loadingChunkLock.lock(i, 0);
        //    try {
        //        if ((c = lookupChunk(i, requiredStatus, false)) != null) {
        //            return c;
        //        }
        //        cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
        //    } finally {
        //        loadingChunkLock.unlock(locks);
        //    }
        //} else {
            synchronized (locks.get(requiredStatus.getIndex())) {
                if (chunkCache.containsKey(new ChunkCacheAddress(i, requiredStatus)) && (c = lookupChunk(i, requiredStatus, false)) != null) {
                    return c;
                }
                cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
                cacheChunk(i, cl, requiredStatus);
            }
        //}
        return cl;
    }*/

    @Override
    protected CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFutureMainThread(int p_8457_, int p_8458_, ChunkStatus p_8459_, boolean p_8460_) {
        Thread value = Thread.currentThread();
        synchronized ((!generatorThread1.contains(value) && threadBlacklist.containsValue(value)) ? threadBlacklist : this) {
            return super.getChunkFutureMainThread(p_8457_, p_8458_, p_8459_, p_8460_);
        }
    }

    @Deprecated
    protected synchronized ChunkAccess KMT$baseGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load, boolean isBlacklistThread){
        long i = ChunkPos.asLong(chunkX, chunkZ);
        Thread thisThread = Thread.currentThread();
        ChunkAccess c;
        ChunkAccess cl;

        //synchronized (locks.get(requiredStatus.getIndex())) {
        if (chunkCache.containsKey(new ChunkCacheAddress(i, requiredStatus)) && (c = lookupChunk(i, requiredStatus, false)) != null) {
            return c;
        }
        if (isBlacklistThread){
            generatorThread2 = thisThread;
        }else {
            //generatorThread1 = thisThread;
            generatorThread1.add(thisThread);
        }
        cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
        cacheChunk(i, cl, requiredStatus);
        if (isBlacklistThread){
            generatorThread2 = null;
        }else {
            //generatorThread1 = null;
            generatorThread1.remove(thisThread);
        }
        return cl;
        //}
    }

    @Override
    @Nullable
    public ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {

        long i = ChunkPos.asLong(chunkX, chunkZ);

        //log.info("Missed chunk " + i + " on status "  + requiredStatus.toString());
        //log.info("Thread: {}", Thread.currentThread().getName());


        ChunkAccess c = lookupChunk(i, requiredStatus, false);
        if (c != null) {
            return c;
        }

        Thread thisThread = Thread.currentThread();
        boolean isBlacklistThread = !generatorThread1.contains(thisThread) && threadBlacklist.containsValue(thisThread);
        //log.info("Thread: {}, threadBlacklist : {}", Thread.currentThread().getName(), Arrays.toString(threadBlacklist.values().stream().map(Thread::getId).toArray()));
        if (!Base.isThreadPooled() && !isBlacklistThread){
            return waitGetChunk(chunkX, chunkZ, requiredStatus, load);
        }


        ChunkAccess cl;
        //if (ASMHookTerminator.shouldThreadChunks()) {
        //    // Multithread but still limit to 1 load op per chunk
        //    long[] locks = loadingChunkLock.lock(i, 0);
        //    try {
        //        if ((c = lookupChunk(i, requiredStatus, false)) != null) {
        //            return c;
        //        }
        //        cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
        //    } finally {
        //        loadingChunkLock.unlock(locks);
        //    }
        //} else {
        //if (requiredStatus != ChunkStatus.FULL && !iMainThreadExecutor.isCall() && Thread.currentThread() != iMainThreadExecutor.getCallThread()){
            //synchronized (isBlacklistThread ? threadBlacklist : this) {
            //    //synchronized (locks.get(requiredStatus.getIndex())) {
            //        if (chunkCache.containsKey(new ChunkCacheAddress(i, requiredStatus)) && (c = lookupChunk(i, requiredStatus, false)) != null) {
            //            return c;
            //        }
            //        if (isBlacklistThread){
            //            generatorThread2 = thisThread;
            //        }else {
            //            //generatorThread1 = thisThread;
            //            generatorThread1.add(thisThread);
            //        }
            //        cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
            //        cacheChunk(i, cl, requiredStatus);
            //        if (isBlacklistThread){
            //            generatorThread2 = null;
            //        }else {
            //            //generatorThread1 = null;
            //            generatorThread1.remove(thisThread);
            //        }
            //    //}
            //}
            //测试
            //if (isBlacklistThread){
            //    synchronized(threadBlacklist) {
            //        cl = KMT$baseGetChunk(chunkX, chunkZ, requiredStatus, load, true);
            //    }
            //}else {
                cl = KMT$baseGetChunk(chunkX, chunkZ, requiredStatus, load, isBlacklistThread);
            //}
        //}else {
        //    return waitGetChunk(chunkX, chunkZ, requiredStatus, load);
        //}
        //}
        return cl;
    }

    @Override
    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFuture(int p_8432_, int p_8433_, @NotNull ChunkStatus p_8434_, boolean p_8435_) {
        ChunkCacheLine chunkCacheLine = chunkCache.get(new ChunkCacheAddress(ChunkPos.asLong(p_8432_, p_8433_), p_8434_));
        if (chunkCacheLine != null && chunkCacheLine.getChunk() != null) {
            ChunkAccess chunk = chunkCacheLine.getChunk();
            return CompletableFuture.completedFuture(Either.left(chunk));
        }
        return super.getChunkFuture(p_8432_, p_8433_, p_8434_, p_8435_);
    }

    //@Override
    //public boolean runDistanceManagerUpdates() {
    //    boolean b = super.runDistanceManagerUpdates();
    //    return b;
    //}

    public void testChunkCache(){
        //if (Util.getMillis() + 14246622 % 28 == 0) {
        List<ChunkCacheAddress> remove = new ArrayList<>();
        for (ChunkCacheAddress chunkCacheAddress : chunkCache.keySet()) {
            if (distanceManager.getTickets(chunkCacheAddress.chunk).isEmpty()) {
                remove.add(chunkCacheAddress);
            }
        }
        remove.forEach(chunkCache::remove);
        //}
    }

    @Override
    public void clearCache() {
        super.clearCache();
        if (chunkCache != null) {
            /*chunkCache.clear();/*/testChunkCache();
        }
    }

    @Override
    public void tick(BooleanSupplier p_201913_, boolean p_201914_) {
        isCallTick = true;
        super.tick(p_201913_, p_201914_);
        synchronized (tasksRunLock) {
            tasks.forEach(Runnable::run);
            tasks.clear();
        }

        isCallTick = false;

    }

    @Override
    @Nullable
    public LevelChunk getChunkNow(int chunkX, int chunkZ) {
        long i = ChunkPos.asLong(chunkX, chunkZ);

        ChunkAccess c = lookupChunk(i, ChunkStatus.FULL, false);
        if (c != null) {
            return (LevelChunk) c;
        }

        for(int j = 0; j < 4; ++j) {
            if (i == lastChunkPos[j] && lastChunkStatus[j] == ChunkStatus.FULL) {
                    ChunkAccess chunkaccess = lastChunk[j];
                return chunkaccess instanceof LevelChunk ? (LevelChunk)chunkaccess : null;
            }
        }
        ChunkHolder chunkholder = chunkMap.getVisibleChunkIfPresent(i);
        if (chunkholder == null) {
            return null;
        } else {
            try {
                Object o = currentlyLoading.get(chunkholder);
                if (o != null) return (LevelChunk)o; // Forge: If the requested chunk is loading, bypass the future chain to prevent a deadlock.
            } catch (IllegalAccessException e) {
                return null;
            }
            Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either = chunkholder.getFutureIfPresent(ChunkStatus.FULL).getNow(null);
            if (either == null) {
                return null;
            } else {
                ChunkAccess chunkaccess1 = either.left().orElse(null);
                if (chunkaccess1 != null) {
                    //this.storeInCache(i, chunkaccess1, ChunkStatus.FULL);
                    if (chunkaccess1 instanceof LevelChunk) {
                        return (LevelChunk)chunkaccess1;
                    }
                }

                return null;
            }
        }

        /*//log.debug("Missed chunk " + i + " now");
        //synchronized (this){
            LevelChunk cl = super.getChunkNow(chunkX, chunkZ);
            cacheChunk(i, cl, ChunkStatus.FULL);
            return cl;
        //}*/
    }

    public ChunkAccess lookupChunk(long chunkPos, ChunkStatus status, boolean compute) {
        int oldaccess = access.getAndIncrement();
        if (access.get() < oldaccess) {
            // Long Rollover so super rare
            chunkCache.clear();
            return null;
        }
        ChunkCacheLine ccl;
        ccl = chunkCache.get(new ChunkCacheAddress(chunkPos, status));
        if (ccl != null) {
            ccl.updateLastAccess();
            return ccl.getChunk();
        }
        return null;

    }

    public void cacheChunk(long chunkPos, ChunkAccess chunk, ChunkStatus status) {
        long oldaccess = access.getAndIncrement();
        if (access.get() < oldaccess) {
            // Long Rollover so super rare
            chunkCache.clear();
        }
        ChunkCacheLine ccl;
        if ((ccl = chunkCache.get(new ChunkCacheAddress(chunkPos, status))) != null) {
            ccl.updateLastAccess();
            ccl.updateChunkRef(chunk);
        }
        ccl = new ChunkCacheLine(chunk);
        chunkCache.put(new ChunkCacheAddress(chunkPos, status), ccl);
    }

    @Deprecated
    public void chunkCacheCleanup() {
        while (getLevel() == null || getLevel().getServer() == null) {
            log.debug(chunkCleaner, "ChunkCleaner Waiting for startup");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (getLevel().getServer().isRunning()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock){
                long clearTime1 = System.currentTimeMillis();
                if (clearTime < clearTime1 - 5000){
                    clearTime = clearTime1;
                    chunkCache.clear();
                    //我也没什么好办法。。。他老是会缓存2个不同的区块
                }else {
                    int size = chunkCache.size();
                    if (size < CACHE_SIZE)
                        continue;
                    // System.out.println("CacheFill: " + size);
                    long maxAccess = chunkCache.values().stream().mapToInt(ccl -> ccl.lastAccess).max().orElseGet(() -> access.get());
                    long minAccess = chunkCache.values().stream().mapToInt(ccl -> ccl.lastAccess).min()
                            .orElseGet(() -> Integer.MIN_VALUE);
                    long cutoff = minAccess + (long) ((maxAccess - minAccess) / ((float) size / ((float) CACHE_SIZE)));
                    for (Entry<ChunkCacheAddress, ChunkCacheLine> l : chunkCache.entrySet()) {
                        if (l.getValue().getLastAccess() < cutoff | l.getValue().getChunk() == null) {
                            chunkCache.remove(l.getKey());
                        }
                    }
                }
            }
        }
        log.debug(chunkCleaner, "ChunkCleaner terminating");
    }

    @Override
    public void pushThread(long id) {
        Thread value = Thread.currentThread();
        if (threadBlacklist.containsKey(id)) throw new IllegalStateException("Thread " + id + " is already blacklisted");
        //if (value != generatorThread1) {
        if (!generatorThread1.contains(value)) {
            threadBlacklist.put(id, value);
        }
    }

    @Override
    public long pushThread() {
        Thread thread = Thread.currentThread();
        long l = thread.getId();
        if (generatorThread1.contains(thread)|| threadBlacklist.containsKey(l)) {
            threadBlacklist.put(l, thread);
            return l;
        }
        return -1;
    }

    @Override
    public void pop(long id) {
        threadBlacklist.remove(id);
    }

    @Override
    public void pop() {
        threadBlacklist.remove(Thread.currentThread().getId());
    }

    @Override
    public void execTasks() {
        synchronized (lock2){
            isCallGeneratorTick = true;
            generatorTasks.forEach(Runnable::run);
            generatorTasks.clear();
            isCallGeneratorTick = false;
            synchronized (tasksRunLock) {
                tasks.forEach(Runnable::run);
                tasks.clear();
            }
        }
    }

    @Override
    public void execTask(Runnable task) {
        generatorTasks.add(task);
    }

    @Override
    public void execWaitTask(Runnable task) {
        generatorTasks.add(task);while (isCallGeneratorTick) Thread.onSpinWait();
    }

    protected static class ChunkCacheAddress {

        protected long chunk;
        protected ChunkStatus status;

        public ChunkCacheAddress(long chunk, ChunkStatus status) {
            super();
            this.chunk = chunk;
            this.status = status;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(chunk) ^ status.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ChunkCacheAddress) {
                if ((((ChunkCacheAddress) obj).chunk == chunk) && (((ChunkCacheAddress) obj).status.equals(status))) {
                    return true;
                }
            }
            return false;
        }
    }

    protected class ChunkCacheLine {
        WeakReference<ChunkAccess> chunk;
        int lastAccess;

        public ChunkCacheLine(ChunkAccess chunk) {
            this(chunk, access.get());
        }

        public ChunkCacheLine(ChunkAccess chunk, int lastAccess) {
            this.chunk = new WeakReference<>(chunk);
            this.lastAccess = lastAccess;
        }

        public ChunkAccess getChunk() {
            return chunk.get();
        }

        public int getLastAccess() {
            return lastAccess;
        }

        public void updateLastAccess() {
            lastAccess = access.get();
        }

        public void updateChunkRef(ChunkAccess c) {
            if (chunk.get() == null) {
                chunk = new WeakReference<>(c);
            }
        }
    }
}
