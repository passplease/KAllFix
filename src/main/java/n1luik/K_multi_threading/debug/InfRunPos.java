package n1luik.K_multi_threading.debug;

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
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public static final int SAVE = 1000*60*1;
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
        public long threadId = -1;
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

        public void mergeFrom(BufTree other) {
            buf[0 / MAX_BUF].time[0 % MAX_BUF] += other.buf[0 / MAX_BUF].time[0 % MAX_BUF];
            if (threadId == -1) threadId = other.threadId;
            int[] ostack = new int[512];
            int[] tstack = new int[512];
            ostack[0] = 0;
            tstack[0] = 0;
            int sp = 1;
            while (true) {
                int on = ostack[sp - 1];
                int tn = tstack[sp - 1];
                int oc = other.buf[on / MAX_BUF].callnext[on % MAX_BUF];
                if (oc >= 0) {
                    int tc = findOrCreateChild(tn, other.buf[oc / MAX_BUF].call[oc % MAX_BUF]);
                    buf[tc / MAX_BUF].time[tc % MAX_BUF] += other.buf[oc / MAX_BUF].time[oc % MAX_BUF];
                    ostack[sp] = oc;
                    tstack[sp] = tc;
                    sp++;
                    continue;
                }
                boolean advanced = false;
                while (sp > 0) {
                    int cur = ostack[sp - 1];
                    int osib = other.buf[cur / MAX_BUF].thisnext[cur % MAX_BUF];
                    if (osib >= 0) {
                        int parentThis = sp >= 2 ? tstack[sp - 2] : -1;
                        int ts = findOrCreateChild(parentThis, other.buf[osib / MAX_BUF].call[osib % MAX_BUF]);
                        buf[ts / MAX_BUF].time[ts % MAX_BUF] += other.buf[osib / MAX_BUF].time[osib % MAX_BUF];
                        ostack[sp - 1] = osib;
                        tstack[sp - 1] = ts;
                        advanced = true;
                        break;
                    }
                    sp--;
                }
                if (!advanced && sp == 0) break;
            }
        }

        private int findOrCreateChild(int parentThis, long callf) {
            int child = buf[parentThis / MAX_BUF].callnext[parentThis % MAX_BUF];
            int prev = -1;
            while (child >= 0 && buf[child / MAX_BUF].call[child % MAX_BUF] != callf) {
                prev = child;
                child = buf[child / MAX_BUF].thisnext[child % MAX_BUF];
            }
            if (child >= 0) return child;
            if (empty <= 100) {
                max += MAX_BUF;
                empty += MAX_BUF;
                buf[max / MAX_BUF - 1] = new BufV();
            }
            int add = max - empty;
            empty--;
            if (prev >= 0) {
                buf[prev / MAX_BUF].thisnext[prev % MAX_BUF] = add;
            } else {
                buf[parentThis / MAX_BUF].callnext[parentThis % MAX_BUF] = add;
            }
            buf[add / MAX_BUF].callnext[add % MAX_BUF] = -1;
            buf[add / MAX_BUF].thisnext[add % MAX_BUF] = -1;
            buf[add / MAX_BUF].call[add % MAX_BUF] = callf;
            buf[add / MAX_BUF].time[add % MAX_BUF] = 0;
            return add;
        }
    }

    public static final ConcurrentHashMap<String, Integer> stringId = new ConcurrentHashMap<>();
    public static final AtomicInteger stringIdSize = new AtomicInteger();
    static {
        getStringId(".");
        getStringId("/");
    }

    public static int getStringId(String s){
        Integer v = stringId.get(s);
        if (v != null) return v;
        int id = stringIdSize.getAndIncrement();
        Integer prev = stringId.putIfAbsent(s, id);
        return prev != null ? prev : id;
    }
    public static long className(int cni, int mni){
        return ((long)cni) << 32 | mni;
    }

    public static final ConcurrentHashMap<String, BufTree> globalBufTree = new ConcurrentHashMap<>();
    public static volatile ZipOutputStream globalZipOut;

    public final class Worker implements Runnable {
        public final int index;
        public long[] methods = new long[64];
        public final ConcurrentHashMap<String, BufTree> bufTree = new ConcurrentHashMap<>();

        public final long[] speedTime = new long[SPEED_BUF];
        public final int[] speedCount = new int[SPEED_BUF];
        public int speedIndex = 0;
        public int speedSize = 0;

        public Worker(int index){
            this.index = index;
        }

        public BufTree getBufTree(String threadName, long threadId){
            return bufTree.computeIfAbsent(threadName, k -> {
                BufTree tree = new BufTree();
                tree.threadName = threadName;
                tree.threadId = threadId;
                return tree;
            });
        }

        public synchronized void mergeIntoGlobal(TreeMap<Long, Long> speedAgg){
            for (var entry : bufTree.entrySet()) {
                String name = entry.getKey();
                BufTree localTree = entry.getValue();
                BufTree globalTree = globalBufTree.computeIfAbsent(name, k -> {
                    BufTree t = new BufTree();
                    t.threadName = name;
                    return t;
                });
                globalTree.mergeFrom(localTree);
            }
            bufTree.clear();
            for (int i = 0; i < speedSize; i++) {
                int idx = (speedIndex - speedSize + i + SPEED_BUF) % SPEED_BUF;
                speedAgg.merge(speedTime[idx], (long) speedCount[idx], Long::sum);
            }
        }

        public synchronized void recordSpeed(long timeMs, int count){
            int i = speedIndex;
            speedTime[i] = timeMs;
            speedCount[i] = count;
            speedIndex = (i + 1) % SPEED_BUF;
            if (speedSize < SPEED_BUF) speedSize++;
        }

        public synchronized void run(){
            long[] allThreadIds = threadMXBean.getAllThreadIds();
            int total = allThreadIds.length;
            for (int i = 0; i < total; i++) {
                long allThreadId = allThreadIds[i];
                if (Math.floorMod(allThreadId, WORKERS) != index) continue;
                ThreadInfo threadInfo = threadMXBean.getThreadInfo(allThreadId, 256);
                if (threadInfo == null) continue;
                if (threadInfo.getThreadState() != Thread.State.RUNNABLE) continue;
                String name = threadInfo.getThreadName();
                if (name != null && name.startsWith("InfRunPos")) continue;
                StackTraceElement[] stackTrace = threadInfo.getStackTrace();
                int len = stackTrace.length;
                if (methods.length <= len) {
                    methods = new long[len + 1];
                }
                long[] callpos = methods;
                callpos[0] = 0;
                for (int j = 0; j < len; j++) {
                    StackTraceElement stackTraceElement = stackTrace[len - 1 - j];
                    int cni = getStringId(stackTraceElement.getClassName());
                    int mni = getStringId(stackTraceElement.getMethodName());
                    callpos[j+1] = className(cni, mni);
                }
                getBufTree(name, allThreadId).add(callpos, len + 1, 0, 0);
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

    private static final AtomicBoolean started = new AtomicBoolean();

    @SneakyThrows
    public void start(){
        if (!started.compareAndSet(false, true)) {
            log.warn("InfRunPos already started, skip");
            return;
        }
        log.info("InfRunPos start, workers={}", WORKERS);
        globalZipOut = new ZipOutputStream(new FileOutputStream(String.valueOf(System.currentTimeMillis()) + ".zip"));
        List<Worker> workers = new ArrayList<>();
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
        Thread saveThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(SAVE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                try {
                    saveAll(workers);
                } catch (Exception e) {
                    log.error("InfRunPos save error", e);
                }
            }
        }, "InfRunPos-Save");
        saveThread.setDaemon(true);
        saveThread.start();
    }

    public static void saveAll(List<Worker> workers) throws IOException {
        TreeMap<Long, Long> speedAgg = new TreeMap<>();
        synchronized (globalBufTree) {
            for (Worker worker : workers) {
                worker.mergeIntoGlobal(speedAgg);
            }
            ZipOutputStream zipOutputStream = globalZipOut;
            synchronized (zipOutputStream) {
                zipOutputStream.putNextEntry(new ZipEntry(String.valueOf(System.currentTimeMillis())));
                DataOutputStream d = new DataOutputStream(zipOutputStream);
                Set<Map.Entry<String, BufTree>> entries = globalBufTree.entrySet();
                int size = entries.size();
                d.writeInt(size);
                for (Map.Entry<String, BufTree> bufTreeEntry : entries) {
                    BufTree tree = bufTreeEntry.getValue();
                    d.writeLong(tree.threadId);
                    d.writeInt(tree.max);
                    d.writeInt(tree.empty);
                    String tname = tree.threadName;
                    d.writeBoolean(tname != null);
                    if (tname != null) d.writeUTF(tname);
                    for (int i = 0; i < tree.max; i++) {
                        d.writeLong(tree.buf[i/MAX_BUF].call[i % MAX_BUF]);
                    }
                    for (int i = 0; i < tree.max; i++) {
                        d.writeInt(tree.buf[i/MAX_BUF].time[i % MAX_BUF]);
                    }
                    for (int i = 0; i < tree.max; i++) {
                        d.writeInt(tree.buf[i/MAX_BUF].thisnext[i % MAX_BUF]);
                    }
                    for (int i = 0; i < tree.max; i++) {
                        d.writeInt(tree.buf[i/MAX_BUF].callnext[i % MAX_BUF]);
                    }
                    if ((size-=1) <0)break;
                }
                for (var entry : stringId.entrySet()) {
                    String k = entry.getKey();
                    Integer v = entry.getValue();
                    d.write(1);
                    d.writeUTF(k);
                    d.writeInt(v);
                }
                d.write(0);

                d.writeInt(speedAgg.size());
                for (Map.Entry<Long, Long> e : speedAgg.entrySet()) {
                    d.writeLong(e.getKey());
                    d.writeInt(e.getValue().intValue());
                }

                zipOutputStream.flush();
            }
        }
    }

}
