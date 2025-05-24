package n1luik.K_multi_threading.debug.ex;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import n1luik.K_multi_threading.core.util.Unsafe;
import n1luik.K_multi_threading.debug.ex.data.LogRoot;
import n1luik.K_multi_threading.debug.ex.data.RelationshipSave;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

public class DebugLog {
    public volatile static boolean debug = false;
    public volatile static long startDebugTime = 0;
    public volatile static long interval = 0;//纳秒
    public static final ConcurrentLinkedQueue<Relationship> relationships = new ConcurrentLinkedQueue<>();
    public static final DebugLog caller = new DebugLog();
    //protected static final TaskRun addTash = new TaskRun("DebugLog.addTask");

    protected final AtomicInteger runSize = new AtomicInteger(0);
    protected volatile boolean run = false;
    protected volatile int taskSize = 0;
    protected volatile IntList taskEmpty = new IntArrayList();
    protected volatile BooleanSupplier[] tasks = new BooleanSupplier[0];
    //protected volatile boolean[] data = new boolean[0];
    //protected volatile BooleanSupplier[] taskUp = null;
    protected final Int2ObjectArrayMap<ThreadNode> runThreadNodes = new Int2ObjectArrayMap<>();
    protected final List<ThreadNode> nodes = new ArrayList<>();
    protected final Queue<ThreadNode> stopNode = new ArrayDeque<>();
    protected final ReentrantLock lock = new ReentrantLock();
    protected final Condition condition = lock.newCondition();

    static  {
        //addTash.start();
    }


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
        relationships.forEach(Relationship::stop);
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
        logRoot.runTime = System.currentTimeMillis() - startDebugTime;
        logRoot.startTime = startDebugTime;
        logRoot.interval = interval;
        logRoot.totalTicks = 1;
        return logRoot;
    }


    public static void add(Relationship relationship) {
        //addTash.execute(()-> {
            synchronized (caller) {
                if (debug) {
                    relationships.add(relationship);
                    caller.addTask(relationship);
                }
            }
        //});
    }

    private synchronized void remove(int id, ThreadNode node) {
        taskSize--;
        tasks[id] = null;
        taskEmpty.add(id);
        runThreadNodes.remove(id);
        stopNode.add(node);
    }

    protected DebugLog() {
    }
    
    protected synchronized void startTask(int id){
        if (nodes.size() <= runSize.get()){
            ThreadNode e = new ThreadNode();
            //DebugVoidAsyncWait debugVoidAsyncWait = new DebugVoidAsyncWait(lock, condition, () -> {
            //});
            //e.start = debugVoidAsyncWait;
            nodes.add(e);
            e.restart(id);
            e.start();
            //debugVoidAsyncWait.waitTask();
        }else {
            if (stopNode.isEmpty()){
                throw new RuntimeException("nodes.size():"+nodes.size()+" runSize:"+runSize.get());
            }
            stopNode.remove().restart(id);
        }
    } 

    public synchronized void addTask(BooleanSupplier task) {
        if (run) {
            if (tasks.length == taskSize) {
                BooleanSupplier[] taskUp = Arrays.copyOf(tasks, (int) (tasks.length * 1.06) + 1);
                tasks = taskUp;
                //data = Arrays.copyOf(data, (int)(data.length * 1.06) + 1);
                tasks[taskSize] = task;
                taskSize++;
                startTask(taskSize-1);
            }else {
                if (taskEmpty.isEmpty()) {
                    for (int i = tasks.length - 1; i >= 0; i--) {
                        if (tasks[i] == null) {
                            tasks[i] = task;
                            startTask(i);
                            taskSize++;
                            return;
                        }
                    }
                    throw new RuntimeException("taskSize:"+taskSize+" tasks.length:"+tasks.length);
                }
                taskSize++;
                int i = taskEmpty.removeInt(taskEmpty.size()-1);
                tasks[i] = task;
                startTask(i);
            }
        }
    }

    private synchronized void restart() {
        stopAll();
        taskSize = 0;
        tasks = new BooleanSupplier[0];
        taskEmpty.clear();
        //data = new boolean[0];
        //taskUp = null;
        run = true;
    }
    protected void stopAll() {
        synchronized (this) {
            run = false;
            for (ThreadNode value : runThreadNodes.values()) {
                value.stopRun();
            }
            runThreadNodes.clear();
            stopNode.addAll(nodes);
        }
        while (runSize.get() > 0){
            System.out.println(runSize.get());//Thread.onSpinWait();
        }
        Arrays.fill(tasks, null);
    }

    public class ThreadNode extends Thread{
        protected final Object upLovk = new Object();
        protected final ReentrantLock stopLock = new ReentrantLock();
        protected volatile boolean up = false;
        public volatile int id = -1;
        public volatile boolean stop = false;
        public volatile boolean runThis = false;
        public volatile boolean exit = false;
        //public Runnable start = null;

        public ThreadNode(){
            super("KMT-Debug-Thread");
        }

        public synchronized void restart(int id) {
            synchronized (upLovk) {
                this.id = id;
                up = true;
                synchronized (DebugLog.this) {
                    stopNode.remove(this);
                    runThreadNodes.put(id, this);
                    synchronized (upLovk) {
                        if (!runThis) {
                            runSize.getAndAdd(1);
                            runThis = true;
                        }
                    }
                }
            }
            if (stopLock.isLocked()){
                Unsafe.unsafe.unpark(this);
            }
        }
        public void stopRun() {
            synchronized (upLovk) {
                if (runThis) {
                    runSize.getAndAdd(-1);
                    runThis = false;
                }
                this.id = -1;
                up = true;
                stop = true;
                runThis = false;
            }
        }

        @Override
        public void run() {
            //if (start != null) {
            //    start.run();
            //    start = null;
            //}
            while (!exit){
                long l = System.nanoTime();
                if (up) {
                    synchronized (upLovk) {
                        Thread.onSpinWait();
                        up = false;
                    }
                }
                int id1 = id;
                if (id1 > -1) {
                    if (tasks[id1] != null) {
                        if (tasks[id1].getAsBoolean()) {
                            synchronized (DebugLog.this) {
                                stopRun();
                                DebugLog.this.remove(id1, this);
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
                } else {
                    synchronized (upLovk) {
                        if (runThis) {
                            runSize.getAndAdd(-1);
                            runThis = false;
                        }
                        if (!run) {
                            this.id = -1;
                        }
                        stop = false;
                        up = false;
                        stopLock.lock();
                    }
                    Unsafe.unsafe.park(false, 0);
                    stopLock.unlock();
                }
            }
        }
    }

}
