package n1luik.K_multi_threading.debug;

import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class InfRunPos {
    public static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    public static final Method rootMethod;

    static {
        try {
            rootMethod = InfRunPos.class.getDeclaredMethod("root");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    //占位符函数
    public static final void root(){}
    public static final int MAX_BUF = 1024*64;
    public static final int MAX_SIZE = 65535;
    public static final int WAIT = 1600;
    public static final int SAVE = 1000*60*3;
    public static final int WORKERS = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
    public static final long SPEED_WINDOW = 1000_000_000L;
    public static final int SPEED_BUF = 1024;
    public static final class BufTree{
        public static final class BufV{
            public long[] call = new long[MAX_BUF];
            public int[] time = new int[MAX_BUF];
            public int[] thisnext = new int[MAX_BUF];
            public int[] callnext = new int[MAX_BUF];
        }

        public int max = MAX_BUF;
        public int empty = MAX_BUF-1;
        public String threadName;
        public final BufV[] buf = new BufV[MAX_SIZE];
        {
            buf[0] = new BufV();
            buf[0].thisnext[0] = -1;
            buf[0].callnext[0] = -1;
            buf[0].time[0] = 1;
            buf[0].call[0] = 0;
        }
        //public Method[] call;
        //public int[] time;
        //public int[] thisnext;
        //public int[] callnext;

        public int add(long[] callpos, int len, int start, int startTree) {
            if (empty <= len + 100) {
                max += MAX_BUF;
                empty += MAX_BUF;
                buf[max / MAX_BUF - 1] = new BufV();
            }

            int oldcall = -1;
            for (; start < len; start++) {

                var callf = callpos[start];
                int oldnext = -1;
                while (startTree >= 0 && buf[startTree / MAX_BUF].call[startTree % MAX_BUF] != callf) {
                    oldnext = startTree;
                    startTree = buf[startTree / MAX_BUF].thisnext[startTree % MAX_BUF];
                }
                if (startTree >= 0) {
                    oldcall = startTree;
                    buf[startTree / MAX_BUF].time[startTree % MAX_BUF]++;
                    startTree = buf[startTree / MAX_BUF].callnext[startTree % MAX_BUF];
                    if (startTree >= 0) {
                        continue;
                    }
                    start++;
                    for (; start < len; start++) {
                        int add = max - empty;
                        empty--;
                        buf[oldcall / MAX_BUF].callnext[oldcall % MAX_BUF] = add;
                        oldcall = add;
                        buf[add / MAX_BUF].callnext[add % MAX_BUF] = -1;
                        buf[add / MAX_BUF].thisnext[add % MAX_BUF] = -1;
                        buf[add / MAX_BUF].call[add % MAX_BUF] = callpos[start];
                        buf[add / MAX_BUF].time[add % MAX_BUF] = 1;
                    }
                    break;
                } else {
                    start++;
                    if (start < len) {
                        int add = max - empty;
                        empty--;
                        buf[oldnext / MAX_BUF].thisnext[oldnext % MAX_BUF] = add;
                        oldcall = add;
                        buf[add / MAX_BUF].callnext[add % MAX_BUF] = -1;
                        buf[add / MAX_BUF].thisnext[add % MAX_BUF] = -1;
                        buf[add / MAX_BUF].call[add % MAX_BUF] = callf;
                        buf[add / MAX_BUF].time[add % MAX_BUF] = 1;
                    }
                    start++;
                    for (; start < len; start++) {
                        int add = max - empty;
                        empty--;
                        buf[oldcall / MAX_BUF].callnext[oldcall % MAX_BUF] = add;
                        oldcall = add;
                        buf[add / MAX_BUF].callnext[add % MAX_BUF] = -1;
                        buf[add / MAX_BUF].thisnext[add % MAX_BUF] = -1;
                        buf[add / MAX_BUF].call[add % MAX_BUF] = callpos[start];
                        buf[add / MAX_BUF].time[add % MAX_BUF] = 1;
                    }
                }


            }
            return oldcall;
        }
    }

    public final ConcurrentHashMap<String, Integer> stringId = new ConcurrentHashMap<>();
    public final AtomicInteger stringIdSize = new AtomicInteger();
    public DataOutputStream sharedDataOutputStream;
    public final Object saveLock = new Object();
    public final List<Worker> workers = new ArrayList<>();
    public volatile long nextSaveTime;
    {
        getStringId(".");
        getStringId("/");
    }

    public int getStringId(String s){
        Integer v = stringId.get(s);
        if (v != null) return v;
        int id = stringIdSize.getAndIncrement();
        Integer prev = stringId.putIfAbsent(s, id);
        return prev != null ? prev : id;
    }
    public long className(int cni, int mni){
        return ((long)cni) << 32 | mni;
    }

    public void saveAll(long time2) throws IOException {
        synchronized (saveLock) {
            if (time2 < nextSaveTime) return;
            nextSaveTime = time2 + SAVE;

            DataOutputStream d = sharedDataOutputStream;
            d.writeLong(System.currentTimeMillis());

            // 显存合并：汇总所有 worker 的线程数
            int totalSize = 0;
            for (Worker w : workers) {
                totalSize += w.bufTree.size();
            }
            d.writeInt(totalSize);

            // 写入所有 worker 的 bufTree 数据
            for (Worker w : workers) {
                ObjectSortedSet<Long2ObjectMap.Entry<BufTree>> entries = w.bufTree.long2ObjectEntrySet();
                for (Long2ObjectMap.Entry<BufTree> bufTreeEntry : entries) {
                    d.writeLong(bufTreeEntry.getLongKey());
                    d.writeInt(bufTreeEntry.getValue().max);
                    d.writeInt(bufTreeEntry.getValue().empty);
                    String name = bufTreeEntry.getValue().threadName;
                    d.writeBoolean(name != null);
                    if (name != null) d.writeUTF(name);
                    for (int i = 0; i < bufTreeEntry.getValue().max; i++) {
                        d.writeLong(bufTreeEntry.getValue().buf[i / MAX_BUF].call[i % MAX_BUF]);
                    }
                    for (int i = 0; i < bufTreeEntry.getValue().max; i++) {
                        d.writeInt(bufTreeEntry.getValue().buf[i / MAX_BUF].time[i % MAX_BUF]);
                    }
                    for (int i = 0; i < bufTreeEntry.getValue().max; i++) {
                        d.writeInt(bufTreeEntry.getValue().buf[i / MAX_BUF].thisnext[i % MAX_BUF]);
                    }
                    for (int i = 0; i < bufTreeEntry.getValue().max; i++) {
                        d.writeInt(bufTreeEntry.getValue().buf[i / MAX_BUF].callnext[i % MAX_BUF]);
                    }
                }
            }

            // 文本合并：stringId 只写一次
            for (Map.Entry<String, Integer> entry : stringId.entrySet()) {
                String k = entry.getKey();
                Integer v = entry.getValue();
                d.write(1);
                d.writeUTF(k);
                d.writeInt(v);
            }
            d.write(0);

            // speed 数据合并（老格式：int + 数组，所有 worker 拼接）
            int totalSpeedSize = 0;
            for (Worker w : workers) {
                totalSpeedSize += w.speedSize;
            }
            d.writeInt(totalSpeedSize);
            for (Worker w : workers) {
                for (int i = 0; i < w.speedSize; i++) {
                    int idx = (w.speedIndex - w.speedSize + i + SPEED_BUF) % SPEED_BUF;
                    d.writeLong(w.speedTime[idx]);
                    d.writeInt(w.speedCount[idx]);
                }
            }

            sharedDataOutputStream.flush();
        }
    }

    public final class Worker implements Runnable {
        public final int index;
        public long[] methods = new long[64];
        public final Long2ObjectAVLTreeMap<BufTree> bufTree = new Long2ObjectAVLTreeMap<>();

        public final long[] speedTime = new long[SPEED_BUF];
        public final int[] speedCount = new int[SPEED_BUF];
        public int speedIndex = 0;
        public int speedSize = 0;

        public Worker(int index){
            this.index = index;
        }

        public BufTree getBufTree(long threadId, String threadName){
            return bufTree.computeIfAbsent(threadId, k -> {
                BufTree tree = new BufTree();
                tree.threadName = threadName;
                return tree;
            });
        }

        public void recordSpeed(long timeMs, int count){
            int i = speedIndex;
            speedTime[i] = timeMs;
            speedCount[i] = count;
            speedIndex = (i + 1) % SPEED_BUF;
            if (speedSize < SPEED_BUF) speedSize++;
        }

        public void run(){
            long[] allThreadIds = threadMXBean.getAllThreadIds();
            for (long allThreadId : allThreadIds) {
                if (Math.floorMod(allThreadId, WORKERS) != index) continue;
                ThreadInfo threadInfo = threadMXBean.getThreadInfo(allThreadId, 256);
                if (threadInfo == null) continue;
                if (threadInfo.getThreadState() != Thread.State.RUNNABLE) continue;
                StackTraceElement[] stackTrace = threadInfo.getStackTrace();
                int len = stackTrace.length;
                if (methods.length <= len) {
                    methods = new long[len + 1];
                }
                long[] callpos = methods;
                callpos[0] = 0;
                for (int i = 0; i < len; i++) {
                    StackTraceElement stackTraceElement = stackTrace[len - 1 - i];
                    int cni = getStringId(stackTraceElement.getClassName());
                    int mni = getStringId(stackTraceElement.getMethodName());
                    callpos[i+1] = className(cni, mni);
                }
                getBufTree(allThreadId, threadInfo.getThreadName()).add(callpos, len + 1, 0, 0);
            }
        }

        @SneakyThrows
        public void task(){
            long time = System.nanoTime();
            long time2 = time / 1000000;

            long windowStart = time;
            long count = 0;

            while (true){
                run();
                if (time2 > nextSaveTime) {
                    saveAll(time2);
                }
                long time3 = System.nanoTime();
                time2 = time3 / 1000000;
                count++;
                if (time3 - windowStart >= SPEED_WINDOW) {
                    long elapsed = time3 - windowStart;
                    int rate = elapsed > 0 ? (int)(count * 1000000000L / elapsed) : 0;
                    recordSpeed(time2, rate);
                    count = 0;
                    windowStart = time3;
                }
                var t4 = WAIT - (time3 - time);
                if(t4 >= 0) {
                    LockSupport.parkNanos(t4);
                    time = time3 +t4;
                }else {
                    time = time3;
                }
            }
        }
    }

    @SneakyThrows
    public void start(){
        log.info("InfRunPos start, workers={}", WORKERS);
        sharedDataOutputStream = new DataOutputStream(new FileOutputStream(String.valueOf(System.currentTimeMillis()) + "_infrunpos"));
        nextSaveTime = System.currentTimeMillis() + SAVE;
        for (int i = 0; i < WORKERS; i++) {
            final Worker worker = new Worker(i);
            workers.add(worker);
            var i2 = i;
            Thread thread = new Thread(() -> {
                try{
                    log.info("InfRunPos worker {} start", i2);
                    worker.task();
                }catch (Exception e){
                    log.error("InfRunPos worker error", e);
                }
            }, "InfRunPos-w" + i);
            thread.setDaemon(true);
            thread.start();
        }
    }

}
