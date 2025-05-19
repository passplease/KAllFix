package n1luik.K_multi_threading.debug.ex;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import n1luik.KAllFix.util.TaskRun;
import n1luik.K_multi_threading.core.util.OB2;
import n1luik.K_multi_threading.core.util.Unsafe;
import n1luik.K_multi_threading.debug.ex.data.LogRoot;
import n1luik.K_multi_threading.debug.ex.data.RelationshipSave;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

public class DebugLog {
    public volatile static boolean debug = false;
    public volatile static long startDebugTime = 0;
    public volatile static long interval = 0;//纳秒
    public static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    public static final ConcurrentLinkedQueue<Relationship> relationships = new ConcurrentLinkedQueue<>();
    public static final DebugLog caller = new DebugLog();

    protected final AtomicInteger runSize = new AtomicInteger(0);
    protected volatile boolean run = false;
    protected volatile int taskSize = 0;
    protected volatile BooleanSupplier[] tasks = new BooleanSupplier[0];
    //protected volatile boolean[] data = new boolean[0];
    protected volatile BooleanSupplier[] taskUp = null;
    protected final Int2ObjectArrayMap<ThreadNode> runThreadNodes = new Int2ObjectArrayMap<>();
    protected final List<ThreadNode> nodes = new ArrayList<>();
    protected final Queue<ThreadNode> stopNode = new ArrayDeque<>();


    public static void start(long interval) {
        startDebugTime = System.currentTimeMillis();
        relationships.clear();
        DebugLog.interval = interval;
        caller.restart();
        debug = true;
    }

    public static LogRoot stop() {
        debug = false;
        caller.stopAll();
        LogRoot ret = save();
        relationships.clear();
        return ret;
    }

    public static LogRoot save() {
        if (debug) throw new RuntimeException("DebugLog is running");
        LogRoot logRoot = new LogRoot();
        ArrayList<RelationshipSave> relationshipSave = new ArrayList<>(relationships.size());
        logRoot.relationshipSave = relationshipSave;
        for (Relationship relationship : relationships) {
            relationshipSave.add(relationship.save());
        }
        logRoot.sumTime = System.currentTimeMillis() - startDebugTime;
        logRoot.startTime = startDebugTime;
        return logRoot;
    }


    public static void add(Relationship relationship) {
        synchronized (caller) {
            relationships.add(relationship);
            caller.addTask(relationship);
        }
    }

    private synchronized void remove(int id) {
        taskSize--;
        tasks[id] = null;
        stopNode.add(runThreadNodes.remove(id));
    }

    protected DebugLog() {
    }
    
    protected synchronized void startTask(int id){
        if (nodes.size() <= runSize.get()){
            ThreadNode e = new ThreadNode();
            nodes.add(e);
            e.restart(id);
            e.start();
        }else {
            stopNode.remove().restart(id);
        }
    } 

    public synchronized void addTask(BooleanSupplier task) {
        synchronized(this) {
            if (run) {
                if (tasks.length == taskSize) {
                    taskUp = Arrays.copyOf(tasks, (int) (tasks.length * 1.06) + 1);
                    tasks = taskUp;
                    //data = Arrays.copyOf(data, (int)(data.length * 1.06) + 1);
                    tasks[taskSize] = task;
                    taskSize++;
                    startTask(taskSize-1);
                }else {
                    for (int i = tasks.length - 1; i >= 0; i--) {
                        if (tasks[i] == null) {
                            tasks[i] = task;
                            startTask(i);
                            taskSize++;
                            return;
                        }
                    }
                }
            }
        }
    }

    private synchronized void restart() {
        stopAll();
        taskSize = 0;
        tasks = new BooleanSupplier[0];
        //data = new boolean[0];
        taskUp = null;
        run = true;
    }
    protected synchronized void stopAll() {
        run = false;
        runThreadNodes.clear();
        while (runSize.get() > 0){
            Thread.onSpinWait();
        }
    }

    public class ThreadNode extends Thread{
        protected final Object upLovk = new Object();
        protected final ReentrantLock stopLock = new ReentrantLock();
        protected volatile boolean up = false;
        public int id = -1;
        public volatile boolean stop = false;

        public void restart(int id) {
            synchronized (upLovk) {
                this.id = id;
                up = true;
            }
            synchronized (DebugLog.this) {
                stopNode.remove(this);
                runThreadNodes.put(id, this);
                runSize.getAndAdd(1);
            }
            if (stopLock.isLocked()){
                Unsafe.unsafe.unpark(this);
            }
        }
        public void stopRun() {
            synchronized (upLovk) {
                this.id = -1;
                up = true;
                stop = true;
            }
        }

        @Override
        public void run() {
            long l = System.nanoTime();
            if (up) {
                synchronized (upLovk) {
                    Thread.onSpinWait();
                }
            }
            int id1 = id;
            if (id1 > -1) {
                if (tasks[id1] != null) {
                    if (tasks[id1].getAsBoolean()) {
                        synchronized (DebugLog.this) {
                            DebugLog.this.remove(id1);
                            stopRun();
                        }
                    }
                }
            }
            if (run && !stop) {
                //暂停指定的时间
                if (interval > 0) {
                    long now = System.nanoTime();
                    long l1 = interval - (now - l);
                    if (l1 > 0)
                        Unsafe.unsafe.park(false, l1);

                }
            }else {
                runSize.getAndAdd(-1);
                if (!run){
                    this.id = -1;
                }
                stop = false;
                up = false;
                stopLock.lock();
                Unsafe.unsafe.park(false, 0);
                stopLock.unlock();
            }
        }
    }

}
